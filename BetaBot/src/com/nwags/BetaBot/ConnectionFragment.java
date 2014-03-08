package com.nwags.BetaBot;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class ConnectionFragment extends Fragment{
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater,
								ViewGroup container,
								Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.connection, container, false);
		
		Spinner spinnerFile = (Spinner) v.findViewById(R.id.rowSpinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapterRow = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(),
		        R.array.rows, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapterRow.setDropDownViewResource(R.layout.spinner_item);
		// Apply the adapter to the spinner
		spinnerFile.setAdapter(adapterRow);
		
		Spinner spinnerRun = (Spinner) v.findViewById(R.id.run);
		ArrayAdapter<CharSequence> adapterGo = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), 
				R.array.gogo, R.layout.spinner_item);
		adapterGo.setDropDownViewResource(R.layout.spinner_item);
		spinnerRun.setAdapter(adapterGo);
		return v;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	
	    }else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
	    	
	    }
	}
}
