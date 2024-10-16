import argparse
import os
import subprocess as subp
import sys
import time

from com.dtmilano.android.viewclient import ViewClient
from com.dtmilano.android.viewclient import UiScrollable

res_dir = "res-container-launch"
app_dir = "./apks"

APIKEY = "<api_key>"
timeout = 60


def download_app(sha256):
    if os.path.exists(os.path.join(app_dir, sha256 + ".apk")):
        return
    if not os.path.exists(app_dir):
        os.mkdir(app_dir)
    cmd = f"curl -O --remote-header-name -G -d apikey={APIKEY} -d sha256={sha256} https://androzoo.uni.lu/api/download && mv {sha256}.apk {app_dir}/"
    subp.run(
        cmd,
        shell=True,
        stdout=subp.DEVNULL,
        stderr=subp.DEVNULL,
    )


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "apk_list", help="list of sha256 corresponding to androzoo apks"
    )
    parser.add_argument("apk_dir", help="directory with apps to analyze")
    args = parser.parse_args()
    if not args.apk_list and not args.apk_dir:
        sys.exit(1)
    return args


# get serial number of device
def get_serialno():
    return subp.check_output(["adb", "get-serialno"]).decode("utf-8").strip()


# get the package name of a plugin
def get_package_name(plugin_path):
    p1 = subp.Popen(
        ["aapt", "dump", "badging", plugin_path],
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p2 = subp.Popen(
        ["grep", r"package:\ name"],
        stdin=p1.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p3 = subp.Popen(
        ["cut", "-c", "15-"],
        stdin=p2.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p4 = subp.Popen(
        ["awk", "{print $1}"],
        stdin=p3.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    return p4.communicate()[0].decode("utf-8").strip()


# get the main activity of a plugin
def get_main_activity(plugin_path):
    p1 = subp.Popen(
        ["aapt", "dump", "badging", plugin_path],
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p2 = subp.Popen(
        ["grep", "launchable-activity"],
        stdin=p1.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p3 = subp.Popen(
        ["awk", "{print $2}"],
        stdin=p2.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    p4 = subp.Popen(
        ["cut", "-c6-"], stdin=p3.stdout, stdout=subp.PIPE, stderr=subp.DEVNULL
    )
    return p4.communicate()[0].decode("utf-8").strip()


# get the application name of a plugin
def get_application_name(plugin_path):
    p1 = subp.Popen(["aapt", "dump", "badging", plugin_path], stdout=subp.PIPE)
    p2 = subp.Popen(
        ["sed", "-n", r"s/^application-label-en:'\(.*\)'/\1/p"],
        stdin=p1.stdout,
        stdout=subp.PIPE,
        stderr=subp.DEVNULL,
    )
    out = p2.communicate()[0].decode("utf-8").rstrip().replace("\\n", " ")
    if len(out) == 0:
        p1 = subp.Popen(
            ["aapt", "dump", "badging", plugin_path],
            stdout=subp.PIPE,
            stderr=subp.DEVNULL,
        )
        p2 = subp.Popen(
            ["sed", "-n", r"s/^application-label-en-GB:'\(.*\)'/\1/p"],
            stdin=p1.stdout,
            stdout=subp.PIPE,
            stderr=subp.DEVNULL,
        )
        out2 = p2.communicate()[0].decode("utf-8").strip().replace("\\n", " ")
        if len(out2) != 0:
            return out2
    if len(out) == 0:
        p1 = subp.Popen(
            ["aapt", "dump", "badging", plugin_path], stdout=subp.PIPE
        )
        p2 = subp.Popen(
            ["sed", "-n", r"s/^application-label:'\(.*\)'/\1/p"],
            stdin=p1.stdout,
            stdout=subp.PIPE,
            stderr=subp.DEVNULL,
        )
        return p2.communicate()[0].decode("utf-8").strip().replace("\\n", " ")
    return out


# gets the container component by combining the container package and container activity
def get_container_component(container_apk):
    container_package = get_package_name(container_apk)
    container_activity = get_main_activity(container_apk)
    return container_package + "/" + container_activity


def wait_until(delegate, timeout: int):
    end = time.time() + timeout
    while time.time() < end:
        if delegate():
            return True
        else:
            time.sleep(0.1)
    return False


def write_result(content, plugin):
    flags = "ab"
    if not os.path.exists(res_dir + "/" + plugin + ".log"):
        flags = "wb"
    f = open(res_dir + "/" + plugin + ".log", flags)
    f.write(content)
    f.close()


def force_dump(vc):
    while True:
        try:
            vc.dump(-1, sleep=0)
            break
        except Exception as e:
            pass


def native_execution(vc, device, plugin_path):

    package_name = get_package_name(plugin_path)

    main_component_plugin = get_container_component(plugin_path)

    out = b""

    if (
        "/" in main_component_plugin
        and not main_component_plugin.split("/")[1].strip()
    ):
        out += b"no main activity - could not test outside of container\n"
        print("no main activity")
        return out

    try:
        device.startActivity(component=get_container_component(plugin_path))
    except:
        print("failure")
        out += subp.check_output(
            "adb logcat -d", shell=True, stderr=subp.DEVNULL
        )
        out += b"\n"
        return out

    check_process = (
        lambda: subp.call(
            "adb logcat -d | grep --line-buffered {container_pkg} | grep Displayed".format(
                container_pkg=package_name
            ),
            stdout=subp.DEVNULL,
            shell=True,
        )
        == 0
    )
    if not wait_until(check_process, timeout):
        print("failure")
        out += subp.check_output(
            "adb logcat -d", shell=True, stderr=subp.DEVNULL
        )
        out += b"\n"
    else:
        print("success")
        out += b"success - works outside of container\n"
    return out


def container_execution(vc, device, container_apk, container_pkg, plugin_path):
    out = b""
    device.startActivity(component=get_container_component(container_apk))
    print("started container main activity")

    force_dump(vc)
    vc.sleep(5)

    # navigate to the application list
    vc.findViewByIdOrRaise("com.excelliance.multiaccount:id/add_but").touch()
    vc.sleep(1)
    print("navigated to application list")

    # generate ViewClient and find scrollable list view
    force_dump(vc)

    recycler_view = vc.findViewByIdOrRaise(
        "com.excelliance.multiaccount:id/add_game_rv"
    )
    scroll_view = UiScrollable(recycler_view)
    scroll_view.vc = vc

    # clone application
    application_name = get_application_name(plugin_path)
    print(application_name)
    vc.sleep(1)

    # manual scrolling
    tries = 2
    scrolls = 0
    while True:
        force_dump(vc)
        if vc.findViewWithText(application_name):
            vc.findViewWithText(application_name).touch()
            break
        if scrolls > 5:
            scroll_view.flingToBeginning(4)
            scrolls = 0
            tries -= 1
            if tries == 0:
                break
        else:
            scroll_view.flingForward()
            scrolls += 1

    # app not found in scrolling list
    if tries == 0 or not application_name.strip():
        out += b"app not found in list - diy\n"
        subp.call(
            "adb shell am force-stop com.excelliance.multiaccount",
            shell=True,
            stderr=subp.DEVNULL,
        )
        vc.sleep(1)
        return out
    else:
        print("cloned app")

        vc.sleep(4)

        # clear adb logs before launching
        subp.call(["adb", "logcat", "-c"])

        # launch cloned app
        force_dump(vc)
        print("launching cloned app")
        while not vc.findViewWithText(application_name):
            force_dump(vc)

        vc.findViewWithTextOrRaise(application_name).touch()

        # check cloned application launch
        check_process = (
            lambda: subp.call(
                "adb logcat -d | grep --line-buffered {container_pkg} | grep Displayed".format(
                    container_pkg=container_pkg
                ),
                stdout=subp.DEVNULL,
                shell=True,
            )
            == 0
        )
        if not wait_until(check_process, timeout):
            print("failure")
            out += subp.check_output("adb logcat -d", shell=True)
            out += b"\n"
        else:
            print("success")
            out += b"success\n"

        vc.sleep(1)
        # close cloned app
        subp.call(
            "adb shell am force-stop com.excelliance.multiaccount",
            shell=True,
            stderr=subp.DEVNULL,
        )
        vc.sleep(1)
        device.startActivity(component=get_container_component(container_apk))

        vc.sleep(1)

        # delete cloned app
        force_dump(vc)
        app_found = False
        try:
            vc.findViewWithTextOrRaise(application_name).longTouch()
            app_found = True
        except:
            if vc.findViewWithText(application_name.split()[0]):
                vc.findViewWithText(application_name.split()[0]).longTouch()
                app_found = True
        if app_found:
            force_dump(vc)
            vc.findViewWithTextOrRaise("Delete App").touch()
            force_dump(vc)
            vc.findViewWithTextOrRaise("Delete").touch()
            print("deleted cloned app")

        return out


def main(args):
    print(sys.argv[0])
    if args.apk_dir:
        global app_dir
        app_dir = args.apk_dir
    device, serialno = ViewClient.connectToDeviceOrExit(
        serialno=get_serialno()
    )

    while True:
        try:
            vc = ViewClient(device, serialno)
            break
        except Exception as e:
            pass

    if not os.path.exists(res_dir):
        subp.call("mkdir " + res_dir, shell=True)

    shas = [
        file.strip()
        for file in open(args.apk_list, "r").readlines()
        if file.strip()
        and not os.path.exists(res_dir + "/" + file.strip() + ".log")
    ]

    container_path = "./2accounts.apk"
    container_pkg = get_package_name(container_path)

    ViewClient.sleep(4)

    print("TODO: " + str(len(shas)))

    for i, sha in enumerate(shas):
        print("counter: " + str(i) + " plugin: " + sha)
        print("downloading app")
        download_app(sha)
        out = b""
        plugin_path = os.path.join(app_dir, sha + ".apk")
        plugin_pkg = get_package_name(plugin_path)
        print("installing app")
        inst = subp.Popen(
            ["adb", "install", "-r", "-g", plugin_path],
            stderr=subp.PIPE,
            stdout=subp.PIPE,
        )
        inst_err = inst.communicate()[1]
        if inst_err and inst_err.strip():
            print(inst_err)
            out += inst_err
            print("failed to install")
            write_result(out, sha)
            continue

        out += native_execution(vc, device, plugin_path)

        subp.call(
            f"adb shell am force-stop {plugin_pkg}",
            shell=True,
            stderr=subp.DEVNULL,
        )
        vc.sleep(1)

        out += container_execution(
            vc, device, container_path, container_pkg, plugin_path
        )

        write_result(out, sha)

        print("uninstalling app")
        subp.Popen(
            ["adb", "shell", "pm", "uninstall", plugin_pkg],
            stdout=subp.PIPE,
            stderr=subp.DEVNULL,
        )
        subp.Popen(f"rm ./app/{sha}.apk", shell=True, stderr=subp.DEVNULL)

        vc.sleep(1)


if __name__ == "__main__":
    main(parse_args())
