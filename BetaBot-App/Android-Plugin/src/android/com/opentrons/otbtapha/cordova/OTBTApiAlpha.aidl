package com.opentrons.otbtalpha.cordova;   

import com.opentrons.otbtalpha.cordova.OTBTListenerAlpha;

interface OTBTApiAlpha {  
	String getLatestResult();     
	
	void addListener(OTBTListenerAlpha listener);     
	
	void removeListener(OTBTListenerAlpha listener); 
	
	String getConfiguration();
	
	void run(String configuration);
	
	void pause();
	
	void resume();
	
	void kill();
	
	void write(inout byte[] data);
	
	void connect(String address);
	
	int getState();
	
	void stop();
	
	void disconnect();
	
} 
