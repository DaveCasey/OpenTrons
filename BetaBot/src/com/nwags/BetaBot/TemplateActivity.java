package com.nwags.BetaBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Stack;

import com.nwags.BetaBot.FileChooser.FileDialog;
import com.nwags.BetaBot.FileChooser.SelectionMode;
import com.nwags.BetaBot.Support.Template;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TemplateActivity extends Activity{
	private static final String TAG = "TemplateActivity";
	
	public static EditText templateFileView;
	public static EditText templateNameView;
	public static String templatefilename;
	public static TableLayout templateTable;
	public static NoDefaultSpinner fileSpinner;
	
	public static Template template;
	
	public static RandomAccessFile templateFile;
	
	public static final int REQUEST_TEMPLATE = 4;
	
	public static int tNRows;
	
	public static String tString=null;
	
	public static SharedPreferences settings;
	public static Context mContext;
	public static boolean debug;
	
	public static Stack<RowHolder> rowholders = null;
	public static Typeface tf;
	
	@Override
	protected void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tNRows = 0;
		if(template==null)
			template = new Template();
		else
			template.clear();
		
		setContentView(R.layout.template);
		
		mContext = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug",false);
		
		templatefilename = settings.getString("templatefilename", Environment.getExternalStorageDirectory().getPath() + "/OpenTrons/defaultTemplate.txt");
		
		templateNameView = (EditText) findViewById(R.id.templateNameEdit);
		templateFileView = (EditText) findViewById(R.id.templateFileEdit);
		templateFileView.setText(templatefilename);
		templateTable = (TableLayout) findViewById(R.id.templateTable);
		if(rowholders==null)
			rowholders = new Stack<RowHolder>();
		else
			rowholders.clear();
		
		addRows(10);
		
		
		tString = getIntent().getStringExtra("template");
		if(template.Inflate(tString))
		{
			if(template.size()>tNRows){
				addRows(template.size()-tNRows);
			}
			//TableRow tr;
			//MyEditText met;
			int i = 0;
			String ingrid = null;
			while(i<template.size()) {
				/*tr = (TableRow) templateTable.findViewWithTag(("ta"+String.valueOf(i+1)));
				met = (MyEditText) tr.findViewWithTag("ingredient");
				*/
				ingrid = template.getIngredient(i);
				/*met.setText(ingrid);
				met = (MyEditText) tr.findViewWithTag("x");
				met.setText(template.getX(ingrid));
				met = (MyEditText) tr.findViewWithTag("y");
				met.setText(template.getY(ingrid));
				met = (MyEditText) tr.findViewWithTag("z");
				met.setText(template.getZ(ingrid));*/
				
				rowholders.get(i).ingredient.setText(ingrid);
				if(Float.valueOf(template.getX(ingrid))<0f) {template.setIngredient(ingrid, "0.0", template.getY(ingrid),template.getZ(ingrid));}
				if(Float.valueOf(template.getY(ingrid))<0f) {template.setIngredient(ingrid, template.getX(ingrid),"0.0", template.getZ(ingrid));}
				if(Float.valueOf(template.getZ(ingrid))<0f) {template.setIngredient(ingrid, template.getX(ingrid),template.getY(ingrid), "0.0");}
				
				rowholders.get(i).x.setText(String.format("%.3f", Float.valueOf(template.getX(ingrid))));
				rowholders.get(i).y.setText(String.format("%.3f", Float.valueOf(template.getY(ingrid))));
				rowholders.get(i++).z.setText(String.format("%.3f", Float.valueOf(template.getZ(ingrid))));
			}
		}
		
		templateNameView.setText(template.getName());
		
		getIntent().putExtra("attachtemplate", "false");
		fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
		spinnerReset();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private class MyOnItemSelectedListener implements OnItemSelectedListener{
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Toast.makeText(parent.getContext(), "OnItemSelectedListener : " +
					parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {	}
		
	}
	
	private boolean addRows(int n){
		boolean result = false;
		//TableRow tr;
		//TextView tv;
		LayoutParams tvLP; 
		//MyEditText met;
		LayoutParams metLP;
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for(int i=0;i<n;i++){
			++tNRows;
			RowHolder holder = new RowHolder();
			//tr = (TableRow)inflater.inflate(R.layout.myrow,null);
			holder.tr = (TableRow)inflater.inflate(R.layout.myrow,null);
			//templateTable.addView(tr);
			templateTable.addView(holder.tr);	// THIS MUST GO BEFORE LayoutParams
			//tr.setTag("ta"+String.valueOf(tNRows));
			holder.tr.setTag("ta"+String.valueOf(tNRows));
			
			//tv = (TextView)inflater.inflate(R.layout.mytextview, null);
			holder.no = (TextView)inflater.inflate(R.layout.mytextview, null);
			//tr.addView(tv);
			holder.tr.addView(holder.no);	// THIS MUST GO BEFORE LayoutParams
			//tv.setTag("no");
			holder.no.setTag("no");
			//tvLP = (LayoutParams) tv.getLayoutParams();
			tvLP = (LayoutParams) holder.no.getLayoutParams();
			tvLP.height=getDips(30);
			tvLP.width=getDips(40);
			//tv.setText(String.valueOf(tNRows));
			//tv.setTextSize(20);
			//tv.setBackgroundResource(R.drawable.cell_shape);
			holder.no.setText(String.valueOf(tNRows));
			holder.no.setTextSize(20);
			holder.no.setBackgroundResource(R.drawable.cell_shape);
			//tv.setOnClickListener(new OnClickListener(){
			//holder.no.setOnClickListener(new OnClickListener() {
			//	@Override
			//	public void onClick(View v) {
			//		fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
			//		fileSpinner.setPrompt("FILE");
			//	}
			//});
			
			
			
			//met = (MyEditText)inflater.inflate(R.layout.myedittext,null);
			holder.ingredient = (MyEditText)inflater.inflate(R.layout.myedittext,null);
			//tr.addView(met);
			holder.tr.addView(holder.ingredient);	// THIS MUST GO BEFORE LayoutParams
			//met.setTag("ingredient");
			holder.ingredient.setTag("ingredient");
			//metLP = (LayoutParams) met.getLayoutParams();
			metLP = (LayoutParams) holder.ingredient.getLayoutParams();
			metLP.height=getDips(30);
			metLP.width=getDips(150);
			//met.setTextSize(20);
			//met.setBackgroundResource(R.drawable.cell_shape);
			holder.ingredient.setTextSize(20);
			holder.ingredient.setBackgroundResource(R.drawable.cell_shape);
			//met.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
			//met.setOnClickListener(new OnClickListener(){
			holder.ingredient.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
					fileSpinner.setPrompt("FILE");
				}
			});

			
			
			//met = (MyEditText)inflater.inflate(R.layout.myfloatedittext,null);
			holder.x = (MyEditText)inflater.inflate(R.layout.myedittext,null);
			//tr.addView(met);
			holder.tr.addView(holder.x);	// THIS MUST GO BEFORE LayoutParams
			//met.setTag("x");
			holder.x.setTag("x");
			//metLP = (LayoutParams) met.getLayoutParams();
			metLP = (LayoutParams) holder.x.getLayoutParams();
			metLP.height=getDips(30);
			metLP.width=getDips(100);
			//met.setTextSize(20);
			//met.setBackgroundResource(R.drawable.cell_shape);
			holder.x.setTextSize(20);
			holder.x.setBackgroundResource(R.drawable.cell_shape);
			//met.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			//met.setOnClickListener(new OnClickListener(){
			holder.x.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
					fileSpinner.setPrompt("FILE");
				}
			});
			
			
			
			//met = (MyEditText)inflater.inflate(R.layout.myfloatedittext,null);
			holder.y = (MyEditText)inflater.inflate(R.layout.myedittext,null);
			//met.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			//tr.addView(met);
			holder.tr.addView(holder.y);	// THIS MUST GO BEFORE LayoutParams
			//met.setTag("y");
			holder.y.setTag("y");
			//metLP = (LayoutParams) met.getLayoutParams();
			metLP = (LayoutParams) holder.y.getLayoutParams();
			metLP.height=getDips(30);
			metLP.width=getDips(100);
			//met.setTextSize(20);
			//met.setBackgroundResource(R.drawable.cell_shape);
			holder.y.setTextSize(20);
			holder.y.setBackgroundResource(R.drawable.cell_shape);
			//met.setOnClickListener(new OnClickListener(){
			holder.y.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
					fileSpinner.setPrompt("FILE");
				}
			});
			
			
			//met = (MyEditText)inflater.inflate(R.layout.myfloatedittext,null);
			holder.z = (MyEditText)inflater.inflate(R.layout.myedittext,null);
			//met.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			//tr.addView(met);
			holder.tr.addView(holder.z);	// THIS MUST GO BEFORE LayoutParams
			//met.setTag("z");
			holder.z.setTag("z");
			//metLP = (LayoutParams) met.getLayoutParams();
			metLP = (LayoutParams) holder.z.getLayoutParams();
			metLP.height=getDips(30);
			metLP.width=getDips(100);
			//met.setTextSize(20);
			//met.setBackgroundResource(R.drawable.cell_shape);
			holder.z.setTextSize(20);
			holder.z.setBackgroundResource(R.drawable.cell_shape);
			//met.setOnClickListener(new OnClickListener(){
			holder.z.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
					fileSpinner.setPrompt("FILE");
				}
			});
			
			rowholders.push(holder);
		}
		
		return result;
	}
	
	private boolean deleteRows(int n) {
		boolean result = false;
		for(int i=0;i<n;i++){
			templateTable.removeView(rowholders.pop().tr);
			--tNRows;
		}
		return result;
	}
	
	private boolean insertRows(int insert, int n) {
		boolean result = false;
		
		Stack<RowHolder> tempholder = new Stack<RowHolder>();
		while(tNRows>=insert) {
			tNRows--;
			tempholder.push(rowholders.pop());
			templateTable.removeView(tempholder.peek().tr);
		}
		addRows(n);
		
		while(!tempholder.isEmpty()) {
			tNRows++;
			RowHolder rh = new RowHolder();
			rh = tempholder.pop();
			rh.no.setText(String.valueOf(tNRows));
			rh.tr.setTag("ta"+String.valueOf(tNRows));
			templateTable.addView(rh.tr);
			rowholders.push(rh);
		}
		
		return result;
	}
	
	public void AddRowsHandler(View view) {
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View myADView = inflater.inflate(R.layout.ad_rows, null, false);

	    final MyEditText et = (MyEditText) myADView.findViewById(R.id.row_count);
	    et.setTypeface(tf);
	    et.setHint("enter number to add");
	    
		AlertDialog.Builder adBldr = new AlertDialog.Builder(TemplateActivity.this);
		adBldr.setView(myADView)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("ADD ROWS")
		.setCancelable( true )
		.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int id) {
				MyEditText eta = (MyEditText) myADView.findViewById(R.id.row_count);
				if(eta.getText().toString().length()>0){
					String numero = eta.getText().toString();
					int rcount = Integer.valueOf(numero);
					if((rcount<100)&&(tNRows<101)){
						Toast.makeText(TemplateActivity.this, "WHOA! "+String.valueOf(rcount),Toast.LENGTH_SHORT).show();
						addRows(rcount);
					}
				}else
					Toast.makeText(TemplateActivity.this, "Enter a number!",Toast.LENGTH_SHORT).show();
				
			}
		});
		AlertDialog alert = adBldr.create();
		alert.show();
	}
	
	public void DeleteRowsHandler(View view){
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View myADView = inflater.inflate(R.layout.ad_rows, null, false);

	    final MyEditText et = (MyEditText) myADView.findViewById(R.id.row_count);
	    et.setTypeface(tf);
	    et.setHint("enter number to delete");
	    
		AlertDialog.Builder adBldr = new AlertDialog.Builder(TemplateActivity.this);
		adBldr.setView(myADView)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("DELETE ROWS FROM END")
		.setCancelable( true )
		.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int id) {
				MyEditText eta = (MyEditText) myADView.findViewById(R.id.row_count);
				if(eta.getText().toString().length()>0){
					String numero = eta.getText().toString();
					int rcount = Integer.valueOf(numero);
					if(tNRows-rcount>=10){
						Toast.makeText(TemplateActivity.this, String.valueOf(rcount)+" of the rows deleted",Toast.LENGTH_SHORT).show();
						deleteRows(rcount);
					}else{
						//int c = 10 + rcount - tNRows;
						//rcount = (tNRows - rcount) + c;
						rcount = tNRows - 10;
						Toast.makeText(TemplateActivity.this, String.valueOf(rcount)+" of the rows deleted to keep a minimum of 10 rows",Toast.LENGTH_SHORT).show();
						deleteRows(rcount);
					}
				}else
					Toast.makeText(TemplateActivity.this, "Enter a number!",Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = adBldr.create();
		alert.show();
	}
	
	public void InsertRowsHandler(View view){
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View myIView = inflater.inflate(R.layout.i_rows, null, false);

	    final MyEditText et1 = (MyEditText) myIView.findViewById(R.id.insert_row);
	    et1.setTypeface(tf);
	    et1.setHint("insert before row number");
	    
	    final MyEditText et2 = (MyEditText) myIView.findViewById(R.id.insert_count);
	    et2.setTypeface(tf);
	    et2.setHint("enter number to insert");
	    
	    
		AlertDialog.Builder adBldr = new AlertDialog.Builder(TemplateActivity.this);
		adBldr.setView(myIView)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("INSERT ROWS")
		.setCancelable( true )
		.setPositiveButton("INSERT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int id) {
				int rcount = 0;
				int rinsert = 1;
				MyEditText eta = (MyEditText) myIView.findViewById(R.id.insert_count);
				MyEditText etb = (MyEditText) myIView.findViewById(R.id.insert_row);
				if(eta.getText().toString().length()>0){
					String numeroi = etb.getText().toString();
					String numero = eta.getText().toString();
					rcount = Integer.valueOf(numero);
					if(etb.getText().toString().length()!=0)
						rinsert = Integer.valueOf(numeroi);
					
					if(rinsert==0)
						rinsert=1;
					
					if(rinsert>tNRows)
						rinsert=tNRows;
					
					
					if((rcount<100)&&(tNRows<101)){
						if(rinsert<=tNRows){
							Toast.makeText(TemplateActivity.this, "WHOA! "+String.valueOf(rcount),Toast.LENGTH_SHORT).show();
							insertRows(rinsert, rcount);
						} else {
							Toast.makeText(TemplateActivity.this, "Fix your insertion point!",Toast.LENGTH_SHORT).show();
						}
					}
				}else
					Toast.makeText(TemplateActivity.this, "Fix your number of rows!",Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = adBldr.create();
		alert.show();
	}
	
	
	private int getDips(int pix){
		int result = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pix, getResources().getDisplayMetrics());
		return result;
	}
	
	
	private void pickFile(){
		Intent intent = new Intent(this, FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.
				getExternalStorageDirectory().getPath()+"/OpenTrons");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		try{
			startActivityForResult(intent, REQUEST_TEMPLATE);
		} catch(ActivityNotFoundException e){
			Toast.makeText(this, R.string.no_filemanager_installed,Toast.LENGTH_SHORT).show();
		}
	}
	
	private void newFile(){
		Intent intent = new Intent(this, FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.
				getExternalStorageDirectory().getPath()+"/OpenTrons");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
		try{
			startActivityForResult(intent, REQUEST_TEMPLATE);
		} catch(ActivityNotFoundException e){
			Toast.makeText(this,  R.string.no_filemanager_installed, 0).show();
		}
	}
	
	@Override
	public void onActivityResult(	int requestCode, int resultCode, Intent data	) {
		fileSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {
						
						if(parent.getItemAtPosition(pos).toString().equals("OPEN")){
							pickFile();
						}else if(parent.getItemAtPosition(pos).toString().equals("NEW")){
							newFile();
						}else if(parent.getItemAtPosition(pos).toString().equals("SAVE")){
							String pike = templateFileView.getText().toString();
							String sext = getMyExtension(pike);
							if(sext.equals("txt")||sext.equals("csv")){
								if(templateNameView.getText().toString().length()!=0){
									//TODO: Validate UI form
									fileSave(templateFileView.getText().toString());
								}else{
									Toast.makeText(getApplication(), "please name the Template", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getApplicationContext(), "please use \"txt\" or \"csv\" file formate",Toast.LENGTH_SHORT).show();
							}
						}else if(parent.getItemAtPosition(pos).toString().equals("ATTACH")){
							if(templateNameView.getText().toString().length()!=0){
								//TODO: Validate UI form
								
								if(attachFile()){
									getIntent().putExtra("attachtemplate", "true");
									getIntent().putExtra("template",template.makeString());
									setResult(RESULT_OK,getIntent());
									finish();
								}else{
									Toast.makeText(getApplication(), "please use at least one Ingredient", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getApplication(), "please name the Template", Toast.LENGTH_SHORT).show();
							}
							fileSpinner.setAdapter(fileSpinner.getAdapter());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {	}
							
				});
		if(requestCode!=REQUEST_TEMPLATE)
			return;
			
		
		if(resultCode==Activity.RESULT_OK && data != null){
			String newname = data.getStringExtra(FileDialog.RESULT_PATH);
			if(newname!=null){
				templatefilename = newname;
				templateFileView.setText(newname);
				
				try{
					if(templateFile != null)
						templateFile.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
				
				if(getMyExtension(templateFileView.getText().toString()).equals("txt")||getMyExtension(templateNameView.getText().toString()).equals("csv")){
					if(validateCSVTemplateFile(templateFileView.getText().toString())){
						loadCSVTemplateFile(templateFileView.getText().toString());
					}
				}
				
			}
		}
	}
	
	
	private String getMyExtension(String testString){
		String result = "unkown";
		result = testString.substring(testString.lastIndexOf(".")+1);
		return result;
	}
	
	
	private boolean validateCSVTemplateFile(String fileString){
		boolean result = false;
		String line="";
		int numLines = 0;
		BufferedReader br = null;
		boolean hasTemplate=false;
		boolean hasName=false;
		boolean hasIngredient=false;
		boolean hasX=false;
		boolean hasY=false;
		boolean hasZ=false;
		boolean hasFive=false;
		float x;
		float y;
		float z;
		
		try{
			br = new BufferedReader(new FileReader(fileString));
			while((line = br.readLine())!=null){
				numLines++;
				String[] dataRow = line.split(",");
				
				if(dataRow.length<5){
					
				}else{
					hasFive=true;
				
					if(dataRow[0].equals("TEMPLATE"))
					{	
						hasTemplate=true;
						if(!dataRow[1].trim().equals(""))
						{
							hasName=true;
							if(!dataRow[2].trim().equals(""))
							{	
								hasIngredient=true;
								try{
									x=Float.parseFloat(dataRow[3]);
									hasX = true;
									try{
										y=Float.parseFloat(dataRow[4]);
										hasY = true;
										try{
											z = Float.parseFloat(dataRow[5]);
											hasZ = true;
										}catch(NumberFormatException e){
											Toast.makeText(this, "malformed z value - Line: "+String.valueOf(numLines+1), Toast.LENGTH_SHORT).show();
										}
									}catch(NumberFormatException e){
										Toast.makeText(this, "malformed y value - Line: "+String.valueOf(numLines+1), Toast.LENGTH_SHORT).show();
									}
								}catch(NumberFormatException e){
									Toast.makeText(this, "malformed x value - Line: "+String.valueOf(numLines+1), Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
				}
				if(	hasTemplate &&
					hasName &&
					hasIngredient &&
					hasX &&
					hasY &&	
					hasZ &&
					hasFive )
				{
					result=true;
					hasTemplate=false;
					hasName=false;
					hasIngredient=false;
					hasX=false;
					hasY=false;
					hasZ=false;
					hasFive=false;
				}
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(br!=null){
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	private void loadCSVTemplateFile(String fileString)
	{
		String line = "";
		int lineNum = 0;
		BufferedReader br = null;
		boolean hasTemplate=false;
		boolean hasName=false;
		boolean hasIngredient=false;
		boolean hasX=false;
		boolean hasY=false;
		boolean hasZ=false;
		boolean hasFive=false;
		float x;
		float y;
		float z;
		//TableRow tr;
		//MyEditText met;
		StringBuilder sbTemplate = new StringBuilder();
		
		try{
			br = new BufferedReader(new FileReader(fileString));
			while((line = br.readLine())!=null){
				line.replace("\r", "");
				String[] dataRow = line.split(",");
				
				if(dataRow.length<4){
					
				}else{
					hasFive=true;
					if(dataRow[0].equals("TEMPLATE"))
					{	
						hasTemplate=true;
						if(!dataRow[1].equals(""))
						{
							hasName=true;
							if(!dataRow[2].trim().equals(""))
							{	
								hasIngredient=true;
								try{
									x=Float.parseFloat(dataRow[3]);
									hasX = true;
									try{
										y=Float.parseFloat(dataRow[4]);
										hasY = true;
										try{
											z=Float.parseFloat(dataRow[5]);
											hasZ = true;
										}catch(NumberFormatException e){
											if(debug)
												Log.d(TAG,"TEMP Z ERROR!");
										}
									}catch(NumberFormatException e){
										if(debug)
											Log.d(TAG, "TEMP Y ERROR!");
									}
								}catch(NumberFormatException e){
									if(debug)
										Log.d(TAG,"TEMP X ERROR!");
								}
							}
						}
					}
				}
				if(	hasTemplate &&
					hasName &&
					hasIngredient &&
					hasX &&
					hasY &&	
					hasZ &&
					hasFive )
				{
					lineNum++;
					if(lineNum==1)
					{
						templateNameView.setText(dataRow[1]);
					}
					if(lineNum>tNRows)
						addRows(1);
					
					/*tr = (TableRow) templateTable.findViewWithTag(("ta"+String.valueOf(lineNum)));
					met = (MyEditText) tr.findViewWithTag("ingredient");
					met.setText(dataRow[2]);
					met = (MyEditText) tr.findViewWithTag("x");
					met.setText(dataRow[3]);
					met = (MyEditText) tr.findViewWithTag("y");
					met.setText(dataRow[4]);
					met = (MyEditText) tr.findViewWithTag("z");
					met.setText(dataRow[5]);*/
					rowholders.get(lineNum-1).ingredient.setText(dataRow[2]);
					rowholders.get(lineNum-1).x.setText(String.format("%.3f", Float.valueOf(dataRow[3])));
					rowholders.get(lineNum-1).y.setText(String.format("%.3f", Float.valueOf(dataRow[4])));
					rowholders.get(lineNum-1).z.setText(String.format("%.3f", Float.valueOf(dataRow[5])));
					
					sbTemplate.append(dataRow[0]);
					sbTemplate.append(",");
					sbTemplate.append(dataRow[1]);
					sbTemplate.append(",");
					sbTemplate.append(dataRow[2]);
					sbTemplate.append(",");
					sbTemplate.append(dataRow[3]);
					sbTemplate.append(",");
					sbTemplate.append(dataRow[4]);
					sbTemplate.append(",");
					sbTemplate.append(dataRow[5]);
					sbTemplate.append("\n");
					hasTemplate=false;
					hasName=false;
					hasIngredient=false;
					hasX=false;
					hasY=false;
					hasZ=false;
					hasFive=false;
				}
			
			}
			template.Inflate(sbTemplate.toString().substring(0, sbTemplate.toString().length()-1));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(br!=null){
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void fileSave(String fileString){
		File file = new File(fileString);
		FileOutputStream outputStream;
		//TableRow tr;
		//MyEditText met;
		StringBuilder sb = new StringBuilder();
		StringBuilder bobby = new StringBuilder();
		String comma = ",";
		
		try{
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
			
			outputStream = new FileOutputStream(file, false);
			
			for(int i=0;i<tNRows;i++) {
				//tr = (TableRow) templateTable.findViewWithTag(("ta"+String.valueOf(i+1)));
				//met = (MyEditText) tr.findViewWithTag("ingredient");
				//if(met.getText().toString().equals("")){
				if(rowholders.get(i).ingredient.getText().toString().equals("")) {
					break;
				}else{
					sb.append("TEMPLATE");
					sb.append(comma);
					sb.append(templateNameView.getText().toString());
					sb.append(comma);
					//met = (MyEditText) tr.findViewWithTag("ingredient");
					sb.append(rowholders.get(i).ingredient.getText().toString());
					sb.append(comma);
					//met = (MyEditText) tr.findViewWithTag("x");
					sb.append(rowholders.get(i).x.getText().toString());
					sb.append(comma);
					//met = (MyEditText) tr.findViewWithTag("y");
					sb.append(rowholders.get(i).y.getText().toString());
					sb.append(comma);
					//met = (MyEditText) tr.findViewWithTag("z");
					sb.append(rowholders.get(i).z.getText().toString());
					sb.append("\r\n");
				}
				outputStream.write(sb.toString().getBytes());
				bobby.append(sb);
				sb.setLength(0);
				fileSpinner = (NoDefaultSpinner) findViewById(R.id.templatepicker);
				fileSpinner.setPrompt("SAVED");
			}
			
			outputStream.close();
			template.Inflate(bobby.toString());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean attachFile(){
		boolean goattach = false;
		//TableRow tr;
		//MyEditText met;
		StringBuilder sb = new StringBuilder();
		StringBuilder bobby = new StringBuilder();
		String comma = ",";
		int i = 0;
		while(i<tNRows){
			//tr = (TableRow) templateTable.findViewWithTag(("ta"+String.valueOf((i++)+1)));
			//met = (MyEditText) tr.findViewWithTag("ingredient");
			//if(met.getText().toString().equals("")){
			if(rowholders.get(i).ingredient.getText().toString().equals("")) {
				break;
			}else{
				sb.append("TEMPLATE");
				sb.append(comma);
				sb.append(templateNameView.getText().toString());
				sb.append(comma);
				//met = (MyEditText) tr.findViewWithTag("ingredient");
				sb.append(rowholders.get(i).ingredient.getText().toString());
				sb.append(comma);
				//met = (MyEditText) tr.findViewWithTag("x");
				if(rowholders.get(i).x.getText().toString().equals(""))
					sb.append("0");
				else
					sb.append(rowholders.get(i).x.getText().toString());
				
				sb.append(comma);
				//met = (MyEditText) tr.findViewWithTag("y");
				//if(met.getText().toString().equals(""))
				if(rowholders.get(i).y.getText().toString().equals(""))
					sb.append("0");
				else
					sb.append(rowholders.get(i).y.getText().toString());
				
				sb.append(comma);
				//met = (MyEditText) tr.findViewWithTag("z");
				if(rowholders.get(i).z.getText().toString().equals(""))
					sb.append("0");
				else
					sb.append(rowholders.get(i).z.getText().toString());
				
				sb.append("\r\n");
			}
			bobby.append(sb);
			sb.setLength(0);
			i++;
		}
		if(i>0){
			template.Inflate(bobby.toString());
			goattach = true;
		}
			
		return goattach;
	}
	
	public void spinnerReset(){
		fileSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {
						
						if(parent.getItemAtPosition(pos).toString().equals("OPEN")){
							pickFile();
						}else if(parent.getItemAtPosition(pos).toString().equals("NEW")){
							newFile();
						}else if(parent.getItemAtPosition(pos).toString().equals("SAVE")){
							String pike = templateFileView.getText().toString();
							String sext = getMyExtension(pike);
							if(sext.equals("txt")||sext.equals("csv")){
								if(templateNameView.getText().toString().length()!=0){
									//TODO: Validate UI form
									fileSave(templateFileView.getText().toString());
								}else{
									Toast.makeText(getApplication(), "please name the Template", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getApplicationContext(), "please use \"txt\" or \"csv\" file formate",Toast.LENGTH_SHORT).show();
							}
						}else if(parent.getItemAtPosition(pos).toString().equals("ATTACH")){
							if(templateNameView.getText().toString().length()!=0){
								//TODO: Validate UI form
								
								if(attachFile()){
									getIntent().putExtra("attachtemplate", "true");
									getIntent().putExtra("template",template.makeString());
									setResult(RESULT_OK,getIntent());
									finish();
								}else{
									Toast.makeText(getApplication(), "please use at least one Ingredient", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getApplication(), "please name the Template", Toast.LENGTH_SHORT).show();
							}
							fileSpinner.setAdapter(fileSpinner.getAdapter());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {	}
				});
	}
	
	static class RowHolder{
		TableRow	tr;
		
		TextView	no;
		MyEditText	ingredient;
		MyEditText	x;
		MyEditText	y;
		MyEditText	z;
	}
	
}
