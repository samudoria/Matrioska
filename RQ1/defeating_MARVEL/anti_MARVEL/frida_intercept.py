import frida
import sys
import time
import threading
import os
import subprocess as subp
import re

package_name = "com.example.trustedcontainervirtualapp"
dex_counter = 0
package_dex = ""
id_dex = {}
hexPattern = re.compile(r"\s--([0-9a-fA-F]+)(?:--)?\s")


def my_message_handler(message, payload):
    print(message, payload)


def on_spawned(spawn):
    print("On_spawned: ", spawn)
    event.set()


def spawn_added(spawn):
    event.set()
    if spawn.identifier.startswith(package_name):
        session = device.attach(spawn.pid)
        script = session.create_script(open("anti_MARVEL/dump_dex.js").read())
        script.on("message", on_message)
        script.load()
        device.resume(spawn.pid)


def spawn_removed(spawn):
    print("Spawn removed", spawn)
    event.set()


def on_message(spawn, message, data):
    print("on_message:", spawn, message, data)


def on_message(message, data):
    if message["type"] == "send":
        comm = message["payload"]
        key = comm.split()[3].split(".")[0]
        value = comm.split("->")[1]
        if "$" in value:
            value = value.replace("$", "")
        global id_dex
        print(f"[+] Found hook to {value} with id {key}")
        id_dex[key] = value
    else:
        print(message)


def disassemble(apk_file):
    subp.call(f"apktool d -f {apk_file} -o disass", shell=True)


def inject_jic():
    for root, dirs, files in os.walk("./disass"):
        for file_name in files:
            if file_name == "JavaIntegrityChecks.smali":
                target_file = os.path.join(root, file_name)
                subp.call(
                    f"cp anti_MARVEL/JavaIntegrityChecks.smali {target_file}",
                    shell=True,
                )
                print("[+] Modified JavaIntegrityChecks.smali injected")
                break


def baksmali_dex():
    if not os.path.exists("./dex_extracted"):
        os.mkdir("./dex_extracted")
    root = "./dex_extracted/dump"
    if not os.path.exists(root):
        os.mkdir(root)
    for file_name in os.listdir(root):
        target_file = os.path.join(root, file_name)
        if target_file.split(".")[1] == "dex":
            subp.call(
                f"./anti_MARVEL/baksmali d {target_file} -o {root}/{file_name}-baksmali",
                shell=True,
                stderr=subp.DEVNULL,
            )
            print(f"[+] Extracted smali from {file_name}")
            new_search = os.path.join(root, f"{file_name}-baksmali")
            if not os.path.exists(new_search):
                print("[-] DEX is empty")
                subp.call(f"rm {target_file}", shell=True)
                continue
            complete = False
            orig_new_search = new_search
            while not complete:
                for file_name in os.listdir(new_search):
                    signature = file_name.split(".")[0]
                    if signature in id_dex:
                        subp.call(
                            f"mv {new_search}/{file_name} {root}/{id_dex[signature]}.smali",
                            shell=True,
                        )
                        complete = True
                    else:
                        if file_name[len(file_name) - 6 :] == ".smali":
                            global package_dex
                            package_path = package_dex.replace(".", "/") + "/"
                            for other_file_name in os.listdir(
                                f"./disass/smali/{package_path}"
                            ):
                                component_name = other_file_name.split(".")[0]
                                if component_name in signature:
                                    class_name = component_name
                                    method = signature.split(component_name)[1]
                                    subp.call(
                                        f"mv {new_search}/{file_name} {root}/{package_dex}.{class_name}-{method}.smali",
                                        shell=True,
                                    )
                                    complete = True
                        else:
                            new_search = os.path.join(new_search, file_name)
                            break
            subp.call(f"rm -rf {orig_new_search}", shell=True)
            subp.call(f"rm {target_file}", shell=True)


def replace_method_body():
    dexes = "./dex_extracted/dump"
    for file_name in os.listdir(dexes):
        class_name = file_name.split("-")[0].split(".")[-1]
        method = file_name.split("-")[1].split(".")[0]
        if "access" in method:
            method = method.replace("access", "access$")
            if len(method) < 10:
                method = method + "0"
        method_body = []
        start_copy = False
        file_path = os.path.join(dexes, file_name)
        with open(file_path, "r") as smali_file:
            lines = smali_file.readlines()
            for i in range(len(lines)):
                if lines[i].startswith(".method public static hook"):
                    header = [lines[i + 1]]
                    method_body += lines[i + 2 : len(lines) - 1]
                    method_body = header + method_body
                    parameters = lines[i].split("(")[1].split(")")[0]
        for line in method_body:
            if "JavaIntegrityChecks" in line:
                method_body.remove(line)
        for root, dirs, files in os.walk("./disass"):
            for other_file_name in files:
                if class_name in other_file_name:
                    other_file_path = os.path.join(root, other_file_name)
                    new_class_file = []
                    with open(other_file_path, "r") as other_smali_file:
                        lines = other_smali_file.readlines()
                        i = 0
                        while i < len(lines):
                            if lines[i].startswith(".method"):
                                splits_method = lines[i].split()
                                method_name = ""
                                for split_method in splits_method:
                                    if "(" in split_method:
                                        method_name = split_method.split("(")[
                                            0
                                        ]
                                        parameters_original = (
                                            lines[i]
                                            .split("(")[1]
                                            .split(")")[0]
                                        )
                                if (
                                    method_name in method
                                    and parameters_original in parameters
                                ):
                                    new_class_file.append(lines[i])
                                    start_copy = True
                                    new_class_file = (
                                        new_class_file + method_body
                                    )
                                    print(
                                        f"[*] {class_name} {method} replaced"
                                    )
                            if start_copy and lines[i].startswith(
                                ".end method"
                            ):
                                start_copy = False
                            if not start_copy:
                                new_class_file.append(lines[i])
                            i += 1
                    with open(other_file_path, "w") as other_smali_file:
                        for line in new_class_file:
                            other_smali_file.write(line)


