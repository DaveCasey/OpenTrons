package com.nwags.BetaBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Locale;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;


import com.nwags.BetaBot.ConfigFragment.ConfigFragmentListener;
import com.nwags.BetaBot.Bluetooth.BluetoothSerialService;
import com.nwags.BetaBot.Support.BetaBotService;
import com.nwags.BetaBot.Support.Command;
import com.nwags.BetaBot.Support.Config;
import com.nwags.BetaBot.Support.Machine;
import com.nwags.BetaBot.Support.MixBook;
import com.nwags.BetaBot.Support.Recipe;
import com.nwags.BetaBot.Support.Template;
import com.nwags.BetaBot.USBHost.USBHostService;
import com.nwags.BetaBot.USBAccessory.USBAccessoryService;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements
	JogFragment.JogFragmentListener, CommandFragment.CommandFragmentListener, 
	StatusFragment.StatusFragmentListener, HomeFragment.HomeFragmentListener,
	ConfigFragmentListener {
	
	public static final String TAG = "MainActivity";
	public static int bindType = 0;
	public static boolean connected = false;
	public static ServiceConnection currentServiceConnection;
	public static BroadcastReceiver mIntentReceiver;
	public static PrefsListener mPreferencesListener;
	public static boolean pendingConnect = false;
	public static BetaBotService BetaBot = null;
	public static BluetoothSerialService BTBetaBot = null;
	
	public static SharedPreferences settings;
	public static Context mContext;
	public static boolean debug;
	
	public static StringBuilder mText = new StringBuilder();
	
	// bluetooth
	// Message types sent from the BluetoothReadService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;	
	
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	public static final int REQUEST_WTF = 3;
	
	private static final int REQUEST_TEMPLATE = 7;
	private static final int REQUEST_RECIPE = 8;
	private static final int REQUEST_ADDRECIPE = 9;
	private static final int REQUEST_MIXBOOK = 6;
	private static final int REQUEST_BOOM = 11;
	
	private static final int REQUEST_CONFIG = 12;
	
	public static int GOTO_TAB = 0;
	
	public static BluetoothAdapter mBluetoothAdapter = null;
	public static String mConnectedDeviceName = null;
	public static MenuItem mMenuItemConnect;
	public static final int TEXT_MAX_SIZE = 8192;
	public final static String BR = System.getProperty("line.separator");
	
	public static boolean show_opening_settings_dialog = false;	// not used anymore, might get deleted	
	public static StringBuilder dialogSB = new StringBuilder();
	public static Typeface tf;
	
	public static boolean homingXYZA = false;
	public static boolean homingXY = false;
	public static boolean homingX = false;
	public static boolean homingY = false;
	public static boolean homingZ = false;
	public static boolean homingA = false;
	public static boolean cancel = false;
	
	public static int homingFlag = 0;
	
	public static float posX, posY, posZ;
	public static String mixbookFilename;
	public static String templateFilename;
	public static MixBook mixbook;
	public static Template template;
	public static Command command;
	public static int mixbook_code;
	public static boolean has_template;
	public static boolean sensing = false;
	public static boolean startup_sequence = false;
	public static boolean system_sequence = false;
	public static boolean motor_sequence = false;
	public static boolean axis_sequence = false;
	public static boolean settings_sequence = false;
	public static boolean locked = true;
	
	public class SysFlags{
		public boolean si=true;
		public boolean st=true;
		public boolean ej=true;
		public boolean jv=true;
		public boolean tv=true;
		public boolean qv=true;
		public boolean sv=true;
		public boolean gpl=true;
		public boolean gun=true;
		public boolean gco=true;
		public boolean gpa=true;
		public boolean gdi=true;
		
		SysFlags(){}
	}
	private SysFlags sysFlags;
	
	public class MotFlags{
		public boolean ma=true;
		public boolean sa=true;
		public boolean tr=true;
		public boolean mi=true;
		public boolean po=true;
		public boolean pm=true;
	}
	public MotFlags motFlags1;
	public MotFlags motFlags2;
	public MotFlags motFlags3;
	public MotFlags motFlags4;
	
	public class AxiFlags{
		public boolean am=true;
		public boolean vm=true;
		public boolean fr=true;
		public boolean tm=true;
		public boolean jm=true;
	}
	public static AxiFlags axiFlagsX;
	public static AxiFlags axiFlagsY;
	public static AxiFlags axiFlagsZ;
	public static AxiFlags axiFlagsA;
	
	public static ProgressDialog progressBar;
	public static ProgressDialog progressBart;
	public static ProgressDialog progressBarn;
	public static boolean reading_configs = false;
	public static boolean connecting;
	public boolean showStatus = false;
	
	public static int fullX;
	public static int fullY;
	public static int fullZ;
	public static int fullA;
	public static boolean proceed = false;
	
	public static Handler systemHandler = new Handler();
	public static Handler motorHandler = new Handler();
	public static Handler axisHandler = new Handler();
	public static Handler settingsHandler = new Handler();
	public static Handler homingHandler = new Handler();
	
	public static float xHome=10.0f, xSendHome=-1500.0f, xTapA=-1.0f, xTapB=10.0f, xNudge=10.0f;
	public static float yHome=10.0f, ySendHome=-250.0f, yTapA=-1.0f, yTapB=10.0f, yNudge=10.0f;
	public static float zHome=4.0f, zSendHome=-200.0f, zTapA=-0.1f, zTapB=10.0f, zNudge=10.0f;
	public static float aHome=4.0f, aSendHome=-40.0f, aTapA=-0.5f, aTapB=10.0f, aNudge=5.0f;
	
	public static float xHomeF=50.0f, xSendHomeF=10.0f, xTapAF=50.0f, xTapBF=50.0f, xNudgeF=50.0f;
	public static float yHomeF=50.0f, ySendHomeF=10.0f, yTapAF=50.0f, yTapBF=50.0f, yNudgeF=50.0f;
	public static float zHomeF=50.0f, zSendHomeF=50.0f, zTapAF=50.0f, zTapBF=50.0f, zNudgeF=50.0f;
	public static float aHomeF=3.0f, aSendHomeF=3.0f, aTapAF=3.0f, aTapBF=3.0f, aNudgeF=3.0f;
	
	public static boolean aGateA=false, aGateB=true;
	public static boolean xGateA=false, xGateB=true;
	public static boolean yGateA=false, yGateB=true;
	public static boolean zGateA=false, zGateB=true;
	
	public static boolean adjust = false;
	public static float coeffy, consty, tempting;
	public static String temperature;
	
	public static boolean ads = false;
	
	public static boolean includeIngredient=false;
	public static float temporaryX, temporaryY, temporaryZ;
	public static Machine machine;
	public static StringBuilder homeSB = null;
	public static StringBuilder settSB = null;
	public static boolean doX=false, doY=false, doZ=false, doA=false, doB=false, doC=false;
	
	public static String configFilename;
	public static boolean loading_settings;
	public static String key, value;
	
	public static String[] tabsL;
	public static TabListener tabListenerL;
	public static SharedPreferences.Editor EdL;
	
	public static int tries = 0;
	public static int collegeTries = 0;
	public static boolean trying = false;
	public static ArrayList<String> configurations;
	public static Config config;
	public static int settingsIndex = 0;
	
	public static StringBuilder generalSB = new StringBuilder();
	public static int peck=0;
	public static View view;
	
	public boolean bindDriver(ServiceConnection s)
	{
		if(debug)
			Log.d(TAG, "bindDriver(ServiceConnection s)");
		
		switch (bindType) {
		case 0: // BlueTooth
			
			//return bindService(new Intent(getApplicationContext(),
			//		TinyGNetwork.class), s, Context.BIND_AUTO_CREATE);
			
			ComponentName mBTService = startService(new Intent(this, BluetoothSerialService.class));
			return bindService(new Intent(this, BluetoothSerialService.class)
				, s, Context.BIND_AUTO_CREATE);
			
			
		case 1: // USB host
			// Check to see if the platform supports USB host
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
				Toast.makeText(this, R.string.no_usb_host, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			ComponentName mUSBHService = startService(new Intent(this, USBHostService.class));
			return bindService(new Intent(getApplicationContext(),
					USBHostService.class), s, Context.BIND_AUTO_CREATE);
		case 2: // USB accessory
			// Check to see if the platform support USB accessory
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				Toast.makeText(this, R.string.no_usb_accessory,
						Toast.LENGTH_SHORT).show();
				return false;
			}
			ComponentName mUSBAService = startService(new Intent(this, USBAccessoryService.class));
			return bindService(new Intent(getApplicationContext(),
					USBAccessoryService.class), s, Context.BIND_AUTO_CREATE);
		default:
			return false;
		}
	}
	
	private void restoreState(Bundle inState)
	{
		MainActivity.bindType = inState.getInt("bindType");
		MainActivity.connected = inState.getBoolean("connected");
		if(debug)
			Log.d("BetaBot","restoreState() connected state is " + MainActivity.connected);
		
	}
	
	public boolean connectionState()
	{
		return MainActivity.connected;
	}
	
	public void myClickHandler(View view)
	{
		android.support.v4.app.Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
		if (f == null)
			return;
		
		if (f.getClass() == FileFragment.class)
			((FileFragment) f).myClickHandler(view);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getActionBar();
		setContentView(R.layout.activity_main);
		
		view = this.findViewById(R.layout.activity_main);
		
		mContext = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		
		mixbookFilename = settings.getString("mixbookfilename",Environment.getExternalStorageDirectory().getPath() + "/OpenTrons/defaultMixBook.txt");
		mixbook = new MixBook();
		mixbook_code = mixbook.Inflate(settings.getString("mixbook","nothing"));
		
		config = new Config();
		
		
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Resources res = getResources();
		String[] tabs = res.getStringArray(R.array.tabArray);
		TabListener tabListener = (TabListener) new MainTabListener();
		for(int i=0; i < 3; i++){//tabs.length
			Tab tab = actionBar.newTab();
			tab.setText(tabs[i]);
			tab.setTag(tabs[i]);
			tab.setTabListener(tabListener);
			actionBar.addTab(tab);
		}
		
		bindType = Integer.parseInt(settings.getString("connectionType","0"));
		
		mPreferencesListener = new PrefsListener();
		settings.registerOnSharedPreferenceChangeListener(mPreferencesListener);
		SharedPreferences.Editor Ed = settings.edit();
		Ed.putString("locked", "true");
		Ed.commit();
		mixbookFilename = settings.getString("mixbookfilename",Environment.getExternalStorageDirectory().getPath() + "/OpenTrons/defaultMixBook.txt");
		templateFilename = settings.getString("templatefilename", Environment.getExternalStorageDirectory().getPath() + "/OpenTrons/defaultTemplate.txt");
		
		fullX = Integer.valueOf(settings.getString("1vm", "10240"));
		fullY = Integer.valueOf(settings.getString("2vm", "10240"));
		fullZ = Integer.valueOf(settings.getString("3vm", "10240"));
		fullA = Integer.valueOf(settings.getString("4vm", "36000"));
		
		if(savedInstanceState!=null)
			restoreState(savedInstanceState);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		mText.setLength(0);
		mText.append(settings.getString("rawness", ""));
		Log.d(TAG,"mText1: "+mText.toString());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment f = new StatusFragment();
		if(findViewById(R.id.statusF)!=null)
			ft.replace(R.id.statusF, f);
		else
			ft.add(R.id.statusF, f);
		ft.commit();
		
		if(showStatus)
			showStatus();
		else
			hideStatus();
		
		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener(){
			
			@Override
			public void onBackStackChanged() {
				Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
				final ActionBar actionBar = getActionBar();
				if(f!=null){
					if(f.getClass()==FileFragment.class) {
						actionBar.setSelectedNavigationItem(4);
					}else if(f.getClass()==HomeFragment.class) {
						actionBar.setSelectedNavigationItem(0);
					}else if(f.getClass()==JogFragment.class) {
						actionBar.setSelectedNavigationItem(2);
					}else if(f.getClass()==CommandFragment.class) {
						actionBar.setSelectedNavigationItem(3);
					}else if(f.getClass()==MixBookFragment.class) {
						actionBar.setSelectedNavigationItem(1);
					}else if(f.getClass()==ConfigFragment.class) {
						actionBar.setSelectedNavigationItem(5);
					}
				}
			}
		});
		
		
		
		configFilename = settings.getString("configfilename",Environment.getExternalStorageDirectory().getPath()+"/OpenTrons/config.txt");
		machine = new Machine(this);
		File storagePath = new File(Environment.getExternalStorageDirectory().getPath() + "/OpenTrons");
	    storagePath.mkdirs();
	    readConfigs(configFilename);
		
	}
	
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt("bindType", bindType);
		outState.putBoolean("connected", connected);
		if(debug)
			Log.d(TAG, "onSaveInstanceState() connected state is "
				+ connected);
		
	}
	
	public void onDestroy()
	{
		
		if(bindType==0){
			if(MainActivity.BTBetaBot != null)
			{
				unbindService(MainActivity.currentServiceConnection);
				MainActivity.BTBetaBot = null;
			}
		}else{
			if(MainActivity.BetaBot != null)
			{
				unbindService(MainActivity.currentServiceConnection);
				MainActivity.BetaBot = null;
			}
		}
		super.onDestroy();
	}
	
	//	Jog functions
	public void goHome()
	{
		String command = "g28.2";
		
		if(bindType==0){
			if((MainActivity.BTBetaBot == null) || (!MainActivity.connected))
			{
				return;
			}
			int i = 0;
			while(true)
			{
				if(i>=3)
				{
					if(debug)
						Log.d("BTBetaBot","home command: " + command);
					
					MainActivity.BTBetaBot.send_gcode(command);
					return;
				}
				Bundle b = MainActivity.BTBetaBot.getAxis(i);
				if(b.getInt("am")==1||b.getInt("am")==3)
					command = command + com.nwags.BetaBot.Support.Machine.axisIndexToName[i] + "0";
					
				i++;
			}
		}else{
			if((MainActivity.BetaBot == null) || (!MainActivity.connected))
			{
				return;
			}
			int i = 0;
			while(true)
			{
				if(i>=3)
				{
					if(debug)
						Log.d(TAG,"home command: " + command);
					
					MainActivity.BetaBot.send_gcode(command);
					return;
				}
				Bundle b = MainActivity.BetaBot.getAxis(i);
				if(b.getInt("am")==1||b.getInt("am")==3)
					command = command + com.nwags.BetaBot.Support.Machine.axisIndexToName[i] + "0";
					
				i++;
			}
		}
	}
	
	public void pauseMove()
	{
		if(bindType==0){
			if((MainActivity.BTBetaBot==null)||(!MainActivity.connected))
				return;
				
			MainActivity.BTBetaBot.send_pause();
		}else{
			if((MainActivity.BetaBot==null)||(!MainActivity.connected))
				return;
				
			MainActivity.BetaBot.send_pause();
		}
	}
	
	public int queueSize()
	{
		if(bindType==0){
			if((MainActivity.BTBetaBot == null)||(!MainActivity.connected))
				return -1;
				
			return MainActivity.BTBetaBot.queueSize();
		}else{
			if((MainActivity.BetaBot == null)||(!MainActivity.connected))
				return -1;
				
			return MainActivity.BetaBot.queueSize();
		}
	}
	
	public void resumeMove()
	{
		if(bindType==0){
			if((MainActivity.BTBetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BTBetaBot.send_resume();
		}else{
		if((MainActivity.BetaBot == null)||(!MainActivity.connected))
			return;
		
		MainActivity.BetaBot.send_resume();
		}
	}
	
	public void sendGcode(String cmd)
	{
		if(bindType==0){
			if((MainActivity.BTBetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BTBetaBot.send_gcode(cmd);
		}else{
			if((MainActivity.BetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BetaBot.send_gcode(cmd);
		}
	}
	
	public void sendReset()
	{
		if(bindType==0){
			if((MainActivity.BTBetaBot == null) || (!MainActivity.connected))
				return;
			
			MainActivity.BTBetaBot.send_reset();
		}else{
			if((MainActivity.BetaBot == null) || (!MainActivity.connected))
				return;
			
			MainActivity.BetaBot.send_reset();
		}
	}
	
	public void stopMove()
	{
		if(bindType==0){
			if((MainActivity.BTBetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BTBetaBot.send_stop();
		}else{
			if((MainActivity.BetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BetaBot.send_stop();
		}
	}
	
	//	
	
	private class DriverServiceConnection implements ServiceConnection
	{
		private DriverServiceConnection() { }
		
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			BetaBotService.BetaBotBinder binder = (BetaBotService.BetaBotBinder)service;
			if(debug)
				Log.d(TAG,"Service connected");
			
			
			if(bindType==0){
				MainActivity.BTBetaBot = (BluetoothSerialService) binder.getService();
				BTBetaBot.setHandler(mHandlerBT);
				
				if((mBluetoothAdapter!=null)&&(!mBluetoothAdapter.isEnabled())){
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage(R.string.alert_dialog_turn_on_bt)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.alert_dialog_warning_title)
					.setCancelable( false )
					.setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//mEnablingBT = true;
							Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
							startActivityForResult(enableIntent, REQUEST_ENABLE_BT);			
						}
					})
					.setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finishDialogNoBluetooth();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}else if(getBluetoothConnectionState()==BluetoothSerialService.STATE_NONE){
					Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
					startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				} else if (getBluetoothConnectionState()==BluetoothSerialService.STATE_CONNECTED){
					BTBetaBot.stop();
					BTBetaBot.start();
				}
			} else {
				MainActivity.BetaBot = binder.getService();
				AlertDialog.Builder settingsBldr = new AlertDialog.Builder(MainActivity.this);
				settingsBldr.setMessage("Load settings for board?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Board Settings")
					.setCancelable( true )
					.setPositiveButton("Load", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
						}
					})
					.setNeutralButton("Review", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
						}
					});
				AlertDialog alert = settingsBldr.create();
				alert.show();
			}
			
			if(MainActivity.pendingConnect)
			{
				if(bindType==0){
					BTBetaBot.connect();
					pendingConnect = false;
				}else{
					MainActivity.BetaBot.connect();
					MainActivity.pendingConnect = false;
				}
			}
		}
		
		public void onServiceDisconnected(ComponentName className)
		{
			if(debug)
				Log.d(TAG, "Service disconnected");
			MainActivity.BetaBot = null;
			
			if(BTBetaBot!=null)
				BTBetaBot.disconnect();
				BTBetaBot=null;
		}
	}
	
	//	Listener stuff
	
	private class MainTabListener implements ActionBar.TabListener
	{

		private MainTabListener(){}
		
		public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fat){	}
		
		public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fat)
		{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Fragment f;
			FragmentManager fm = getSupportFragmentManager();
			f = fm.findFragmentByTag((String) tab.getText());
			
			if (f == null)
			{
				if(tab.getText().equals("Settings"))
				{
					Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
					startActivity(intent);
					return;
				}else if(tab.getText().equals("Jog")){
					f = new JogFragment();
					if(bindType==0)
					{
						if(BTBetaBot != null)
							((JogFragment) f).updateState(BTBetaBot.getMachine());
					}else{
						if(BetaBot != null)
							((JogFragment) f).updateState(BetaBot.getMachine());
					}
					
				}else if(tab.getText().equals("Config")){
					f = new ConfigFragment();
				}else if(tab.getText().equals("File")){
					f = new FileFragment();
				}else if(tab.getText().equals("Monitor")){
					f = new CommandFragment();
				}else if(tab.getText().equals("Commands")){
					f = new ConnectionFragment();
				}else if(tab.getText().equals("Template")){
					f = new TemplateFragment();
				}else if(tab.getText().equals("MixBook")){
					f = new MixBookFragment();
				}else{
					f = new HomeFragment();
				}
				//ft.add(R.id.tabview, f, (String) tab.getText());
				ft.replace(R.id.tabview, f);
				if(!tab.getText().equals("Settings"))
					ft.addToBackStack(null);
				
				ft.commit();
			} else {
				if (f.isDetached())
				{	
					ft.replace(R.id.tabview, f);
					if(!tab.getText().equals("Settings"))
						ft.addToBackStack(null);
					
					ft.commit();
				}
				
			}
			
		}
		
		public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fat)
		{
			android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			FragmentManager fm = MainActivity.this.getSupportFragmentManager();
			Fragment f = (Fragment) fm.findFragmentByTag((String)tab.getText());
			if (f != null)
				ft.detach(f);
			ft.commit();
		}

	}	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(debug)
			Log.d(TAG, "onActivityResult " + resultCode + " : requestCode is " + requestCode);
		if(resultCode == Activity.RESULT_OK){
			
			switch(requestCode){
			case REQUEST_CONNECT_DEVICE:
				
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					
					connecting = true;
					progressBar = new ProgressDialog(MainActivity.this);
					progressBar.setCancelable(false);
					progressBar.setIndeterminate(true);
					progressBar.setMessage("Connecting ...");
					progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressBar.setProgress(0);
					progressBar.setMax(100);
					progressBar.show();
					
					new Thread(new Runnable() {
						public void run() {
							while(connecting){
								try{
									Thread.sleep(1000);
								}catch(InterruptedException e){
									e.printStackTrace();
								}
							}
							
							progressBar.dismiss();
							if(connected)
								systemHandler.postDelayed(SettingsRunner, 1000);
							//LoadSettings();
							
							
						}
					}).start();
					
					/*
					Toast toast = Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_LONG);
		            toast.setGravity(Gravity.CENTER, 0, 0);
		            toast.show();
		            */
					// Get the device MAC address
					String address = data.getExtras()
							.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// Get the BLuetoothDevice object
					BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
					// Attempt to connect to the device
					BTBetaBot.connect(device);                
				}
				break;

			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					if(debug)
						Log.d(TAG, "BT not enabled");
					//finishDialogNoBluetooth();                
				}
			case REQUEST_WTF:
				if(debug)
					Log.d(TAG, "REQUEST_WTF");
				if(resultCode == Activity.RESULT_OK && data != null)
				{
					FileFragment f = (FileFragment) getSupportFragmentManager().findFragmentById(R.id.tabview);
					f.onActivityResult(requestCode, resultCode, data);
				}
			
			case REQUEST_TEMPLATE:
				if(resultCode == Activity.RESULT_OK && data != null)
				{
					MixBookFragment f = (MixBookFragment) getSupportFragmentManager().findFragmentById(R.id.tabview);
					f.onActivityResult(requestCode, resultCode, data);
				}
			case REQUEST_MIXBOOK:
				if(resultCode == Activity.RESULT_OK && data != null)
				{
					MixBookFragment f = (MixBookFragment) getSupportFragmentManager().findFragmentById(R.id.tabview);
					f.onActivityResult(requestCode, resultCode, data);
				}
				
			case REQUEST_ADDRECIPE:
				if(resultCode == Activity.RESULT_OK && data != null)
				{
					MixBookFragment mf = (MixBookFragment) getSupportFragmentManager().findFragmentById(R.id.tabview);
					mf.onActivityResult(requestCode, resultCode, data);
					
				}
			case REQUEST_RECIPE:
				if(resultCode == Activity.RESULT_OK && data != null){
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					if(f.getClass().equals(MixBookFragment.class)){
						MixBookFragment mf = (MixBookFragment) getSupportFragmentManager().findFragmentById(R.id.tabview);
						mf.onActivityResult(requestCode, resultCode, data);
					}
				}
			case REQUEST_CONFIG:
				if(resultCode == Activity.RESULT_OK && data != null) {
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					if(f.getClass().equals(ConfigFragment.class)){
						f.onActivityResult(requestCode, resultCode, data);
					}
				}
			}
		}
	}
	
	//Preference Listener
	private class PrefsListener implements SharedPreferences.OnSharedPreferenceChangeListener
	{
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key)
		{
			Log.d(TAG,"debug value: "+settings.getBoolean("debug", false));
			if(key.equals("debug")){
				debug = settings.getBoolean("debug",false);
				Log.d(TAG,"new debug value: "+settings.getBoolean("debug",false));
			}
			
			if(debug)
				Log.d(TAG, "PrefsListener| key:"+key);
			//	connection and debug
			if(key.equals("debug"))
			{
				if(debug)
					Log.d(TAG, "Changing log debugging state");
				if(BTBetaBot != null)
					BTBetaBot.logging();
				
			}
			if(key.equals("connectionType"))
			{
				if(debug)
					Log.d(TAG,"Changing binding");
				MainActivity.bindType = Integer.parseInt(
						sharedPreferences.getString("connectionType","0"));
				if(MainActivity.BTBetaBot != null){
					try{
						MainActivity.this.unbindService(MainActivity.currentServiceConnection);
						MainActivity.BTBetaBot = null;
					}
					catch(IllegalArgumentException e)
					{
						if(debug)
							Log.w(TAG,"trying to unbind a non-bound service");
					}
				}
				currentServiceConnection = new DriverServiceConnection();
				bindDriver(currentServiceConnection);
			}
			// motor settings
			if(key.equals("1ma")||key.equals("2ma")||key.equals("3ma")||key.equals("4ma")) {
				if(debug){
					Log.d(TAG, key.toString());
					Log.d(TAG, key.toString().substring(0,1));
				}
				int mnum = 0;
				if(key.equals("1ma")){
					mnum = 1;
				}else if(key.equals("2ma")){
					mnum = 2;
				}else if(key.equals("3ma")){
					mnum = 3;
				}else if(key.equals("4ma")){
					mnum = 4;
				}
				helpAMSInty("ma",key,mnum,"",1,true);
				
			} else if(key.equals("1mi")||key.equals("2mi")||key.equals("3mi")||key.equals("4mi")) {
				int mnum = 0;
				if(key.equals("1mi")){
					mnum = 1;
				}else if(key.equals("2mi")){
					mnum = 2;
				}else if(key.equals("3mi")){
					mnum = 3;
				}else if(key.equals("4mi")){
					mnum = 4;
				}
				helpAMSFloaty("mi",key,mnum,"",1,true);
				
			} else if(key.equals("1tr")||key.equals("2tr")||key.equals("3tr")||key.equals("4tr")) {
				int mnum = 0;
				if(key.equals("1tr")){
					mnum = 1;
				}else if(key.equals("2tr")){
					mnum = 2;
				}else if(key.equals("3tr")){
					mnum = 3;
				}else if(key.equals("4tr")){
					mnum = 4;
				}
				helpAMSFloaty("tr",key,mnum,"",1,true);
				
			} else if(key.equals("1sa")||key.equals("2sa")||key.equals("3sa")||key.equals("4sa")) {
				int mnum = 0;
				if(key.equals("1sa")){
					mnum = 1;
				}else if(key.equals("2sa")){
					mnum = 2;
				}else if(key.equals("3sa")){
					mnum = 3;
				}else if(key.equals("4sa")){
					mnum = 4;
				}
				
				Bundle update = new Bundle();
				String str = sharedPreferences.getString(key, "");
				Float floaty = (float) 0.0;
				try{
					floaty = Float.valueOf(str);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				update.putFloat("sa", floaty);
				
				machine.updateMotorBundle(mnum, update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putMotor(mnum, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putMotor(mnum, update);
				}
				
			} else if(key.equals("1po")||key.equals("2po")||key.equals("3po")||key.equals("4po")) {
				int mnum = 0;
				if(key.equals("1po")){
					mnum = 1;
				}else if(key.equals("2po")){
					mnum = 2;
				}else if(key.equals("3po")){
					mnum = 3;
				}else if(key.equals("4po")){
					mnum = 4;
				}
				
				Bundle update = new Bundle();
				update.putBoolean("po", sharedPreferences.getBoolean(key, false));
				
				machine.updateMotorBundle(mnum, update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putMotor(mnum, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putMotor(mnum, update);
				}
				
			} else if(key.equals("1pm")||key.equals("2pm")||key.equals("3pm")||key.equals("4pm")) {
				int mnum = 0;
				if(key.equals("1pm")){
					mnum = 1;
				}else if(key.equals("2pm")){
					mnum = 2;
				}else if(key.equals("3pm")){
					mnum = 3;
				}else if(key.equals("4pm")){
					mnum = 4;
				}
				
				Bundle update = new Bundle();
				update.putBoolean("pm", sharedPreferences.getBoolean(key, true));
				
				machine.updateMotorBundle(mnum, update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putMotor(mnum, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putMotor(mnum, update);
				}
			}
			
			// axis settings
			 else if(key.equals("xam")||key.equals("yam")||key.equals("zam")||
					 key.equals("aam")||key.equals("bam")||key.equals("cam")) {
				int a = 0;
				if(key.equals("xam")){
					a = 0;
				}else if(key.equals("yam")){
					a = 1;
				}else if(key.equals("zam")){
					a = 2;
				}else if(key.equals("aam")){
					a = 3;
				}else if(key.equals("bam")){
					a = 4;
				}else if(key.equals("cam")){
					a = 5;
				}
				helpAMSInty("am",key,a,"",0,true);
				
			} else if(key.equals("xsn")||key.equals("ysn")||key.equals("zsn")||
					  key.equals("asn")||key.equals("bsn")||key.equals("csn")) {
				int a = 0;
				if(key.equals("xsn")){
					a = 0;
				}else if(key.equals("ysn")){
					a = 1;
				}else if(key.equals("zsn")){
					a = 2;
				}else if(key.equals("asn")){
					a = 3;
				}else if(key.equals("bsn")){
					a = 4;
				}else if(key.equals("csn")){
					a = 5;
				}
				helpAMSInty("sn",key,a,"",0,true);
				
			} else if(key.equals("xsx")||key.equals("ysx")||key.equals("zsx")||
					  key.equals("asx")||key.equals("bsx")||key.equals("csx")) {
				int a = 0;
				if(key.equals("xsx")){
					a = 0;
				}else if(key.equals("ysx")){
					a = 1;
				}else if(key.equals("zsx")){
					a = 2;
				}else if(key.equals("asx")){
					a = 3;
				}else if(key.equals("bsx")){
					a = 4;
				}else if(key.equals("csx")){
					a = 5;
				}
				helpAMSInty("sx",key,a,"",0,true);
				
			} else if(key.equals("xvm")||key.equals("yvm")||key.equals("zvm")||
					  key.equals("avm")||key.equals("bvm")||key.equals("cvm")) {
				int a = 0;
				if(key.equals("xvm")){
					a = 0;
				}else if(key.equals("yvm")){
					a = 1;
				}else if(key.equals("zvm")){
					a = 2;
				}else if(key.equals("avm")){
					a = 3;
				}else if(key.equals("bvm")){
					a = 4;
				}else if(key.equals("cvm")){
					a = 5;
				}
				helpAMSFloaty("vm",key,a,"",0,true);
				      
			} else if(key.equals("xra")||key.equals("yra")||key.equals("zra")||
					  key.equals("ara")||key.equals("bra")||key.equals("cra")) {
				int a = 0;
				if(key.equals("xra")){
					a = 0;
				}else if(key.equals("yra")){
					a = 1;
				}else if(key.equals("zra")){
					a = 2;
				}else if(key.equals("ara")){
					a = 3;
				}else if(key.equals("bra")){
					a = 4;
				}else if(key.equals("cra")){
					a = 5;
				}
				helpAMSFloaty("ra",key,a,"",0,true);
				      
			} else if(key.equals("xzb")||key.equals("yzb")||key.equals("zzb")||
					  key.equals("azb")||key.equals("bzb")||key.equals("czb")) {
				int a = 0;
				if(key.equals("xzb")){
					a = 0;
				}else if(key.equals("yzb")){
					a = 1;
				}else if(key.equals("zzb")){
					a = 2;
				}else if(key.equals("azb")){
					a = 3;
				}else if(key.equals("bzb")){
					a = 4;
				}else if(key.equals("czb")){
					a = 5;
				}
				helpAMSFloaty("zb",key,a,"",0,true);
				      
			} else if(key.equals("xtm")||key.equals("ytm")||key.equals("ztm")||
					  key.equals("atm")||key.equals("btm")||key.equals("ctm")) {
				int a = 0;
				if(key.equals("xtm")){
					a = 0;
				}else if(key.equals("ytm")){
					a = 1;
				}else if(key.equals("ztm")){
					a = 2;
				}else if(key.equals("atm")){
					a = 3;
				}else if(key.equals("btm")){
					a = 4;
				}else if(key.equals("ctm")){
					a = 5;
				}
				helpAMSFloaty("tm",key,a,"",0,true);
				
			} else if(key.equals("xjm")||key.equals("yjm")||key.equals("zjm")||
					  key.equals("ajm")||key.equals("bjm")||key.equals("cjm")) {
				int a = 0;
				if(key.equals("xjm")){
					a = 0;
				}else if(key.equals("yjm")){
					a = 1;
				}else if(key.equals("zjm")){
					a = 2;
				}else if(key.equals("ajm")){
					a = 3;
				}else if(key.equals("bjm")){
					a = 4;
				}else if(key.equals("cjm")){
					a = 5;
				}
				helpAMSFloaty("jm",key,a,"",0,true);
				
			} else if(key.equals("xjh")||key.equals("yjh")||key.equals("zjh")||
					  key.equals("ajh")||key.equals("bjh")||key.equals("cjh")) {
				int a = 0;
				if(key.equals("xjh")){
					a = 0;
				}else if(key.equals("yjh")){
					a = 1;
				}else if(key.equals("zjh")){
					a = 2;
				}else if(key.equals("ajh")){
					a = 3;
				}else if(key.equals("bjh")){
					a = 4;
				}else if(key.equals("cjh")){
					a = 5;
				}
				
				Bundle update = new Bundle();
				String str = sharedPreferences.getString(key,"");
				Float floaty = (float) 0.0;
				try{
					floaty = Float.parseFloat(str);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				update.putFloat("jh", floaty);
				
				/*
				machine.updateAxisBundle(a, update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putAxis(a, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putAxis(a, update);
				}
				*/
				if(debug)
					Log.d(TAG, "BTBetaBot-putAxis(" + a + "jh)?");
				
			} else if(key.equals("xjd")||key.equals("yjd")||key.equals("zjd")||
					  key.equals("ajd")||key.equals("bjd")||key.equals("cjd")) {
				int a = 0;
				if(key.equals("xjd")){
					a = 0;
				}else if(key.equals("yjd")){
					a = 1;
				}else if(key.equals("zjd")){
					a = 2;
				}else if(key.equals("ajd")){
					a = 3;
				}else if(key.equals("bjd")){
					a = 4;
				}else if(key.equals("cjd")){
					a = 5;
				}
				helpAMSFloaty("jd",key,a,"",0,true);
				
			} else if(key.equals("xlv")||key.equals("ylv")||key.equals("zlv")||
					  key.equals("alv")||key.equals("blv")||key.equals("clv")) {
				int a = 0;
				if(key.equals("xlv")){
					a = 0;
				}else if(key.equals("ylv")){
					a = 1;
				}else if(key.equals("zlv")){
					a = 2;
				}else if(key.equals("alv")){
					a = 3;
				}else if(key.equals("blv")){
					a = 4;
				}else if(key.equals("clv")){
					a = 5;
				}
				helpAMSFloaty("lv",key,a,"",0,true);
				
			} else if(key.equals("xsv")||key.equals("ysv")||key.equals("zsv")||
					  key.equals("asv")||key.equals("bsv")||key.equals("csv")) {
				int a = 0;
				if(key.equals("xsv")){
					a = 0;
				}else if(key.equals("ysv")){
					a = 1;
				}else if(key.equals("zsv")){
					a = 2;
				}else if(key.equals("asv")){
					a = 3;
				}else if(key.equals("bsv")){
					a = 4;
				}else if(key.equals("csv")){
					a = 5;
				}
				helpAMSFloaty("sv",key,a,"",0,true);
				
			} else if(key.equals("xfr")||key.equals("yfr")||key.equals("zfr")||
					  key.equals("afr")||key.equals("bfr")||key.equals("cfr")) {
				int a = 0;
				if(key.equals("xfr")){
					a = 0;
				}else if(key.equals("yfr")){
					a = 1;
				}else if(key.equals("zfr")){
					a = 2;
				}else if(key.equals("afr")){
					a = 3;
				}else if(key.equals("bfr")){
					a = 4;
				}else if(key.equals("cfr")){
					a = 5;
				}
				helpAMSFloaty("fr",key,a,"",0,true);
			} 
			 // system
			 else if(key.equals("hv")) {
				helpAMSInty("hv",key,0,"",2,true);
			} else if(key.equals("ja")) {
				helpAMSFloaty("ja",key,0,"",2,true);
			} else if(key.equals("ct")) {
				helpAMSFloaty("ct",key,0,"",2,true);
			} else if(key.equals("st")) {
				helpAMSInty("st",key,0,"",2,true);
			} else if(key.equals("ej")) {
				Bundle update = new Bundle();
				update.putBoolean("ej", settings.getBoolean(key, true));
				machine.updateSystemBundle(update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			} else if(key.equals("jv")) {
				helpAMSInty("jv",key,0,"",2,true);
			} else if(key.equals("tv")) {
				helpAMSInty("tv",key,0,"",2,true);
			} else if(key.equals("qv")) {
				helpAMSInty("qv",key,0,"",2,true);
			} else if(key.equals("sv")) {
				helpAMSInty("sv",key,0,"",2,true);
			} else if(key.equals("si")) {
				helpAMSInty("si",key,0,"",2,true);
			} else if(key.equals("ic")) {
				helpAMSInty("ic",key,0,"",2,true);
			} else if(key.equals("ec")) {
				Bundle update = new Bundle();
				update.putBoolean("ec", settings.getBoolean(key, true));
				machine.updateSystemBundle(update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			} else if(key.equals("ee")) {
				Bundle update = new Bundle();
				update.putBoolean("ee", settings.getBoolean(key, true));
				machine.updateSystemBundle(update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			} else if(key.equals("ex")) {
				helpAMSInty("ex",key,0,"",2,true);
			} else if(key.equals("gpl")) {
				helpAMSInty("gpl",key,0,"",2,true);
			} else if(key.equals("gun")) {
				Bundle update = new Bundle();
				update.putBoolean("gun", settings.getBoolean(key, true));
				machine.updateSystemBundle(update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			} else if(key.equals("gco")) {
				helpAMSInty("gco",key,0,"",2,true);
			} else if(key.equals("gpa")) {
				helpAMSInty("gpa",key,0,"",2,true);
			} else if(key.equals("gdi")) {
				helpAMSInty("gdi",key,0,"",2,true);
			} else if(key.equals("nwa")) {
				Bundle update = new Bundle();
				update.putString("nwa", settings.getString(key, "XYZA"));
				machine.updateSystemBundle(update);
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			} else if(key.equals("nwae")) {
				helpAMSFloaty("nwae",key,0,"18",2,true);
			} else if(key.equals("nwadt")) {
				helpAMSFloaty("nwadt",key,0,"22",2,true);
			}
			
			//HOMING
			  else if(key.equals("nwxh")) {
				xHome = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwyh")) {
				yHome = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwzh")) {
				zHome = readFloat(settings.getString(key, "4.0"),4.0f);
			} else if(key.equals("nwah")) {
				aHome = readFloat(settings.getString(key, "0.1"),0.1f);
				Log.d(TAG,"aHome: "+String.valueOf(aHome));
			} 
			
			  else if(key.equals("nwxsh")) {
				xSendHome = readFloat(settings.getString(key, "-1500.0"),-1500.0f);
			} else if(key.equals("nwysh")) {
				ySendHome = readFloat(settings.getString(key, "-250.0"),-250.0f);
			} else if(key.equals("nwzsh")) {
				zSendHome = readFloat(settings.getString(key, "-200.0"),-200.0f);
			} else if(key.equals("nwash")) {
				aSendHome = readFloat(settings.getString(key, "-40.0"),-40.0f);
				Log.d(TAG,"aSendHome: "+String.valueOf(aSendHome));
			} 
			
			  else if(key.equals("nwxta")) {
				xTapA = readFloat(settings.getString(key, "-1.0"),-1.0f);
			} else if(key.equals("nwyta")) {
				yTapA = readFloat(settings.getString(key, "-1.0"),-1.0f);
			} else if(key.equals("nwzta")) {
				zTapA = readFloat(settings.getString(key, "-0.1"),-0.1f);
			} else if(key.equals("nwata")) {
				aTapA = readFloat(settings.getString(key, "-0.1"),-0.1f);
				Log.d(TAG,"aSendHome: "+String.valueOf(aTapA));
			} 
			
			  else if(key.equals("nwxtb")) {
				xTapB = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwytb")) {
				yTapB = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwztb")) {
				zTapB = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwatb")) {
				aTapB = readFloat(settings.getString(key, "1.0"),1.0f);
				Log.d(TAG,"aSendHome: "+String.valueOf(aTapB));
			}
			
			  else if(key.equals("nwxn")) {
				xNudge = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwyn")) {
				yNudge = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwzn")) {
				zNudge = readFloat(settings.getString(key, "10.0"),10.0f);
			} else if(key.equals("nwan")) {
				aNudge = readFloat(settings.getString(key, "0.1"),0.1f);
				Log.d(TAG,"aNudge: "+String.valueOf(aNudge));
			}
			
			// speeds
			  else if(key.equals("nwxhf")) {
				xHomeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwyhf")) {
				yHomeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwzhf")) {
				zHomeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwahf")) {
				aHomeF = readFloat(settings.getString(key, "3.0"),3.0f);
			} 
			
			  else if(key.equals("nwxshf")) {
				xSendHomeF = readFloat(settings.getString(key, "50.0"),10.0f);
			} else if(key.equals("nwyshf")) {
				ySendHomeF = readFloat(settings.getString(key, "50.0"),10.0f);
			} else if(key.equals("nwzshf")) {
				zSendHomeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwashf")) {
				aSendHomeF = readFloat(settings.getString(key, "3.0"),3.0f);	
			} 
			
			  else if(key.equals("nwxtaf")) {
				xTapAF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwytaf")) {
				yTapAF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwztaf")) {
				zTapAF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwataf")) {
				aTapAF = readFloat(settings.getString(key, "3.0"),3.0f);
			} 
			
			  else if(key.equals("nwxtbf")) {
				xTapBF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwytbf")) {
				yTapBF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwztbf")) {
				zTapBF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwatbf")) {
				aTapBF = readFloat(settings.getString(key, "3.0"),3.0f);
			}
			
			  else if(key.equals("nwxnf")) {
				xNudgeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwynf")) {
				yNudgeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwznf")) {
				zNudgeF = readFloat(settings.getString(key, "50.0"),50.0f);
			} else if(key.equals("nwanf")) {
				aNudgeF = readFloat(settings.getString(key, "3.0"),3.0f);
			}
		}
	}
	
	private float readFloat(String float_string, float default_value) {
		float result = 0.0f;
		try{
			result = Float.parseFloat(float_string);
		}catch(NumberFormatException e){
			e.printStackTrace();
			result = default_value;
		}
		if(debug)
			Log.d(TAG,"readFloat -> "+String.valueOf(result));
		return result;
	}
	
	private void helpAMSFloaty(String where, String _key, int _a, String dfault, int am, boolean _force){
		if(debug)
			Log.d(TAG, "where: "+where+", key: "+_key+", a: "+_a+", default: "+dfault+", am: "+am+", force? "+String.valueOf(_force));
		Bundle update = new Bundle();
		String str = settings.getString(_key, dfault);
		Float floaty = (float) 0.0;
		try{
			floaty = Float.parseFloat(str);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		update.putFloat(where, floaty);
		switch(am){
		case 0:
			machine.updateAxisBundle(_a, update);
			if(true) { //_force
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putAxis(_a, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putAxis(_a, update);
				}
			}
			break;
		case 1:
			machine.updateMotorBundle(_a, update);
			if(true) { //_force
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putMotor(_a, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putMotor(_a, update);
				}
			}
			break;
		case 2:
			machine.updateSystemBundle(update);
			if(true) { //_force
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			}
			break;
		}
	}
	
	private void helpAMSInty(String where, String _key, int _a, String dfault, int am, boolean _force){
		if(debug)
			Log.d(TAG, "where: "+where+", key: "+_key+", a: "+_a+", default: "+dfault+", am: "+am+", force? "+String.valueOf(_force));
		Bundle update = new Bundle();
		String str = settings.getString(_key, dfault);
		Integer inty = 0;
		try{
			inty = Integer.parseInt(str);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		update.putInt(where, inty);
		switch(am){
		case 0:
			machine.updateAxisBundle(_a, update);
			if(true){ //_force
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putAxis(_a, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putAxis(_a, update);
				}
			}
			break;
		case 1:
			machine.updateMotorBundle(_a, update);
			if(true){ //_force
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putMotor(_a, update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putMotor(_a, update);
				}
			}
			break;
		case 2:
			machine.updateSystemBundle(update);
			if(true){ //_force
				if(bindType==0){
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BTBetaBot.putSystem(update);
				}else{
					if(MainActivity.BTBetaBot == null)
						return;
					else
						MainActivity.BetaBot.putSystem(update);
				}
			}
			break;
		}
	
	}
	
	public class BetaBotServiceReceiver extends BroadcastReceiver
	{
		@SuppressLint({"NewApi"})
		public void onReceive(Context context, Intent intent)
		{
			Bundle b = intent.getExtras();
			String action;
			action = intent.getAction();
			if(action.equals(BetaBotService.JSON_ERROR)){
				if(loading_settings){
					if(9<tries++){
						if(settingsIndex<configurations.size()-1)
							tryToLoadSetting(configurations.get(++settingsIndex));
						else
							loading_settings = trying = false;
						tries = collegeTries = 0;
					} else {// if(4<tries) {
						tryToLoadSetting(configurations.get(settingsIndex));
					}
				}
				
			}
			
			
			//THIS IS ONLY FOR DEBUGGING
			if(false){//debug
				if(trying){
					Iterator<?> ito = b.keySet().iterator();
					int i = 0;
					peck++;
					while(ito.hasNext()){
						String entry = (String) ito.next();
						generalSB.setLength(0);
						generalSB.append(b.get(entry));
						Log.d(TAG,"b("+peck+") key["+i+"]: "+entry+" value["+i+"]: "+generalSB.toString());
						i++;
					}
					if(configurations!=null){
						Log.d(TAG,"configurations.size(): "+configurations.size());
						Log.d(TAG,"settingsIndex "+settingsIndex);
						Log.d(TAG,"collegeTries: "+collegeTries);
					}
				}
			}
			
				
			
			if(trying){
				if(b.containsKey(configurations.get(settingsIndex))) {
					if(settingsIndex>=configurations.size()-1){
						loading_settings = trying = false;
						sendReset();
						sendReset();
					}else{
						if(connected){
							if(bindType==0){
								BTBetaBot.cleanse();
							}else{
								BetaBot.cleanse();
							}
						}
						//matchSettings(String key, String accion, Bundle b)
						if(action.equals(BetaBotService.STATUS)){
							if(matchSettings(configurations.get(settingsIndex),BetaBotService.STATUS, b))
							{	
								tries = collegeTries = 0;
								tryToLoadSetting(configurations.get(++settingsIndex));
							}
							else {
								if(1<tries++){
									tries = collegeTries = 0;
									//sendCommand("%\n");
									TryRunner tr = new TryRunner(configurations.get(++settingsIndex));
									systemHandler.post(tr);
								}else
									tryToLoadSetting(configurations.get(settingsIndex));
							}
						}
						else if(action.equals(BetaBotService.AXIS_UPDATE)){
							if(matchSettings(configurations.get(settingsIndex),BetaBotService.AXIS_UPDATE, b))
							{
								tries = collegeTries = 0;
								tryToLoadSetting(configurations.get(++settingsIndex));
							}
							else {
								if(1<tries++){
									tries = collegeTries = 0;
									//sendCommand("%\n");
									TryRunner tr = new TryRunner(configurations.get(++settingsIndex));
									systemHandler.post(tr);
								}else
									tryToLoadSetting(configurations.get(settingsIndex));
							}
						}
						else if(action.equals(BetaBotService.MOTOR_UPDATE)){
							if(matchSettings(configurations.get(settingsIndex),BetaBotService.MOTOR_UPDATE, b))
							{
								tries = collegeTries= 0;
								tryToLoadSetting(configurations.get(++settingsIndex));
							}
							else {
								if(1<tries++){
									tries = collegeTries = 0;
									//sendCommand("%\n");
									TryRunner tr = new TryRunner(configurations.get(++settingsIndex));
									systemHandler.post(tr);
								}else
									tryToLoadSetting(configurations.get(settingsIndex));
							}
						}
						
						
					}
				}
				else {
					if(collegeTries++>50){
						collegeTries=0;
						if(settingsIndex<configurations.size()-1) {
							sendReset();
							TryRunner tr = new TryRunner(configurations.get(++settingsIndex));
							systemHandler.postDelayed(tr, 1000);
							//tryToLoadSetting(configurations.get(++settingsIndex));
						}else
							loading_settings = trying = false;
					} else {
						//tryToLoadSetting(configurations.get(settingsIndex));
					}
					
				}
			}
			
			if(b.containsKey("posx"))
				posX = b.getFloat("posx");
			if(b.containsKey("posy"))
				posY = b.getFloat("posy");
			if(b.containsKey("posz"))
				posZ = b.getFloat("posz");
			
			//STATUS
			if(action.equals(BetaBotService.STATUS))
			{
				//STATUS FRAGMENT
				if(bindType==0)
				{
					StatusFragment sf = (StatusFragment)MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.statusF);
					sf.updateState(b);
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					
					if((f!=null)&&(f.getClass()==FileFragment.class)&&
							(BTBetaBot!=null))
					{
						((FileFragment) f).nextLine(b.getInt("line"));
					}
				}else{
					StatusFragment sf = (StatusFragment)MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.statusF);
					sf.updateState(b);
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					
					if((f!=null)&&(f.getClass()==FileFragment.class)&&
							(BetaBot!=null))
					{
						((FileFragment) f).nextLine(b.getInt("line"));
					}
				}//STATUS FRAGMENT
				
				
				//HOMING
				if(homingFlag==1){
					if(b.containsKey("posz")){
						Float posz = b.getFloat("posz");
						if((posz>zHome-0.01f)&&posz<(zHome+0.01f)){
							if(!zGateA){
								zGateA = true;
								zGateB = false;
								sendHome("z");
							}
							
						}
					}
				}else if(homingFlag==2){
					if(b.containsKey("posz")){
						Float posz = b.getFloat("posz");
						if(posz<=(zSendHome+(float)10)){
							if(!zGateB){
								zGateB = true;
								imHome("z");
							}
						}
					}
				}else if(homingFlag==4){
					if(b.containsKey("posz")){
						Float posz = b.getFloat("posz");
						if((posz>zNudge-0.01f)&&posz<zNudge+0.01f){
							if(!zGateB){
								zGateB = true;
								imHome("z");
							}
						}
					}
				}else if(homingFlag==6){
					if(b.containsKey("posy")){
						Float posy = b.getFloat("posy");
						if((posy>yHome-0.01f)&&posy<(yHome+0.01f)){
							if(!yGateA){
								yGateA = true;
								yGateB = false;
								sendHome("y");
							}
						}
					}
				}else if(homingFlag==7){
					if(b.containsKey("posy")){
						Float posy = b.getFloat("posy");
						if(posy<=(ySendHome+(float)10)){
							if(!yGateB){
								yGateB = true;
								imHome("y");
							}
						}
					}
				}else if(homingFlag==9){
					if(b.containsKey("posy")){
						Float posy = b.getFloat("posy");
						if((posy>yNudge-0.01f)&&posy<yNudge+0.01f){
							if(!yGateB){
								yGateB = true;
								imHome("y");
							}
						}
					}
				}else if(homingFlag==11){
					if(b.containsKey("posx")){
						Float posx = b.getFloat("posx");
						if((posx>(xHome-0.01f))&&posx<(xHome+0.01f)){
							if(!xGateA){
								xGateA = true;
								xGateB = false;
								sendHome("x");
							}
						}
					}
				}else if(homingFlag==12){
					if(b.containsKey("posx")){
						Float posx = b.getFloat("posx");
						if(posx<=xSendHome+(float)10){
							if(!xGateB){
								xGateB = true;
								imHome("x");
							}
						}
					}
				}else if(homingFlag==14){
					if(b.containsKey("posx")){
						Float posx = b.getFloat("posx");
						if((posx>xNudge-0.01f)&&posx<xNudge+0.01f){
							if(!xGateB){
								xGateB = true;
								imHome("x");
							}
						}
					}
				}else if(homingFlag==16){
					if(b.containsKey("posa")){
						Float posa = b.getFloat("posa");
						Float rosa = Float.valueOf(settings.getString("4ra", "0.3183"));
						Float pasta = posa*((float)(Math.PI*2.0)*rosa/(float)360);
						if(debug)
							Log.d(TAG, "PASTA: "+String.valueOf(pasta));
						
						if((pasta>aHome-0.04f)&&(pasta<aHome+0.04f)){
							if(debug)
								Log.d(TAG,"homingFlag 16 -> sendHomeA");
							if(!aGateA){
								aGateA = true;
								aGateB = false;
								sendHome("a");	
							}	
						}
					}
				}else if(homingFlag==17){
					if(b.containsKey("posa")){
						Float posa = b.getFloat("posa");
						Float rosa = Float.valueOf(settings.getString("4ra", "0.3183"));
						Float pasta = posa*((float)(Math.PI*2.0)*rosa/(float)360);
						if(pasta<=aSendHome+(float)5){
							if(!aGateB){
								aGateB = true;
								if(debug)
									Log.d(TAG,"homingFlag 16 -> imHome(a)");
								imHome("a");
							}
						}
					}
				}else if(homingFlag==19){
					if(b.containsKey("posa")){
						Float posa = b.getFloat("posa");
						Float rosa = Float.valueOf(settings.getString("4ra", "0.3183"));
						Float pasta = posa*((float)(Math.PI*2.0)*rosa/(float)360);
						if((pasta>aNudge-0.04f)&&pasta<aNudge+0.04f){
							if(!aGateB){
								aGateB = true;
								imHome("a");
							}
						}
					}
				}//HOMING
				
				//SENSOR
				if(sensing){
					adjust = settings.getBoolean("adjust", false);
					if(adjust){
						coeffy = settings.getFloat("coeff", 1.0f);
						consty = settings.getFloat("const", 0.0f);
						try{
							temperature = b.getString("temp");
							tempting = Float.parseFloat(temperature)*coeffy+consty;
						}catch(Exception e){
							
						}
					}
					if(b.containsKey("temp")){
						Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
						if(f != null && f.getClass() == JogFragment.class)
						{
							((JogFragment) f).setReading(b.getString("temp")+" C");
							if(adjust)
								((JogFragment) f).setAdjusted(String.format("%.2f",tempting));
							else
								((JogFragment) f).setAdjusted(" ----- ");
								
						}
					} else {
						Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
						if(f != null && f.getClass() == JogFragment.class)
						{
							((JogFragment) f).errorReading();
						}
					}
				}//SENSOR
				
				
			}//STATUS
			
			if(action.equals(BetaBotService.AXIS_UPDATE)){
				
				if(bindType==0){
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					if(f != null && f.getClass() == JogFragment.class &&
							BTBetaBot != null)
					{
						((JogFragment) f).updateState(BTBetaBot.getMachine());
					}
				}else{
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					if(f != null && f.getClass() == JogFragment.class &&
							BetaBot != null)
					{
						((JogFragment) f).updateState(BetaBot.getMachine());
					}
				}
				if(startup_sequence) {
					//sendCommand("{\"x\":{\"am\":1,\"vm\":10240,\"fr\":10240,\"tm\":150,\"jm\":200000000}}\n");
					if(axiFlagsX.am||axiFlagsX.vm||axiFlagsX.fr||axiFlagsX.tm||axiFlagsX.jm){
						if(b.containsKey("am"))
							axiFlagsX.am=false;
						
						if(b.containsKey("vm"))
							axiFlagsX.vm=false;
						
						if(b.containsKey("fr"))
							axiFlagsX.fr=false;
						
						if(b.containsKey("tm"))
							axiFlagsX.tm=false;
						
						if(b.containsKey("jm"))
							axiFlagsX.jm=false;
						
						if(!axiFlagsX.am&&!axiFlagsX.vm&&!axiFlagsX.fr&&!axiFlagsX.tm&&!axiFlagsX.jm){
							sendCommand("{\"y\":{\"am\":1,\"vm\":10240,\"fr\":10240,\"tm\":150,\"jm\":100000000}}\n");
							//200000000
						}
					}else if(axiFlagsY.am||axiFlagsY.vm||axiFlagsY.fr||axiFlagsY.tm||axiFlagsY.jm){
						if(b.containsKey("am"))
							axiFlagsY.am=false;
						
						if(b.containsKey("vm"))
							axiFlagsY.vm=false;
						
						if(b.containsKey("fr"))
							axiFlagsY.fr=false;
						
						if(b.containsKey("tm"))
							axiFlagsY.tm=false;
						
						if(b.containsKey("jm"))
							axiFlagsY.jm=false;
						
						if(!axiFlagsY.am&&!axiFlagsY.vm&&!axiFlagsY.fr&&!axiFlagsY.tm&&!axiFlagsY.jm){
							sendCommand("{\"z\":{\"am\":1,\"vm\":10240,\"fr\":10240,\"tm\":150,\"jm\":75000000}}\n");
						}
					}else if(axiFlagsZ.am||axiFlagsZ.vm||axiFlagsZ.fr||axiFlagsZ.tm||axiFlagsZ.jm){
						if(b.containsKey("am"))
							axiFlagsZ.am=false;
						
						if(b.containsKey("vm"))
							axiFlagsZ.vm=false;
						
						if(b.containsKey("fr"))
							axiFlagsZ.fr=false;
						
						if(b.containsKey("tm"))
							axiFlagsZ.tm=false;
						
						if(b.containsKey("jm"))
							axiFlagsZ.jm=false;
						
						if(!axiFlagsZ.am&&!axiFlagsZ.vm&&!axiFlagsZ.fr&&!axiFlagsZ.tm&&!axiFlagsZ.jm){
							sendCommand("{\"a\":{\"am\":3,\"vm\":16240,\"fr\":36000,\"tm\":150,\"jm\":200000000,\"ra\":0.3183099}}\n");
						}
					}else if(axiFlagsA.am||axiFlagsA.vm||axiFlagsA.fr||axiFlagsA.tm||axiFlagsA.jm){
						if(b.containsKey("am"))
							axiFlagsA.am=false;
						
						if(b.containsKey("vm"))
							axiFlagsA.vm=false;
						
						if(b.containsKey("fr"))
							axiFlagsA.fr=false;
						
						if(b.containsKey("tm"))
							axiFlagsA.tm=false;
						
						if(b.containsKey("jm"))
							axiFlagsA.jm=false;
						
						if(!axiFlagsA.am&&!axiFlagsA.vm&&!axiFlagsA.fr&&!axiFlagsA.tm&&!axiFlagsA.jm){
							sendGcode("(msgAXIS FINISH)");
							axis_sequence = false;
							//RunStartup(4);
							
							/*
							 *
							 * 
							 * 
							 * 
							 * 
							 */
							
							
						}
					}
				}
			}
			if(action.equals(BetaBotService.MOTOR_UPDATE)){
				if(startup_sequence){
					//sendCommand("{\"1\":{\"ma\":0,\"sa\":1.8,\"tr\":36.50,\"mi\":8,\"po\":0,\"pm\":1}}\n");
					if(motFlags1.ma||motFlags1.sa||motFlags1.tr||motFlags1.mi||motFlags1.po||motFlags1.pm){
						if(b.containsKey("ma"))
							motFlags1.ma=false;
					
						if(b.containsKey("sa"))
							motFlags1.sa=false;
					
						if(b.containsKey("tr"))
							motFlags1.tr=false;
					
						if(b.containsKey("mi"))
							motFlags1.mi=false;
					
						if(b.containsKey("po"))
							motFlags1.po=false;
					
						if(b.containsKey("pm"))
							motFlags1.pm=false;
					
						if(!motFlags1.ma&&!motFlags1.sa&&!motFlags1.tr&&!motFlags1.mi&&!motFlags1.po&&!motFlags1.pm)
							sendCommand("{\"2\":{\"ma\":1,\"sa\":1.8,\"tr\":36.54,\"mi\":8,\"po\":1,\"pm\":0}}\n");
					}else if(motFlags2.ma||motFlags2.sa||motFlags2.tr||motFlags2.mi||motFlags2.po||motFlags2.pm){
						if(b.containsKey("ma"))
							motFlags2.ma=false;
					
						if(b.containsKey("sa"))
							motFlags2.sa=false;
					
						if(b.containsKey("tr"))
							motFlags2.tr=false;
					
						if(b.containsKey("mi"))
							motFlags2.mi=false;
					
						if(b.containsKey("po"))
							motFlags2.po=false;
					
						if(b.containsKey("pm"))
							motFlags2.pm=false;
					
						if(!motFlags2.ma&&!motFlags2.sa&&!motFlags2.tr&&!motFlags2.mi&&!motFlags2.po&&!motFlags2.pm)
							sendCommand("{\"3\":{\"ma\":2,\"sa\":1.8,\"tr\":8.1,\"mi\":8,\"po\":1,\"pm\":0}}\n");
					}else if(motFlags3.ma||motFlags3.sa||motFlags3.tr||motFlags3.mi||motFlags3.po||motFlags3.pm){
						if(b.containsKey("ma"))
							motFlags3.ma=false;
					
						if(b.containsKey("sa"))
							motFlags3.sa=false;
					
						if(b.containsKey("tr"))
							motFlags3.tr=false;
					
						if(b.containsKey("mi"))
							motFlags3.mi=false;
					
						if(b.containsKey("po"))
							motFlags3.po=false;
					
						if(b.containsKey("pm"))
							motFlags3.pm=false;
					
						if(!motFlags3.ma&&!motFlags3.sa&&!motFlags3.tr&&!motFlags3.mi&&!motFlags3.po&&!motFlags3.pm)
							sendCommand("{\"4\":{\"ma\":3,\"sa\":1.8,\"tr\":360,\"mi\":8,\"po\":1,\"pm\":1}}\n");
					}else if(motFlags4.ma||motFlags4.sa||motFlags4.tr||motFlags4.mi||motFlags4.po||motFlags4.pm){
						if(b.containsKey("ma"))
							motFlags4.ma=false;
					
						if(b.containsKey("sa"))
							motFlags4.sa=false;
					
						if(b.containsKey("tr"))
							motFlags4.tr=false;
					
						if(b.containsKey("mi"))
							motFlags4.mi=false;
					
						if(b.containsKey("po"))
							motFlags4.po=false;
					
						if(b.containsKey("pm"))
							motFlags4.pm=false;
					
						if(!motFlags4.ma&&!motFlags4.sa&&!motFlags4.tr&&!motFlags4.mi&&!motFlags4.po&&!motFlags4.pm){
							//sendCommand("{\"x\":{\"am\":1,\"vm\":10240,\"fr\":10240,\"tm\":150,\"jm\":200000000}}\n");
							sendGcode("(msgMOTOR FINISH)");
							motor_sequence = false;
						}
					}
					
				}
			}
			if(action.equals(BetaBotService.CONNECTION_STATUS))
			{
				MainActivity.connected = b.getBoolean("connection");
				if(!MainActivity.connected){
					MainActivity.pendingConnect = false;
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					if(f!=null && f.getClass()==CommandFragment.class){
						((CommandFragment) f).disableButton();
					}
				}else{
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
					if(f!=null && f.getClass()==CommandFragment.class){
						((CommandFragment) f).enableButton();
					}
				}
				
				MainActivity.this.invalidateOptionsMenu();
			}
			if(action.equals(BetaBotService.RAWS))
			{
				if(debug)
					Log.d(TAG, "RAW log: " + b.getString("rawness"));
				if(mText.length()>TEXT_MAX_SIZE){
					mText.delete(0, TEXT_MAX_SIZE/2);
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append(b.getString("rawness"));
				String sta = sb.toString();
				if(!sta.endsWith("\n")&&!sta.endsWith("\r"))
					sb.append(BR);
				mText.append(sb);
				Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
				if(f!=null && f.getClass()==CommandFragment.class)
				{
					((CommandFragment) f).doSomething(sb.toString());
				}else{
					SharedPreferences.Editor Ed = settings.edit();
					Ed.putString("rawness", mText.toString());
					Ed.commit();
				}
				
				//	this is not used, might be cut
				if(show_opening_settings_dialog){
					if(!b.getString("rawness").contains("tinyg [mm] ok>")) {
						dialogSB.append(b.getString("rawness")).append("\n");
					} else {
						show_opening_settings_dialog = false;
						tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
						LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					    View mySettingsView = inflater.inflate(R.layout.opening_settings, null, false);

					    // textViewWithScroll is the name of our TextView on scroll_text.xml
					    TextView tv = (TextView) mySettingsView
					            .findViewById(R.id.textOpeningSettings);
					    tv.setTypeface(tf);
					    // Initializing a blank textview so that we can just append a text later
					    dialogSB.insert(0,"\n");
					    tv.setText(dialogSB.toString());
						AlertDialog.Builder setBldr = new AlertDialog.Builder(MainActivity.this);
						setBldr.setView(mySettingsView)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("SETTINGS")
						.setCancelable( true )
						.setPositiveButton("GREAT!", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int id) {
								
							}
						})
						.setNegativeButton("SETTINGS", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int id) {
								
							}
						});
						AlertDialog alert = setBldr.create();
						alert.show();	
						dialogSB.setLength(0);
					}	
				}
				String rawStr = b.getString("rawness");
				
				if(rawStr.contains("ZMIN")){
					if(homingFlag==1){
						zGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("z");
						systemHandler.postDelayed(dsh,0);
					}else if(homingFlag==2){
						temporaryZ = -posZ-zNudge;
						zGateB = false;
						sendCommand("%\n");
						tap("z");
					}else if(homingFlag==3){
						zGateB = false;
						sendCommand("%\n");
						nudge("z");
					}
				}
				
				if(rawStr.contains("YMIN")){
					if(homingFlag==6){
						yGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("y");
						systemHandler.postDelayed(dsh,0);
					}else if(homingFlag==7){
						temporaryY = -posY-yNudge;
						yGateB = false;
						sendCommand("%\n");
						tap("y");
					}else if(homingFlag==8){
						yGateB = false;
						sendCommand("%\n");
						nudge("y");
					}
				}
				
				if(rawStr.contains("XMIN")){
					if(homingFlag==11){
						xGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("x");
						systemHandler.postDelayed(dsh,0);
					}else if(homingFlag==12){
						temporaryX = -posX-xNudge;
						xGateB = false;
						sendCommand("%\n");
						tap("x");
					}else if(homingFlag==13){
						xGateB = false;
						sendCommand("%\n");
						nudge("x");
					}
				}
				
				if(rawStr.contains("AMIN")){
					if(debug)
						Log.d(TAG,"homingFlag: "+String.valueOf(homingFlag));
					if(homingFlag==16){
						aGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("a");
						systemHandler.postDelayed(dsh,500);
						sendHome("a");
					}else if(homingFlag==17){
						aGateB = false;
						sendCommand("%\n");
						tap("a");
					}else if(homingFlag==18){
						aGateB = false;
						sendCommand("%\n");
						nudge("a");
					}
				}
				
				rawStr = "";
				
			}
			if(action.equals(BetaBotService.SYSTEM)){
				if(startup_sequence){
					if(sysFlags.si){
						if(b.containsKey("si")){
							sysFlags.si=false;
							sendCommand("{\"st\":1}\n");
						}
					}else if(sysFlags.st){
						if(b.containsKey("st")){
							sysFlags.st=false;
							sendCommand("{\"ej\":1}\n");
						}
					}else if(sysFlags.ej){
						if(b.containsKey("ej")){
							sysFlags.ej=false;
							sendCommand("{\"jv\":5}\n");
						}
					}else if(sysFlags.jv){
						if(b.containsKey("jv")){
							sysFlags.jv=false;
							sendCommand("{\"tv\":1}\n");
						}
					}else if(sysFlags.tv){
						if(b.containsKey("tv")){
							sysFlags.tv=false;
							sendCommand("{\"qv\":2}\n");
						}
					}else if(sysFlags.qv){
						if(b.containsKey("qv")){
							sysFlags.qv=false;
							sendCommand("{\"sv\":1}\n");
						}
					}else if(sysFlags.sv){
						if(b.containsKey("sv")){
							sysFlags.sv=false;
							sendCommand("{\"gpl\":0}\n");
						}
					}else if(sysFlags.gpl){
						if(b.containsKey("gpl")){
							sysFlags.gpl=false;
							sendCommand("{\"gun\":1}\n");
						}
					}else if(sysFlags.gun){
						if(b.containsKey("gun")){
							sysFlags.gun=false;
							sendCommand("{\"gco\":1}\n");
						}
					}else if(sysFlags.gco){
						if(b.containsKey("gco")){
							sysFlags.gco=false;
							sendCommand("{\"gpa\":2}\n");
						}
					}else if(sysFlags.gpa){
						if(b.containsKey("gpa")){
							sysFlags.gpa=false;
							sendCommand("{\"gdi\":0}\n");
						}
					}else if(sysFlags.gdi){
						if(b.containsKey("gdi")){
							sysFlags.gdi=false;
							
							if(debug)
								Log.d(TAG,"FIRST EDIT - START");
							
							//sendCommand("{\"1\":{\"ma\":0,\"sa\":1.8,\"tr\":36.50,\"mi\":8,\"po\":0,\"pm\":1}}\n");
							sendGcode("(msgSYSTEM FINISH)");
							system_sequence = false;
						}
					}
				}
				
				
			}
		}
		
		
	}
	
	@Override
	public void onResume()
	{
		IntentFilter updateFilter = new IntentFilter();
		updateFilter.addAction(BetaBotService.STATUS);
		updateFilter.addAction(BetaBotService.CONNECTION_STATUS);
		updateFilter.addAction(BetaBotService.JSON_ERROR);
		updateFilter.addAction(BetaBotService.AXIS_UPDATE);
		updateFilter.addAction(BetaBotService.MOTOR_UPDATE);
		updateFilter.addAction(BetaBotService.RAWS);
		updateFilter.addAction(BetaBotService.SYSTEM);
		mIntentReceiver = new BetaBotServiceReceiver();
		registerReceiver(mIntentReceiver, updateFilter);
			
		super.onResume();
		
		
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
		final ActionBar actionBar = getActionBar();
		if(f.getClass()==FileFragment.class) {
			actionBar.setSelectedNavigationItem(4);
		}else if(f.getClass()==HomeFragment.class) {
			actionBar.setSelectedNavigationItem(0);
		}else if(f.getClass()==JogFragment.class) {
			actionBar.setSelectedNavigationItem(2);
		}else if(f.getClass()==CommandFragment.class) {
			actionBar.setSelectedNavigationItem(3);
		}else if(f.getClass()==MixBookFragment.class) {
			actionBar.setSelectedNavigationItem(1);
		}else if(f.getClass()==ConfigFragment.class) {
			actionBar.setSelectedNavigationItem(5);
		}
		
		
	}

	@Override
	public void onPause(){
		unregisterReceiver(mIntentReceiver);
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		mMenuItemConnect = menu.findItem(R.id.connect);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//android.view.MenuItem menuConnect = menu.findItem(R.id.connect);
		if (connected)
			//menuConnect.setTitle(R.string.disconnect);
			mMenuItemConnect.setTitle(R.string.disconnect);
		else
			//menuConnect.setTitle(R.string.connect);
			mMenuItemConnect.setTitle(R.string.connect);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(debug)
			Log.d(TAG, "onOptionsItemSelected");
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.connect:
			if (pendingConnect) {
				if(debug)
					Log.d(TAG, "Waiting for connection...");
				return true;
			}
			if(bindType==0){
				if (BTBetaBot == null) {
					connected = false;
					currentServiceConnection = new DriverServiceConnection();
					bindDriver(currentServiceConnection);
					// We can't call connect until we know we have a binding.
					pendingConnect = true;
					if(debug)
						Log.d(TAG, "Binding... BTBetaBot");
					
					return true;
				}
				if (connected)
					BTBetaBot.disconnect();
				else {
					if(debug)
						Log.d(TAG, "Conn using old binding");
					//BTBetaBot.connect();
					Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
					startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				}
			
			}else{
				if (BetaBot == null) {
					connected = false;
					currentServiceConnection = new DriverServiceConnection();
					bindDriver(currentServiceConnection);
					// We can't call connect until we know we have a binding.
					pendingConnect = true;
					if(debug)
						Log.d(TAG, "Binding...");
					
					return true;
				}
				if (connected)
					BetaBot.disconnect();
				else {
					if(debug)
						Log.d(TAG, "Conn using old binding");
					BetaBot.connect();
					
				}
			}
			return true;
		case R.id.refresh:
			Fragment f = getSupportFragmentManager().findFragmentById(
					R.id.tabview);
			if (f != null && f.getClass() == FileFragment.class && ((FileFragment) f).isActive())
				return true;
			if (connected)
				if(bindType==0){
					BTBetaBot.refresh();
				}else{
					BetaBot.refresh();
				}
			else 
				endConnecting();
				Toast.makeText(this, "Not connected!", Toast.LENGTH_SHORT)
						.show();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public int getBluetoothConnectionState(){
		if (BTBetaBot!=null)
			return BTBetaBot.getState();
		else
			return BluetoothSerialService.STATE_NONE;
	}
	
	public void finishDialogNoBluetooth() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.alert_dialog_no_bt)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(R.string.app_name)
		.setCancelable( false )
		.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();            	
			}
		});
		AlertDialog alert = builder.create();
		alert.show(); 
	}
	
	private final static Handler mHandlerBT = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MESSAGE_STATE_CHANGE:
				if(debug)
					Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothSerialService.STATE_CONNECTED:
					connected = true;
					BTBetaBot.connect();
					if (mMenuItemConnect != null) {
						mMenuItemConnect.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
						mMenuItemConnect.setTitle(R.string.disconnect);
					}

					//mInputManager.showSoftInput(mEmulatorView, InputMethodManager.SHOW_IMPLICIT);

					//mTitle.setText(R.string.title_connected_to);
					//mTitle.append(mConnectedDeviceName);
					break;

				case BluetoothSerialService.STATE_CONNECTING:
					//mTitle.setText(R.string.title_connecting);
					break;

				case BluetoothSerialService.STATE_LISTEN:
				case BluetoothSerialService.STATE_NONE:
					if (mMenuItemConnect != null) {
						mMenuItemConnect.setIcon(android.R.drawable.ic_menu_search);
						mMenuItemConnect.setTitle(R.string.connect);
					}

					//mInputManager.hideSoftInputFromWindow(mEmulatorView.getWindowToken(), 0);

					//mTitle.setText(R.string.title_not_connected);

					break;
				}
				break;
			/*
			case MESSAGE_WRITE:
				if (mLocalEcho) {
					byte[] writeBuf = (byte[]) msg.obj;
					mEmulatorView.write(writeBuf, msg.arg1);
				}

				break;
				/*                
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;              
                mEmulatorView.write(readBuf, msg.arg1);

                break;
				 */                
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				
				
				Toast.makeText(mContext, "Connected to "
						+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				
				endConnecting();
				
				break;
			case MESSAGE_TOAST:
				endConnecting();
				Toast.makeText(mContext, msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	@Override
	public boolean isConnected() {
		boolean isconnected = false;
		if(connected){
			isconnected = true;
		}
		return isconnected;
	}

	@Override
	public void sendCommand(String cmd) {
		if(bindType==0){
			if((MainActivity.BTBetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BTBetaBot.send_command(cmd);
		}else{
			if((MainActivity.BetaBot == null)||(!MainActivity.connected))
				return;
			
			MainActivity.BetaBot.send_command(cmd);
		}
	}
	
	public void hitemup(Bundle b){
		if(debug)
			Log.d(TAG, "RAW log: " + b.getString("nwags"));
		if(mText.length()>TEXT_MAX_SIZE){
			mText.delete(0, TEXT_MAX_SIZE/2);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(b.getString("rawness"));
		String stu = sb.toString();
		if(!stu.endsWith("\n")&&!stu.endsWith("\r"))
			sb.append(BR);
		mText.append(sb);
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
		if(f!=null && f.getClass()==CommandFragment.class)
		{
			((CommandFragment) f).doSomething(sb.toString());
		}else{
			SharedPreferences.Editor Ed = settings.edit();
			Ed.putString("rawness", mText.toString());
			Ed.commit();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    
	    if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
	    	if(debug)
	    	{
	    		Toast.makeText(this, "PORTRAIT", Toast.LENGTH_SHORT)
	    		.show();
	    	}
	    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Fragment f;
			FragmentManager fm = getSupportFragmentManager();
			f = fm.findFragmentById(R.id.statusF);
			
			ft.remove(f);
			ft.addToBackStack(null);
			if(f!=null)
				ft.add(R.id.statusF, f);
	    	
			Fragment fbi = fm.findFragmentById(R.id.tabview);
			ft.remove(fbi);
			ft.addToBackStack(null);
			ft.add(R.id.tabview, fbi);
			ft.commit();
	    	
	    	
	    	
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	if(debug){
        		Toast.makeText(this, "LANDSCAPE", Toast.LENGTH_SHORT)
    			.show();
        	}
        	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Fragment f;
			FragmentManager fm = getSupportFragmentManager();
			f = fm.findFragmentById(R.id.statusF);
			
			ft.remove(f);
			ft.addToBackStack(null);
			try{
				ft.add(R.id.statusF, f);
			}catch(Exception e){}
			//ft.commit();
			
			try{
			Fragment fbi = fm.findFragmentById(R.id.tabview);
			ft.remove(fbi);
			ft.addToBackStack(null);
			ft.add(R.id.tabview, fbi);
			ft.commit();
			} catch(Exception e) {}
        }
	}

	@Override
	public void homeAll() {
		if(debug)
			Log.d(TAG,"includeIngredient: "+String.valueOf(includeIngredient));
		
		cancel = false;
		
		if(homeSB == null) 
			homeSB = new StringBuilder();
		else
			homeSB.setLength(0);	
		
		homeSB.append("HOMING "); // 10
		
		
		if(MainActivity.connected) {
			if(bindType==0) {
				BTBetaBot.send_flush();
			} else {
				BetaBot.send_flush();
			}
			
			String strAxes = settings.getString("nwa", "xyza");
			strAxes = strAxes.toLowerCase(Locale.ENGLISH);
			
			if(strAxes.contains("z")) {
				doZ = true;
				homeSB.append(" Z ");
			} if(strAxes.contains("a")) {
				if(!includeIngredient){
					doA = true;
					homeSB.append(" A ");
				}
			} if(strAxes.contains("x")) {
				doX = true;
				homeSB.append(" X ");
			} if(strAxes.contains("y")) {
				doY = true;
				homeSB.append(" Y ");
			} if(strAxes.contains("b")) {
				doB = true;
			} if(strAxes.contains("c")) {
				doC = true;
			}
			homeSB.append("\n");
			
			progressBar = new ProgressDialog(MainActivity.this);
			progressBar.setCancelable(true);
			progressBar.setIndeterminate(true);
			progressBar.setMessage("Homing...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.setButton(DialogInterface.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int id) {
					
					homingXYZA = homingXY = homingX = homingY = homingZ = homingA = false;
					cancel = true;
					sendReset();
					sendReset();
					progressBar.dismiss();
				}
			});
			progressBar.show();
			
			new Thread(new Runnable() {
				public void run() {
					homingXYZA = true;
					
					if(doZ) {
						if(!cancel) {
							homingZ = true;
							RunHome(2);
						}
						if(!cancel) {
							homeSB.append("\nhoming Z...");	// 12
							systemHandler.post(new Runnable() {
								public void run(){
									if(!cancel)
										progressBar.setMessage(homeSB.toString());
								}
							});
						}
						while(homingZ) {
							try{
								Thread.sleep(1000);
								if(debug)
									Log.d(TAG, "homingZ: "+String.valueOf(homingZ));
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
						homeSB.setLength(homeSB.length()-12);
						homeSB.append("Z homed\n");
						systemHandler.post(new Runnable() {
							public void run(){
								progressBar.setMessage(homeSB.toString());
							}
						});
					}
					if(!includeIngredient) {
						if(doA) {
							if(!cancel) {
								homingA = true;
								RunHome(3);
							}
							if(!cancel) {
								homeSB.append("\nhoming A..."); // 12
								systemHandler.post(new Runnable() {
									public void run() {
										if(!cancel)
											progressBar.setMessage(homeSB.toString());
									}
								});
							}
							while(homingA) {
								try{
									Thread.sleep(1000);
									if(debug)
										Log.d(TAG, "homingA: "+String.valueOf(homingA));
								} catch(InterruptedException e) {
									e.printStackTrace();
								}
							}
							if(!cancel){
								homeSB.setLength(homeSB.length()-12);
								homeSB.append("A homed\n");
								systemHandler.post(new Runnable() {
									public void run() {
										if(!cancel)
											progressBar.setMessage(homeSB.toString());
									}
								});
							}
						}
					}
					if(doX) {
						if(!cancel){
							homingX = true;
							RunHome(0);
						}
						if(!cancel){
							homeSB.append("\nhoming X..."); // 12
							systemHandler.post(new Runnable() {
								public void run(){
									if(!cancel)
										progressBar.setMessage(homeSB.toString());
								}
							});
						}
						while(homingX) {
							try{
								Thread.sleep(1000);
								if(debug)
									Log.d(TAG, "homingX: "+String.valueOf(homingX));
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
						if(!cancel){
							homeSB.setLength(homeSB.length()-12);
							homeSB.append("X homed\n");
							systemHandler.post(new Runnable() {
								public void run() {
									if(!cancel)
										progressBar.setMessage(homeSB.toString());
								}
							});
						}
						if(includeIngredient){
							TemplateXYZ templateXYZ= new TemplateXYZ();
							systemHandler.post(templateXYZ);
						}
					}
					
					if(doY) {
						if(!cancel){
							homingY = true;
							RunHome(1);
						}
						if(!cancel){
							homeSB.append("\nhoming Y..."); // 12
							systemHandler.post(new Runnable() {
								public void run(){
									if(!cancel)
										progressBar.setMessage(homeSB.toString());
								}
							});
						}
						while(homingY) {
							try{
								Thread.sleep(1000);
								if(debug)
									Log.d(TAG, "homingY: "+String.valueOf(homingY));
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
						if(!cancel){
							homeSB.setLength(homeSB.length()-12);
							homeSB.append("Y homed\n");
							systemHandler.post(new Runnable() {
								public void run() {
									if(!cancel)
										progressBar.setMessage(homeSB.toString());
								}
							});
						}
					}
					homingXYZA = homingXY = homingX = homingY = homingZ = homingA = false;
					progressBar.dismiss();
				}
			}).start();
			
		}
	}
	
	public class HomeRunner implements Runnable{
		HomeRunner(){ }

		@Override
		public void run() {
			if(connected)
				homeAll();
		}
		
	}
	
	public void homeXY() {
		cancel = false;
		if(MainActivity.connected){
			if(bindType==0){
				BTBetaBot.send_flush();
			}else{
				BetaBot.send_flush();
			}
			homingXY = true;
			
			progressBar = new ProgressDialog(MainActivity.this);
			progressBar.setCancelable(true);
			progressBar.setIndeterminate(true);
			progressBar.setMessage("Homing Y...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.setButton(DialogInterface.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int id) {
					homingXYZA = homingXY = homingX = homingY = homingZ = homingA = false;
					cancel = true;
					sendReset();
					progressBar.dismiss();
				}
			});
			progressBar.show();
			
			new Thread(new Runnable() {
				public void run() {
					homingXY = true;
					homingY = true;
					if(!cancel)
						RunHome(1);
					while(homingY) {
						try{
							Thread.sleep(1000);
							Log.d(TAG, "homing Y: "+String.valueOf(homingY));
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					systemHandler.post(new Runnable() {
						public void run() {
							progressBar.setMessage(" Y homed\n Homing X...");
						}
					});
					homingX = true;
					if(!cancel)
						RunHome(0);
					while(homingX) {
						try{
							Thread.sleep(1000);
							Log.d(TAG, "homing X: "+String.valueOf(homingX));
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					homingXY = false;
					progressBar.dismiss();
				}
			}).start();
		}
	}
	
	final Runnable HomeMenuRunner = new Runnable(){
		public void run() {
			homeMenu();
		}
	};
	
	public void homeMenu(){
		AlertDialog.Builder setHomingBldr = new AlertDialog.Builder(MainActivity.this);
		setHomingBldr.setMessage("Homing the machine is highly recommended")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("HOMING")
		.setCancelable( false )
		.setPositiveButton("HOME ALL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int id) {
				homeAll();
			}
		});
		/*
		.setNeutralButton("HOME X,Y", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				homeXY();
			}
		});
		*/
		AlertDialog alert = setHomingBldr.create();
		alert.show();
	}
	
	@Override
	public void homeX() {
		if(MainActivity.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingX = true;
				homingProgress("x");
			}
			
			
			if(!includeIngredient) {
				xGateA = false;
				xGateB = true;
				homingFlag = 11;
				
				if(bindType==0){
					BTBetaBot.send_flush();
				}else{
					BetaBot.send_flush();
				}
				sendCommand("{\"xsn\":3}\n");
				sendCommand("{\"ex\":0}\n");
				sendGcode("M22");
				sendGcode("g28.3x0");
				sendGcode("g91 g1 f"+String.valueOf(fullX/xHomeF)+" x"+String.valueOf(xHome));
				if(Float.valueOf(xHome)==0.0f)
					sendCommand("{\"sr\":\"\"}\n");
			} else {
				if(bindType==0) {
					BTBetaBot.send_flush();
				} else {
					BetaBot.send_flush();
				}
				sendCommand("{\"xsn\":3}\n");
				sendCommand("{\"ex\":0}\n");
				sendGcode("M22");
				sendGcode("g28.3x0");
				xGateA = true;
				xGateB = false;
				DelayedSendHome dsh = new DelayedSendHome("x");
				systemHandler.postDelayed(dsh,1000);
			}
		}
	}

	@Override
	public void homeY() {
		if(MainActivity.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingY = true;
				homingProgress("y");
			}
			if(!includeIngredient){
				yGateA = false;
				yGateB = true;
				homingFlag = 6;
				
				if(bindType==0){
					BTBetaBot.send_flush();
				}else{
					BetaBot.send_flush();
				}
				sendCommand("{\"ysn\":3}\n");
				sendCommand("{\"ex\":0}\n");
				sendGcode("M22");
				sendGcode("g28.3y0");
				sendGcode("g91 g1 f"+String.valueOf(fullY/yHomeF)+" y"+String.valueOf(yHome));
				if(Float.valueOf(yHome)==0.0f)
					sendCommand("{\"sr\":\"\"}\n");
			}else{
				if(bindType==0){
					BTBetaBot.send_flush();
				}else{
					BetaBot.send_flush();
				}
				sendCommand("{\"xsn\":3}\n");
				sendCommand("{\"ex\":0}\n");
				sendGcode("M22");
				sendGcode("g28.3y0");
				xGateA = true;
				xGateB = false;
				DelayedSendHome dsh = new DelayedSendHome("y");
				systemHandler.postDelayed(dsh,1000);
			}
		}
	}

	@Override
	public void homeZ() {
		if(MainActivity.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingZ = true;
				homingProgress("z");
			}
			if(!includeIngredient){
				zGateA = false;
				zGateB = true;
				homingFlag = 1;
				
				if(bindType==0){
					BTBetaBot.send_flush();
				}else{
					BetaBot.send_flush();
				}
				sendCommand("{\"zsn\":3}\n");
				sendCommand("{\"ex\":0}\n");
				sendGcode("M22");
				sendGcode("g28.3z0");
				sendGcode("g91 g1 f"+String.valueOf(fullZ/zHomeF)+" z"+String.valueOf(zHome));
				if(Float.valueOf(zHome)==0.0f)
					sendCommand("{\"sr\":\"\"}\n");
			}else{
				if(bindType==0){
					BTBetaBot.send_flush();
				}else{
					BetaBot.send_flush();
				}
				sendCommand("{\"xsn\":3}\n");
				sendCommand("{\"ex\":0}\n");
				sendGcode("M22");
				sendGcode("g28.3z0");
				xGateA = true;
				xGateB = false;
				DelayedSendHome dsh = new DelayedSendHome("z");
				systemHandler.postDelayed(dsh,1000);
			}
		}
	}
	
	public void sendHome(String axis){
		Log.d(TAG, "sendingHome "+axis);
		Handler xHandler = new Handler();
		if(axis.equals("x")){
			homingFlag = 12;
			sendGcode("!%");
			sendGcode("g28.3x0");
			String sh = null;
			if(!includeIngredient)
				sh = String.valueOf(fullX/xSendHomeF);
			else
				sh = String.valueOf(fullX/xNudgeF);
			SendGcodePST sgc = new SendGcodePST("g90g1f"+sh+" x"+String.valueOf(xSendHome));
			xHandler.postDelayed(sgc,400);
			if(Float.valueOf(xSendHome)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,400);
			}
		}else if(axis.equals("y")){
			homingFlag = 7;
			sendGcode("!%");
			sendGcode("g28.3y0");
			String sh = null;
			if(!includeIngredient)
				sh = String.valueOf(fullX/ySendHomeF);
			else
				sh = String.valueOf(fullX/yNudgeF);
			SendGcodePST sgc = new SendGcodePST("g90g1f"+sh+" y"+String.valueOf(ySendHome));
			xHandler.postDelayed(sgc,400);
			if(Float.valueOf(ySendHome)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,400);
			}
		}else if(axis.equals("z")){
			homingFlag = 2;
			sendGcode("!%");
			sendGcode("g28.3z0");
			String sh = null;
			if(!includeIngredient)
				sh = String.valueOf(fullX/zSendHomeF);
			else
				sh = String.valueOf(fullX/zNudgeF);
			SendGcodePST sgc = new SendGcodePST("g90g1f"+sh+" z"+String.valueOf(zSendHome));
			xHandler.postDelayed(sgc,400);
			if(Float.valueOf(zSendHome)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,400);
			}
		}else if(axis.equals("a")){
			homingFlag = 17;
			sendGcode("!%");
			sendGcode("g28.3a0");
			SendGcodePST sgc = new SendGcodePST("g90g1f"+String.valueOf(fullA/aSendHomeF)+" a"+String.valueOf(aSendHome));
			xHandler.postDelayed(sgc,400);
			if(Float.valueOf(aSendHome)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,400);
			}
		}
	}
	
	public class DelayedSendHome implements Runnable{
		private String axe;
		public DelayedSendHome(String _axis){
			axe = _axis;
		}
		@Override
		public void run() {
			sendHome(axe);
		}
	}
	
	public void tap(String axis){
		Log.d(TAG, "tapping "+axis);
		Handler xHandler = new Handler();
		if(axis.equals("x")){
			homingFlag = 13;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullX/xTapAF)+" x"+String.valueOf(xTapA));
			xHandler.postDelayed(sgc,1000);
			if(Float.valueOf(xTapA)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,1001);
			}
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullX/xTapBF)+" x"+String.valueOf(xTapB));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("y")){
			homingFlag = 8;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullY/yTapAF)+" y"+String.valueOf(yTapA));
			xHandler.postDelayed(sgc,1000);
			if(Float.valueOf(yTapA)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,1001);
			}
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullY/yTapBF)+" y"+String.valueOf(yTapB));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("z")){
			homingFlag = 3;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullZ/zTapAF)+" z"+String.valueOf(zTapA));
			xHandler.postDelayed(sgc,1000);
			if(Float.valueOf(zTapA)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,1001);
			}
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullZ/zTapBF)+" z"+String.valueOf(zTapB));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("a")){
			homingFlag = 18;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullA/aTapAF)+" a"+String.valueOf(aTapA));
			xHandler.postDelayed(sgc,1000);
			if(Float.valueOf(aTapA)==0.0f){
				SendCommandPST scmd = new SendCommandPST("{\"sr\":\"\"}");
				xHandler.postDelayed(scmd,1001);
			}
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullA/aTapBF)+" a"+String.valueOf(aTapB));
			xHandler.postDelayed(sgc,2000);
		}	
	}
	
	public void nudge(String axis){
		Log.d(TAG,"nudging "+axis);
		Handler xHandler = new Handler();
		if(axis.equals("x")){
			//if(includeIngredient)
			//	temporaryX = -posX-xNudge;
			
			SendGcodePST sgc = new SendGcodePST("g28.3x0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 14;
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullX/xNudgeF)+" x"+String.valueOf(xNudge));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("y")) {
			//if(includeIngredient)
			//	temporaryY = -posY-yNudge;
			
			SendGcodePST sgc = new SendGcodePST("g28.3y0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 9;
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullY/yNudgeF)+" y"+String.valueOf(yNudge));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("z")) {
			//if(includeIngredient)
			//	temporaryZ = -posZ-zNudge;
			
			SendGcodePST sgc = new SendGcodePST("g28.3z0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 4;
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullZ/zNudgeF)+" z"+String.valueOf(zNudge));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("a")) {
			SendGcodePST sgc = new SendGcodePST("g28.3a0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 19;
			sgc = new SendGcodePST("g91g1f"+String.valueOf(fullA/aNudgeF)+" a"+String.valueOf(aNudge));
			xHandler.postDelayed(sgc,2000);
		}
	}
	
	public void imHome(String axis) {
		if(axis.equals("x")) {
			SendGcodePST hx = new SendGcodePST("g28.3x0");
			systemHandler.postDelayed(hx, 300);
			homingX = false;
			xGateA = false;
		}else if(axis.equals("y")) {
			SendGcodePST hy = new SendGcodePST("g28.3y0");
			systemHandler.postDelayed(hy, 300);
			homingY = false;
			yGateA = false;
		}else if(axis.equals("z")) {
			SendGcodePST hz = new SendGcodePST("g28.3z0");
			systemHandler.postDelayed(hz, 300);
			homingZ = false;
			zGateA = false;
		}else if(axis.equals("a")) {
			SendGcodePST ha = new SendGcodePST("g28.3a0");
			systemHandler.postDelayed(ha, 1300);
			homingA = false;
			aGateA = false;
		}
	}
	
	@Override
	public void homeA() {
		if(MainActivity.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingA = true;
				homingProgress("a");
			}
			aGateA = false;
			aGateB = true;
			homingFlag = 16;
			
			if(bindType==0){
				BTBetaBot.send_flush();
			}else{
				BetaBot.send_flush();
			}
			sendCommand("{\"asn\":3}\n");
			sendCommand("{\"ex\":0}\n");
			sendGcode("M22");
			sendGcode("g28.3a0");
			sendGcode("g91 g1 f"+String.valueOf(fullA/aHomeF)+" a"+String.valueOf(aHome));
			if(Float.valueOf(aHome)==0.0f)
				sendCommand("{\"sr\":\"\"}\n");
		}
	}
	
	public class SendGcodePST implements Runnable{

		String cmdStr;
		
		SendGcodePST(String _cmdStr){
			cmdStr = _cmdStr;
		}
		
		@Override
		public void run() {
			sendGcode(cmdStr);
		}
	}
	
	public class SendCommandPST implements Runnable{
		String cmdStr;
		SendCommandPST(String _cmdStr){
			cmdStr = _cmdStr + "\n";
		}
		@Override
		public void run() {
			sendCommand(cmdStr);
		}
	}
	
	public void RunHome(int axisCode) {
		switch(axisCode){
		case 0:
			DelayedHomeX delayedHomeX = new DelayedHomeX();
			homingHandler.postDelayed(delayedHomeX,3010);
			break;
		case 1:
			DelayedHomeY delayedHomeY = new DelayedHomeY();
			homingHandler.postDelayed(delayedHomeY,3010);
			break;
		case 2:
			DelayedHomeZ delayedHomeZ = new DelayedHomeZ();
			homingHandler.postDelayed(delayedHomeZ,3010);
			break;
		case 3:
			DelayedHomeA delayedHomeA = new DelayedHomeA();
			homingHandler.postDelayed(delayedHomeA,3010);
			break;
			default:
				break;
		}
	}
	
	public class DelayedHomeX implements Runnable{
		DelayedHomeX(){	}

		@Override
		public void run() {
			if(!cancel)
				homeX();
		}
	}
	public class DelayedHomeY implements Runnable{
		DelayedHomeY(){	}

		@Override
		public void run() {
			if(!cancel)
				homeY();
		}
	}
	public class DelayedHomeZ implements Runnable{
		DelayedHomeZ(){	}

		@Override
		public void run() {
			if(!cancel)
				homeZ();
		}
	}
	public class DelayedHomeA implements Runnable{
		DelayedHomeA(){	}

		@Override
		public void run() {
			if(!cancel)
				homeA();
		}
	}
	
	public void homingProgress(String _axis){
		
		final String axis = _axis;
		
		progressBar = new ProgressDialog(MainActivity.this);
		progressBar.setCancelable(true);
		progressBar.setIndeterminate(true);
		progressBar.setMessage("Homing "+axis+" axis ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				homingXYZA = homingXY = homingX = homingY = homingZ = homingA = false;
				homingFlag = 0;
				progressBar.dismiss();
			}
		});
		progressBar.show();
		
		new Thread(new Runnable(){
			public void run() {
				if(axis.equals("x")){
					while(homingX){
						try{
							Thread.sleep(500);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}else if(axis.equals("y")){
					while(homingY){
						try{
							Thread.sleep(500);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}else if(axis.equals("z")){
					while(homingZ){
						try{
							Thread.sleep(500);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}else if(axis.equals("a")){
					while(homingA){
						try{
							Thread.sleep(500);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}
				homingXYZA = homingXY = homingX = homingY = homingZ = homingA = false;
				progressBar.dismiss();
			}
		}).start();
		
	}
	
	@Override
	public void templateXY() {
		if(connected){
		
		tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View myIngredientView = inflater.inflate(R.layout.template_xy, null, false);

	    // textViewWithScroll is the name of our TextView on scroll_text.xml
	    final EditText et = (EditText) myIngredientView
	            .findViewById(R.id.ingrText);
	    et.setTypeface(tf);
	    et.setHint("Ingredient Name");
	    
	    TextView tv = (TextView) myIngredientView.findViewById(R.id.mixbookFile);
	    tv.setText(mixbookFilename);
	    
	    tv = (TextView) myIngredientView.findViewById(R.id.templateFile);
	    tv.setText(templateFilename);
	    
	    openFiles(mixbookFilename,templateFilename);
	    
	    tv = (TextView) myIngredientView.findViewById(R.id.templateName);
	    tv.setText(template.getName());
	    
	    tv = (TextView) myIngredientView.findViewById(R.id.position);
	    tv.setText("X: "+String.valueOf(posX)+" Y: "+String.valueOf(posY));
	    
	    // Initializing a blank textview so that we can just append a text later
		AlertDialog.Builder ingBldr = new AlertDialog.Builder(MainActivity.this);
		ingBldr.setView(myIngredientView)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("New Ingredient")
		.setCancelable( true )
		.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int id) {
				String ingrid = et.getText().toString();
				if(ingrid.length()>0){
					mixbook.getTemplate().addIngredient(ingrid,String.valueOf(posX),String.valueOf(posY));
					template.addIngredient(ingrid, String.valueOf(posX), String.valueOf(posY));
					
					fileSave(mixbookFilename,mixbook.makeString());
					fileSave(templateFilename,template.makeString());
					
					SharedPreferences.Editor Ed = settings.edit();
					Ed.putString("mixbook", mixbook.makeString());
					Ed.putString("template", template.makeString());
					Ed.commit();
					
				} else {
					Toast mcgoat = Toast.makeText(getApplicationContext(), "No Ingredient was entered!", Toast.LENGTH_SHORT);
					mcgoat.setGravity(Gravity.CENTER, 0, 0);
					mcgoat.show();
				}
			}
		});
		AlertDialog alert = ingBldr.create();
		alert.show();	
		} else {
			Toast mcgoat = Toast.makeText(getApplicationContext(), "Not connected!", Toast.LENGTH_SHORT);
			mcgoat.setGravity(Gravity.CENTER, 0, 0);
			mcgoat.show();
		}
	}
	
	@Override
	public void setPosXYZ(float x, float y, float z) {
		posX = x;
		posY = y;
		posZ = z;
	}
	
	private void openFiles(String mxbkFileString, String tempFileString){
		
		try{
			if(mixbook==null)
				mixbook=new MixBook();
			
			mixbook.clear();
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(mxbkFileString));
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			while((line = br.readLine())!=null) {
				String[] pieces = line.split(",");
				if(pieces.length<3){
					pieces = line.split("\t");
				}else{
					pieces = line.split(",");
				}
				
				if(pieces.length<26){
					
				}
					
				if(!line.endsWith("\n"))
					line = line + "\n";
				sb.append(line);
			}
			mixbook_code = mixbook.Inflate(sb.toString());
			
			if(template==null)
				template=new Template();
			
			br.close();
			sb.setLength(0);
			line = "";
			br = new BufferedReader(new FileReader(mxbkFileString));
			
			while((line = br.readLine())!=null){
				String[] pieces = line.split(",");
				if(pieces.length<26){
					
				}
					
				if(!line.endsWith("\n"))
					line = line + "\n";
				sb.append(line);
			}
			has_template = template.Inflate(sb.toString());
			
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
			mixbook.clear();
			mixbook_code = -1;
		}
	}
	
	private void fileSave(String fileString, String saveString){
		File file = new File(fileString);
		FileOutputStream outputStream;
		
		try{
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
			
			outputStream = new FileOutputStream(file, false);
			outputStream.write(saveString.getBytes());
			outputStream.close();
			
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error occurred while saving", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	public void templateXYZ() {
				if(connected){
				
				tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View myIngredientView = inflater.inflate(R.layout.template_xy, null, false);

			    // textViewWithScroll is the name of our TextView on scroll_text.xml
			    final EditText et = (EditText) myIngredientView
			            .findViewById(R.id.ingrText);
			    et.setTypeface(tf);
			    et.setHint("Ingredient Name");
			    
			    TextView tv = (TextView) myIngredientView.findViewById(R.id.mixbookFile);
			    tv.setText(mixbookFilename);
			    
			    tv = (TextView) myIngredientView.findViewById(R.id.templateFile);
			    tv.setText(templateFilename);
			    
			    openFiles(mixbookFilename,templateFilename);
			    
			    tv = (TextView) myIngredientView.findViewById(R.id.templateName);
			    tv.setText(template.getName());
			    
			    tv = (TextView) myIngredientView.findViewById(R.id.position);
			    if(includeIngredient){
			    	tv.setText("X: "+String.valueOf(temporaryX)+" Y: "+String.valueOf(temporaryY)+" Z: "+String.valueOf(temporaryZ));
			    	if(temporaryX<0f)
			    		temporaryX=0f;
			    	if(temporaryY<0f)
			    		temporaryY=0f;
			    	if(temporaryZ<0f)
			    		temporaryZ=0f;
			    	
			    }else{
			    	tv.setText("X: "+String.valueOf(posX)+" Y: "+String.valueOf(posY)+" Z: "+String.valueOf(posZ));
			    	if(posX<0f)
			    		posX=0f;
			    	if(posY<0f)
			    		posY=0f;
			    	if(posZ<0f)
			    		posZ=0f;
			    	
			    }
			    // Initializing a blank textview so that we can just append a text later
				AlertDialog.Builder ingBldr = new AlertDialog.Builder(MainActivity.this);
				ingBldr.setView(myIngredientView)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("New Ingredient")
				.setCancelable( true )
				.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int id) {
						String ingrid = et.getText().toString();
						if(ingrid.length()>0){
							mixbook.getTemplate().addIngredient(ingrid, String.valueOf(posX), String.valueOf(posY), String.valueOf(posZ));
							template.addIngredient(ingrid, String.valueOf(posX), String.valueOf(posY), String.valueOf(posZ));
							
							fileSave(mixbookFilename,mixbook.makeString());
							fileSave(templateFilename,template.makeString());
							
							SharedPreferences.Editor Ed = settings.edit();
							Ed.putString("mixbook", mixbook.makeString());
							Ed.putString("template", template.makeString());
							Ed.commit();
							
							
							Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
							
							if(f!=null){
								if(f.getClass()==JogFragment.class) {
									((JogFragment) f).updateIngredients();
								}
							}
							
						} else {
							Toast mcgoat = Toast.makeText(getApplicationContext(), "No Ingredient was entered!", Toast.LENGTH_SHORT);
							mcgoat.setGravity(Gravity.CENTER, 0, 0);
							mcgoat.show();
						}
						if(includeIngredient)
							sendCommand("$me\n");
						
					}
				});
				AlertDialog alert = ingBldr.create();
				alert.show();	
				} else {
					Toast mcgoat = Toast.makeText(getApplicationContext(), "Not connected!", Toast.LENGTH_SHORT);
					mcgoat.setGravity(Gravity.CENTER, 0, 0);
					mcgoat.show();
				}
	}
	
	public class TemplateXYZ implements Runnable{

		@Override
		public void run() {
			if(connected){
				
				tf = Typeface.createFromAsset(getAssets(), "fonts/Inconsolata.otf");
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View myIngredientView = inflater.inflate(R.layout.template_xy, null, false);

			    // textViewWithScroll is the name of our TextView on scroll_text.xml
			    final EditText et = (EditText) myIngredientView
			            .findViewById(R.id.ingrText);
			    et.setTypeface(tf);
			    et.setHint("Ingredient Name");
			    
			    TextView tv = (TextView) myIngredientView.findViewById(R.id.mixbookFile);
			    tv.setText(mixbookFilename);
			    
			    tv = (TextView) myIngredientView.findViewById(R.id.templateFile);
			    tv.setText(templateFilename);
			    
			    openFiles(mixbookFilename,templateFilename);
			    
			    tv = (TextView) myIngredientView.findViewById(R.id.templateName);
			    tv.setText(template.getName());
			    
			    tv = (TextView) myIngredientView.findViewById(R.id.position);
			    if(includeIngredient){
			    	tv.setText("X: "+String.valueOf(temporaryX)+" Y: "+String.valueOf(temporaryY)+" Z: "+String.valueOf(temporaryZ));
			    }else{
			    	tv.setText("X: "+String.valueOf(posX)+" Y: "+String.valueOf(posY)+" Z: "+String.valueOf(posZ));
			    }
			    // Initializing a blank textview so that we can just append a text later
				AlertDialog.Builder ingBldr = new AlertDialog.Builder(MainActivity.this);
				ingBldr.setView(myIngredientView)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("New Ingredient")
				.setCancelable( false )
				.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int id) {
						String ingrid = et.getText().toString();
						if(ingrid.length()>0){
							mixbook.getTemplate().addIngredient(ingrid, String.valueOf(temporaryX), String.valueOf(temporaryY), String.valueOf(temporaryZ));
							template.addIngredient(ingrid, String.valueOf(temporaryX), String.valueOf(temporaryY), String.valueOf(temporaryZ));
							
							fileSave(mixbookFilename,mixbook.makeString());
							fileSave(templateFilename,template.makeString());
							
							SharedPreferences.Editor Ed = settings.edit();
							Ed.putString("mixbook", mixbook.makeString());
							Ed.putString("template", template.makeString());
							Ed.commit();
							
							Fragment f = getSupportFragmentManager().findFragmentById(R.id.tabview);
							
							if(f!=null){
								if(f.getClass()==JogFragment.class) {
									((JogFragment) f).updateIngredients();
								}
							}
							
						} else {
							Toast mcgoat = Toast.makeText(getApplicationContext(), "No Ingredient was entered!", Toast.LENGTH_SHORT);
							mcgoat.setGravity(Gravity.CENTER, 0, 0);
							mcgoat.show();
						}
						if(includeIngredient){
							sendCommand("$me\n");
							includeIngredient=false;
						}
					}
				})
				.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(includeIngredient){
							sendCommand("$me\n");
							includeIngredient=false;
						}
					}
					
				});
				AlertDialog alert = ingBldr.create();
				alert.show();	
				} else {
					Toast mcgoat = Toast.makeText(getApplicationContext(), "Not connected!", Toast.LENGTH_SHORT);
					mcgoat.setGravity(Gravity.CENTER, 0, 0);
					mcgoat.show();
				}
		}
		
	}
	
	@Override
	public void sensor() {
		sensing = !sensing;
		if(sensing==true){
			sendGcode("M24");
		}else{
			sendGcode("M25");
		}
	}
	
	public void primeHoming(){	}
	
	public class DelayedHoming implements Runnable{
		private String axis;
		
		DelayedHoming(String _axis){
			axis = _axis;
		}

		@Override
		public void run() {
			if(axis.equals("x")){
				homeX();
			}else if(axis.equals("y")){
				homeY();
			}else if(axis.equals("z")){
				homeZ();
			}else if(axis.equals("a")){
				homeA();
			}
		}
	}
	
	public class DelayedCommand implements Runnable{
		String comm;
		
		DelayedCommand(){	}
		
		DelayedCommand(String _comm){
			comm = _comm;
		}
		
		void setCommand(String _comm){
			comm = _comm;
		}
		
		@Override
		public void run() {
			String commander = comm + "\n";
			sendCommand(commander);
		}
		
	}
	
	public static void endConnecting(){
		Log.d(TAG, "endConnecting");
		connecting = false;
	}
	
	public void endStartup(){
		Log.d(TAG, "endStartup");
		startup_sequence = false;
	}
	
	@Override
	public void unlock() {
		final ActionBar actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Resources res = getResources();
		tabsL = res.getStringArray(R.array.tabArray);
		tabListenerL = (TabListener) new MainTabListener();
		EdL = settings.edit();
		if(locked){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Congratulations, you can now change settings. But please proceed with caution.")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Configuration Access")
			.setCancelable( false )
			.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					locked = false;
					EdL.putString("locked", "false");
					EdL.commit();
					if(actionBar.getTabCount()<4){
						for(int i=3; i < tabsL.length; i++){
							Tab tab = actionBar.newTab();
							tab.setText(tabsL[i]);
							tab.setTag(tabsL[i]);
							tab.setTabListener(tabListenerL);
							actionBar.addTab(tab);
						}
					}
				}
			})
			.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			
		} else {
			for(int i = tabsL.length; i>3; i--){
				actionBar.removeTabAt(i-1);
			}
			/*
			if(actionBar.getTabCount()>5){
				actionBar.removeTabAt(5);
				actionBar.removeTabAt(4);
				actionBar.removeTabAt(3);
			}
			*/
			
			EdL.putString("locked", "true");
			EdL.commit();
			locked = true;
		}
	}
	
	@Override
	public boolean isLocked() {
		return locked;
	}
	
	@Override
	public void toggleStatus() {
		showStatus = !showStatus;
		if(showStatus)
			showStatus();
		else
			hideStatus();
			
	}
	
	public void showStatus(){
		findViewById(R.id.statusF).setVisibility(1);
	}
	
	public void hideStatus(){
		findViewById(R.id.statusF).setVisibility(8);
	}
	
	@Override
	public boolean isStatus() {
		return showStatus;
	}
	
	public void LoadSettings(){
		
		//loading_settings = true;
		loadSettings();
		
		/*new Thread(new Runnable() {
			public void run() {
				while(loading_settings) {
					try{
						Thread.sleep(1000);
					} catch(InterruptedException e) {
						
					}
				}
				//systemHandler.post(HomeRunner);//homeMenu();
			}
		}).start();
		*/
		
		/*
		startup_sequence = true;
		system_sequence = true;
		sysFlags = new SysFlags();
		
		motor_sequence = true;
		motFlags1 = new MotFlags();
		motFlags2 = new MotFlags();
		motFlags3 = new MotFlags();
		motFlags4 = new MotFlags();
		
		axis_sequence = true;
		axiFlagsX = new AxiFlags();
		axiFlagsY = new AxiFlags();
		axiFlagsZ = new AxiFlags();
		axiFlagsA = new AxiFlags();
		
		settings_sequence = true;
		
		progressBar = new ProgressDialog(MainActivity.this);
		progressBar.setCancelable(true);
		progressBar.setIndeterminate(true);
		progressBar.setMessage("Loading System settings ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.setButton(DialogInterface.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int id) {
				startup_sequence = false;
				system_sequence = false;
				motor_sequence = false;
				axis_sequence = false;
				settings_sequence = false;
				sendReset();
				progressBar.dismiss();
			}
		});
		progressBar.show();
		
		new Thread(new Runnable(){
			public void run() {
				int i = 0;
				if(system_sequence)
					RunStartup(1);
				while(system_sequence){
					i++;
					try{
						Thread.sleep(1000);
						Log.d(TAG, "system_sequence: "+String.valueOf(system_sequence));
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					if(i>30){
						systemHandler.postDelayed(new Runnable() {
							public void run(){
								progressBar.setMessage("Settings are taking too long to load...\n\nYour best bet is to try\npower cycling the tron.");
							}
						}, 1000);
					}
				}
				systemHandler.post(new Runnable() {
					public void run(){
						progressBar.setMessage("       System settings... X\nLoading Motor settings ...");
					}
				});
				i=0;
				if(motor_sequence)
					RunStartup(2);
				while(motor_sequence){
					i++;
					try{
						Thread.sleep(1000);
						Log.d(TAG, "motor_sequence: "+String.valueOf(motor_sequence));
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					if(i>10){
						motorHandler.postDelayed(new Runnable() {
							public void run(){
								progressBar.setMessage("There was a problem loading motor settings.\nPlease manually reset the board");
							}
						}, 1000);
					}
				}
				motorHandler.post(new Runnable() {
					public void run(){
						progressBar.setMessage("       System settings... X\n       Motor settings...    X\nLoading Axis settings ...");
					}
				});
				i=0;
				if(axis_sequence)
					RunStartup(3);
				while(axis_sequence){
					i++;
					try{
						Thread.sleep(1000);
						Log.d(TAG, "axis_sequence: "+String.valueOf(axis_sequence));
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					if(i>10){
						axisHandler.postDelayed(new Runnable() {
							public void run(){
								progressBar.setMessage("There was a problem loading axis settings.\nPlease manually reset the board");
							}
						}, 1000);
					}
				}
				axisHandler.post(new Runnable() {
					public void run(){
						progressBar.setMessage("       System settings... X\n       Motor settings...    X\n       Axis settings...       X\nLoading remaining settings ...");
					}
				});
				i=0;
				if(settings_sequence)
					RunStartup(4);
				while(settings_sequence){
					try{
						Thread.sleep(2000);
						Log.d(TAG, "settings_sequence: "+String.valueOf(settings_sequence));
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					i++;
					if(i>10){
						settingsHandler.postDelayed(new Runnable() {
							public void run(){
								progressBar.setMessage("There was a problem loading settings.\nPlease manually reset the board");
							}
						}, 1000);
					}
				}
				
				HomeMenuRunner homeMenuRunner = new HomeMenuRunner();
				systemHandler.postDelayed(homeMenuRunner, 200);
				progressBar.dismiss();
				
			}
		}).start();
		*/
		
	}
	
	final Runnable SettingsRunner = new Runnable(){
		public void run() {
			LoadSettings();
		}
	};
	
	public void RunStartup(int startupCode) {
		
		switch(startupCode){
		case 0:
			break;
		case 1:
			SystemStartup systemStartup = new SystemStartup();
			homingHandler.postDelayed(systemStartup, 5010);
			break;
		case 2:
			MotorStartup motorStartup = new MotorStartup();
			homingHandler.postDelayed(motorStartup, 2010);
			break;
		case 3:
			AxisStartup axisStartup = new AxisStartup();
			homingHandler.postDelayed(axisStartup, 2010);
			break;
		case 4:
			SettingsStartup settingsStartup = new SettingsStartup();
			homingHandler.postDelayed(settingsStartup, 2010);
			break;
		default:
			break;	
		}
		
	}
	
	public class SystemStartup implements Runnable{
		
		SystemStartup(){	}
		
		@Override
		public void run() {
			sendGcode("(msgSYSTEM START)");
			sendCommand("{\"si\":250}\n");
		}
	}
	
	public class MotorStartup implements Runnable{
		
		MotorStartup(){	}
		
		@Override
		public void run() {
			sendGcode("(msgMOTOR START)");
			sendCommand("{\"1\":{\"ma\":0,\"sa\":1.8,\"tr\":36.50,\"mi\":8,\"po\":0,\"pm\":0}}\n");
			
		}
	}
	
	public class AxisStartup implements Runnable{
		
		AxisStartup(){	}
		
		@Override
		public void run() {
			sendGcode("(msgAXIS START)");
			if(debug)
				Log.d(TAG,"AXIS!!!");
			//200000000
			sendCommand("{\"x\":{\"am\":1,\"vm\":10240,\"fr\":10240,\"tm\":150,\"jm\":100000000}}\n");
		}
	}
	
	private class SettingsStartup implements Runnable{
		
		SettingsStartup(){	}
		
		@Override
		public void run() {
			if(debug)
				Log.d(TAG,"SettingsStratup running!");
			
			PrefsListener newPreferencesListener = new PrefsListener();
			SharedPreferences newSettings;
			Context newContext = getApplicationContext();
			newSettings = PreferenceManager.getDefaultSharedPreferences(newContext);
			newSettings.registerOnSharedPreferenceChangeListener(newPreferencesListener);
			SharedPreferences.Editor Ed = newSettings.edit();
			
			sendGcode("(msgSETTINGS START)");
			
			Ed.putString("1ma", "0");
			Ed.putString("1mi", "8");
			Ed.putString("1tr", "36.54");
			Ed.putString("1sa", "1.8");
			Ed.putBoolean("1po", false);
			Ed.putBoolean("1pm",false);
			
			Ed.putString("2ma", "1");
			Ed.putString("2mi", "8");
			Ed.putString("2tr", "36.54");
			Ed.putString("2sa", "1.8");
			Ed.putBoolean("2po", true);
			Ed.putBoolean("2pm", false);
			
			Ed.putString("3ma", "2");
			Ed.putString("3mi", "8");
			Ed.putString("3tr", "8.1");
			Ed.putString("3sa", "1.8");
			Ed.putBoolean("3po", true);
			Ed.putBoolean("3pm", false);
			
			Ed.putString("4ma", "3");
			Ed.putString("4mi", "8");
			Ed.putString("4tr", "360");
			Ed.putString("4sa", "1.8");
			Ed.putBoolean("4po", false);
			Ed.putBoolean("4pm", true);
			
			Ed.putString("1am", "1");
			Ed.putString("2am", "1");
			Ed.putString("3am", "1");
			Ed.putString("4am", "3");
			
			Ed.putString("1vm", "10240");
			Ed.putString("2vm", "10240");
			Ed.putString("3vm", "10240");
			Ed.putString("4vm", "36000");
			
			Ed.putString("1fr", "10240");
			Ed.putString("2fr", "10240");
			Ed.putString("3fr", "10240");
			Ed.putString("4fr", "36000");
			
			Ed.putString("1tm", "150");
			Ed.putString("2tm", "150");
			Ed.putString("3tm", "150");
			Ed.putString("4tm", "150");
			
			Ed.putString("1jm", "100000000");
			Ed.putString("2jm", "100000000");
			Ed.putString("3jm", "75000000");
			Ed.putString("4jm", "200000000");
			
			Ed.putString("4ra", "0.3183099");
			
			Ed.commit();
			settings_sequence=false;
			
			sendGcode("(msgSETTINGS FINISH)");
			fullX = Integer.valueOf(settings.getString("1vm", "10240"));
			fullY = Integer.valueOf(settings.getString("2vm", "10240"));
			fullZ = Integer.valueOf(settings.getString("3vm", "10240"));
			fullA = Integer.valueOf(settings.getString("4vm", "36000"));
		}
		
	}
	
	
	@Override
	public void templateManual() {
		sendCommand("$md\n");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("MOVE HEAD TO DESIRED POSITION THEN PRESS \"ENTER\"")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("SET INGREDIENT MANUALLY")
		.setCancelable( false )
		.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				includeIngredient = true;
				sendCommand("$me\n");
				homeAll();
			}
		})
		.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				sendCommand("$me\n");
				includeIngredient = true;
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public void shortcut(String rString, String tString, boolean _rBool) {
		if(_rBool){
			if(connected) {
				final String frString = rString;
				final String ftString = tString;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("SELECT METHOD FOR RUNNING RECIPE")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("RUN RECIPE")
				.setCancelable(true)
				.setPositiveButton("RUN!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(mContext, "starting single run", Toast.LENGTH_LONG).show();
						
						final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						final List<RunningServiceInfo> services = 
								activityManager.getRunningServices(Integer.MAX_VALUE);
						for (int i = 0; i < services.size(); i++){
							if(debug){
								Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
								Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
								Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
							}
							if(services.get(i).service.getClassName().equals("com.nwags.BetaBot.Bluetooth.BluetoothSerialService")){
								Intent runIntent = new Intent(mContext,Boom.class);
								runIntent.putExtra("recipe", frString);
								runIntent.putExtra("template", ftString);
								runIntent.putExtra("visualize", false);
								runIntent.putExtra("runcontinously", false);
								startActivityForResult(runIntent, REQUEST_BOOM);
							}
						}
					}
				})
				.setNeutralButton("RUN CONTINUOUSLY", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(mContext, "starting continuous run", Toast.LENGTH_LONG).show();
						
						final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						final List<RunningServiceInfo> services = 
								activityManager.getRunningServices(Integer.MAX_VALUE);
						for (int i = 0; i < services.size(); i++){
							if(debug){
								Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
								Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
								Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
							}
							if(services.get(i).service.getClassName().equals("com.nwags.BetaBot.Bluetooth.BluetoothSerialService")){
								Intent runIntent = new Intent(mContext,Boom.class);
								runIntent.putExtra("recipe", frString);
								runIntent.putExtra("template", ftString);
								runIntent.putExtra("visualize", false);
								runIntent.putExtra("runcontinously", true);
								startActivityForResult(runIntent, REQUEST_BOOM);
							}
						}
					}
				})
				.setNegativeButton("VISUALIZE", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						final List<RunningServiceInfo> services = 
								activityManager.getRunningServices(Integer.MAX_VALUE);
						for (int i = 0; i < services.size(); i++){
							if(debug){
								Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
								Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
								Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
							}
							if(services.get(i).service.getClassName().equals("com.nwags.BetaBot.Bluetooth.BluetoothSerialService")){
								Intent runIntent = new Intent(mContext,Boom.class);
								runIntent.putExtra("recipe", frString);
								runIntent.putExtra("template", ftString);
								runIntent.putExtra("visualize", true);
								runIntent.putExtra("runcontinously", false);
								startActivityForResult(runIntent, REQUEST_BOOM);
							}
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Toast.makeText(mContext,"Not connected", Toast.LENGTH_SHORT).show();
			}
		}else{
			
			if(mixbook_code==2||mixbook_code==3){
        		//Toast.makeText(getActivity(),String.valueOf(mxbkCode), Toast.LENGTH_SHORT).show();
            	final Intent recipeIntent = new Intent(mContext,RecipesActivity.class);
            	recipeIntent.putExtra("mixbook",mixbook.makeString());
            	recipeIntent.putExtra("file",mixbookFilename);
            	if(debug)
            		Log.d(TAG,"mixbook.makeString: "+mixbook.makeString());
            	if(mixbook_code==3){
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
    	    		
            		
            		AlertDialog.Builder aldb = new AlertDialog.Builder(mContext);
            		aldb.setMessage("WARNING: The following Recipes and Ingredients are not defined in the Template: "+sb.toString())
            		.setIcon(android.R.drawable.ic_dialog_alert)
            		.setTitle("WARNING")
            		.setCancelable( true )
            		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            			@Override
            			public void onClick(DialogInterface dialog,
            					int id) {
            				startActivityForResult(recipeIntent, REQUEST_RECIPE);
            			}
            		});
            		
    	    		AlertDialog ad = aldb.create();
    	    		ad.show();
            	}else{
            		startActivityForResult(recipeIntent, REQUEST_RECIPE);
            	}
            	
        	}else if(mixbook_code==-1){
        		Toast.makeText(mContext,"Where's the mixbook?", Toast.LENGTH_SHORT).show();
        	}else if(mixbook_code==1){
        		Toast.makeText(mContext,"Please add a recipe first", Toast.LENGTH_SHORT).show();
        	}else if(mixbook_code==4){
        		Toast.makeText(mContext,"Please attach a Template", Toast.LENGTH_SHORT).show();
        	}
        	
		}
	}
	
	@Override
	public void saveConfiguration(String str) {
		// TODO Auto-generated method stub
		saveConfigs(str);
	}
	
	@Override
	public void loadConfiguration(String str) {
		// TODO Auto-generated method stub
		readConfigs(str);
	}
	
	public void loadSettings(){
		tries = 0;
		settSB = new StringBuilder();
		
		settSB.setLength(0);
		settSB.append("Loading settings...\n");
		
		
		progressBart = new ProgressDialog(MainActivity.this);
		progressBart.setCancelable(false);
		progressBart.setIndeterminate(true);
		progressBart.setMessage(settSB.toString());
		progressBart.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBart.setProgress(0);
		progressBart.setMax(100);
		progressBart.show();
		
		loading_settings = true;
		settingsIndex = 0;
		
		new Thread(new Runnable(){
			public void run() {
				int timer = 0;
				while(loading_settings){
					try{
						Thread.sleep(2000);
						timer++;
						if(timer>30){
							loading_settings = trying = false;
							systemHandler.post(ToastMe);
							break;
						}
						if(timer>6&&timer<30){
							if(settingsIndex<10) {
								//loading_settings = trying = false;
								Log.d(TAG,"spank");
							}
						}
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
				progressBart.dismiss();
				if(settingsIndex<10){
					loading_settings = trying = false;
					systemHandler.post(ToastMe);
				}
				systemHandler.postDelayed(HomeMenuRunner, 200);
			}
		
			
		}).start();
		
		
		
		
		machine.loadPreferenceSettings();
		
		ArrayList<String> preconfig = new ArrayList<String>();
		Map<String,?> mp = settings.getAll();
		Iterator<?> it = mp.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) it.next();
			key = (String) mapEntry.getKey();
			preconfig.add(key);
		}
		fillConfigurations(preconfig);
		sendReset();
		DelayedTry dt = new DelayedTry(configurations.get(settingsIndex));
		//tryToLoadSetting(configurations.get(settingsIndex));
		systemHandler.postDelayed(dt,5000);
	}
	
	public class DelayedTry implements Runnable {
		private String axe;
		public DelayedTry(String _axis) {
			axe = _axis;
		}
		@Override
		public void run() {
			tryToLoadSetting(axe);
		}
	}
	
	final Runnable ToastMe = new Runnable(){
		public void run() {
			Toast.makeText(mContext, "There was an error loading settings. Please power cycle board, wait several seconds, and then try again.", Toast.LENGTH_SHORT).show();
		}
	};
	
	public void fillConfigurations(ArrayList<String> configList) {
		if(configurations==null)
			configurations = new ArrayList<String>();
		else	
			configurations.clear();
		
		
		for(int i=0;i<configList.size();i++) {
			boolean con = false;
			for(Config.BetaBotType v: config.getAxis()) {
				if(configList.get(i).substring(1).equals(v.name)) {
					if(v.type.equals("float")||v.type.equals("string")||v.type.equals("int")){
						con = true;
						if(!settings.getString(configList.get(i), "").equals("")){
							configurations.add(configList.get(i));
							break;
						}
					} else if(v.type.equals("boolean")) {
						configurations.add(configList.get(i));
						con = true;
						break;
					}
				}
			}
			if(con)continue;
			for(Config.BetaBotType v: config.getMotor()) {
				if(configList.get(i).substring(1).equals(v.name)) {
					if(v.type.equals("float")||v.type.equals("string")||v.type.equals("int")){
						con = true;
						if(!settings.getString(configList.get(i), "").equals("")){
							configurations.add(configList.get(i));
							break;
						}
					} else if(v.type.equals("boolean")) {
						configurations.add(configList.get(i));
						con = true;
						break;
					}
				}
			}if(con)continue;
			for(Config.BetaBotType v: config.getSys()) {
				if(configList.get(0).equals(v.name)) {
					if(v.type.equals("float")||v.type.equals("string")||v.type.equals("int")){
						con = true;
						if(!settings.getString(configList.get(i), "").equals("")){
							configurations.add(configList.get(i));
							break;
						}
					} else if(v.type.equals("boolean")) {
						configurations.add(configList.get(i));
						con = true;
						break;
					}
				}
			}
		}
		
		
	}
	
	public class TryRunner implements Runnable{
		String key=null;
		TryRunner(String kay){
			key = kay;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			tryToLoadSetting(key);
		}
		
	}
	
	public void tryToLoadSetting(String key) {
		MainActivity.key = key;
		
		trying = true;
		boolean con = false;
		for(Config.BetaBotType v: config.getAxis()) {
			if(key.substring(1).equals(v.name)) {
				if(v.type.equals("float")){
					generalSB.setLength(0);
					generalSB.append(settings.getString(key,"777"));
					MainActivity.value = generalSB.toString();
					tryToLoadStringSetting(key,settings.getString(key,"777"));
					con = true;
					break;
				}else if(v.type.equals("boolean")) {
					generalSB.setLength(0);
					generalSB.append(settings.getBoolean(key,false));
					MainActivity.value = generalSB.toString();
					tryToLoadBooleanSetting(key,settings.getBoolean(key,false));
					con = true;
					break;
				}else if(v.type.equals("string")) {
					generalSB.setLength(0);
					generalSB.append(settings.getString(key, "777"));
					MainActivity.value = generalSB.toString();
					tryToLoadStringSetting(key,settings.getString(key, "777"));
					con = true;
					break;
				}else if(v.type.equals("int")) {
					generalSB.setLength(0);
					generalSB.append(settings.getString(key,"777"));
					MainActivity.value = generalSB.toString();
					tryToLoadStringSetting(key,settings.getString(key,"777"));
					con = true;
					break;
				}
			}
		}
		for(Config.BetaBotType v: config.getMotor()) {
			if(con)break;
			if(key.substring(1).equals(v.name)) {
				if(v.type.equals("float")){
					tryToLoadStringSetting(key,settings.getString(key,"888"));
					con = true;
					break;
				}else if(v.type.equals("boolean")) {
					tryToLoadBooleanSetting(key,settings.getBoolean(key,false));
					con = true;
					break;
				}else if(v.type.equals("string")) {
					tryToLoadStringSetting(key,settings.getString(key, "888"));
					con = true;
					break;
				}else if(v.type.equals("int")) {
					tryToLoadStringSetting(key,settings.getString(key,"888"));
					con = true;
					break;
				}
			}
		}
		for(Config.BetaBotType v: config.getSys()) {
			if(con)break;
			if(key.equals(v.name)) {
				if(v.type.equals("float")){
					tryToLoadStringSetting(key,settings.getString(key,"9999"));
					con = true;
					break;
				}else if(v.type.equals("boolean")) {
					tryToLoadBooleanSetting(key,settings.getBoolean(key,false));
					con = true;
					break;
				}else if(v.type.equals("string")) {
					tryToLoadStringSetting(key,settings.getString(key, "999"));
					con = true;
					break;
				}else if(v.type.equals("int")) {
					tryToLoadStringSetting(key,settings.getString(key,"999"));
					con = true;
					break;
				}
			}
		}
	}
	
	public boolean matchSettings(String key, String accion, Bundle b) {
		boolean result=false;
		if(accion.equals(BetaBotService.STATUS)) {
			for(Config.BetaBotType v: config.getSys()) {
				if(key.equals(v.name)) {
					if(v.type.equals("float")){
						float rounders=((float) Math.round(1000*Float.parseFloat(settings.getString(key, "0.0"))))/1000f;
						if(b.getFloat(key)==rounders||b.getFloat(key)==Float.parseFloat(settings.getString(key, "0.0"))){
							result = true;
							break;
						}
					}else if(v.type.equals("boolean")) {
						if(b.getBoolean(key)==settings.getBoolean(key, false)) {
							result = true;
							break;
						}
					}else if(v.type.equals("string")) {
						if(b.getString(key).equals(settings.getString(key, ""))) {
							result = true;
							break;
						}
					}else if(v.type.equals("int")) {
						if(b.getInt(key)==Integer.parseInt(settings.getString(key, "0"))) {
							result = true;
							break;
						}
					}
				}
			}
		}
		
		else if(accion.equals(BetaBotService.AXIS_UPDATE)) {
			for(Config.BetaBotType v: config.getAxis()) {
				if(key.substring(1).equals(v.name)) {
					if(v.type.equals("float")){
						float rounders;
						try{rounders=((float) Math.round(1000*Float.parseFloat(settings.getString(key, "0.0"))))/1000f;
						}catch(NumberFormatException nfe){
							nfe.printStackTrace();
							rounders = 0.0f;
						}
						if(b.getFloat(key)==rounders||b.getFloat(key)==Float.parseFloat(settings.getString(key, "0.0"))) {
							result = true;
							break;
						}
					}else if(v.type.equals("boolean")) {
						if(b.getBoolean(key)==settings.getBoolean(key, false)) {
							result = true;
							break;
						}
					}else if(v.type.equals("string")) {
						if(b.getString(key).equals(settings.getString(key, ""))) {
							result = true;
							break;
						}
					}else if(v.type.equals("int")) {
						if(b.getInt(key)==Integer.parseInt(settings.getString(key, "0"))) {
							result = true;
							break;
						}
					}
				}
			}
		}
		
		else if(accion.equals(BetaBotService.MOTOR_UPDATE)) {
			for(Config.BetaBotType v: config.getMotor()) {
				if(key.substring(1).equals(v.name)) {
					if(v.type.equals("float")){
						float rounders=((float) Math.round(1000*Float.parseFloat(settings.getString(key, "0.0"))))/1000f;
						if(b.getFloat(key)==rounders||b.getFloat(key)==Float.parseFloat(settings.getString(key, "0.0")))
							result = true;
					}else if(v.type.equals("boolean")) {
						if(b.getBoolean(key)==settings.getBoolean(key, false))
							result = true;
					}else if(v.type.equals("string")) {
						if(b.getString(key).equals(settings.getString(key, "")))
							result = true;
					}else if(v.type.equals("int")) {
						if(b.getInt(key)==Integer.parseInt(settings.getString(key, "0")))
							result = true;
					}
				}
			}
		}
		
		return result;
	}
	
	public void tryToLoadIntSetting(String key, int value) {
		String cmd = "";
		try {
			cmd = "{\""+key+"\":"+String.valueOf(value)+"}\n";
		} catch(Exception e) {
			cmd = "{\""+key+"\":}\n";
		}
		sendCommand(cmd);
	}
	public void tryToLoadFloatSetting(String key, float value) {
		String cmd = "";
		try{
			cmd = "{\""+key+"\":"+String.valueOf(value)+"}\n";
		}catch(Exception e) {
			cmd = "{\""+key+"\":}\n";
		}
		sendCommand(cmd);
	}
	public void tryToLoadStringSetting(String key, String value) {
		String cmd = "";
		try {
			cmd = "{\""+key+"\":"+String.valueOf(value)+"}\n";
		} catch(Exception e) {
			cmd = "{\""+key+"\":}\n";
		}
		sendCommand(cmd);
	}
	public void tryToLoadBooleanSetting(String key, boolean value) {
		String cmd = "";
		try {
			cmd = "{\""+key+"\":"+String.valueOf(value)+"}\n";
		} catch(Exception e) {
			cmd = "{\""+key+"\":}\n";
		}
		sendCommand(cmd);
	}
	
	public boolean readConfigs(String configfile) {
		boolean result = false;
		reading_configs = true;
		try {
			
			//**************************************************************************************************************************************
			
			progressBarn = new ProgressDialog(MainActivity.this);
			progressBarn.setCancelable(false);
			progressBarn.setIndeterminate(true);
			progressBarn.setMessage("Reading configs from "+configfile);
			progressBarn.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressBarn.setProgress(0);
			progressBarn.setMax(100);
			progressBarn.show();
			
			new Thread(new Runnable() {
				public void run() {
					while(reading_configs) {
						try {
							Thread.sleep(4000);
						}catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					progressBarn.dismiss();
					
				}
			}).start();
			
			//**************************************************************************************************************************************			
			
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(configfile));
			
			String line = "";
			
			String testStr = null;
			SharedPreferences.Editor Ed = settings.edit();
			Ed.putString("configfilename", configfile);
			Ed.commit();
			while((line = br.readLine())!=null) {
				if(line.startsWith("$") && line.length()>4 && line.contains("=")) {
					if(debug)
						Log.d(TAG,"loadConfigs: "+line);
					
					testStr = line.substring(1);
					testStr.toLowerCase(Locale.ENGLISH);
					String[] pieces = line.split("=");
					String group, word, code;
					if(pieces[0].length()>2&&pieces.length>1) {
						word = pieces[0].substring(1);
						group = word.substring(0,1);
						code = word.substring(1);
					}else
						continue;
					
					if(debug){
						Log.d(TAG,"code="+code);
						Log.d(TAG,"group="+group);
						Log.d(TAG,"word="+word);
						if(pieces.length>1)
							Log.d(TAG,"pieces[1].trim()="+pieces[1].trim());
						else
							Log.d(TAG,"no pieces[1]");
					}
					
					if(
							(	group.equals("1")||
								group.equals("2")||
								group.equals("3")||
								group.equals("4")
									)	) {
						
						if(code.equals("tr")) {
							Ed.putString((group+"tr"), pieces[1].trim());
						} else if(code.equals("sa")) {
							Ed.putString((group+"sa"), pieces[1].trim());
						} else if(code.equals("mi")) {
							Ed.putString((group+"mi"), pieces[1].trim());
						} else if(code.equals("po")) {
							Ed.putBoolean((group+"po"), Boolean.parseBoolean(pieces[1].trim()));
						} else if(code.equals("pm")) {
							Ed.putBoolean((group+"pm"), Boolean.parseBoolean(pieces[1].trim()));
						} else if(code.equals("ma")) {
							Ed.putString((group+"ma"), pieces[1].trim());
						}
						
						Ed.commit();
						
					} else if(
							(	group.equals("x")||
								group.equals("y")||
								group.equals("z")||
								group.equals("a")||
								group.equals("b")||
								group.equals("c")
									)	) {
						
						if(code.equals("tm")) {
							Ed.putString((group+"tm"), pieces[1].trim());
						} else if(code.equals("vm")) {
							Ed.putString((group+"vm"), pieces[1].trim());
						} else if(code.equals("jm")) {
							Ed.putString((group+"jm"), pieces[1].trim());
						} else if(code.equals("jd")) {
							Ed.putString((group+"jd"), pieces[1].trim());
						} else if(code.equals("ra")) {
							Ed.putString((group+"ra"), pieces[1].trim());
						} else if(code.equals("fr")) {
							Ed.putString((group+"fr"), pieces[1].trim());
						} else if(code.equals("am")) {
							Ed.putString((group+"am"), pieces[1].trim());
						} else if(code.equals("sv")) {
							Ed.putString((group+"sv"), pieces[1].trim());
						} else if(code.equals("lv")) {
							Ed.putString((group+"lv"), pieces[1].trim());
						} else if(code.equals("sn")) {
							Ed.putString((group+"sn"), pieces[1].trim());
						} else if(code.equals("sx")) {
							Ed.putString((group+"sm"), pieces[1].trim());
						} else if(code.equals("zb")) {
							Ed.putString((group+"zb"), pieces[1].trim());
						}
						
						Ed.commit();
					} else {
						if(word.equals("hv")) {
							Ed.putString("hv", pieces[1].trim());
						} else if(word.equals("ja")) {
							Ed.putString("ja", pieces[1].trim());
						} else if(word.equals("ct")) {
							Ed.putString("ct", pieces[1].trim());
						} else if(word.equals("st")) {
							Ed.putString("st", pieces[1].trim());
						} else if(word.equals("ej")) {
							Ed.putBoolean("ej", Boolean.parseBoolean(pieces[1].trim()));
						} else if(word.equals("jv")) {
							Ed.putString("jv", pieces[1].trim());
						} else if(word.equals("tv")) {
							Ed.putString("tv", pieces[1].trim());
						} else if(word.equals("qv")) {
							Ed.putString("qv", pieces[1].trim());
						} else if(word.equals("sv")) {
							Ed.putString("sv", pieces[1].trim());
						} else if(word.equals("si")) {
							Ed.putString("si", pieces[1].trim());
						} else if(word.equals("ic")) {
							Ed.putString("ic", pieces[1].trim());
						} else if(word.equals("ec")) {
							Ed.putBoolean("ec", Boolean.parseBoolean(pieces[1].trim()));
						} else if(word.equals("ee")) {
							Ed.putBoolean("ee", Boolean.parseBoolean(pieces[1].trim()));
						} else if(word.equals("iex")) {
							Ed.putString("iex", pieces[1].trim());
						} else if(word.equals("gpl")) {
							Ed.putString("gpl", pieces[1].trim());
						} else if(word.equals("gun")) {
							Ed.putBoolean("gun", Boolean.parseBoolean(pieces[1].trim()));
						} else if(word.equals("gco")) {
							Ed.putString("gco", pieces[1].trim());
						} else if(word.equals("gpa")) {
							Ed.putString("gpa", pieces[1].trim());
						} else if(word.equals("gdi")) {
							Ed.putString("gdi", pieces[1].trim());
						} else if(word.equals("nwa")) {
							Ed.putString("nwa", pieces[1].trim());
						} else if(word.equals("connectionType")) {
							Ed.putString("connectionType", pieces[1].trim());
						} else if(word.equals("baud")) {
							Ed.putString("baud", pieces[1].trim());
						} else if(word.equals("configfilename")){
							//Ed.putString("configfilename", pieces[1].trim());
						} else if(word.equals("nwsal")) {
							Ed.putString("nwsal", pieces[1].trim());
						} else if(word.equals("nwae")) {
							Ed.putString("nwae", pieces[1].trim());
						} else if(word.equals("nwadt")) {
							Ed.putString("nwadt", pieces[1].trim());
						} 
						// HOMING STUFF
						// distances
						  else if(word.equals("nwxh")) {
							Ed.putString("nwxh", pieces[1].trim());
						} else if(word.equals("nwyh")) {
							Ed.putString("nwyh", pieces[1].trim());
						} else if(word.equals("nwzh")) {
							Ed.putString("nwzh", pieces[1].trim());
						} else if(word.equals("nwah")) {
							Ed.putString("nwah", pieces[1].trim());
							aHome = Float.parseFloat(pieces[1].trim());
							Log.d(TAG,"load aHome:"+String.valueOf(aHome));
						} 
						
						  else if(word.equals("nwxsh")) {
							Ed.putString("nwxsh", pieces[1].trim());
						} else if(word.equals("nwysh")) {
							Ed.putString("nwysh", pieces[1].trim());
						} else if(word.equals("nwzsh")) {
							Ed.putString("nwzsh", pieces[1].trim());
						} else if(word.equals("nwash")) {
							Ed.putString("nwash", pieces[1].trim());
							aSendHome = Float.parseFloat(pieces[1].trim());
							Log.d(TAG,"load aSendHome:"+String.valueOf(aSendHome));
						} 
						
						  else if(word.equals("nwxta")) {
							Ed.putString("nwxta", pieces[1].trim());
						} else if(word.equals("nwyta")) {
							Ed.putString("nwyta", pieces[1].trim());
						} else if(word.equals("nwzta")) {
							Ed.putString("nwzta", pieces[1].trim());
						} else if(word.equals("nwata")) {
							Ed.putString("nwata", pieces[1].trim());
							aTapA = Float.parseFloat(pieces[1].trim());
							Log.d(TAG,"load aTapA:"+String.valueOf(aTapA));
						} 
						
						  else if(word.equals("nwxtb")) {
							Ed.putString("nwxtb", pieces[1].trim());
						} else if(word.equals("nwytb")) {
							Ed.putString("nwytb", pieces[1].trim());
						} else if(word.equals("nwztb")) {
							Ed.putString("nwztb", pieces[1].trim());
						} else if(word.equals("nwatb")) {
							Ed.putString("nwatb", pieces[1].trim());
							aTapB = Float.parseFloat(pieces[1].trim());
							Log.d(TAG,"load aTapB:"+String.valueOf(aTapB));
						}
						
						  else if(word.equals("nwxn")) {
							Ed.putString("nwxn", pieces[1].trim());
						} else if(word.equals("nwyn")) {
							Ed.putString("nwyn", pieces[1].trim());
						} else if(word.equals("nwzn")) {
							Ed.putString("nwzn", pieces[1].trim());
						} else if(word.equals("nwan")) {
							Ed.putString("nwan", pieces[1].trim());
							aNudge = Float.parseFloat(pieces[1].trim());
							Log.d(TAG,"load aNudge:"+String.valueOf(aNudge));
						}
						
						// speeds
						  else if(word.equals("nwxhf")) {
							Ed.putString("nwxhf", pieces[1].trim());
						} else if(word.equals("nwyhf")) {
							Ed.putString("nwyhf", pieces[1].trim());
						} else if(word.equals("nwzhf")) {
							Ed.putString("nwzhf", pieces[1].trim());
						} else if(word.equals("nwahf")) {
							Ed.putString("nwahf", pieces[1].trim());
						} 
						
						  else if(word.equals("nwxshf")) {
							Ed.putString("nwxshf", pieces[1].trim());
						} else if(word.equals("nwyshf")) {
							Ed.putString("nwyshf", pieces[1].trim());
						} else if(word.equals("nwzshf")) {
							Ed.putString("nwzshf", pieces[1].trim());
						} else if(word.equals("nwashf")) {
							Ed.putString("nwashf", pieces[1].trim());	
						} 
						
						  else if(word.equals("nwxtaf")) {
							Ed.putString("nwxtaf", pieces[1].trim());
						} else if(word.equals("nwytaf")) {
							Ed.putString("nwytaf", pieces[1].trim());
						} else if(word.equals("nwztaf")) {
							Ed.putString("nwztaf", pieces[1].trim());
						} else if(word.equals("nwataf")) {
							Ed.putString("nwataf", pieces[1].trim());
						} 
						
						  else if(word.equals("nwxtbf")) {
							Ed.putString("nwxtbf", pieces[1].trim());
						} else if(word.equals("nwytbf")) {
							Ed.putString("nwytbf", pieces[1].trim());
						} else if(word.equals("nwztbf")) {
							Ed.putString("nwztbf", pieces[1].trim());
						} else if(word.equals("nwatbf")) {
							Ed.putString("nwatbf", pieces[1].trim());
						}
						
						  else if(word.equals("nwxnf")) {
							Ed.putString("nwxnf", pieces[1].trim());
						} else if(word.equals("nwynf")) {
							Ed.putString("nwynf", pieces[1].trim());
						} else if(word.equals("nwznf")) {
							Ed.putString("nwznf", pieces[1].trim());
						} else if(word.equals("nwanf")) {
							Ed.putString("nwanf", pieces[1].trim());
						}
						
						Ed.commit();
					}
				}
			}
			
			br.close();
		} catch (FileNotFoundException en) {
		    System.out.println(en);
		    Toast.makeText(mContext, "Config file not found, loading factory default settings", Toast.LENGTH_LONG).show();
		    PreferenceManager.setDefaultValues(mContext, R.xml.preference_system, true);
		    PreferenceManager.setDefaultValues(mContext, R.xml.preference_motors, true);
		    PreferenceManager.setDefaultValues(mContext, R.xml.preference_axis, true);
		    PreferenceManager.setDefaultValues(mContext, R.xml.preference_connection, true);
		    
		    SharedPreferences.Editor Eddy = settings.edit();
		    Eddy.putString("configfilename",Environment.getExternalStorageDirectory().getPath()+"/OpenTrons/config.txt");
		    Eddy.commit();
		    saveConfigs(configfile);
		    
		}catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(mContext, "There was a configuration error", Toast.LENGTH_LONG).show();
		}
		reading_configs = false;
		return result;
	}
	
	public boolean saveConfigs(String fileString){
		boolean result = false;
		final String finFileString = fileString;
		SortedSet<String> set = new TreeSet<String>();
		try{
			File fileA = new File(fileString);
			if(fileA.delete()){	}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		try{
			
			
			
			StringBuilder sb = new StringBuilder();
			
			Map<String,?> mp = settings.getAll();
			Iterator<?> it = mp.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry mapEntry = (Map.Entry) it.next();
				String key = (String) mapEntry.getKey();
				if(debug)
					Log.d(TAG,key);
				String littleKey = key.toLowerCase(Locale.ENGLISH);
				if(!littleKey.contains("template")&&!littleKey.contains("mixbook")&&
						!littleKey.equals("rawness")&&!littleKey.equals("locked") &&
						!littleKey.contains("recipe")&&!littleKey.equals("debug") &&
						!littleKey.equals("filename")&&!littleKey.equals("show_warning")
							){
					if(debug)
						Log.d(TAG,"check!");
					
					String valueToPut = String.valueOf(mapEntry.getValue());
					if(valueToPut!=null){
						if(valueToPut.length()!=0) {
							set.add("$"+key+"="+valueToPut+"\n");
							//sb.append("$");
							//sb.append(key);
							//sb.append("=");
							//sb.append(valueToPut);
							//sb.append("\n");
						}
					}
				}
			}
			
			Iterator<String> itty = set.iterator();
			while(itty.hasNext()) {
				Object element = itty.next();
				sb.append(element.toString());
			}
			
			final String savoyString = sb.toString();
			AlertDialog.Builder setHomingBldr = new AlertDialog.Builder(MainActivity.this);
			setHomingBldr.setMessage("Configurations to be saved:\n"+sb.toString())
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("HOMING")
			.setCancelable( false )
			.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int id) {
					finalizeConfigSave(finFileString, savoyString);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
				}
				
			});
			AlertDialog ash = setHomingBldr.create();
			ash.show();
			
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(mContext, "Error occurred while preparing to save", Toast.LENGTH_SHORT).show();
		}
		
		return result;
	}
	
	public void finalizeConfigSave(String fileStr, String tobesaved) {
		try {
			File fileB = new File(fileStr);
			FileOutputStream outputStream;
			PrintWriter writer = new PrintWriter(fileB);
			writer.print("");
			writer.close();
			
			outputStream = new FileOutputStream(fileB, false);
			outputStream.write(tobesaved.getBytes());
			outputStream.close();
		} catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(mContext, "Error occurred while saving", Toast.LENGTH_SHORT).show();
		}
	}
	
}

