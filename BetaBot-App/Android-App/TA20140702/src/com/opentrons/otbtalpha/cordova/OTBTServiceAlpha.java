package com.opentrons.otbtalpha.cordova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opentrons.otbtalpha.cordova.OTBTLogicAlpha.ExecuteResult;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class OTBTServiceAlpha extends Service implements IUpdateListener{
	
	/*
	 ************************************************************************************************
	 * Static values 
	 ************************************************************************************************
	 */
	public static final String TAG = OTBTServiceAlpha.class.getSimpleName();
	private static final boolean D = true;
	// Message types sent from the BluetoothSerialService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
    public static final String blueName = OTBTBlueServiceAlpha.class.getName();
    
    // Received when starting a job
    public static String args;
    public static JSONObject job;
    public static JSONArray ingredients;
    
    // Control operation during job run
    public static boolean kill = false;
    public static boolean paused = false;
    
    // Control during setup
	public static boolean oscCalled = false;
	private boolean mServiceConnected = false;
	private boolean mServiceInitialised = false;
	private boolean boomthreading = false;
	private final Object mResultLock = new Object();
	private Object myServiceConnectedLock = new Object();
	
	// Misc
	StringBuffer buffer = new StringBuffer();
    private OTBTApiAlpha mApi;
    private JSONObject mLatestResult = null;
	private List<OTBTListenerAlpha> mListeners = new ArrayList<OTBTListenerAlpha>();
	public static Handler dHandler = new Handler();
	ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private String mUniqueID = java.util.UUID.randomUUID().toString();
	
	protected JSONObject getLatestResult() {
		synchronized (mResultLock) {
			return mLatestResult;
		}
	}
	
	protected void setLatestResult(JSONObject value) {
		synchronized (mResultLock) {
			this.mLatestResult = value;
		}
	}
	
	
	/*
	 ************************************************************************************************
	 * Overriden Methods 
	 ************************************************************************************************
	 */
	
	@Override  
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind called");
		
		return apiEndpoint;
	}     
	
	@Override
	public boolean onUnbind(Intent intent) {
		boolean result = false;
		try {
			mApi.removeListener(serviceListener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@Override  
	public void onCreate() {     
		super.onCreate();
		Log.i(TAG, "Service creating");
		if(!mServiceInitialised) {
			
		}
		// Duplicating the call to initialiseService across onCreate and onStart
		// Done this to ensure that my initialisation code is called.
		// Found that the onStart was not called if Android was re-starting the service if killed
		initialiseService();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(TAG, "onStartCommand called("+intent.toString()+", "+flags+", "+startId+") called");
		args = intent.getStringExtra("args");
		Log.d(TAG, "args: " + args);
		if(!(args==null)){
			try {
				job = new JSONObject(args);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "oscCalled? "+oscCalled);
			if(!oscCalled) {
				oscCalled = true;
				// We want this service to continue running until it is explicitly
			    // stopped, so return sticky.
				Log.d(TAG, "oscCalled -> true");
				final ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
				final List<RunningServiceInfo> services = 
						activityManager.getRunningServices(Integer.MAX_VALUE);
				boolean blueFound = false;
				for (int i = 0; i < services.size(); i++) {
					if(D) {
						Log.d(TAG,"SERVICES_A|Service Nr. " + i + ":" + services.get(i).service);
						Log.d(TAG,"SERVICES_B|Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
						Log.d(TAG,"SERVICES_C|Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
					}
					if(services.get(i).service.getClassName().equals("com.opentrons.otbtalpha.cordova.OTBTBlueServiceAlpha")){
						blueFound = true;
						break;
					}
				}
				if(!blueFound){
					Log.d(TAG, "BLUE not found, what gives!?!");
				}else{
					if(!mServiceConnected){
						
						serviceConnectionZ = new ServiceConnection() {
							@Override
							public void onServiceConnected(ComponentName name, IBinder service) {
								// that's how we get the client side of the IPC connection
								Log.d(TAG, "onServiceConnected called!");
								mApi = OTBTApiAlpha.Stub.asInterface(service);
								try {
									mApi.addListener(serviceListener);
									
								} catch (RemoteException e) {
									Log.d(TAG, "addListener failed", e);
								}
								
								synchronized(myServiceConnectedLock) {
									mServiceConnected = true;

									myServiceConnectedLock.notify();
								}
								try {
									if(!boomthreading){
										Log.d(TAG, "if(!boomthreading)...");
										BoomThread boomer = new BoomThread(job);
										/*new AsyncTask<Void, Void, Void>() {

											@Override
											protected Void doInBackground(
													Void... params) {
												//try{
												//	Thread.sleep(1000);
												//}catch(Exception e){
												//	e.printStackTrace();
												//}
												Log.d(TAG, "boom.run() 1");
												boomer.run();
												return null; 
											}
											
										}.execute();*/
										//while(!executorService.isTerminated()){
										//	executorService.awaitTermination(1000,TimeUnit.MILLISECONDS);
										//	Log.d(TAG, "waiting for termination");
										//}
										executorService = Executors.newSingleThreadExecutor();
										executorService.execute(boomer);
										//dHandler.postDelayed(boomer, 1000);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							@Override
							public void onServiceDisconnected(ComponentName name) {
								synchronized(myServiceConnectedLock) {
									mServiceConnected = false;

									myServiceConnectedLock.notify();
								}
							}
						};
						
						Intent mIntent = new Intent("com.opentrons.otbtalpha.cordova.OTBTBlueServiceAlpha");
						mIntent.setClass(this, OTBTBlueServiceAlpha.class);
						Log.d(TAG, "Attempting to bind to BLUE");
						if (this.bindService(mIntent, serviceConnectionZ, 1)) {
							Log.d(TAG, "bindService succeeding... maybe...");
							/*Log.d(TAG, "Waiting for service connected lock");
							synchronized(myServiceConnectedLock) {
								Log.d(TAG, "doing the waiting thing...");
								while (mServiceConnected==null) {
									try {
										myServiceConnectedLock.wait();
									} catch (InterruptedException e) {
										Log.d(TAG, "Interrupt occurred while waiting for connection", e);
									}
								}
								//result = this.mServiceConnected;
								
							}*/
							
						}else{
							Log.d(TAG, "bindService failed... for sure...");
						}
					}else{
						try {
							if(!boomthreading){
								Log.d(TAG, "if(!boomthreading)...1");
								BoomThread boomer = new BoomThread(job);
								
								/*new AsyncTask<Void, Void, Void>() {

									@Override
									protected Void doInBackground(
											Void... params) {
										//try{
										//	Thread.sleep(1000);
										//}catch(Exception e){
										//	e.printStackTrace();
										//}
										Log.d(TAG, "boomer.run() 2");
										boomer.run();
										return null;
									}
									
								}.execute();*/
								//while(!executorService.isTerminated()){
								//	executorService.awaitTermination(1000,TimeUnit.MILLISECONDS);
								//	Log.d(TAG, "waiting for termination");
								//}
								executorService = Executors.newSingleThreadExecutor();
								executorService.execute(boomer);
								
								//dHandler.postDelayed(boomer, 1000);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}else{
			Log.d(TAG, "args == null, what gives!?!, try again");
		}
		
	    return START_STICKY;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "Service started");       
		
		// Duplicating the call to initialiseService across onCreate and onStart
		// Done this to ensure that my initialisation code is called.
		// Found that the onStart was not called if Android was re-starting the service if killed
		initialiseService();
		args = intent.getStringExtra("args");
		Log.d(TAG, "args: " + args);
		try {
			job = new JSONObject(args);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override  
	public void onDestroy() {     
		super.onDestroy();     
		Log.i(TAG, "Service destroying");
		try {
			mApi.removeListener(serviceListener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unbindService(serviceConnectionZ);
	}
	
	
	/*
	 ************************************************************************************************
	 * Private methods 
	 ************************************************************************************************
	 */
	private JSONObject getConfig() throws JSONException{
		return job;
	}
	
	private void setConfig(JSONObject joob) throws JSONException {
		job = joob;
	}
	
	
	private OTBTApiAlpha.Stub apiEndpoint = new OTBTApiAlpha.Stub() {
		
		/*
		 ************************************************************************************************
		 * Overriden Methods 
		 ************************************************************************************************
		 */
		@Override
		public String getLatestResult() throws RemoteException {
			synchronized (mResultLock) {
				if (mLatestResult == null)
					return "{}";
				else
					return mLatestResult.toString();
			}
		}
		
		@Override
		public void addListener(OTBTListenerAlpha listener)
				throws RemoteException {
			
			synchronized (mListeners) {
				if (mListeners.add(listener))
					Log.d(TAG, "Listener added");
				else
					Log.d(TAG, "Listener not added");
			}
		}
		
		@Override
		public void removeListener(OTBTListenerAlpha listener) throws RemoteException {
			
			synchronized (mListeners) {
				if (mListeners.size() > 0) {
					boolean removed = false;
					for (int i = 0; i < mListeners.size() && !removed; i++)
					{
						if (listener.getUniqueID().equals(mListeners.get(i).getUniqueID())) {
							mListeners.remove(i);
							removed = true;
						}
					}
					
					if (removed)
						Log.d(TAG, "Listener removed");
					else 
						Log.d(TAG, "Listener not found");
				}
			}
		}
		
		
		
		@Override
		public String getConfiguration() throws RemoteException {
			try {
				JSONObject argh;
				argh = getConfig();
				if (argh == null)
					return "";
				else 
					return argh.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		}
		/*
		@Override
		public void setConfiguration(String configuration) throws RemoteException {
			try {
				JSONObject array = null;
				if (configuration.length() > 0) {
					array = new JSONObject(configuration);
				} else {
					array = new JSONObject();
				}	
				setConfig(array);
			} catch (Exception ex) {
				throw new RemoteException();
			}
		}*/

		@Override
		public void run(String configuration) throws RemoteException {
			try{
				JSONObject argh = null;
				if(configuration.length()>0){
					argh = new JSONObject(configuration);
					setConfig(argh);
					runOnce();
				}
			}catch(Exception ex){
				throw new RemoteException();
			}
		}

		@Override
		public void pause() throws RemoteException {
			// TODO Auto-generated method stub
			paused = true;
			mApi.write("!".getBytes());
		}

		@Override
		public void kill() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d(TAG, "sending !%");
			mApi.write("!%".getBytes());
			kill = true;
			oscCalled = false;
			mServiceConnected = false;
		}

		@Override
		public void write(byte[] data) throws RemoteException {
			// NOOP
		}

		@Override
		public void connect(String address) throws RemoteException {
			// NOOP
		}

		@Override
		public int getState() throws RemoteException {
			return 0; // basically NOOP
		}

		@Override
		public void stop() throws RemoteException {
			// NOOP
		}

		@Override
		public void disconnect() throws RemoteException {
			// NOOP
			
		}

		@Override
		public void resume() throws RemoteException {
			mApi.write("~".getBytes());
			paused = false;
		}
	};

	private void initialiseService() {
		
		if (!this.mServiceInitialised) {
			Log.i(TAG, "Initialising the service");
			
			this.mServiceInitialised = true;
		}

	}
	
	/*
	*/
   
   /*private void notifyConnectionLost(String error){
//	   Log.i(TAG, "notifying to all listeners of msg");
	   for (int i = 0; i < mListeners.size(); i++)
	   {
		   try {
			   mListeners.get(i).handleUpdate();
			   mListeners.get(i).notifyError(error);
		   } catch (RemoteException e) {
				Log.i(TAG, "Failed to notify listener - " + i + " - " + e.getMessage());
		   }
	   }
   }*/
   
   
   private void runOnce(){
	  // NOOP 
   }
   
   
   private class BoomThread implements Runnable {
	   
	   private JSONObject mJob;
	   private JSONArray mIngredients;
	   private JSONArray mProtocol;
	   int pee = 0;
	   int status = 0;
	   //private OTBTWorkerAlpha whack;
	   private String mMessage = "";
	   private BlockingQueue<String> whackattack = new LinkedBlockingQueue<String>();
	   private BlockingQueue<Location>lackattack = new LinkedBlockingQueue<Location>();
	   private BlockingQueue<String> whackfinish = new LinkedBlockingQueue<String>();
	   private BlockingQueue<GPS>gpsattack = new LinkedBlockingQueue<GPS>();
	   private HashMap<String, Location> hIngredients = new HashMap<String, Location>();
	   private int pipette;
	   private int idx = 0;
	   private double ablow = 16.0;
	   private double dtip = 22.0;
	   
	   private boolean checkGCs = true;
	   private boolean checkPs = false;
	   private boolean lockdown = true;
	   private boolean running = true;
	   private boolean endo = false;
	   
	   private double posx, posy, posz, bosx=0.0, bosy=0.0, bosz=0.0;
	   private String gc;
	   // variables for logging
	   private int total = 0;
	   int rowcount = 0;
	   
	   public BoomThread(JSONObject job) {
		   mJob = job;
		   
		   try {
			   mProtocol = mJob.getJSONArray("protocol");
			   total = mProtocol.length();
			   Log.d(TAG, "mProtocol = " + mProtocol.toString());
			   
		   } catch (JSONException e2) {
			   e2.printStackTrace();
		   }
		   
		   try {
			   pipette = job.getInt("pipette");
			   Log.d(TAG, "pipette = "+String.valueOf(pipette));
		   } catch (JSONException e1) {
			    e1.printStackTrace();
		   }
		   try {
			   mIngredients = job.getJSONArray("ingredients");
			   Log.d(TAG, "ingredients = "+mIngredients.toString());
		   } catch (JSONException e) {
			   e.printStackTrace();
		   }
		   
		   
		   try{
			   for(int i=0; i<mIngredients.length(); i++) {
				   Log.d(TAG, "adding location "+i);
				   Location loco = new Location();
				   JSONObject jay = mIngredients.getJSONObject(i);
				   loco.ingredient = jay.getString("name");
				   loco.x = jay.getDouble("x");
				   loco.y = jay.getDouble("y");
				   loco.z = jay.getDouble("z");
				   hIngredients.put(loco.ingredient, loco);
			   }
		   }catch(Exception ex){
			   Log.d(TAG, "something went terribly wrong");
			   ex.printStackTrace();
		   }
		   Log.d(TAG, "now what?...");
	   }
	   
	   @Override
	   public void run() {
		   Log.d(TAG, "run() called");
		   //boomerang();
		   Log.d(TAG, "trying to whack some bytes");
		   try {
			   if(mApi==null){
				   Log.d(TAG, "mApi is null, uh oh");
			   }
			   gc = "G90G0X0Y0Z0A"+String.valueOf(ablow);
			   String cmd = "{\"gc\":\""+gc+"\"}\n";
			   mApi.write(cmd.getBytes());
			   mApi.write("{\"sr\":\"\"}\n".getBytes());
		   } catch (RemoteException e) {
			   e.printStackTrace();
			   running = false;
		   }
		   Log.d(TAG, "running = "+String.valueOf(running));
		   while(running) {
			   if(buffer.length()>0)
				   boomerang();
			   if(kill){
				   Log.d(TAG, "kill -> true");
				   running = false;
			   }
		   }
		   boomthreading = false;
		   Log.d(TAG, "run() finished");
		   for(OTBTListenerAlpha listener:mListeners){
			   	try {
			   		Log.d(TAG, "calling listener.shutMeDown()");
			   		buffer.setLength(0);
					listener.shutMeDown();
				} catch (RemoteException e) {
					try {
						mApi.removeListener(serviceListener);
						getApplicationContext().unbindService(serviceConnectionZ);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.d(TAG, "error tryting to send shutMeDown()");
					e.printStackTrace();
				}
		   }
		   kill = false;
		   return;
	   }
	   
	   private void boomerang() {
		   synchronized(this) {
			   //Log.d(TAG, "boomerang called");
			   String data = "";
			   data = readUntil("\n");
			   String jsonStr = "";
			   if(data != null && data.length() > 0) {
		    		try {
		    			Log.d(TAG, "data read = " + data);
		    			JSONObject json = new JSONObject(data);
		    			if (json.has("r")) {
		    				processBody(json.getJSONObject("r"));
		    			} else if (json.has("sr")) {
		    				processStatusReport(json.getJSONObject("sr"));
		    			}
		    			//PluginResult result = new PluginResult(PluginResult.Status.OK, jsonStr);
		                //result.setKeepCallback(true);
		                //dataAvailableCallback.sendPluginResult(result);
		    			if(mMessage!=null&&!mMessage.equals("")) {
		    				// TODO: SEND MESSAGE(JSON) BACK TO UI or not...
		    			}
		    		} catch(Exception e) {
		    			if(e.getMessage()!=null)
		    				Log.d(TAG, "GAHHH!");
		    				Log.e(TAG, e.getMessage());
		    				buffer.setLength(0);
		    				
		    				//try {
								//mApi.write("{\"sr\":\"\"}\n".getBytes());
							//} catch (RemoteException e1) {
								// TODO Auto-generated catch block
							//	e1.printStackTrace();
							//}
		    			
		    		}
		    		
	            }
			   //Log.d(TAG, "status = "+status);
			   if(status==3){
				   if(!lockdown||checkPs){//||checkGCs){
					   lockdown = true;
					   if(whackattack.size()>0) {
						   try {
							   String round = whackattack.take();
							   Location tug = lackattack.take();
							   GPS where = gpsattack.take();
							   Bundle b = new Bundle();
							   b.putInt("what", 6);
							   b.putDouble("current", where.p_current);
							   b.putDouble("total", where.p_total);
							   bundleAll(b);
							   bosx = tug.x;
							   bosy = tug.y;
							   bosz = tug.z;
							   JSONObject tj;
								try {
									tj = new JSONObject(round);
									gc = tj.getString("gc");
								} catch (JSONException e) {
									e.printStackTrace();
								}
							   checkGCs = true;
							   Log.d(TAG, "checkGCs! gc=round="+gc);
							   while(paused){	/* NOOP */	}
							   
							   if(kill){
								   Log.d(TAG, "running -> false & then return");
								   running = false;
								   return;
							   }else{
								   mApi.write(round.getBytes());
							   }
						   } catch (InterruptedException e) {
							   // TODO Auto-generated catch block
							   e.printStackTrace();
						   } catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						   }
						   
					   } else {
					   		Log.d(TAG, "before pee = "+pee);
						    if(!(pee>=mProtocol.length())){
						    	lockdown = true;
							    JSONObject jsahn = null;
							    // pee here????
								try {
									jsahn = mProtocol.getJSONObject(pee++);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								rowcount++;
								commandSetup(jsahn);
								boomerang();
							} else {
								if(!endo) {
									endSequence();
								} else {
									if(whackfinish.size()>0) {
										String finisher;
										try {
											finisher = whackfinish.take();
											Location bug = lackattack.take();
											bosx = bug.x;
											bosy = bug.y;
											bosz = bug.z;
											JSONObject tj;
											try {
												tj = new JSONObject(finisher);
												gc = tj.getString("gc");
											} catch (JSONException e) {
												e.printStackTrace();
											}
											checkGCs = true;
											Log.d(TAG, "checkGCs! gc=finisher="+gc);
											while(paused){	/* NOOP	*/	}
											mApi.write(finisher.getBytes());
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										} catch (RemoteException e) {
											e.printStackTrace();
										}
									} else {
										Log.d(TAG, "running and oscCalled -> false");
										running = false;
										oscCalled = false;
									}
								}
							}
						   	
					   }
				   }
				   
			   }
		   }
	   }
	   
	   private String readUntil(String c) {
		   String data = "";
		   try{
			   int index = buffer.indexOf(c, 0);
			   if (index > -1) {
				   data = buffer.substring(0, index + c.length());
				   buffer.delete(0, index + c.length());
			   }
		   }catch(Exception e){
			   buffer.delete(0,buffer.length());
			   Log.d(TAG, "WTF!");
			   try {
				mApi.write("{\"sr\":\"\"}\n".getBytes());
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   }
		   return data;
	   }
	   
	   private synchronized String processBody(JSONObject json) throws JSONException {
		   Log.d(TAG, "processBody called");
		   String result = "";
		   Log.d(TAG, "json: "+json.toString());
		   Log.d(TAG, "checkGCs?:"+checkGCs);
		   if(checkGCs){
			   if(json.has("gc")){
				   Log.d(TAG, "json has gc");
				   if(checkGC(json.getString("gc"))) {
					   Log.d(TAG, "gc==gcc");
					   checkGCs = false;
					   checkPs = true;
				   }else{
					   Log.d(TAG, "gc!=gcc");
					   Log.d(TAG, "gc="+gc);
					   Log.d(TAG, "gcc="+json.getString("gc"));
				   }
			   }else{
				   Log.d(TAG, "json has not gc");
			   }
			   
		   }
		   
		   if(json.has("sr"))
			   result = processStatusReport(json.getJSONObject("sr"));
		   return result;
	   }
	   
	   
	   private boolean checkPositions(){
		   if(Math.abs(posx-bosx)<0.001&&Math.abs(posy-bosy)<0.001&&Math.abs(posz-bosz)<0.001) return true;
		   else return false;
	   }
	   
	   private boolean checkGC(String gcc){
		   if(gc.equals(gcc)) return true;
		   else return false;
	   }
	   
	   private synchronized String processStatusReport(JSONObject sr) throws JSONException {
		   Log.d(TAG, "processStatusReport called");
		   String result = "";
		   Log.d(TAG, "sr: "+ sr.toString());
		   
		   if (sr.has("stat")){
			   if(sr.getInt("stat")==3) {
				   status = 3;
			   } else {
				   status = 0;
			   }
			   Log.d(TAG, "status = "+status);
		   }
		   
		   
		   if (sr.has("posx")){
			   posx = sr.getDouble("posx");
		   }
		   if (sr.has("posy")){
			   posy = sr.getDouble("posy");
		   }
		   if (sr.has("posz")){
			   posz = sr.getDouble("posz");
		   }
		   Log.d(TAG, "posx="+String.valueOf(posx)+", posy="+String.valueOf(posy)+", posz="+String.valueOf(posz));
		   Log.d(TAG, "checkPs?"+checkPs);
		   if(checkPs){
			   if(checkPositions()){
				   Log.d(TAG, "positions check out!");
				   checkPs = false;
				   lockdown = false;
			   }else{
				   Log.d(TAG, "positions don't check out");
			   }
		   }
		   
		   
		   
		   return result;
	   }
	   
	   public boolean commandSetup(JSONObject json){
		   
		   int time = 0;
		   double aspirate = 0.0;
		   int grip = 0;
		   boolean blowout = false;
		   boolean droptip = false;
		   String ingredient = "";
		   Bundle b = new Bundle();
		   b.putInt("what", 6);
		   b.putString("job_data", "STARTING A NEW LINE... NO BIGGIE");
		   //bundleAll(b);
		   
		   try {
			   ingredient = json.getString("ingredient");
		   } catch(Exception iex) {
			   return false;
		   }
		   
		   try{
			   time = json.getJSONObject("trigger").getInt("value");
		   }catch(Exception timex){
		   }
		   try{
			   aspirate = json.getJSONObject("action").getDouble("aspirate");
		   }catch(Exception apex){
		   }
		   try{
			   grip = json.getJSONObject("action").getInt("grip");
		   }catch(Exception gex){
		   }
		   try{
			   json.getJSONObject("action").get("blowout");
			   blowout = true;
		   }catch(Exception bex){
		   }
		   try{
			   json.getJSONObject("action").get("droptip");
			   droptip = true;
		   }catch(Exception tex){
			   
		   }
		   Log.d(TAG, "ingredient:"+ingredient);
		   Log.d(TAG, "time:"+time);
		   Log.d(TAG, "aspirate:"+String.valueOf(aspirate));
		   Log.d(TAG, "grip:"+String.valueOf(grip));
		   Log.d(TAG, "blowout:"+String.valueOf(blowout));
		   
		   
		   try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   Log.d(TAG, "proceeding");
		   // 2. Create gcode commands for completing action
		   String cmdStr;
		   if(hIngredients==null)
			   Log.d(TAG, "hIngredients==null!");
		   Location loco = hIngredients.get(ingredient);
		   String xgo = String.valueOf(loco.x);
		   Log.d(TAG, "xgo = " + xgo);
		   String ygo = String.valueOf(loco.y);
		   Log.d(TAG, "ygo = " + ygo);
		   idx++;
		   cmdStr = "{\"gc\":\"N" + idx + "G0X"+xgo+"Y"+ygo+"\"}\n";
		   Location loca = new Location();
		   loca.x = Double.parseDouble(xgo);
		   loca.y = Double.parseDouble(ygo);
		   loca.z = bosz;
		   lackattack.add(loca);
		   whackattack.add(cmdStr);
		   GPS gpsa = new GPS();
		   gpsa.current = rowcount;
		   gpsa.ingrate = ingredient;
		   gpsa.p_current = 0.0;
		   gpsa.p_total = gpsa.p_current/(double)total + ((double)rowcount-1)/(double)total;
		   gpsattack.add(gpsa);
		   
		   String zgo = String.valueOf(loco.z);
		   Log.d(TAG, "zgo = " + zgo);
		   idx++;
		   cmdStr = "{\"gc\":\"N" + idx + "G0Z"+zgo+"\"}\n";
		   Location locb = new Location();
		   locb.x = loca.x;
		   locb.y = loca.y;
		   locb.z = Double.parseDouble(zgo);
		   lackattack.add(locb);
		   whackattack.add(cmdStr);
		   GPS gpsb = new GPS();
		   gpsb.current = gpsa.current;
		   gpsb.ingrate = gpsa.ingrate;
		   gpsb.p_current = 0.1666;
		   gpsb.p_total = gpsb.p_current/(double)total + ((double)rowcount-1)/(double)total;
		   gpsattack.add(gpsb);
		   
		   
		   double d_pipette = (double)pipette;
		   String ago = String.valueOf((aspirate/d_pipette)*16.0);
		   
		   if(Double.parseDouble(ago)>22.0)
			   ago = "22.0";
		   if(Double.parseDouble(ago)<0.0){
			   ago = "0.0";
		   }
		   Location locc = new Location();
		   locc.x = locb.x;
		   locc.y = locb.y;
		   locc.z = locb.z;
		   
		   idx++;
		   cmdStr = "{\"gc\":\"N" + idx + "M5\"}\n";
		   GPS gpsca = new GPS();
		   gpsca.current = gpsca.current;
		   gpsca.ingrate = gpsca.ingrate;
		   gpsca.p_current = 0.25;
		   gpsca.p_total = gpsca.p_current/(double)total + ((double)rowcount-1)/(double)total;
		   gpsattack.add(gpsca);
		   lackattack.add(locc);
		   whackattack.add(cmdStr);
		   idx++;
		   cmdStr = "{\"gc\":\"N" + idx + "G91G0A-" + ago + "\"}\n";
		   GPS gpscb = new GPS();
		   gpscb.current = gpsca.current;
		   gpscb.ingrate = gpsca.ingrate;
		   gpscb.p_current = 0.3333;
		   gpscb.p_total = gpscb.p_current/(double)total + ((double)rowcount-1)/(double)total;
		   gpsattack.add(gpscb);
		   lackattack.add(locc);
		   whackattack.add(cmdStr);
		   
		   
		   Location locd = new Location();
		   locd.x = locc.x;
		   locd.y = locc.y;
		   locd.z = locc.z;
		   Location loce = new Location();
		   loce.x = locd.x;
		   loce.y = locd.y;
		   loce.z = locd.z;
		   
		   /*
		   if(grip==0){
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "M3\"}\n";
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "G90G0A"+String.valueOf(bopen)+"\"}\n";
			   lackattack.add(loce);
			   whackattack.add(cmdStr);
		   }else if(grip==1){
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "M3\"}\n";
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "G90G0A"+String.valueOf(bclose)+"\"}\n";
			   lackattack.add(loce);
			   whackattack.add(cmdStr);
		   }*/
		   
		   
		   
		   if(blowout){
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "M5\"}\n";
			   GPS gpsda = new GPS();
			   gpsda.current = gpsda.current;
			   gpsda.ingrate = gpsda.ingrate;
			   gpsda.p_current = 0.4166;
			   gpsda.p_total = gpsda.p_current/(double)total + ((double)rowcount-1)/(double)total;
			   gpsattack.add(gpsda);
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
			   idx++;
			   GPS gpsdb = new GPS();
			   gpsdb.current = gpsdb.current;
			   gpsdb.ingrate = gpsdb.ingrate;
			   gpsdb.p_current = 0.5;
			   gpsdb.p_total = gpsdb.p_current/(double)total + ((double)rowcount-1)/(double)total;
			   gpsattack.add(gpsdb);
			   cmdStr = "{\"gc\":\"N" + idx + "G90G0A"+String.valueOf(ablow)+"\"}\n";
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
		   }
		   
		   if(droptip){
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "M5\"}\n";
			   GPS gpsea = new GPS();
			   gpsea.current = gpsea.current;
			   gpsea.ingrate = gpsea.ingrate;
			   gpsea.p_current = 0.5555;
			   gpsea.p_total = gpsea.p_current/(double)total + ((double)rowcount-1)/(double)total;
			   gpsattack.add(gpsea);
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "G90G0A"+String.valueOf(dtip)+"\"}\n";
			   GPS gpseb = new GPS();
			   gpseb.current = gpseb.current;
			   gpseb.ingrate = gpseb.ingrate;
			   gpseb.p_current = 0.6111;
			   gpseb.p_total = gpseb.p_current/(double)total + ((double)rowcount-1)/(double)total;
			   gpsattack.add(gpseb);
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
			   idx++;
			   cmdStr = "{\"gc\":\"N" + idx + "G0A"+String.valueOf(ablow)+"\"}\n";
			   GPS gpsec = new GPS();
			   gpsec.current = gpsec.current;
			   gpsec.ingrate = gpsec.ingrate;
			   gpsec.p_current = 0.6666;
			   gpsec.p_total = gpsec.p_current/(double)total + ((double)rowcount-1)/(double)total;
			   gpsattack.add(gpsec);
			   lackattack.add(locd);
			   whackattack.add(cmdStr);
			   idx++;
			   
		   }
		   
		   // Z return to 0 at end of job
		   Location locf = new Location();
		   locf.x = loce.x;
		   locf.y = loce.y;
		   locf.z = loce.z;
		   idx++;
		   cmdStr = "{\"gc\":\"N" + idx + "G90G0Z0\"}\n";
		   GPS gpsf = new GPS();
		   gpsf.current = gpsf.current;
		   gpsf.ingrate = gpsf.ingrate;
		   gpsf.p_current = 0.8333;
		   gpsf.p_total = gpsf.p_current/(double)total + ((double)rowcount-1)/(double)total;
		   gpsattack.add(gpsf);
		   lackattack.add(locf);
		   whackattack.add(cmdStr);
		   
		   // 3. Add them to whackattack queue (throughout above)
		   //try {
			  //mApi.write("{\"sr\":\"\"}".getBytes());
			   lockdown = false;
		   //} catch (RemoteException e) {
			   // TODO Auto-generated catch block
		//	   e.printStackTrace();
		   //}
		   
		   return true;
	   }
	   
	   public void endSequence(){
		   Log.d(TAG, "END SEQUENCE!!!");
		   Location foca = new Location();
		   Location focb = new Location();
		   Location focc = new Location();
		   foca.x = foca.y = foca.z = focb.x = focb.y = focb.z = focc.x = focc.y = focc.z = 0.0;
		   lackattack.add(foca);
		   lackattack.add(focb);
		   lackattack.add(focc);
		   whackfinish.add("{\"gc\":\"G90G0Z0\"}\n");
		   whackfinish.add("{\"gc\":\"G0X0Y0\"}\n");
		   whackfinish.add("{\"gc\":\"G0A0\"}\n");
		   endo = true;
		   rowcount = 0;
		   kill = false;
	   }
	   
   }
   
   
   private class Location{
	   public double x, y, z;
	   public String ingredient;
	   Location(){
		   
	   }	
	   
   }
   
   private class GPS{
	   public double p_total, p_current;
	   public String ingrate;
	   public int current;
	   GPS(){
		   
	   }
   }
   
   private OTBTListenerAlpha.Stub serviceListener = new OTBTListenerAlpha.Stub() {
		@Override
		public void handleUpdate() throws RemoteException {
			//handleLatestResult();
		}
		
		@Override
		public String getUniqueID() throws RemoteException {
			return mUniqueID;
		}
		
		@Override
		public void sendMessage(String data) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void notifySuccess() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void notifyError(String error) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendBundle(Bundle bunt) throws RemoteException {
			int what = bunt.getInt("what");
			switch (what) {
           case MESSAGE_READ:
              buffer.append(bunt.getString("message"));
              
              //if (dataAvailableCallback != null) {
              //    sendDataToSubscriber();
              //}
              /*
              if(jogDataCallback != null){ //nwags
              	sendJogDataToSubscriber();
              }*/
              break;
           case MESSAGE_STATE_CHANGE:
           	int arg1 = bunt.getInt("state");
              if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + arg1);
              switch (arg1) {
                  case OTBTBlueServiceAlpha.STATE_CONNECTED:
                      Log.i(TAG, "BLUE.STATE_CONNECTED");
                      //notifyConnectionSuccess();
                      break;
                  case OTBTBlueServiceAlpha.STATE_CONNECTING:
                      Log.i(TAG, "BLUE.STATE_CONNECTING");
                      break;
                  case OTBTBlueServiceAlpha.STATE_LISTEN:
                      Log.i(TAG, "BLUE.STATE_LISTEN");
                      break;
                  case OTBTBlueServiceAlpha.STATE_NONE:
                      Log.i(TAG, "BLUE.STATE_NONE");
                      break;
              }
              break;
          case MESSAGE_WRITE:
              //  byte[] writeBuf = (byte[]) msg.obj;
              //  String writeMessage = new String(writeBuf);
              //  Log.i(TAG, "Wrote: " + writeMessage);
              break;
          case MESSAGE_DEVICE_NAME:
              Log.i(TAG, bunt.getString("device_name"));
              break;
          case MESSAGE_TOAST:
              String message = bunt.getString("toast"); 
              Log.d(TAG, "toast = "+message);
              //msg.getData().getString(TOAST);
              //notifyConnectionLost(message);
              break;
			}
		}

		
		@Override
		public void shutMeDown() throws RemoteException {
			// NOOP
		}
	};
	
	
	private ServiceConnection serviceConnectionZ = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// that's how we get the client side of the IPC connection
			Log.d(TAG, "onServiceConnected called!");
			mApi = OTBTApiAlpha.Stub.asInterface(service);
			try {
				mApi.addListener(serviceListener);
				
			} catch (RemoteException e) {
				Log.d(TAG, "addListener failed", e);
			}
			
			synchronized(myServiceConnectedLock) {
				mServiceConnected = true;

				myServiceConnectedLock.notify();
			}
			try {
				if(!boomthreading){
					Log.d(TAG, "if(!boomthreading)...a");
					BoomThread boomer = new BoomThread(job);
					/*new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(
								Void... params) {
							//try{
							//	Thread.sleep(1000);
							//}catch(Exception e){
							//	e.printStackTrace();
							//}
							Log.d(TAG, "boom.run() 1");
							boomer.run();
							return null;
						}
						
					}.execute();*/
					//while(!executorService.isTerminated()){
					//	executorService.awaitTermination(1000,TimeUnit.MILLISECONDS);
					//	Log.d(TAG, "waiting for termination");
					//}
					executorService = Executors.newSingleThreadExecutor();
					executorService.execute(boomer);
					//dHandler.postDelayed(boomer, 1000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			synchronized(myServiceConnectedLock) {
				mServiceConnected = false;

				myServiceConnectedLock.notify();
			}
		}
	};

	@Override
	public void handleUpdate(ExecuteResult logicResult, Object[] listenerExtras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeListener(ExecuteResult logicResult, Object[] listenerExtras) {
		// TODO Auto-generated method stub
		
	}
	
	private void bundleAll(Bundle b){
    	for(OTBTListenerAlpha listener: mListeners){
    		try {
				listener.sendBundle(b);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
	
}

