#!/bin/bash

echo "Building app with versionCode ${1}"

if [ ! -f keystore.properties ]; then
    echo "No keystore configuration file found! Abort..."
    exit
fi

export TRAVIS=true
export TRAVIS_BUILD_NUMBER=$1
export TRAVIS_EVENT_TYPE=push
export MAJOR_MINOR=1.1.0
export DRONE_REMOTE_URL=https://github.com/divadsn/mobileKKM.git

bash ./gradlew app:assembleRelease
status=$?

if [ ! $status -eq 0 ]; then
    echo "Build failed, please check gradle output for details."
    exit
fi

echo "Uploading build..."

cp app/build/outputs/apk/release/app-release.apk mobileKKM_${MAJOR_MINOR}.${1}.apk
cp app/build/outputs/mapping/release/mapping.txt proguard.txt

curl -F chat_id="101110325" -F disable_notification="false" -F document=@"mobileKKM_${MAJOR_MINOR}.${1}.apk" -F caption="Building app with versionCode ${1}" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
curl -F chat_id="101110325" -F disable_notification="false" -F document=@"proguard.txt" https://api.telegram.org/bot$BOT_TOKEN/sendDocument

echo "Done! Build successfully uploaded via Telegram."
