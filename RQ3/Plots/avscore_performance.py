import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.patches import Patch
import pathlib
import numpy


def absolute_value(val):
    a = numpy.round(val / 100.0 * 1765, 0)
    if a < 1:
        return ""
    return str(int(a))


parent = str(pathlib.Path(__file__).parent.resolve())
sumup = pd.read_csv("../Results/Dataset2/s1_s2_results+vahunt.csv")

labels = ["Negative", "AppVirtualization"]

engine_labels = [
    "virtualapp",
    "packed_virtualapp",
    "droidplugin",
    "packed_droidplugin",
    "excelliance",
    "packed_excelliance",
    "unknown",
    "packed_unknown",
    "negative",
    "packed_negative",
]
engine_hist = [0 for f in engine_labels]

mtrk_hist = [[0, 0] for f in engine_labels]
vah_hist = [[0, 0] for f in engine_labels]
vah_broken = 0


for i in range(len(sumup["Name"])):
    if (
        str(sumup["Framework"][i]).find("virtual") >= 0
        or str(sumup["Framework"][i]).find("droidplugin") >= 0
        or sumup["AVScore"][i] >= 3
        or str(sumup["VAHunt"][i]) == "VirtualApp"
        or str(sumup["VAHunt"][i]) == "Droidplugin"
    ):
        # print(i)
        j = 8
        if str(sumup["Framework"][i]).find("0") >= 0:
            j = 8
        elif str(sumup["Framework"][i]).find("virtual") >= 0:
            j = 0
        elif str(sumup["Framework"][i]).find("droidplugin") >= 0:
            j = 2
        elif str(sumup["Framework"][i]).find("excelliance") >= 0:
            j = 4
        else:
            j = 6
        if (
            str(sumup["Framework"][i]).find("packed") >= 0
            or str(sumup["Packed"][i]) == "1"
        ):
            j += 1
        engine_hist[j] += 1
        if sumup["AVScore"][i] >= 3:
            mtrk_hist[j][1] += 1
        else:
            mtrk_hist[j][0] += 1

        if (
            str(sumup["VAHunt"][i]) == "VirtualApp"
            or str(sumup["VAHunt"][i]) == "Droidplugin"
        ):
            vah_hist[j][1] += 1
        else:
            vah_hist[j][0] += 1
            if str(sumup["VAHunt"][i]).find("BROKEN") >= 0:
                vah_broken += 1

# PIES
w = 0.3
plt.rcParams["hatch.linewidth"] = 2.0
cA = [
    "b",
    "b",
    "gold",
    "gold",
    "cyan",
    "cyan",
    "magenta",
    "magenta",
    "grey",
    "grey",
]

p = plt.pie(
    engine_hist,
    colors=cA,
    wedgeprops=dict(width=w, edgecolor="black"),
    autopct=absolute_value,
    pctdistance=(1 - w / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)
for i in range(len(p[0])):
    if not i % 2 == 0:
        p[0][i].set(hatch="///")
plt.legend(
    handles=[
        Patch(color="b", label="VirtualApp"),
        Patch(color="gold", label="Droidplugin"),
        Patch(color="cyan", label="Excelliance"),
        Patch(color="magenta", label="Other Framework"),
        Patch(
            facecolor="magenta",
            edgecolor="black",
            hatch="///",
            label="Other Framework Packed",
        ),
        Patch(color="grey", label="FalsePositives"),
        Patch(
            facecolor="grey",
            edgecolor="black",
            hatch="///",
            label="FalsePositive Packed",
        ),
    ],
    loc="upper right",
)
plt.title("Frameworks")
plt.show()

cB = (
    ["r", "lime"]
    + ["r", "lime"]
    + ["r", "lime"]
    + ["r", "lime"]
    + ["r", "lime"]
    + ["r", "lime"]
    + ["r", "lime"]
    + ["r", "lime"]
    + ["lime", "r"]
    + ["lime", "r"]
)
vals = np.array(vah_hist)
plt.pie(
    vals.flatten(),
    radius=1,
    colors=cB,
    wedgeprops=dict(width=w, edgecolor="black"),
    pctdistance=(1 - w / 2),
)
p = plt.pie(
    vals.sum(axis=1),
    radius=1 - w,
    colors=cA,
    wedgeprops=dict(width=w, edgecolor="black"),
    autopct=absolute_value,
    pctdistance=(1 - w / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)
for i in range(len(p[0])):
    if not i % 2 == 0:
        p[0][i].set(hatch="///")
"""
plt.legend(
    handles=[
        Patch(color="lime", label="Correct"),
        Patch(color="r", label="Wrong"),
        Patch(color="b", label="VirtualApp"),
        Patch(color="gold", label="Droidplugin"),
        Patch(color="cyan", label="Excelliance"),
        Patch(color="magenta", label="Other Framework"),
        Patch(facecolor="w", edgecolor="black", hatch="///", label="Packed"),
        Patch(color="silver", label="FalsePositives"),
    ],
    loc="upper right",
)
"""
plt.title("VAHunt")
plt.show()


vals = np.array(mtrk_hist)
plt.pie(
    vals.flatten(),
    radius=1,
    colors=cB,
    wedgeprops=dict(width=w, edgecolor="black"),
    pctdistance=(1 - w / 2),
)
p = plt.pie(
    vals.sum(axis=1),
    radius=1 - w,
    colors=cA,
    wedgeprops=dict(width=w, edgecolor="black"),
    autopct=absolute_value,
    pctdistance=(1 - w / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)
for i in range(len(p[0])):
    if not i % 2 == 0:
        p[0][i].set(hatch="///")
"""
plt.legend(
    handles=[
        Patch(color="lime", label="Correct"),
        Patch(color="r", label="Wrong"),
        Patch(color="b", label="VirtualApp"),
        Patch(color="gold", label="Droidplugin"),
        Patch(color="cyan", label="Excelliance"),
        Patch(color="magenta", label="Other Framework"),
        Patch(facecolor="w", edgecolor="black", hatch="///", label="Packed"),
        Patch(color="silver", label="FalsePositives"),
    ],
    loc="upper right",
)
"""
plt.title("Matrioksa")
plt.show()

plt.legend(
    handles=[
        Patch(color="lime", label="Correct"),
        Patch(color="r", label="Wrong"),
        Patch(color="b", label="VirtualApp"),
        Patch(
            facecolor="b",
            edgecolor="black",
            hatch="///",
            label="VirtualApp Packed",
        ),
        Patch(color="gold", label="Droidplugin"),
        Patch(
            facecolor="gold",
            edgecolor="black",
            hatch="///",
            label="Droidplugin Packed",
        ),
        Patch(color="cyan", label="Excelliance"),
        Patch(
            facecolor="cyan",
            edgecolor="black",
            hatch="///",
            label="Excelliance Packed",
        ),
        Patch(color="magenta", label="Other Framework"),
        Patch(
            facecolor="magenta",
            edgecolor="black",
            hatch="///",
            label="Other Framework Packed",
        ),
        Patch(color="grey", label="FalsePositive"),
        Patch(
            facecolor="grey",
            edgecolor="black",
            hatch="///",
            label="FalsePositive Packed",
        ),
    ],
    loc="upper right",
)
plt.show()
