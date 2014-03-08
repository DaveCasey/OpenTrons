package com.nwags.BetaBot;


import java.io.BufferedReader;
import java.io.FileReader;

import com.nwags.BetaBot.Support.Machine;
import com.nwags.BetaBot.Support.MixBook;
import com.nwags.BetaBot.Support.Template;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class JogFragment extends Fragment {
	private JogFragmentListener parent;
	private static final String TAG = "JogFragment";
	
	SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	private static float jogSlowRate[] = { 400f, 300f, 100f, 10f, 10f, 10f, 10f, 10f };
	private static final String CMD_ZERO_AXIS = "g28.3%s0";
	private static final String CMD_JOG_SLOW = "g91 f%f g1%s%f";
	private static final String CMD_JOG_FULL = "g91 g0%s%f";
	private static final String CMD_MOVE_ORIGIN = "g90g0x0y0z0a0";
	
	private static int[] allButtons = { R.id.xpos, R.id.xneg, R.id.ypos,
			R.id.yneg, R.id.zpos, R.id.zneg, R.id.apos, R.id.aneg, R.id.jogRate,
			R.id.xzero, R.id.yzero, R.id.zzero, R.id.azero,
			R.id.step_001, R.id.step_01, R.id.step_1, R.id.step_1_0, R.id.step_10,
			R.id.step_100, R.id.step_400,
			R.id.reset, R.id.go, 
			R.id.usenewspeed, R.id.factorD10, R.id.factorD100,
			R.id.speed_400, R.id.speed_300, R.id.speed_200, R.id.speed_100,
			R.id.home_sequence, R.id.grip, R.id.suction,
			R.id.xhome, R.id.yhome, R.id.zhome, R.id.ahome,
			R.id.tempXY, R.id.tempXYZ, R.id.sensor, R.id.toggle_status, R.id.adjFactorToggle,
			R.id.go_to,
			R.id.suction_timer_toggle, R.id.grip_timer_toggle,
			R.id.toggle_power,
			R.id.tempManual};
	
	private static int[] jogControls = {	R.id.xpos, R.id.xneg, 
											R.id.ypos, R.id.yneg, 
											R.id.zpos, R.id.zneg, 
											R.id.apos, R.id.aneg	}; 
	
	private ToggleButton jogRateButton;
	private ToggleButton speedButton;
	private EditText gCodeEditText;
	
	private ToggleButton fD10;
	private ToggleButton fD100;
	
	private EditText coeff;
	private EditText constance;
	private ToggleButton adjToggle;
	
	private EditText suctionTimer;
	private EditText gripTimer;
	
	private float factor = 1;
	
	private float jogStep = 1;
	private float jogSpeedX = 100f;
	private float jogSpeedY = 100f;
	private float jogSpeedZ = 100f;
	private float jogSpeedA = 100f;
	
	private float fullX;
	private float fullY;
	private float fullZ;
	private float fullA;
	private float threequartX;
	private float threequartY;
	private float threequartZ;
	private float threequartA;
	private float halfX;
	private float halfY;
	private float halfZ;
	private float halfA;
	private float quartX;
	private float quartY;
	private float quartZ;
	private float quartA;
	
	private float gtX, gtY, gtZ;
	
	private String mixbookfilename;
	private MixBook mixbook;
	private Template template;
	
	private int mixbookCode;
	
	private String[] tNames;
	
	private int factorSwitch=0;
	
	private boolean useSuctionTimer=false;
	private boolean useGripTimer=false;
	boolean jogActive = false;
	
	private Handler exHandler = new Handler();
	boolean showHX, showHY, showHZ, showHA;
	
	View view;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			parent = (JogFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement JogFragmentListener");
		}
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.jog, container, false);
		
		mContext = getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		
		String strAxes = settings.getString("nwa", "xyza");
		strAxes = strAxes.toLowerCase();
		
		if(strAxes.contains("x")) {
			showHX = true;
		} if(strAxes.contains("y")) {
			showHY = true;
		} if(strAxes.contains("z")) {
			showHZ = true;
		} if(strAxes.contains("a")) {
			showHA = true;
		}
		
		if(!showHX)
			((Button) view.findViewById(R.id.xhome)).setVisibility(8);
		if(!showHY)
			((Button) view.findViewById(R.id.yhome)).setVisibility(8);
		if(!showHZ)
			((Button) view.findViewById(R.id.zhome)).setVisibility(8);
		if(!showHA)
			((Button) view.findViewById(R.id.ahome)).setVisibility(8);
		
		((ToggleButton) view.findViewById(R.id.go_to)).setChecked(true);
		
		View v;
		
		gtX=gtY=gtZ=-1.0f;
		
		for (int id : allButtons) {
			v = view.findViewById(id);
			if(v.getTag()==null)
				v.setOnClickListener(clickListener);
		}
		

		for (int id : jogControls) { 
			v = view.findViewById(id);
			if(v.getTag()==null)
				v.setOnLongClickListener(jogHoldListener);
			if(v.getTag()==null)
				v.setOnTouchListener(jogReleaseListener);
		}

		// Rapid movement
		jogRateButton = (ToggleButton) view.findViewById(R.id.jogRate);
		
		jogRateButton.setChecked(true);
		
		mContext = getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		fullX = Integer.valueOf(settings.getString("1_velocity_max","15240"));
		fullY = Integer.valueOf(settings.getString("2_velocity_max","15240"));
		fullZ = Integer.valueOf(settings.getString("3_velocity_max","15240"));
		fullA = Integer.valueOf(settings.getString("4_velocity_max","15240"));
		threequartX = (fullX*(float)0.75);
		threequartY = (fullY*(float)0.75); 
		threequartZ = (fullZ*(float)0.75);
		threequartA = (fullA*(float)0.75);
		halfX = (fullX*(float)0.5);
		halfY = (fullY*(float)0.5);
		halfZ = (fullZ*(float)0.5);
		halfA = (fullA*(float)0.5);
		quartX = (fullX*(float)0.25);
		quartY = (fullY*(float)0.25);
		quartZ = (fullZ*(float)0.25);
		quartA = (fullA*(float)0.25);
		
		jogSpeedX = fullX;
		jogSpeedY = fullY;
		jogSpeedZ = fullZ;
		jogSpeedA = fullA;
		
		
		adjToggle = (ToggleButton) view.findViewById(R.id.adjFactorToggle);
		adjToggle.setChecked(settings.getBoolean("adjust", false));
		
		coeff = (EditText) view.findViewById(R.id.adjCoef);
		constance = (EditText) view.findViewById(R.id.adjConst);
		
		suctionTimer = (EditText) view.findViewById(R.id.suction_timer);
		gripTimer = (EditText) view.findViewById(R.id.grip_timer);
		
		coeff.setText(String.valueOf(settings.getFloat("coeff",1.0f)));
		constance.setText(String.valueOf(settings.getFloat("const",0.0f)));
		
		
		fD10 = (ToggleButton) view.findViewById(R.id.factorD10);
		fD100 = (ToggleButton) view.findViewById(R.id.factorD100);
		
		((RadioButton) view.findViewById(R.id.step_1_0)).setChecked(true);
		((RadioButton) view.findViewById(R.id.speed_100)).setChecked(true);
		
		((TextView)    view.findViewById(R.id.speedLabel)).setEnabled(false);
		((RadioButton) view.findViewById(R.id.speed_400)).setEnabled(false);
		((RadioButton) view.findViewById(R.id.speed_300)).setEnabled(false);
		((RadioButton) view.findViewById(R.id.speed_200)).setEnabled(false);
		((RadioButton) view.findViewById(R.id.speed_100)).setEnabled(false);
		
		fD10.setEnabled(false);
		fD100.setEnabled(false);
		
		speedButton = (ToggleButton) view.findViewById(R.id.usenewspeed);
		speedButton.setChecked(false);
		
		gCodeEditText = (EditText) view.findViewById(R.id.gcode);
		gCodeEditText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		gCodeEditText.setBackgroundColor(Color.YELLOW);
		
		if(parent.isLocked()){
			((TextView) view.findViewById(R.id.gcode_label)).setVisibility(8);
			((EditText) view.findViewById(R.id.gcode)).setVisibility(8);
			((Button) view.findViewById(R.id.go)).setVisibility(8);
			((Button) view.findViewById(R.id.xzero)).setVisibility(8);
			((Button) view.findViewById(R.id.yzero)).setVisibility(8);
			((Button) view.findViewById(R.id.zzero)).setVisibility(8);
			((Button) view.findViewById(R.id.azero)).setVisibility(8);
			//((LinearLayout)view.findViewById(R.id.fifth_line)).setVisibility(8);
			
			((ToggleButton)view.findViewById(R.id.adjFactorToggle)).setVisibility(8);
			((EditText)view.findViewById(R.id.adjConst)).setVisibility(8);
			((EditText)view.findViewById(R.id.adjCoef)).setVisibility(8);
			((TextView)view.findViewById(R.id.coeffLabel)).setVisibility(8);
			((TextView)view.findViewById(R.id.constLabel)).setVisibility(8);
			((TextView)view.findViewById(R.id.adjReading)).setVisibility(8);
			
		}
		
		if(parent.isStatus())
			((ToggleButton) view.findViewById(R.id.toggle_status)).setChecked(true);
		else
			((ToggleButton) view.findViewById(R.id.toggle_status)).setChecked(false);
		
		if(factorSwitch==0){
			fD10.setChecked(false);
			fD100.setChecked(false);
			factor=1;
		} else if(factorSwitch==1){
			fD10.setChecked(false);
			fD100.setChecked(false);
			factor=100;
		}else if(factorSwitch==2){
			fD10.setChecked(false);
			fD100.setChecked(false);
			factor=10;
		}
		
		mixbookfilename = settings.getString("mixbookfilename",Environment.getExternalStorageDirectory().getPath() + "/OpenTrons/defaultMixBook.txt");
        
		fileOpen(mixbookfilename);
		
		int ingrPos = settings.getInt("ingrPos", 0);
		boolean gotoToggle = settings.getBoolean("gotoToggle",false);
		com.nwags.BetaBot.NoDefaultSpinner listview = (com.nwags.BetaBot.NoDefaultSpinner) view.findViewById(R.id.ingredientsList);
		listview.setSelection(ingrPos);
		((ToggleButton) view.findViewById(R.id.go_to)).setChecked(gotoToggle);
		return view;
	}

	public interface JogFragmentListener {
		void sendGcode(String cmd);
	
		void sendCommand(String cmd);
		
		void sendReset();

		void stopMove();

		void resumeMove();

		void pauseMove();

		int queueSize();

		void goHome();
		
		void homeAll();
		
		void homeX();
		
		void homeY();
		
		void homeZ();
		
		void homeA();
		
		void templateXY();
		
		void templateXYZ();
		
		void sensor();
		
		boolean isLocked();
		
		boolean isStatus();
		
		void toggleStatus();
		
		void templateManual();
		
	}
	
	@SuppressLint("DefaultLocale")
	private String getRateByAxis(int axis, float jogSpeed,float step) {
		if(speedButton.isChecked()){
			
			return String.format(CMD_JOG_SLOW, jogSpeed, Machine.axisIndexToName[axis], step);
		}else{
			if (jogRateButton.isChecked())
				return String.format(CMD_JOG_FULL, Machine.axisIndexToName[axis], step);
			else
				return String.format(CMD_JOG_SLOW, jogSlowRate[axis], Machine.axisIndexToName[axis], step);
		}
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		public void onClick(View v) {

			// These buttons should work all the time
			switch (v.getId()) {
			case R.id.step_001:
				jogStep = 0.001f;
				break;
			case R.id.step_01:
				jogStep = 0.01f;
				break;
			case R.id.step_1:
				jogStep = 0.1f;
				break;
			case R.id.step_1_0:
				jogStep = 1.0f;
				break;
			case R.id.step_10:
				jogStep = 10.0f;
				break;		
			case R.id.step_100:
				jogStep = 100.0f;
				break;	
			case R.id.step_400:
				jogStep = 400.0f;
				break;	
			case R.id.xpos:
				parent.sendGcode(getRateByAxis(0, jogSpeedX*factor, jogStep));
				break;
			case R.id.xneg:
				parent.sendGcode(getRateByAxis(0, jogSpeedX*factor, -jogStep));
				break;
			case R.id.ypos:
				parent.sendGcode(getRateByAxis(1, jogSpeedY*factor, jogStep));
				break;
			case R.id.yneg:
				parent.sendGcode(getRateByAxis(1, jogSpeedY*factor, -jogStep));
				break;
			case R.id.zpos:
				parent.sendGcode(getRateByAxis(2, jogSpeedZ*factor, jogStep));
				break;
			case R.id.zneg:
				parent.sendGcode(getRateByAxis(2, jogSpeedZ*factor, -jogStep));
				break;
			case R.id.apos:
				parent.sendGcode(getRateByAxis(3, jogSpeedA*factor, jogStep));
				break;
			case R.id.aneg:
				parent.sendGcode(getRateByAxis(3, jogSpeedA*factor, -jogStep));
				break;
			case R.id.azero:
				parent.sendGcode(String.format(CMD_ZERO_AXIS, "a"));
				break;
			case R.id.zzero:
				parent.sendGcode(String.format(CMD_ZERO_AXIS, "z"));
				break;
			case R.id.yzero:
				parent.sendGcode(String.format(CMD_ZERO_AXIS, "y"));
				break;
			case R.id.xzero:
				parent.sendGcode(String.format(CMD_ZERO_AXIS, "x"));
				break;
			case R.id.reset:
				parent.sendReset();
				break;
			case R.id.go:
				String text = ((EditText)view.findViewById(R.id.gcode)).getText().toString();
				parent.sendGcode(text);
				break;
			case R.id.usenewspeed:
				if(speedButton.isChecked()){
					jogRateButton.setEnabled(false);
					((TextView)    view.findViewById(R.id.speedLabel)).setEnabled(true);
					((RadioButton) view.findViewById(R.id.speed_400)).setEnabled(true);
					((RadioButton) view.findViewById(R.id.speed_300)).setEnabled(true);
					((RadioButton) view.findViewById(R.id.speed_200)).setEnabled(true);
					((RadioButton) view.findViewById(R.id.speed_100)).setEnabled(true);
					fD10.setEnabled(true);
					fD100.setEnabled(true);
				}else{
					jogRateButton.setEnabled(true);
					((TextView)    view.findViewById(R.id.speedLabel)).setEnabled(false);
					((RadioButton) view.findViewById(R.id.speed_400)).setEnabled(false);
					((RadioButton) view.findViewById(R.id.speed_300)).setEnabled(false);
					((RadioButton) view.findViewById(R.id.speed_200)).setEnabled(false);
					((RadioButton) view.findViewById(R.id.speed_100)).setEnabled(false);
					fD10.setEnabled(false);
					fD100.setEnabled(false);
				}
				break;
			case R.id.factorD10:
				if(debug)
					Log.d(TAG,"factorD10 A|factorSwitch: "+String.valueOf(factorSwitch));
				if(factorSwitch!=1){
					factor=(float)0.1;
					factorSwitch=1;
					fD100.setChecked(false);
				}else{
					factor = 1;
					factorSwitch=0;
				}
				if(debug)
					Log.d(TAG,"factorD10 B|factorSwitch: "+String.valueOf(factorSwitch));
				break;
			case R.id.factorD100:
				if(debug)
					Log.d(TAG,"factorD100 A|factorSwitch: "+String.valueOf(factorSwitch));
				if(factorSwitch!=2){
					factor=(float)0.01;
					factorSwitch=2;
					fD10.setChecked(false);
				}else{
					factor = 1;
					factorSwitch=0;
				}
				if(debug)
					Log.d(TAG,"factorD100 B|factorSwitch: "+String.valueOf(factorSwitch));
				break;
			
			case R.id.grip:
				if (((ToggleButton) v).isChecked()){
					parent.sendGcode("M20");
					if(useGripTimer){
						if(gripTimer.getText()!=null){
							try{
								float tsecs = Float.parseFloat(gripTimer.getText().toString())*1000f;
								String tStr = String.format("%.3f", tsecs);
								tsecs = Float.parseFloat(tStr)*1000f;
								long msecs = (long)tsecs;
								PDSendGcode pdsg = new PDSendGcode(1, v);
								exHandler.postDelayed(pdsg, msecs);
							}catch(Exception e){
								long secs = 1l;
								PDSendGcode pdsg = new PDSendGcode(1, v);
								exHandler.postDelayed(pdsg, secs*1000l);
							}
						}
					}
				}else{
					parent.sendGcode("M21");
				}
				break;
			case R.id.suction:
				if (((ToggleButton) v).isChecked()){
					parent.sendGcode("M18");
					if(useSuctionTimer){
						if(suctionTimer.getText()!=null){
							try{
								float tsecs = Float.parseFloat(suctionTimer.getText().toString())*1000f;
								String tStr = String.format("%.3f", tsecs);
								tsecs = Float.parseFloat(tStr)*1000f;
								long msecs = (long)tsecs;
								PDSendGcode pdsg = new PDSendGcode(0, v);
								exHandler.postDelayed(pdsg, msecs);
							}catch(Exception e){
								long secs = 1l;
								PDSendGcode pdsg = new PDSendGcode(0, v);
								exHandler.postDelayed(pdsg, secs*1000l);
							}
						}
					}
				}else{
					parent.sendGcode("M19");
				}
				break;
			case R.id.home_sequence:
				parent.homeAll();
				break;
			case R.id.xhome:
				parent.homeX();
				break;
			case R.id.yhome:
				parent.homeY();
				break;
			case R.id.zhome:
				parent.homeZ();
				break;
			case R.id.ahome:
				parent.homeA();
				break;
			case R.id.tempXY:
				parent.templateXY();
				break;
			case R.id.tempXYZ:
				parent.templateXYZ();
				break;
			case R.id.sensor:
				parent.sensor();
				break;
			case R.id.toggle_status:
				parent.toggleStatus();
				break;
			case R.id.adjFactorToggle:
				if(adjToggle.isChecked()){
					SharedPreferences.Editor Ed = settings.edit();
					Ed.putBoolean("adjust",true);
					try{
						float consty = Float.parseFloat(constance.getText().toString());
						float coeffy = Float.parseFloat(coeff.getText().toString());
						Ed.putFloat("coeff", coeffy);
						Ed.putFloat("const", consty);
					}catch(Exception ex1){
						ex1.printStackTrace();
						Ed.putFloat("coeff", 1.0f);
						Ed.putFloat("const",0.0f);
					}
					Ed.commit();
				}else{
					SharedPreferences.Editor Ed = settings.edit();
					Ed.putBoolean("adjust",false);
					Ed.commit();
				}
				break;
			case R.id.speed_400:
				jogSpeedX = fullX;
				jogSpeedY = fullY;
				jogSpeedZ = fullZ;
				jogSpeedA = fullA;
				break;
			case R.id.speed_300:
				jogSpeedX = threequartX;
				jogSpeedY = threequartY;
				jogSpeedZ = threequartZ;
				jogSpeedA = threequartA;
				break;
			case R.id.speed_200:
				jogSpeedX = halfX;
				jogSpeedY = halfY;
				jogSpeedZ = halfZ;
				jogSpeedA = halfA;
				break;
			case R.id.speed_100:
				jogSpeedX = quartX;
				jogSpeedY = quartY;
				jogSpeedZ = quartZ;
				jogSpeedA = quartA;
				break;
			case R.id.go_to:
				if(gtX!=-1.0f){
					if (((ToggleButton) v).isChecked()){
						parent.sendGcode(String.format("g90 f%f g1z0", jogSpeedZ*factor));
						parent.sendGcode(String.format("g90 f%f g1x0y0", jogSpeedX*factor));
					}else{
						parent.sendGcode(String.format("g90 f%f g1x%fy%f", jogSpeedX*factor,gtX,gtY));
						parent.sendGcode(String.format("g90 f%f g1z%f", jogSpeedZ*factor, gtZ));
					}
					
				}
				break;
			case R.id.suction_timer_toggle:
				if(((ToggleButton) v).isChecked())
					useSuctionTimer = true;
				else
					useSuctionTimer = false;
			case R.id.grip_timer_toggle:
				if(((ToggleButton) v).isChecked())
					useGripTimer = true;
				else
					useGripTimer = false;
			case R.id.toggle_power:
				if(((ToggleButton) v).isChecked()){
					parent.sendCommand("$md\n");
					//parent.sendCommand("{\"aam\":2\n");
				}else{
					parent.sendCommand("$me\n");
					//parent.sendCommand("{\"aam\":3}\n");
				}
				break;
			case R.id.tempManual:
				parent.templateManual();
				break;
			}
		}
	};
	
	private View.OnLongClickListener jogHoldListener = new View.OnLongClickListener() {
		public boolean onLongClick(View v) {
			switch (v.getId()) {
			case R.id.xpos:
				parent.sendGcode(getRateByAxis(0, jogSpeedX*factor, 100f));
				jogActive = true;
				return true;
			case R.id.xneg:
				parent.sendGcode(getRateByAxis(0, jogSpeedX*factor, -100f));
				jogActive = true;
				return true;
			case R.id.ypos:
				parent.sendGcode(getRateByAxis(1, jogSpeedY*factor, 100f));
				jogActive = true;
				return true;
			case R.id.yneg:
				parent.sendGcode(getRateByAxis(1, jogSpeedY*factor,-100f));
				jogActive = true;
				return true;
			case R.id.zpos:
				parent.sendGcode(getRateByAxis(2, jogSpeedZ*factor,100f));
				jogActive = true;
				return true;
			case R.id.zneg:
				parent.sendGcode(getRateByAxis(2, jogSpeedZ*factor,-100f));
				jogActive = true;
				return true;
			case R.id.apos:
				parent.sendGcode(getRateByAxis(3, jogSpeedA*factor,100f));
				jogActive = true;
				return true;
			case R.id.aneg:
				parent.sendGcode(getRateByAxis(3, jogSpeedA*factor,-100f));
				jogActive = true;
				return true;
			}
			return false;
		}
	};
	
	private View.OnTouchListener jogReleaseListener = new View.OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (!jogActive)
					return false;
				jogActive = false;
				switch (v.getId()) {
				case R.id.xpos:
				case R.id.xneg:
				case R.id.ypos:
				case R.id.yneg:
				case R.id.zpos:
				case R.id.zneg:
					parent.stopMove();
					return false;
				}
			}
			return false;
		}
	};
	
	// Used to use this for enable/disable of buttons, but maybe it's ok to
	// queue things up.
	public void updateState(Machine m) {
		for (int i=0; i < 6; i++)
			jogSlowRate[i] = (m.getAxisBundle(i).getFloat("vm"))/2f;
	}
	
	public void disableJogControls(){
		Button button;
		button = (Button) view.findViewById(R.id.xpos);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.xneg);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.xzero);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.xhome);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.ypos);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.yneg);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.yzero);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.yhome);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.zpos);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.zneg);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.zzero);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.zhome);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.apos);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.aneg);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.azero);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.ahome);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.g28);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.go);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.home_sequence);
		button.setEnabled(false);
		button = (Button) view.findViewById(R.id.home);
		button.setEnabled(false);
	}
	
	public void enableJogControls(){
		Button button;
		button = (Button) view.findViewById(R.id.xpos);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.xneg);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.xzero);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.xhome);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.ypos);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.yneg);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.yzero);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.yhome);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.zpos);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.zneg);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.zzero);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.zhome);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.apos);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.aneg);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.azero);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.ahome);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.g28);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.go);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.home_sequence);
		button.setEnabled(true);
		button = (Button) view.findViewById(R.id.home);
		button.setEnabled(true);
	}
	
	
	public void setReading(String _reading){
		TextView tv = (TextView) view.findViewById(R.id.reading);
		tv.setText(_reading);
	}
	
	public void setAdjusted(String _reading){
		TextView tv = (TextView) view.findViewById(R.id.adjReading);
		tv.setText(_reading);
	}
	
	public void errorReading(){
		TextView tv = (TextView) view.findViewById(R.id.reading);
		tv.setText("no sensor?");
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
				
				template = mixbook.getTemplate();
				String[] values = new String[template.size()+1];
				values[0] = " ";
				tNames = new String[template.size()+1];
				tNames[0] = " ";
				String ingrName = null;
				for(int i=0;i<template.size();i++) {
					ingrName = template.getIngredient(i);
					tNames[i+1] = template.getIngredient(i);
					values[i+1] = template.getIngredient(i)+"\n ( "+template.getX(ingrName)+" , "+
					template.getY(ingrName)+" , "+template.getZ(ingrName)+" )";
				}
				final com.nwags.BetaBot.NoDefaultSpinner listview = (com.nwags.BetaBot.NoDefaultSpinner) view.findViewById(R.id.ingredientsList);
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, values);
				listview.setAdapter(adapter);
				listview.setOnItemSelectedListener(
						new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								String puck = tNames[listview.getSelectedItemPosition()];
								if(!puck.equals(" ")) {
									gtX=Float.parseFloat(template.getX(puck));
									gtY=Float.parseFloat(template.getY(puck));
									gtZ=Float.parseFloat(template.getZ(puck));
								}else {
									gtX=gtY=gtZ=-1.0f;
								}
								SharedPreferences.Editor Ed = settings.edit();
								Ed.putInt("ingrPos", arg2);
								Ed.putBoolean("gotoToggle", true);
								Ed.commit();
								((ToggleButton) view.findViewById(R.id.go_to)).setChecked(true);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {	}
							
						});
			}else{
				
				String[] values = new String[]{"Missing Template"};
				final com.nwags.BetaBot.NoDefaultSpinner listview = (com.nwags.BetaBot.NoDefaultSpinner) view.findViewById(R.id.ingredientsList);
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, values);
				listview.setAdapter(adapter);
			}
			if(br!=null)
				br.close();
		}catch (Exception e){
			e.printStackTrace();
			mixbook.clear();
			template.clear();
			String[] values = new String[]{"Error Loading Template"};
			final com.nwags.BetaBot.NoDefaultSpinner listview = (com.nwags.BetaBot.NoDefaultSpinner) view.findViewById(R.id.ingredientsList);
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, values);
			listview.setAdapter(adapter);
		}
	}
	
	public void updateIngredients(){
		
		mixbookCode = mixbook.Inflate(settings.getString("mixbook", mixbook.toString()));
		if(mixbookCode>0){
			
			template = mixbook.getTemplate();
			String[] values = new String[template.size()+1];
			values[0] = " ";
			tNames = new String[template.size()+1];
			tNames[0] = " ";
			String ingrName = null;
			for(int i=0;i<template.size();i++) {
				ingrName = template.getIngredient(i);
				tNames[i+1] = template.getIngredient(i);
				values[i+1] = template.getIngredient(i)+" ( "+template.getX(ingrName)+" , "+
				template.getY(ingrName)+" , "+template.getZ(ingrName)+" )";
			}
			final com.nwags.BetaBot.NoDefaultSpinner listview = (com.nwags.BetaBot.NoDefaultSpinner) view.findViewById(R.id.ingredientsList);
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, values);
			listview.setAdapter(adapter);
			listview.setOnItemSelectedListener(
					new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							String puck = tNames[listview.getSelectedItemPosition()];
							if(!puck.equals(" ")){
								gtX=Float.parseFloat(template.getX(puck));
								gtY=Float.parseFloat(template.getY(puck));
								gtZ=Float.parseFloat(template.getZ(puck));
							}else{
								gtX=gtY=gtZ=-1.0f;
							}
							
							SharedPreferences.Editor Ed = settings.edit();
							Ed.putInt("ingrPos", arg2);
							Ed.putBoolean("gotoToggle", true);
							Ed.commit();
							((ToggleButton) view.findViewById(R.id.go_to)).setChecked(true);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {	}
						
					});
		}
	}
	
	public class PDSendGcode implements Runnable{
		private int cmd;
		private View view;
		
		PDSendGcode(int _cmd, View _view){
			cmd = _cmd;
			view = _view;
		}
		
		@Override
		public void run() {
			ToggleButton tgBtn;
			switch(cmd){
			case 0:
				parent.sendGcode("M19");
				tgBtn = (ToggleButton) view.findViewById(R.id.suction);
				tgBtn.setChecked(false);
				break;
			case 1:
				parent.sendGcode("M21");
				tgBtn = (ToggleButton) view.findViewById(R.id.grip);
				tgBtn.setChecked(false);
				break;
				default:
					break;
			}
		}
	}
	
	
}
