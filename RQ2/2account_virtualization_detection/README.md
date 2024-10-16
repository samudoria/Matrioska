# Adoption of anti-virtualization libraries and MARVEL defence mechanisms

## Requirements

This experiment requires a device running Android 9. We used a Pixel 3A and a Pixel 2 real devices.

## Setup

### Emulator Setup

First, download the Android Sdk by first installing [Android Studio](https://developer.android.com/studio) (if you have not already).

Then, add the following environment variables in your `.bashrc/.zshrc` to have the required command-line tools.

```
export ANDROID_HOME=<your Android Sdk location>
export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin/:$PATH
```

We have prepared a template emulator that will be created by running the `setup_emulator.sh` script. After running it, it will take a couple of minutes to boot.

Alternatively, you can also create an Android 9 emulator using Genymotion, following the procedure explained in `RQ1/defeating_MARVEL`.

## Dataset

The `app_dataset.txt` file contains a full list of the sha256 of the apps. The APK files can be downloaded from this [archive](https://drive.google.com/file/d/1l4pHWeZYfr3axTTzrzgelB25X6PD9YZy/view?usp=sharing) (~1GB of space required).

## Experiment

Make sure the device is connected through ADB, then install `2Accounts` and its Helper.

```
adb install 2accounts.apk
adb install 2accounts_helper.apk
```

Before running the experiment, make sure to have enabled the **airplane mode** to prevent advertisements from interfering during the automated testing.

If it's your first time running the script, install the requirements:

```
pip install -r requirements.txt
```

Finally, run the script:

```
python virtualization_detection.py <apk_dir>
- <apk_dir> is the path to the directory of applications you would like to analyze
```

## Expected experiment duration

The experiment is expected to last less than one hour and a half, if executed on the provided apps.

## Expected disk usage

The experiment requires less than 10GB of space, which can only result if an emulator is used. The applications used occupy less than a GB of space.

## Expected output

If a crash occurs, a log file with the app name will be created in the `res-container-launch` directory (created at startup). The log file will contain the output of `adb logcat` concerning the crash.

We share the results of our experiment over the 5K apps in the file `final_stats.csv`, while in the `outcome_not_virtualized_apps.csv` file, we report the reason of crash for the 38 apps that could not run inside the 2account virtual environment.
