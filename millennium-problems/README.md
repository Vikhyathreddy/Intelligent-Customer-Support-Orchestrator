# Millennium Prize problems and openai/math

This folder has nothing to do with the customer-support application in the rest of this
repository. It is a standalone research note.

**What was asked:** find which results in [openai/math](https://github.com/openai/math) are close
to the unsolved Millennium Prize problems, then use them as stepping stones toward a proof of one
that the collection doesn't already address. Navier–Stokes was the one named.

**Result: there is no proof here.** No Millennium problem is solved in this folder. The attempt
on Navier–Stokes is written out honestly. It records what is rigorous, what comes from the
literature, and the exact point where it stops. That point is the open problem itself.

| File | Contents |
|---|---|
| [`millennium-map.md`](millennium-map.md) | Every openai/math result relevant to each of the six open Millennium problems, its Lean status, and how far it is from the actual problem |
| [`navier-stokes-attempt.md`](navier-stokes-attempt.md) | The attempt on Navier–Stokes, with each step labeled proved here / known / open |
| [`verify_scaling.py`](verify_scaling.py) | Symbolic (sympy) checks of every identity the attempt proves |

## Short version

- **Closest progress in openai/math** (if correct): a zero-free half-plane $\operatorname{Re}s>7/8$ for $\zeta$, with a Lean-formalized statement; full BSD for ranks 0 and 1; Hodge for CM abelian varieties.
- **Navier–Stokes:** the only 3D Navier–Stokes family (376) builds flows that compute under an external force. Its force is defined from the flow, so it does all the work. Proposition 1 shows that smooth forcing shrinks like $\lambda^{3+k}$ at a would-be singularity, so those constructions cannot be the stages of a blowup cascade.
- **Known theorems** (Escauriaza–Seregin–Šverák, Seregin, Nečas–Růžička–Šverák, Tsai, Koch–Nadirashvili–Seregin–Šverák) rule out the simplest replicating cascades. What remains (§4, Step 2d) is unsolved.

## Reproduce the checks

```bash
pip install sympy
python3 millennium-problems/verify_scaling.py
```

Expected output: 12 `[PASS]` lines, then `All checks passed.`
