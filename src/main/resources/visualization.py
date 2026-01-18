import json
import numpy as np
import matplotlib.pyplot as plt

with open("figures.json", "r", encoding="utf-8") as f:
    simplexes = json.load(f)

# funkcja celu (n-wymiarowa Rosenbrocka)
def rosenbrock(x):
    return sum(100*(x[i+1] - x[i]**2)**2 + (1-x[i])**2 for i in range(len(x)-1))

# Zakres wykresu z danych
all_pts = np.array([v["coords"] for s in simplexes for v in s["vertices"]])
x0_min, x0_max = all_pts[:,0].min()-0.5, all_pts[:,0].max()+0.5
x1_min, x1_max = all_pts[:,1].min()-0.5, all_pts[:,1].max()+0.5

# Siatka konturów (tylko dla osi 0 i 1)
xx = np.linspace(x0_min, x0_max, 300)
yy = np.linspace(x1_min, x1_max, 300)
X, Y = np.meshgrid(xx, yy)

# Ocenianie funkcji przy pozostałych współrzędnych = 0
def f2d(x, y):
    n = all_pts.shape[1]
    v = np.zeros(n)
    v[0] = x
    v[1] = y
    return rosenbrock(v)

Z = np.vectorize(f2d)(X, Y)

plt.figure(figsize=(8, 6))
plt.contour(X, Y, Z, levels=40)
plt.colorbar(label="f(x)")

# Rysowanie simplexów
for simplex in simplexes:
    pts = np.array([v["coords"][:2] for v in simplex["vertices"]])
    pts = np.vstack([pts, pts[0]])
    plt.plot(pts[:,0], pts[:,1], "o-", alpha=0.35)

# Punkt końcowy
last = simplexes[-1]
best = min(last["vertices"], key=lambda v: rosenbrock(np.array(v["coords"])))
bx, by = best["coords"][:2]
plt.plot(bx, by, "ro", markersize=10, label="Minimum")

plt.xlabel("x₀")
plt.ylabel("x₁")
plt.title("Nelder–Mead (rzut na 2 wymiary)")
plt.legend()
plt.grid(True)
plt.show()

