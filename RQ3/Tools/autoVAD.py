import os
import sys
import glob
import time
import subprocess

if len(sys.argv) < 2:
    print(
        "Usage: python3 autoVAD.py <samples/directory> <mod=[7,7arm,9,9arm]>"
    )
    sys.exit(1)

samples_dir = str(sys.argv[1])
mod = "7"
if len(sys.argv) > 2 and (
    str(sys.argv[2]).find("7") >= 0 or str(sys.argv[2]).find("9") >= 0
):
    mod = str(sys.argv[2])

features = [
    "Name",
    "Error",
    "VAScore",
    "AssetsApk",
    "StubComponents",
    "StubProcesses",
    "#Permissions",
    "#Activities",
    "#Components",
    "#Processes",
    "SpawnApk",
    "SpawnProcesses",
    "SameCertificates",
    "StubIntent",
    "#Packages",
    "UnknownIntent",
    "CombinedIntentAnalysis",
]

filename = "matrioska" + mod + "_results.csv"
if not os.path.exists(filename):
    # csv head
    results = open(filename, "w")
    results.write(features[0])
    for f in features[1:]:
        results.write(",")
        results.write(f)
    results.write("\n")
    results.close()


apk_list = glob.glob(samples_dir + "/**/*.apk", recursive=True)[0:]
progress = 0
for apk in apk_list:
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

    # load apk
    proc = subprocess.Popen(["adb", "logcat", "-c"])
    proc.wait()
    proc = subprocess.Popen(["adb", "push", apk, "/sdcard/" + apk_name])
    proc.wait()

    # install matrioska
    if mod.find("9") >= 0:
        if mod.find("arm") >= 0:
            proc = subprocess.Popen(
                [
                    "adb",
                    "install",
                    "-g",
                    "--abi",
                    "armeabi-v7a",
                    "Matrioska9auto.apk",
                ]
            )
        else:
            proc = subprocess.Popen(
                ["adb", "install", "-g", "Matrioska9auto.apk"]
            )
    else:
        if mod.find("arm") >= 0:
            proc = subprocess.Popen(
                [
                    "adb",
                    "install",
                    "-g",
                    "--abi",
                    "armeabi-v7a",
                    "Matrioska7auto.apk",
                ]
            )
        else:
            proc = subprocess.Popen(
                ["adb", "install", "-g", "Matrioska7auto.apk"]
            )
    proc.wait()

    # execute matrioska
    if mod.find("9") >= 0:
        os.system(
            "adb shell am start -n com.example.autovadetector/com.example.autovadetector.MainActivity"
        )
    else:
        os.system(
            "adb shell am start -n com.example.dynamicVAdetector/com.example.dynamicVAdetector.MainActivity"
        )

    # check if plugin is running
    time.sleep(10)
    if mod.find("7") >= 0:
        os.system(
            "adb shell input tap 280 1680"
        )  # remove the malware dialog (specific fot Huawei P9 Lite)
    time.sleep(15)
    proc = subprocess.Popen(
        ["adb", "shell", "dumpsys", "window"], stdout=subprocess.PIPE
    )
    grep = subprocess.Popen(
        ["grep", "mCurrentFocus"], stdin=proc.stdout, stdout=subprocess.PIPE
    )
    for line in grep.stdout:
        print(line)
        result["Error"] = 0
        if str(line).split("=")[1].split("\\")[0] == "null":
            result["Error"] = "crashed"
        if str(line).find("com.example.autovadetector") >= 0:
            result["Error"] = "crashed"
        if str(line).find("com.example.dynamicVAdetector") >= 0:
            result["Error"] = "crashed"
        if str(line).find("com.google.android.apps.nexuslauncher") >= 0:
            result["Error"] = "crashed"
        if str(line).find("com.huawei.android.launcher") >= 0:
            result["Error"] = "crashed"
    proc.wait()
    grep.wait()

    # wait for response
    proc = subprocess.Popen(["adb", "logcat"], stdout=subprocess.PIPE)
    T = time.time()
    for line in proc.stdout:
        for f in features[2:]:
            if (f in str(line)) and ("VADetector" in str(line)):
                print("Line: " + str(line))
                value = str(line).split(f + ": ")[1].split("\\")[0]
                result[f] = int(value)
                # result['Error'] = 0
        if "Please Wait" in str(line):
            T = time.time()
        if "Error: Cannot install plugin!" in str(line):
            print(line)
            result["Error"] = "cannot_install"
        if (apk_name in str(line)) and ("COMPLETED!" in str(line)):
            proc.kill()
            break
        if time.time() - T > 40:
            result["Error"] = "timeout"
            proc.kill()
            break
    proc.wait()
    time.sleep(1)

    # remove apk
    proc = subprocess.Popen(["adb", "shell", "rm", "/sdcard/" + apk_name])
    # proc = subprocess.Popen(
    #    ["adb", "shell", "rm", "-r", "sdcard/*", "sdcard/.*"]
    # )
    proc.wait()
    # uninstall matrioska
    if mod.find("9") >= 0:
        proc = subprocess.Popen(
            ["adb", "uninstall", "com.example.autovadetector"]
        )
    else:
        proc = subprocess.Popen(
            ["adb", "uninstall", "com.example.dynamicVAdetector"]
        )
    proc.wait()

    os.system("adb shell media volume --stream 3 --set 0")

    if (result["#Packages"] == 0) and (
        result["#Permissions"] < 0 or result["#Activities"] < 0
    ):
        result["Error"] = "not_started"

    # SCORE
    score = 0
    if result["#Processes"] > 0:
        if result["AssetsApk"] > 0:  # threshold
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

    print("[**] ERROR: " + str(result["Error"]))
    # write result
    results = open(filename, "a")
    results.write(str(result[features[0]]))
    for f in features[1:]:
        results.write(",")
        results.write(str(result[f]))
    results.write("\n")
    results.close()
