package com.nwags.BetaBot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.nwags.BetaBot.AddRecipeActivity.RowHolder;
import com.nwags.BetaBot.Support.Command;
import com.nwags.BetaBot.Support.MixBook;
import com.nwags.BetaBot.Support.Recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AddRecipeActivity extends Activity{
	public static final String TAG = "AddRecipeActivity";
	public static EditText arNameView;
	
	public static SharedPreferences settings;
	public static Context mContext;
	public static boolean debug;
	
	public static final int REQUEST_ADDRECIPE = 6;
	public static TableLayout arTable;
	public static TableLayout arNoTable;
	public static int arNRows;
	public static MixBook mixbook;
	public static Recipe recipe;
	public static Command command;
	
	public static String mString=null;
	public static String nString=null;
	public static String aString=null;
	public static String fString=null;
	
	public static NoDefaultSpinner fileSpinner;
	public static NoDefaultSpinner pipetteSpinner;
	public static HorizontalScrollView HSV;
	
	public static Typeface tf;
	
	public static boolean pipettingHide = true;
	public static boolean autosHide = true;
	public static boolean offsetsHide = true;
	public static boolean flowHide = true;
	public static boolean conditionalsHide = true;
	public static boolean miscHide = true;
	
	public static TextView tvLabel;
	
	public static int conversion=0;
	public static Stack<RowHolder> rowholders=null;
	public static LabelHolder labelholder = null;
	
	public static ArrayAdapter<String> ingredientAA;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED, getIntent());
		setContentView(R.layout.addrecipe);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		arNRows = 0;
		
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mContext = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		Log.d(TAG,"debug: "+debug);
		if(mixbook==null)
			mixbook = new MixBook();
		else
			mixbook.clear();
		
		if(recipe==null)
			recipe = new Recipe();
		else
			recipe.clear();
		
		mString = getIntent().getStringExtra("mixbook");
		nString = getIntent().getStringExtra("name");
		aString = getIntent().getStringExtra("action");
		fString = getIntent().getStringExtra("file");
		
		mixbook.clear();
		mixbook.Inflate(mString);
		recipe.clear();
		recipe = mixbook.get(nString);
		
		int q=0;
		ArrayList<String> ingredientList = new ArrayList<String>();
		ingredientList.add("");
		while(q<mixbook.getTemplate().size()){
			ingredientList.add(mixbook.getTemplate().getIngredient(q++));
		}
		ingredientAA = new ArrayAdapter<String>(this, R.layout.spinner_item_2, ingredientList);
		
		arTable = (TableLayout) findViewById(R.id.arTable);
		arNoTable = (TableLayout) findViewById(R.id.arNoTable);
		if(labelholder==null)
			labelholder = new LabelHolder();
		
		if(rowholders==null)
			rowholders = new Stack<RowHolder>();
		else
			rowholders.clear();
		
		fileSpinner = (NoDefaultSpinner) findViewById(R.id.savoy);
		fileSpinner.setPrompt("SAVE");
		arNameView = (EditText) findViewById(R.id.recipeName);
		pipetteSpinner = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
		addRows(10);
		
		labelholder.misc.depth = (TextView) findViewById(R.id.depth_label);
		labelholder.misc.zspeed = (TextView) findViewById(R.id.zspeed_label);
		labelholder.misc.suction = (TextView) findViewById(R.id.suction_label);
		labelholder.misc.suction_timer = (TextView) findViewById(R.id.suction_timer_label);
		labelholder.misc.grip = (TextView) findViewById(R.id.grip_label);
		labelholder.misc.grip_timer = (TextView) findViewById(R.id.grip_timer_label);
		
		labelholder.pip.aspspeed = (TextView) findViewById(R.id.aspspeed_label);
		labelholder.pip.blowout = (TextView) findViewById(R.id.blowout_label);
		labelholder.pip.mix = (TextView) findViewById(R.id.mix_label);
		labelholder.pip.droptip = (TextView) findViewById(R.id.droptip_label);
		
		labelholder.aut.ary = (TextView) findViewById(R.id.ay_label);
		labelholder.aut.arz = (TextView) findViewById(R.id.az_label);
		labelholder.aut.home = (TextView) findViewById(R.id.home_label);
		
		labelholder.off.oy = (TextView) findViewById(R.id.oy_label);
		labelholder.off.oz = (TextView) findViewById(R.id.oz_label);
		
		labelholder.flo.rowb = (TextView) findViewById(R.id.rowb_label);
		labelholder.flo.delay = (TextView) findViewById(R.id.delay_label);
		labelholder.flo.times = (TextView) findViewById(R.id.times_label);
		
		labelholder.con.condition = (TextView) findViewById(R.id.condition_label);
		labelholder.con.criterion = (TextView) findViewById(R.id.criterion_label);
		labelholder.con.negative = (TextView) findViewById(R.id.negative_label);
		
		
		
		labelholder.misc.depth.setVisibility(View.GONE);
		labelholder.misc.zspeed.setVisibility(View.GONE);
		labelholder.misc.suction.setVisibility(View.GONE);
		labelholder.misc.suction_timer.setVisibility(View.GONE);
		labelholder.misc.grip.setVisibility(View.GONE);
		labelholder.misc.grip_timer.setVisibility(View.GONE);
		
		labelholder.pip.aspspeed.setVisibility(View.GONE);
		labelholder.pip.blowout.setVisibility(View.GONE);
		labelholder.pip.mix.setVisibility(View.GONE);
		labelholder.pip.droptip.setVisibility(View.GONE);
		
		labelholder.aut.ary.setVisibility(View.GONE);
		labelholder.aut.arz.setVisibility(View.GONE);
		labelholder.aut.home.setVisibility(View.GONE);
		
		labelholder.off.oy.setVisibility(View.GONE);
		labelholder.off.oz.setVisibility(View.GONE);
		
		labelholder.flo.rowb.setVisibility(View.GONE);
		labelholder.flo.delay.setVisibility(View.GONE);
		labelholder.flo.times.setVisibility(View.GONE);
		
		labelholder.con.condition.setVisibility(View.GONE);
		labelholder.con.criterion.setVisibility(View.GONE);
		labelholder.con.negative.setVisibility(View.GONE);
		
		//tvLabel = (TextView) findViewById(R.id.speed_label);
		//tvLabel.setVisibility(View.GONE);
		/*tvLabel = (TextView) findViewById(R.id.depth_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.zspeed_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.suction_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.suction_timer_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.grip_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.grip_timer_label);
		tvLabel.setVisibility(View.GONE);
		
		
		//tvLabel = (TextView) findViewById(R.id.aspirate_label);
		//tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.aspspeed_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.blowout_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.mix_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.droptip_label);
		tvLabel.setVisibility(View.GONE);
		//tvLabel = (TextView) findViewById(R.id.ax_label);
		//tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.ay_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.az_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.home_label);
		tvLabel.setVisibility(View.GONE);
		//tvLabel = (TextView) findViewById(R.id.ox_label);
		//tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.oy_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.oz_label);
		tvLabel.setVisibility(View.GONE);
		//tvLabel = (TextView) findViewById(R.id.rowa_label);
		//tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.rowb_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.delay_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.times_label);
		tvLabel.setVisibility(View.GONE);
		//tvLabel = (TextView) findViewById(R.id.sensor_label);
		//tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.condition_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.criterion_label);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.negative_label);
		tvLabel.setVisibility(View.GONE);*/
		
		tvLabel = (TextView) findViewById(R.id.misc_tab_open);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.pipetting_tab_open);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.autos_tab_open);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.offs_tab_open);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.flow_tab_open);
		tvLabel.setVisibility(View.GONE);
		tvLabel = (TextView) findViewById(R.id.conditionals_tab_open);
		tvLabel.setVisibility(View.GONE);
		
		
		HSV = (HorizontalScrollView) findViewById(R.id.hstwo);
		HSV.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				v.requestFocusFromTouch();
				if(v.getClass().equals(MyEditText.class)){
					Toast.makeText(AddRecipeActivity.this, "WHOA!",Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		
		spinnerReset();
		
		
		pipetteReset();
		
		if(aString.equals("ADDRECIPE")) {
			
		} else if(aString.equals("EDITRECIPE")) {
			
			q = 0;
			//TableRow tr;
			//TableRow tr_no;
			NoDefaultSpinner nds = new NoDefaultSpinner(this,0);
			//MyEditText met;
			//CheckBox cb;
			
			arNameView.setText(recipe.getName());
			
			if(recipe.size()>arNRows)
				addRows(recipe.size()-arNRows);
			
			conversion = recipe.get(0).Conversion;
			nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
			String[] bedsArray = getResources().getStringArray(R.array.pipettor_size);
			ArrayAdapter<String> ab = new ArrayAdapter<String>(this, R.layout.spinner_item_2, bedsArray);
			nds.setAdapter(ab);
			nds.setSelection(conversion);
			pipetteReset();
			
			if(debug)
				Log.d(TAG, "conversion: "+conversion);
			
			while(q<arNRows){
				if(q<recipe.size()){
					RowHolder rh = new RowHolder();
					rh = rowholders.get(q);
					
					command = recipe.get(q);
					//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(1+(q)));//++
					//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(1+(q++)));
					
					//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					
					//int pos = aa.getPosition(command.Ingredient);
					//nds.setAdapter(aa);
					//nds.setSelection(pos);
					
					rh.ingredient.setSelection(ingredientAA.getPosition(command.Ingredient));
					
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					rh.speed.setGravity(Gravity.CENTER_HORIZONTAL);
					rh.speed.setBackgroundResource(R.drawable.cell_shape);
					
					//pos = command.Speed;
					//nds.setSelection(pos);
					rh.speed.setSelection(command.Speed);
					
					//met = (MyEditText) tr.findViewWithTag("depth");
					//met.setText(command.getDepthS());
					rh.misc.depth.setText(command.DepthS);
					
					//met = (MyEditText) tr.findViewWithTag("aspirate");
					//met.setText(command.getAspirateS());
					rh.aspirate.setText(command.AspirateS);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					rh.misc.zspeed.setGravity(Gravity.CENTER_HORIZONTAL);
					rh.misc.zspeed.setBackgroundResource(R.drawable.cell_shape);
					
					//pos = command.ZSpeed;
					//nds.setSelection(pos);
					rh.misc.zspeed.setSelection(command.ZSpeed);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					rh.pip.aspspeed.setGravity(Gravity.CENTER_HORIZONTAL);
					rh.pip.aspspeed.setBackgroundResource(R.drawable.cell_shape);
					
					//pos = command.AspSpeed;
					//nds.setSelection(pos);
					rh.pip.aspspeed.setSelection(command.AspSpeed);
					
					//cb = (CheckBox) tr.findViewWithTag("blowout");
					//cb.setChecked(command.getBlowout());
					rh.pip.blowout.setChecked(command.Blowout);
					
					//cb = (CheckBox) tr.findViewWithTag("droptip");
					//cb.setChecked(command.getDropTip());
					rh.pip.droptip.setChecked(command.DropTip);
					
					//cb = (CheckBox) tr.findViewWithTag("suction");
					//cb.setChecked(command.getSuction());
					rh.misc.suction.setChecked(command.Suction);
					
					//met = (MyEditText) tr.findViewWithTag("suction_timer");
					//met.setText(command.getSuction_timerS());
					rh.misc.suction_timer.setText(command.Suction_timerS);
					
					//cb = (CheckBox) tr.findViewWithTag("echo");
					//cb.setChecked(command.getSuction());
					
					//cb = (CheckBox) tr.findViewWithTag("grip");
					//cb.setChecked(command.getGrip());
					rh.misc.grip.setChecked(command.Grip);
					
					//met = (MyEditText) tr.findViewWithTag("grip_timer");
					//met.setText(command.getGrip_timerS());
					rh.misc.grip_timer.setText(command.Grip_timerS);
					
					//cb = (CheckBox) tr.findViewWithTag("arx");
					//cb.setChecked(command.getAutoReturnX());
					rh.arx.setChecked(command.AutoReturnX);
					
					//cb = (CheckBox) tr.findViewWithTag("ary");
					//cb.setChecked(command.getAutoReturnY());
					rh.aut.ary.setChecked(command.AutoReturnY);
					
					//cb = (CheckBox) tr.findViewWithTag("arz");
					//cb.setChecked(command.getAutoReturnZ());
					rh.aut.arz.setChecked(command.AutoReturnZ);
					
					//cb = (CheckBox) tr.findViewWithTag("home");
					//cb.setChecked(command.getHome());
					rh.aut.home.setChecked(command.Home);
					
					//met = (MyEditText) tr.findViewWithTag("ox");
					//met.setText(command.getOffsetXS());
					rh.ox.setText(command.OffsetXS);
					
					//met = (MyEditText) tr.findViewWithTag("oy");
					//met.setText(command.getOffsetYS());
					rh.off.oy.setText(command.OffsetYS);
					
					//met = (MyEditText) tr.findViewWithTag("oz");
					//met.setText(command.getOffsetZS());
					rh.off.oz.setText(command.OffsetZS);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
					//pos = command.RowA;
					//nds.setSelection(pos);
					rh.rowa.setSelection(command.RowA);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
					//pos = command.getRowB();
					//nds.setSelection(pos);
					rh.flo.rowb.setSelection(command.RowB);
					
					//met = (MyEditText) tr.findViewWithTag("delay");
					//met.setText(command.getDelayS());
					rh.flo.delay.setText(command.DelayS);
					
					//met = (MyEditText) tr.findViewWithTag("times");
					//met.setText(command.getTimesS());
					rh.flo.times.setText(command.TimesS);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					rh.sensor.setGravity(Gravity.CENTER_HORIZONTAL);
					rh.sensor.setBackgroundResource(R.drawable.cell_shape);
					
					//pos = command.getSensor();
					//nds.setSelection(pos);
					rh.sensor.setSelection(command.Sensor);
					
					//met = (MyEditText) tr.findViewWithTag("condition");
					//met.setText(command.getConditionS());
					rh.con.condition.setText(command.ConditionS);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					rh.con.criterion.setGravity(Gravity.CENTER_HORIZONTAL);
					rh.con.criterion.setBackgroundResource(R.drawable.cell_shape);
					
					//pos = command.getCriterion();
					//nds.setSelection(pos);
					rh.con.criterion.setSelection(command.Criterion);
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
					//nds.setGravity(Gravity.CENTER_HORIZONTAL);
					//nds.setBackgroundResource(R.drawable.cell_shape);
					rh.con.negative.setGravity(Gravity.CENTER_HORIZONTAL);
					rh.con.negative.setBackgroundResource(R.drawable.cell_shape);
					
					//pos = command.getNegative();
					//nds.setSelection(pos);
					rh.con.negative.setSelection(command.Negative);
					
					//cb = (CheckBox) tr.findViewWithTag("trace");
					//cb.setChecked(command.getTrace());
					rh.trace.setChecked(command.Trace);
				
				} else {//no longer necessary
					//just set ingredients
					/*
					tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(1+(q++)));
					
					nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
					nds.setGravity(Gravity.CENTER_HORIZONTAL);
					nds.setBackgroundResource(R.drawable.cell_shape);
					
					nds.setAdapter(aa);
					*/	
				}
				q++;
			}
		}
	}
	
	private boolean addRows(int n){
		boolean result = false;
		//TableRow tr;
		//TextView tv;
		ArrayAdapter<String> adapterRows;
		//MyEditText met;
		NoDefaultSpinner nds = new NoDefaultSpinner(this,0);
		nds.setBackgroundResource(R.drawable.cell_shape);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//CheckBox cb;
		
		for(int i=0;i<n;i++){
			++arNRows;
			
			ArrayList<String> funky  = new ArrayList<String>();
			ArrayList<String> chunky = new ArrayList<String>();
			chunky.add("");
			chunky.add("Hold");
			chunky.add("Home");
			chunky.add("End");
			funky.add("");
			for(int j=0;j<(arNRows-1);j++){
				
				funky.add(String.valueOf(j+1));
				chunky.add(String.valueOf(j+1));
			}
			RowHolder	holder  = new RowHolder();
			
			holder.trNO = (TableRow)inflater.inflate(R.layout.ar_no, null);
			holder.trNO.setTag("trNO"+String.valueOf(arNRows));
			arNoTable.addView(holder.trNO);
			
			holder.no = (TextView) holder.trNO.findViewWithTag("no");
			holder.no.setText(String.valueOf(arNRows));
			
			
			holder.tr = (TableRow)inflater.inflate(R.layout.ar_row, null);
			holder.tr.setTag("tr"+String.valueOf(arNRows));
			arTable.addView(holder.tr);
			
			
			holder.misc.depth 			= (MyEditText) holder.tr.findViewWithTag("depth");
			holder.misc.grip 			= (CheckBox) holder.tr.findViewWithTag("grip");
			holder.misc.grip_timer 		= (MyEditText) holder.tr.findViewWithTag("grip_timer");
			holder.misc.suction 		= (CheckBox) holder.tr.findViewWithTag("suction");
			holder.misc.suction_timer  	= (MyEditText) holder.tr.findViewWithTag("suction_timer");
			holder.misc.zspeed			= (NoDefaultSpinner) holder.tr.findViewWithTag("zspeed");
			
			holder.pip.aspspeed			= (NoDefaultSpinner) holder.tr.findViewWithTag("aspspeed");
			holder.pip.blowout			= (CheckBox) holder.tr.findViewWithTag("blowout");
			holder.pip.droptip			= (CheckBox) holder.tr.findViewWithTag("droptip");
			holder.pip.mix				= (MyEditText) holder.tr.findViewWithTag("mix");
			
			holder.aut.ary				= (CheckBox) holder.tr.findViewWithTag("ary");
			holder.aut.arz				= (CheckBox) holder.tr.findViewWithTag("arz");
			holder.aut.home				= (CheckBox) holder.tr.findViewWithTag("home");
			
			holder.off.oy				= (MyEditText) holder.tr.findViewWithTag("oy");
			holder.off.oz				= (MyEditText) holder.tr.findViewWithTag("oz");
			
			holder.flo.delay			= (MyEditText) holder.tr.findViewWithTag("delay");
			holder.flo.rowb				= (NoDefaultSpinner) holder.tr.findViewWithTag("rowb");
			holder.flo.times			= (MyEditText) holder.tr.findViewWithTag("times");
			
			holder.con.condition 		= (MyEditText) holder.tr.findViewWithTag("condition");
			holder.con.criterion		= (NoDefaultSpinner) holder.tr.findViewWithTag("criterion");
			holder.con.negative			= (NoDefaultSpinner) holder.tr.findViewWithTag("negative");
			
			
			holder.ingredient	= (NoDefaultSpinner) holder.trNO.findViewWithTag("ingredient");
			holder.arx			= (CheckBox) holder.tr.findViewWithTag("arx");
			holder.aspirate		= (MyEditText) holder.tr.findViewWithTag("aspirate");
			holder.ox			= (MyEditText) holder.tr.findViewWithTag("ox");
			holder.rowa			= (NoDefaultSpinner) holder.tr.findViewWithTag("rowa");
			holder.sensor		= (NoDefaultSpinner) holder.tr.findViewWithTag("sensor");
			holder.speed		= (NoDefaultSpinner) holder.tr.findViewWithTag("speed");
			holder.trace		= (CheckBox) holder.tr.findViewWithTag("trace");
			
			if(pipettingHide){
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//met.setVisibility(View.GONE);
				/*nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				nds.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("mix");
				met.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("blowout");
				cb.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("droptip");
				cb.setVisibility(View.GONE);*/
				holder.pip.aspspeed.setVisibility(View.GONE);
				holder.pip.mix.setVisibility(View.GONE);
				holder.pip.blowout.setVisibility(View.GONE);
				holder.pip.droptip.setVisibility(View.GONE);
			}
			if(autosHide){
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//cb.setVisibility(View.GONE);
				/*cb = (CheckBox) tr.findViewWithTag("ary");
				cb.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("arz");
				cb.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("home");
				cb.setVisibility(View.GONE);*/
				holder.aut.ary.setVisibility(View.GONE);
				holder.aut.arz.setVisibility(View.GONE);
				holder.aut.home.setVisibility(View.GONE);
			}
			if(offsetsHide){
				//met = (MyEditText) tr.findViewWithTag("ox");
				//met.setVisibility(View.GONE);
				/*met = (MyEditText) tr.findViewWithTag("oy");
				met.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("oz");
				met.setVisibility(View.GONE);*/
				holder.off.oy.setVisibility(View.GONE);
				holder.off.oz.setVisibility(View.GONE);
			}
			if(flowHide){
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//nds.setVisibility(View.GONE);
				/*nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				nds.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("delay");
				met.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("times");
				met.setVisibility(View.GONE);*/
				holder.flo.delay.setVisibility(View.GONE);
				holder.flo.rowb.setVisibility(View.GONE);
				holder.flo.times.setVisibility(View.GONE);
			}
			if(conditionalsHide){
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//nds.setVisibility(View.GONE);
				/*met = (MyEditText) tr.findViewWithTag("condition");
				met.setVisibility(View.GONE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				nds.setVisibility(View.GONE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				nds.setVisibility(View.GONE);*/
				holder.con.condition.setVisibility(View.GONE);
				holder.con.criterion.setVisibility(View.GONE);
				holder.con.negative.setVisibility(View.GONE);
			}
			
			if(miscHide){
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//nds.setVisibility(View.GONE);
				/*met = (MyEditText) tr.findViewWithTag("depth");
				met.setVisibility(View.GONE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				nds.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("suction");
				cb.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("suction_timer");
				met.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("grip");
				cb.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("grip_timer");
				met.setVisibility(View.GONE);*/
				holder.misc.depth.setVisibility(View.GONE);
				holder.misc.grip.setVisibility(View.GONE);
				holder.misc.grip_timer.setVisibility(View.GONE);
				holder.misc.suction.setVisibility(View.GONE);
				holder.misc.suction_timer.setVisibility(View.GONE);
				holder.misc.zspeed.setVisibility(View.GONE);
			}
			
			holder.ingredient.setGravity(Gravity.CENTER_HORIZONTAL);
			holder.ingredient.setAdapter(ingredientAA);
			holder.ingredient.setBackgroundResource(R.drawable.cell_shape);
			//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
			
			adapterRows = new ArrayAdapter<String>(this, R.layout.spinner_item_2, funky);
				adapterRows.notifyDataSetChanged();
			
			//nds.setAdapter(adapterRows);
			holder.rowa.setAdapter(adapterRows);
				
				
			//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
			
			adapterRows = new ArrayAdapter<String>(this, R.layout.spinner_item_2, funky);
				adapterRows.notifyDataSetChanged();
			
			//nds.setAdapter(adapterRows);
			holder.flo.rowb.setAdapter(adapterRows);
				
			//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
			
			adapterRows = new ArrayAdapter<String>(this, R.layout.spinner_item_2, chunky);
				adapterRows.notifyDataSetChanged();
			
			//nds.setAdapter(adapterRows);
			holder.con.negative.setAdapter(adapterRows);
			
			//cb = (CheckBox) tr.findViewWithTag("trace");
			//cb.setChecked(true);
			holder.trace.setChecked(true);
			
			//cb = (CheckBox) tr.findViewWithTag("arz");
			//cb.setChecked(true);
			holder.aut.arz.setChecked(true);
			
			rowholders.push(holder);
		}
		pipetteReset();
		return result;
	}
	
	private boolean deleteRows(int n) {
		boolean result = false;
		//TableRow tr;
		for(int i=0;i<n;i++){
			//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(arNRows));
			//arTable.removeView(tr);
			//tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(arNRows));
			arNoTable.removeView(rowholders.peek().trNO);
			arTable.removeView(rowholders.pop().tr);
			--arNRows;
			
		}
		
		return result;
	}
	
	private boolean clearRows(int insert, int n) {
		boolean result = false;
		
		
		return result;
	}
	
	private boolean insertRows(int insert, int n) {
		boolean result = false;
		//addRows(n);
		
		//NoDefaultSpinner ndsA = new NoDefaultSpinner(this,0);
		//NoDefaultSpinner ndsB = new NoDefaultSpinner(this,0);
		//TableRow trA;
		//TableRow trB;
		//TableRow trNoA;
		//TableRow trNoB;
		//MyEditText metA;
		//MyEditText metB;
		//CheckBox cbA;
		//CheckBox cbB;
		
		Stack<RowHolder> tempHolder = new Stack<RowHolder>();
		
		while(arNRows>=insert) {
			arNRows--;
			tempHolder.push(rowholders.pop());
			arNoTable.removeView(tempHolder.peek().trNO);
			arTable.removeView(tempHolder.peek().tr);
		}
		addRows(n);
		
		while(!tempHolder.isEmpty()) {
			arNRows++;
			RowHolder rh = new RowHolder();
			rh = tempHolder.pop();
			rh.no.setText(String.valueOf(arNRows));
			rh.tr.setTag("tr"+String.valueOf(arNRows));
			rh.trNO.setTag("trNO"+String.valueOf(arNRows));
			arTable.addView(rh.tr);
			arNoTable.addView(rh.trNO);
			rowholders.push(rh);
		}
		
		/*
		for(int i = arNRows-n;i>=insert;i--){
			
			trA = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(i));
			trB = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(i+n));
			
			trNoA = (TableRow) arNoTable.findViewWithTag("tr_no"+String.valueOf(i));
			trNoB = (TableRow) arNoTable.findViewWithTag("tr_no"+String.valueOf(i+n));
			
			
			ndsA = (NoDefaultSpinner) trNoA.findViewWithTag("ingredient");
			int pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trNoB.findViewWithTag("ingredient");
			ndsB.setSelection(pos);
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("speed");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("speed");
			ndsB.setSelection(pos);
			
			metA = (MyEditText) trA.findViewWithTag("depth");
			metB = (MyEditText) trB.findViewWithTag("depth");
			metB.setText(metA.getText().toString());
			
			metA = (MyEditText) trA.findViewWithTag("aspirate");
			metB = (MyEditText) trB.findViewWithTag("aspirate");
			metB.setText(metA.getText().toString());
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("aspspeed");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("aspspeed");
			ndsB.setSelection(pos);
			
			metA = (MyEditText) trA.findViewWithTag("mix");
			metB = (MyEditText) trB.findViewWithTag("mix");
			metB.setText(metA.getText().toString());
			
			cbA = (CheckBox) trA.findViewWithTag("blowout");
			cbB = (CheckBox) trB.findViewWithTag("blowout"); 
			cbB.setChecked(cbA.isChecked());
			
			cbA = (CheckBox) trA.findViewWithTag("droptip");
			cbB = (CheckBox) trB.findViewWithTag("droptip");
			cbB.setChecked(cbA.isChecked());
			
			cbA = (CheckBox) trA.findViewWithTag("suction");
			cbB = (CheckBox) trB.findViewWithTag("suction");
			cbB.setChecked(cbA.isChecked());
			
			metA = (MyEditText) trA.findViewWithTag("suction_timer");
			metB = (MyEditText) trB.findViewWithTag("suction_timer");
			metB.setText(metA.getText().toString());
			
			cbA = (CheckBox) trA.findViewWithTag("echo");
			cbB = (CheckBox) trB.findViewWithTag("echo");
			cbB.setChecked(cbA.isChecked());
			
			cbA = (CheckBox) trA.findViewWithTag("grip");
			cbB = (CheckBox) trB.findViewWithTag("grip");
			cbB.setChecked(cbA.isChecked());
			
			metA = (MyEditText) trA.findViewWithTag("grip_timer");
			metB = (MyEditText) trB.findViewWithTag("grip_timer");
			metB.setText(metA.getText().toString());
			
			cbA = (CheckBox) trA.findViewWithTag("arx");
			cbB = (CheckBox) trB.findViewWithTag("arx");
			cbB.setChecked(cbA.isChecked());
			
			cbA = (CheckBox) trA.findViewWithTag("ary");
			cbB = (CheckBox) trB.findViewWithTag("ary");
			cbB.setChecked(cbA.isChecked());
			
			cbA = (CheckBox) trA.findViewWithTag("arz");
			cbB = (CheckBox) trB.findViewWithTag("arz");
			cbB.setChecked(cbA.isChecked());
			
			cbA = (CheckBox) trA.findViewWithTag("home");
			cbB = (CheckBox) trB.findViewWithTag("home");
			cbB.setChecked(cbA.isChecked());
			
			metA = (MyEditText) trA.findViewWithTag("ox");
			metB = (MyEditText) trB.findViewWithTag("ox");
			metB.setText(metA.getText().toString());
			
			metA = (MyEditText) trA.findViewWithTag("oy");
			metB = (MyEditText) trB.findViewWithTag("oy");
			metB.setText(metA.getText().toString());
			
			metA = (MyEditText) trA.findViewWithTag("oz");
			metB = (MyEditText) trB.findViewWithTag("oz");
			metB.setText(metA.getText().toString());
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("rowa");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("rowa");
			ndsB.setSelection(pos);
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("rowb");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("rowb");
			ndsB.setSelection(pos);
			
			metA = (MyEditText) trA.findViewWithTag("delay");
			metB = (MyEditText) trB.findViewWithTag("delay");
			metB.setText(metA.getText().toString());
			
			metA = (MyEditText) trA.findViewWithTag("times");
			metB = (MyEditText) trB.findViewWithTag("times");
			metB.setText(metA.getText().toString());
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("sensor");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("sensor");
			ndsB.setSelection(pos);
			
			metA = (MyEditText) trA.findViewWithTag("condition");
			metB = (MyEditText) trB.findViewWithTag("condition");
			metB.setText(metA.getText().toString());
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("criterion");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("criterion");
			ndsB.setSelection(pos);
			
			ndsA = (NoDefaultSpinner) trA.findViewWithTag("negative");
			pos = ndsA.getSelectedItemPosition();
			ndsB = (NoDefaultSpinner) trB.findViewWithTag("negative");
			ndsB.setSelection(pos);
			
			cbA = (CheckBox) trA.findViewWithTag("trace");
			cbB = (CheckBox) trB.findViewWithTag("trace");
			cbB.setChecked(cbA.isChecked());
		}*/
		
		return result;
	}
	
	private class SaveOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			if(debug)
				Log.d(TAG,"SAVE BUTTON CLICKED");
			if(aString.equals("ADDRECIPE")){
				if(arNameView.getText().toString().equals("")){
					Toast.makeText(AddRecipeActivity.this, "Please give this recipe a name", Toast.LENGTH_SHORT).show();
					return;
				}
				int rt=0;
				//TableRow tr;
				//TableRow tr_no;
				//TextView tv;
				NoDefaultSpinner nds;
				//MyEditText met;
				//CheckBox cb;
				if(recipe!=null)
					recipe.clear();
				else
					recipe = new Recipe();
					
				nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
				if(nds.getSelectedItem()==null)
					conversion = 0;
				else
					conversion = nds.getSelectedItemPosition();
				
				int zero = 0;
				
				while(rt<arNRows) {
					RowHolder rv = new RowHolder();
					rv = rowholders.get(rt++);
					
					//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++rt));
					//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
					//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
					//if(nds.getSelectedItem()==null||nds.getSelectedItemPosition()==0)
					//	break;
					if(rv.ingredient.getSelectedItem()==null||
					   rv.ingredient.getSelectedItemPosition()==0)
							break;
					
					
					if(rt==1)
						recipe.setName(arNameView.getText().toString());
					int number;
					String ingredient;
					int speed;
					float depth;
					int zspeed;
					float aspirate;
					int aspspeed;
					boolean blowout;
					boolean droptip;
					boolean suction;
					boolean echo;
					boolean grip;
					boolean autoreturnx;
					boolean autoreturny;
					boolean autoreturnz;
					boolean home;
					float offsetx;
					float offsety;
					float offsetz;
					int rowa;
					int rowb;
					float delay;
					int times;
					int sensor;
					float condition;
					int criterion;
					int negative;
					boolean trace;					
					int mix;
					float grip_timer;
					float suction_timer;
					
					speed = aspspeed = zspeed = 0;
					
					//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
					//ingredient = nds.getSelectedItem().toString();
					ingredient = rv.ingredient.getSelectedItem().toString();
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
					//speed = nds.getSelectedItemPosition();
					speed = rv.speed.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("depth");
					//if(met.getText().toString().equals(""))
					if(rv.misc.depth.getText().toString().equals(""))	
						depth = 0f;
					else
						depth = Float.parseFloat(rv.misc.depth.getText().toString());
						//depth = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
					//zspeed = nds.getSelectedItemPosition();
					zspeed = rv.misc.zspeed.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("aspirate");
					//if(met.getText().toString().equals(""))
					if(rv.aspirate.getText().toString().equals(""))	
						aspirate = 0f;
					else
						aspirate = Float.parseFloat(rv.aspirate.getText().toString());
						//aspirate = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
					//aspspeed = nds.getSelectedItemPosition();
					aspspeed = rv.pip.aspspeed.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("mix");
					//if(met.getText().toString().equals(""))
					if(rv.pip.mix.getText().toString().equals(""))
						mix = 0;
					else
						mix = Integer.parseInt(rv.pip.mix.getText().toString());
						//mix = Integer.parseInt(met.getText().toString());
					
					//cb = (CheckBox) tr.findViewWithTag("blowout");
					//blowout = cb.isChecked();
					blowout = rv.pip.blowout.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("droptip");
					//droptip = cb.isChecked();
					droptip = rv.pip.droptip.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("suction");
					//suction = cb.isChecked();
					suction = rv.misc.suction.isChecked();
					
					//met = (MyEditText) tr.findViewWithTag("suction_timer");
					//if(met.getText().toString().equals(""))
					if(rv.misc.suction_timer.getText().toString().equals(""))
						suction_timer = 0f;
					else
						suction_timer = Float.parseFloat(rv.misc.suction_timer.getText().toString());
						//suction_timer = Float.parseFloat(met.getText().toString());
					
					//cb = (CheckBox) tr.findViewWithTag("echo");
					//echo = cb.isChecked();
					echo = false;
					
					//cb = (CheckBox) tr.findViewWithTag("grip");
					//grip = cb.isChecked();
					grip = rv.misc.grip.isChecked();
					
					//met = (MyEditText) tr.findViewWithTag("grip_timer");
					//if(met.getText().toString().equals(""))
					if(rv.misc.grip_timer.getText().toString().equals(""))
						grip_timer = 0f;
					else
						grip_timer = Float.parseFloat(rv.misc.grip_timer.getText().toString());
						//grip_timer = Float.parseFloat(met.getText().toString());
					
					//cb = (CheckBox) tr.findViewWithTag("arx");
					//autoreturnx = cb.isChecked();
					autoreturnx = rv.arx.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("ary");
					//autoreturny = cb.isChecked();
					autoreturny = rv.aut.ary.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("arz");
					//autoreturnz = cb.isChecked();
					autoreturnz = rv.aut.arz.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("home");
					//home = cb.isChecked();
					home = rv.aut.home.isChecked();
					
					//met = (MyEditText) tr.findViewWithTag("ox");
					//if(met.getText().toString().equals(""))
					if(rv.ox.getText().toString().equals(""))
						offsetx = 0f;
					else
						offsetx = Float.parseFloat(rv.ox.getText().toString());
						//offsetx = Float.parseFloat(met.getText().toString());
					
					//met = (MyEditText) tr.findViewWithTag("oy");
					//if(met.getText().toString().equals(""))
					if(rv.off.oy.getText().toString().equals(""))
						offsety = 0f;
					else
						offsety = Float.parseFloat(rv.off.oy.getText().toString());
						//offsety = Float.parseFloat(met.getText().toString());
					
					//met = (MyEditText) tr.findViewWithTag("oz");
					//if(met.getText().toString().equals(""))
					if(rv.off.oz.getText().toString().equals(""))
						offsetz = 0f;
					else
						offsetz = Float.parseFloat(rv.off.oz.getText().toString());
						//offsetz = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
					//if(nds.getSelectedItem()==null)
					if(rv.rowa.getSelectedItem()==null)
						rowa = zero;
					else
						rowa = Integer.parseInt(rv.rowa.getSelectedItem().toString());
						//rowa = Integer.parseInt(nds.getSelectedItem().toString());					
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
					//if(nds.getSelectedItem()==null)
					if(rv.flo.rowb.getSelectedItem()==null)
						rowb = zero;
					else
						rowb = Integer.parseInt(rv.flo.rowb.getSelectedItem().toString());
						//rowb = Integer.parseInt(nds.getSelectedItem().toString());
					
					//met = (MyEditText) tr.findViewWithTag("delay");
					//if(met.getText().toString().equals(""))
					if(rv.flo.delay.getText().toString().equals(""))
						delay = 0f;
					else
						delay = Float.parseFloat(rv.flo.delay.getText().toString());
						//delay = Float.parseFloat(met.getText().toString());
					
					//met = (MyEditText) tr.findViewWithTag("times");
					//if(met.getText().toString().equals(""))
					if(rv.flo.times.getText().toString().equals(""))
						times=zero;
					else
						times = Integer.parseInt(rv.flo.times.getText().toString());
						//times = Integer.parseInt(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
					//if(nds.getSelectedItem()==null)
					if(rv.sensor.getSelectedItem()==null)
						sensor = zero;
					else
						sensor = rv.sensor.getSelectedItemPosition();
						//sensor = nds.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("condition");
					//if(met.getText().toString().equals(""))
					if(rv.con.condition.getText().toString().equals(""))
						condition=zero;
					else
						condition = Float.parseFloat(rv.con.condition.getText().toString());
						//condition = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
					//if(nds.getSelectedItem()==null)
					if(rv.con.criterion.getSelectedItem()==null)
						criterion = zero;
					else
						criterion = rv.con.criterion.getSelectedItemPosition();
						//criterion = nds.getSelectedItemPosition();
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
					//if(nds.getSelectedItem()==null)
					if(rv.con.negative.getSelectedItem()==null)
						negative = zero;
					else
						negative = rv.con.negative.getSelectedItemPosition();
						//negative = nds.getSelectedItemPosition();
					
					//cb = (CheckBox) tr.findViewWithTag("trace");
					//trace = cb.isChecked();
					trace = rv.trace.isChecked();
					
					//tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
					//tv = (TextView) tr.findViewWithTag("no");
					//number = Integer.parseInt(tv.getText().toString());
					number = Integer.parseInt(rv.no.getText().toString());
					
					
					Command command = new Command(	number,		
													ingredient,
													speed,
													depth,
													zspeed,
													aspirate,
													aspspeed,
													blowout,
													droptip,
													suction,
													echo,
													grip,
													autoreturnx,
													autoreturny,
													autoreturnz,
													home,
													offsetx,
													offsety,
													offsetz,
													rowa,
													rowb,
													delay,
													times,
													sensor,
													condition,
													criterion,
													negative,
													trace,
													conversion,
													mix,
													grip_timer,
													suction_timer
																);
					
					recipe.add(command);
					
				}
				
				getIntent().putExtra("recipe", recipe.makeString());
				setResult(RESULT_OK,getIntent());
				finish();
			}else if(aString.equals("EDITRECIPE")){
				if(debug)
					Log.d(TAG,"Save-EDITRECIPE");
				if(arNameView.getText().toString().equals("")){
					Toast.makeText(AddRecipeActivity.this, "Please give this recipe a name", Toast.LENGTH_SHORT).show();
					return;
				}
				int rt=0;
				//TableRow tr;
				//TableRow tr_no;
				//TextView tv;
				NoDefaultSpinner nds;
				//MyEditText met;
				//CheckBox cb;
				if(recipe!=null)
					recipe.clear();
				else
					recipe = new Recipe();
					
				nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
				if(nds.getSelectedItem()==null)
					conversion = 0;
				else
					conversion = nds.getSelectedItemPosition();
				
				
				int zero = 0;
				
				while(rt<arNRows) {
					RowHolder rv = new RowHolder();
					rv = rowholders.get(rt++);
					
					//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++rt));
					//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
					//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
					//if(nds.getSelectedItem()==null||nds.getSelectedItemPosition()==0)
					//	break;
					if(rv.ingredient.getSelectedItem()==null||
					   rv.ingredient.getSelectedItemPosition()==0)
							break;
					
					
					if(rt==1)
						recipe.setName(arNameView.getText().toString());
					int number;
					String ingredient;
					int speed;
					float depth;
					int zspeed;
					float aspirate;
					int aspspeed;
					boolean blowout;
					boolean droptip;
					boolean suction;
					boolean echo;
					boolean grip;
					boolean autoreturnx;
					boolean autoreturny;
					boolean autoreturnz;
					boolean home;
					float offsetx;
					float offsety;
					float offsetz;
					int rowa;
					int rowb;
					float delay;
					int times;
					int sensor;
					float condition;
					int criterion;
					int negative;
					boolean trace;
					int mix;
					float grip_timer;
					float suction_timer;
					
					speed = aspspeed = zspeed = 0;
					
					//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
					//ingredient = nds.getSelectedItem().toString();
					ingredient = rv.ingredient.getSelectedItem().toString();
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
					//speed = nds.getSelectedItemPosition();
					speed = rv.speed.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("depth");
					//if(met.getText().toString().equals(""))
					if(rv.misc.depth.getText().toString().equals(""))	
						depth = 0f;
					else
						depth = Float.parseFloat(rv.misc.depth.getText().toString());
						//depth = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
					//zspeed = nds.getSelectedItemPosition();
					zspeed = rv.misc.zspeed.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("aspirate");
					//if(met.getText().toString().equals(""))
					if(rv.aspirate.getText().toString().equals(""))	
						aspirate = 0f;
					else
						aspirate = Float.parseFloat(rv.aspirate.getText().toString());
						//aspirate = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
					//aspspeed = nds.getSelectedItemPosition();
					aspspeed = rv.pip.aspspeed.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("mix");
					//if(met.getText().toString().equals(""))
					if(rv.pip.mix.getText().toString().equals(""))
						mix = 0;
					else
						mix = Integer.parseInt(rv.pip.mix.getText().toString());
						//mix = Integer.parseInt(met.getText().toString());
					
					//cb = (CheckBox) tr.findViewWithTag("blowout");
					//blowout = cb.isChecked();
					blowout = rv.pip.blowout.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("droptip");
					//droptip = cb.isChecked();
					droptip = rv.pip.droptip.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("suction");
					//suction = cb.isChecked();
					suction = rv.misc.suction.isChecked();
					
					//met = (MyEditText) tr.findViewWithTag("suction_timer");
					//if(met.getText().toString().equals(""))
					if(rv.misc.suction_timer.getText().toString().equals(""))
						suction_timer = 0f;
					else
						suction_timer = Float.parseFloat(rv.misc.suction_timer.getText().toString());
						//suction_timer = Float.parseFloat(met.getText().toString());
					
					//cb = (CheckBox) tr.findViewWithTag("echo");
					//echo = cb.isChecked();
					echo = false;
					
					//cb = (CheckBox) tr.findViewWithTag("grip");
					//grip = cb.isChecked();
					grip = rv.misc.grip.isChecked();
					
					//met = (MyEditText) tr.findViewWithTag("grip_timer");
					//if(met.getText().toString().equals(""))
					if(rv.misc.grip_timer.getText().toString().equals(""))
						grip_timer = 0f;
					else
						grip_timer = Float.parseFloat(rv.misc.grip_timer.getText().toString());
						//grip_timer = Float.parseFloat(met.getText().toString());
					
					//cb = (CheckBox) tr.findViewWithTag("arx");
					//autoreturnx = cb.isChecked();
					autoreturnx = rv.arx.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("ary");
					//autoreturny = cb.isChecked();
					autoreturny = rv.aut.ary.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("arz");
					//autoreturnz = cb.isChecked();
					autoreturnz = rv.aut.arz.isChecked();
					
					//cb = (CheckBox) tr.findViewWithTag("home");
					//home = cb.isChecked();
					home = rv.aut.home.isChecked();
					
					//met = (MyEditText) tr.findViewWithTag("ox");
					//if(met.getText().toString().equals(""))
					if(rv.ox.getText().toString().equals(""))
						offsetx = 0f;
					else
						offsetx = Float.parseFloat(rv.ox.getText().toString());
						//offsetx = Float.parseFloat(met.getText().toString());
					
					//met = (MyEditText) tr.findViewWithTag("oy");
					//if(met.getText().toString().equals(""))
					if(rv.off.oy.getText().toString().equals(""))
						offsety = 0f;
					else
						offsety = Float.parseFloat(rv.off.oy.getText().toString());
						//offsety = Float.parseFloat(met.getText().toString());
					
					//met = (MyEditText) tr.findViewWithTag("oz");
					//if(met.getText().toString().equals(""))
					if(rv.off.oz.getText().toString().equals(""))
						offsetz = 0f;
					else
						offsetz = Float.parseFloat(rv.off.oz.getText().toString());
						//offsetz = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
					//if(nds.getSelectedItem()==null)
					if(rv.rowa.getSelectedItem()==null)
						rowa = zero;
					else
						rowa = Integer.parseInt(rv.rowa.getSelectedItem().toString());
						//rowa = Integer.parseInt(nds.getSelectedItem().toString());					
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
					//if(nds.getSelectedItem()==null)
					if(rv.flo.rowb.getSelectedItem()==null)
						rowb = zero;
					else
						rowb = Integer.parseInt(rv.flo.rowb.getSelectedItem().toString());
						//rowb = Integer.parseInt(nds.getSelectedItem().toString());
					
					//met = (MyEditText) tr.findViewWithTag("delay");
					//if(met.getText().toString().equals(""))
					if(rv.flo.delay.getText().toString().equals(""))
						delay = 0f;
					else
						delay = Float.parseFloat(rv.flo.delay.getText().toString());
						//delay = Float.parseFloat(met.getText().toString());
					
					//met = (MyEditText) tr.findViewWithTag("times");
					//if(met.getText().toString().equals(""))
					if(rv.flo.times.getText().toString().equals(""))
						times=zero;
					else
						times = Integer.parseInt(rv.flo.times.getText().toString());
						//times = Integer.parseInt(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
					//if(nds.getSelectedItem()==null)
					if(rv.sensor.getSelectedItem()==null)
						sensor = zero;
					else
						sensor = rv.sensor.getSelectedItemPosition();
						//sensor = nds.getSelectedItemPosition();
					
					//met = (MyEditText) tr.findViewWithTag("condition");
					//if(met.getText().toString().equals(""))
					if(rv.con.condition.getText().toString().equals(""))
						condition=zero;
					else
						condition = Float.parseFloat(rv.con.condition.getText().toString());
						//condition = Float.parseFloat(met.getText().toString());
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
					//if(nds.getSelectedItem()==null)
					if(rv.con.criterion.getSelectedItem()==null)
						criterion = zero;
					else
						criterion = rv.con.criterion.getSelectedItemPosition();
						//criterion = nds.getSelectedItemPosition();
					
					//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
					//if(nds.getSelectedItem()==null)
					if(rv.con.negative.getSelectedItem()==null)
						negative = zero;
					else
						negative = rv.con.negative.getSelectedItemPosition();
						//negative = nds.getSelectedItemPosition();
					
					//cb = (CheckBox) tr.findViewWithTag("trace");
					//trace = cb.isChecked();
					trace = rv.trace.isChecked();
					
					//tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
					//tv = (TextView) tr.findViewWithTag("no");
					//number = Integer.parseInt(tv.getText().toString());
					number = Integer.parseInt(rv.no.getText().toString());
					
					Command command = new Command(	number,		
													ingredient,
													speed,
													depth,
													zspeed,
													aspirate,
													aspspeed,
													blowout,
													droptip,
													suction,
													echo,
													grip,
													autoreturnx,
													autoreturny,
													autoreturnz,
													home,
													offsetx,
													offsety,
													offsetz,
													rowa,
													rowb,
													delay,
													times,
													sensor,
													condition,
													criterion,
													negative,
													trace,
													conversion,
													mix,
													grip_timer,
													suction_timer
																);
					
					recipe.add(command);
					
				}
				
				mixbook.replace(nString, recipe);
				File file = new File(fString);
				FileOutputStream outputStream;
				try{
					outputStream = new FileOutputStream(file, false);
					outputStream.write(mixbook.makeString().getBytes());
					outputStream.close();
					if(debug){
						Log.d(TAG,"returning to RA");
						Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
					}
					getIntent().putExtra("mixbook", mixbook.makeString());
					setResult(RESULT_OK,getIntent());
					finish();
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}else{
				if(debug){
					Log.d(TAG,"NOTHING SELECTED!?!");
					Log.d(TAG,"aString = "+aString);
				}
			}
		}
		
	}
	
	private void SaveRecipe(){
		spinnerReset();
		if(debug)
			Log.d(TAG,"SAVE BUTTON CLICKED");
		if(aString.equals("ADDRECIPE")){
			if(arNameView.getText().toString().equals("")){
				Toast.makeText(AddRecipeActivity.this, "Please give this recipe a name", Toast.LENGTH_SHORT).show();
				return;
			}
			int rt=0;
			TableRow tr;
			TableRow tr_no;
			TextView tv;
			NoDefaultSpinner nds;
			//MyEditText met;
			//CheckBox cb;
			if(recipe!=null)
				recipe.clear();
			else
				recipe = new Recipe();
				
			nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
			if(nds.getSelectedItem()==null)
				conversion = 0;
			else
				conversion = nds.getSelectedItemPosition();
			
			int zero = 0;
			
			
			while(rt<arNRows) {
				RowHolder rv = new RowHolder();
				rv = rowholders.get(rt++);
				
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++rt));
				//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//if(nds.getSelectedItem()==null||nds.getSelectedItemPosition()==0)
				//	break;
				if(rv.ingredient.getSelectedItem()==null||
				   rv.ingredient.getSelectedItemPosition()==0)
						break;
				
				
				if(rt==1)
					recipe.setName(arNameView.getText().toString());
				int number;
				String ingredient;
				int speed;
				float depth;
				int zspeed;
				float aspirate;
				int aspspeed;
				boolean blowout;
				boolean droptip;
				boolean suction;
				boolean echo;
				boolean grip;
				boolean autoreturnx;
				boolean autoreturny;
				boolean autoreturnz;
				boolean home;
				float offsetx;
				float offsety;
				float offsetz;
				int rowa;
				int rowb;
				float delay;
				int times;
				int sensor;
				float condition;
				int criterion;
				int negative;
				boolean trace;
				int mix;
				float grip_timer;
				float suction_timer;
				
				speed = aspspeed = zspeed = 0;
				
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//ingredient = nds.getSelectedItem().toString();
				ingredient = rv.ingredient.getSelectedItem().toString();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//speed = nds.getSelectedItemPosition();
				speed = rv.speed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("depth");
				//if(met.getText().toString().equals(""))
				if(rv.misc.depth.getText().toString().equals(""))	
					depth = 0f;
				else
					depth = Float.parseFloat(rv.misc.depth.getText().toString());
					//depth = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				//zspeed = nds.getSelectedItemPosition();
				zspeed = rv.misc.zspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//if(met.getText().toString().equals(""))
				if(rv.aspirate.getText().toString().equals(""))	
					aspirate = 0f;
				else
					aspirate = Float.parseFloat(rv.aspirate.getText().toString());
					//aspirate = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				//aspspeed = nds.getSelectedItemPosition();
				aspspeed = rv.pip.aspspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("mix");
				//if(met.getText().toString().equals(""))
				if(rv.pip.mix.getText().toString().equals(""))
					mix = 0;
				else
					mix = Integer.parseInt(rv.pip.mix.getText().toString());
					//mix = Integer.parseInt(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("blowout");
				//blowout = cb.isChecked();
				blowout = rv.pip.blowout.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("droptip");
				//droptip = cb.isChecked();
				droptip = rv.pip.droptip.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("suction");
				//suction = cb.isChecked();
				suction = rv.misc.suction.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("suction_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.suction_timer.getText().toString().equals(""))
					suction_timer = 0f;
				else
					suction_timer = Float.parseFloat(rv.misc.suction_timer.getText().toString());
					//suction_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("echo");
				//echo = cb.isChecked();
				echo = false;
				
				//cb = (CheckBox) tr.findViewWithTag("grip");
				//grip = cb.isChecked();
				grip = rv.misc.grip.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("grip_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.grip_timer.getText().toString().equals(""))
					grip_timer = 0f;
				else
					grip_timer = Float.parseFloat(rv.misc.grip_timer.getText().toString());
					//grip_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//autoreturnx = cb.isChecked();
				autoreturnx = rv.arx.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("ary");
				//autoreturny = cb.isChecked();
				autoreturny = rv.aut.ary.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("arz");
				//autoreturnz = cb.isChecked();
				autoreturnz = rv.aut.arz.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("home");
				//home = cb.isChecked();
				home = rv.aut.home.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("ox");
				//if(met.getText().toString().equals(""))
				if(rv.ox.getText().toString().equals(""))
					offsetx = 0f;
				else
					offsetx = Float.parseFloat(rv.ox.getText().toString());
					//offsetx = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oy");
				//if(met.getText().toString().equals(""))
				if(rv.off.oy.getText().toString().equals(""))
					offsety = 0f;
				else
					offsety = Float.parseFloat(rv.off.oy.getText().toString());
					//offsety = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oz");
				//if(met.getText().toString().equals(""))
				if(rv.off.oz.getText().toString().equals(""))
					offsetz = 0f;
				else
					offsetz = Float.parseFloat(rv.off.oz.getText().toString());
					//offsetz = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//if(nds.getSelectedItem()==null)
				if(rv.rowa.getSelectedItem()==null)
					rowa = zero;
				else
					rowa = Integer.parseInt(rv.rowa.getSelectedItem().toString());
					//rowa = Integer.parseInt(nds.getSelectedItem().toString());					
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				//if(nds.getSelectedItem()==null)
				if(rv.flo.rowb.getSelectedItem()==null)
					rowb = zero;
				else
					rowb = Integer.parseInt(rv.flo.rowb.getSelectedItem().toString());
					//rowb = Integer.parseInt(nds.getSelectedItem().toString());
				
				//met = (MyEditText) tr.findViewWithTag("delay");
				//if(met.getText().toString().equals(""))
				if(rv.flo.delay.getText().toString().equals(""))
					delay = 0f;
				else
					delay = Float.parseFloat(rv.flo.delay.getText().toString());
					//delay = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("times");
				//if(met.getText().toString().equals(""))
				if(rv.flo.times.getText().toString().equals(""))
					times=zero;
				else
					times = Integer.parseInt(rv.flo.times.getText().toString());
					//times = Integer.parseInt(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//if(nds.getSelectedItem()==null)
				if(rv.sensor.getSelectedItem()==null)
					sensor = zero;
				else
					sensor = rv.sensor.getSelectedItemPosition();
					//sensor = nds.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("condition");
				//if(met.getText().toString().equals(""))
				if(rv.con.condition.getText().toString().equals(""))
					condition=zero;
				else
					condition = Float.parseFloat(rv.con.condition.getText().toString());
					//condition = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				//if(nds.getSelectedItem()==null)
				if(rv.con.criterion.getSelectedItem()==null)
					criterion = zero;
				else
					criterion = rv.con.criterion.getSelectedItemPosition();
					//criterion = nds.getSelectedItemPosition();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				//if(nds.getSelectedItem()==null)
				if(rv.con.negative.getSelectedItem()==null)
					negative = zero;
				else
					negative = rv.con.negative.getSelectedItemPosition();
					//negative = nds.getSelectedItemPosition();
				
				//cb = (CheckBox) tr.findViewWithTag("trace");
				//trace = cb.isChecked();
				trace = rv.trace.isChecked();
				
				tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				tv = (TextView) tr.findViewWithTag("no");
				number = Integer.parseInt(tv.getText().toString());
				
				
				Command command = new Command(	number,		
												ingredient,
												speed,
												depth,
												zspeed,
												aspirate,
												aspspeed,
												blowout,
												droptip,
												suction,
												echo,
												grip,
												autoreturnx,
												autoreturny,
												autoreturnz,
												home,
												offsetx,
												offsety,
												offsetz,
												rowa,
												rowb,
												delay,
												times,
												sensor,
												condition,
												criterion,
												negative,
												trace,
												conversion,
												mix,
												grip_timer,
												suction_timer
															);
				
				recipe.add(command);
				
			}
			
			mixbook.put(recipe.getName(), recipe);
			getIntent().putExtra("mixbook", mixbook.makeString());
			
			
			File file = new File(fString);
			FileOutputStream outputStream;
			try{
				outputStream = new FileOutputStream(file, false);
				outputStream.write(mixbook.makeString().getBytes());
				outputStream.close();
				
				if(debug){
					Log.d(TAG,"returning to RA");
					Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
				}
				getIntent().putExtra("mixbook", mixbook.makeString());
				setResult(RESULT_OK,getIntent());
				fileSpinner = (NoDefaultSpinner) findViewById(R.id.savoy);
				fileSpinner.setAdapter(fileSpinner.getAdapter());
				fileSpinner.setPrompt("SAVED");
				//finish();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else if(aString.equals("EDITRECIPE")){
			if(debug)
				Log.d(TAG,"Save-EDITRECIPE");
			if(arNameView.getText().toString().equals("")){
				Toast.makeText(AddRecipeActivity.this, "Please give this recipe a name", Toast.LENGTH_SHORT).show();
				return;
			}
			int rt=0;
			TableRow tr;
			TableRow tr_no;
			TextView tv;
			NoDefaultSpinner nds;
			//MyEditText met;
			//CheckBox cb;
			if(recipe!=null)
				recipe.clear();
			else
				recipe = new Recipe();
				
			nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
			if(nds.getSelectedItem()==null)
				conversion = 0;
			else
				conversion = nds.getSelectedItemPosition();
			
			int zero = 0;
			
			while(rt<arNRows) {
				RowHolder rv = new RowHolder();
				rv = rowholders.get(rt++);
				
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++rt));
				//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//if(nds.getSelectedItem()==null||nds.getSelectedItemPosition()==0)
				//	break;
				if(rv.ingredient.getSelectedItem()==null||
				   rv.ingredient.getSelectedItemPosition()==0)
						break;
				
				if(rt==1)
					recipe.setName(arNameView.getText().toString());
				int number;
				String ingredient;
				int speed;
				float depth;
				int zspeed;
				float aspirate;
				int aspspeed;
				boolean blowout;
				boolean droptip;
				boolean suction;
				boolean echo;
				boolean grip;
				boolean autoreturnx;
				boolean autoreturny;
				boolean autoreturnz;
				boolean home;
				float offsetx;
				float offsety;
				float offsetz;
				int rowa;
				int rowb;
				float delay;
				int times;
				int sensor;
				float condition;
				int criterion;
				int negative;
				boolean trace;
				int mix;
				float grip_timer;
				float suction_timer;
				
				speed = aspspeed = zspeed = 0;
				
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//ingredient = nds.getSelectedItem().toString();
				ingredient = rv.ingredient.getSelectedItem().toString();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//speed = nds.getSelectedItemPosition();
				speed = rv.speed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("depth");
				//if(met.getText().toString().equals(""))
				if(rv.misc.depth.getText().toString().equals(""))	
					depth = 0f;
				else
					depth = Float.parseFloat(rv.misc.depth.getText().toString());
					//depth = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				//zspeed = nds.getSelectedItemPosition();
				zspeed = rv.misc.zspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//if(met.getText().toString().equals(""))
				if(rv.aspirate.getText().toString().equals(""))	
					aspirate = 0f;
				else
					aspirate = Float.parseFloat(rv.aspirate.getText().toString());
					//aspirate = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				//aspspeed = nds.getSelectedItemPosition();
				aspspeed = rv.pip.aspspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("mix");
				//if(met.getText().toString().equals(""))
				if(rv.pip.mix.getText().toString().equals(""))
					mix = 0;
				else
					mix = Integer.parseInt(rv.pip.mix.getText().toString());
					//mix = Integer.parseInt(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("blowout");
				//blowout = cb.isChecked();
				blowout = rv.pip.blowout.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("droptip");
				//droptip = cb.isChecked();
				droptip = rv.pip.droptip.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("suction");
				//suction = cb.isChecked();
				suction = rv.misc.suction.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("suction_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.suction_timer.getText().toString().equals(""))
					suction_timer = 0f;
				else
					suction_timer = Float.parseFloat(rv.misc.suction_timer.getText().toString());
					//suction_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("echo");
				//echo = cb.isChecked();
				echo = false;
				
				//cb = (CheckBox) tr.findViewWithTag("grip");
				//grip = cb.isChecked();
				grip = rv.misc.grip.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("grip_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.grip_timer.getText().toString().equals(""))
					grip_timer = 0f;
				else
					grip_timer = Float.parseFloat(rv.misc.grip_timer.getText().toString());
					//grip_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//autoreturnx = cb.isChecked();
				autoreturnx = rv.arx.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("ary");
				//autoreturny = cb.isChecked();
				autoreturny = rv.aut.ary.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("arz");
				//autoreturnz = cb.isChecked();
				autoreturnz = rv.aut.arz.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("home");
				//home = cb.isChecked();
				home = rv.aut.home.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("ox");
				//if(met.getText().toString().equals(""))
				if(rv.ox.getText().toString().equals(""))
					offsetx = 0f;
				else
					offsetx = Float.parseFloat(rv.ox.getText().toString());
					//offsetx = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oy");
				//if(met.getText().toString().equals(""))
				if(rv.off.oy.getText().toString().equals(""))
					offsety = 0f;
				else
					offsety = Float.parseFloat(rv.off.oy.getText().toString());
					//offsety = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oz");
				//if(met.getText().toString().equals(""))
				if(rv.off.oz.getText().toString().equals(""))
					offsetz = 0f;
				else
					offsetz = Float.parseFloat(rv.off.oz.getText().toString());
					//offsetz = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//if(nds.getSelectedItem()==null)
				if(rv.rowa.getSelectedItem()==null)
					rowa = zero;
				else
					rowa = Integer.parseInt(rv.rowa.getSelectedItem().toString());
					//rowa = Integer.parseInt(nds.getSelectedItem().toString());					
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				//if(nds.getSelectedItem()==null)
				if(rv.flo.rowb.getSelectedItem()==null)
					rowb = zero;
				else
					rowb = Integer.parseInt(rv.flo.rowb.getSelectedItem().toString());
					//rowb = Integer.parseInt(nds.getSelectedItem().toString());
				
				//met = (MyEditText) tr.findViewWithTag("delay");
				//if(met.getText().toString().equals(""))
				if(rv.flo.delay.getText().toString().equals(""))
					delay = 0f;
				else
					delay = Float.parseFloat(rv.flo.delay.getText().toString());
					//delay = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("times");
				//if(met.getText().toString().equals(""))
				if(rv.flo.times.getText().toString().equals(""))
					times=zero;
				else
					times = Integer.parseInt(rv.flo.times.getText().toString());
					//times = Integer.parseInt(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//if(nds.getSelectedItem()==null)
				if(rv.sensor.getSelectedItem()==null)
					sensor = zero;
				else
					sensor = rv.sensor.getSelectedItemPosition();
					//sensor = nds.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("condition");
				//if(met.getText().toString().equals(""))
				if(rv.con.condition.getText().toString().equals(""))
					condition=zero;
				else
					condition = Float.parseFloat(rv.con.condition.getText().toString());
					//condition = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				//if(nds.getSelectedItem()==null)
				if(rv.con.criterion.getSelectedItem()==null)
					criterion = zero;
				else
					criterion = rv.con.criterion.getSelectedItemPosition();
					//criterion = nds.getSelectedItemPosition();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				//if(nds.getSelectedItem()==null)
				if(rv.con.negative.getSelectedItem()==null)
					negative = zero;
				else
					negative = rv.con.negative.getSelectedItemPosition();
					//negative = nds.getSelectedItemPosition();
				
				//cb = (CheckBox) tr.findViewWithTag("trace");
				//trace = cb.isChecked();
				trace = rv.trace.isChecked();
				
				tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				tv = (TextView) tr.findViewWithTag("no");
				number = Integer.parseInt(tv.getText().toString());
				
				
				Command command = new Command(	number,		
												ingredient,
												speed,
												depth,
												zspeed,
												aspirate,
												aspspeed,
												blowout,
												droptip,
												suction,
												echo,
												grip,
												autoreturnx,
												autoreturny,
												autoreturnz,
												home,
												offsetx,
												offsety,
												offsetz,
												rowa,
												rowb,
												delay,
												times,
												sensor,
												condition,
												criterion,
												negative,
												trace,
												conversion,
												mix,
												grip_timer,
												suction_timer
															);
				
				recipe.add(command);
				
			}
			
			
			
			mixbook.replace(nString, recipe);
			File file = new File(fString);
			FileOutputStream outputStream;
			try{
				outputStream = new FileOutputStream(file, false);
				outputStream.write(mixbook.makeString().getBytes());
				outputStream.close();
				
				if(debug){
					Log.d(TAG,"returning to RA");
					Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
				}
				getIntent().putExtra("mixbook", mixbook.makeString());
				setResult(RESULT_OK,getIntent());
				fileSpinner = (NoDefaultSpinner) findViewById(R.id.savoy);
				fileSpinner.setAdapter(fileSpinner.getAdapter());
				fileSpinner.setPrompt("SAVED");
				//finish();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			if(debug){
				Log.d(TAG,"NOTHING SELECTED!?!");
				Log.d(TAG,"aString = "+aString);
			}
		}
	
	}
	
	private void SaveAndReturn(){
		if(debug)
			Log.d(TAG,"SAVE BUTTON CLICKED");
		if(aString.equals("ADDRECIPE")){
			if(arNameView.getText().toString().equals("")){
				Toast.makeText(AddRecipeActivity.this, "Please give this recipe a name", Toast.LENGTH_SHORT).show();
				return;
			}else if(arNameView.getText().toString().contains(",")){
				Toast.makeText(AddRecipeActivity.this, "Please remove any commas from the recipes a name", Toast.LENGTH_SHORT).show();
				return;
			}else{
				for(String key:mixbook.getRecipeSet()){
					if(mixbook.get(key).getName().equals(arNameView.getText().toString())){
						Toast.makeText(AddRecipeActivity.this, "A recipe with this name already exists", Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			int rt=0;
			//TableRow tr;
			//TableRow tr_no;
			//TextView tv;
			NoDefaultSpinner nds;
			//MyEditText met;
			//CheckBox cb;
			if(recipe!=null)
				recipe.clear();
			else
				recipe = new Recipe();
				
			nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
			if(nds.getSelectedItem()==null)
				conversion = 0;
			else
				conversion = nds.getSelectedItemPosition();
			
			int zero = 0;
			
			while(rt<arNRows) {
				RowHolder rv = new RowHolder();
				rv = rowholders.get(rt++);
				
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++rt));
				//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//if(nds.getSelectedItem()==null||nds.getSelectedItemPosition()==0)
				//	break;
				if(rv.ingredient.getSelectedItem()==null||
				   rv.ingredient.getSelectedItemPosition()==0)
						break;
				
				
				if(rt==1)
					recipe.setName(arNameView.getText().toString());
				int number;
				String ingredient;
				int speed;
				float depth;
				int zspeed;
				float aspirate;
				int aspspeed;
				boolean blowout;
				boolean droptip;
				boolean suction;
				boolean echo;
				boolean grip;
				boolean autoreturnx;
				boolean autoreturny;
				boolean autoreturnz;
				boolean home;
				float offsetx;
				float offsety;
				float offsetz;
				int rowa;
				int rowb;
				float delay;
				int times;
				int sensor;
				float condition;
				int criterion;
				int negative;
				boolean trace;
				int mix;
				float grip_timer;
				float suction_timer;
				
				
				speed = aspspeed = zspeed = 0;
				
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//ingredient = nds.getSelectedItem().toString();
				ingredient = rv.ingredient.getSelectedItem().toString();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//speed = nds.getSelectedItemPosition();
				speed = rv.speed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("depth");
				//if(met.getText().toString().equals(""))
				if(rv.misc.depth.getText().toString().equals(""))	
					depth = 0f;
				else
					depth = Float.parseFloat(rv.misc.depth.getText().toString());
					//depth = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				//zspeed = nds.getSelectedItemPosition();
				zspeed = rv.misc.zspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//if(met.getText().toString().equals(""))
				if(rv.aspirate.getText().toString().equals(""))	
					aspirate = 0f;
				else
					aspirate = Float.parseFloat(rv.aspirate.getText().toString());
					//aspirate = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				//aspspeed = nds.getSelectedItemPosition();
				aspspeed = rv.pip.aspspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("mix");
				//if(met.getText().toString().equals(""))
				if(rv.pip.mix.getText().toString().equals(""))
					mix = 0;
				else
					mix = Integer.parseInt(rv.pip.mix.getText().toString());
					//mix = Integer.parseInt(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("blowout");
				//blowout = cb.isChecked();
				blowout = rv.pip.blowout.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("droptip");
				//droptip = cb.isChecked();
				droptip = rv.pip.droptip.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("suction");
				//suction = cb.isChecked();
				suction = rv.misc.suction.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("suction_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.suction_timer.getText().toString().equals(""))
					suction_timer = 0f;
				else
					suction_timer = Float.parseFloat(rv.misc.suction_timer.getText().toString());
					//suction_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("echo");
				//echo = cb.isChecked();
				echo = false;
				
				//cb = (CheckBox) tr.findViewWithTag("grip");
				//grip = cb.isChecked();
				grip = rv.misc.grip.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("grip_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.grip_timer.getText().toString().equals(""))
					grip_timer = 0f;
				else
					grip_timer = Float.parseFloat(rv.misc.grip_timer.getText().toString());
					//grip_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//autoreturnx = cb.isChecked();
				autoreturnx = rv.arx.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("ary");
				//autoreturny = cb.isChecked();
				autoreturny = rv.aut.ary.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("arz");
				//autoreturnz = cb.isChecked();
				autoreturnz = rv.aut.arz.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("home");
				//home = cb.isChecked();
				home = rv.aut.home.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("ox");
				//if(met.getText().toString().equals(""))
				if(rv.ox.getText().toString().equals(""))
					offsetx = 0f;
				else
					offsetx = Float.parseFloat(rv.ox.getText().toString());
					//offsetx = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oy");
				//if(met.getText().toString().equals(""))
				if(rv.off.oy.getText().toString().equals(""))
					offsety = 0f;
				else
					offsety = Float.parseFloat(rv.off.oy.getText().toString());
					//offsety = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oz");
				//if(met.getText().toString().equals(""))
				if(rv.off.oz.getText().toString().equals(""))
					offsetz = 0f;
				else
					offsetz = Float.parseFloat(rv.off.oz.getText().toString());
					//offsetz = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//if(nds.getSelectedItem()==null)
				if(rv.rowa.getSelectedItem()==null)
					rowa = zero;
				else
					rowa = Integer.parseInt(rv.rowa.getSelectedItem().toString());
					//rowa = Integer.parseInt(nds.getSelectedItem().toString());					
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				//if(nds.getSelectedItem()==null)
				if(rv.flo.rowb.getSelectedItem()==null)
					rowb = zero;
				else
					rowb = Integer.parseInt(rv.flo.rowb.getSelectedItem().toString());
					//rowb = Integer.parseInt(nds.getSelectedItem().toString());
				
				//met = (MyEditText) tr.findViewWithTag("delay");
				//if(met.getText().toString().equals(""))
				if(rv.flo.delay.getText().toString().equals(""))
					delay = 0f;
				else
					delay = Float.parseFloat(rv.flo.delay.getText().toString());
					//delay = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("times");
				//if(met.getText().toString().equals(""))
				if(rv.flo.times.getText().toString().equals(""))
					times=zero;
				else
					times = Integer.parseInt(rv.flo.times.getText().toString());
					//times = Integer.parseInt(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//if(nds.getSelectedItem()==null)
				if(rv.sensor.getSelectedItem()==null)
					sensor = zero;
				else
					sensor = rv.sensor.getSelectedItemPosition();
					//sensor = nds.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("condition");
				//if(met.getText().toString().equals(""))
				if(rv.con.condition.getText().toString().equals(""))
					condition=zero;
				else
					condition = Float.parseFloat(rv.con.condition.getText().toString());
					//condition = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				//if(nds.getSelectedItem()==null)
				if(rv.con.criterion.getSelectedItem()==null)
					criterion = zero;
				else
					criterion = rv.con.criterion.getSelectedItemPosition();
					//criterion = nds.getSelectedItemPosition();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				//if(nds.getSelectedItem()==null)
				if(rv.con.negative.getSelectedItem()==null)
					negative = zero;
				else
					negative = rv.con.negative.getSelectedItemPosition();
					//negative = nds.getSelectedItemPosition();
				
				//cb = (CheckBox) tr.findViewWithTag("trace");
				//trace = cb.isChecked();
				trace = rv.trace.isChecked();
				
				//tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				//tv = (TextView) tr.findViewWithTag("no");
				//number = Integer.parseInt(tv.getText().toString());
				number = Integer.parseInt(rv.no.getText().toString());
				
				
				Command command = new Command(	number,		
												ingredient,
												speed,
												depth,
												zspeed,
												aspirate,
												aspspeed,
												blowout,
												droptip,
												suction,
												echo,
												grip,
												autoreturnx,
												autoreturny,
												autoreturnz,
												home,
												offsetx,
												offsety,
												offsetz,
												rowa,
												rowb,
												delay,
												times,
												sensor,
												condition,
												criterion,
												negative,
												trace,
												conversion,
												mix,
												grip_timer,
												suction_timer
															);
				
				recipe.add(command);
				
			}
			mixbook.put(recipe.getName(), recipe);
			File file = new File(fString);
			FileOutputStream outputStream;
			try{
				outputStream = new FileOutputStream(file, false);
				outputStream.write(mixbook.makeString().getBytes());
				outputStream.close();
				
				if(debug){
					Log.d(TAG,"returning to RA");
					Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
				}
				getIntent().putExtra("mixbook", mixbook.makeString());
				setResult(RESULT_OK,getIntent());
				finish();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(aString.equals("EDITRECIPE")){
			if(debug)
				Log.d(TAG,"Save-EDITRECIPE");
			if(arNameView.getText().toString().equals("")){
				Toast.makeText(AddRecipeActivity.this, "Please give this recipe a name", Toast.LENGTH_SHORT).show();
				return;
			}
			int rt=0;
			//TableRow tr;
			//TableRow tr_no;
			//TextView tv;
			NoDefaultSpinner nds;
			//MyEditText met;
			//CheckBox cb;
			if(recipe!=null)
				recipe.clear();
			else
				recipe = new Recipe();
				
			nds = (NoDefaultSpinner) findViewById(R.id.pipettor_size);
			if(nds.getSelectedItem()==null)
				conversion = 0;
			else
				conversion = nds.getSelectedItemPosition();
			
			int zero = 0;
			
			while(rt<arNRows) {
				RowHolder rv = new RowHolder();
				rv = rowholders.get(rt++);
				
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++rt));
				//tr_no = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//if(nds.getSelectedItem()==null||nds.getSelectedItemPosition()==0)
				//	break;
				if(rv.ingredient.getSelectedItem()==null||
				   rv.ingredient.getSelectedItemPosition()==0)
						break;
				
				if(rt==1)
					recipe.setName(arNameView.getText().toString());
				int number;
				String ingredient;
				int speed;
				float depth;
				int zspeed;
				float aspirate;
				int aspspeed;
				boolean blowout;
				boolean droptip;
				boolean suction;
				boolean echo;
				boolean grip;
				boolean autoreturnx;
				boolean autoreturny;
				boolean autoreturnz;
				boolean home;
				float offsetx;
				float offsety;
				float offsetz;
				int rowa;
				int rowb;
				float delay;
				int times;
				int sensor;
				float condition;
				int criterion;
				int negative;
				boolean trace;
				int mix;
				float grip_timer;
				float suction_timer;
				
				
				speed = aspspeed = zspeed = 0;
				
				//nds = (NoDefaultSpinner) tr_no.findViewWithTag("ingredient");
				//ingredient = nds.getSelectedItem().toString();
				ingredient = rv.ingredient.getSelectedItem().toString();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//speed = nds.getSelectedItemPosition();
				speed = rv.speed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("depth");
				//if(met.getText().toString().equals(""))
				if(rv.misc.depth.getText().toString().equals(""))	
					depth = 0f;
				else
					depth = Float.parseFloat(rv.misc.depth.getText().toString());
					//depth = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				//zspeed = nds.getSelectedItemPosition();
				zspeed = rv.misc.zspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//if(met.getText().toString().equals(""))
				if(rv.aspirate.getText().toString().equals(""))	
					aspirate = 0f;
				else
					aspirate = Float.parseFloat(rv.aspirate.getText().toString());
					//aspirate = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				//aspspeed = nds.getSelectedItemPosition();
				aspspeed = rv.pip.aspspeed.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("mix");
				//if(met.getText().toString().equals(""))
				if(rv.pip.mix.getText().toString().equals(""))
					mix = 0;
				else
					mix = Integer.parseInt(rv.pip.mix.getText().toString());
					//mix = Integer.parseInt(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("blowout");
				//blowout = cb.isChecked();
				blowout = rv.pip.blowout.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("droptip");
				//droptip = cb.isChecked();
				droptip = rv.pip.droptip.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("suction");
				//suction = cb.isChecked();
				suction = rv.misc.suction.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("suction_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.suction_timer.getText().toString().equals(""))
					suction_timer = 0f;
				else
					suction_timer = Float.parseFloat(rv.misc.suction_timer.getText().toString());
					//suction_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("echo");
				//echo = cb.isChecked();
				echo = false;
				
				//cb = (CheckBox) tr.findViewWithTag("grip");
				//grip = cb.isChecked();
				grip = rv.misc.grip.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("grip_timer");
				//if(met.getText().toString().equals(""))
				if(rv.misc.grip_timer.getText().toString().equals(""))
					grip_timer = 0f;
				else
					grip_timer = Float.parseFloat(rv.misc.grip_timer.getText().toString());
					//grip_timer = Float.parseFloat(met.getText().toString());
				
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//autoreturnx = cb.isChecked();
				autoreturnx = rv.arx.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("ary");
				//autoreturny = cb.isChecked();
				autoreturny = rv.aut.ary.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("arz");
				//autoreturnz = cb.isChecked();
				autoreturnz = rv.aut.arz.isChecked();
				
				//cb = (CheckBox) tr.findViewWithTag("home");
				//home = cb.isChecked();
				home = rv.aut.home.isChecked();
				
				//met = (MyEditText) tr.findViewWithTag("ox");
				//if(met.getText().toString().equals(""))
				if(rv.ox.getText().toString().equals(""))
					offsetx = 0f;
				else
					offsetx = Float.parseFloat(rv.ox.getText().toString());
					//offsetx = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oy");
				//if(met.getText().toString().equals(""))
				if(rv.off.oy.getText().toString().equals(""))
					offsety = 0f;
				else
					offsety = Float.parseFloat(rv.off.oy.getText().toString());
					//offsety = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("oz");
				//if(met.getText().toString().equals(""))
				if(rv.off.oz.getText().toString().equals(""))
					offsetz = 0f;
				else
					offsetz = Float.parseFloat(rv.off.oz.getText().toString());
					//offsetz = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//if(nds.getSelectedItem()==null)
				if(rv.rowa.getSelectedItem()==null||rv.rowa.getSelectedItem().toString().equals(""))
					rowa = zero;
				else
					rowa = Integer.parseInt(rv.rowa.getSelectedItem().toString());
					//rowa = Integer.parseInt(nds.getSelectedItem().toString());					
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				//if(nds.getSelectedItem()==null)
				if(rv.flo.rowb.getSelectedItem()==null||rv.flo.rowb.getSelectedItem().toString().equals(""))
					rowb = zero;
				else
					rowb = Integer.parseInt(rv.flo.rowb.getSelectedItem().toString());
					//rowb = Integer.parseInt(nds.getSelectedItem().toString());
				
				//met = (MyEditText) tr.findViewWithTag("delay");
				//if(met.getText().toString().equals(""))
				if(rv.flo.delay.getText().toString().equals(""))
					delay = 0f;
				else
					delay = Float.parseFloat(rv.flo.delay.getText().toString());
					//delay = Float.parseFloat(met.getText().toString());
				
				//met = (MyEditText) tr.findViewWithTag("times");
				//if(met.getText().toString().equals(""))
				if(rv.flo.times.getText().toString().equals(""))
					times=zero;
				else
					times = Integer.parseInt(rv.flo.times.getText().toString());
					//times = Integer.parseInt(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//if(nds.getSelectedItem()==null)
				if(rv.sensor.getSelectedItem()==null)
					sensor = zero;
				else
					sensor = rv.sensor.getSelectedItemPosition();
					//sensor = nds.getSelectedItemPosition();
				
				//met = (MyEditText) tr.findViewWithTag("condition");
				//if(met.getText().toString().equals(""))
				if(rv.con.condition.getText().toString().equals(""))
					condition=zero;
				else
					condition = Float.parseFloat(rv.con.condition.getText().toString());
					//condition = Float.parseFloat(met.getText().toString());
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				//if(nds.getSelectedItem()==null)
				if(rv.con.criterion.getSelectedItem()==null)
					criterion = zero;
				else
					criterion = rv.con.criterion.getSelectedItemPosition();
					//criterion = nds.getSelectedItemPosition();
				
				//nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				//if(nds.getSelectedItem()==null)
				if(rv.con.negative.getSelectedItem()==null)
					negative = zero;
				else
					negative = rv.con.negative.getSelectedItemPosition();
					//negative = nds.getSelectedItemPosition();
				
				//cb = (CheckBox) tr.findViewWithTag("trace");
				//trace = cb.isChecked();
				trace = rv.trace.isChecked();
				
				//tr = (TableRow) arNoTable.findViewWithTag("trNO"+String.valueOf(rt));
				//tv = (TextView) tr.findViewWithTag("no");
				//number = Integer.parseInt(tv.getText().toString());
				number = Integer.parseInt(rv.no.getText().toString());
				
				
				Command command = new Command(	number,		
												ingredient,
												speed,
												depth,
												zspeed,
												aspirate,
												aspspeed,
												blowout,
												droptip,
												suction,
												echo,
												grip,
												autoreturnx,
												autoreturny,
												autoreturnz,
												home,
												offsetx,
												offsety,
												offsetz,
												rowa,
												rowb,
												delay,
												times,
												sensor,
												condition,
												criterion,
												negative,
												trace,
												conversion,
												mix,
												grip_timer,
												suction_timer
															);
				
				recipe.add(command);
				
			}
			mixbook.replace(nString, recipe);
			File file = new File(fString);
			FileOutputStream outputStream;
			try{
				outputStream = new FileOutputStream(file, false);
				outputStream.write(mixbook.makeString().getBytes());
				outputStream.close();
				if(debug){
					Log.d(TAG,"returning to RA");
					Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
				}
				getIntent().putExtra("mixbook", mixbook.makeString());
				setResult(RESULT_OK,getIntent());
				finish();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			if(debug){
				Log.d(TAG,"NOTHING SELECTED!?!");
				Log.d(TAG,"aString = "+aString);
			}
		}
	
	}
	
	public void AddRowsHandler(View view) {
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View myADView = inflater.inflate(R.layout.ad_rows, null, false);
	    
	    final MyEditText et = (MyEditText) myADView.findViewById(R.id.row_count);
	    et.setTypeface(tf);
	    et.setHint("enter number to add");
	    
		AlertDialog.Builder adBldr = new AlertDialog.Builder(AddRecipeActivity.this);
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
					if((rcount<100)&&(arNRows<101)){
						Toast.makeText(AddRecipeActivity.this, "WHOA! "+String.valueOf(rcount),Toast.LENGTH_SHORT).show();
						addRows(rcount);
					}
				}else
					Toast.makeText(AddRecipeActivity.this, "Enter a number!",Toast.LENGTH_SHORT).show();
				
			}
		});
		AlertDialog alert = adBldr.create();
		alert.show();
	}
	
	public void DeleteRowsHandler(View view) {
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View myADView = inflater.inflate(R.layout.ad_rows, null, false);

	    final MyEditText et = (MyEditText) myADView.findViewById(R.id.row_count);
	    et.setTypeface(tf);
	    et.setHint("enter number to delete");
	    
		AlertDialog.Builder adBldr = new AlertDialog.Builder(AddRecipeActivity.this);
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
					int rcount;
					try{
						rcount = Integer.valueOf(numero);
					}catch(Exception e) {
						e.printStackTrace();
						rcount = 0;
					}
					if(arNRows-rcount>=10){
						Toast.makeText(AddRecipeActivity.this, String.valueOf(rcount)+" of the rows deleted",Toast.LENGTH_SHORT).show();
						deleteRows(rcount);
					}else{
						//int c = 10 + rcount - arNRows;
						//rcount = (arNRows - rcount) + c;
						rcount = arNRows - 10;
						Toast.makeText(AddRecipeActivity.this, String.valueOf(rcount)+" of the rows deleted to keep a minimum of 10 rows",Toast.LENGTH_SHORT).show();
						deleteRows(rcount);
					}
				}else
					Toast.makeText(AddRecipeActivity.this, "Enter a number!",Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = adBldr.create();
		alert.show();
	}
	
	public void InsertRowsHandler(View view) {
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View myIView = inflater.inflate(R.layout.i_rows, null, false);

	    final MyEditText et1 = (MyEditText) myIView.findViewById(R.id.insert_row);
	    et1.setTypeface(tf);
	    et1.setHint("insert before row number");
	    
	    final MyEditText et2 = (MyEditText) myIView.findViewById(R.id.insert_count);
	    et2.setTypeface(tf);
	    et2.setHint("enter number to insert");
	    
	    
		AlertDialog.Builder adBldr = new AlertDialog.Builder(AddRecipeActivity.this);
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
					
					if(rinsert>arNRows)
						rinsert=arNRows;
					
					
					if((rcount<100)&&(arNRows<101)){
						if(rinsert<=arNRows){
							Toast.makeText(AddRecipeActivity.this, "WHOA! "+String.valueOf(rcount),Toast.LENGTH_SHORT).show();
							insertRows(rinsert, rcount);
						} else {
							Toast.makeText(AddRecipeActivity.this, "Fix your insertion point!",Toast.LENGTH_SHORT).show();
						}
					}
				}else
					Toast.makeText(AddRecipeActivity.this, "Fix your number of rows!",Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = adBldr.create();
		alert.show();
	}
	
	public void spinnerReset() {
		fileSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener(){

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {
						if(parent.getItemAtPosition(pos).toString().equals("SAVE")){
							SaveRecipe();
						}else if(parent.getItemAtPosition(pos).toString().equals("SAVE & RETURN")){
							SaveAndReturn();
						}
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {	}
				}
				);
	}
	
	public void pipetteReset() {
		pipetteSpinner.setOnItemSelectedListener(
			new OnItemSelectedListener(){
				
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					// TODO Auto-generated method stub
					if(parent.getItemAtPosition(pos).toString().equals("direct")){
						signAspirators();
					} else {
						unsignAspirators();
					}
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
				
			}
		);
		if(pipetteSpinner.getSelectedItemPosition()==5){
			signAspirators();
		}
	}
	
	public void signAspirators(){
		int q = 0;
		while(q<arNRows) {
			RowHolder rh = new RowHolder();
			rh = rowholders.get(q++);
			rh.aspirate.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
	}
	
	public void unsignAspirators() {
		int q = 0;
		while(q<arNRows) {
			RowHolder rh = new RowHolder();
			rh = rowholders.get(q++);
			rh.aspirate.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
	}
	
	public void pipettingHandler(View view) {
		//TableRow tr;
		//NoDefaultSpinner nds;
		//MyEditText met;
		//CheckBox cb;
		//aspirate, asp-speed, blowout, droptip
		int q=0;
		if(pipettingHide){
			pipettingHide=false;
			Button bff = (Button) findViewById(R.id.pipetting_tab_closed);
			bff.setVisibility(View.GONE);
			bff = (Button) findViewById(R.id.pipetting_tab_open);
			bff.setVisibility(View.VISIBLE);
			/*TextView tvLabel = (TextView) findViewById(R.id.aspspeed_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.mix_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.blowout_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.droptip_label);
			tvLabel.setVisibility(View.VISIBLE);*/
			labelholder.pip.aspspeed.setVisibility(View.VISIBLE);
			labelholder.pip.mix.setVisibility(View.VISIBLE);
			labelholder.pip.blowout.setVisibility(View.VISIBLE);
			labelholder.pip.droptip.setVisibility(View.VISIBLE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//met.setVisibility(View.VISIBLE);
				/*nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				nds.setVisibility(View.VISIBLE);
				met = (MyEditText) tr.findViewWithTag("mix");
				met.setVisibility(View.VISIBLE);
				cb = (CheckBox) tr.findViewWithTag("blowout");
				cb.setVisibility(View.VISIBLE);
				cb = (CheckBox) tr.findViewWithTag("droptip");
				cb.setVisibility(View.VISIBLE);*/
				rh.pip.aspspeed.setVisibility(View.VISIBLE);
				rh.pip.mix.setVisibility(View.VISIBLE);
				rh.pip.blowout.setVisibility(View.VISIBLE);
				rh.pip.droptip.setVisibility(View.VISIBLE);
			}
			
		}else{
			pipettingHide=true;
			Button bff = (Button) findViewById(R.id.pipetting_tab_closed);
			bff.setVisibility(View.VISIBLE);
			bff = (Button) findViewById(R.id.pipetting_tab_open);
			bff.setVisibility(View.GONE);
			/*TextView tvLabel = (TextView) findViewById(R.id.aspspeed_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.mix_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.blowout_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.droptip_label);
			tvLabel.setVisibility(View.GONE);*/
			labelholder.pip.aspspeed.setVisibility(View.GONE);
			labelholder.pip.mix.setVisibility(View.GONE);
			labelholder.pip.blowout.setVisibility(View.GONE);
			labelholder.pip.droptip.setVisibility(View.GONE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//met = (MyEditText) tr.findViewWithTag("aspirate");
				//met.setVisibility(View.GONE);
				/*nds = (NoDefaultSpinner) tr.findViewWithTag("aspspeed");
				nds.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("mix");
				met.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("blowout");
				cb.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("droptip");
				cb.setVisibility(View.GONE);*/
				rh.pip.aspspeed.setVisibility(View.GONE);
				rh.pip.mix.setVisibility(View.GONE);
				rh.pip.blowout.setVisibility(View.GONE);
				rh.pip.droptip.setVisibility(View.GONE);
			}
		}
	}
	
	public void autosHandler(View view) {
		//TableRow tr;
		//CheckBox cb;
		//auto xyz, home
		int q=0;
		if(autosHide){
			autosHide=false;
			Button bff = (Button) findViewById(R.id.autos_tab_closed);
			bff.setVisibility(View.GONE);
			bff = (Button) findViewById(R.id.autos_tab_open);
			bff.setVisibility(View.VISIBLE);
			/*TextView tvLabel = (TextView) findViewById(R.id.ay_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.az_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.home_label);
			tvLabel.setVisibility(View.VISIBLE);*/
			labelholder.aut.ary.setVisibility(View.VISIBLE);
			labelholder.aut.arz.setVisibility(View.VISIBLE);
			labelholder.aut.home.setVisibility(View.VISIBLE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//cb.setVisibility(View.VISIBLE);
				/*cb = (CheckBox) tr.findViewWithTag("ary");
				cb.setVisibility(View.VISIBLE);
				cb = (CheckBox) tr.findViewWithTag("arz");
				cb.setVisibility(View.VISIBLE);
				cb = (CheckBox) tr.findViewWithTag("home");
				cb.setVisibility(View.VISIBLE);*/
				rh.aut.ary.setVisibility(View.VISIBLE);
				rh.aut.arz.setVisibility(View.VISIBLE);
				rh.aut.home.setVisibility(View.VISIBLE);
			}
			
		}else{
			autosHide=true;
			Button bff = (Button) findViewById(R.id.autos_tab_closed);
			bff.setVisibility(View.VISIBLE);
			bff = (Button) findViewById(R.id.autos_tab_open);
			bff.setVisibility(View.GONE);
			/*TextView tvLabel = (TextView) findViewById(R.id.ay_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.az_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.home_label);
			tvLabel.setVisibility(View.GONE);*/
			labelholder.aut.ary.setVisibility(View.GONE);
			labelholder.aut.arz.setVisibility(View.GONE);
			labelholder.aut.home.setVisibility(View.GONE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows){
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//cb = (CheckBox) tr.findViewWithTag("arx");
				//cb.setVisibility(View.GONE);
				/*cb = (CheckBox) tr.findViewWithTag("ary");
				cb.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("arz");
				cb.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("home");
				cb.setVisibility(View.GONE);*/
				rh.aut.ary.setVisibility(View.GONE);
				rh.aut.arz.setVisibility(View.GONE);
				rh.aut.home.setVisibility(View.GONE);
			}
		}
	}
	
	public void offsetsHandler(View view) {
		//TableRow tr;
		//MyEditText met;
		//offset xyz
		int q=0;
		if(offsetsHide){
			offsetsHide=false;
			Button bff = (Button) findViewById(R.id.offs_tab_closed);
			bff.setVisibility(View.GONE);
			bff = (Button) findViewById(R.id.offs_tab_open);
			bff.setVisibility(View.VISIBLE);
			/*TextView tvLabel = (TextView) findViewById(R.id.oy_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.oz_label);
			tvLabel.setVisibility(View.VISIBLE);*/
			labelholder.off.oy.setVisibility(View.VISIBLE);
			labelholder.off.oz.setVisibility(View.VISIBLE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//met = (MyEditText) tr.findViewWithTag("ox");
				//met.setVisibility(View.VISIBLE);
				/*met = (MyEditText) tr.findViewWithTag("oy");
				met.setVisibility(View.VISIBLE);
				met = (MyEditText) tr.findViewWithTag("oz");
				met.setVisibility(View.VISIBLE);*/
				rh.off.oy.setVisibility(View.VISIBLE);
				rh.off.oz.setVisibility(View.VISIBLE);
			}
			
		}else{
			offsetsHide=true;
			Button bff = (Button) findViewById(R.id.offs_tab_closed);
			bff.setVisibility(View.VISIBLE);
			bff = (Button) findViewById(R.id.offs_tab_open);
			bff.setVisibility(View.GONE);
			/*TextView tvLabel = (TextView) findViewById(R.id.oy_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.oz_label);
			tvLabel.setVisibility(View.GONE);*/
			labelholder.off.oy.setVisibility(View.GONE);
			labelholder.off.oz.setVisibility(View.GONE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//met = (MyEditText) tr.findViewWithTag("ox");
				//met.setVisibility(View.GONE);
				/*met = (MyEditText) tr.findViewWithTag("oy");
				met.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("oz");
				met.setVisibility(View.GONE);*/
				rh.off.oy.setVisibility(View.GONE);
				rh.off.oz.setVisibility(View.GONE);
			}
		}
	}
	
	public void flowHandler(View view) {
		//TableRow tr;
		//NoDefaultSpinner nds;
		//MyEditText met;
		//row ab, delay, times
		int q=0;
		if(flowHide){
			flowHide=false;
			Button bff = (Button) findViewById(R.id.flow_tab_closed);
			bff.setVisibility(View.GONE);
			bff = (Button) findViewById(R.id.flow_tab_open);
			bff.setVisibility(View.VISIBLE);
			/*TextView tvLabel = (TextView) findViewById(R.id.rowb_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.delay_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.times_label);
			tvLabel.setVisibility(View.VISIBLE);*/
			labelholder.flo.rowb.setVisibility(View.VISIBLE);
			labelholder.flo.delay.setVisibility(View.VISIBLE);
			labelholder.flo.times.setVisibility(View.VISIBLE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//nds.setVisibility(View.VISIBLE);
				/*nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				nds.setVisibility(View.VISIBLE);
				met = (MyEditText) tr.findViewWithTag("delay");
				met.setVisibility(View.VISIBLE);
				met = (MyEditText) tr.findViewWithTag("times");
				met.setVisibility(View.VISIBLE);*/
				rh.flo.rowb.setVisibility(View.VISIBLE);
				rh.flo.delay.setVisibility(View.VISIBLE);
				rh.flo.times.setVisibility(View.VISIBLE);
			}
			
		}else{
			flowHide=true;
			Button bff = (Button) findViewById(R.id.flow_tab_closed);
			bff.setVisibility(View.VISIBLE);
			bff = (Button) findViewById(R.id.flow_tab_open);
			bff.setVisibility(View.GONE);
			/*TextView tvLabel = (TextView) findViewById(R.id.rowb_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.delay_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.times_label);
			tvLabel.setVisibility(View.GONE);*/
			labelholder.flo.rowb.setVisibility(View.GONE);
			labelholder.flo.delay.setVisibility(View.GONE);
			labelholder.flo.times.setVisibility(View.GONE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//nds = (NoDefaultSpinner) tr.findViewWithTag("rowa");
				//nds.setVisibility(View.GONE);
				/*nds = (NoDefaultSpinner) tr.findViewWithTag("rowb");
				nds.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("delay");
				met.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("times");
				met.setVisibility(View.GONE);*/
				rh.flo.rowb.setVisibility(View.GONE);
				rh.flo.delay.setVisibility(View.GONE);
				rh.flo.times.setVisibility(View.GONE);
			}
		}
	}
	
	public void conditionalsHandler(View view) {
		//TableRow tr;
		//NoDefaultSpinner nds;
		//MyEditText met;
		//sensor, condition, criterion, negative
		int q=0;
		if(conditionalsHide){
			conditionalsHide=false;
			//TextView tff = (TextView) findViewById(R.id.filler_five);
			//tff.setVisibility(View.VISIBLE);
			Button bff = (Button) findViewById(R.id.conditionals_tab_closed);
			bff.setVisibility(View.GONE);
			bff = (Button) findViewById(R.id.conditionals_tab_open);
			bff.setVisibility(View.VISIBLE);
			/*TextView tvLabel = (TextView) findViewById(R.id.condition_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.criterion_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.negative_label);
			tvLabel.setVisibility(View.VISIBLE);*/
			labelholder.con.condition.setVisibility(View.VISIBLE);
			labelholder.con.criterion.setVisibility(View.VISIBLE);
			labelholder.con.negative.setVisibility(View.VISIBLE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//nds.setVisibility(View.VISIBLE);
				/*met = (MyEditText) tr.findViewWithTag("condition");
				met.setVisibility(View.VISIBLE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				nds.setVisibility(View.VISIBLE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				nds.setVisibility(View.VISIBLE);*/
				rh.con.condition.setVisibility(View.VISIBLE);
				rh.con.criterion.setVisibility(View.VISIBLE);
				rh.con.negative.setVisibility(View.VISIBLE);
			}
			
		}else{
			conditionalsHide=true;
			//TextView tff = (TextView) findViewById(R.id.filler_five);
			//tff.setVisibility(View.GONE);
			Button bff = (Button) findViewById(R.id.conditionals_tab_closed);
			bff.setVisibility(View.VISIBLE);
			bff = (Button) findViewById(R.id.conditionals_tab_open);
			bff.setVisibility(View.GONE);
			/*TextView tvLabel = (TextView) findViewById(R.id.condition_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.criterion_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.negative_label);
			tvLabel.setVisibility(View.GONE);*/
			labelholder.con.condition.setVisibility(View.GONE);
			labelholder.con.criterion.setVisibility(View.GONE);
			labelholder.con.negative.setVisibility(View.GONE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows){
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//nds.setVisibility(View.GONE);
				/*met = (MyEditText) tr.findViewWithTag("condition");
				met.setVisibility(View.GONE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("criterion");
				nds.setVisibility(View.GONE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("negative");
				nds.setVisibility(View.GONE);*/
				rh.con.condition.setVisibility(View.GONE);
				rh.con.criterion.setVisibility(View.GONE);
				rh.con.negative.setVisibility(View.GONE);
			}
		}
	}
		
	public void miscHandler(View view) {
		//TableRow tr;
		//NoDefaultSpinner nds;
		//MyEditText met;
		//CheckBox cb;
		//sensor, condition, criterion, negative
		int q=0;
		if(miscHide){
			miscHide=false;
			//TextView tff = (TextView) findViewById(R.id.filler_five);
			//tff.setVisibility(View.VISIBLE);
			Button bff = (Button) findViewById(R.id.misc_tab_closed);
			bff.setVisibility(View.GONE);
			bff = (Button) findViewById(R.id.misc_tab_open);
			bff.setVisibility(View.VISIBLE);
			//tvLabel = (TextView) findViewById(R.id.speed_label);
			//tvLabel.setVisibility(View.VISIBLE);
			/*tvLabel = (TextView) findViewById(R.id.depth_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.zspeed_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.suction_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.suction_timer_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.grip_label);
			tvLabel.setVisibility(View.VISIBLE);
			tvLabel = (TextView) findViewById(R.id.grip_timer_label);
			tvLabel.setVisibility(View.VISIBLE);*/
			labelholder.misc.depth.setVisibility(View.VISIBLE);
			labelholder.misc.zspeed.setVisibility(View.VISIBLE);
			labelholder.misc.suction.setVisibility(View.VISIBLE);
			labelholder.misc.suction_timer.setVisibility(View.VISIBLE);
			labelholder.misc.grip.setVisibility(View.VISIBLE);
			labelholder.misc.grip_timer.setVisibility(View.VISIBLE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//nds.setVisibility(View.VISIBLE);
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//nds.setVisibility(View.VISIBLE);
				/*met = (MyEditText) tr.findViewWithTag("depth");
				met.setVisibility(View.VISIBLE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				nds.setVisibility(View.VISIBLE);
				cb = (CheckBox) tr.findViewWithTag("suction");
				cb.setVisibility(View.VISIBLE);
				met = (MyEditText) tr.findViewWithTag("suction_timer");
				met.setVisibility(View.VISIBLE);
				cb = (CheckBox) tr.findViewWithTag("grip");
				cb.setVisibility(View.VISIBLE);
				met = (MyEditText) tr.findViewWithTag("grip_timer");
				met.setVisibility(View.VISIBLE);*/
				rh.misc.depth.setVisibility(View.VISIBLE);
				rh.misc.zspeed.setVisibility(View.VISIBLE);
				rh.misc.suction.setVisibility(View.VISIBLE);
				rh.misc.suction_timer.setVisibility(View.VISIBLE);
				rh.misc.grip.setVisibility(View.VISIBLE);
				rh.misc.grip_timer.setVisibility(View.VISIBLE);
			}
			
		}else{
			miscHide=true;
			//TextView tff = (TextView) findViewById(R.id.filler_five);
			//tff.setVisibility(View.GONE);
			Button bff = (Button) findViewById(R.id.misc_tab_closed);
			bff.setVisibility(View.VISIBLE);
			bff = (Button) findViewById(R.id.misc_tab_open);
			bff.setVisibility(View.GONE);
			//tvLabel = (TextView) findViewById(R.id.speed_label);
			//tvLabel.setVisibility(View.GONE);
			/*tvLabel = (TextView) findViewById(R.id.depth_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.zspeed_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.suction_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.suction_timer_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.grip_label);
			tvLabel.setVisibility(View.GONE);
			tvLabel = (TextView) findViewById(R.id.grip_timer_label);
			tvLabel.setVisibility(View.GONE);*/
			labelholder.misc.depth.setVisibility(View.GONE);
			labelholder.misc.zspeed.setVisibility(View.GONE);
			labelholder.misc.suction.setVisibility(View.GONE);
			labelholder.misc.suction_timer.setVisibility(View.GONE);
			labelholder.misc.grip.setVisibility(View.GONE);
			labelholder.misc.grip_timer.setVisibility(View.GONE);
			//Stack<RowHolder> tempholders = new Stack<RowHolder>();
			
			while(q<arNRows) {
				RowHolder rh = new RowHolder();
				rh = rowholders.get(q++);
				//tr = (TableRow) arTable.findViewWithTag("tr"+String.valueOf(++q));
				//nds = (NoDefaultSpinner) tr.findViewWithTag("sensor");
				//nds.setVisibility(View.VISIBLE);
				//nds = (NoDefaultSpinner) tr.findViewWithTag("speed");
				//nds.setVisibility(View.GONE);
				/*met = (MyEditText) tr.findViewWithTag("depth");
				met.setVisibility(View.GONE);
				nds = (NoDefaultSpinner) tr.findViewWithTag("zspeed");
				nds.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("suction");
				cb.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("suction_timer");
				met.setVisibility(View.GONE);
				cb = (CheckBox) tr.findViewWithTag("grip");
				cb.setVisibility(View.GONE);
				met = (MyEditText) tr.findViewWithTag("grip_timer");
				met.setVisibility(View.GONE);*/
				rh.misc.depth.setVisibility(View.GONE);
				rh.misc.zspeed.setVisibility(View.GONE);
				rh.misc.suction.setVisibility(View.GONE);
				rh.misc.suction_timer.setVisibility(View.GONE);
				rh.misc.grip.setVisibility(View.GONE);
				rh.misc.grip_timer.setVisibility(View.GONE);
			}
		}
	}
	
	static class RowHolder_Misc{
		//NoDefaultSpinner 	speed;
		MyEditText 			depth;
		NoDefaultSpinner	zspeed;
		CheckBox			suction;
		MyEditText			suction_timer;
		CheckBox			grip;
		MyEditText			grip_timer;
	}
	static class RowHolder_Pip{
		//MyEditText			aspirate;
		NoDefaultSpinner	aspspeed;
		MyEditText			mix;
		CheckBox			blowout;
		CheckBox			droptip;		
	}
	static class RowHolder_Aut{
		//CheckBox			arx;
		CheckBox			ary;
		CheckBox			arz;
		CheckBox			home;
	}
	static class RowHolder_Off{
		//MyEditText			ox;
		MyEditText			oy;
		MyEditText			oz;
	}
	static class RowHolder_Flo{
		//NoDefaultSpinner	rowa;
		NoDefaultSpinner	rowb;
		MyEditText			delay;
		MyEditText			times;
	}
	static class RowHolder_Con{
		//NoDefaultSpinner	sensor;
		MyEditText			condition;
		NoDefaultSpinner	criterion;
		NoDefaultSpinner	negative;
	}
	static class RowHolder{
		TableRow			trNO;
		TableRow			tr;
		TextView			no;
		
		NoDefaultSpinner	ingredient;
		NoDefaultSpinner 	speed;
		MyEditText			aspirate;
		CheckBox			arx;
		MyEditText			ox;
		NoDefaultSpinner	rowa;
		NoDefaultSpinner	sensor;
		RowHolder_Misc	misc;
		RowHolder_Pip	pip;
		RowHolder_Aut	aut;
		RowHolder_Off	off;
		RowHolder_Flo	flo;
		RowHolder_Con	con;
		CheckBox		trace;
		
		RowHolder() {
			misc = new RowHolder_Misc();
			pip = new RowHolder_Pip();
			aut = new RowHolder_Aut();
			off = new RowHolder_Off();
			flo = new RowHolder_Flo();
			con = new RowHolder_Con();
		}
	}
	static class LabelHolder_Misc{
		//TextView		speed;
		TextView 		depth;
		TextView		zspeed;
		TextView		suction;
		TextView		suction_timer;
		TextView		grip;
		TextView		grip_timer;
	}
	static class LabelHolder_Pip{
		//TextView		aspirate;
		TextView		aspspeed;
		TextView		mix;
		TextView		blowout;
		TextView		droptip;		
	}
	static class LabelHolder_Aut{
		//TextView		arx;
		TextView		ary;
		TextView		arz;
		TextView		home;
	}
	static class LabelHolder_Off{
		//TextView		ox;
		TextView		oy;
		TextView		oz;
	}
	static class LabelHolder_Flo{
		//TextView		rowa;
		TextView		rowb;
		TextView		delay;
		TextView		times;
	}
	static class LabelHolder_Con{
		//TextView		sensor;
		TextView		condition;
		TextView		criterion;
		TextView		negative;
	}
	static class LabelHolder{
		TextView 			speed;
		TextView			aspirate;
		TextView			arx;
		TextView			ox;
		TextView			rowa;
		TextView			sensor;
		LabelHolder_Misc	misc;
		LabelHolder_Pip		pip;
		LabelHolder_Aut		aut;
		LabelHolder_Off		off;
		LabelHolder_Flo		flo;
		LabelHolder_Con		con;
		TextView			trace;
		
		LabelHolder() {
			misc = new LabelHolder_Misc();
			pip = new LabelHolder_Pip();
			aut = new LabelHolder_Aut();
			off = new LabelHolder_Off();
			flo = new LabelHolder_Flo();
			con = new LabelHolder_Con();
		}
	}
	
}
