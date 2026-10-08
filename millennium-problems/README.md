# Millennium Prize problems and openai/math

This folder has nothing to do with the customer-support application in the rest of this
repository. It is a standalone research note.

**What was asked:**
1. Find which results in [openai/math](https://github.com/openai/math) are close to the unsolved Millennium Prize problems.
2. Use them as stepping stones toward a proof of one that the collection doesn't already address. Navier–Stokes was the one named.
3. Then push systematically on the open step: a singularity of the **unforced** fluid.

**Result: no Millennium problem is solved here.** The folder contains:
- a map of the relevant results;
- an honest first attempt, with corrections;
- an up-to-date status of Navier–Stokes, including OpenAI's separate September 2026 claim of the **forced** case;
- one new, hand-checked regularity theorem. It extends Constantin–Ignatova–Vicol and narrows where an **unforced** singularity could hide.

| File | Contents |
|---|---|
| [`millennium-map.md`](millennium-map.md) | Every openai/math result relevant to each Millennium problem, its Lean status, and its distance from the actual problem |
| [`navier-stokes-status-2026.md`](navier-stokes-status-2026.md) | What is claimed (forced (C)/(D) by OpenAI, Lean-formalized, under Clay review), what is open (the unforced problem), every known constraint on an unforced singularity, and the regions still open |
| [`anisotropic-zoom-regularity.md`](anisotropic-zoom-regularity.md) | **New result (hand-checked, not peer reviewed):** axisymmetric solutions obeying anisotropic power-law bounds at *any* rates $(\alpha,\beta)$ with $\min(\alpha,\beta)<\tfrac12$ are regular. This extends CIV (2026) beyond their single rate family. |
| [`navier-stokes-attempt.md`](navier-stokes-attempt.md) | The first attempt, with in-place corrections after OpenAI's forced result came to light |
| [`verify_scaling.py`](verify_scaling.py) | Symbolic checks of the scaling identities in the first attempt |
| [`verify_anisotropic_exponents.py`](verify_anisotropic_exponents.py) | Symbolic checks of every exponent in the anisotropic regularity proof |

## Short version

- **Closest progress in openai/math** (if correct): a zero-free half-plane $\operatorname{Re}s>7/8$ for $\zeta$, with a Lean-formalized statement; full BSD for ranks 0 and 1; Hodge for CM abelian varieties.
- **Navier–Stokes, forced (Clay C/D):** claimed by OpenAI in September 2026 (separate from openai/math). The force is the residual of an approximate solution that vanishes to infinite order at the singularity. Lean-formalized; Clay's review is ongoing.
- **Navier–Stokes, unforced:** open. Constantin–Ignatova–Vicol show that OpenAI's construction cannot work with zero (or analytic) force. [`anisotropic-zoom-regularity.md`](anisotropic-zoom-regularity.md) extends their argument to every anisotropic power-law zoom rate. So an unforced singularity with an axisymmetric core cannot follow any uniform power-law scaling. What remains: genuinely 3D cores, multi-scale or non-power-law rates, or non-kinematic scalings.

## Reproduce the checks

```bash
pip install sympy
python3 millennium-problems/verify_scaling.py
python3 millennium-problems/verify_anisotropic_exponents.py
```

Each script ends with `All checks passed.`
