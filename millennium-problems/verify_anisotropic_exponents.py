"""Exponent bookkeeping for anisotropic-zoom-regularity.md.

Every coefficient in the rescaled vorticity equations is a power T**e of the
selected time-to-blowup T = -t_n -> 0. A coefficient vanishes in the limit iff
e > 0 and survives with size one iff e == 0. This script derives each exponent
from the definitions (lengths, field normalizations, time unit) and checks the
sign that the proof needs in every regime, symbolically and on a sample grid.

It checks bookkeeping only; the analysis is in the Markdown note.
Run with:  python3 verify_anisotropic_exponents.py   (needs sympy)
"""
from fractions import Fraction

import sympy as sp

a, b = sp.symbols("alpha beta", positive=True)
T = sp.Symbol("T", positive=True)
lam, mu = T**a, T**b  # radial and axial lengths


def exponent(expr):
    """The exponent e with expr == T**e (expr is a monomial in T)."""
    e = sp.simplify(sp.log(sp.powsimp(expr, force=True), T).expand(force=True))
    return sp.simplify(sp.expand_log(e, force=True))


def regime_data(time_unit, radial_dominant):
    """Coefficients of the rescaled potential-vorticity (finite axis) and
    azimuthal-vorticity (receding axis) equations after multiplying by the
    normalization and the time unit.

    radial_dominant: True when alpha >= beta, i.e. the term d_R W dominates the
    rescaled azimuthal vorticity; False when alpha < beta (d_Z V dominates).
    """
    # Normalizations making the dominant part of Omega = omega_theta / r, and of
    # omega_theta itself, of size one (derived in the note, Section 3).
    c_pv = T * lam**2 / mu if radial_dominant else T * mu
    c_az = T * lam / mu if radial_dominant else T * mu / lam
    d = {
        "transport": time_unit / T,
        "radial diffusion": time_unit / lam**2,
        "axial diffusion": time_unit / mu**2,
        # swirl source d_z(Gamma^2 / r^4) with bounded circulation Gamma
        "swirl source (finite axis)": c_pv * time_unit / (mu * lam**4),
        # swirl source d_z(Gamma^2) / r^3 in the azimuthal vorticity equation
        "swirl source (receding axis)": c_az * time_unit / (mu * lam**3),
        # force terms g = (curl f)_theta / r and (curl f)_theta are bounded
        "force (finite axis)": c_pv * time_unit,
        "force (receding axis)": c_az * time_unit * lam / lam,
        # the subdominant part of the rescaled vorticity (delta^2 or delta^-2)
        "subdominant vorticity part": (lam / mu) ** 2 if radial_dominant else (mu / lam) ** 2,
        # the off-diagonal gradient term that is small by hypothesis:
        # T * |d_z u_r| <= C T^(alpha-beta) or T * |d_r u_z| <= C T^(beta-alpha)
        "small off-diagonal term": T * (lam / T) / mu if radial_dominant else T * (mu / T) / lam,
    }
    return {k: exponent(v) for k, v in d.items()}


# Each regime: (name, condition on (alpha, beta), time unit, radial_dominant,
# which coefficients must survive with size one (exponent 0); all others must vanish).
REGIMES = [
    # On the diagonal the two parts of the vorticity are comparable and both off-diagonal
    # gradients are kept in G, so those exponents are 0 rather than positive.
    ("A0: alpha = beta < 1/2 (diagonal)", lambda x, y: x == y and x < Fraction(1, 2),
     T, True, {"transport", "subdominant vorticity part", "small off-diagonal term"}),
    ("A: alpha, beta < 1/2, alpha > beta", lambda x, y: x < Fraction(1, 2) and y < Fraction(1, 2) and x > y,
     T, True, {"transport"}),
    ("A': alpha, beta < 1/2, alpha < beta", lambda x, y: x < Fraction(1, 2) and y < Fraction(1, 2) and x < y,
     T, False, {"transport"}),
    ("B: alpha = 1/2 > beta (CIV)", lambda x, y: x == Fraction(1, 2) and y < Fraction(1, 2),
     T, True, {"transport", "radial diffusion"}),
    ("C: alpha > 1/2 > beta", lambda x, y: x > Fraction(1, 2) and y < Fraction(1, 2),
     lam**2, True, {"radial diffusion"}),
    ("D: beta = 1/2 > alpha", lambda x, y: y == Fraction(1, 2) and x < Fraction(1, 2),
     T, False, {"transport", "axial diffusion"}),
    ("E: beta > 1/2 > alpha", lambda x, y: y > Fraction(1, 2) and x < Fraction(1, 2),
     mu**2, False, {"axial diffusion"}),
]

