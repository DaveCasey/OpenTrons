package com.nwags.BetaBot;

import android.os.Bundle;
import android.preference.*;

public class MotorSettingsFragment extends PreferenceFragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preference_motors, false);
		addPreferencesFromResource(R.xml.preference_motors);
		
	}
	
}
