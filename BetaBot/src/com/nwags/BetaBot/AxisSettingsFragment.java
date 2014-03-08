package com.nwags.BetaBot;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;



public class AxisSettingsFragment extends PreferenceFragment
implements OnSharedPreferenceChangeListener{
	
	SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preference_axis, false);
		addPreferencesFromResource(R.xml.preference_axis);
		
		PreferenceScreen prefScreen = (PreferenceScreen)getPreferenceScreen().findPreference("button_axisX_category_key");
		
		
		for (int i = 0; i < prefScreen.getPreferenceCount(); i++) {
            initSummary(prefScreen.getPreference(i));
        }
		
		
	    
	}
	
	private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            listPref.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            //((EditTextPreference) p).setText(settings.getString(p.getKey(), "peanut butter"));
            //p.setSummary(settings.getString(p.getKey(), "peanut butter"));
        }
        if (p instanceof MyEditTextPreference) {
            MyEditTextPreference myEditTextPref = (MyEditTextPreference) p;
            //((EditTextPreference) p).setText("peanut butter");
            //p.setSummary(settings.getString(p.getKey(), "peanut butter"));
        }
        if (p instanceof MyEditFloatTextPreference) {
            MyEditFloatTextPreference myEditFloatTextPref = (MyEditFloatTextPreference) p;
            //((EditTextPreference) p).setText("0.999");
            //p.setSummary(settings.getString(p.getKey(), "jelly"));
        }
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}
