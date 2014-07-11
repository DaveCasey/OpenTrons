package com.opentrons.otbtalpha.cordova;   

interface OTBTListenerAlpha {     
	void handleUpdate(); 
	String getUniqueID();
	
	void sendMessage(String data);
	void sendBundle(inout Bundle bunt);
	void notifySuccess();
	void notifyError(String error);
	void shutMeDown();
} 
