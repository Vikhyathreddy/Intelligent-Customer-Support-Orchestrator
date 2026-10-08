# Navier–Stokes in October 2026: what is claimed, what is open, and where an unforced singularity could still hide

Each item is tagged with how well I checked it:

| Tag | Meaning |
|---|---|
| **[read]** | I read the statement in a primary source myself (Lean code or TeX). |
| **[classical]** | Established published result, cited from the literature. |
| **[reported]** | Taken from abstracts or secondary reports. I could not read the paper itself, because arxiv.org and cdn.openai.com are blocked by this environment's network policy. |

---

## 1. The forced problem, Clay (C) and (D): claimed solved

- **OpenAI, *Finite time blowup for Navier–Stokes* (September 2026).**
  - For every viscosity $\nu>0$: smooth initial data and a smooth force on $\mathbb R^3$ (resp. $\mathbb T^3$) with no global smooth solution of bounded energy (resp. no global smooth periodic solution).
  - The Lean 4 repository [openai/NavierStokesAndEuler](https://github.com/openai/NavierStokesAndEuler) states the theorems `navier_stokes_breakdown_R3` and `navier_stokes_breakdown_periodic`. Their statements are copied from Google DeepMind's formal-conjectures encoding of Clay's (C) and (D). **[read]**
  - The repository's `formalization.yaml` reports 0 `sorry` and only Lean's standard axioms. **[read]** I did not build the project.
- **How the construction works.** **[read]**, from `NavierStokes/LocalPaperTheorem.lean`, structure `Properties`.
  - It builds smooth $(u,p)$ on $t<1$ that are exactly axisymmetric near the axis.
  - The Navier–Stokes residual has all derivatives $O(q^r)$ for every $r$ near the singular point (`residual_flatness`), and it is exactly zero in an exterior region.
  - The force is that residual, so it extends smoothly by zero through $t=1$.
  - Rates: radial length $(1-t)^{1/2}$, axial length $(1-t)^{1/2-h}$, velocity $(1-t)^{-1/2-h}$, with $0<h<1/100$.
- **Status.** On September 11, 2026, Clay said the problem has "apparently been settled" and that its review is deliberately unhurried **[reported]**. Buckmaster and Alpöge announced related forced-blowup results (3D Euler, 2D Boussinesq, IPM with $C^\infty$ forcing) **[reported]**.
- **Companion result.** OpenAI also claims finite-time blowup for the **unforced 3D Euler** equations from smooth, compactly supported data. It is formalized in the same repository **[read: README]**.

Forced blowup does not settle (A)/(B). Those quantify over all data with **zero** force.

---

## 2. The unforced problem: open

Neither global regularity nor an unforced singularity is known. Any unforced singularity must
satisfy every item below.

### General constraints

| Constraint on a singularity at time $T$ | Source |
|---|---|
| Singular set has zero 1D parabolic Hausdorff measure | Caffarelli–Kohn–Nirenberg 1982 **[classical]** |
| $\|u(t)\|_{L^\infty}\ge c\,(\nu/(T-t))^{1/2}$ | Leray 1934 **[classical]** |
| $\|u(t)\|_{L^3}\to\infty$ | Escauriaza–Seregin–Šverák 2003; Seregin 2012 **[classical]** |
| Not backward self-similar with an $L^3$ or $L^p$ ($3<p<\infty$) or locally finite-energy profile | Nečas–Růžička–Šverák 1996; Tsai 1998 **[classical]** |
| Vorticity direction cannot stay coherent | Constantin–Fefferman 1993 **[classical]** |
| Not rotated-self-similar with a Type I bound, at small or large rotation speed | Pineau–Vicol 2026 **[reported, via CIV]** |

### Axisymmetric constraints

| Constraint | Source |
|---|---|
| No Type I blowup: $\lvert u\rvert\le C(-t)^{-1/2}$, or $r\lvert u\rvert\le C$, or a meridional-only Type I bound | Chen–Strain–Tsai–Yau 2008/09; Koch–Nadirashvili–Seregin–Šverák 2009; Seregin–Šverák 2009 **[classical]** |
| Must be Type II in scale-invariant integral quantities | Seregin 2020 **[reported, via CIV]** |
| No blowup under uniform $C^2$ bounds at an isotropic "Euler length" $\ell(t)$ with $(-t)/\ell^2\to0$ | CIV, [arXiv:2609.20762](https://arxiv.org/abs/2609.20762) **[reported]** |
| No blowup under OpenAI-type anisotropic bounds, rates $(\tfrac12,\tfrac12-h)$, with $C^2$ force | CIV, [arXiv:2609.20803](https://arxiv.org/abs/2609.20803) **[read: full TeX]**, Lean-formalized by S. Armstrong ([CIVAxisymmetric](https://github.com/scottnarmstrong/CIVAxisymmetric)) |
| **No blowup under kinematic anisotropic power-law bounds at any rates $(\alpha,\beta)$ with $\min(\alpha,\beta)<\tfrac12$, with $C^2$ force** | **This folder:** [`anisotropic-zoom-regularity.md`](anisotropic-zoom-regularity.md). An extension of CIV, hand-checked, not peer reviewed. |

### Constraints from analytic force and symmetry

- If the force is real analytic in space (in particular $f\equiv0$) and the solution is exactly axisymmetric on some core ball at each time, then the solution is axisymmetric everywhere. All the axisymmetric constraints above then apply. CIV §2 **[read]**.
- **Consequence for the OpenAI construction:** its force can be neither zero nor analytic near the singular point (CIV Corollary 2.3) **[read]**.
- If the non-axisymmetric part stays bounded in $C^3$, the point is regular even without analyticity (CIV Remark 1.2(e)) **[read]**.

### Constraints on the Euler-profile route

- A self-similar Euler blowup from finite-energy data must have similarity exponent $\gamma\ge2/5$.
- If the profile is smooth with an "outgoing" property, or is axisymmetric with a $C^2$ profile, then $\gamma\ge1/2$. That blocks the plan of seeding Navier–Stokes blowup from an Euler profile with $\gamma<1/2$, under those conditions. CIV, [arXiv:2602.17570](https://arxiv.org/abs/2602.17570) **[reported]**.

### Heuristic obstructions (not theorems)

- **Cascades of instabilities at lacunary frequencies:** in the three-dimensional parameter range, viscous loss is argued to beat the growth. This is attributed to Palasek and Looi, with discussion by Tao **[reported]**.
- **Positive defect:** Petrillo–Glimm list necessary features of a candidate, such as Type II in velocity and energy concentrating on a set of zero length. They report numerical searches without a candidate **[reported]**.

---

## 3. Where an unforced singularity could still be

Putting §2 together, an unforced singularity must avoid all of the following:
- exact self-similarity;
- Type I bounds in the axisymmetric class;
- uniform power-law anisotropic kinematic bounds on an axisymmetric core, at any rates (CIV plus [`anisotropic-zoom-regularity.md`](anisotropic-zoom-regularity.md));
- a non-axisymmetric part bounded in $C^3$ near an axisymmetric core.

What remains open:

1. **Genuinely three-dimensional cores.** The flow is not axisymmetric on any core ball, or its non-axisymmetric part is unbounded in $C^3$ there.
2. **Multi-scale or non-power-law cores.** Rates that change along the cascade (logarithmic, oscillating, or different on different parts of the ball), so that no single rate pair holds uniformly.
3. **Non-kinematic scaling.** Velocity components that are not length over time for any power-law lengths.
4. **Instability-driven cascades that beat the viscous obstruction.** These would need a mechanism the shell-model heuristics do not capture.

No known construction lives in any of these regions. A next systematic step would be a reduced
model in region 1 or 2 where blowup is either provable or provably ruled out.

---

## Sources

- OpenAI, *Finite time blowup for Navier–Stokes* and *Finite time blowup for the Euler equation* (2026); Lean: [openai/NavierStokesAndEuler](https://github.com/openai/NavierStokesAndEuler).
- Clay statement coverage: [The Decoder](https://the-decoder.com/clay-mathematics-institute-says-the-navier-stokes-millennium-prize-problem-has-apparently-been-settled/), [Tufts Daily](https://www.tuftsdaily.com/article/2026/09/mathematicians-still-checking-the-navier-stokes-proof-that-openai-claims-to-have-solved).
- P. Constantin, M. Ignatova, V. Vicol: [arXiv:2609.20803](https://arxiv.org/abs/2609.20803), [arXiv:2609.20762](https://arxiv.org/abs/2609.20762), [arXiv:2602.17570](https://arxiv.org/abs/2602.17570).
- Z. Lei, X. Ren, exposition of the OpenAI profile construction: [arXiv:2609.35406](https://arxiv.org/abs/2609.35406).
- J. Petrillo, J. Glimm, *The positive defect problem*: [arXiv:2609.23868](https://arxiv.org/abs/2609.23868).
- Density of forces producing blowup: [arXiv:2609.10262](https://arxiv.org/abs/2609.10262).
