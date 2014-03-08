package com.nwags.BetaBot;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TemplateFragment extends Fragment{

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(	LayoutInflater inflater,
								ViewGroup container,
								Bundle savedInstanceState	){
		View v = inflater.inflate(R.layout.template, container, false);
		
		return v;
	}
	
}
