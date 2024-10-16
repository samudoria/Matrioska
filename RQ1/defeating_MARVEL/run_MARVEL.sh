#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 package_name"
    exit 1
fi

package_name=$1

MARVEL_DIR="${PWD}/MARVEL"

marveloid_output="apks/$package_name/marveloid_output"

if [ ! -f $maveloid_output ]; then
    java -jar ${MARVEL_DIR}/Binaries/MARVELoid-1.0.jar -o ${PWD}/marveloid_output -re true -ri true -rp true -a ${HOME}/Android/Sdk/platforms -en 10 -ex 10 -in 10 -i ${PWD}/apks/${package_name}/${package_name}.apk -j ${JAVA_HOME}/bin/jarsigner -k ${MARVEL_DIR}/Example/my-release-key.keystore
fi

adb install -r -g ${MARVEL_DIR}/Binaries/trusted-container.apk

adb shell mkdir /sdcard/${package_name}

adb push $marveloid_output/* /sdcard/${package_name}/

adb push $marveloid_output/${package_name}.apk /sdcard/plugin.apk

adb push $marveloid_output/integrityCheck /sdcard/integrityCheck

adb push $marveloid_output/injectorDetails /sdcard/injectorDetails

adb push $marveloid_output/protectorDetails /sdcard/protectorDetails

adb shell mkdir /sdcard/dump