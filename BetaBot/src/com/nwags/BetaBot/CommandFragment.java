package com.nwags.BetaBot;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;


public class CommandFragment extends Fragment {

	private ScrollView mSvText;
    private TextView mTvSerial;
    private Button btWrite;
    private Button btClear;
    private EditText etWrite;
    private String strHolder;
    
	private static final String TAG = "CommandFragment";
	
	SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	View v;
	
	private static final int TEXT_MAX_SIZE = 6000;
	
	private CommandFragmentListener parent;
	
	public interface CommandFragmentListener{
		public boolean isConnected();
		public void sendCommand(String cmd);
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(debug)
			Log.d(TAG, "Inflating CommandFragment");
		
		v = inflater.inflate(R.layout.commands, container, false);
		
		mSvText = (ScrollView) v.findViewById(R.id.svText);
        mTvSerial = (TextView) v.findViewById(R.id.tvSerial);
        btWrite = (Button) v.findViewById(R.id.btWrite);
        btClear = (Button) v.findViewById(R.id.btClear);
        etWrite = (EditText) v.findViewById(R.id.etWrite);
        
        etWrite.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER){
					writeDataToSerial();
					return true;
				}
				return false;
			}
        });
        
        btWrite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				writeDataToSerial();
			}
        });
        
        btClear.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		mTvSerial.setText("");
        	}
        });
        
        //mTvSerial.setText(settings.getString("rawness", ""));
        
		return v;
	}
	
	private void writeDataToSerial() {
		String strWrite = etWrite.getText().toString();
		strWrite = changeLinefeedcode(strWrite);
		parent.sendCommand(strWrite);
	}
	
	private String changeLinefeedcode(String str) {
		str = str.replace("\\r", "\r");
		str = str.replace("\\n", "\n");
		str = str + "\r\n";
		return str;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		strHolder = settings.getString("rawness", "");
		mTvSerial.setText(strHolder);
		
		if(parent.isConnected()){
			btWrite.setEnabled(true);
			etWrite.setEnabled(true);
		}else{
			btWrite.setEnabled(false);
			etWrite.setEnabled(false);
		}
	}
	
	public void doSomething(String str){
		
		if(strHolder.length()>TEXT_MAX_SIZE);
		{
			StringBuilder sb = new StringBuilder();
			sb.append(strHolder);
			sb.delete(0, 2000);
			mTvSerial.setText(sb);
		}
		strHolder+=str;
		mTvSerial.append(str);
		mSvText.fullScroll(ScrollView.FOCUS_DOWN);
		
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		try
		{
			parent = (CommandFragmentListener) activity;
		} 
		catch (ClassCastException e) 
		{ 
			throw new ClassCastException(activity.toString()
					+ " must implement CommandFragmentListener");
		}
		
		mContext = getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
	}
	
	public void enableButton(){
		btWrite.setEnabled(true);
		etWrite.setEnabled(true);
	}
	public void disableButton(){
		btWrite.setEnabled(false);
		etWrite.setEnabled(false);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Editor Ed = settings.edit();
		Ed.putString("rawness", mTvSerial.getText().toString());
		Ed.commit();
	}
	
	
}
