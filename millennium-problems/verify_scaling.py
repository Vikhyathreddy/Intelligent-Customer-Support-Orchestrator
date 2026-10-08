"""Symbolic checks for the identities used in navier-stokes-attempt.md.

Each check is an exact symbolic identity for arbitrary smooth fields, not a
numerical experiment. Run with:  python3 verify_scaling.py   (needs sympy)
"""
import sympy as sp

lam, nu, T = sp.symbols("lambda nu T", positive=True)
x0 = sp.symbols("x0:3", real=True)
y = sp.symbols("y0:3", real=True)
X = sp.symbols("X0:3", real=True)
s, t = sp.symbols("s t", real=True)


def ns_residual(u, p, xs, tt):
    """du/dt + (u . grad)u - nu * Lap u + grad p, component by component."""
    return [
        sp.diff(u[i], tt)
        + sum(u[j] * sp.diff(u[i], xs[j]) for j in range(3))
        - nu * sum(sp.diff(u[i], xs[j], 2) for j in range(3))
        + sp.diff(p, xs[i])
        for i in range(3)
    ]


def divergence(u, xs):
    return sum(sp.diff(u[i], xs[i]) for i in range(3))


def check(name, expr):
    ok = sp.simplify(expr) == 0
    print(f"[{'PASS' if ok else 'FAIL'}] {name}")
    if not ok:
        raise SystemExit(1)


# Arbitrary smooth velocity, pressure and force, written as functions of the
# rescaled point so that sympy applies the chain rule exactly.
point = [x0[i] + lam * y[i] for i in range(3)] + [T + lam**2 * s]
U = [sp.Function(f"u{i}") for i in range(3)]
P = sp.Function("p")


def at_point(g):
    return g(*point)


def derivative_at_point(g, orders):
    """(d^orders g)(point), built without Subs objects."""
    args = sp.symbols("a0:4", real=True)
    d = g(*args)
    for var_index, k in orders:
        d = sp.diff(d, args[var_index], k)
    return d.subs(dict(zip(args, point)), simultaneous=True)


# 1. Navier-Stokes scaling: u^lam(y,s) = lam u(x0 + lam y, T + lam^2 s),
#    p^lam = lam^2 p(...)  =>  residual(u^lam, p^lam) = lam^3 residual(u, p)(...).
u_lam = [lam * at_point(Ui) for Ui in U]
p_lam = lam**2 * at_point(P)
lhs = ns_residual(u_lam, p_lam, y, s)
for i in range(3):
    rhs = lam**3 * (
        derivative_at_point(U[i], [(3, 1)])
        + sum(at_point(U[j]) * derivative_at_point(U[i], [(j, 1)]) for j in range(3))
        - nu * sum(derivative_at_point(U[i], [(j, 2)]) for j in range(3))
        + derivative_at_point(P, [(i, 1)])
    )
    check(f"NS residual scales by lambda^3 (component {i})", lhs[i] - rhs)

# 2. Incompressibility is preserved: div_y u^lam = lam^2 (div u)(...).
check(
    "divergence scales by lambda^2",
    divergence(u_lam, y) - lam**2 * sum(derivative_at_point(U[i], [(i, 1)]) for i in range(3)),
)

# 3. Force derivatives: f^lam = lam^3 f(...) has
#    d_y^alpha d_s^m f^lam = lam^(3 + |alpha| + 2m) (d^alpha d^m f)(...).
#    Sympy cannot always match mixed derivatives of an abstract function after
#    substitution, so f is an explicit nonlinear field with symbolic coefficients.
c = sp.symbols("c0:4", real=True)
d = sp.symbols("d0:4", real=True)
A = sp.symbols("A0:4", real=True)
f_test = sp.exp(sum(ci * ai for ci, ai in zip(c, A))) * (1 + sum(di * ai for di, ai in zip(d, A)) ** 3)
to_point = dict(zip(A, point))
f_lam = lam**3 * f_test.subs(to_point, simultaneous=True)
for alpha, m in [((1, 0, 0), 0), ((0, 2, 0), 0), ((1, 0, 1), 1), ((0, 0, 0), 2), ((2, 1, 0), 1)]:
    lhs, rhs = f_lam, f_test
    for j, k in enumerate(alpha):
        if k:
            lhs, rhs = sp.diff(lhs, y[j], k), sp.diff(rhs, A[j], k)
    if m:
        lhs, rhs = sp.diff(lhs, s, m), sp.diff(rhs, A[3], m)
    power = 3 + sum(alpha) + 2 * m
    check(
        f"force derivative alpha={alpha}, m={m} scales by lambda^{power}",
        lhs - lam**power * rhs.subs(to_point, simultaneous=True),
    )

# 4. L^3 is scale invariant: ||mu^-1 V(./mu)||_{L^3(R^3)} = ||V||_{L^3} for a
#    radial bump V(r) = exp(-r^2). (The same change of variables works for any V.)
r, mu = sp.symbols("r mu", positive=True)
V = sp.exp(-(r**2))
norm_cubed = sp.integrate(4 * sp.pi * r**2 * (V.subs(r, r / mu) / mu) ** 3, (r, 0, sp.oo))
check("L^3 norm of a rescaled block does not depend on the scale", sp.diff(norm_cubed, mu))

# 5. Energy is supercritical: zooming in by u^lam(y) = lam u(lam y) multiplies
#    the energy ||u||_{L^2}^2 by 1/lam, so the energy bound weakens at small scales.
energy = lambda g: sp.integrate(4 * sp.pi * r**2 * g**2, (r, 0, sp.oo))
check(
    "energy of the zoomed-in field is (1/lambda) times the original energy",
    sp.simplify(energy(mu * V.subs(r, mu * r)) - energy(V) / mu),
)

# 6. A parabolic cascade with time steps lam^(2n) reaches a finite time.
n = sp.Symbol("n", integer=True, nonnegative=True)
total_time = sp.summation(lam ** (2 * n), (n, 0, sp.oo)).subs(lam, sp.Rational(1, 2))
check("time steps lambda^(2n) sum to a finite blowup time (lambda = 1/2 gives 4/3)", total_time - sp.Rational(4, 3))

print("All checks passed.")
