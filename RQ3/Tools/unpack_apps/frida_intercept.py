import frida
import sys
import time
import threading
import os
import subprocess as subp


def my_message_handler(message, payload):
    print(message, payload)


def on_spawned(spawn):
    print("On_spawned: ", spawn)
    event.set()


def spawn_added(spawn):
    event.set()


def spawn_removed(spawn):
    print("Spawn removed", spawn)
    event.set()


def on_message(spawn, message, data):
    print("on_message:", spawn, message, data)


def on_message(message, data):
    print(message)
    print(data)


def get_package_name(apk_path):
    """get the package name of an app"""
    p1 = subp.Popen(["aapt", "dump", "badging", apk_path], stdout=subp.PIPE)
    p2 = subp.Popen(
        ["grep", r"package:\ name"],
        stdin=p1.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p3 = subp.Popen(["cut", "-c", "15-"], stdin=p2.stdout, stdout=subp.PIPE)
    p4 = subp.Popen(["awk", "{print $1}"], stdin=p3.stdout, stdout=subp.PIPE)
    return p4.communicate()[0].decode("utf-8").strip()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} apk_dir")
        exit(1)

    apk_dir = sys.argv[1]

    if not os.path.exists("dumps"):
        os.mkdir("dumps")

    if not os.path.exists("analyzed.txt"):
        with open("analyzed.txt", "w") as _:
            pass

    analyzed = [f.strip() for f in open("analyzed.txt", "r").readlines()]

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

    for app in os.listdir(apk_dir):
        if app in analyzed:
            continue
        print(app)
        app_path = os.path.join(apk_dir, app)
        if not os.path.exists(app_path):
            continue
        subp.run(f"adb install -g {app_path}", shell=True)
        package_name = get_package_name(app_path).replace("'", "")
        print(package_name)
        time.sleep(5)
        try:
            pid = device.spawn([package_name])
            session = device.attach(pid)

            script = session.create_script(open("agent.js").read())
            script.on("message", on_message)
            script.load()

            device.resume(pid)

            time.sleep(20)

            dexdump_check = subp.Popen(
                "adb shell ls -l /sdcard/dexdump", shell=True, stdout=subp.PIPE
            )
            dexes = (
                dexdump_check.communicate()[0].decode("utf-8").strip().split()
            )

            print(dexes)

            if len(dexes) > 2:

                subp.run(
                    f"adb pull /sdcard/dexdump dumps/{package_name} && adb shell rm -rf /sdcard/dexdump/*",
                    shell=True,
                )
        except:
            pass

        subp.run(f"adb uninstall {package_name}", shell=True)

        with open("analyzed.txt", "a") as out_file:
            out_file.write(app + "\n")
