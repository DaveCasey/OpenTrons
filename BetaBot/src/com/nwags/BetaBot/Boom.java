package com.nwags.BetaBot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



//import com.nwags.BetaBot.Boom.RowTimes;
import com.nwags.BetaBot.Bluetooth.BluetoothSerialService;

import com.nwags.BetaBot.MainActivity.TemplateXYZ;
import com.nwags.BetaBot.Support.BetaBotService;
import com.nwags.BetaBot.Support.Command;
import com.nwags.BetaBot.Support.Recipe;
import com.nwags.BetaBot.Support.SystemUiHider;
import com.nwags.BetaBot.Support.Template;
import com.nwags.BetaBot.USBHost.USBHostService;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Boom extends Activity implements Runnable{

	private int bindType = 0;
	private BetaBotService BetaBot = null;
	private static BluetoothSerialService BTBetaBot = null;
	private boolean pendingConnect = true;
	private ServiceConnection currentServiceConnection;
	
	SharedPreferences settings;
	private static Context mContext;
	private static boolean debug;
	
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;	
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private static String mConnectedDeviceName = null;
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	
	private static boolean connected=false;
	private static String TAG = "BOOM";
	
	private String rString=null;
	private String tString=null;
	
	private Template template;
	private Recipe recipe;
	
	private TextView column_one;
	private TextView column_two;
	private TextView tvnum;
	private TextView tvrow;
	private TextView tving;
	private TextView tvtim;
	private TextView tvtst;
	private TextView tvsen;
	
	int idx = 0;
	private Handler exHandler = new Handler();
	private final static String BR = System.getProperty("line.separator");
	
	private BroadcastReceiver mRecipeReceiver;
	private ScrollView ctwoScroll;
	
	private ToggleButton pauseBtn;
	private static String Status="Status";
	private static String Message = "";
	
	private final BlockingQueue<String> queuePID = new LinkedBlockingQueue<String>();
	private boolean PAUSED = false;
	
	private boolean gripping = false;
	
	private boolean homingX = false;
	private boolean homingY = false;
	private boolean homingZ = false;
	private boolean homingA = false;
	private boolean homingXYZA = false;
	private boolean homingXY = false;
	private int startseq = 0;
	ProgressDialog progressBar;
	
	private int homingFlag = 0;
	
	private SurfaceView surface;
	private SurfaceHolder holder;
	private boolean locker=true;
	private Thread thread;
	private float[][] CooArray;
	private int[]ColArray;
	
	float[] cumx, cumy, cumz;
	boolean[] firstx, firsty, firstz;
	float CumX, CumY, CumZ, CumA;
	float X, Y, Z, A, pesto, lastX, lastY, lastZ, lastA;
	float[] spotX, spotY, spotZ, spotA;
	String temperature="999.999";
	String temptemp="999.999";
	int eye=0;
	
	private float fullXY;
	private float fullZ;
	private float fullA;
	private float threequartXY;
	private float threequartZ;
	private float threequartA;
	private float halfXY;
	private float halfZ;
	private float halfA;
	private float quartXY;
	private float quartZ;
	private float quartA;
	private float onetenXY;
	private float onetenZ;
	private float onetenA;
	
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerLinear;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	private boolean visualize = false;
	private Handler startHandler = new Handler();
	
	private float xHome=10.0f, xSendHome=-1500.0f, xTapA=-1.0f, xTapB=10.0f, xNudge=10.0f;
	private float yHome=10.0f, ySendHome=-250.0f, yTapA=-1.0f, yTapB=10.0f, yNudge=10.0f;
	private float zHome=4.0f, zSendHome=-200.0f, zTapA=-0.1f, zTapB=10.0f, zNudge=10.0f;
	private float aHome=4.0f, aSendHome=-40.0f, aTapA=-0.5f, aTapB=10.0f, aNudge=5.0f;
	
	private float xHomeF=50.0f, xSendHomeF=10.0f, xTapAF=50.0f, xTapBF=50.0f, xNudgeF=50.0f;
	private float yHomeF=50.0f, ySendHomeF=10.0f, yTapAF=50.0f, yTapBF=50.0f, yNudgeF=50.0f;
	private float zHomeF=50.0f, zSendHomeF=50.0f, zTapAF=50.0f, zTapBF=50.0f, zNudgeF=50.0f;
	private float aHomeF=3.0f, aSendHomeF=3.0f, aTapAF=3.0f, aTapBF=3.0f, aNudgeF=3.0f;
	
	private boolean aGateA=false, aGateB=true;
	private boolean xGateA=false, xGateB=true;
	private boolean yGateA=false, yGateB=true;
	private boolean zGateA=false, zGateB=true;
	
	boolean locked=true;
	
	boolean running=false;
	boolean gogoStart = false;
	boolean gogoEnd = false;
	boolean zend = false;
	
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	
	private Stack<Integer> goRows;
	private Stack<RowTimes> timeRows;
	private Stack<RowTimes> goB;
	private int currentRow;
	private boolean gogoLock = false;
	private boolean cupidLock = false;
	
	private boolean continuous;
	private MenuItem mPause;
	private LinearLayout mainline;
	private boolean trace;
	private boolean sense;
	private int negative;
	private boolean goneg;
	private int criteria;
	private float condition;
	private int boomcounter = 0;
	private boolean firstthree = false;
	private boolean pushtime=false;
	private String lastMessage = "";
	
	private boolean holding=false;
	private boolean endhold=false;
	
	private String pltime="";
	private int logcounter = 1;
	
	private String droptip = "22";
	private String empty = "18";
	private boolean notemp = true;
	private boolean errorx = false;
	private boolean errory = false;
	private boolean errorz = false;
	private boolean errora = false;
	
	private StringBuilder cmdlog;
	private boolean endquick = false;
	private int width;
	private ArrayList<RectF>ArrayRectFs;
	private int cSize;
	private int columns=165;
	private int rows;
	private float coldest;
	private float coeffy=1.0f,consty=0.0f;
	private float stuffy = 40.0f;
	private boolean adjust = false;
	private float tempting;
	private int[] colors;
	private int[] lastcolor; // 0=nothing, 1=sense, 2=trace, 3=viz
	private final double aRa = Math.PI/6.0,
			   bRa = Math.PI/3.0,
			   cRa = 2.0*Math.PI/3.0,
			   dRa = 5*Math.PI/6.0,
			   eRa = -dRa,
			   fRa = -cRa,
			   gRa = -bRa,
			   hRa = -aRa;	
	private boolean alertOnce = true;
	private boolean tempOnce = true;
	private int blinker = 0;
	
	private int conversion_int;
	private float conversion_factor;
	
	private boolean suckDelay = false;
	private boolean gripDelay = false;
	private boolean go_once = false;
	
	PDSendGcode delaySuckGrip;
	
	private StringBuilder homeSB;
	boolean doX=false, doY=false, doZ=false, doA=false, doB=false, doC=false;
	private boolean cancel = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Message = "";
		setResult(RESULT_CANCELED, getIntent());
		setContentView(R.layout.boom);
		
		mContext = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		debug = settings.getBoolean("debug", false);
		
		lastX = lastY = lastZ = lastA = -1.0f;
		mainline = (LinearLayout)findViewById(R.id.main_linear);
		mainline.setKeepScreenOn(true);
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer);
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		cmdlog = new StringBuilder();
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		ArrayRectFs = new ArrayList<RectF>();
		cSize = (int)(((float)(width-40))/(float)columns);
		rows = columns/3;
		Paint paintA = new Paint();
		paintA.setARGB(0, 0, 0, 0);
		int cool = paintA.getColor();
		colors = new int[columns*rows];
		lastcolor = new int[3];
		
		for(int j=0;j<rows;j++){
			for(int i=0;i<columns;i++){
				float fx = i*(float)cSize+stuffy;
				float fy = j*(float)cSize+stuffy;
				colors[j*columns+i] = cool;
				RectF reco = new RectF(fx, fy,fx+cSize, fy+cSize);
				ArrayRectFs.add(reco);
			}
		}
		
		
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
				int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
				TextView yourTextView = (TextView)findViewById(titleId);
				yourTextView.setTextColor(Color.WHITE);
				mainline.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if(getActionBar().isShowing())
							getActionBar().hide();
						else
							getActionBar().show();
						if (TOGGLE_ON_CLICK) {
							mSystemUiHider.toggle();
						} else {
							mSystemUiHider.show();
						}
					}
				});
				
			}
			
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
				mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				mDrawerLinear.requestDisallowInterceptTouchEvent(true);
				mainline.setOnClickListener(null);
				int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
				TextView yourTextView = (TextView)findViewById(titleId);
				yourTextView.setTextColor(Color.BLACK);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		final View contentView = findViewById(R.id.mysurfacev);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;
					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if(visible){
							getActionBar().show();
						}else
							getActionBar().hide();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);	
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}						
					}
				});
		
		
		
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(debug) {
					Toast mcgoat = Toast.makeText(getApplicationContext(), "Surfaceview!", Toast.LENGTH_SHORT);
					mcgoat.setGravity(Gravity.TOP, 0, 0);
					mcgoat.show();
				}
				getActionBar().show();
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
		mainline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(debug) {
					Toast mcgoat = Toast.makeText(getApplicationContext(), "main_linear!", Toast.LENGTH_SHORT);
					mcgoat.setGravity(Gravity.CENTER, 0, 0);
					mcgoat.show();
				}
				if(getActionBar().isShowing())
					getActionBar().hide();
				else
					getActionBar().show();
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		findViewById(R.id.dummy_button).setVisibility(View.GONE);
		
		bindType = Integer.parseInt(settings.getString("connectionType","0"));
		if(settings.getString("locked", "true").equals("true")){
			if(debug)
				Log.d(TAG,"LOCKED?: "+settings.getString("locked", "true"));
			locked = true;
		}else
			locked = false;
		
		
		empty = settings.getString("nwae", "18");
		if(empty.equals(""))
			empty = "18";
		droptip = settings.getString("nwadt", "22");
		if(droptip.equals(""))
			droptip = "22";
		
		if(savedInstanceState!=null)
			restoreState(savedInstanceState);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		template = new Template();
		recipe = new Recipe();
		
		tString = getIntent().getStringExtra("template");
		rString = getIntent().getStringExtra("recipe");
		visualize = getIntent().getBooleanExtra("visualize", true);
		continuous = getIntent().getBooleanExtra("runcontinuously",false);
		
		tvnum = (TextView) findViewById(R.id.cmd_number);
		tving = (TextView) findViewById(R.id.cmd_ingredient);
		tvtim = (TextView) findViewById(R.id.cmd_time);
		tvrow = (TextView) findViewById(R.id.cmd_row);
		tvsen = (TextView) findViewById(R.id.cmd_sensor);
		tvtst = (TextView) findViewById(R.id.cmd_timestamp);
		
		if(!template.Inflate(tString)) {
			Toast.makeText(this, "TEMPLATE MISSING", Toast.LENGTH_SHORT).show();
			finish();
		}
		recipe.Inflate(rString);
		
		currentServiceConnection = new DriverServiceConnection();
		bindDriver(currentServiceConnection);
		
		surface = (SurfaceView) findViewById(R.id.mysurfacev);
		holder = surface.getHolder();
		
		thread = new Thread(this);
		thread.start();
		
		column_one = (TextView) findViewById(R.id.column_one);
		column_two = (TextView) findViewById(R.id.column_two);
		ctwoScroll = (ScrollView) findViewById(R.id.ctwoScroll);
		if(locked)
			ctwoScroll.setVisibility(View.GONE);
		
		fullXY = (Float.valueOf(settings.getString("1vm","10240")) + 
				Float.valueOf(settings.getString("2vm","10240")))/(float)2;
		fullZ = Float.valueOf(settings.getString("3vm","10240"));
		fullA = Float.valueOf(settings.getString("4vm","36000"));
		threequartXY = fullXY*(float)0.75; 
		threequartZ = fullZ*(float)0.75;
		threequartA = fullA*(float)0.75;
		halfXY = fullXY*(float)0.5;
		halfZ = fullZ*(float)0.5;
		halfA = fullA*(float)0.5;
		quartXY = fullXY*(float)0.25;
		quartZ = fullZ*(float)0.25;
		quartA = fullA*(float)0.25;
		onetenXY = fullXY*(float)0.1;
		onetenZ = fullZ*(float)0.1;
		onetenA = fullA*(float)0.1;
		
		CooArray = new float[2][10000];
		ColArray = new int[10000];
		Arrays.fill(CooArray[0], -1.0f);
		Arrays.fill(CooArray[1], -1.0f);
		Arrays.fill(ColArray, R.color.white);
		cumx = new float[recipe.size()];
		cumy = new float[recipe.size()];
		cumz = new float[recipe.size()];
		
		firstx = new boolean[recipe.size()];
		firsty = new boolean[recipe.size()];
		firstz = new boolean[recipe.size()];
		
		spotX = new float[5];
		spotY = new float[5];
		spotZ = new float[5];
		spotA = new float[5];
		
		Arrays.fill(cumx, 0.0f);
		Arrays.fill(cumy, 0.0f);
		Arrays.fill(cumz, 0.0f);
		
		Arrays.fill(firstx, false);
		Arrays.fill(firsty, false);
		Arrays.fill(firstz, false);
		
		goRows = new Stack<Integer>();
		timeRows = new Stack<RowTimes>();
		goB = new Stack<RowTimes>();
		
		adjust = settings.getBoolean("adjust", false);
		coeffy = settings.getFloat("coeff", 1.0f);
		consty = settings.getFloat("const", 0.0f);
		
		lastcolor[0] = 0;
		lastcolor[1] = 0;
		lastcolor[2] = 0;
		
		conversion_int = recipe.get(0).Conversion;
		switch(conversion_int){
		case 0:
			conversion_factor = Float.parseFloat(settings.getString("1ml", "0.017"));
			break;
		case 1:
			conversion_factor = Float.parseFloat(settings.getString("300ul", "0.05533"));
			break;
		case 2:
			conversion_factor = Float.parseFloat(settings.getString("250ul", "0.068"));
			break;
		case 3:
			conversion_factor = Float.parseFloat(settings.getString("200ul", "0.085"));
			break;
		case 4:
			conversion_factor = Float.parseFloat(settings.getString("50ul", "0.34"));
			break;
		case 5:
			conversion_factor = 1f;
			break;
		default:
			conversion_factor = Float.parseFloat(settings.getString("1ml", "0.017"));
			break;
		}
		
		xHome=readFloat(settings.getString("nwxh", "10.0"),10.0f);
		xSendHome=readFloat(settings.getString("nwxsh", "-1500.0"),-1500.0f);
		xTapA=readFloat(settings.getString("nwxta", "-1.0"),-1.0f);
		xTapB=readFloat(settings.getString("nwxtb", "10.0"),10.0f);
		xNudge=readFloat(settings.getString("nwxn", "10.0"),10.0f);
		
		yHome=readFloat(settings.getString("nwyh", "10.0"),10.0f);
		ySendHome=readFloat(settings.getString("nwysh", "-250.0"),-250.0f);
		yTapA=readFloat(settings.getString("nwyta", "-1.0"),-1.0f);
		yTapB=readFloat(settings.getString("nwytb", "10.0"),10.0f);
		yNudge=readFloat(settings.getString("nwyn", "10.0"),10.0f);
		
		zHome=readFloat(settings.getString("nwzh", "4.0"),4.0f);
		zSendHome=readFloat(settings.getString("nwzsh", "-200.0"),-200.0f);
		zTapA=readFloat(settings.getString("nwzta", "-0.1"),-0.1f);
		zTapB=readFloat(settings.getString("nwztb", "10.0"),10.0f);
		zNudge=readFloat(settings.getString("nwzn", "10.0"),10.0f);
		
		aHome=readFloat(settings.getString("nwah", "4.0"),4.0f);
		aSendHome=readFloat(settings.getString("nwash", "-40.0"),-40.0f);
		aTapA=readFloat(settings.getString("nwata", "-0.4"),-0.5f);
		aTapB=readFloat(settings.getString("nwatb", "10.0"),10.0f);
		aNudge=readFloat(settings.getString("nwan", "4.0"),5.0f);
		
		xHomeF=readFloat(settings.getString("nwxhf", "50.0"),50.0f);
		xSendHomeF=readFloat(settings.getString("nwxshf", "10.0"),10.0f);
		xTapAF=readFloat(settings.getString("nwxtaf", "50.0"),50.0f);
		xTapBF=readFloat(settings.getString("nwxtbf", "50.0"),50.0f);
		xNudgeF=readFloat(settings.getString("nwxnf", "50.0"),50.0f);
		
		yHomeF=readFloat(settings.getString("nwyhf", "50.0"),50.0f);
		ySendHomeF=readFloat(settings.getString("nwyshf", "10.0"),10.0f);
		yTapAF=readFloat(settings.getString("nwytaf", "50.0"),50.0f);
		yTapBF=readFloat(settings.getString("nwytbf", "50.0"),50.0f);
		yNudgeF=readFloat(settings.getString("nwynf", "50.0"),50.0f);
		
		zHomeF=readFloat(settings.getString("nwzhf", "50.0"),50.0f);
		zSendHomeF=readFloat(settings.getString("nwzshf", "50.0"),50.0f);
		zTapAF=readFloat(settings.getString("nwztaf", "50.0"),50.0f);
		zTapBF=readFloat(settings.getString("nwztbf", "50.0"),50.0f);
		zNudgeF=readFloat(settings.getString("nwznf", "50.0"),50.0f);
		
		aHomeF=readFloat(settings.getString("nwahf", "3.0"),3.0f);
		aSendHomeF=readFloat(settings.getString("nwashf", "3.0"),3.0f);
		aTapAF=readFloat(settings.getString("nwxataf", "3.0"),3.0f);
		aTapBF=readFloat(settings.getString("nwatbf", "3.0"),3.0f);
		aNudgeF=readFloat(settings.getString("nwanf", "3.0"),3.0f);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.boom, menu);
		mPause = menu.findItem(R.id.pause);
		pauseBtn = (ToggleButton) mPause.getActionView();
		pauseBtn.setTextOff("Running");
		pauseBtn.setTextOn("Paused");
		pauseBtn.setChecked(false);
		
		
		pauseBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		pauseBtn.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View arg0) {
				if(pauseBtn.isChecked()){
					pauseMove();
					pause();
				}
				else{
					resumeMove();
					resume();
				}
			}
		});
		pauseBtn.setOnLongClickListener(new OnLongClickListener(){
			
			@Override
			public boolean onLongClick(View v) {
				stopMove();
				queuePID.clear();
				endquick = true;
				continuous = false;
				homeAll();
				return false;
			}
			
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		return true;
    }
	
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        delayedHide(100);
    }

    /**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }	
	
    Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			//getActionBar().hide();
			mSystemUiHider.hide();
		}
	};
	
    private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	private void restoreState(Bundle inState)
	{
		this.bindType = inState.getInt("bindType");
		Boom.connected = inState.getBoolean("connected");
		if(debug)
			Log.d(TAG,"restoreState() connected state is " + Boom.connected);
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
	
	private boolean bindDriver(ServiceConnection s)
	{
		if(debug)
			Log.d(TAG, "bindDriver(ServiceConnection s)");
		switch(bindType){
		case 0:	// Bluetooth
			ComponentName mBTService = startService(new Intent(this, BluetoothSerialService.class));
			return bindService(new Intent(this, BluetoothSerialService.class)
				, s, Context.BIND_AUTO_CREATE);
		case 1:	//	USB host
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				Toast.makeText(this, R.string.no_usb_accessory,
						Toast.LENGTH_SHORT).show();
				return false;
			}
			ComponentName mUSBHService = startService(new Intent(this, USBHostService.class));
			return bindService(new Intent(getApplicationContext(),
					USBHostService.class), s, Context.BIND_AUTO_CREATE);
		default:
			return false;
		}
	}
	
	private class DriverServiceConnection implements ServiceConnection
	{
		private DriverServiceConnection(){	}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BetaBotService.BetaBotBinder binder = (BetaBotService.BetaBotBinder) service;
			if(debug)
				Log.d(TAG,"Service connected");
			
			if(bindType==0){
				BTBetaBot = (BluetoothSerialService) binder.getService();
				BTBetaBot.setHandler(mHandlerBT);
				
				
				if((mBluetoothAdapter!=null)&&(!mBluetoothAdapter.isEnabled())){
					AlertDialog.Builder builder = new AlertDialog.Builder(Boom.this);
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
					//BTBetaBot.stop();
					//BTBetaBot.start();
				}
				
			} else {
				Boom.this.BetaBot = binder.getService();
			}
			
			if(Boom.this.pendingConnect)
			{
				if(bindType==0){
					BTBetaBot.connect();
					Boom.this.pendingConnect = false;
				}else{
					Boom.this.BetaBot.connect();
					Boom.this.pendingConnect = false;
				}
			}
			
			running = true;
			connected = true;
			
			sendCommand("!%\n");
			sendCommand("{\"sv\":1}\n");
			sendCommand("{\"jv\":5}\n");
			sendGcode("(msg)");
			//sendCommand("$si=50\n");
			
			StartSequence launch = new StartSequence();
			startHandler.postDelayed(launch, 300);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if(debug)
				Log.d(TAG, "Service disconnected");
			Boom.this.BetaBot = null;
			
			if(BTBetaBot!=null)
				BTBetaBot=null;
		}
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
					//mInputManager.showSoftInput(mEmulatorView, InputMethodManager.SHOW_IMPLICIT);

					//mTitle.setText(R.string.title_connected_to);
					//mTitle.append(mConnectedDeviceName);
					break;

				case BluetoothSerialService.STATE_CONNECTING:
					//mTitle.setText(R.string.title_connecting);
					break;

				case BluetoothSerialService.STATE_LISTEN:
				case BluetoothSerialService.STATE_NONE:
					

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
				break;
			case MESSAGE_TOAST:
				Toast.makeText(mContext, msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
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
	
	public int getBluetoothConnectionState(){
		if (BTBetaBot!=null)
			return BTBetaBot.getState();
		else
			return BluetoothSerialService.STATE_NONE;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(debug)
			Log.d(TAG, "onActivityResult " + resultCode + " : requestCode is " + requestCode);
		if(resultCode == Activity.RESULT_OK){
			
			switch(requestCode){
			case REQUEST_CONNECT_DEVICE:

				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
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
			}
		}
	}
	
	public void onDestroy()
	{
		pause();
		if(bindType==0){
			if(Boom.BTBetaBot != null)
			{
				unbindService(this.currentServiceConnection);
				Boom.BTBetaBot = null;
			}
		}else{
			if(this.BetaBot != null)
			{
				unbindService(this.currentServiceConnection);
				this.BetaBot = null;
			}
		}
		
		super.onDestroy();
	}
	
	public void goHome()
	{
		String cmnd = "g28.2";
		
		if(bindType==0){
			if((Boom.BTBetaBot == null))// || (!this.connected))
			{
				return;
			}
			int i = 0;
			while(true)
			{
				if(i>=3)
				{
					if(debug)
						Log.d(TAG,"home command: " + cmnd);
					Boom.BTBetaBot.send_gcode(cmnd);
					return;
				}
				Bundle b = Boom.BTBetaBot.getAxis(i);
				if(b.getInt("am")==1)
					cmnd = cmnd + com.nwags.BetaBot.Support.Machine.axisIndexToName[i] + "0";
				
				i++;
			}
		}else{
			if((Boom.this.BetaBot == null))// || (!this.connected))
			{
				return;
			}
			int i = 0;
			while(true)
			{
				if(i>=3)
				{
					if(debug)
						Log.d(TAG,"home command: " + cmnd);
					Boom.this.BetaBot.send_gcode(cmnd);
					return;
				}
				Bundle b = this.BetaBot.getAxis(i);
				if(b.getInt("am")==1)
					cmnd = cmnd + com.nwags.BetaBot.Support.Machine.axisIndexToName[i] + "0";
				
				i++;
			}
		}
	}
	
	public void pauseMove()
	{
		PAUSED = true;
		Toast.makeText(this, "PAUSED", Toast.LENGTH_LONG).show();
		if(bindType==0){
			if((Boom.BTBetaBot==null))//||(!this.connected))
				return;
			
			Boom.BTBetaBot.send_pause();
		}else{
			if((Boom.this.BetaBot==null))//||(!this.connected))
				return;
			
			Boom.this.BetaBot.send_pause();
		}
	}
	
	public int queueSize()
	{
		if(bindType==0){
			if((Boom.BTBetaBot == null))//||(!this.connected))
				return -1;
			
			return Boom.BTBetaBot.queueSize();
		}else{
			if((this.BetaBot == null))//||(!this.connected))
				return -1;
			
			return Boom.this.BetaBot.queueSize();
		}
	}
	
	public void resumeMove()
	{
		PAUSED = false;
		
		if(bindType==0){
			if((Boom.BTBetaBot == null))//||(!this.connected))
				return;
			
			Boom.BTBetaBot.send_resume();
		}else{
		if((this.BetaBot == null))//||(!this.connected))
			return;
		
		Boom.this.BetaBot.send_resume();
		}
	}
	
	public void sendGcode(String cmd)
	{
		if(bindType==0){
			if((Boom.BTBetaBot == null))//||(!this.connected))
				return;
			
			Boom.BTBetaBot.send_gcode(cmd);
		}else{
			if((this.BetaBot == null))//||(!this.connected))
				return;
			
			Boom.this.BetaBot.send_gcode(cmd);
		}
	}
	
	public void sendReset()
	{
		if(bindType==0){
			if((Boom.BTBetaBot == null))// || (!this.connected))
				return;
			
			Boom.BTBetaBot.send_reset();
		}else{
			if((this.BetaBot == null))// || (!this.connected))
				return;
			
			Boom.this.BetaBot.send_reset();
		}
	}
	
	public void stopMove()
	{
		if(bindType==0){
			if((Boom.BTBetaBot == null))//||(!this.connected))
				return;
			
			Boom.BTBetaBot.send_stop();
		}else{
			if((this.BetaBot == null))//||(!this.connected))
				return;
			
			Boom.this.BetaBot.send_stop();
		}
	}
	
	public void onResume()
	{
		IntentFilter updateFilter = new IntentFilter();
		updateFilter.addAction(BetaBotService.STATUS);
		updateFilter.addAction(BetaBotService.JSON_ERROR);
		updateFilter.addAction(BetaBotService.RAWS);
		mRecipeReceiver = new AlphaBotServiceReceiver();
		registerReceiver(mRecipeReceiver, updateFilter);
		
		super.onResume();
		resume();
	}
	
	private void resume() {
		//RESTART THREAD AND OPEN LOCKER FOR run();
		if(debug)
			Log.d(TAG,"RESUME!");
		locker = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void onPause() {
		unregisterReceiver(mRecipeReceiver);
		super.onPause();
		pause();
	}
	
	private void pause() {
		//CLOSE LOCKER FOR run();
		locker = false;
		if(debug)
			Log.d(TAG,"PAUSED!");
		
		if(!PAUSED){
			while(true){
				try {
					//WAIT UNTIL THREAD DIE, THEN EXIT WHILE LOOP AND RELEASE a thread
					if(thread!=null)
						thread.join();
				} catch (InterruptedException e) {e.printStackTrace();
				}
				break;
			}
			thread = null;
		}
	}

	public class AlphaBotServiceReceiver extends BroadcastReceiver
	{
		boolean onetime = false;
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			String action;
			action = intent.getAction();
			if(action.equals(BetaBotService.JSON_ERROR)){
				
			}
			if(action.equals(BetaBotService.STATUS))
			{
				updateState(b);
				/*if(!Message.equals("")){
					Toast toast = Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					//toast.show();
				}*/
				if(debug){
					Log.d(TAG,"MESSAGE 1: "+Message);
					Log.d(TAG,"homingFlag: "+String.valueOf(homingFlag));
					Log.d(TAG,"Status: "+Status);
				}
				
				
				
				
				
				
				if(	(Message.equals("ALL SYSTEMS GO")			||
						Message.equals("MOVEMENT COMPLETE")		||
						Message.equals("MODALS COMPLETE")		||
						Message.equals("AUTORETURNS COMPLETE")	||
						Message.equals("NEXT")					||
						Message.equals("THE END")				||
						Message.equals("GO TIME")				||
						Message.equals("START SEQUENCE")		||
						Message.equals("SYSTEM READY"))			&&
						(Status.equals("stop")||Status.equals("ready"))				&&
						homingFlag==0)
				{
						if(Message.equals("GO TIME")){
							cupidLock = false;
							if(debug)
								Log.d(TAG,"CupidLock unlocked | Go Time");
						}
						if(Message.equals("ALL SYSTEMS GO")){
							
							if(X==spotX[0]&&Y==spotY[0]&&Z==spotZ[0]&& (Math.abs(pesto-spotA[0])<0.01)){
								cupidLock = false;
								CumZ = 0.0f;
								if(debug)
									Log.d(TAG,"CupidLock unlocked | All Systems Go");
								gogoLock = false;
								if(debug)
									Log.d(TAG, "GOGOLOCK UNLOCKED!");
							}else{
								if(alertOnce){
									alertOnce = false;
									alertLocation(0,spotX[0],spotY[0],spotZ[0],spotA[0],X,Y,Z,pesto);
								}
								
								//Toast.makeText(, "Not connected!", Toast.LENGTH_SHORT).show();
								if(debug){
									Log.d(TAG, "cupidlock BLOCKED! All Systems Go");
									Log.d(TAG,"(X,Y,Z,A):"+String.valueOf(X)+", "+String.valueOf(Y)+", "+String.valueOf(Z)+", "+String.valueOf(pesto));
									Log.d(TAG,"vs spots[0]: "+String.valueOf(spotX[0])+", "+String.valueOf(spotY[0])+", "+String.valueOf(spotZ[0])+", "+String.valueOf(spotA[0]));
								}
							}
						}else if(Message.equals("MOVEMENT COMPLETE")){
							if(X==spotX[1]&&Y==spotY[1]&&Z==spotZ[1]&& (Math.abs(pesto-spotA[1])<0.01)){
								cupidLock = false;
								if(debug)
									Log.d(TAG,"CupidLock unlocked | Movement Complete");
							}else{
								if(alertOnce){
									alertOnce = false;
									alertLocation(1,spotX[1],spotY[1],spotZ[1],spotA[1],X,Y,Z,pesto);
								}
								if(debug){
									Log.d(TAG, "cupidlock BLOCKED! Movement Complete");
									Log.d(TAG,"(X,Y,Z,A):"+String.valueOf(X)+", "+String.valueOf(Y)+", "+String.valueOf(Z)+", "+String.valueOf(pesto));
									Log.d(TAG,"vs spots[1]: "+String.valueOf(spotX[1])+", "+String.valueOf(spotY[1])+", "+String.valueOf(spotZ[1])+", "+String.valueOf(spotA[1]));
								}
							}
						}else if(Message.equals("MODALS COMPLETE")){
							if(X==spotX[2]&&Y==spotY[2]&&Z==spotZ[2]&& (Math.abs(pesto-spotA[2])<0.01)){
								cupidLock = false;
								if(debug)
									Log.d(TAG,"CupidLock unlocked | Modals Complete");
							}else{
								if(alertOnce){
									alertOnce = false;
									alertLocation(2,spotX[2],spotY[2],spotZ[2],spotA[2],X,Y,Z,pesto);
								}
								if(debug){
									Log.d(TAG, "cupidlock BLOCKED! Modals Complete");
									Log.d(TAG,"(X,Y,Z,A):"+String.valueOf(X)+", "+String.valueOf(Y)+", "+String.valueOf(Z)+", "+String.valueOf(pesto));
									Log.d(TAG,"vs spots[2]: "+String.valueOf(spotX[2])+", "+String.valueOf(spotY[2])+", "+String.valueOf(spotZ[2])+", "+String.valueOf(spotA[2]));
								}
							}
						}else if(Message.equals("AUTORETURNS COMPLETE")){
							if(X==spotX[3]&&Y==spotY[3]&&Z==spotZ[3]&& (Math.abs(pesto-spotA[3])<0.01)){
								cupidLock = false;
								if(debug)
									Log.d(TAG,"CupidLock unlocked | Autoreturns");
							}else{
								
								if(alertOnce){
									alertOnce = false;
									alertLocation(3,spotX[3],spotY[3],spotZ[3],spotA[3],X,Y,Z,pesto);
				    	    	}
			    	    		if(debug){
			    	    			Log.d(TAG, "cupidlock BLOCKED! Autoreturns");
									Log.d(TAG,"(X,Y,Z,A):"+String.valueOf(X)+", "+String.valueOf(Y)+", "+String.valueOf(Z)+", "+String.valueOf(pesto));
									Log.d(TAG,"vs spots[3]: "+String.valueOf(spotX[3])+", "+String.valueOf(spotY[3])+", "+String.valueOf(spotZ[3])+", "+String.valueOf(spotA[3]));
			    	    		}
							}
						}else if(Message.equals("NEXT")){
							if(X==spotX[4]&&Y==spotY[4]&&Z==spotZ[4]&& (Math.abs(pesto-spotA[4])<0.01)){
								cupidLock = false;
								if(debug)
									Log.d(TAG,"CupidLock unlocked | Next");
								
								//gogolock unlock?
								gogoLock = false;
								if(debug)
									Log.d(TAG, "GOGOLOCK UNLOCKED!");
								
							}else{
								if(alertOnce){
									alertOnce = false;
									alertLocation(4,spotX[4],spotY[4],spotZ[4],spotA[4],X,Y,Z,pesto);
								}
								if(debug){
									Log.d(TAG, "cupidlock BLOCKED! Next");
									Log.d(TAG,"(X,Y,Z,A):"+String.valueOf(X)+", "+String.valueOf(Y)+", "+String.valueOf(Z)+", "+String.valueOf(pesto));
									Log.d(TAG,"vs spots[4]: "+String.valueOf(spotX[4])+", "+String.valueOf(spotY[4])+", "+String.valueOf(spotZ[4])+", "+String.valueOf(spotA[4]));
								}
							}
						}else if(Message.equals("THE END")){
							cupidLock = false;
							if(debug)
								Log.d(TAG,"CupidLock unlocked | The End");
							gogoLock = false;
							if(debug)
								Log.d(TAG, "GOGOLOCK UNLOCKED!");
						}else if(Message.equals("START SEQUENCE")){
							cupidLock = false;
							if(debug)
								Log.d(TAG,"CupidLock unlocked | Start");
							gogoLock = false;
							if(debug)
								Log.d(TAG, "GOGOLOCK UNLOCKED!");
						}else if(Message.equals("SYSTEM READY")){
							cupidLock = false;
							if(debug)
								Log.d(TAG,"CupidLock unlocked | Ready");
							gogoLock = false;
							if(debug)
								Log.d(TAG, "GOGOLOCK UNLOCKED!");
						}
						
						
						if(!cupidLock){
							if(debug)
								Log.d(TAG, "queuePID size: "+String.valueOf(queuePID.size()));
							cupidLock = true;
							if(debug)
								Log.d(TAG, "cupidLock locked");
							if(Message.equals("NEXT")||Message.equals("ALL SYSTEMS GO")||Message.equals("THE END")||
									Message.equals("START SEQUENCE")||Message.equals("SYSTEM READY")){
								
							}else{
								Message = "";
							}
							try {
								if(queuePID.size()>0){
									//sendGcode("(msgNWAGS)");
										
									String magazine = queuePID.take();
									String pipe = "\n";
									String[] rounds = magazine.split(pipe);
									StringBuilder sb = new StringBuilder();
									
									if(debug)
										Log.d(TAG,""+rounds[0]);
									
									for(int i=0;i<rounds.length;i++){
										sb.append(rounds[i]);
										sb.append("\n");
									}
									
									FireAway m60 = new FireAway(sb.toString());
									while(true){
										if(PAUSED==false)
											break;
									}
									
									exHandler.post(m60);
								}else{ 		// queuePID size > 0 END
									if(!gogoLock){
										gogoLock=true;
										if(debug){
											Log.d(TAG, "GOGOLOCK LOCKED!");
											Log.d(TAG, "QPID");
										}
										if(Message.equals("NEXT")||Message.equals("ALL SYSTEMS GO")||endhold){
											if(!endhold){
												boomcounter++;
												if(debug)
													Log.d(TAG, "QPID->"+Message);
												Message = "";
												if(debug){
													Log.d(TAG, "BOOMCOUNT: "+String.valueOf(boomcounter));
													Log.d(TAG, "currentRow: "+String.valueOf(currentRow));
												}
												if(!goB.isEmpty()){
													if(debug){
														Log.d(TAG, "goB.count(rowb):"+String.valueOf(goB.peek().count()));
														Log.d(TAG, "goB.times(currentRow):"+String.valueOf(goB.peek().times()));
													}
												}else{
													if(debug)
														Log.d(TAG,"goB is empty!");
												}
												if(debug){
													Log.d(TAG, "timeRows.count:"+String.valueOf(timeRows.peek().count()));
													Log.d(TAG, "timeRows.times:"+String.valueOf(timeRows.peek().times()));
													Log.d(TAG, "timeRows Size: "+String.valueOf(timeRows.size()));
													Log.d(TAG, "goRows:"+String.valueOf(goRows.peek()));
													Log.d(TAG, "goRows Size: "+String.valueOf(goRows.size()));
												}
												Message = "";
												Float temper = Float.valueOf(temperature);
												
												Log.d(TAG,"Criteria: "+String.valueOf(criteria));
												switch(criteria){
												case 0:
													goneg = false;
													break;
												case 1:
													if(!(temper>condition)){
														goneg = true;
													}
													break;
												case 2:
													if(!(temper<condition)){
														goneg = true;
													}
													break;
												default:
													goneg = false;
													break;
												}
												if(debug){
													Log.d(TAG,"goneg?: "+String.valueOf(goneg));
													Log.d(TAG,"negative: "+String.valueOf(negative));
												}
												if(goneg){
													if(negative==-3){
														goneg = false;
													}else if(negative==-2){
														//	HOLDING
														holding = true;
														SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
														String format = s.format(new Date());
														cmdlog.insert(0, String.valueOf(logcounter++)+" : "+
																format+" : "+
																String.valueOf(currentRow)+" -> HOLD\n");
														column_one.setText(cmdlog.toString());
																
														Log.d(TAG, "HOLDING!");
													}else if(negative==-1){
														SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
														String format = s.format(new Date());
														
														cmdlog.insert(0,String.valueOf(logcounter++)+" : "+
																format+" : "+
																String.valueOf(currentRow)+" -> HOMING\n");
														column_one.setText(cmdlog.toString());
														Message = "nwags";
														homeAll();
														if(debug)
															Log.d(TAG, "HOMING!");
													}else if(negative==0){
														SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
														String format = s.format(new Date());
														
														cmdlog.insert(0,String.valueOf(logcounter++)+" : "+
																format+" : "+
																String.valueOf(currentRow)+" -> END\n");
														column_one.setText(cmdlog.toString());
														EndSequence endSequence = new EndSequence(continuous);
														exHandler.post(endSequence);
													}else{
														pushtime=true;
														int tis = recipe.get(negative-1).Times;
														if(tis<1)
															tis=1;
														RowTimes rtss = new RowTimes(tis);
														if(debug)
															Log.d(TAG,"timeRows.push: "+String.valueOf(tis));
														timeRows.push(rtss);
														
														SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
														String format = s.format(new Date());
														switch(criteria){
														case 1:
															
															cmdlog.insert(0,String.valueOf(logcounter++)+" : "+
																	format+" : "+
																	String.valueOf(currentRow)+" -> "+
														    recipe.get(negative-1).NumberS+" ("+
															String.valueOf(temper)+"<="+String.valueOf(condition)+")\n");
															column_one.setText(cmdlog.toString());
															break;
														case 2:
															
															cmdlog.insert(0,String.valueOf(logcounter++)+" : "+
																	format+" : "+
																	String.valueOf(currentRow)+" -> "+
																    recipe.get(negative-1).NumberS+" ("+
																	String.valueOf(temper)+">="+String.valueOf(condition)+")\n");
															column_one.setText(cmdlog.toString());
															break;
														}
														
														pltime = String.valueOf(timeRows.peek().count());
														OneAtATime ona;
														ona = new OneAtATime(recipe.get(negative-1),true);
														exHandler.post(ona);
														pushtime=false;
													}
												}
											}
											if(!goneg||endhold){
												if(endhold)
													endhold=false;
												
												if(timeRows.peek().count()==timeRows.peek().times()){
													timeRows.pop();
													if(debug){
														Log.d(TAG, "POP timeRows!...");
														Log.d(TAG,"TimeRows isEmpty?: "+String.valueOf(timeRows.isEmpty()));
														Log.d(TAG,"TimeRows How many?: "+String.valueOf(timeRows.size()));
														Log.d(TAG,"goB isEmpty?: "+String.valueOf(goB.isEmpty()));
														Log.d(TAG,"goB How many?: "+String.valueOf(goB.size()));
													}
													if(goB.isEmpty()){
														currentRow++;
														pushtime = true;
														if(currentRow==3){
															if(firstthree==false){
																firstthree=true;
																if(debug)
																	Log.d(TAG,"FIRST THREE! **********************************************************************************************************************************************");
															}
														}
													}else{
														if(debug)
															Log.d(TAG, "currentRow vs goB(times,count): "+String.valueOf(currentRow)+" vs "+
																String.valueOf(goB.peek().times())+
																","+String.valueOf(String.valueOf(goB.peek().count())));
														if(currentRow==goB.peek().count()){
															currentRow=goB.pop().times();
															if(debug)
																Log.d(TAG,"Pop currentRow: "+String.valueOf(currentRow));
														}else{													
															currentRow++;
															pushtime = true;
															if(debug)
																Log.d(TAG,"++ currentRow: "+String.valueOf(currentRow));
														}
													}
													if(timeRows.isEmpty()){
														if(currentRow>recipe.size()){	}else{	}
													}else{
														
													}	
												}else{
													timeRows.peek().increment();
													if(debug)
														Log.d(TAG,"INCREMENT times,count: "+String.valueOf(timeRows.peek().times()+
															","+String.valueOf(timeRows.peek().count())));
												}
												
												if(currentRow>recipe.size()){
													EndSequence endSequence = new EndSequence(continuous);
													exHandler.post(endSequence);
												}else{
													if(debug){
														Log.d(TAG,"JUST BEFORE ONA");
														Log.d(TAG, "currentRow: "+String.valueOf(currentRow));
													}
													if(!goB.isEmpty()){
														if(debug){
															Log.d(TAG, "goB.count(rowb):"+String.valueOf(goB.peek().count()));
															Log.d(TAG, "goB.times(currentRow):"+String.valueOf(goB.peek().times()));
														}
													}else{
														if(debug)
															Log.d(TAG,"goB is empty!");
													}
													if(!timeRows.isEmpty()){
														if(debug){
															Log.d(TAG, "timeRows.count:"+String.valueOf(timeRows.peek().count()));
															Log.d(TAG, "timeRows.times:"+String.valueOf(timeRows.peek().times()));
															Log.d(TAG, "timeRows Size: "+String.valueOf(timeRows.size()));
														}
													}else{
														if(debug)
															Log.d(TAG,"timeRows is empty!");
													}
													if(debug){
														Log.d(TAG, "goRows:"+String.valueOf(goRows.peek()));
														Log.d(TAG, "goRows Size: "+String.valueOf(goRows.size()));
													}
													OneAtATime ona;
													
													if(currentRow==goRows.peek()){
														if(debug)
															Log.d(TAG,"goRows a poppin!");
														if(pushtime){
															int tis = recipe.get(goRows.peek()-1).Times;
															if(tis<1)
																tis=1;
															RowTimes rtss = new RowTimes(tis);
															if(debug)
																Log.d(TAG,"timeRows.push: "+String.valueOf(tis));
															timeRows.push(rtss);
															pushtime=false;
														}
														ona = new OneAtATime(recipe.get(goRows.pop()-1),false);
													}else{
														if(pushtime){
															int tis = recipe.get(currentRow-1).Times;
															if(tis<1)
																tis=1;
															RowTimes rtss = new RowTimes(tis);
															if(debug)
																Log.d(TAG,"timeRows.push: "+String.valueOf(tis));
															timeRows.push(rtss);
															pushtime=false;
														}
														ona = new OneAtATime(recipe.get(currentRow-1),true);
													}
													pltime = String.valueOf(timeRows.peek().count());
													exHandler.post(ona);
												}
											}
											Message = "";
										}else if(Message.equals("THE END")) {
											sendGcode("(msgnwags)");
											Message = "";
											if(debug)
												Log.d(TAG, "THE END");
											gogoEnd = false;
											zend = true;
											if(debug)
												Log.d(TAG, "Visualize?: "+String.valueOf(visualize));
											if(visualize){
												sendCommand("{\"xam\":1}\n");
												sendCommand("{\"yam\":1}\n");
												sendCommand("{\"zam\":1}\n");
												sendCommand("{\"aam\":3}\n");
											}
											
											if(debug)
												Log.d(TAG,String.valueOf(continuous));
											if(continuous){
												
												if(suckDelay){
													progressBar = new ProgressDialog(Boom.this);
													progressBar.setCancelable(true);
													progressBar.setIndeterminate(true);
													progressBar.setMessage("waiting for suction to complete...");
													progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
													progressBar.setProgress(0);
													progressBar.setMax(100);
													progressBar.setButton(DialogInterface.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog,
																int id) {
															
															sendGcode("M19");
															exHandler.removeCallbacks(delaySuckGrip);
															suckDelay = false;
															progressBar.dismiss();
														}
													});
													progressBar.show();
													new Thread(new Runnable(){
														public void run() {
															while(suckDelay){
																try{
																	Thread.sleep(500);
																}catch(InterruptedException e){
																	e.printStackTrace();
																}
															}
															progressBar.dismiss();
														}
													}).start();
												}
												if(gripDelay){
													progressBar = new ProgressDialog(Boom.this);
													progressBar.setCancelable(true);
													progressBar.setIndeterminate(true);
													progressBar.setMessage("waiting for gripping to complete...");
													progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
													progressBar.setProgress(0);
													progressBar.setMax(100);
													progressBar.setButton(DialogInterface.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog,
																int id) {
															
															sendGcode("M21");
															exHandler.removeCallbacks(delaySuckGrip);
															gripDelay = false;
															progressBar.dismiss();
														}
													});
													progressBar.show();
													new Thread(new Runnable(){
														public void run() {
															while(suckDelay){
																try{
																	Thread.sleep(500);
																}catch(InterruptedException e){
																	e.printStackTrace();
																}
															}
															progressBar.dismiss();
														}
													}).start();
												}
												new Thread(new Runnable(){
													public void run(){
														while(suckDelay||gripDelay){
															try{
																Thread.sleep(1000);
																if(debug)
																	Log.d(TAG, "suckDelay: "+String.valueOf(suckDelay)+" , gripDelay: "+String.valueOf(gripDelay));
															} catch(InterruptedException e) {
																e.printStackTrace();
															}
														}
														if(debug)
															Log.d(TAG, "FINALLY... suckDelay: "+String.valueOf(suckDelay)+" , gripDelay: "+String.valueOf(gripDelay));
														StartSequence launch = new StartSequence();
														startHandler.postDelayed(launch, 1000);
													}
													
												}).start();
												
											}else{
												if(debug)
													Log.d(TAG,"DELAYED PAUSE...");
												DelayedPause dp = new DelayedPause();
												exHandler.postDelayed(dp, 500);
												running=false;
											}
											if(endquick){
												if(debug)
													Log.d(TAG,"finish!");
												running = false;
												DelayedFinish df =  new DelayedFinish();
												exHandler.postDelayed(df, 1500);
											}
										}else if(Message.equals("START SEQUENCE")){
											Message = "";
											if(Float.parseFloat(temptemp)>-55.0f&&Float.parseFloat(temptemp)<999.0f){
												notemp = false;
											}else{
												notemp = true;
											}
											
											sendReset();
												
										}else if(Message.equals("SYSTEM READY")){
											Message = "";
											FinalStartup finalstartup = new FinalStartup(notemp);
											exHandler.postDelayed(finalstartup,2000);
											
										}else{
											if(debug)
												Log.d(TAG,"MESSAGE 3: "+Message);
										}
									}else{
										if(debug)
											Log.d(TAG, "GOGOLOCK BLOCKED!");
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						
				}else{
					if(Message.equals("HOME")&&homingFlag==0){
						Message = "nwags";
						if(debug)
							Log.d(TAG,"MESSAGE 2: "+Message);
						homeAll();
					}
					
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
									sendCommand("!%\n");
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
									sendCommand("!%\n");
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
									sendCommand("!%\n");
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
							Float rosa = Float.valueOf(settings.getString("ara", "0.3183099"));
							Float pasta = posa*((float)(Math.PI*2.0)*rosa/(float)360);
							if(debug){
								Log.d(TAG, "pasta: "+String.valueOf(pasta));
								Log.d(TAG, "aHome: "+String.valueOf(aHome));
							}
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
							Float rosa = Float.valueOf(settings.getString("ara", "0.3183099"));
							Float pasta = posa*((float)(Math.PI*2.0)*rosa/(float)360);
							if(pasta<=aSendHome+(float)5){
								if(!aGateB){
									aGateB = true;
									if(debug)
										Log.d(TAG,"homingFlag 16 -> imHome(a)");
									sendCommand("!%\n");
									imHome("a");
								}
							}
						}
					}else if(homingFlag==19){
						if(b.containsKey("posa")){
							Float posa = b.getFloat("posa");
							Float rosa = Float.valueOf(settings.getString("ara", "0.3183099"));
							Float pasta = posa*((float)(Math.PI*2.0)*rosa/(float)360);
							if((pasta>aNudge-0.04f)&&pasta<aNudge+0.04f){
								if(!aGateB){
									aGateB = true;
									imHome("a");
								}
							}
						}
					}
				}
				
			}
			if(action.equals(BetaBotService.RAWS))
			{
				String rawStr = b.getString("rawness");
				
				if(rawStr.contains("ZMIN")){
					if(homingFlag==1){
						zGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("z");
						exHandler.postDelayed(dsh,500);
					}else if(homingFlag==2){
						zGateB = false;
						sendCommand("%\n");
						tap("z");
					}else if(homingFlag==3){
						zGateB = false;
						sendCommand("%\n");
						nudge("z");
					}else if(homingFlag==0){
						Message = "nwags";
						queuePID.clear();
						homeAll();
						errorz = true;
					}
				}
				
				if(rawStr.contains("YMIN")){
					if(homingFlag==6){
						yGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("y");
						exHandler.postDelayed(dsh,500);
					}else if(homingFlag==7){
						yGateB = false;
						sendCommand("%\n");
						tap("y");
					}else if(homingFlag==8){
						yGateB = false;
						sendCommand("%\n");
						nudge("y");
					}else if(homingFlag==0){
						Message = "nwags";
						queuePID.clear();
						homeAll();
						errory = true;
					}
				}
				
				if(rawStr.contains("XMIN")){
					if(homingFlag==11){
						xGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("x");
						exHandler.postDelayed(dsh,500);
					}else if(homingFlag==12){
						xGateB = false;
						sendCommand("%\n");
						tap("x");
					}else if(homingFlag==13){
						xGateB = false;
						sendCommand("%\n");
						nudge("x");
					}else if(homingFlag==0){
						Message = "nwags";
						queuePID.clear();
						homeAll();
						errorx = true;
					}
				}
				
				if(rawStr.contains("AMIN")){
					if(debug)
						Log.d(TAG,"homingFlag: "+String.valueOf(homingFlag));
					if(homingFlag==16){
						aGateB = false;
						sendCommand("%\n");
						DelayedSendHome dsh = new DelayedSendHome("a");
						exHandler.postDelayed(dsh,500);
						sendHome("a");
					}else if(homingFlag==17){
						aGateB = false;
						sendCommand("%\n");
						tap("a");
					}else if(homingFlag==18){
						aGateB = false;
						sendCommand("%\n");
						nudge("a");
					}else if(homingFlag==0){
						Message = "nwags";
						queuePID.clear();
						homeAll();
						errora = true;
					}
				}
				rawStr = "";
				
				StringBuilder sb = new StringBuilder();
				if(b.getString("rawness").contains("temp")){
					
				}else{
					sb.append(b.getString("rawness"));
					
					sb.append(BR);
					if(column_two.getText().toString().length()>10000)
					{
						StringBuilder sb2 = new StringBuilder();
						sb2.append(column_two.getText().toString());
						sb2.delete(0, 2000);
						column_two.setText(sb2.toString());
					}
					column_two.append(sb.toString());
				}
				ctwoScroll.fullScroll(View.FOCUS_DOWN);
				
			}
		}
	}
	
	public void updateState(Bundle b)
	{
		if(b.containsKey("status")){
			Status = b.getString("status");
		}
		if(b.containsKey("msg")){
			if(!lastMessage.equals(b.getString("msg"))){
				Message = b.getString("msg");
				lastMessage = Message;
			}
		}else{	}
		try{
			X = b.getFloat("posx");
			Y = b.getFloat("posy");
			Z = b.getFloat("posz");
			A = b.getFloat("posa");
			Float rosa = Float.valueOf(settings.getString("ara", "0.3183099"));
			if(visualize)
				pesto = A;
			else
				pesto = A*((float)(Math.PI*2.0)*rosa/(float)360);
			
			if(X!=lastX||Y!=lastY||Z!=lastZ||A!=lastA){
				if(debug){
					Log.d(TAG,"X: "+String.valueOf(X));
					Log.d(TAG,"Y: "+String.valueOf(Y));
					Log.d(TAG,"Z: "+String.valueOf(Z));
					Log.d(TAG,"A: "+String.valueOf(A));
					Log.d(TAG,"pesto, rosa: "+String.valueOf(pesto)+", "+String.valueOf(rosa));
				}
				lastX = X;
				lastY = Y;
				lastZ = Z;
				lastA = A;
			}
		}catch(Exception ex2){
			X = -1.0f;
			Y = -1.0f;
		}
		try{
			temptemp = b.getString("temp");
			if(debug){
				Log.d(TAG, "temperature: "+temptemp);
				Log.d(TAG, "Adjusting?: "+String.valueOf(adjust));
			}
			if(adjust){
				tempting = Float.parseFloat(temptemp)*coeffy+consty;
			}else{
				tempting = Float.parseFloat(temptemp);
			}
			temperature = String.valueOf(tempting);
			if(debug)
				Log.d(TAG, "new temperature: "+temperature);
			
			if(holding){
				try{
					
					switch(criteria){
					case 1:
						if(tempting>condition){
							endhold = true;
							Message = "NEXT";
							holding = false;
						}
						break;
					case 2:
						if(tempting<condition){
							endhold = true;
							Message = "NEXT";
							holding = false;
						}
						break;
					default:
						break;
					}
				} catch(Exception ex1){	}
			}
			
		}catch(Exception ex3){
			temperature = "999.999";
			
		}finally{
			if(Float.parseFloat(temperature)<-50.0f){
				
			}
		}
	}
	
	public class FireAway implements Runnable{
		String chamber;
		
		FireAway(String round){
			chamber = round;
		}
		
		@Override
		public void run() {
			String[] fire = chamber.split("\n");
			for(int i=0; i<fire.length; i++){
				String thr = fire[i].substring(0, 3);
				if(thr.equals("cmd")){
					sendCommand(fire[i].substring(3)+"\n");
					if(debug)
						Log.d(TAG,"substring: "+ fire[i].substring(3));
				}else if(thr.equals("num")){
					String numero = fire[i].substring(3);
					tvrow.setText(numero);
					tvsen.setText(temperature);
					if(adjust)
						tvsen.append(" *");
					
					SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String format = s.format(new Date());
					tvtst.setText(format);
					tvtim.setText(pltime);
					if(numero.equals("-1")){	}
				}else if(thr.equals("nic")){
					tvnum.setText(fire[i].substring(3));
				}else if(thr.equals("ing")){
					String ingrid = fire[i].substring(3);
					tving.setText(ingrid);
				}else if(thr.equals("sen")){
					sense = true;
				}else if(thr.equals("seb")){
					sense = false;
				}else if(thr.equals("cri")){
					criteria = Integer.valueOf(fire[i].substring(3));
				}else if(thr.equals("pla")){
					String loco = fire[i].substring(3);
					//tvpla.setText(String.valueOf(loco));
				}else if(thr.equals("stm")){
					float tsecs = Float.parseFloat(fire[i].substring(3));
					String tStr = String.format("%.3f", tsecs);
					tsecs = Float.parseFloat(tStr)*1000f;
					long dFuze = (long)tsecs;
					launchDelay(0,dFuze);
					if(debug)
						Log.d(TAG, "launched 0");
				}else if(thr.equals("gtm")){
					float tsecs = Float.parseFloat(fire[i].substring(3));
					String tStr = String.format("%.3f", tsecs);
					tsecs = Float.parseFloat(tStr)*1000f;
					long dFuze = (long)tsecs;
					launchDelay(1,dFuze);
					if(debug)
						Log.d(TAG, "launched 1");
				}else{
					sendGcode(fire[i]);
				}
				if(debug)
					Log.d(TAG,"done firing this line");
			}
			if(debug)
				Log.d(TAG,"done with clip");
		}
		
	}
	
	public void sendCommand(String cmd) {
		if(bindType==0){
			if((Boom.BTBetaBot == null))
				return;
			
			Boom.BTBetaBot.send_command(cmd);
		}else{
			if((this.BetaBot == null))
				return;
			
			this.BetaBot.send_command(cmd);
		}
	}
	
	public void homeX() {
		if(Boom.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingX = true;
				homingProgress("x");
			}
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
			sendGcode("g91 g1 f"+String.valueOf(fullXY/xHomeF)+" x"+String.valueOf(xHome));
		}
	}
	
	public void homeY() {
		if(Boom.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingY = true;
				homingProgress("y");
			}
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
			sendGcode("g91 g1 f"+String.valueOf(fullXY/yHomeF)+" y"+String.valueOf(yHome));
		}
	}
	
	public void homeZ() {
		if(Boom.connected){
			if(homingXYZA||homingXY){
				
			}else{
				homingZ = true;
				homingProgress("z");
			}
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
		}
	}
	
	public void homeA() {
		if(Boom.connected){
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
		}
	}
	
	public void sendHome(String axis){
		Handler xHandler = new Handler();
		if(axis.equals("x")){
			homingFlag = 12;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullXY/xSendHomeF)+" x"+String.valueOf(xSendHome));
			xHandler.post(sgc);
		}else if(axis.equals("y")){
			homingFlag = 7;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullXY/ySendHomeF)+" y"+String.valueOf(ySendHome));
			xHandler.post(sgc);
		}else if(axis.equals("z")){
			homingFlag = 2;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullZ/zSendHomeF)+" z"+String.valueOf(zSendHome));
			xHandler.post(sgc);
		}else if(axis.equals("a")){
			homingFlag = 17;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullA/aSendHomeF)+" a"+String.valueOf(aSendHome));
			xHandler.post(sgc);
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
		Handler xHandler = new Handler();
		if(axis.equals("x")){
			homingFlag = 13;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullXY/xTapAF)+" x"+String.valueOf(xTapA));
			xHandler.postDelayed(sgc,1000);
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullXY/xTapBF)+" x"+String.valueOf(xTapB));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("y")){
			homingFlag = 8;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullXY/yTapAF)+" y"+String.valueOf(yTapA));
			xHandler.postDelayed(sgc,1000);
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullXY/yTapBF)+" y"+String.valueOf(yTapB));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("z")){
			homingFlag = 3;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullZ/zTapAF)+" z"+String.valueOf(zTapA));
			xHandler.postDelayed(sgc,1000);
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullZ/zTapBF)+" z"+String.valueOf(zTapB));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("a")){
			homingFlag = 18;
			SendGcodePST sgc = new SendGcodePST("g91g1f"+String.valueOf(fullA/aTapAF)+" a"+String.valueOf(aTapA));
			xHandler.postDelayed(sgc,1000);
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullA/aTapBF)+" a"+String.valueOf(aTapB));
			xHandler.postDelayed(sgc,2000);
		}
	}
	
	public void nudge(String axis){
		Handler xHandler = new Handler();
		if(axis.equals("x")){
			SendGcodePST sgc = new SendGcodePST("g28.3x0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 14;
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullXY/xNudgeF)+" x"+String.valueOf(xNudge));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("y")){
			SendGcodePST sgc = new SendGcodePST("g28.3y0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 9;
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullXY/yNudgeF)+" y"+String.valueOf(yNudge));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("z")){
			SendGcodePST sgc = new SendGcodePST("g28.3z0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 4;
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullZ/zNudgeF)+" z"+String.valueOf(zNudge));
			xHandler.postDelayed(sgc,2000);
		}else if(axis.equals("a")){
			SendGcodePST sgc = new SendGcodePST("g28.3a0");
			xHandler.postDelayed(sgc,1100);
			homingFlag = 19;
			sgc = new SendGcodePST("g91f1f"+String.valueOf(fullA/aNudgeF)+" a"+String.valueOf(aNudge));
			xHandler.postDelayed(sgc,2000);
		}
	}
	
	public void imHome(String axis){
		if(axis.equals("x")){
			homingX = false;
			xGateA = false;
			SendGcodePST hx = new SendGcodePST("g28.3x0");
			exHandler.postDelayed(hx, 300);
		}else if(axis.equals("y")){
			homingY = false;
			yGateA = false;
			SendGcodePST hy = new SendGcodePST("g28.3y0");
			exHandler.postDelayed(hy, 300);
		}else if(axis.equals("z")){
			homingZ = false;
			zGateA = false;
			SendGcodePST hz = new SendGcodePST("g28.3z0");
			exHandler.postDelayed(hz, 300);
		}else if(axis.equals("a")){
			homingA = false;
			aGateA = false;
			SendGcodePST ha = new SendGcodePST("g28.3a0");
			exHandler.postDelayed(ha, 1300);
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
				//homeA();
			}
		}
	}
	
	@Override
	public void run() {
		while(locker){
			//checks if the lockCanvas() method will be success,and if not, will check this statement again
			if(!holder.getSurface().isValid()){
				continue;
			}
			/** Start editing pixels in this surface.*/
			Canvas canvas = holder.lockCanvas();
			
			//ALL PAINT-JOB MAKE IN draw(canvas); method.
			draw(canvas);
			if(X>=0&&Y>=0)
				drawTemp(canvas,X,Y,temperature);
			else
				drawTemp(canvas,0.0f,0.0f,temperature);
			
			// End of painting to canvas. system will paint with this canvas,to the surface.
			holder.unlockCanvasAndPost(canvas);
		}
	}
	
	private void draw(Canvas canvas) {
		canvas.drawColor(android.R.color.holo_blue_bright);
		
		int border = 20;
		RectF r = new RectF(border, border, canvas.getWidth()-20, canvas.getHeight()-20);
		Paint paint = new Paint();		
		paint.setARGB(200, 135, 135, 135); //paint color GRAY+SEMY TRANSPARENT 
		canvas.drawRect(r , paint );
		Bitmap _scratch = BitmapFactory.decodeResource(getResources(),
				R.drawable.open_trons);
		Bitmap bmp = Bitmap.createScaledBitmap(_scratch, 120, 120, false);
		canvas.drawBitmap(bmp, (canvas.getWidth()-bmp.getWidth())-20,(canvas.getHeight()-bmp.getHeight())-20, null);
	}
	
	private void drawTemp(Canvas canvas, Float ex, Float why, String temperature){
		Paint paint = new Paint();
		
		float tempe;
		float h = canvas.getHeight();
		
		int sq1 = (int)(ex/5.0);
		if(sq1>=columns)
			sq1 = columns-1;
		int sq2 = (int)(why/5.0);
		if(sq2>=rows)
			sq2 = rows-1;
		
		int square = sq1+sq2*columns;
		
		try{
			tempe = Float.parseFloat(temperature);
		} catch(Exception ex1){
			tempe = 999.999f;
		}
		
		int r=0,g=0,b=255;
		
		if(tempe>998||tempe<-20.0f||tempe>120.0f){
			r = g = 0;
			b = 55;
		}else {
			/*
			if(tempe<-10.0f){
				b = r = (int)(25.5f*(tempe+20.0f));
			}else if(tempe<0.0f){
				b = 255;
				r = (int)(255.0f-(25.5f*(tempe+10.0f)));
			}else if(tempe<10.0f){
				b = 255;
				g = (int)(25.5f*(tempe));
			}else if(tempe<17.5f){
				g = 255;
				b = (int)(255.0f-(34.0f*(tempe-10.0f)));
			}else if(tempe<25.0f){
				g = 255;
				r = (int)(34.0f*(tempe-17.5f));
			}else if(tempe<32.5f){
				r = 255;
				g = (int)(255.0f-(34.0f*(tempe-25.0f)));
			}else if(tempe<40.0f){
				r = 255;
				g = b = (int)(34.0f*(tempe-32.5f));
			}else if(tempe<120.0f){
				r = g = b = 255;
			}		
			*/
			if(tempe<22.0f){
				b = (int)(20f*(tempe+20.0f))+55;
				g = (int)(12.7f*(tempe+20.0f));//12.75f
				r = (int)(6.5f*(tempe+20.0f));//6.0714f
				if(b>255)
					b=255;
				if(g>255)
					g=255;
				if(r>255)
					r=255;
			} else {
				r = g = b = 255;
			}
		}
		
		paint.setARGB(0, 0, 0, 0);
		
		int cooler = 0;
		blinker++;
		if(blinker==8)
			blinker=0;
		
		int bax,bbx,bcx,bdx;
		int bay,bby,bcy,bdy;
		bax = bbx = bcx = bdx = -1;
		bay = bby = bcy = bdy = -1;
		
		if(blinker>0&&blinker<5){
			if(sq1!=0){
				bax = sq1-1;
				bay = sq2;
			}
			if(sq2!=0){
				bbx = sq1;
				bby = sq2-1;
			}
			if(sq1!=columns-1){
				bcx = sq1+1;
				bcy = sq2;
			}
			if(sq2!=rows-1){
				bdx = sq1;
				bdy = sq2+1;
			}
		}
		if(debug)
			Log.d(TAG,"tempe:"+String.valueOf(tempe)+" r: "+r+" g: "+g+" b:"+b);
		Log.d(TAG,"sense: "+String.valueOf(sense));
		
		if(sense) {
			paint.setARGB(255, r, g, b);
			cooler = paint.getColor();
			if(sq1<columns){
				if(sq2<rows){
					colors[square] = cooler;
					if(sq1+1<columns&&sq2+1<rows){
						colors[sq2*columns+sq1+1]=cooler;
						colors[(sq2+1)*columns+sq1+1]=cooler;
						colors[(sq2+1)*columns+sq1]=cooler;
					}else if(sq1<columns&&sq2+1<rows){
						colors[(sq2+1)*columns+sq1]=cooler;
					}else if(sq1+1<columns&&sq2<rows){
						colors[sq2*columns+sq1+1]=cooler;
					}
					
					if(lastcolor[0]==1){
						
						int xpt=lastcolor[1];
						int ypt=lastcolor[2];
						int xdist;
						int ydist;
						double arct;
						
						if(debug){
							Log.d(TAG,"sq1: "+sq1+", sq2: "+sq2+", lstclr[1]: "+lastcolor[1]+
								", lstclr[2]: "+lastcolor[2]);
							Log.d(TAG,"columns: "+columns+", rows: "+rows);
						}
						if(Math.abs(sq1-xpt)<20.0 && Math.abs(sq2-ypt)<20.0){
							
							while(xpt!=sq1||ypt!=sq2){
								xdist = sq1 - xpt;
								ydist = sq2 - ypt;
								arct = Math.atan2(ydist, xdist);
								if(debug)
									Log.d(TAG,"xpt: "+xpt+", ypt: "+ypt+", xdist: "+xdist+", ydist: "+ydist+
										", arctan: "+arct);
								
								if(arct>hRa && arct<aRa){
									// 0 -> right
									xpt+=1;
								}else if(arct>=aRa && arct<=bRa){
									// pi/4 -> up-right
									xpt+=1;
									ypt+=1;
								}else if(arct>bRa && arct<cRa){
									// pi/2 -> up
									ypt+=1;
								}else if(arct>=cRa && arct<=dRa){
									// 3pi/4 -> up-left
									ypt+=1;
									xpt-=1;
								}else if(arct>dRa || arct<eRa){
									// pi -> left
									xpt-=1;
								}else if(arct>=eRa && arct<=fRa){
									// -3pi/4 -> down-left
									xpt-=1;
									ypt-=1;
								}else if(arct>fRa && arct<gRa){
									// -pi/2 -> down
									ypt-=1;
								}else if(arct>gRa && arct<hRa){
									// -pi/4 -> down-right
									xpt+=1;
									ypt-=1;
								}
								colors[ypt*columns+xpt] = cooler;
								if(xpt+1<columns&&ypt+1<rows){
									colors[ypt*columns+xpt+1]=cooler;
									colors[(ypt+1)*columns+xpt+1]=cooler;
									colors[(ypt+1)*columns+xpt]=cooler;
								}else if(xpt<columns&&ypt+1<rows){
									colors[(ypt+1)*columns+xpt]=cooler;
								}else if(xpt+1<columns&&ypt<rows){
									colors[ypt*columns+xpt+1]=cooler;
								}	
							}
						}
					}
					
					lastcolor[0]=1;
					lastcolor[1]=sq1;
					lastcolor[2]=sq2;
				}
			}else if(!(sq2==rows-1)){
			}
		} else {
			Log.d(TAG,"SENSE: "+String.valueOf(sense));
		}
		if(trace){ //else if
			paint.setARGB(255, 0, 0, 0);
			colors[square] = paint.getColor();
			
			if(lastcolor[0]==2){
				
				int xpt=lastcolor[1];
				int ypt=lastcolor[2];
				int xdist;
				int ydist;
				double arct;
				
				if(Math.abs(sq1-xpt)<10&&Math.abs(sq2-ypt)<10){
					while(xpt!=sq1||ypt!=sq2){
						xdist = sq1 - xpt;
						ydist = sq2 - ypt;
						arct = Math.atan2(ydist, xdist);
						if(arct>hRa && arct<aRa){
							// 0 -> right
							xpt+=1;
						}else if(arct>=aRa && arct<=bRa){
							// pi/4 -> up-right
							xpt+=1;
							ypt+=1;
						}else if(arct>bRa && arct<cRa){
							// pi/2 -> up
							ypt+=1;
						}else if(arct>=cRa && arct<=dRa){
							// 3pi/4 -> up-left
							ypt+=1;
							xpt-=1;
						}else if(arct>dRa || arct<eRa){
							// pi -> left
							xpt-=1;
						}else if(arct>=eRa && arct<=fRa){
							// -3pi/4 -> down-left
							xpt-=1;
							ypt-=1;
						}else if(arct>fRa && arct<gRa){
							// -pi/2 -> down
							ypt-=1;
						}else if(arct>gRa && arct<hRa){
							// -pi/4 -> down-right
							xpt+=1;
							ypt-=1;
						}
						colors[ypt*columns+xpt] = paint.getColor();
					}
				}
			}
			
			lastcolor[0]=2;
			lastcolor[1]=sq1;
			lastcolor[2]=sq2;
		}else if(visualize){
			paint.setARGB(255, 100, 100, 100);
			colors[square] = paint.getColor();
			
			if(lastcolor[0]==3){
				
				int xpt=lastcolor[1];
				int ypt=lastcolor[2];
				int xdist;
				int ydist;
				double arct;
				
				if(Math.abs(sq1-xpt)<10&&Math.abs(sq2-ypt)<10){
					while(xpt!=sq1||ypt!=sq2){
						xdist = sq1 - xpt;
						ydist = sq2 - ypt;
						arct = Math.atan2(ydist, xdist);
						if(arct>hRa && arct<aRa){
							// 0 -> right
							xpt+=1;
						}else if(arct>=aRa && arct<=bRa){
							// pi/4 -> up-right
							xpt+=1;
							ypt+=1;
						}else if(arct>bRa && arct<cRa){
							// pi/2 -> up
							ypt+=1;
						}else if(arct>=cRa && arct<=dRa){
							// 3pi/4 -> up-left
							ypt+=1;
							xpt-=1;
						}else if(arct>dRa || arct<eRa){
							// pi -> left
							xpt-=1;
						}else if(arct>=eRa && arct<=fRa){
							// -3pi/4 -> down-left
							xpt-=1;
							ypt-=1;
						}else if(arct>fRa && arct<gRa){
							// -pi/2 -> down
							ypt-=1;
						}else if(arct>gRa && arct<hRa){
							// -pi/4 -> down-right
							xpt+=1;
							ypt-=1;
						}
						colors[ypt*columns+xpt] = paint.getColor();
					}
				}
				
			}
			
			lastcolor[0]=3;
			lastcolor[1]=sq1;
			lastcolor[2]=sq2;
		}
			
		for(int j=0;j<rows;j++){
			for(int i=0;i<columns;i++) {
				if((i==bax&&j==bay)||(i==bbx&&j==bby)||(i==bcx&&j==bcy)||(i==bdx&&j==bdy))
					paint.setARGB(255, 0, 255, 0);
				else
					paint.setColor(colors[j*columns+i]);
				
				canvas.drawRect(ArrayRectFs.get(j*columns+i), paint);
			}
		}
		
		paint.setTextSize(100);
		paint.setARGB(255, 0, 0, 0);
		if((tempe>(float)999||tempe<(float)-50)&&!gogoStart&&!gogoEnd&&!errorx&&!errory&&!errorz&&!errora&&!zend) {
			paint.setTextSize(30);
			String posStr = String.format("(%.3f,%.3f,%.3f,%.3f)",X,Y,Z,pesto);
			canvas.drawText(posStr, 40, h-40, paint);
		}else if(!gogoStart&&!gogoEnd&&!zend){
			if(tempe<coldest)
				coldest = tempe;
			
			paint.setTextSize(30);
			String posStr = String.format("(%.3f,%.3f,%.3f,%.3f)",X,Y,Z,pesto);
			canvas.drawText(posStr, 40, h-150, paint);
			paint.setTextSize(100);
			String tform = String.format("Coldest: %.2f | %.2f",coldest, tempe);			
			if(adjust)
				tform+=" *";
			
			canvas.drawText(tform, 40, h-40, paint);
		}else if(gogoStart){
			switch(startseq){
			case 0:
				canvas.drawText("start sequence.",40,h-40,paint);
				startseq++;
				break;
			case 1:
				canvas.drawText("start sequence .",40,h-40,paint);
				startseq++;
				break;
			case 2:
				canvas.drawText("start sequence  .",40,h-40,paint);
				startseq++;
				break;
			case 3:
				canvas.drawText("start sequence   .",40,h-40,paint);
				startseq=0;
				break;
			}
			
		}else if(gogoEnd){
			switch(startseq){
			case 0:
				canvas.drawText("end sequence.",40,h-40,paint);
				startseq++;
				break;
			case 1:
				canvas.drawText("end sequence .",40,h-40,paint);
				startseq++;
				break;
			case 2:
				canvas.drawText("end sequence  .",40,h-40,paint);
				startseq++;
				break;
			case 3:
				canvas.drawText("end sequence   .",40,h-40,paint);
				startseq=0;
				break;
			}
		}else if(zend){
			canvas.drawText("finished",40,h-40,paint);
		}
		else if(errorx)
			canvas.drawText("X-axis error",40,h-40,paint);
		else if(errory)
			canvas.drawText("Y-axis error",40,h-40,paint);
		else if(errorz)
			canvas.drawText("Z-axis error",40,h-40,paint);
		else if(errora)
			canvas.drawText("A-axis error",40,h-40,paint);
	}
	
	
	public class PostCommand implements Runnable{
		private String comdStr;
		PostCommand(String _comdStr){
			comdStr = _comdStr;
		}
		@Override
		public void run(){
			sendCommand(comdStr);
		}
		
	}
	
	public class PostGcode implements Runnable{
		private String gStr;
		PostGcode(String _gStr){
			gStr = _gStr;
		}
		@Override
		public void run(){
			sendGcode(gStr);
		}
	}
	
	public class EndSequence implements Runnable{
		boolean runcont;
		EndSequence(boolean cont){
			runcont = cont;
		}

		@Override
		public void run() {
			if(debug)
				Log.d(TAG, "END_SEQUENCE");
			
			gogoEnd = true;
			StringBuilder cap = new StringBuilder();
			if(!endquick){	
				cap.append("num-1");
				cap.append("\n");
				cap.append("ingHOME");
				cap.append("\n");
				cap.append("pla-1");
				cap.append("\n");
			}
			idx++;
			String cappy = "N" + idx + "g90 g1 f"+String.valueOf(quartZ)+" z0";
			cap.append(cappy);
			cap.append("\n");
			idx++;
			cappy = "N" + idx + "g90 g1 f"+String.valueOf(quartXY)+" x0y0"; 
			cap.append(cappy);
			cap.append("\n");
			idx++;
			cappy = "N" + idx + "g90 g1 f"+String.valueOf(fullA)+" a0"; 
			cap.append(cappy);
			cap.append("\n");
			if(!suckDelay){
				idx++;
				cappy = "N" + idx + "M19";
				cap.append(cappy);
				cap.append("\n");
			}
			if(!gripDelay){
				idx++;
				cappy = "N" + idx + "M21";
				cap.append(cappy);
				cap.append("\n");
			}
			cap.append("(msgTHE END)\n");
			cappy = "cmd{\"si\":250}";
			cap.append(cappy);
			cap.append("\n");
			cappy = "cmd{\"sr\":\"\"}";
			cap.append(cappy);
			cap.append("\n");
			
			
			queuePID.add(cap.toString());
			cap.setLength(0);
			sendCommand("{\"sr\":\"\"}\n");
			Message = "GO TIME";
		}
	}
	
	public class StartSequence implements Runnable{
		
		StartSequence(){
			
		}
		
		@Override
		public void run() {
			if(tempOnce){
				sendGcode("M24");
				tempOnce = false;
			}
			coldest = 1000f;
			if(debug)
				Log.d(TAG, "START_SEQUENCE");
			currentRow = 0;
			gogoStart = true;
			gogoEnd = false;
			zend = false;
			running = true;
			if(!locker)
				resume();
			
			goRows.clear();
			timeRows.clear();
			RowTimes rots = new RowTimes(1);
			goRows.push(0);
			timeRows.push(rots);
			resetOffsets();
			sendGcode("(msgSTART SEQUENCE)");
			sendCommand("{\"sr\":\"\"}\n");
			
		}
		
	}
	
	public class FinalStartup implements Runnable{
		private boolean bo;
		public FinalStartup(boolean _bo){
			bo = _bo;
		}
		@Override
		public void run() {
			if(!bo)
				sendGcode("M24");
			
			if(visualize){
				PostCommand xamCmd = new PostCommand("{\"xam\":2}\n");
				PostCommand yamCmd = new PostCommand("{\"yam\":2}\n");
				PostCommand zamCmd = new PostCommand("{\"zam\":2}\n");
				PostCommand aamCmd = new PostCommand("{\"aam\":2}\n");
				
				startHandler.postDelayed(xamCmd, 300);
				startHandler.postDelayed(yamCmd, 600);
				startHandler.postDelayed(zamCmd, 900);
				startHandler.postDelayed(aamCmd, 1200);
			}else{
				PostGcode zCmd = new PostGcode("g90 g1 f"+String.valueOf(quartZ)+" z0");
				if(debug)
					Log.d(TAG,"quartXY: "+String.valueOf(quartXY));
				
				PostGcode xyCmd = new PostGcode("g90 g1 f"+String.valueOf(quartXY)+" x0y0");
				PostGcode aCmd = new PostGcode("g90g1f"+String.valueOf(fullA)+"A0.0");
				
				spotX[0] = spotY[0] = spotZ[0] = CumZ = 0.0f; 
				spotA[0] = CumA = 0.0f;
				
				startHandler.postDelayed(zCmd, 300);
				startHandler.postDelayed(xyCmd, 600);
				startHandler.postDelayed(aCmd, 900);
			}
			gripping = false;
			homingFlag = 0;
			PostCommand m21Cmd = new PostCommand("M21\n");
			PostCommand m22Cmd = new PostCommand("M22\n");
			PostCommand siCmd = new PostCommand("{\"si\":50}\n");
			PostCommand srCmd = new PostCommand("{\"sr\":\"\"}\n");
			PostGcode asgGcode = new PostGcode("(msgALL SYSTEMS GO)");
			startHandler.postDelayed(m21Cmd, 1500);
			startHandler.postDelayed(m22Cmd, 1800);
			startHandler.postDelayed(siCmd, 2400);
			startHandler.postDelayed(asgGcode, 2700);
			startHandler.postDelayed(srCmd, 2750);
		}
	}
	
	@Override
	public void onBackPressed(){
		if(running){
			
		}else{
			sendGcode("M25");
			super.onBackPressed();
		}
	}
	
	public class OneAtATime implements Runnable{
		private boolean go;
		private Command comm;
		
		OneAtATime(Command command, boolean _go){
			go = _go;
			comm = new Command(command);
		}
		
		@Override
		public void run() {
			gogoStart = false;
			currentRow = comm.Number;
			
			if(debug)
				Log.d(TAG, "OneAtATime running, CR: "+String.valueOf(currentRow));
			if(!go){
				if(debug)
					Log.d(TAG, "NO GO");
				enterQ(comm);
			}else{
				if(debug){
					Log.d(TAG, "GO");
					Log.d(TAG, "command.getRowA("+String.valueOf(comm.RowA+")"));
				}
				
				if(comm.RowA>0){
					int tis = recipe.get(comm.RowA-1).Times;
					if(tis<1)
						tis=1;
					RowTimes rtss = new RowTimes(tis);
					if(debug)
						Log.d(TAG,"timeRows.push: "+String.valueOf(tis));
					timeRows.push(rtss);
					if(debug)
						Log.d(TAG,"goRows PUSH: "+String.valueOf(currentRow));
					goRows.push(currentRow);
					if(debug)
						Log.d(TAG,"ROW B:"+String.valueOf(String.valueOf(comm.RowB)));
					if(comm.RowB>0){
						RowTimes bob = new RowTimes(currentRow,comm.RowB);
						goB.push(bob);
						if(debug)
							Log.d(TAG,"goB pushed: "+String.valueOf(comm.RowB));
					}else{
						RowTimes bob = new RowTimes(currentRow,currentRow-1);
						goB.push(bob);
						if(debug)
							Log.d(TAG,"goB pushed: "+String.valueOf(currentRow-1));
					}
					pltime = String.valueOf(timeRows.peek().count());
					SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String format = s.format(new Date());
					
					cmdlog.insert(0,String.valueOf(logcounter++)+" : "+
						format+" : "+String.valueOf(currentRow)+
						" -> "+String.valueOf(comm.RowA)+"\n"
						);
					column_one.setText(cmdlog.toString());
					OneAtATime oaat = new OneAtATime(recipe.get(comm.RowA-1),true);
					exHandler.post(oaat);
				}else{
					enterQ(comm);
				}
			}
		}
	}
	
	public void enterQ(Command com){
		String cmdStr = null;
		String 	xGo,  
				yGo,
				zTe,
				zGo,  
				aGo,
				speed,
				zspeed,
				aspspeed;
		float 	osx = 0.0f, ox = 0.0f,
				osy = 0.0f, oy = 0.0f,
				osz = 0.0f, oz = 0.0f,
				condi,
				aspi;
		
		int		sensorq,
				critter,
				neg,
				mixtimes;
		
		boolean traceq;
		
		sensorq = com.Sensor;
		traceq = com.Trace;
		condi = com.Condition;
		critter = com.Criterion;
		neg = com.Negative-3;
		mixtimes = com.Mix;
		
		StringBuilder ordinance = new StringBuilder();
		if(sensorq==1||sensorq==2)
			ordinance.append("sen\n");
		else
			ordinance.append("seb\n");
		
		if(traceq)
			trace = true;
		else
			trace = false;
		
		switch(critter){
		case 0:
			ordinance.append("cri0\n");
			break;
		case 1:
			ordinance.append("cri1\n");
			break;
		case 2:
			ordinance.append("cri2\n");
			break;
		default:
			ordinance.append("cri3\n");
			break;
		}

		negative = neg;
		condition = condi;
		
		ordinance.append("nic");
		ordinance.append(String.valueOf(logcounter));
		ordinance.append("\n");
		ordinance.append("num");
		ordinance.append(com.NumberS);
		ordinance.append("\n");
		ordinance.append("ing");
		ordinance.append(com.Ingredient);
		ordinance.append("\n");
		
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String format = s.format(new Date());
		
		cmdlog.insert(0,String.valueOf(logcounter++)+" : "+
				format+" : "+
				com.NumberS+" : "+
				com.Ingredient+" : "+
				pltime+" : "+temperature+"\n"
				);
		column_one.setText(cmdlog.toString());
		
		xGo = template.getX(com.Ingredient);
		yGo = template.getY(com.Ingredient);
		zTe = template.getZ(com.Ingredient);
		zGo = com.DepthS;
		
		ox = Float.parseFloat(xGo);
		oy = Float.parseFloat(yGo);
		oz = Float.parseFloat(zGo);
		
		if(com.OffsetXS!="")
			osx = com.OffsetX;
		if(com.OffsetYS!="")
			osy = com.OffsetY;
		if(com.OffsetZS!="")
			osz = com.OffsetZ;
		
		int numb = com.Number-1;
		
		if(firstx[numb]==false){
			firstx[numb]=true;
		}else{
			cumx[numb]+=osx;
		}
		if(firsty[numb]==false){
			firsty[numb]=true;
		}else{
			cumy[numb]+=osy;
		}
		if(firstz[numb]==false){
			firstz[numb]=true;
		}else{
			cumz[numb]+=osz;
		}
		
		ox = ox + cumx[numb];
		oy = oy + cumy[numb];
		oz = oz + cumz[numb];
		
		xGo = String.valueOf(ox);
		yGo = String.valueOf(oy);
		zGo = String.valueOf(oz);
		
		if(debug){
			Log.d("OFFSETY","osx: " + String.valueOf(osx) + " xGo: " + xGo);
			Log.d("OFFSETY","osy: " + String.valueOf(osy) + " yGo: " + yGo);
		}
		
		aspi = com.Aspirate;
		aspi = -aspi*conversion_factor;
		aGo = String.valueOf(aspi);//com.getAspirateS();
		
		if(debug)
			Log.d(TAG,"getSpeed: "+com.SpeedS);
		switch(com.Speed){
		case 0:
			speed = String.valueOf(fullXY);
			break;
		case 1:
			speed = String.valueOf(threequartXY);
			break;
		case 2:
			speed = String.valueOf(halfXY);
			break;
		case 3:
			speed = String.valueOf(quartXY);
			break;
		case 4:
			speed = String.valueOf(onetenXY);
			break;
		default:
			speed = String.valueOf(fullXY);
			break;	
		}
		if(debug){
			Log.d(TAG,"Speed: "+speed);
			Log.d(TAG,"getZSeed: "+com.ZSpeedS);
		}
		switch(com.ZSpeed){
		case 0:
			zspeed = String.valueOf(fullZ);
			break;
		case 1:
			zspeed = String.valueOf(threequartZ);
			break;
		case 2:
			zspeed = String.valueOf(halfZ);
			break;
		case 3:
			zspeed = String.valueOf(quartZ);
			break;
		case 4:
			zspeed = String.valueOf(onetenZ);
			break;
		default:
			zspeed = String.valueOf(fullZ);
			break;	
		}
		if(debug){
			Log.d(TAG,"ZSpeed: "+zspeed);
			Log.d(TAG,"getAspSeed: "+com.AspSpeedS);
		}
		switch(com.AspSpeed){
		case 0:
			aspspeed = String.valueOf(fullA);
			break;
		case 1:
			aspspeed = String.valueOf(threequartA);
			break;
		case 2:
			aspspeed = String.valueOf(halfA);
			break;
		case 3:
			aspspeed = String.valueOf(quartA);
			break;
		case 4:
			aspspeed = String.valueOf(onetenA);
			break;
		default:
			aspspeed = String.valueOf(fullA);
			break;	
		}
		if(debug)
			Log.d(TAG,"AspSpeed: "+aspspeed);
		
		if(com.Delay>0.0){
			idx++;
			cmdStr = "N" + idx + " G4 P" + com.DelayS;
			ordinance.append(cmdStr);
			ordinance.append("\n");
		}
		
		idx++;
		cmdStr = "N" + idx + " g90 g1 f"+ speed + " X" + xGo + " Y" + yGo;
		ordinance.append(cmdStr);
		ordinance.append("\n");
		
		if(Float.parseFloat(zTe)!=(float)0.0){
			idx++;
			cmdStr = "N" + idx + " g90 g1 f" + zspeed + " Z" + zTe;
			ordinance.append(cmdStr);
			ordinance.append("\n");
			CumZ = Float.parseFloat(zTe);
		}
		
		idx++;
		cmdStr = "N" + idx + " g91 g1 f" + zspeed + " Z" + zGo;
		ordinance.append(cmdStr);
		ordinance.append("\n");
		
		idx++;
		cmdStr = "N" + idx + " g91 g1 f" + aspspeed + " A" + aGo;
		ordinance.append(cmdStr);
		ordinance.append("\n");
		
		if(mixtimes>0){
			for(int mt = 0; mt<mixtimes; mt++){
				cmdStr = "N" + idx + " g90 g1 f" + aspspeed + " A"+empty;
				ordinance.append(cmdStr);
				ordinance.append("\n");
				cmdStr = "N" + idx + " g91 g1 f" + aspspeed + " A" + aGo;
				ordinance.append(cmdStr);
				ordinance.append("\n");
			}
			cmdStr = "N" + idx + " g90 g1 f" + aspspeed + " A"+empty;
			ordinance.append(cmdStr);
			ordinance.append("\n");
			
			CumZ+=Float.parseFloat(zGo);
			CumA=Float.parseFloat(empty);
			
		}else{
			CumZ+=Float.parseFloat(zGo);
			CumA+=Float.parseFloat(aGo);
		}
		
		spotX[1] = Float.parseFloat(xGo);
		spotY[1] = Float.parseFloat(yGo);
		spotZ[1] = CumZ;
		spotA[1] = CumA;
		
		ordinance.append("(msgMOVEMENT COMPLETE)\n");
		queuePID.add(ordinance.toString());
		ordinance.setLength(0);

		spotX[2] = spotX[1];
		spotY[2] = spotY[1];
		spotZ[2] = CumZ;
		//spotA2.offer(CumA);
		
		if(sensorq==1||sensorq==3)
			ordinance.append("sen\n");
		else
			ordinance.append("seb\n");
		
		if(com.Blowout){
			idx++;
			cmdStr = "N" + idx + " g90 g1 f" + aspspeed + " A"+empty;
			ordinance.append(cmdStr);
			ordinance.append("\n");
			CumA=Float.parseFloat(empty);
		}
		
		if(com.DropTip){
			idx++;
			cmdStr = "N" + idx + " G90 a"+droptip;
			ordinance.append(cmdStr);
			ordinance.append("\n");
			cmdStr = "N" + idx + " G90 a"+empty;
			ordinance.append(cmdStr);
			ordinance.append("\n");
			CumA=Float.parseFloat(empty);
		}
		
		if(com.Suction){
			idx++;
			cmdStr = "N" + idx + " M18";
			ordinance.append(cmdStr);
			ordinance.append("\n");
			if(com.Suction_timer>0.0f){
				suckDelay = true;
				ordinance.append("stm");
				ordinance.append(String.valueOf(com.Suction_timer));
				ordinance.append("\n");
			}
		}else{
			if(!suckDelay){
				idx++;
				cmdStr = "N" + idx + " M19";
				ordinance.append(cmdStr);
				ordinance.append("\n");
			}
		}
		
		if(com.Grip){
			idx++;
			cmdStr = "N" + idx + " M20";
			ordinance.append(cmdStr);
			ordinance.append("\n");
			gripping=true;
			idx++;
			cmdStr = "N" + idx + " G4 P2";
			ordinance.append(cmdStr);
			ordinance.append("\n");
			if(com.Grip_timer>0.0f){
				gripDelay = true;
				ordinance.append("gtm");
				ordinance.append(String.valueOf(com.Grip_timer));
				ordinance.append("\n");
			}
		}else{
			if(!gripDelay){
				idx++;
				cmdStr = "N" + idx + " M21";
				ordinance.append(cmdStr);
				ordinance.append("\n");
				if(gripping){
					idx++;
					cmdStr = "N" + idx + " G4 P2";
					ordinance.append(cmdStr);
					ordinance.append("\n");
					gripping = false;
				}
			}
		}
		
		spotA[2] = CumA;
		
		ordinance.append("(msgMODALS COMPLETE)\n");
		queuePID.add(ordinance.toString());
		ordinance.setLength(0);
		
		/*spotX3.offer(Float.parseFloat(xGo));
		spotY3.offer(Float.parseFloat(yGo));
		spotZ3.offer(CumZ);
		spotA3.offer(CumA);
		*/
		if(sensorq==1||sensorq==4)
			ordinance.append("sen\n");
		else
			ordinance.append("seb\n");
		
		//	AUTORETURNS
		if(com.AutoReturnZ){
			idx++;
			cmdStr = "N" + idx + " g90 g1 f" + String.valueOf(quartZ) + " Z0"; // zspeed
			ordinance.append(cmdStr);
			ordinance.append("\n");
			
			CumZ = 0.0f;
			//spotZ[3] = 0.0f;
		}
		if(com.AutoReturnY){
			idx++;
			cmdStr = "N" + idx + " g90 g1 f" + String.valueOf(quartXY) + " Y0";
			ordinance.append(cmdStr);
			ordinance.append("\n");
			spotY[3] = spotY[4] = spotY[0] = 0.0f;
		}else{
			spotY[3] = spotY[4] = spotY[0] = Float.parseFloat(yGo);
		}
		if(com.AutoReturnX){
			idx++;
			cmdStr = "N" + idx + " g90 g1 f" + String.valueOf(quartXY) + " X0"; // speed
			ordinance.append(cmdStr);
			ordinance.append("\n");
			spotX[3] = spotX[4] = spotX[0] = 0.0f;
		}else{
			spotX[3] = spotX[4] = spotX[0] = Float.parseFloat(xGo);
		}
		spotZ[3] = spotZ[4] = spotZ[0] = CumZ;
		spotA[3] = spotA[4] = spotA[0] = CumA;
		
		ordinance.append("(msgAUTORETURNS COMPLETE)\n");
		queuePID.add(ordinance.toString());
		
		/*
		spotX[0] = spotX[4] = spotX[3];
		spotY[0] = spotY[4] = spotY[3];
		spotZ[0] = spotZ[4] = spotZ[3];
		spotA[0] = spotA[4] = spotA[3];
		*/
		ordinance.setLength(0);
		
		if(sensorq==1||sensorq==4)
			ordinance.append("sen\n");
		else
			ordinance.append("seb\n");
		
		if(com.Home){
			if(debug)
				Log.d(TAG,"GOING HOME");
			ordinance.append("(msgHOME)\n");
			ordinance.append("cmd{\"sr\":\"\"}\n");
			queuePID.add(ordinance.toString());
		}else{
			idx++;
			cmdStr = "N" + idx + "(msgNEXT)";
			ordinance.append(cmdStr);
			queuePID.add(ordinance.toString());
		}
		if(debug)
			Log.d(TAG,""+queuePID.size());
		sendCommand("{\"sr\":\"\"}\n");
		Message = "GO TIME";
	}
	
	private class RowTimes{
		private int times;
		private int count;
		public RowTimes(int _times, int _count){
			times = _times;
			count = _count;
		}
		public RowTimes(int _times){
			this(_times, 1);
		}
		
		public int count(){
			return count;
		}
		public int times(){
			return times;
		}
		
		public void increment(){
			count++;
		}
	}
	
	public void homeAll() {
		if(debug){
			Log.d(TAG,"made it to homing...");
			Log.d(TAG, "this.connected? " + String.valueOf(Boom.connected));
			Log.d(TAG, "connected? " + String.valueOf(connected));
		}
		if(Boom.connected){
			if(bindType==0){
				BTBetaBot.send_flush();
			}else{
				BetaBot.send_flush();
			}
			
			if(homeSB == null) 
				homeSB = new StringBuilder();
			else
				homeSB.setLength(0);	
			
			homeSB.append("HOMING "); // 10
			
			String strAxes = settings.getString("nwa", "xyza");
			strAxes = strAxes.toLowerCase();
			
			if(strAxes.contains("z")) {
				doZ = true;
				homeSB.append(" Z ");
			} if(strAxes.contains("a")) {
				doA = true;
				homeSB.append(" A ");
			} if(strAxes.contains("x")) {
				doX = true;
				homeSB.append(" X ");
			} if(strAxes.contains("y")) {
				doY = true;
				homeSB.append(" Y ");
			}  if(strAxes.contains("b")) {
				doB = true;
			} if(strAxes.contains("c")) {
				doC = true;
			}
			homeSB.append("\n");
			
			progressBar = new ProgressDialog(this);
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
					progressBar.dismiss();
				}
			});
			progressBar.show();
			
			new Thread(new Runnable() {
				public void run() {
					homingXYZA = true;
					
					if(doZ){
						if(!cancel){
							homingZ = true;
							RunHome(2);
						}
						
						homeSB.append("\nhoming Z...");	// 12
						exHandler.post(new Runnable() {
							public void run(){
								progressBar.setMessage(homeSB.toString());
							}
						});
						while(homingZ){
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
						exHandler.post(new Runnable() {
							public void run(){
								progressBar.setMessage(homeSB.toString());
							}
						});
					}
					
					if(doA) {
						if(!cancel){
							homingA = true;
							RunHome(3);
						}
						homeSB.append("\nhoming A..."); // 12
						exHandler.post(new Runnable() {
							public void run(){
								progressBar.setMessage(homeSB.toString());
							}
						});
						while(homingA){
							try{
								Thread.sleep(1000);
								if(debug)
									Log.d(TAG, "homingA: "+String.valueOf(homingA));
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
						homeSB.setLength(homeSB.length()-12);
						homeSB.append("A homed\n");
						exHandler.post(new Runnable() {
							public void run() {
								progressBar.setMessage(homeSB.toString());
							}
						});
					}
					if(doX) {
						if(!cancel){
							homingX = true;
							RunHome(0);
						}
						homeSB.append("\nhoming X..."); // 12
						exHandler.post(new Runnable() {
							public void run(){
								progressBar.setMessage(homeSB.toString());
							}
						});
						while(homingX) {
							try{
								Thread.sleep(1000);
								if(debug)
									Log.d(TAG, "homingX: "+String.valueOf(homingX));
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
						homeSB.setLength(homeSB.length()-12);
						homeSB.append("X homed\n");
						exHandler.post(new Runnable() {
							public void run() {
								progressBar.setMessage(homeSB.toString());
							}
						});
					}
					if(doY) {
						if(!cancel){
							homingY = true;
							RunHome(1);
						}
						homeSB.append("\nhoming Y..."); // 12
						exHandler.post(new Runnable() {
							public void run(){
								progressBar.setMessage(homeSB.toString());
							}
						});
						while(homingY) {
							try{
								Thread.sleep(1000);
								if(debug)
									Log.d(TAG, "homingY: "+String.valueOf(homingY));
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
						homeSB.setLength(homeSB.length()-12);
						homeSB.append("Y homed\n");
						exHandler.post(new Runnable() {
							public void run() {
								progressBar.setMessage(homeSB.toString());
							}
						});
					}
					homingXYZA = homingXY = homingX = homingY = homingZ = homingA = false;
					progressBar.dismiss();
					spotX[4] = spotY[4] = spotZ[4] = spotA[4] = 0.0f;
					homingFlag = 0;
					
					if(errorx||errory||errorx||errory||endquick){
						if(debug)
							Log.d(TAG,"endquick1: "+String.valueOf(endquick));
						queuePID.clear();
						EndSequence endSequence = new EndSequence(false);
						exHandler.post(endSequence);
					}else{
						Message = "NEXT";
						sendCommand("{\"sr\":\"\"}\n");
					}
				}
			}).start();
		}
	}
	
	public void RunHome(int axisCode) {
		switch(axisCode){
		case 0:
			DelayedHomeX delayedHomeX = new DelayedHomeX();
			exHandler.postDelayed(delayedHomeX,3010);
			break;
		case 1:
			DelayedHomeY delayedHomeY = new DelayedHomeY();
			exHandler.postDelayed(delayedHomeY,3010);
			break;
		case 2:
			DelayedHomeZ delayedHomeZ = new DelayedHomeZ();
			exHandler.postDelayed(delayedHomeZ,3010);
			break;
		case 3:
			DelayedHomeA delayedHomeA = new DelayedHomeA();
			exHandler.postDelayed(delayedHomeA,3010);
			break;
			default:
				break;
		}
	}
	
	public class DelayedHomeX implements Runnable{
		DelayedHomeX(){	}

		@Override
		public void run() {
			homeX();
		}
	}
	public class DelayedHomeY implements Runnable{
		DelayedHomeY(){	}

		@Override
		public void run() {
			homeY();
		}
	}
	public class DelayedHomeZ implements Runnable{
		DelayedHomeZ(){	}

		@Override
		public void run() {
			homeZ();
		}
	}
	public class DelayedHomeA implements Runnable{
		DelayedHomeA(){	}

		@Override
		public void run() {
			homeA();
		}
	}
	
	public void homingProgress(String _axis){
		
		final String axis = _axis;
		
		progressBar = new ProgressDialog(Boom.this);
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
				homingX = homingY = homingZ = homingA = false;
				progressBar.dismiss();
			}
		}).start();
		
	}
	
	public void resetOffsets(){
		Arrays.fill(cumx, 0.0f);
		Arrays.fill(cumy, 0.0f);
		Arrays.fill(cumz, 0.0f);
	}
	
	public class DelayedPause implements Runnable{
		public DelayedPause(){	}

		@Override
		public void run() {
			pause();
		}
		
	}
	
	public class DelayedFinish implements Runnable{
		public DelayedFinish(){	}
		
		@Override
		public void run() {
			finish();
		}
	}
	
	public class PointColor{
		private Point pnt;
		private int colour;
		public PointColor(Point _pnt, int _colour){
			pnt = new Point(_pnt);
			colour = _colour;
		}
		
		public PointColor(int x, int y, int _colour){
			pnt = new Point(x,y);
			colour = _colour;
		}
		
		public Point point(){
			return pnt;
		}
		
		public int color(){
			return colour;
		}
		
		public void setColor(int _colour){
			colour = _colour;
		}
	}
	
	public void alertLocation(int _category, float _sx, float _sy, float _sz, float _sa, float _x, float _y, float _z, float _a){
		if(debug)
			Log.d(TAG,"alertLocation fired");
		boolean go = false;
		if(!settings.getBoolean("show_warning",true)){
			if(Math.abs(_sx-_x)>1||Math.abs(_sy-_y)>1||Math.abs(_sz-_z)>1||Math.abs(_sa-_a)>1){
				//go = true;
			}
		}else{
			if(go_once){
				
			}else{
				if(continuous)
					go_once = true;
				
				// If this condition is included there's not much point to the above, but I'm on the
				// fence about it so I'm leaving the rest as is in case it gets pulled 
				if(Math.abs(_sx-_x)>1||Math.abs(_sy-_y)>1||Math.abs(_sz-_z)>1||Math.abs(_sa-_a)>1){
					//go = true;
				}
			}
		}
		
		if(go){
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View myCustomView = inflater.inflate(R.layout.custom_alert, null, false);		
			
			AlertDialog.Builder aldb = new AlertDialog.Builder(this);
			aldb.setMessage("Caution: Unexpected location["+_category+"]:\n" +
					"expected ("+_sx+","+_sy+","+_sz+","+_sa+")\n" +
					"versus   ("+_x+","+_y+","+_z+","+_a+")")
			.setView(myCustomView)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("CAUTION")
			.setCancelable( true )
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int id) {
					CheckBox cb = (CheckBox) myCustomView.findViewById(R.id.no_show);
					
					if(cb.isChecked()){
						SharedPreferences.Editor Ed = settings.edit();
						Ed.putBoolean("show_warning", false);
						Ed.commit();
					}
				}
			});
			AlertDialog ad = aldb.create();
			ad.show();
		}else{
			
		}
	}
	
	public void launchDelay(int _kind, long _time){
		delaySuckGrip = new PDSendGcode(_kind);
		exHandler.postDelayed(delaySuckGrip, _time);
	}
	
	public class PDSendGcode implements Runnable{
		private int cmd;
		
		PDSendGcode(int _cmd){
			cmd = _cmd;
		}
		
		@Override
		public void run() {
			switch(cmd){
			case 0:
				//parent.sendGcode("M21");
				sendGcode("M19");
				suckDelay = false;
				if(debug)
					Log.d(TAG,"PDSendGcode m19");
				break;
			case 1:
				//parent.sendGcode("M19");
				sendGcode("M21");
				gripDelay = false;
				if(debug)
					Log.d(TAG,"PDSendGcode m21");
				break;
				default:
					break;
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
		return result;
	}
	
}
