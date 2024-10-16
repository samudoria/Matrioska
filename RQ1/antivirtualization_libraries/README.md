# AntiVirtualization Libraries

## Requirements

This experiment requires an ARM64 device running Android 9. We used a Pixel 2 physical device. Due to the constraints of our laptop machines, we were not able to test the experiment on emulated devices. Thus, we cannot guarantee its correct execution.

## Experiment

The `AntiVirtualizationLibraries` project contains an app implementing all the anti-virtualization mechanisms described in the state-of-art. Once built (the prebuilt `AntiVirtualizationLibraries.apk` file is already provided), the APK file has to be pushed in the `/sdcard` with the command:

```
adb push AntiVirtualizationLibraries.apk /sdcard/sample.apk
```

The project `DeceivingContainer` contains our customized VirtualApp container, which is able to bypass all the anti-virtualization mechanisms. Install it through the following command:

```
adb install -g deceiving_container.apk
```

Once the DeceivingContainer is installed, you can specify the location of the AntiVirtualizationLibraries app so that the DeceivingContainer executes it in its virtual environment. By default, the location is `/sdcard/sample.apk`.

## Expected experiment duration

The experiment is expected to last less than a minute, since it only requires the check the outcome of the anti-virtualization libraries.

## Expected Output

When the AntiVirtualizationLibraries app is executed in the DeceivingContainer, you can see that all the anti-virtualization mechanisms fail the detection of the virtual environment.

Check out the demo video at the following [YouTube link](https://youtu.be/u-PIOZbkg0Q).
