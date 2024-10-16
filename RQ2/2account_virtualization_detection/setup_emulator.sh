# Kill all running emulators
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done

# Install the emulator system image if not
cd $ANDROID_HOME/tools/bin
sdk="system-images;android-28;google_apis_playstore;x86"
installedImage=$(sdkmanager --list_installed | grep -o $sdk)
if [[ $installedImage = $sdk ]]; then 
  echo "The required SDK is already installed"; 
else 
  yes | sdkmanager --install $sdk
  yes | sdkmanager --licenses

fi

name=pixel_2_28
cd $ANDROID_HOME/tools/bin
echo no | avdmanager create avd --force --name $name --abi x86 --package $sdk

sysDir=$(cat $HOME/.android/avd/$name.avd/config.ini | grep "image.sysdir.1=")

tee $HOME/.android/avd/$name.avd/config.ini << END
AvdId=$name
PlayStore.enabled=true
abi.type=x86
avd.ini.displayname=$name
avd.ini.encoding=UTF-8
disk.dataPartition.size=2G
fastboot.chosenSnapshotFile=
fastboot.forceChosenSnapshotBoot=no
fastboot.forceColdBoot=no
fastboot.forceFastBoot=yes
hw.accelerometer=yes
hw.arc=false
hw.audioInput=yes
hw.battery=yes
hw.camera.back=virtualscene
hw.camera.front=emulated
hw.cpu.arch=x86
hw.cpu.ncore=4
hw.dPad=no
hw.device.hash2=MD5:55acbc835978f326788ed66a5cd4c9a7
hw.device.manufacturer=Google
hw.device.name=pixel_2
hw.gps=yes
hw.gpu.enabled=yes
hw.gpu.mode=auto
hw.initialOrientation=portrait
hw.keyboard=yes
hw.lcd.density=420
hw.lcd.height=1920
hw.lcd.width=1080
hw.mainKeys=no
hw.ramSize=2048
hw.sdCard=yes
hw.sensors.orientation=yes
hw.sensors.proximity=yes
hw.trackBall=no
$sysDir
runtime.network.latency=none
runtime.network.speed=full
sdcard.path=$HOME/.android/avd/$name.avd/sdcard.img
sdcard.size=512 MB
showDeviceFrame=no
skin.dynamic=yes
skin.name=1080x1920
skin.path=_no_skin
skin.path.backup=_no_skin
tag.display=Google Play
tag.id=google_apis_playstore
vm.heapSize=228
END

cd $ANDROID_HOME/emulator
./emulator -list-avds

cd $ANDROID_HOME/emulator;
nohup ./emulator -avd $name -no-snapshot -no-boot-anim -wipe-data & 