def check_current_activity():
    get_current_focus = subp.Popen(
        "adb shell dumpsys window | grep mCurrentFocus",
        shell=True,
        stdout=subp.PIPE,
    )
    communicate = get_current_focus.communicate()
    current_focus = ""
    if len(communicate) > 0:
        decoded = communicate[0].decode().strip()
        if "/" in decoded:
            current_focus = decoded.split("/")[1].replace("}", "")
    global package_dex
    if package_dex in current_focus:
        print(f"[+] CURRENT FOCUS {current_focus}")
        return True
    return False


def rebuild_apk(apk_name):
    subp.call("apktool b -d -f disass/", shell=True)
    subp.call(
        f"mv disass/dist/{apk_name} ./rebuilt_{apk_name}",
        shell=True,
    )
    subp.call(
        f"echo 'asd123' | jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore anti_MARVEL/my-release-key.keystore ./rebuilt_{apk_name} alias_name",
        shell=True,
    )
    time.sleep(1)
    subp.call(f"adb install -r ./rebuilt_{apk_name}", shell=True)
    print("[+] STANDALONE PLUGIN INSTALLED")


def get_package_name(apk_path):
    """get the package name of an app"""
    p1 = subp.Popen(
        ["./anti_MARVEL/aapt", "dump", "badging", apk_path], stdout=subp.PIPE
    )
    p2 = subp.Popen(
        ["grep", r"package:\ name"],
        stdin=p1.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p3 = subp.Popen(["cut", "-c", "15-"], stdin=p2.stdout, stdout=subp.PIPE)
    p4 = subp.Popen(["awk", "{print $1}"], stdin=p3.stdout, stdout=subp.PIPE)
    return p4.communicate()[0].decode().strip()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} protected_apk")
        exit(1)

    apk_file = sys.argv[1]

    old_apk = apk_name = apk_file.split("/")[-1]

    event = threading.Event()
    device = frida.get_usb_device()
    device.on("spawn-added", spawn_added)
    device.on("spawn-removed", spawn_removed)
    device.on("child-added", on_spawned)
    device.on("child-removed", on_spawned)
    device.on("process-crashed", on_spawned)
    device.on("output", on_spawned)
    device.on("uninjected", on_spawned)
    device.on("lost", on_spawned)
    device.enable_spawn_gating()

    complete = False

    while not complete:
        pid = device.spawn([package_name])
        session = device.attach(pid)

        script = session.create_script(open("anti_MARVEL/dump_dex.js").read())
        script.on("message", on_message)
        script.load()

        device.resume(pid)
        print("[*] PRESS CTRL+C TO STOP THE DUMPING PROCESS")
        try:
            sys.stdin.read()
        except KeyboardInterrupt:
            subp.call(
                "adb shell su -c 'cp /data/user/0/com.example.trustedcontainervirtualapp/files/dump/* /sdcard/dump'",
                shell=True,
            )
            subp.call("adb pull /sdcard/dump/ ./dex_extracted", shell=True)
            pass

        dex_counter = len(os.listdir("./dex_extracted/dump"))

        print(f"[+] Found {dex_counter} dex files")

        disassemble(apk_file)

        package_dex = get_package_name(apk_file).replace("'", "")

        inject_jic()

        baksmali_dex()

        replace_method_body()

        rebuild_apk(apk_name)

        time.sleep(2)

        print("[+] STANDALONE PLUGIN LAUNCHED")
        device.spawn([package_dex])

        print(
            "[*] PRESS CTRL+C IF THE REBUILT APP IS STILL NOT ENTIRELY WORKING"
        )
        try:
            sys.stdin.read()
        except KeyboardInterrupt:
            pass

        if not check_current_activity():
            print("[-] NOT RUNNING")

            old_apk = apk_name
            apk_name = "rebuilt_" + apk_name
            subp.call(f"adb push {apk_file} /sdcard/plugin.apk", shell=True)
            print("[+] Restarting VirtualApp with new plugin")
        else:
            print()
            complete = True
