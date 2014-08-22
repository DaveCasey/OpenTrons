THE APP
======
These folders contain all the source code for the BetaBot App, including the requisite Android plugin and user-interface for building the app with Cordova. If you just want the app, you can side load the OpenTrons_2_4Alpha.apk file found in the Android-App folder. Otherwise, there are many options for playing with the code. One option is to import the TA20140702 and TA20140702-CordovaLib folders into Eclipse. To build the app using Cordova, you can do the following, after setting up Cordova and cd-ing to your desired folder, of course:

- cordova create [you-folder-name] [your.package.name] [your-project-name]
- cd [your-folder-name]
- cordova platform add android
- cordova plugin add https://github.com/Opentrons/Android-Plugin.git

https://github.com/Opentrons/Android-Plugin was specifically setup for this as trying to do the same with this repo will result in errors...

Next, you must replace the project's www folder contents with the contents from User-Interface www folder.

From here there are a number of options, but the two most practical and basic are that you can go ahead and run the app with "cordova run android" if you don't want further changes or use "cordova build android", and then you can import the app into Eclipse and make further changes. I went ahead and ran it, and then imported it into Eclipse to make some changes. The changes included fixing the screen orientation to portrait (can be done in AndroidManifest.xml or config.xml in cordova project folder), changing the icons, the app name, app version).





LICENSE
--------
Copyright 2014 Nicholas Wagner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

