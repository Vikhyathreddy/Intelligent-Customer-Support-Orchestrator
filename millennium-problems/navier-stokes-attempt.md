# An attempt at the Navier–Stokes Millennium problem, using openai/math as stepping stones

> **Status: this is not a proof.** The attempt does not solve the Navier–Stokes problem.
> It proves two small facts, uses published theorems to rule out the most direct version of
> the strategy, and stops at a gap that is the open problem itself. Treat any document,
> including this one, that claims to close that gap as unverified until experts have checked it.

> **Correction (October 8, 2026).** When I wrote this note I had only searched the
> openai/math repository and missed a separate OpenAI release. In September 2026 OpenAI
> published *Finite time blowup for Navier–Stokes*, claiming Clay alternatives **(C)** and
> **(D)** (smooth forcing). It comes with a Lean 4 formalization,
> [openai/NavierStokesAndEuler](https://github.com/openai/NavierStokesAndEuler). Clay has called
> the problem "apparently settled" and its review is ongoing. Its construction is an approximate
> solution whose Navier–Stokes residual vanishes to infinite order at the singular point, with
> the force *defined* as that residual.
>
> That is exactly the route this note dismissed in Observation 2.1 and Step 2d, and exactly what
> Step 2a says the stages must do. So the sentences below claiming "no leverage" and "no one has"
> are wrong for the forced problem. They are corrected in place. The **unforced** problem is
> still open; see [`navier-stokes-status-2026.md`](navier-stokes-status-2026.md) and
> [`anisotropic-zoom-regularity.md`](anisotropic-zoom-regularity.md).

Every claim below carries one of these labels:

| Label | Meaning |
|---|---|
| **[Proved here]** | Full proof in this document. The algebra is checked symbolically by [`verify_scaling.py`](verify_scaling.py). |
| **[Known]** | A published theorem, cited. |
| **[openai/math]** | Claimed in the [openai/math](https://github.com/openai/math) collection, with its Lean status. |
| **[Open]** | Not known to anyone. This is where the attempt stops. |

---

## 1. The problem

The Clay problem (C. Fefferman's official statement) concerns the incompressible Navier–Stokes
equations on $\mathbb R^3$ or on the torus $\mathbb T^3=\mathbb R^3/\mathbb Z^3$:

$$
\partial_t u + (u\cdot\nabla)u = \nu\Delta u - \nabla p + f,\qquad \nabla\cdot u = 0,\qquad u(\cdot,0)=u_0,
$$

with viscosity $\nu>0$. Any one of four statements wins the prize:

- **(A) Existence and smoothness on $\mathbb R^3$.** With $f\equiv 0$ and smooth, rapidly decaying, divergence-free $u_0$, there is a smooth solution with bounded energy for all time.
- **(B) Existence and smoothness on $\mathbb T^3$.** Same, with periodic data.
- **(C) Breakdown on $\mathbb R^3$.** Some smooth $u_0$ and some smooth force $f$ with $|\partial_x^\alpha\partial_t^m f|\le C_{\alpha mK}(1+|x|+t)^{-K}$ admit no global smooth bounded-energy solution.
- **(D) Breakdown on $\mathbb T^3$.** Some smooth periodic $u_0$ and some smooth periodic force with $|\partial_x^\alpha\partial_t^m f|\le C_{\alpha mK}(1+t)^{-K}$ admit no global smooth solution.

**Why I aimed at (D).** The only part of openai/math that deals with 3D incompressible
Navier–Stokes is family 376. It builds forced flows on exactly $\mathbb T^3$. In one of its papers the
forces decay faster than every power of $t$, so they are *admissible* forces for (D). If any
openai/math result could serve as a stepping stone, it would be toward (D).

---

## 2. What the stepping stone actually does

**[openai/math, Lean-formalized]** Family 376, "Universal computation in forced Navier–Stokes
flows" (10 papers). For any fixed computable $\nu>0$, it builds smooth forces on $\mathbb T^3$ that
make a fluid starting at rest act as a Turing machine: a marked particle enters a fixed
region exactly when the machine halts.

Every paper in the family uses the same **realization lemma**. Choose any smooth
divergence-free $U$ with $U(0)=0$ and *define* the force as its residual:

$$
f := \mathcal R_\nu[U] = \partial_t U + (U\cdot\nabla)U - \nu\Delta U .
$$

Then $(u,p)=(U,0)$ solves the forced equation. Its uniqueness in a classical class follows
from Leray's energy estimate. The computing is designed into $U$, and the force pays for it.

**Observation 2.1 [Proved here].** On its own, the realization lemma cannot yield statement (D).

*Proof.* If $U$ is smooth for all time, the solution is smooth and nothing breaks down. If $U$
blows up at a time $T$, the force $\mathcal R_\nu[U]$ is admissible for (D) only if it extends
smoothly through $t=T$. In that case $U$ is itself a breakdown solution with an admissible force.
So "find $U$ that blows up while $\mathcal R_\nu[U]$ stays smooth" is just statement (D) restated. ∎

*Correction:* I originally concluded that the lemma "gives no leverage". That was wrong in
spirit. The restatement moves all of the difficulty into constructing such a $U$, and OpenAI's
September 2026 manuscript claims to do exactly that (see the correction at the top).

**Observation 2.2.** The paper *Computation under Rapidly Vanishing Navier–Stokes Forcing*
does push information to finer and finer spatial scales. But every step takes one unit of
time, and the velocity gets *smaller* at each step. A singularity needs the opposite: step $n$
at length scale $\lambda^n$ must take time about $\lambda^{2n}$ and have velocity about
$\lambda^{-n}$ (see §4).

---

## 3. Step 1: forcing becomes invisible at a singularity

**Proposition 1 [Proved here].** Let $\nu>0$, let $f$ be smooth on $\mathbb T^3\times[0,T]$, and
let $(u,p)$ solve the forced equation on $[0,T)$. Fix a base point $(x_0,t_0)$ with $t_0\le T$
and a scale $\lambda>0$. Define

$$
u^\lambda(y,s)=\lambda\,u(x_0+\lambda y,\ t_0+\lambda^2 s),\qquad
p^\lambda=\lambda^2 p(\cdots),\qquad
f^\lambda=\lambda^3 f(\cdots).
$$

Then:

1. $(u^\lambda,p^\lambda)$ solves the Navier–Stokes equations with the **same** viscosity $\nu$ and force $f^\lambda$, and $\nabla\cdot u^\lambda=0$.
2. $\sup|\partial_y^\alpha\partial_s^m f^\lambda| \le \lambda^{3+|\alpha|+2m}\,\sup|\partial_x^\alpha\partial_t^m f|$.

*Proof.* By the chain rule, each $\partial_y$ contributes a factor $\lambda$ and $\partial_s$
contributes $\lambda^2$. Each term of $\partial_s u + (u\cdot\nabla)u - \nu\Delta u + \nabla p$
then picks up exactly $\lambda^3$:

- $\partial_s u^\lambda$: $\lambda\cdot\lambda^2$
- $(u^\lambda\cdot\nabla_y)u^\lambda$: $\lambda\cdot\lambda\cdot\lambda$
- $\nu\Delta_y u^\lambda$: $\lambda\cdot\lambda^2$
- $\nabla_y p^\lambda$: $\lambda^2\cdot\lambda$

Hence the rescaled equation's left side equals $\lambda^3 f(\cdots)=f^\lambda$. The divergence
picks up $\lambda^2$, so it stays zero. Part 2 is the same chain rule applied to $f$.
Checks 1–3 in `verify_scaling.py` verify these identities symbolically for arbitrary smooth
fields. ∎

**What this means.** Zoom toward a singular time with $\lambda\to 0$, using the rescaling that
leaves the equation unchanged. The force then goes to zero in every $C^k$ norm. So any
breakdown with an admissible force must come from the **unforced** equation at small scales.
In particular, the unit-scale forcing programs of family 376 cannot be the mechanism: at scale
$\lambda$ their influence is $O(\lambda^3)$.

---

## 4. Step 2: try a self-replicating cascade (Tao's program)

This is the most promising known strategy for breakdown. Tao used it to prove blowup for an
*averaged* Navier–Stokes equation (J. Amer. Math. Soc. 2016) and proposed it for the real
equation.

Build a fluid "machine" $V$. In unit time, the machine moves most of its energy into a copy of
itself, smaller by a factor $\lambda<1$. Then repeat. Under the equation's scaling, stage $n$ has:

- length scale $\lambda^n$
- duration $\lambda^{2n}$
- velocity $\lambda^{-n}$

The durations add up to a finite time (check 6), while the velocity grows without bound. That
would be a singularity.

The natural idea is to build the machine $V$ with family 376's compiler.

### Step 2a: the stages must solve the *unforced* equation **[Proved here]**

Apply Proposition 1 at the base point and scale of stage $n$, with $\lambda_n=\lambda^n$. In its
own frame, stage $n$ must solve the Navier–Stokes equations with a force bounded by
$C_k\lambda^{(3+k)n}$ in $C^k$, which tends to 0 as $n\to\infty$. Equivalently, if stage $n$
is the realization of a rescaled profile $U_n(x,t)=\lambda^{-n}V_n(\lambda^{-n}(x-x_n),\lambda^{-2n}(t-t_n))$,
then $\partial_x^k\mathcal R_\nu[U_n]=\lambda^{-(3+k)n}(\partial^k\mathcal R_\nu V_n)(\cdots)$.
For the total force to stay admissible, we need $\|\mathcal R_\nu V_n\|_{C^k}=O(\lambda^{(3+k)n})$
for every $k$.

In other words, the replicating machine has to run under the fluid's **own** dynamics, to
every order. Family 376's machines have residual of size $O(1)$, because the force does the
computing. **This is where the openai/math stepping stone drops out.** No result in the
collection supplies a replicator driven by the unforced fluid.

### Step 2b: a cascade with boundedly many active stages cannot blow up **[Known + Proved here]**

Each stage has the same $L^3$ norm, $\|U_n(t)\|_{L^3}=\|V\|_{L^3}$, because $L^3$ is invariant
under the equation's scaling (check 4). Suppose at most $M$ stages are active at any moment and
the leftover "debris" at earlier, larger scales stays smooth. Then $\sup_{t<T}\|u(t)\|_{L^3}<\infty$.

- **[Known]** Escauriaza–Seregin–Šverák (Russian Math. Surveys 2003): a solution bounded in $L^\infty_tL^3_x$ is regular.
- **[Known]** Seregin (Comm. Math. Phys. 2012): at a singular time $T$, $\|u(t)\|_{L^3}\to\infty$ as $t\uparrow T$.

So the simple "one machine at a time" replicator cannot blow up.

*Caveat:* these theorems are stated for the unforced equation on $\mathbb R^3$, and Seregin also
has local versions. Proposition 1 makes smooth forcing a vanishing perturbation at small scales,
so I expect the same conclusion for admissible forcing on $\mathbb T^3$. I have **not** written
out that adaptation in full.

### Step 2c: what a successful cascade would need **[Known constraints]**

Combining 2a and 2b, a real breakdown has to be produced by the unforced dynamics at small
scales and must make the $L^3$ norm diverge. Known theorems rule out the simplest such shapes:

- **Exactly self-similar blowup:** impossible. Nečas–Růžička–Šverák (Acta Math. 1996) for profiles in $L^3$; Tsai (Arch. Ration. Mech. Anal. 1998) for $L^q$ with $3<q<\infty$ and for profiles with locally finite energy.
- **Type I blowup ($|u|\lesssim (T-t)^{-1/2}$) for axisymmetric flows:** impossible. Chen–Strain–Tsai–Yau (2008/2009); Koch–Nadirashvili–Seregin–Šverák (Acta Math. 2009).

What is left are cascades that pile up $L^3$ norm across unboundedly many scales at once
(roughly a $1/|x|$ profile, whose $L^3$ norm diverges logarithmically), are not exactly
self-similar, and are not axisymmetric with a type I bound. Alternatively, they could grow
faster than the parabolic rate (type II).

### Step 2d: the gap **[Open]**

To finish, one needs a smooth, finite-energy initial state that, under the **unforced**
viscous dynamics, produces a smaller rescaled copy of itself. The process must be robust enough
to repeat forever and must make the $L^3$ norm diverge.

**I could not construct one.**

*Correction:* for the forced problem (D) the requirement above is stronger than needed. By
Step 2a the stages only have to solve the unforced equation up to errors that vanish to
infinite order at the singular time. OpenAI's September 2026 manuscript claims exactly such a
construction. For the **unforced** problem no construction is known.

When this note was written, the closest rigorous results I listed were all for *other* equations:

- Tao (2016): blowup for an averaged Navier–Stokes equation with a modified nonlinearity.
- Elgindi (Ann. of Math. 2021): finite-time singularities for 3D Euler (no viscosity) from $C^{1,\alpha}$ data.
- Chen–Hou (2022–23, computer-assisted): blowup for 3D axisymmetric Euler with a boundary, from smooth data.

Numerical searches for Navier–Stokes candidates (e.g. Hou, 2021–23) are evidence, not proof.

**The attempt toward (C)/(D) stops here.**

---

## 5. The other direction: global regularity, (A)/(B)

I also checked whether the openai/math regularity and blowup results could be adapted to prove
smoothness. They can't.

**Vlasov–Maxwell (family 362, [openai/math, Lean-formalized]).** That proof rests on the
Glassey–Strauss continuation criterion (bounded momentum support implies smoothness) and on
retarded, light-cone representations of the fields. Both exist because relativistic particle
speeds $|v|/\sqrt{1+|v|^2}$ stay below 1, giving finite propagation speed. Navier–Stokes has no
light cone, and its pressure acts instantly everywhere.

**Supercriticality [Proved here, check 5].** The only coercive quantity known to stay bounded
for Navier–Stokes is the energy $\|u\|_{L^2}^2$. Zooming in by $u^\lambda(y)=\lambda u(\lambda y)$
multiplies the energy by $1/\lambda$. So the energy bound says less and less at small scales,
which is the core difficulty.

**[Known]** Tao's averaged equation (2016) satisfies the same energy identity and the usual
harmonic-analysis estimates, yet blows up. Any regularity proof must therefore use finer
structure of the true nonlinearity than those tools provide.

**Defocusing NLS blowup (family 371, [openai/math, Lean-formalized]).** That construction gets
an open set of blowup data by sending the nonlinearity's power $p\to\infty$ in dimension 12, a
limit where the problem becomes very supercritical. 3D Navier–Stokes has a fixed nonlinearity
and a fixed half-derivative gap between the energy $L^2$ and the critical $\dot H^{1/2}$.
There is no large parameter to send to a limit.

---

## 6. What this attempt established

| Item | Status |
|---|---|
| The realization lemma alone only restates (D) (Obs. 2.1); OpenAI's 2026 forced blowup builds on this restatement with a residual flat at the singularity | **Proved here** (restatement); OpenAI claim, Lean-formalized, under review |
| Smooth forcing vanishes like $\lambda^{3+k}$ in $C^k$ under the scaling that leaves NS unchanged (Prop. 1) | **Proved here**, symbolically checked |
| Each stage of a breakdown cascade must solve unforced NS to every order (Step 2a), so family 376's machines cannot be stages | **Proved here** |
| A cascade with boundedly many active stages cannot blow up (Step 2b) | **Known** (ESS 2003, Seregin 2012) + scale invariance (checked); adaptation to forced $\mathbb T^3$ not fully written out |
| A smooth unforced self-replicating configuration that drives $\|u\|_{L^3}\to\infty$ (Step 2d) | **Open** for the unforced problem. For the forced problem (D), replication up to flat errors is claimed by OpenAI (Sept 2026). |
| A regularity proof via the Vlasov–Maxwell or NLS methods (§5) | No route found; structural obstructions identified |

**Bottom line:** this note proves nothing about the Navier–Stokes problem in either direction.
The openai/math results do not reach it. The one family there that studies 3D Navier–Stokes
cannot supply the missing ingredient, because its forces do the work the fluid would have to do
by itself.

*Since corrected:* OpenAI's separate September 2026 release claims the forced alternatives (C)
and (D). The unforced alternatives, and unforced blowup, remain open.

---

## References

- C. Fefferman, *Existence and smoothness of the Navier–Stokes equation*, Clay Mathematics Institute official problem description (2000).
- J. Leray, *Sur le mouvement d'un liquide visqueux emplissant l'espace*, Acta Math. 63 (1934).
- J. Nečas, M. Růžička, V. Šverák, *On Leray's self-similar solutions of the Navier–Stokes equations*, Acta Math. 176 (1996).
- T.-P. Tsai, *On Leray's self-similar solutions of the Navier–Stokes equations satisfying local energy estimates*, Arch. Ration. Mech. Anal. 143 (1998).
- L. Escauriaza, G. Seregin, V. Šverák, *L<sub>3,∞</sub>-solutions of the Navier–Stokes equations and backward uniqueness*, Russian Math. Surveys 58 (2003).
- C.-C. Chen, R. Strain, T.-P. Tsai, H.-T. Yau, *Lower bounds on the blow-up rate of the axisymmetric Navier–Stokes equations* I (IMRN 2008) and II (Comm. PDE 2009).
- G. Koch, N. Nadirashvili, G. Seregin, V. Šverák, *Liouville theorems for the Navier–Stokes equations and applications*, Acta Math. 203 (2009).
- G. Seregin, *A certain necessary condition of potential blow up for Navier–Stokes equations*, Comm. Math. Phys. 312 (2012).
- T. Tao, *Finite time blowup for an averaged three-dimensional Navier–Stokes equation*, J. Amer. Math. Soc. 29 (2016).
- T. Elgindi, *Finite-time singularity formation for C<sup>1,α</sup> solutions to the incompressible Euler equations on ℝ³*, Ann. of Math. 194 (2021).
- J. Chen, T. Y. Hou, *Stable nearly self-similar blowup of the 2D Boussinesq and 3D Euler equations with smooth data* (2022–23, computer-assisted).
- R. Glassey, W. Strauss, *Singularity formation in a collisionless plasma could occur only at high velocities*, Arch. Ration. Mech. Anal. 92 (1986).
- OpenAI, family 376 papers, e.g. *Computation under Rapidly Vanishing Navier–Stokes Forcing* (Sept. 27, 2026), in [openai/math](https://github.com/openai/math).
