import os
import sys
import glob
import time
from difflib import SequenceMatcher

if len(sys.argv) < 2:
    print(
        "Usage: python3 staticVAdetector.py <samples/directory> <optional/output/directory>"
    )
    sys.exit(1)

samples_dir = str(sys.argv[1])
output_dir = str(sys.argv[1])
if len(sys.argv) > 2:
    output_dir = str(sys.argv[2])

features = [
    "Name",
    "VAScore",
    "AssetsAPK",
    "StubComponents",
    "StubProcesses",
    "#Permissions",
    "#Activities",
    "#Components",
    "#Processes",
    "Framework",
]

if not os.path.exists(output_dir):
    os.mkdir(output_dir)

# csv head
results = open(output_dir + "/results.csv", "w")
results.write(features[0])
for f in features[1:]:
    results.write(",")
    results.write(f)
results.write("\n")

counter = 0

t1 = time.time()
apk_list = glob.glob(samples_dir + "/**/*.apk", recursive=True)
progress = 0
for apk in apk_list:
    t3 = time.time()
    splt = apk.split("/")
    apk_name = splt[len(splt) - 1]
    progress += 1
    print(
        "["
        + str(progress)
        + "/"
        + str(len(apk_list))
        + "] Analyzing "
        + apk_name
    )

    result = {f: 0 for f in features}
    result["Name"] = apk_name

    # look for apks into assets
    os.system(
        "unzip -qq -o -P '' "
        + apk
        + " assets/* -d "
        + output_dir
        + "/tmpassets > /dev/null"
    )
    if os.path.exists(output_dir + "/tmpassets"):
        for a in glob.glob(output_dir + "/tmpassets/**", recursive=True):
            if not os.path.isdir(a):
                f = open(a, "rb")
                if f.read(4) == b"\x50\x4b\x03\x04":
                    # if is a zip look for manifest
                    os.system(
                        "unzip -qq -o -P '' "
                        + a
                        + " -d "
                        + output_dir
                        + "/tmpfile > /dev/null"
                    )
                    if os.path.exists(output_dir + "/tmpfile"):
                        if "AndroidManifest.xml" in os.listdir(
                            output_dir + "/tmpfile"
                        ):
                            result["AssetsAPK"] += 1
                        # shutil.rmtree(output_dir+'/tmpfile')
                        os.system("sudo rm -r " + output_dir + "/tmpfile")
                f.close()
        # shutil.rmtree(output_dir+'/tmpassets')
        os.system("sudo rm -r " + output_dir + "/tmpassets")

    # extract manifest
    os.system(
        "aapt dump xmltree "
        + apk
        + " AndroidManifest.xml > "
        + output_dir
        + "/temp.txt"
    )
    manifest = open(output_dir + "/temp.txt", "r")

    plist = []
    for line in manifest:
        # #Permissions
        if line.find("uses-permission") > -1:
            result["#Permissions"] += 1
        # #Components
        if line.find("E: activity") > -1:
            result["#Activities"] += 1
            result["#Components"] += 1
        if line.find("E: service") > -1:
            result["#Components"] += 1
        if line.find("E: receiver") > -1:
            result["#Components"] += 1
        # #Processes
        if line.find("android:process") > -1:
            pname = line.split("=")[1].split(" (Raw:")[0]
            if pname not in plist:
                result["#Processes"] += 1
                plist.append(pname)
        # frameworks
        if line.lower().find("qihoo.replugin") > -1:
            result["Framework"] = "qihoo.replugin"
        if line.lower().find("lody.virtual") > -1:
            result["Framework"] = "lody.virtual"
        if line.lower().find("morgoo.droidplugin") > -1:
            result["Framework"] = "morgoo.droidplugin"
        if line.lower().find("com.excelliance") > -1:
            result["Framework"] = "com.excelliance"
    manifest.close()

    # Collect components
    manifest = open(output_dir + "/temp.txt", "r")
    components_list = []
    line = manifest.readline()
    while True:
        if not line:
            break
        if (
            (line.find("E: activity") > -1)
            or (line.find("E: service") > -1)
            or (line.find("E: receiver") > -1)
        ):
            cname = ""
            pname = ""
            while True:
                line = manifest.readline()
                if (not line) or (line.find("E: ") > -1):
                    break
                if line.find("android:name") > -1:
                    cname = line.split("=")[1].split(" (Raw:")[0]
                if line.find("android:process") > -1:
                    pname = line.split("=")[1].split(" (Raw:")[0]
            components_list.append((cname, pname))
        line = manifest.readline()

    manifest.close()
    os.remove(output_dir + "/temp.txt")

    # Stub Analysis
    Ta = 0.9  # alike Threshold
    Ts = 5  # stub Threshold
    plist = []
    for component in components_list:
        alike = -1
        n = component[0]
        for c in components_list:
            _, _, size = SequenceMatcher(None, n, c[0]).find_longest_match(
                0, len(n), 0, len(c[0])
            )
            if len(n) > 0 and (size / len(n)) > Ta:
                alike += 1
        if alike > Ts:  # if component is a stub component
            if component[1] != "":
                result["StubComponents"] += 1
                if not component[1] in plist:
                    result["StubProcesses"] += 1
                    plist.append(component[1])

    # SCORE
    score = 0
    if result["#Processes"] > 0:
        if result["AssetsAPK"] > 0:  # threshold
            score += 2  # weigth
        if result["StubComponents"] > 10:
            score += 1
            if result["StubProcesses"] > 0:
                score += 2
        if result["#Permissions"] > 100:
            score += 0
        if result["#Activities"] > 50:
            score += 0
        if result["#Components"] > 80:
            score += 0
        if result["#Processes"] > 8:
            score += 1
    result["VAScore"] = score
    # VA detected if score >= 3
    if score >= 3:
        counter += 1

    # write result
    results.write(str(result[features[0]]))
    for f in features[1:]:
        results.write(",")
        results.write(str(result[f]))
    results.write("\n")
    t4 = time.time()
    print("Done in " + str(round(t4 - t3, 3)) + "sec")

results.close()
t2 = time.time()
print(
    "\n\nAll samples analyzed in "
    + str(round(t2 - t1, 3))
    + "sec\nAverage: "
    + str(round((t2 - t1) / len(apk_list), 3))
    + "sec"
)
print("Total AVSample: " + str(counter))
