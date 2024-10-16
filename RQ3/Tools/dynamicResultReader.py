import pandas as pd
import sys


def getVer(cia, stub, ui, sp, aa, sa, pck):
    if cia == 1:
        return "SILENT"
    if sp > 0 and (aa > 0 or sa > 0):
        if (stub > 0 or ui > 0) or pck > 1:
            return "SILENT"
    return "NOT_SILENT"


if len(sys.argv) < 2:
    print("Usage: python3 dynamicResultReader.py <results/path>")
    sys.exit(1)

results_path = str(sys.argv[1])
data = pd.read_csv(results_path)

features = ["Name", "AVScore", "SameCertificates", "Verdict"]

verdict_path = results_path + "_verdict.csv"
verdict = open(verdict_path, "w")
verdict.write(features[0])
for f in features[1:]:
    verdict.write(",")
    verdict.write(f)
verdict.write("\n")
verdict.close()

for i in range(len(data["Name"])):
    v = {0: f for f in features}

    v["Name"] = data["Name"][i]
    v["AVScore"] = data["VAScore"][i]
    v["SameCertificates"] = "No"
    if data["SameCertificates"][i] == 1:
        v["SameCertificates"] = "Yes"
    s = ""
    if data["Error"][i] != 0:
        s = data["Error"][i]
    else:
        s = getVer(
            data["CombinedIntentAnalysis"][i],
            data["StubIntent"][i],
            data["UnknownIntent"][i],
            data["SpawnProcesses"][i],
            data["AssetsApk"][i],
            data["SpawnApk"][i],
            data["#Packages"][i],
        )
    v["Verdict"] = s

    verdict = open(verdict_path, "a")
    verdict.write(str(v[features[0]]))
    for f in features[1:]:
        verdict.write(",")
        verdict.write(str(v[f]))
    verdict.write("\n")
    verdict.close()
