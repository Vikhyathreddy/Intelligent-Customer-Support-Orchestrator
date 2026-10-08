# Which openai/math results are near a Millennium Prize problem?

Source: [openai/math](https://github.com/openai/math) at commit `fd4aeeb` (update of Oct 7, 2026):
719 manuscripts in 372 families, all produced by an unreleased OpenAI model.

**How I read it.** I classified all 372 families by title. I read the catalogue entries for the
roughly 20 families that relate to a Millennium problem, and the analytic set-up of the
Navier–Stokes papers. For the most important results I read the Lean statements that the Lean
catalogue lists (the `lean/ComparatorChallenges/*.lean` files) and checked that they say what is
claimed. I did **not** re-run the Lean proofs or check any paper line by line.

**"Lean ✓"** means openai/math lists a machine-checked formalization of that statement. Without
it, the result is the model's claim. The repo's own README warns that unformalized results
"could have issues". On Oct 7, 2026, three Hodge-related papers were withdrawn over a sign error.

Of the seven Millennium problems, Poincaré is solved (Perelman, 2003). The other six are open.

---

## Riemann Hypothesis: closest dramatic progress (if correct)

| Family | Claim | Lean |
|---|---|---|
| 003 | **Quasi-Riemann hypothesis:** $\zeta(s)$ and every Dirichlet $L$-function have no zeros in $\operatorname{Re}s>7/8$. An alternate proof gives $11/12$. | ✓ (statement read: `riemannZeta s ≠ 0` whenever `7/8 < s.re`) |
| 003 | Uniform exclusion of Landau–Siegel zeros: $(1-\beta)\log q\ge c$ | ✓ |

**Distance:** RH needs every nontrivial zero on $\operatorname{Re}s=1/2$, i.e. a zero-free region
$\operatorname{Re}s>1/2$. Before this, no zero-free half-plane $\operatorname{Re}s>\theta$ with
$\theta<1$ was known. The README says this result was **not** made with the standard procedure,
and that the 11/12 writeup was edited by humans.

## Birch and Swinnerton-Dyer: ranks 0 and 1 claimed in full

| Family | Claim | Lean |
|---|---|---|
| 002 | Full BSD leading-term formula for every elliptic curve over ℚ whose $q^\infty$-Selmer corank is 0 or 1 for some prime $q$ | – |
| 006 | Goldfeld's conjecture: ranks 0 and 1 each occur in density ½ of quadratic twists; average rank → ½ | – |
| 030 | Modularity of elliptic curves over imaginary quadratic fields | – |

**Distance:** BSD covers every rank. Nothing here addresses curves of rank ≥ 2, where even the
rank equality is wide open.

## Hodge conjecture: special varieties only, with recent withdrawals

| Family | Claim | Lean |
|---|---|---|
| 032 | Rational Hodge conjecture for every complex CM abelian variety (README: not from the standard procedure) | – |
| 001 | Milne's rationality conjecture for abelian varieties | – |
| 040 | Bloch's conjecture for complex surfaces | – |
| *withdrawn* | Weil classes on split abelian eightfolds; Kuga–Satake for K3 surfaces; Hodge for products of K3 surfaces (sign error, Oct 7, 2026) | – |

**Distance:** the conjecture is for all smooth projective varieties. These are special families.

## P vs NP: strong results in restricted models only

| Family | Claim | Lean |
|---|---|---|
| 102 | Unique Games Conjecture (as a polynomial-time reduction from 3SAT); tight Max-Cut and Vertex Cover hardness | ✓ |
| 103 | $\mathsf L=\mathsf{RL}=\mathsf{BPL}$ | ✓ |
| 108 | Cubic lower bound for the determinantal complexity of the permanent | ✓ |
| 112 | Explicit depth-3 circuit lower bound beyond $2^{O(\sqrt n)}$ | ✓ |
| 135 | $n^{\Theta(\sqrt n)}$ for homogeneous depth-5 circuits computing iterated matrix multiplication | ✓ |
| 274 | Parity is not in QAC⁰ | ✓ |

**Distance:** none of these separates P from NP.

- Hardness results such as the UGC are reductions. They say "if this is easy, then 3SAT is easy". They do not decide whether 3SAT is easy.
- The lower bounds are for restricted circuit models, or they are polynomial (cubic), where a superpolynomial bound would be needed even for the algebraic analogue VP ≠ VNP.
- Known barriers (relativization, natural proofs, algebrization) still apply.

## Yang–Mills existence and mass gap: 2D toy models only

| Family | Claim | Lean |
|---|---|---|
| 215 | Exponential correlation decay in the 2D lattice $O(n)$ model, $n\ge3$, at every temperature (a lattice form of the Polyakov mass-gap conjecture) | ✓ (finite-volume statement read) |
| 215 | Interacting continuum limit of the 2D $O(3)$ model with a positive mass gap; exact $O(4)$ mass asymptotics | – |
| 282 | Scale symmetry implies local conformal symmetry in 4D QFT (under stated hypotheses) | – |
| 268 | Spin-1 Haldane gap | – |

**Distance:** 2D sigma models are the standard toy model for Yang–Mills, since both are
asymptotically free and both have a mass gap. But the prize asks for a **4D non-abelian gauge
theory** satisfying the Wightman/Osterwalder–Schrader axioms, with a mass gap. No interacting
4D quantum field theory has ever been constructed rigorously.

## Navier–Stokes: nothing close

| Family | Claim | Lean |
|---|---|---|
| 376 | Universal computation in **forced** 3D Navier–Stokes flows on $\mathbb T^3$ (10 papers) | ✓ |
| 362 | Global smoothness for 3D relativistic Vlasov–Maxwell (a different PDE) | ✓ |
| 371 | Stable blowup for a defocusing NLS on $\mathbb T^{12}$ with a large power | ✓ |
| 363–364 | Boltzmann equation: non-uniqueness; derivation from particle systems | 363 ✓ |

**Distance:** far.

- Family 376 never touches blowup or regularity. It chooses a smooth flow and defines the force to fit it.
- 362 and 371 are about other equations, and their methods depend on structure that Navier–Stokes lacks.

See [`navier-stokes-attempt.md`](navier-stokes-attempt.md) for the full analysis and the attempted proof.

---

## Ranking: how close does openai/math get?

1. **Riemann Hypothesis:** a zero-free half-plane would be historic, but $7/8\to1/2$ is the whole problem.
2. **BSD:** complete for ranks 0 and 1 if correct; rank ≥ 2 untouched.
3. **Hodge:** special varieties; the withdrawals show this area needs care.
4. **Yang–Mills:** the right 2D toy models, but no 4D gauge theory.
5. **P vs NP:** restricted lower bounds only.
6. **Navier–Stokes:** nothing that addresses blowup or regularity of the actual equation.
