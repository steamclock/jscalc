jscalc
======

A cross-platform mobile calculator with the logic in JavaScript and native UI on iOS and Android.

This is a proof of concept applciation for JSConf EU 2013, and Steamclock's future work on embedding JavaScript into native apps.

The slides from @apike's JSConf EU talk are available here: http://www.allenpike.com/slides/JSinNativeApps_JSConfEU2013.pdf

Building For iOS
================

Open iOS/JSCalc.xcodeproj in XCode 5. It should run on any iOS 7 device.


Building For Android
====================

Opening an Android project can be tricky. These steps work in Android Studio v0.2.7 besides one ignorable error:

* Open Android Studio
* Select "Open Project"
* Choose android/build.gradle
* Check 'auto-import'
* Click ok, and wait a minute

After that you can just select it from the 'recent projects' list. As usual you need to open a file from the project before you can build.

