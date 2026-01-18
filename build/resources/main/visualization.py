import json
import numpy as np
import matplotlib.pyplot as plt

with open("figures.json", "r", encoding="utf-8") as f:
    data = json.load(f)

function_name = data["function"]
simplexes = data["figures"]

# funkcja celu sphere
def sphere(x):
    return sum(xi**2 for xi in x)

# funkcja celu rosenbrock
def rosenbrock(x):
    return np.sum(
        100.0 * (x[1:] - x[:-1]**2)**2 + (1 - x[:-1])**2,
        axis=0
    )


# wybor funkcji
if function_name == "sphere":
    f = sphere
    title = "Sphere function"
elif function_name == "rosenbrock":
    f = rosenbrock
    title = "Rosenbrock function"
else:
    raise ValueError(f"Nieznana funkcja: {function_name}")

# Zakres wykresu z danych
all_pts = np.array([v["coords"] for s in simplexes for v in s["vertices"]])
x0_min, x0_max = all_pts[:,0].min()-0.5, all_pts[:,0].max()+0.5
x1_min, x1_max = all_pts[:,1].min()-0.5, all_pts[:,1].max()+0.5

# Siatka konturów
xx = np.linspace(x0_min, x0_max, 300)
yy = np.linspace(x1_min, x1_max, 300)
X, Y = np.meshgrid(xx, yy)

def f2d(x, y):
    return f(np.array([x, y]))

Z = f2d(X, Y)


plt.figure(figsize=(8, 6))
plt.contour(X, Y, Z, levels=40)
plt.colorbar(label="f(x)")

# Rysowanie simplexów
for simplex in simplexes:
    pts = np.array([v["coords"][:2] for v in simplex["vertices"]])
    pts = np.vstack([pts, pts[0]])
    plt.plot(pts[:,0], pts[:,1], "o-", alpha=0.25)

# Sciezka najlepszych punktow
path = []
for simplex in simplexes:
    best = min(simplex["vertices"], key=lambda v: f(np.array(v["coords"])))
    path.append(best["coords"][:2])

path = np.array(path)
plt.plot(path[:,0], path[:,1], "r-", linewidth=2.5, label="Ścieżka minimum")
plt.plot(path[:,0], path[:,1], "ro", markersize=4)

# Punkt końcowy
plt.plot(path[-1,0], path[-1,1], "k*", markersize=14, label="Minimum końcowe")

plt.xlabel("x₀")
plt.ylabel("x₁")
plt.title(f"Nelder–Mead – {title}")
plt.legend()
plt.grid(True)
plt.axis("equal")
plt.show()

# Weryfikacja numeryczna
if function_name == "sphere":
    analytical_min = np.zeros(2)
elif function_name == "rosenbrock":
    analytical_min = np.ones(2)

error = np.linalg.norm(path[-1] - analytical_min)

print("Minimum numeryczne:", path[-1])
print("Minimum analityczne:", analytical_min)
print("Błąd:", error)

