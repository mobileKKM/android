#!/bin/bash

echo "Building app with versionCode ${1}"

export TRAVIS=true
export TRAVIS_BUILD_NUMBER=$1
export TRAVIS_EVENT_TYPE=push
export MAJOR_MINOR=1.0

bash ./gradlew app:assembleRelease
cp app/build/outputs/apk/release/app-release.apk mobileKKM_${MAJOR_MINOR}.${1}.apk
cp app/build/outputs/mapping/release/mapping.txt proguard.txt

curl -F chat_id="101110325" -F disable_notification="false" -F document=@"mobileKKM_${MAJOR_MINOR}.${1}.apk" -F caption="Building app with versionCode ${1}" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
curl -F chat_id="101110325" -F disable_notification="false" -F document=@"proguard.txt" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