grid = [Fraction(k, 40) for k in range(1, 61)]  # alpha, beta in (0, 1.5]
failures = 0
for name, cond, unit, radial_dominant, survivors in REGIMES:
    exps = regime_data(unit, radial_dominant)
    samples = [(x, y) for x in grid for y in grid if cond(x, y)]
    ok = True
    for key, e in exps.items():
        for x, y in samples:
            val = e.subs({a: sp.Rational(x.numerator, x.denominator), b: sp.Rational(y.numerator, y.denominator)})
            need_zero = key in survivors
            if (need_zero and val != 0) or (not need_zero and not val > 0):
                ok = False
                failures += 1
                print(f"  FAIL {key} at alpha={x}, beta={y}: exponent {val}")
                break
    print(f"[{'PASS' if ok else 'FAIL'}] regime {name}: {len(samples)} sample points")
    for key, e in exps.items():
        print(f"         {key:32s} T^({e})" + ("   survives" if key in survivors else "   -> 0"))

# Decay exponent kappa > 0 needed by the comparison principle in regimes A, A', B, D
# (time unit -t, rescaled time tau <= -1). The dominant rescaled vorticity is bounded by
# |tau|^(-kappa); derived from the hypothesis bounds in the note, Section 3.
kappas = {
    "finite axis, alpha >= beta": 1 + 2 * a - b,   # sup |d_RR W|
    "finite axis, alpha < beta": 1 + b,           # sup |d_R d_Z V|
    "receding axis, alpha >= beta": 1 + a - b,    # sup |d_R W|
    "receding axis, alpha < beta": 1 + b - a,     # sup |d_Z V|
}
for key, k in kappas.items():
    # sample the closed half-quadrant, so the diagonal alpha = beta is included in ">="
    samples = [(x, y) for x in grid for y in grid if (x >= y) == ("alpha >= beta" in key) and min(x, y) < Fraction(1, 2)]
    bad = [(x, y) for x, y in samples if not k.subs({a: sp.Rational(str(x)), b: sp.Rational(str(y))}) > 0]
    print(f"[{'PASS' if not bad else 'FAIL'}] decay exponent kappa = {k} > 0 ({key})")
    failures += len(bad)

# Rescaled hypothesis bounds are scale invariant: T * lam^(i-1) * mu^j * (T|tau|)^(alpha-1-i*alpha-j*beta)
# has no leftover power of T, for every derivative order (i, j).
i, j, tau = sp.symbols("i j tau", positive=True)
leftover_V = exponent(T * lam ** (i - 1) * mu**j * T ** (a - 1 - i * a - j * b))
leftover_W = exponent(T * lam**i * mu ** (j - 1) * T ** (b - 1 - i * a - j * b))
scale_ok = sp.simplify(leftover_V) == 0 and sp.simplify(leftover_W) == 0
print(f"[{'PASS' if scale_ok else 'FAIL'}] rescaled bounds on V and W carry no power of T (scale invariance)")
failures += 0 if scale_ok else 1

print("All checks passed." if failures == 0 else f"{failures} check(s) failed.")
raise SystemExit(1 if failures else 0)
