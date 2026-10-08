# No anisotropic power-law blowup for axisymmetric Navier–Stokes: extending Constantin–Ignatova–Vicol

> **Status.** This note extends a September 2026 theorem of Constantin, Ignatova and Vicol
> (CIV, [arXiv:2609.20803](https://arxiv.org/abs/2609.20803)) from the single family of zoom rates
> they treat to every anisotropic power-law zoom rate. **It is a proof written and checked by
> hand. It has not been peer reviewed or machine-checked.** The exponent algebra is checked by
> [`verify_anisotropic_exponents.py`](verify_anisotropic_exponents.py). Most of the argument is
> CIV's. §2 lists exactly what is new.
>
> **Novelty, stated carefully.**
> - CIV prove the case $\alpha=\tfrac12>\beta$.
> - Their companion paper ([arXiv:2609.20762](https://arxiv.org/abs/2609.20762)) proves the isotropic case $\alpha=\beta<\tfrac12$.
> - Part of the range $\alpha>\tfrac12$ may already follow from known regularity criteria on the radial velocity (e.g. Kubica 2010). I could not check their exact statements, because arXiv is blocked in this environment.
> - The remaining cases do not appear in CIV's paper, but I could not survey the wider literature: $\alpha\ne\beta$ with both $<\tfrac12$, and $\beta\ge\tfrac12>\alpha$.
>
> **What it is not.** This is a regularity result. It shrinks the set of places an unforced
> singularity could hide (§7). It does not construct a singularity, and it does not solve a
> Millennium problem.

---

## 1. Statement

Notation follows CIV. $\mathcal Q=B(1)\times(-1,0)$; the putative singular point is $(0,0)$.
$(u,\pi)$ is a *suitable weak solution* in the sense of Caffarelli–Kohn–Nirenberg of

$$\partial_t u+(u\cdot\nabla)u-\Delta u+\nabla\pi=f,\qquad \operatorname{div}u=0 \quad\text{on } \mathcal Q,$$

smooth on compact subsets of $\mathcal Q$, with the force bounded in $C^2$ up to $t=0$:

$$M_f:=\sup_{-1<t<0}\|f(\cdot,t)\|_{C^2_x(B(1))}<\infty. \tag{F}$$

For exponents $\alpha,\beta>0$ define the radial and axial lengths
$\ell_r(t)=(-t)^{\alpha}$ and $\ell_z(t)=(-t)^{\beta}$. The **kinematic anisotropic bounds** say
that the meridional velocity has the size of length divided by time, and that each derivative
costs one inverse length. For all integers $a,b\ge0$ with $a+b\le2$, on $\mathcal Q$:

$$|\partial_r^a\partial_z^b u_r|\le C\,(-t)^{\alpha-1-a\alpha-b\beta},\qquad
|\partial_r^a\partial_z^b u_z|\le C\,(-t)^{\beta-1-a\alpha-b\beta}. \tag{H$_{\alpha\beta}$}$$

No bound is imposed on the swirl $u_\theta$. For $\alpha=\tfrac12$ and $\beta=\tfrac12-h$, these are
exactly the bounds of CIV Theorem 1.3 restricted to $u_r,u_z$, which CIV Remark 5.2 shows suffice.

**Theorem A.** Let $(u,\pi)$ be an axisymmetric suitable weak solution as above, with (F). Let
$\alpha,\beta>0$ with $\min(\alpha,\beta)<\tfrac12$, and assume (H$_{\alpha\beta}$). Then $(0,0)$
is a regular point.

**Corollary B (analytic force, axisymmetric core).** Let $(u,\pi)$ be a suitable weak solution,
smooth on compact subsets of $\mathcal Q$, whose force satisfies (F) and is real analytic in
space, locally uniformly (the analyticity hypothesis of CIV Theorem 1.1). Assume:
- the angular mean $v=\mathcal Pu$ satisfies (H$_{\alpha\beta}$) with $\min(\alpha,\beta)<\tfrac12$;
- for every $t$, $u(\cdot,t)$ is exactly axisymmetric on some ball $B(\rho(t))$ with $\rho(t)>0$.

Then $u$ is axisymmetric and $(0,0)$ is regular. In particular, this holds for **$f\equiv0$**.

*Proof of B from A.* CIV §2 applies word for word, since it uses no exponents. Kahane's interior
analyticity makes $u(\cdot,t)$ real analytic on $B(1)$ for each $t<0$. The identity theorem then
spreads axisymmetry from the core $B(\rho(t))$ to all of $B(1)$. Averaging the equation over
rotations keeps (F). Theorem A applies. ∎

**Corollary C (the full exponent quadrant, unforced).** Let $f\equiv0$ and let $u$ be axisymmetric
on a core as in Corollary B. Assume $v$ satisfies (H$_{\alpha\beta}$) for some $\alpha,\beta>0$. Then
$(0,0)$ is regular.

- If $\min(\alpha,\beta)<\tfrac12$, this is Corollary B.
- If $\alpha,\beta\ge\tfrac12$, then $|u_r|+|u_z|\le C(-t)^{-1/2}$. That is a Type I bound on the meridional velocity alone, which Seregin–Šverák (2009) show is regular for axisymmetric solutions, as CIV §1.5 describes. *This case rests on that citation, which I have not re-checked.*

---

## 2. What changes relative to CIV

CIV's proof has two parts. Proposition 1.5 (smallness of meridional quantities) is proved in §4
by zooming in. Lemma 5.1 (smallness implies regularity) is a local enstrophy estimate.

- **Lemma 5.1 uses no exponents.** It holds verbatim for $\alpha>\beta$. For $\alpha<\beta$ the large off-diagonal gradient is $\partial_z u_r$ instead of $\partial_r u_z$, so the integration by parts moves from $z$ to $r$ (§5).
- **The zoom (§4 of CIV) is where exponents enter.** The new ingredients:
  1. **A time unit adapted to the regime.** CIV rescale time by $(-t_n)$. When one length shrinks faster than parabolically ($\alpha>\tfrac12$ or $\beta>\tfrac12$), rescaling time by $(-t_n)$ makes that diffusion coefficient blow up. I rescale time by $\ell_r^2$ or $\ell_z^2$ instead, so that diffusion has size one and transport vanishes.
  2. **A Liouville theorem for bounded ancient solutions of the heat equation** replaces the comparison principle (CIV Lemma 3.3) in those regimes. The limit is constant in the diffusing variable, not zero. Boundedness of the limiting velocity then forces that constant to vanish (§4.4).
  3. **The comparison principle with no diffusion at all** ($d=0$) for $\alpha,\beta<\tfrac12$. CIV's proof of Lemma 3.3 works verbatim with $d=0$; see §4.3.
  4. **The roles of $V$ and $W$ swap when $\alpha<\beta$.** The rescaled azimuthal vorticity is normalized by its $\partial_z u_r$ part, and the endpoint argument integrates in $Z$ instead of $R$ (§4.4).

The regimes, as checked by `verify_anisotropic_exponents.py`:

| Regime | Time unit | Survives in the limit | Mechanism |
|---|---|---|---|
| A: $\beta<\alpha<\tfrac12$ | $-t_n$ | transport | comparison principle, $d=0$ |
| A′: $\alpha<\beta<\tfrac12$ | $-t_n$ | transport | comparison principle, $d=0$ |
| B: $\alpha=\tfrac12>\beta$ (CIV) | $-t_n$ | transport, radial diffusion | comparison principle, diffusion in $X$ |
| C: $\alpha>\tfrac12>\beta$ | $\ell_r(t_n)^2$ | radial diffusion | Liouville in $X$ |
| D: $\beta=\tfrac12>\alpha$ | $-t_n$ | transport, axial diffusion | comparison principle, diffusion in $Z$ |
| E: $\beta>\tfrac12>\alpha$ | $\ell_z(t_n)^2$ | axial diffusion | Liouville in $Z$ |
| diagonal $\alpha=\beta<\tfrac12$ | $-t_n$ | transport | comparison principle with $d=0$, then Liouville for a bounded div-free, curl-free field. CIV's Euler-length paper ([arXiv:2609.20762](https://arxiv.org/abs/2609.20762)) treats this isotropic case for unforced solutions. |

In every regime the swirl source and the force vanish in the limit (table in §3).

---

## 3. Rescaling and the exponent ledger

Fix a sequence $t_n\uparrow0$ and write

$$T=-t_n,\qquad \lambda=T^{\alpha},\qquad \mu=T^{\beta},\qquad \delta=\lambda/\mu=T^{\alpha-\beta}.$$

Let $\theta\in\{T,\lambda^2,\mu^2\}$ be the time unit of the regime, from the table above.

- **Finite-axis zoom:** $R=r/\lambda$, $Z=(z-z_n)/\mu$, $s=(t-t_n)/\theta$.
- **Receding-axis zoom:** $r=r_n+\lambda R$, same $Z$ and $s$.

With $\theta=T$ this is CIV's $\tau=t/(-t_n)$, shifted so that the selected time is $s=0$
instead of $\tau=-1$.

Rescaled meridional fields:

$$V_n=\frac{T}{\lambda}\,u_r,\qquad W_n=\frac{T}{\mu}\,u_z .$$

**Uniform bounds.** Under (H$_{\alpha\beta}$),
$|\partial_R^a\partial_Z^bV_n|\le C\,\bigl((-t)/T\bigr)^{\alpha-1-a\alpha-b\beta}$, and similarly for
$W_n$. No power of $T$ is left over (last check of the script).

- For $\theta=T$, $(-t)/T=|\tau|\ge1$, which gives the decaying rescaled bounds of CIV §4, Step 1.
- For $\theta=\lambda^2$ (regime C), $(-t)/T=1-s\lambda^2/T\in[1,2]$ for $s\in[-S,0]$ once $T^{1-2\alpha}\ge S$. This holds for large $n$ because $\alpha>\tfrac12$. So $V_n,W_n$ are bounded in $C^2$ uniformly on every slab $s\in[-S,0]$, with constants independent of the compact spatial set.
- The same holds for $\theta=\mu^2$ in regime E.

**Rescaled vorticities.** With $\Omega=\omega_\theta/r$ (potential vorticity; finite axis) and
$\omega_\theta=\partial_zu_r-\partial_ru_z$ (receding axis):

| | $\alpha\ge\beta$ | $\alpha<\beta$ |
|---|---|---|
| finite axis | $\Omega_n=\dfrac{T\lambda^2}{\mu}\,\Omega=\dfrac{\delta^2\partial_ZV_n-\partial_RW_n}{R}$ | $\Omega_n=T\mu\,\Omega=\dfrac{\partial_ZV_n-\delta^{-2}\partial_RW_n}{R}$ |
| receding axis | $\Theta_n=\dfrac{T\lambda}{\mu}\,\omega_\theta=\delta^2\partial_ZV_n-\partial_RW_n$ | $\Theta_n=\dfrac{T\mu}{\lambda}\,\omega_\theta=\partial_ZV_n-\delta^{-2}\partial_RW_n$ |

In each case the subdominant coefficient, $\delta^2$ or $\delta^{-2}$, tends to $0$ when $\alpha\ne\beta$.

**Equations.** Multiply CIV's potential-vorticity equation (§3.1) by the normalization times $\theta$.
In the lifted variables $X\in\mathbb R^4$, $|X|=R$ (as in CIV §4, Step 2), the finite-axis equation is

$$\partial_s\Omega_n+e_{\rm tr}\bigl(\operatorname{div}_{X,Z}(\mathcal B_n\Omega_n)-2\tfrac{V_n}{R}\Omega_n\bigr)
= e_r\,\Delta_X\Omega_n+e_z\,\partial_Z^2\Omega_n+e_{\rm sw}\,\partial_Z\!\Bigl(\frac{\widetilde\Gamma_n^2}{R^4}\Bigr)+e_f\,\widetilde g_n ,$$

where $\mathcal B_n=(V_nX/R,\,W_n)$ and $\widetilde\Gamma_n,\widetilde g_n$ are the circulation $ru_\theta$ and
$g=(\operatorname{curl}f)_\theta/r$ evaluated at the zoomed point. Both are bounded:
$|\widetilde\Gamma_n|\le C_\Gamma$ by CIV Lemma 3.2, which uses no exponents, and $|\widetilde g_n|\le CM_f$.
The receding-axis equation is the analogue of CIV's azimuthal-vorticity equation in §4, Step 3, with the same coefficients and the swirl
source $e_{\rm sw}\,\partial_Z(\widetilde\Gamma_n^2)/(A_n+R)^3$.

The coefficients are powers of $T$, computed by the script:

| coefficient | A | A′ | B | C | D | E |
|---|---|---|---|---|---|---|
| transport $e_{\rm tr}=\theta/T$ | $1$ | $1$ | $1$ | $T^{2\alpha-1}$ | $1$ | $T^{2\beta-1}$ |
| radial diffusion $e_r=\theta/\lambda^2$ | $T^{1-2\alpha}$ | $T^{1-2\alpha}$ | $1$ | $1$ | $T^{1-2\alpha}$ | $T^{2\beta-2\alpha}$ |
| axial diffusion $e_z=\theta/\mu^2$ | $T^{1-2\beta}$ | $T^{1-2\beta}$ | $T^{2h}$ | $T^{2\alpha-2\beta}$ | $1$ | $1$ |
| swirl $e_{\rm sw}$ | $T^{2-2\alpha-2\beta}$ | $T^{2-4\alpha}$ | $T^{2h}$ | $T^{1-2\beta}$ | $T^{2-4\alpha}$ | $T^{1+2\beta-4\alpha}$ |
| force $e_f$ (finite axis) | $T^{2+2\alpha-\beta}$ | $T^{2+\beta}$ | $T^{5/2+h}$ | $T^{1+4\alpha-\beta}$ | $T^{2+\beta}$ | $T^{1+3\beta}$ |

In regime B, $\beta=\tfrac12-h$, and the swirl and force entries match CIV's $(-t)^{2h}$ and
$\lambda_n^5\delta_n=T^{5/2+h}$. That cross-check confirms the normalizations.

**The swirl source vanishes in $L^1_{\rm loc}$ of the lifted variables, up to the axis.** This
follows CIV Remark 5.2. By (H$_{\alpha\beta}$), $|u_r/r|\le\sup|\partial_ru_r|\le C(-t)^{-1}$ for
every $\alpha$, so the maximum principle gives $|u_\theta|\le C_N(-t)^{-N}$ near the axis. Then
$\widetilde\Gamma_n^2/R^4\cdot R^3\le\min\{C_\Gamma^2/R,\;\lambda^2C_N^2T^{-2N}R\}$, and
$\int_0^L\widetilde\Gamma_n^2R^{-1}\,dR\le C(1+|\log T|)$. Multiplying by $e_{\rm sw}=T^{(\text{positive})}$
gives $0$ in the limit.

---

## 4. Smallness of the meridional quantities

Define, for $0<\rho<1$,

$$G_\rho(t)=\Bigl\|\,|\partial_ru_r|+|u_r/r|+|\partial_zu_z|+|\partial_\star u_\diamond|\,\Bigr\|_{L^\infty(B(\rho))},
\qquad |\partial_\star u_\diamond|=\begin{cases}|\partial_zu_r|,&\alpha>\beta,\\ |\partial_ru_z|,&\alpha<\beta,\\ |\partial_zu_r|+|\partial_ru_z|,&\alpha=\beta.\end{cases}$$

**Proposition 4.1.** Under the hypotheses of Theorem A, $(-t)G_\rho(t)\to0$ as $t\uparrow0$, for
every $\rho<1$.

On the diagonal $\alpha=\beta<\tfrac12$, this is the isotropic Euler-length situation that CIV
treat for unforced solutions in [arXiv:2609.20762](https://arxiv.org/abs/2609.20762). The
argument below covers it as well, with a $C^2$ force and no swirl bounds (case 5 of §4.5).

### 4.1 The off-diagonal term and the selection

By (H$_{\alpha\beta}$), $(-t)|\partial_zu_r|\le C(-t)^{\alpha-\beta}$ and $(-t)|\partial_ru_z|\le C(-t)^{\beta-\alpha}$.
So for $\alpha\ne\beta$ the chosen off-diagonal term tends to $0$. If the proposition fails, then
exactly as at the start of CIV §4 there are $c_0>0$, $t_n\uparrow0$ and $x_n=(r_n,0,z_n)\in B(\rho)$
with

$$T\bigl(|\partial_ru_r|+|u_r/r|+|\partial_zu_z|+\mathbf 1_{\alpha=\beta}(|\partial_zu_r|+|\partial_ru_z|)\bigr)(x_n,t_n)\ge c_0 . \tag{4.1}$$

At the selected point these quantities equal $|\partial_RV_n|+|V_n/R|+|\partial_ZW_n|$ at $s=0$,
plus, on the diagonal, $|\partial_ZV_n|+|\partial_RW_n|$ (because $\delta=1$). This follows from the
chain rule and the normalizations of §3. Pass to a subsequence along which
$r_n/\lambda$ is bounded (finite axis) or tends to infinity (receding axis).

### 4.2 Compactness and passage to the limit (all regimes)

This is verbatim CIV §4, Steps 2–3, with these observations:

- On compact $K\subset\{R>0\}$, the vorticity formulas of §3 and the $C^2$ bounds on $V_n,W_n$ give uniform Lipschitz bounds on $\Omega_n$, resp. $\Theta_n$.
- $\partial_s\Omega_n$ is bounded in $W^{-2,p}(\operatorname{int}K)$ uniformly on compact $s$-intervals, because every coefficient in the table is $O(1)$.
- Hence, after a subsequence, $\Omega_n\to\Omega_\infty$ locally uniformly on $\{R>0,\ s\le0\}$, endpoint included, and in $L^p_{\rm loc}$ across the axis, using the uniform bound up to the axis from §3.
- Terms with a coefficient $T^{(\text{positive})}$ vanish in distributions. Terms with coefficient $1$ pass to the limit as in CIV: weak-$*$ convergence of the drift against strong convergence of $\Omega_n$.

The limit $\Omega_\infty$ (resp. $\Theta_\infty$) is bounded and continuous on $\{R>0,s\le0\}$.
It solves, in distributions on the whole ancient domain:

| Regime | Finite axis ($\mathbb R^4_X\times\mathbb R_Z$) | Receding axis ($\mathbb R^2_{R,Z}$) |
|---|---|---|
| A, A′ | $\partial_s q+\mathcal B\cdot\nabla q=0$ | $\partial_s q+(V,W)\cdot\nabla q=0$ |
| B | $\partial_s q+\mathcal B\cdot\nabla q=\Delta_Xq$ | $\partial_s q+(V,W)\cdot\nabla q=\partial_R^2q$ |
| C | $\partial_s q=\Delta_Xq$ | $\partial_s q=\partial_R^2 q$ |
| D | $\partial_s q+\mathcal B\cdot\nabla q=\partial_Z^2q$ | $\partial_s q+(V,W)\cdot\nabla q=\partial_Z^2q$ |
| E | $\partial_s q=\partial_Z^2q$ | $\partial_s q=\partial_Z^2q$ |

In regimes A, A′, B and D, the limiting drift is bounded and Lipschitz uniformly on compact time
intervals, as CIV require in Lemma 3.3. This follows from the uniform $C^2$ bounds, as in CIV.

### 4.3 Regimes with transport (A, A′, B, D): the limit vanishes

Here $\theta=T$ and the time variable is CIV's $\tau=t/T\le-1$.

**Decay.** The dominant term of the rescaled vorticity is bounded by $|\tau|^{-\kappa}$, with:

| | finite axis | receding axis |
|---|---|---|
| $\alpha>\beta$ | $\kappa=1+2\alpha-\beta$ (from $\sup\lvert\partial_R^2W_n\rvert$) | $\kappa=1+\alpha-\beta$ (from $\sup\lvert\partial_RW_n\rvert$) |
| $\alpha<\beta$ | $\kappa=1+\beta$ (from $\sup\lvert\partial_R\partial_ZV_n\rvert$) | $\kappa=1+\beta-\alpha$ (from $\sup\lvert\partial_ZV_n\rvert$) |

The subdominant term carries the factor $\delta^{\pm2}\to0$. On the diagonal $\delta=1$, and both
terms obey the same bound, giving $\kappa=1+\alpha$ (finite axis) and $\kappa=1$ (receding axis),
the values of the first row at $\alpha=\beta$. All values of $\kappa$ are positive (checked by
the script).

**Comparison principle.** CIV Lemma 3.3 is stated for diffusion in $1\le d\le m$ variables
$X$. Its proof applies unchanged in two further cases:
- **with $d=0$:** the barrier $\Psi_\sigma$ satisfies $\partial_\tau\Psi_\sigma+B\cdot\nabla\Psi_\sigma\ge\sigma(K-\Lambda_J)\ge0$ with $K=\Lambda_J$. The positive-part energy inequality and the DiPerna–Lions commutator estimate do not involve diffusion;
- **with the diffusing variable being $Z$ instead of $X$:** the lemma does not distinguish coordinates beyond naming them.

So the last assertion of the lemma gives $q\equiv0$.

### 4.4 Regimes without transport (C, E): the limit is constant, then zero

**Liouville lemma.** Let $q\in L^\infty(\mathbb R^k\times\mathbb R^j\times(-\infty,0))$ solve
$\partial_sq=\Delta_yq$ in distributions, where $y\in\mathbb R^k$ and $p\in\mathbb R^j$ is a parameter.
Then $q(y,p,s)=c(p)$ almost everywhere.

*Proof.* For $\varphi\in C_c^\infty(\mathbb R^j)$, the function $q_\varphi(y,s)=\int q(y,p,s)\varphi(p)\,dp$
is a bounded distributional solution of the heat equation on $\mathbb R^k\times(-\infty,0)$. By
hypoellipticity it is smooth. Bounded solutions of the Cauchy problem are unique, so
$q_\varphi(\cdot,s)=e^{(s-s_0)\Delta}q_\varphi(\cdot,s_0)$ for $s_0<s$. Hence
$\|\nabla_yq_\varphi(\cdot,s)\|_\infty\le C_k\|q_\varphi\|_\infty(s-s_0)^{-1/2}$. Letting
$s_0\to-\infty$ gives $\nabla_yq_\varphi=0$, and then $\partial_sq_\varphi=0$. So $q_\varphi$ is a
constant $\kappa(\varphi)$, linear in $\varphi$. Hence $q$ does not depend on $(y,s)$ almost
everywhere. ∎

**Regime C.** The diffusing variable is $X$ (finite axis) or $R$ (receding); the parameter is $Z$.
Since the limit is continuous on $\{R>0,s\le0\}$, $q(\cdot,\cdot,0)=c(Z)$ there.

**Regime E.** The diffusing variable is $Z$; the parameter is $X$ (radial, so a function of $R$)
or $R$. So $q(\cdot,\cdot,0)=c(R)$.

### 4.5 The endpoint $s=0$: the selected gradients vanish

By the uniform $C^2$ bounds, $V_n(\cdot,0)\to V^0$ and $W_n(\cdot,0)\to W^0$ in $C^1$ on compact
subsets, with $V^0,W^0$ **bounded on the whole domain**. The divergence identity becomes:
- $\partial_RV^0+V^0/R+\partial_ZW^0=0$ for the finite axis;
- $\partial_RV^0+\partial_ZW^0=0$ for the receding axis, since $V_n/(A_n+R)\to0$.

The four cases below cover every regime. In regimes A, A′, B and D, $c\equiv0$ by §4.3, and the
cases simply start one step later.

1. **$\alpha>\beta$, finite axis.** The limit of $\Omega_n(\cdot,0)$ is $-\partial_RW^0/R=c(Z)$. So $W^0=W^0(0,Z)-c(Z)R^2/2$, and boundedness in $R$ forces $c\equiv0$. Then $\partial_RW^0=0$, so $\partial_R(RV^0)=-R\,\partial_ZW^0(Z)$. Since $RV^0=0$ on the axis, $V^0=-\tfrac R2\partial_ZW^0$. Boundedness forces $\partial_ZW^0=0$ and $V^0=0$. *(This is CIV's argument once $c=0$.)*
2. **$\alpha>\beta$, receding axis.** $-\partial_RW^0=c(Z)$, so $W^0=W^0(0,Z)-c(Z)R$ on $R\in\mathbb R$, and boundedness forces $c=0$. Then $V^0=V^0(0,Z)-R\,\partial_ZW^0$, and boundedness forces $\partial_ZW^0=0=\partial_RV^0$.
3. **$\alpha<\beta$, finite axis.** $\partial_ZV^0/R=c(R)$, so $V^0=V^0(R,0)+Rc(R)Z$, and boundedness in $Z$ forces $c=0$. Hence $V^0=V^0(R)$. The divergence identity gives $\partial_ZW^0=-k(R)$ with $k=\partial_RV^0+V^0/R$, so $W^0=W^0(R,0)-k(R)Z$, and boundedness forces $k=0$. Then $\partial_R(RV^0)=0$ and $RV^0=0$ on the axis, so $V^0=0$ and $\partial_ZW^0=0$.
4. **$\alpha<\beta$, receding axis.** $\partial_ZV^0=c(R)$, so as in case 3 $c=0$ and $V^0=V^0(R)$. Then $W^0=W^0(R,0)-Z\,\partial_RV^0$, and boundedness forces $\partial_RV^0=0=\partial_ZW^0$.

5. **$\alpha=\beta<\tfrac12$ (regime A with $\delta=1$), finite or receding axis.** Here §4.3 gives $q\equiv0$, i.e. $\partial_ZV^0-\partial_RW^0=0$, together with the divergence identity. Consider the meridional field $b^0=V^0e_R+W^0e_Z$: on $\mathbb R^3$ for the finite axis, or $(V^0,W^0)$ on $\mathbb R^2$ for the receding axis.
   - It is $C^1$. For the finite axis, the Cartesian derivatives involve $\partial_RV,\ V/R,\ \partial_ZV,\ \partial_RW,\ \partial_ZW$, all of which converge locally uniformly up to the axis, as in CIV.
   - It is divergence-free and curl-free, hence harmonic.
   - It is bounded.

   By Liouville's theorem it is constant, so every first derivative vanishes, including $\partial_ZV^0$ and $\partial_RW^0$.

In every case $\partial_RV_n,\ V_n/R,\ \partial_ZW_n\to0$ (and, in case 5, also $\partial_ZV_n,\partial_RW_n\to0$) uniformly on compact sets at $s=0$. For the
finite axis this holds up to the axis, by CIV's argument at the end of §4, Step 2. For the receding axis,
$T|u_r(x_n,t_n)|/r_n\le C\lambda/r_n=C/A_n\to0$ by (H$_{\alpha\beta}$), as in CIV's quotient estimate at the end of §4, Step 3. This
contradicts (4.1), and proves Proposition 4.1. ∎

---

## 5. Smallness implies regularity, including the case $\alpha<\beta$

CIV Lemma 5.1 uses no exponents. For $\alpha>\beta$ it applies as written, since $G_\rho$ is
CIV's $G_\rho$.

For $\alpha<\beta$, $G_\rho$ contains $\partial_ru_z$ instead of $\partial_zu_r$. In the stretching
identity from the proof of CIV Lemma 5.1,

$$(\omega\cdot\nabla u)\cdot\omega=(\partial_ru_r)\omega_r^2+\tfrac{u_r}{r}\omega_\theta^2+(\partial_zu_z)\omega_z^2+(\partial_zu_r+\partial_ru_z)\omega_r\omega_z-2\tfrac{u_\theta}{r}\omega_r\omega_\theta ,$$

every term is then bounded by $C\,G\,Y$ except $I'=\int\chi^2(\partial_zu_r)\omega_r\omega_z\,dx$ and
the last one, which CIV already handle. Write $\omega_z=r^{-1}\partial_r(ru_\theta)$ and
$dx=r\,dr\,d\vartheta\,dz$, and integrate by parts in $r$. There is no boundary term: $ru_\theta=0$
on the axis, and $\chi=0$ near $\partial B(R_*)$. This gives

$$I'=2\pi\!\int\!\!\int\chi^2(\partial_zu_r)\,\omega_r\,\partial_r(ru_\theta)\,dr\,dz
=-\int u_\theta\,\partial_r\bigl(\chi^2(\partial_zu_r)\omega_r\bigr)\,dx .$$

The term with $\partial_r\chi$ lives in the regular annulus and is bounded by $C$. In the rest,
$|\partial_r\partial_zu_r|\le|\nabla^2b|$, $|\partial_zu_r|\le|\nabla b|$ and $|\partial_r\omega_r|\le|\nabla\omega|$.
To see this, evaluate at $\vartheta=0$, where $u_r$ and $\omega_r$ are the first Cartesian
components and $\partial_r=\partial_{x_1}$; by rotation invariance the bounds hold everywhere.
With the elliptic bounds from the proof of CIV Lemma 5.1 and $W=\|u_\theta\|_{L^\infty(B(R_*))}$:

$$|I'|\le C+W\|\chi\nabla^2b\|_2\|\chi\omega\|_2+W\|\chi\nabla b\|_2\|\chi\nabla\omega\|_2\le\tfrac14M+C(W^2+1)(Y+1).$$

This is the bound CIV prove for their term $I$ in Lemma 5.1. The rest of CIV's proof is unchanged: the bound
$W\le C_\eta(-t)^{-\eta}$, the choice of $\eta$, Grönwall, and the Ladyzhenskaya–Prodi–Serrin
endpoint with the Gustafson–Kang–Tsai criterion. ∎

On the diagonal $\alpha=\beta$, $G_\rho$ contains both off-diagonal gradients. So every term of
the stretching identity except the last is bounded by $C\,G\,Y$, and no integration by parts is
needed.

**Proof of Theorem A.** Proposition 4.1, then §5 (CIV Lemma 5.1, or its variant above when
$\alpha<\beta$). ∎

---

## 6. Checks performed

- **Exponent algebra.** `verify_anisotropic_exponents.py` derives every coefficient in §3 from the definitions. It checks:
  - which coefficients survive (exponent $0$) and which vanish (exponent $>0$), in each regime, on 1,919 sample points;
  - the four decay exponents $\kappa$;
  - the scale invariance of the hypotheses.

  Regime B reproduces CIV's constants: $\kappa=\tfrac32+h$ and $1+h$, force factors $T^{5/2+h}$ and $T^{2+h}$, swirl factor $T^{2h}$.
- **Analytic steps.** Each step of §§4–5 was compared line by line with CIV §§3–5 ([TeX source](https://github.com/scottnarmstrong/CIVAxisymmetric/tree/main/paper)). The steps marked as new are the time unit, the Liouville lemma, the $d=0$ comparison, the swapped endpoint cases 3–4, and the $r$-integration by parts.
- **Not done.** No machine-checked proof. Scott Armstrong's Lean formalization of CIV ([CIVAxisymmetric](https://github.com/scottnarmstrong/CIVAxisymmetric)) would be the natural base for one. No independent review.

---

## 7. What this means for the unforced problem

Combined with CIV and the type I literature, Corollary C says the following. An unforced 3D
Navier–Stokes singularity that is exactly axisymmetric on a shrinking core cannot have meridional
velocity obeying uniform kinematic power-law bounds (H$_{\alpha\beta}$) at **any** pair of rates
$\alpha,\beta>0$, isotropic or not. The cases $\alpha,\beta\ge\tfrac12$ rest on Seregin–Šverák.

So a singularity of that kind must do at least one of the following:

1. **break axisymmetry at every scale near the core,** with the non-axisymmetric part unbounded in $C^3$ (CIV Remark 1.2(e) shows a $C^3$ bound would already give regularity, even without analyticity);
2. **not follow a single power-law rate pair on the whole ball,** for example several coexisting scalings, logarithmic or oscillating rates, or rates that change along the cascade;
3. **have velocity not tied to length over time** (non-kinematic scaling), so that (H$_{\alpha\beta}$) fails even when the lengths are power laws.

These are the remaining places a systematic search for an unforced singularity has to look. The
OpenAI forced construction, which is axisymmetric on a parabolic core with rates
$(\tfrac12,\tfrac12-h)$, sits squarely inside the excluded region once the force is removed, as CIV
already showed.

---

## References

- P. Constantin, M. Ignatova, V. Vicol, *Regularity of asymptotically axisymmetric solutions to the 3D Navier–Stokes equations with analytic forcing*, [arXiv:2609.20803](https://arxiv.org/abs/2609.20803) (2026). TeX source and Lean formalization: [scottnarmstrong/CIVAxisymmetric](https://github.com/scottnarmstrong/CIVAxisymmetric).
- P. Constantin, M. Ignatova, V. Vicol, *Regularity for axisymmetric Navier–Stokes with an Euler length*, [arXiv:2609.20762](https://arxiv.org/abs/2609.20762) (2026).
- P. Constantin, M. Ignatova, V. Vicol, *On putative self-similarity for incompressible 3D Euler*, [arXiv:2602.17570](https://arxiv.org/abs/2602.17570) (2026).
- G. Seregin, V. Šverák, *On type I singularities of the local axi-symmetric solutions of the Navier–Stokes equations*, Comm. PDE 34 (2009).
- L. Caffarelli, R. Kohn, L. Nirenberg, *Partial regularity of suitable weak solutions of the Navier–Stokes equations*, CPAM 35 (1982).
- S. Gustafson, K. Kang, T.-P. Tsai, *Interior regularity criteria for suitable weak solutions of the Navier–Stokes equations*, CMP 273 (2007).
- R. DiPerna, P.-L. Lions, *Ordinary differential equations, transport theory and Sobolev spaces*, Invent. Math. 98 (1989).
- A. Kubica, *Remarks on interior regularity criterion for an axially symmetric suitable weak solution to the Navier–Stokes equations* (2010).
