Configuration
=========

To successfully operate the BioBot, the TinyG requires a certain level of proper configuration. This guide provides 

instructions to easily configure the TinyG for operation and some additional information for troubleshooting and 

customization.

========
1st Step: Follow Synthetos' Guide For Connecting TinyG
========
The first thing you need to do is connect TinyG to your computer over USB in order to load the configuration settings. 

Synthetos provides a handy guide for that, found here

https://github.com/synthetos/TinyG/wiki/Connecting-TinyG#establish-usb-connection. 

Although there are many ways to interface with the TinyG, CoolTerm is the easiest, and Synthetos uses it in their guide. 

The rest of this guide picks up from there and assumes you have connected to TinyG with CoolTerm.

========
2nd Step: Configure CoolTerm for sending configuration file
========
Now that you're connected with CoolTerm, there are a couple settings to change to enable loading the config file.

First, click on the options button and select the Transmit options. Check the 'Use transmit line delay' option and enter a

value of 10 for 'Delay (ms):'. That may be more than necessary, but it gets the job done. That's the most important 

setting. I also checked the 'Notify after setting sending text file' option. The settings should look like this:

![ScreenShot](https://cloud.githubusercontent.com/assets/5043095/3868780/72f0259a-2063-11e4-8592-e7c86865373f.png)

Optionally, I increased the receive buffer size to 50000 so I could see the entire set of results.

![ScreenShot](https://cloud.githubusercontent.com/assets/5043095/3868778/513e9da0-2063-11e4-93bf-e1ccebd9daf1.png)


========
3rd Step: Send configuration file
========
Download [base_config.txt](https://github.com/Opentrons/OpenTrons/blob/master/Configuration/base_config.txt).

With CoolTerm, select Connction -> Send Textfile...

A wizard should pop up and select the base_config.txt file you downloaded.

YOUR DONE!... almost

========
Troubleshooting
========
You're only almost done because depending on how the motors are connected, you may need to change their polarity.

The logic for the motor polarity goes like this:

$[n]po=[0 or 1], where n is 1,2,3, or 4

and 1 <-> X, 2 <-> Y, 3 <-> Z, and 4 <-> A

So, for example, if you need to switch the polarity of the Y motors, you would feed either

$2po=0 or $2po=1

into TinyG with CoolTerm. There are only 2 options for a given motor's polarity, so you have a 50-50 shot.

If it doesn't work, switch the value and try again.


========
Customization
========
There are many other configuration settings you can play with, as you can probably tell if you looked at base_config.txt.

Most of these you probably want to ignore, but you may be interested in changing the accelearation and/or

speed. For that, and other configuration settings, please see Synthetos' TinyG configuration guide here

https://github.com/synthetos/TinyG/wiki/TinyG-Configuration#xvm---velocity-maximum

and here 

https://github.com/synthetos/TinyG/wiki/TinyG-Configuration#xjm---jerk-maximum
