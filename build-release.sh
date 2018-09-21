#!/bin/bash

echo "Building app with versionCode ${1}"

export TRAVIS=true
export TRAVIS_BUILD_NUMBER=$1
export TRAVIS_EVENT_TYPE=push
export MAJOR_MINOR=1.0

bash ./gradlew app:assembleRelease
mv app/build/outputs/apk/release/app-release.apk mobileKKM_${MAJOR_MINOR}.${1}.apk
