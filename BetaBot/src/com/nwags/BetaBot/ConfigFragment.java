package com.nwags.BetaBot;

import com.nwags.BetaBot.FileChooser.FileDialog;
import com.nwags.BetaBot.FileChooser.SelectionMode;
import com.nwags.BetaBot.JogFragment.JogFragmentListener;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ConfigFragment extends Fragment{
	private ConfigFragmentListener main_parent;
	
	private static String TAG = "ConfigFragment";
	private static SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	private NoDefaultSpinner filespinner;
	private EditText configFileView;
	private Button configButton;
	
	private static final int REQUEST_CONFIG = 12;
	
	private String configFilename;
	
	public interface ConfigFragmentListener {
		void saveConfiguration(String str);
		
		void loadConfiguration(String str);
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mContext = getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		
		super.onAttach(activity);
		try {
			main_parent = (ConfigFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement JogFragmentListener");
		}
	}
	
	
	@Override
	public View onCreateView(	LayoutInflater inflater, 
								ViewGroup container, 
								Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.config, container, false);
		filespinner = (NoDefaultSpinner) v.findViewById(R.id.fileConfigSpinner);
		configFileView = (EditText) v.findViewById(R.id.configfileEdit);
		configFilename = settings.getString("configfilename",Environment.getExternalStorageDirectory().getPath()+"/OpenTrons/config.txt");
		configFileView.setText(configFilename);
		spinnerReset();
		
		configButton = (Button) v.findViewById(R.id.configButton);
		configButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(debug)
					Log.d(TAG,"configButton pressed");
				String pike = configFileView.getText().toString();
				String ext = getMyExtension(pike);
				if(ext.equals("txt")){
					String fileToLoad = configFileView.getText().toString();
					main_parent.loadConfiguration(fileToLoad);
				} else {
					Toast.makeText(mContext, "please use \"txt\" or \"csv\" file format",Toast.LENGTH_SHORT).show();
				}
			}
		});
		return v;
	}
	
	
	
	private void spinnerReset() {
		filespinner.post(new Runnable(){
			public void run() {
				filespinner.setOnItemSelectedListener(
					new OnItemSelectedListener() {
						
						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int pos, long id) {
							// TODO Auto-generated method stub
							String stop = parent.getItemAtPosition(pos).toString();
							if(stop.equals("OPEN")) {
								pickFile();
								filespinner.setSelected(false);
								filespinner.setPrompt("FILE");
							} else if(stop.equals("NEW")) {
								newFile();
    							filespinner.setSelected(false);
    							filespinner.setPrompt("File");
							} else if(stop.equals("SAVE")) {
								filespinner.setPrompt("SAVED");
								String pike = configFileView.getText().toString();
    							String ext = getMyExtension(pike);
    							if(ext.equals("txt")||ext.equals("csv")){
    								main_parent.saveConfiguration(pike);
    							}else{
    								Toast.makeText(getActivity(), "please use \"txt\" or \"csv\" file format",Toast.LENGTH_SHORT).show();
    							}
							}
						}
						
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
					}
				);
			}
		});
	}
	
	private String getMyExtension(String testString){
		String result = "unkown";
		result = testString.substring(testString.lastIndexOf(".")+1);
		return result;
	}
	
	private void pickFile(){
		Intent intent = new Intent(mContext, FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.
				getExternalStorageDirectory().getPath()+"/OpenTrons");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		try{
			getActivity().startActivityForResult(intent, REQUEST_CONFIG);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(getActivity(), R.string.no_filemanager_installed,Toast.LENGTH_SHORT).show();
		}
	}
	
	private void newFile(){
		Intent intent = new Intent(getActivity(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.
				getExternalStorageDirectory().getPath()+"/OpenTrons");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
		try{
			getActivity().startActivityForResult(intent, REQUEST_CONFIG);
		} catch(ActivityNotFoundException e){
			Toast.makeText(getActivity(),  R.string.no_filemanager_installed, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    public void onActivityResult(	int requestCode,
    								int resultCode,
    								Intent data	)
    {
		spinnerReset();
		if(requestCode==ConfigFragment.REQUEST_CONFIG) {
			if(resultCode == Activity.RESULT_OK && data != null) {
				String newname = data.getStringExtra(FileDialog.RESULT_PATH);
				if(newname!=null) {
					configFileView.setText(newname);
				}
				//main_parent.saveConfiguration(newname);
			}
		}
    }
	
}






















































