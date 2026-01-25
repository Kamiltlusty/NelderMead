import json
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from matplotlib.lines import Line2D   # proxy do legendy

# 1 Dane
with open("figures.json", "r", encoding="utf-8") as f:
    data = json.load(f)

function_name = data["function"]
simplexes = data["figures"]

# 2 Funkcje celu
def sphere(x):
    return sum(xi**2 for xi in x)

def rosenbrock(x):
    return np.sum(
        100.0 * (x[1:] - x[:-1]**2)**2 + (1 - x[:-1])**2,
        axis=0
    )

if function_name == "sphere":
    f = sphere
    title = "Sphere function"
elif function_name == "rosenbrock":
    f = rosenbrock
    title = "Rosenbrock function"
else:
    raise ValueError("Nieznana funkcja")

# 3 Siatka
all_pts = np.array([v["coords"] for s in simplexes for v in s["vertices"]])
x0_min, x0_max = all_pts[:, 0].min() - 0.5, all_pts[:, 0].max() + 0.5
x1_min, x1_max = all_pts[:, 1].min() - 0.5, all_pts[:, 1].max() + 0.5

xx = np.linspace(x0_min, x0_max, 300)
yy = np.linspace(x1_min, x1_max, 300)
X, Y = np.meshgrid(xx, yy)
Z = f(np.array([X, Y]))

# 4 Rysunek
fig, ax = plt.subplots(figsize=(8, 6))

manager = plt.get_current_fig_manager()
manager.window.state('zoomed')

ax.contour(X, Y, Z, levels=40)

ax.set_title(f"Nelder–Mead – {title}")
ax.set_xlabel("x₀")
ax.set_ylabel("x₁")
ax.axis("equal")
ax.grid(True)

# aktualny simpleks
simplex_line, = ax.plot([], [], "o-", lw=2, label="Aktualny simpleks")

# historia simpleksów
history_lines = []

history_proxy = Line2D(
    [0], [0],
    color="black",
    lw=1,
    alpha=0.6,
    label="Poprzednie simpleksy"
)

# ścieżka minimum
path_line, = ax.plot([], [], "r-", lw=2, label="Ścieżka minimum")
best_point, = ax.plot([], [], "ro")

# punkt startowy i końcowy
start_point, = ax.plot([], [], "gs", ms=8, label="Punkt startowy")
final_point, = ax.plot([], [], "k*", ms=12, label="Minimum końcowe")

# teksty
step_text = ax.text(0.02, 0.95, "", transform=ax.transAxes)
start_text = ax.text(0.02, 0.90, "", transform=ax.transAxes)
end_text = ax.text(0.02, 0.85, "", transform=ax.transAxes)

ax.legend(
    handles=[
        simplex_line,
        history_proxy,
        path_line,
        start_point,
        final_point
    ],
    loc="upper right"
)

path = []

# 5 Animacja
def update(frame):
    simplex = simplexes[frame]

    pts = np.array([v["coords"][:2] for v in simplex["vertices"]])
    pts_closed = np.vstack([pts, pts[0]])

    # poprzedni simpleks
    if frame > 0:
        prev = np.array(
            [v["coords"][:2] for v in simplexes[frame - 1]["vertices"]]
        )
        prev = np.vstack([prev, prev[0]])

        line, = ax.plot(
            prev[:, 0],
            prev[:, 1],
            color="black",
            lw=1,
            alpha=0.6,
            zorder=1
        )
        history_lines.append(line)

    # aktualny simpleks
    simplex_line.set_data(pts_closed[:, 0], pts_closed[:, 1])

    # najlepszy punkt
    best = min(
        simplex["vertices"],
        key=lambda v: f(np.array(v["coords"]))
    )

    path.append(best["coords"][:2])
    p = np.array(path)

    path_line.set_data(p[:, 0], p[:, 1])
    best_point.set_data([p[-1, 0]], [p[-1, 1]])

    # punkt startowy
    if frame == 0:
        start = p[0]
        start_point.set_data([start[0]], [start[1]])
        start_text.set_text(f"Start: ({start[0]:.4f}, {start[1]:.4f})")

    # punkt końcowy
    if frame == len(simplexes) - 1:
        final_point.set_data([p[-1, 0]], [p[-1, 1]])
        end_text.set_text(f"Koniec: ({p[-1,0]:.4f}, {p[-1,1]:.4f})")
        # Legenda bez aktualnego simplexu
        ax.legend(
            handles=[
                history_proxy,
                path_line,
                start_point,
                final_point
            ],
            loc="upper right"
        )

    step_text.set_text(f"Krok {frame + 1}/{len(simplexes)}")

    return (
        simplex_line,
        path_line,
        best_point,
        start_point,
        final_point,
        step_text,
        start_text,
        end_text,
        *history_lines
    )

ani = FuncAnimation(
    fig,
    update,
    frames=len(simplexes),
    interval=400,
    blit=False,
    repeat=False
)

plt.show()
