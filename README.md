# Matrioska: A User-Centric Defense Against Virtualization-Based Repackaging Malware on Android

## Overview

The Android virtualization technique enables an app to create independent virtual environments running on top of the native Android environment, allowing multiple apps to execute simultaneously. While this technique has legitimate uses, it has also attracted attention from attackers who exploit it for malicious purposes. Research has identified virtualization-based malware as a significant threat, with over 71,303 malicious samples discovered.

Existing defense mechanisms have been designed to detect and prevent virtualization-based malware and repackaging attacks. However, these mechanisms have limitations that can be exploited by attackers.

This repository contains datasets and source code, organized in three folders that map to the following three research questions:

-   RQ1 - "How effective are existing defence mechanisms against VBR malware?"
-   RQ2 - "How prevalent are the current detection and defence mechanisms against VBR malware?"
-   RQ3 - "How well does our solution perform in detecting VBR malware with respect to the state-of-art?"

## Requirements

We provide a list of software required to run most of our experiments.

First, download the Android SDK by installing [Android Studio](https://developer.android.com/studio).

Then, add the following environment variables in your `.bashrc/.zshrc` to have the required command-line tools.

```
export ANDROID_HOME=<your Android Sdk location>
export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin/:$PATH
```

In case you do not have a physical device running Android 9, RQ1 will also require you to install [Genymotion](https://www.genymotion.com/product-desktop/) and its dependency, [VirtualBox](https://www.virtualbox.org/).

The Java SDK is also required, version 11 is suggested.

The artifact was developed using the following setup:

-   Ubuntu 22.04
-   Python 3.12
-   Java SDK 11

We used a Pixel 2 and a Pixel 3a running Android 9 for experiments in RQ1 and RQ3.
