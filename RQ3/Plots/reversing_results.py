import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.patches import Patch
import pathlib

parent = str(pathlib.Path(__file__).parent.resolve())
data = pd.read_csv("../Results/Dataset2/malware_reverseLabel.csv")


# mtr_labels = [['start-silent','FalsePositive','packed'],['start-silent','negative','Error']]
mtr_hist = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]

# vah_labels = [['silent','FalsePositive','packed'],['silent','not-silent','packed']]
vah_hist = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]


def absolute_value(val):
    a = numpy.round(val / 100.0 * 190, 0)
    if a < 1:
        return ""
    return str(int(a))


def _second_label(label):
    jm = 0
    if label.find("FalsePositive") >= 0:
        jm = 1
    elif label.find("start-silent") >= 0 or label.find("0f3b") >= 0:
        jm = 0
    elif label.find("silent") >= 0:
        jm = 1
    return jm


for i in range(len(data["Name"])):
    jm = -1
    jv = -1
    label = str(data["ReverseLabel"][i])
    if label.find("packed") >= 0:
        second_label = _second_label(label)
        jm = 2 + second_label
        jv = 2 + second_label
    elif label.find("FalsePositive") >= 0:
        jm = 1
        jv = 1
    elif label == "start-silent" or label == "0f3b":
        jm = 0
        jv = 0
    elif label == "silent":
        jm = 1
        jv = 1
    if jm >= 0:
        mtr = str(data["Matrioska"][i])
        if mtr == "SILENT":
            mtr_hist[jm][0] += 1
        elif mtr == "NOT_SILENT":
            mtr_hist[jm][1] += 1
        else:
            mtr_hist[jm][2] += 1
    if jv >= 0:
        vah = str(data["VAHunt"][i])
        if vah == "SILENT":
            vah_hist[jv][0] += 1
        elif vah == "NOT_SILENT":
            vah_hist[jv][1] += 1
        elif vah == "PACKED_APK":
            vah_hist[jv][2] += 1


size = 0.3
vals = np.array(mtr_hist)
print("Matrioska")
print(vals)
p = plt.pie(
    vals.flatten(),
    radius=1,
    colors=["lime", "r", "black"]
    + ["r", "lime", "black"]
    + ["lime", "r", "black"]
    + ["r", "lime", "black"],
    wedgeprops=dict(width=size, edgecolor="black"),
    autopct=lambda p: (
        "{:.0f}".format(p * sum(vals.flatten()) / 100)
        if int("{:.0f}".format(p * sum(vals.flatten()) / 100)) >= 1
        else ""
    ),
    pctdistance=(1 - size / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)
p = plt.pie(
    vals.sum(axis=1),
    radius=1 - size,
    colors=["b", "magenta", "b", "magenta"],
    wedgeprops=dict(width=size, edgecolor="black"),
    autopct=lambda p: (
        "{:.0f}".format(p * sum(vals.flatten()) / 100)
        if int("{:.0f}".format(p * sum(vals.flatten()) / 100)) >= 1
        else ""
    ),
    pctdistance=(1 - size / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)
for i in range(len(p[0])):
    if i == 2 or i == 3:
        p[0][i].set(hatch="///")
"""
plt.legend(
    handles=[
        Patch(color="lime", label="Correct"),
        Patch(color="r", label="Wrong"),
        Patch(color="black", label="Failed"),
        Patch(color="b", label="Silent"),
        Patch(color="magenta", label="NotSilent"),
        Patch(color="silver", label="Packed"),
    ]
)
"""
plt.title("Matrioska")
plt.show()
"""
plt.legend(
    handles=[
        Patch(color="r", label="StartupSilent"),
        Patch(color="b", label="FalsePositive"),
        Patch(color="silver", label="Packed"),
        Patch(color="m", label="Matrioska:StartupSilent"),
        Patch(color="c", label="Matrioska:Negative"),
        Patch(color="black", label="Matrioska:Error"),
    ]
)

plt.show()
"""

vals = np.array(vah_hist)
print("VAHunt")
print(vals)
p = plt.pie(
    vals.flatten(),
    radius=1,
    colors=["lime", "r", "black"]
    + ["r", "lime", "black"]
    + ["lime", "r", "black"]
    + ["r", "lime", "black"],
    wedgeprops=dict(width=size, edgecolor="black"),
    autopct=lambda p: (
        "{:.0f}".format(p * sum(vals.flatten()) / 100)
        if int("{:.0f}".format(p * sum(vals.flatten()) / 100)) >= 1
        else ""
    ),
    pctdistance=(1 - size / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)


p = plt.pie(
    vals.sum(axis=1),
    radius=1 - size,
    colors=["b", "magenta", "b", "magenta"],
    wedgeprops=dict(width=size, edgecolor="black"),
    autopct=lambda p: (
        "{:.0f}".format(p * sum(vals.flatten()) / 100)
        if int("{:.0f}".format(p * sum(vals.flatten()) / 100)) >= 1
        else ""
    ),
    pctdistance=(1 - size / 1.5),
    textprops={
        "color": "black",
        "size": 15,
        "weight": "heavy",
        "bbox": dict(boxstyle="round", facecolor="white", alpha=1),
    },
)
for i in range(len(p[0])):
    if i == 2 or i == 3:
        p[0][i].set(hatch="///")
"""
plt.legend(
    handles=[
        Patch(color="lime", label="Correct"),
        Patch(color="r", label="Wrong"),
        Patch(color="black", label="Failed"),
        Patch(color="b", label="Silent"),
        Patch(color="magenta", label="NotSilent"),
        Patch(color="silver", label="Packed"),
    ]
)
plt.title("VAHunt")
"""
plt.show()
"""
plt.legend(
    handles=[
        Patch(color="r", label="Silent"),
        Patch(color="b", label="FalsePositive"),
        Patch(color="silver", label="Packed"),
        Patch(color="m", label="VAHunt:Silent"),
        Patch(color="c", label="VAHunt:NotSilent"),
        Patch(color="silver", label="VAHunt:Packed"),
    ]
)

plt.show()
"""
plt.legend(
    handles=[
        Patch(color="lime", label="Correct"),
        Patch(color="r", label="Wrong"),
        Patch(color="black", label="Failed"),
        Patch(color="b", label="Silent"),
        Patch(color="magenta", label="NotSilent"),
        Patch(
            facecolor="b",
            edgecolor="black",
            label="Silent & Packed",
            hatch="///",
        ),
        Patch(
            facecolor="magenta",
            edgecolor="black",
            label="NotSilent & Packed",
            hatch="///",
        ),
    ]
)
plt.title("VAHunt")
plt.show()
