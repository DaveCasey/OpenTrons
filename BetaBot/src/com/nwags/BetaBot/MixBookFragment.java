package com.nwags.BetaBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nwags.BetaBot.FileChooser.FileDialog;
import com.nwags.BetaBot.FileChooser.SelectionMode;
import com.nwags.BetaBot.Support.Command;
import com.nwags.BetaBot.Support.MixBook;
import com.nwags.BetaBot.Support.Template;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MixBookFragment extends Fragment{
	private static final String TAG = "MixBookFragment";
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    
    private EditText mixbookFileView;
	private TextView templateNameView;
	private HorizontalScrollView templateNameHSV;
	private NoDefaultSpinner filespinner;
	
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
    private static final int REQUEST_TEMPLATE = 7;
	private static final int REQUEST_RECIPE = 8;
	private static final int REQUEST_ADDRECIPE = 9;
	private static final int REQUEST_MIXBOOK = 6;
	
	private MixBook mixbook;
	private Template template;
	private Command command;
	
	private SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	private String mixbookfilename;
	
	private int mixbookCode;
	
    @Override
    public View onCreateView(	LayoutInflater inflater,
    								ViewGroup container,
    						  Bundle savedInstanceState		) {
        View v = inflater.inflate(R.layout.mixbook, container, false);
        mixbook = new MixBook();
        mixbookCode = -1;
        
        mContext = getActivity().getApplicationContext();
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        debug = settings.getBoolean("debug", false);
        
        mixbookfilename = settings.getString("mixbookfilename",Environment.getExternalStorageDirectory().getPath() + "/OpenTrons/defaultMixBook.txt");
        
        templateNameView = (TextView) v.findViewById(R.id.templateNameTV);
        templateNameView.setBackgroundColor(Color.WHITE);
		templateNameView.setTextColor(Color.BLUE);
        templateNameHSV = (HorizontalScrollView) v.findViewById(R.id.templateNameHSV);
        templateNameHSV.setBackgroundColor(Color.RED);
		mixbookFileView = (EditText) v.findViewById(R.id.mixbookfileEdit);
        mixbookFileView.setText(mixbookfilename);
        
        fileOpen(mixbookfilename);
        
        final Button AddRecipeBtn = (Button) v.findViewById(R.id.recipeAddButton);
        AddRecipeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if(mixbookCode==1||mixbookCode==2||mixbookCode==3){
            		
                    Toast.makeText(mContext,"Opening \"Add Recipe\" screen", Toast.LENGTH_SHORT).show();
                    	
                    new Thread(new Runnable() {
                    	public void run() {
		            		Intent recipeIntent = new Intent(getActivity(),AddRecipeActivity.class);
		            		if(debug)
		            			Log.d(TAG,"mixbook.getTemplate().makeString: "+mixbook.getTemplate().makeString());
		            		recipeIntent.putExtra("mixbook", mixbook.makeString());
		            		recipeIntent.putExtra("file", mixbookfilename);
		            		recipeIntent.putExtra("action","ADDRECIPE");
		            		getActivity().startActivityForResult(recipeIntent, REQUEST_ADDRECIPE);
                    	}
            		}).start();
            	}else{
            		Toast.makeText(mContext,"Please add a template first", Toast.LENGTH_SHORT).show();
            	}
            }
        });
        
        final Button RecipesBtn = (Button) v.findViewById(R.id.recipesButton);
        RecipesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if(mixbookCode==2||mixbookCode==3){
            		//Toast.makeText(getActivity(),String.valueOf(mxbkCode), Toast.LENGTH_SHORT).show();
	            	final Intent recipeIntent = new Intent(getActivity(),RecipesActivity.class);
	            	recipeIntent.putExtra("mixbook",mixbook.makeString());
	            	recipeIntent.putExtra("file",mixbookfilename);
	            	if(debug)
	            		Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
	            	if(mixbookCode==3){
	            		Map<String,List<String>> ingridos = new HashMap<String, List<String>>();
	            		ArrayList<String> recips = new ArrayList<String>();
	            		
	            		StringBuilder sb = new StringBuilder();
	            		String ingrid=null;
	            		for(String key:mixbook.getRecipeSet()){
	        				Iterator<Command> iterator = mixbook.get(key).iterator();
	        				if(!recips.contains(key)){
	        					recips.add(key);
	        					ArrayList<String> ingrids = new ArrayList<String>();
	        					ingridos.put(key,ingrids);
	        				}
	        				while (iterator.hasNext()) {
	        	    			command = iterator.next();
	        	    			ingrid = command.Ingredient;
	        	    			if(!ingridos.get(key).contains(ingrid)){
	        	    				if(mixbook.getTemplate().getX(ingrid)==null||mixbook.getTemplate().getY(ingrid)==null||
	        	    						mixbook.getTemplate().getZ(ingrid)==null){
	        	    					if(ingridos.get(key).size()==0)
	        	    						sb.append("\n\n").append(key).append(":");
	        	    					
	        	    					sb.append("\n  ").append(ingrid);
	        	    					ingridos.get(key).add(ingrid);
	        	    				}
	        	    			}
	        				}
	            		}
	    	    		
	            		
	            		AlertDialog.Builder aldb = new AlertDialog.Builder(getActivity());
	            		aldb.setMessage("WARNING: The following Recipes and Ingredients are not defined in the Template: "+sb.toString())
	            		.setIcon(android.R.drawable.ic_dialog_alert)
	            		.setTitle("WARNING")
	            		.setCancelable( true )
	            		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            			@Override
	            			public void onClick(DialogInterface dialog,
	            					int id) {
	            				Toast.makeText(mContext,"Opening \"Recipe\" screen", Toast.LENGTH_SHORT).show();
	            				getActivity().startActivityForResult(recipeIntent, REQUEST_RECIPE);
	            			}
	            		});
	            		
	    	    		AlertDialog ad = aldb.create();
	    	    		ad.show();
	            	}else{
	            		Toast.makeText(mContext,"Opening \"Recipe\" screen", Toast.LENGTH_SHORT).show();
	            		getActivity().startActivityForResult(recipeIntent, REQUEST_RECIPE);
	            	}
	            	
            	}else if(mixbookCode==-1){
            		Toast.makeText(getActivity(),"-1", Toast.LENGTH_SHORT).show();
            	}else if(mixbookCode==1){
            		Toast.makeText(getActivity(),"Please add a recipe first", Toast.LENGTH_SHORT).show();
            	}else if(mixbookCode==4){
            		Toast.makeText(getActivity(),"Please attach a Template", Toast.LENGTH_SHORT).show();
            	}
            	
            }
        });
        
        final Button TemplateBtn = (Button) v.findViewById(R.id.templatePickButton);
        TemplateBtn.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Intent templateIntent = new Intent(getActivity(),TemplateActivity.class);
        		if(mixbook.getTemplate()!=null)
        		{
        			templateIntent.putExtra("template", mixbook.getTemplate().makeString());
        		}
        		Toast.makeText(mContext,"Opening \"Template\" screen", Toast.LENGTH_SHORT).show();
        		getActivity().startActivityForResult(templateIntent, REQUEST_TEMPLATE);
        	}
        });
        
        filespinner = (NoDefaultSpinner) v.findViewById(R.id.filePickSpinner);
        spinnerReset();
        return v;
    }
    
    @Override
    public void onActivityResult(	int requestCode,
    								int resultCode,
    								Intent data	)
    {
    	spinnerReset();
    	if(requestCode==MixBookFragment.REQUEST_TEMPLATE)
    	{
	    	if(resultCode == Activity.RESULT_OK && data != null)
	    	{
	    		// data contains template represented as String
	    		String attachtemplate = data.getStringExtra("attachtemplate");
	    		if(attachtemplate.equals("true")){
	    			String bigpiece = data.getStringExtra("template");
	    			//bigpiece.replaceAll("\\r", "");
	    			String[] bigpieces = bigpiece.split("\n");
	    			int bp_length = bigpieces.length;
	    			
	    			int bp_n = 0;
	    			template = new Template();
	    			while(bp_n<bp_length){
	    				String[] littlepieces = bigpieces[bp_n++].split(",");
	    				if(bp_n==1)
	    					template.setName(littlepieces[1]);
	    			
	    				template.addIngredient(littlepieces[2],littlepieces[3],littlepieces[4].trim(),littlepieces[5].trim());
	    			}
	    			
	    			if(mixbook==null)
	    				mixbook=new MixBook();
	    			
	    			if(debug)
	    				Log.d(TAG,"template.makeString: "+template.makeString());
		    		mixbook.setTemplate(template);
		    		templateNameView.setText(mixbook.getTemplate().getName());
		    		templateNameView.setBackgroundColor(Color.WHITE);
					templateNameView.setTextColor(Color.BLUE);
		    		SharedPreferences.Editor editor = settings.edit();
		    		//editor.putString("mixbookstring", mixbook.makeString());
		    		editor.putString("templatestring", mixbook.getTemplate().makeString());
		    		//editor.putString("templatefilename", templatefilename);
		    		editor.commit();
		    		fileSave(mixbookFileView.getText().toString());
		    		fileOpen(mixbookFileView.getText().toString());
	    		}
	    	}
    	} else if(requestCode==MixBookFragment.REQUEST_ADDRECIPE){
    		if(resultCode == Activity.RESULT_OK && data != null)
    		{
    			String bigpiece = data.getStringExtra("mixbook");
    			mixbookCode = mixbook.Inflate(bigpiece);
    		}
    	} else if(requestCode==MixBookFragment.REQUEST_MIXBOOK) {
    		if(debug)
    			Log.d(TAG,"REQUEST_MIXBOOK");
    		if(resultCode == Activity.RESULT_OK && data != null)
    		{
    			String newname = data.getStringExtra(FileDialog.RESULT_PATH);
    			if(newname!=null){
    				mixbookfilename = newname;
    				mixbookFileView.setText(newname);
    			}
    			
    			fileOpen(newname);
    			
    		}
    	} else if(requestCode==MixBookFragment.REQUEST_RECIPE) {
    		if(debug)
    			Log.d(TAG,"REQUEST - RECIPE");
    		if(resultCode == Activity.RESULT_OK && data !=null){
    			String bigpiece = data.getStringExtra("mixbook");
        		if(mixbook==null)
    				mixbook=new MixBook();
        		
        		mixbookCode = mixbook.Inflate(bigpiece);
    		}else if(resultCode ==Activity.RESULT_CANCELED){
    			if(debug)
    				Log.d(TAG,"RESULT - CANCELED");
    			if(data==null)
    				if(debug)
    					Log.d(TAG,"DATA: YES NULL");
    			else
    				if(debug)
    					Log.d(TAG,"DATA: NOT NULL");
    		}
    	}
    }
    
	private void newFile(){
		Intent intent = new Intent(getActivity(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.
				getExternalStorageDirectory().getPath()+"/OpenTrons");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
		try{
			getActivity().startActivityForResult(intent, REQUEST_MIXBOOK);
		} catch(ActivityNotFoundException e){
			Toast.makeText(getActivity(),  R.string.no_filemanager_installed, Toast.LENGTH_SHORT).show();
		}
	}
    
	private void pickFile(){
		Intent intent = new Intent(getActivity(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.
				getExternalStorageDirectory().getPath()+"/OpenTrons");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		try{
			getActivity().startActivityForResult(intent, REQUEST_MIXBOOK);
		} catch(ActivityNotFoundException e){
			Toast.makeText(getActivity(), R.string.no_filemanager_installed,Toast.LENGTH_SHORT).show();
		}
	}
	
	private String getMyExtension(String testString){
		String result = "unkown";
		result = testString.substring(testString.lastIndexOf(".")+1);
		return result;
	}
	
	private void fileSave(String fileString){
		File file = new File(fileString);
		FileOutputStream outputStream;
		
		try{
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
			
			outputStream = new FileOutputStream(file, false);
			outputStream.write(mixbook.makeString().getBytes());
			outputStream.close();
			
			SharedPreferences.Editor Ed = settings.edit();
			Ed.putString("mixbook", mixbook.makeString());
			Ed.putString("mixbookfilename", mixbookfilename);
			Ed.commit();
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(getActivity(), "No Mixbook has been created yet", Toast.LENGTH_SHORT).show();
		}
	}

	private void fileOpen(String fileString){
		try{
			if(mixbook==null)
				mixbook=new MixBook();
			
			if(template==null)
				template=new Template();
			
			mixbook.clear();
			template.clear();
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(fileString));
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			while((line = br.readLine())!=null){
				String[] pieces = line.split(",");
				if(pieces.length<26){	}
				
				if(!line.endsWith("\n"))
					line = line + "\n";
				sb.append(line);
			}
			mixbookCode = mixbook.Inflate(sb.toString());
			if(mixbookCode>0){
				templateNameView.setText(mixbook.getTemplate().getName());
				templateNameView.setBackgroundColor(Color.WHITE);
				templateNameView.setTextColor(Color.BLUE);
				templateNameHSV.setBackgroundColor(Color.WHITE);
				SharedPreferences.Editor Ed = settings.edit();
				Ed.putString("mixbook", mixbook.makeString());
				Ed.putString("mixbookfilename", mixbookfilename);
				Ed.commit();
			}else{
				templateNameView.setText("Missing Template");
				templateNameView.setBackgroundColor(Color.RED);
				templateNameView.setTextColor(Color.WHITE);
		        templateNameHSV.setBackgroundColor(Color.RED);
			}
			br.close();
		}catch (Exception e){
			e.printStackTrace();
			mixbook.clear();
			template.clear();
			template.setName("Missing Template");
			
			mixbook.setTemplate(template);
			templateNameView.setText(mixbook.getTemplate().getName());
			templateNameView.setBackgroundColor(Color.RED);
			templateNameView.setTextColor(Color.WHITE);
		}
	}
	
	public void spinnerReset(){
		filespinner.post(new Runnable(){
        	public void run() {
        		filespinner.setOnItemSelectedListener(
            		new OnItemSelectedListener() {

    					@Override
    					public void onItemSelected(AdapterView<?> parent, View view,
    							int pos, long id) {
    						
    						String stop = parent.getItemAtPosition(pos).toString();
    						if(stop.equals("OPEN")){
    							pickFile();
    							filespinner.setSelected(false);
    							filespinner.setPrompt("File");
    						}else if(parent.getItemAtPosition(pos).toString().equals("NEW")){
    							newFile();
    							filespinner.setSelected(false);
    							filespinner.setPrompt("File");
    						}else if(parent.getItemAtPosition(pos).toString().equals("SAVE")){
    							filespinner.setPrompt("SAVED");
    							String pike = mixbookFileView.getText().toString();
    							String ext = getMyExtension(pike);
    							if(ext.equals("txt")||ext.equals("csv")){
    								if(templateNameView.getText().toString().length()!=0){
    									//TODO: Validate UI form
    									fileSave(mixbookFileView.getText().toString());
    								}else{
    									Toast.makeText(getActivity(), "please name the Template", Toast.LENGTH_SHORT).show();
    								}
    							}else{
    								Toast.makeText(getActivity(), "please use \"txt\" or \"csv\" file format",Toast.LENGTH_SHORT).show();
    							}
    						}
    					}

    					@Override
    					public void onNothingSelected(AdapterView<?> arg0) {	}
            		});
        	}
        });
	}
}
