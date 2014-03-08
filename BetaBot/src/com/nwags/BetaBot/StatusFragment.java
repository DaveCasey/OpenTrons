package com.nwags.BetaBot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class StatusFragment extends Fragment
{
	private static final String TAG = "StatusFragment";
	private StatusFragmentListener parent;
	View v;
	
	private SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mContext = getActivity().getApplicationContext();
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        debug = settings.getBoolean("debug", false);
        
        if(debug)
        	Log.d(TAG, "Inflating StatusFragment");
		this.v = inflater.inflate(R.layout.status, container, false);
		return this.v;
	}
	
	public void updateState(Bundle b)
	{
		if(b.containsKey("posx"))
		{
			TextView localTextView = (TextView)this.v.findViewById(R.id.loc);
			Object[] arrayOfObject = new Object[4];
			float x = Float.valueOf(b.getFloat("posx"));
			float y = Float.valueOf(b.getFloat("posy"));
			float z = Float.valueOf(b.getFloat("posz"));
			arrayOfObject[0] = x;
			arrayOfObject[1] = y;
			arrayOfObject[2] = z;
			arrayOfObject[3] = Float.valueOf(b.getFloat("posa"));
			localTextView.setText(String.format("( %.3f, %.3f, %.3f, %.3f)", arrayOfObject));
			parent.setPosXYZ(x, y, z);
			
		}
		
		if(b.containsKey("line"))
			((TextView)this.v.findViewById(R.id.line)).setText(Integer.toString(b.getInt("line")));
		
		if(b.containsKey("momo"))
			((TextView)this.v.findViewById(R.id.momo)).setText(b.getString("momo"));
		
		if(b.containsKey("status"))
			//Log.d(TAG,"KEY: status, VALUE:"+b.getString("status"));
			((TextView)this.v.findViewById(R.id.status)).setText(b.getString("status"));
		
		if(b.containsKey("velocity"))
			((TextView)this.v.findViewById(R.id.velocity)).setText(Float.toString(b.getFloat("velocity")));
	}
	
	public interface StatusFragmentListener{
		void setPosXYZ(float x, float y, float z);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			parent = (StatusFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement JogFragmentListener");
		}

	}
}
