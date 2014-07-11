package com.opentrons.otbtalpha.cordova;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


public class OTBTLogicAlpha implements IUpdateListener{
	
	/*
	 ************************************************************************************************
	 * Static values 
	 ************************************************************************************************
	 */
	public static final String TAG = OTBTLogicAlpha.class.getSimpleName();
	private static final boolean D = true;
	
	// Message types sent from the BluetoothSerialService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_JOB = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	//nwags variable
	public static double posx=0.0;
	public static double posy=0.0;
	public static double posz=0.0;
	public static double posa=0.0;
	public static final double rosa=0.3183099;
	
	public static double xmax=400.0;
	public static double ymax=200.0;
	public static double zmax=200.0;
	public static double amax=24.0;
	public static boolean power=true;
	public static String macAddress;
	public static boolean isBooming=false;
	/*
	 ************************************************************************************************
	 * Keys 
	 ************************************************************************************************
	 */
	
	// actions
    public static final String ACTION_LIST = "list";
    public static final String ACTION_CONNECT = "connect";
    //public static final String CONNECT_INSECURE = "connectInsecure";
    public static final String ACTION_DISCONNECT = "disconnect";
    //public static final String WRITE = "write";
    //public static final String ACTION_AVAILABLE = "available";
    //public static final String READ = "read";
    //public static final String READ_UNTIL = "readUntil";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";
    public static final String ACTION_IS_ENABLED = "isEnabled";
    public static final String ACTION_IS_CONNECTED = "isConnected";
    public static final String ACTION_CLEAR = "clear";
    
    // nwags-actions
    public static final String ACTION_JOG = "jog";
    public static final String ACTION_SET_DIMENSIONS = "setDimensions";
    public static final String ACTION_GET_DIMENSIONS = "getDimensions";
    public static final String ACTION_HOME = "home";
    public static final String ACTION_RUN = "run";
    public static final String ACTION_LOAD = "load";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_LIST_FILES = "listfiles";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_KILL = "kill";
    
    
	// Error codes
	public static final int ERROR_NONE_CODE = 0;
	public static final String ERROR_NONE_MSG = "";
	
	public static final int ERROR_PLUGIN_ACTION_NOT_SUPPORTED_CODE = -1;
	public static final String ERROR_PLUGIN_ACTION_NOT_SUPPORTED_MSG = "Passed action not supported by Plugin";
	
	public static final int ERROR_INIT_NOT_YET_CALLED_CODE = -2;
	public static final String ERROR_INIT_NOT_YET_CALLED_MSG = "Please call init prior any other action";
	
	public static final int ERROR_SERVICE_NOT_RUNNING_CODE = -3;
	public static final String ERROR_SERVICE_NOT_RUNNING_MSG = "Sevice not currently running";
	
	public static final int ERROR_UNABLE_TO_BIND_TO_BACKGROUND_SERVICE_CODE = -4;
	public static final String ERROR_UNABLE_TO_BIND_TO_BACKGROUND_SERVICE_MSG ="Plugin unable to bind to background service";
	
	public static final int ERROR_UNABLE_TO_RETRIEVE_LAST_RESULT_CODE = -5;
	public static final String ERROR_UNABLE_TO_RETRIEVE_LAST_RESULT_MSG = "Unable to retrieve latest result (reason unknown)";
	
	public static final int ERROR_LISTENER_ALREADY_REGISTERED_CODE = -6;
	public static final String ERROR_LISTENER_ALREADY_REGISTERED_MSG = "Listener already registered";
	
	public static final int ERROR_LISTENER_NOT_REGISTERED_CODE = -7;
	public static final String ERROR_LISTENER_NOT_REGISTERED_MSG = "Listener not registered";

	public static final int ERROR_UNABLE_TO_CLOSED_LISTENER_CODE = -8;
	public static final String ERROR_UNABLE_TO_CLOSED_LISTENER_MSG = "Unable to close listener";
	
	public static final int ERROR_ACTION_NOT_SUPPORTED__IN_PLUGIN_VERSION_CODE = -9;
	public static final String ERROR_ACTION_NOT_SUPPORTED__IN_PLUGIN_VERSION_MSG = "Action is not supported in this version of the plugin";

	public static final int ERROR_EXCEPTION_CODE = -99;
	
	public static final String blueName = OTBTBlueServiceAlpha.class.getName();
	public static final String redName = OTBTServiceAlpha.class.getName();
	public static ServiceDetails blueService;
	//public boolean testee = false;
	/*
	 ************************************************************************************************
	 * Fields 
	 ************************************************************************************************
	 */
	private Context mContext;
	private Hashtable<String, ServiceDetails> mServices = new Hashtable<String, ServiceDetails>();

	
	// callbacks
    private CallbackContext connectCallback;
    private CallbackContext dataAvailableCallback;
    
    //nwags-callbacks
    //private CallbackContext jogDataCallback;
    private BluetoothAdapter bluetoothAdapter;
    //private OTBTWorkerAlpha otbtworker;
    
    StringBuffer buffer = new StringBuffer();
	
	
	/*
	 ************************************************************************************************
	 * Constructors 
	 ************************************************************************************************
	 */
	// Part fix for https://github.com/Red-Folder/Cordova-Plugin-BackgroundService/issues/19
	//public BackgroundServicePluginLogic() {
	//}
    
	public OTBTLogicAlpha(Context context) {
		this.mContext = context;
		
		try{
			//String blueName = OTBTBlueServiceAlpha.class.getName();//"BLUE";
			
			Log.d(TAG, "Finding servicename " + blueName);
			
			ServiceDetails service = null;
			Log.d(TAG, "Services contains " + this.mServices.size() + " records");
			
			if(this.mServices.containsKey(blueName)) {
				Log.d(TAG, "Found existing ServiceDetails");
				service = this.mServices.get(blueName);
			} else {
				Log.d(TAG, "Creating new ServiceDetails");
				service = new ServiceDetails(this.mContext, blueName);
				this.mServices.put(blueName, service);
				blueService = service;
			}
			
			if(!service.isInitialised()){
				Log.d(TAG, "blueService not yet initialised, initialising...");
				service.initialise();
			}
			// TODO Start service and all that jazz
			Log.d(TAG, "blueService.startService()");
			service.startService();//.startBooming(args, listener, listenerExtras);
			//result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			
		}catch(Exception ex){
			Log.d(TAG, "starting BLUE failed", ex);
			//result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		
	}

	/*
	 ************************************************************************************************
	 * Public Methods 
	 ************************************************************************************************
	 */
	
	public boolean isActionValid(String action) {
		boolean result = false;
		
		if( ACTION_LIST.equals(action)) result = true;
	    if( ACTION_CONNECT.equals(action)) result = true;
	    		
	    if( ACTION_DISCONNECT.equals(action)) result = true;
	    
	    //if( ACTION_AVAILABLE.equals(action)) result = true;
	    
	    if( ACTION_SUBSCRIBE.equals(action)) result = true;
	    if( ACTION_UNSUBSCRIBE.equals(action)) result = true;
	    if( ACTION_IS_ENABLED.equals(action)) result = true;
	    if( ACTION_IS_CONNECTED.equals(action)) result = true;
	    if( ACTION_CLEAR.equals(action)) result = true;
	    
	    //nwags-actions
	    if( ACTION_JOG.equals(action)) result = true;
	    if( ACTION_SET_DIMENSIONS.equals(action)) result = true;
	    if( ACTION_GET_DIMENSIONS.equals(action)) result = true;
	    if( ACTION_HOME.equals(action)) result = true;
	    if( ACTION_RUN.equals(action)) result = true;
	    if( ACTION_LOAD.equals(action)) result = true;
	    if( ACTION_SAVE.equals(action)) result = true;
	    if( ACTION_LIST_FILES.equals(action)) result = true;
	    if( ACTION_STOP.equals(action)) result = true;
		if( ACTION_PAUSE.equals(action)) result = true;
		if( ACTION_RESUME.equals(action)) result = true;
		if( ACTION_KILL.equals(action)) result = true;
	    
	    return result;
	}
	
	public ExecuteResult execute(String action, CordovaArgs args) {
		return execute(action, args, null, null);
	}
	
	public ExecuteResult execute(String action, CordovaArgs args, IUpdateListener listener, Object[] listenerExtras) {
		ExecuteResult result = null;
		
		Log.d(TAG, "Start of Execute");
		try{
			Log.d(TAG, "Within try block");
			if((args != null) &&
					(!args.isNull(0)) &&
					(args.get(0) instanceof String) &&
					(args.getString(0).length() > 0)) {
				
				/*
				String serviceName = data.getString(0);
				
				Log.d(TAG, "Finding servicename " + serviceName);
				
				ServiceDetails service = null;
				
				Log.d(TAG, "Services contains " + this.mServices.size() + " records");
				
				if (this.mServices.containsKey(serviceName)) {
					Log.d(TAG, "Found existing Service Details");
					service = this.mServices.get(serviceName);
				} else {
					Log.d(TAG, "Creating new Service Details");
					service = new ServiceDetails(this.mContext, serviceName);
					this.mServices.put(serviceName, service);
				}
				
				*/
			}
			Log.d(TAG, "Action = " + action);
			
			if (bluetoothAdapter == null) {
	            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        }

//	        if (otbtworker == null) {
//	            otbtworker = new OTBTWorkerAlpha(mHandler);
//	        }
			
			if(ACTION_LIST.equals(action)){
				//if(bluetoothAdapter==null){
		        //	Toast.makeText(this.mContext.getApplicationContext(), "Bluetooth not found, please make sure it is turned on. You may need to restart the app.", Toast.LENGTH_LONG).show();
		        //}
				result = listBondedDevices(listenerExtras);
				
			} else if(ACTION_CONNECT.equals(action)){
				
				boolean secure = true;
				result = connect(args, secure, listenerExtras);
				
				
			} else if(ACTION_DISCONNECT.equals(action)){
				
				//connectCallback = null;
				result = disconnect(listenerExtras);
				//obtworker.stop();
				//((CallbackContext)listenerExtras[0]).success();
				
			} else if(ACTION_SUBSCRIBE.equals(action)){
				
				result = subscribe(listenerExtras);
				//delimiter = args.getString(0);
				//dataAvailableCallback = (CallbackContext) listenerExtras[0];
				
			} else if(ACTION_UNSUBSCRIBE.equals(action)){
				
				result = unsubscribe(listenerExtras);
				//PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
	            //pluginResult.setKeepCallback(true);
	            //((CallbackContext)listenerExtras[0]).sendPluginResult(pluginResult);
				
				
			} else if(ACTION_IS_ENABLED.equals(action)){
				
				result = isenabled(listenerExtras);
				
			} else if(ACTION_IS_CONNECTED.equals(action)){
				
				result = isconnected(listenerExtras);
				
			} else if(ACTION_CLEAR.equals(action)){
				
				result = clear(listenerExtras);
				
			} else if(ACTION_JOG.equals(action)){
				
				result = jog(args, listenerExtras);
				
			} else if(ACTION_SET_DIMENSIONS.equals(action)){
				
				result = setdimensions(args, listenerExtras);
				
			} else if(ACTION_GET_DIMENSIONS.equals(action)){
				
				result = getdimensions(listenerExtras);
				
			} else if(ACTION_HOME.equals(action)){
				
				result = home(listenerExtras);
				
			} else if(ACTION_RUN.equals(action)){
				
				result = run(args, listener, listenerExtras);
				
			} else if(ACTION_LOAD.equals(action)){
				
				result = load(args, listener, listenerExtras);
				
			} else if(ACTION_SAVE.equals(action)){
				
				result = save(args, listener, listenerExtras);
				
			} else if(ACTION_LIST_FILES.equals(action)){
				
				result = listfiles(args, listener, listenerExtras);
				
			} else if(ACTION_STOP.equals(action)){
				
				result = stop(args, listener, listenerExtras);
				
			} else if(ACTION_PAUSE.equals(action)){
				
				result = pause(args, listener, listenerExtras);
				
			} else if(ACTION_RESUME.equals(action)){
				
				result = resume(args, listener, listenerExtras);
				
			} else if(ACTION_KILL.equals(action)){
				
				result = kill(args, listener, listenerExtras);
				
			}
			
			
			
			
			
		} catch (Exception ex) {
			result = new ExecuteResult(ExecuteStatus.ERROR);
			Log.d(TAG, "Exception - " + ex.getMessage());
		}
		
		return result;
	}
	
	public void onDestroy() {
		Log.d(TAG, "onDestroy Start");
		try{
			Log.d(TAG, "Checking for services");
			if(this.mServices != null &&
					this.mServices.size() > 0) {
				
				Log.d(TAG, "Found services");
				
				Enumeration<String> keys = this.mServices.keys();
				
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					ServiceDetails service = this.mServices.get(key);
					Log.d(TAG, "Calling service.close()");
					service.close();
				}
			}
		}catch(Throwable t){
			Log.d(TAG, "Error has occurred while trying to close services", t);
		}
		
		this.mServices = null;
		Log.d(TAG, "onDestroy Finish");
		
	}
	
	public ExecuteResult listBondedDevices(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			JSONArray deviceList = new JSONArray();
			Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
			
			for (BluetoothDevice device : bondedDevices) {
	        	//if(device.getName().toLowerCase().contains("tron")){
		            JSONObject json = new JSONObject();
		            json.put("name", device.getName());
		            json.put("address", device.getAddress());
		            json.put("id", device.getAddress());
		            if (device.getBluetoothClass() != null) {
		                json.put("class", device.getBluetoothClass().getDeviceClass());
		            }
		            deviceList.put(json);
	        	//}
	        }
			//if(deviceList.length()<1)
			//	Toast.makeText(this.mContext, "No devices found. Make sure you have paired your device.", Toast.LENGTH_SHORT).show();
			
	        ((CallbackContext) listenerExtras[0]).success(deviceList);
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "listBondedDevices failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult connect(CordovaArgs args, boolean secure, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			macAddress = args.getString(0);
			
			try{
				Log.d(TAG, "getting BLUE service");
				if(this.mServices != null &&
						this.mServices.size() > 0){
					Log.d(TAG, "Found services");
					
					ServiceDetails service = this.mServices.get(blueName);
					Log.d(TAG, "Calling BLUE service");
					service.connect(macAddress);
					connectCallback = ((CallbackContext) listenerExtras[0]);
				}
					
			}catch(Throwable t){
				Log.d(TAG, "Error occurred while getting BLUE, stop holding your breath", t);
			}
			
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            ((CallbackContext) listenerExtras[0]).sendPluginResult(pluginResult);

			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "connect failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	// The Handler that gets information back from the BluetoothSerialService
    // Original code used handler for the because it was talking to the UI.
    // Consider replacing with normal callbacks
    private final Handler mHandler = new Handler() {

         public void handleMessage(Message msg) {
             switch (msg.what) {
                 case MESSAGE_READ:
                    buffer.append((String)msg.obj);
                    
                    if (dataAvailableCallback != null) {
                        sendDataToSubscriber();
                    }
                    /*
                    if(jogDataCallback != null){ //nwags
                    	sendJogDataToSubscriber();
                    }*/
                    break;
                 case MESSAGE_STATE_CHANGE:

                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case OTBTBlueServiceAlpha.STATE_CONNECTED:
                            Log.i(TAG, "BluetoothSerialService.STATE_CONNECTED");
                            notifyConnectionSuccess();
                            break;
                        case OTBTBlueServiceAlpha.STATE_CONNECTING:
                            Log.i(TAG, "BluetoothSerialService.STATE_CONNECTING");
                            break;
                        case OTBTBlueServiceAlpha.STATE_LISTEN:
                            Log.i(TAG, "BluetoothSerialService.STATE_LISTEN");
                            break;
                        case OTBTBlueServiceAlpha.STATE_NONE:
                            Log.i(TAG, "BluetoothSerialService.STATE_NONE");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //  byte[] writeBuf = (byte[]) msg.obj;
                    //  String writeMessage = new String(writeBuf);
                    //  Log.i(TAG, "Wrote: " + writeMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    Log.i(TAG, msg.getData().getString(DEVICE_NAME));
                    break;
                case MESSAGE_TOAST:
                    String message = msg.getData().getString(TOAST);
                    notifyConnectionLost(message);
                    break;
             }
         }
    };

    private void notifyConnectionLost(String error) {
        if (connectCallback != null) {
            connectCallback.error(error);
            connectCallback = null;
        }
    }

    private void notifyConnectionSuccess() {
        if (connectCallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            connectCallback.sendPluginResult(result);
        }
    }
    
    private void sendDataToSubscriber() {
    	synchronized(this){
	    	LOG.d(TAG, "sendDataToSubscriber called");
	    	String data = readUntil("\n");
	    	String jsonStr = "";
	    	if(data != null && data.length() > 0){
	    		try {
	    			Log.d(TAG, "data read = "+data);
	    			JSONObject json = new JSONObject(data);
	    			if (json.has("r")) {
	    				jsonStr = processBody(json.getJSONObject("r"));
	    			}else if (json.has("sr")) {
	    				jsonStr = processStatusReport(json.getJSONObject("sr"));
	    			}
	    			PluginResult result = new PluginResult(PluginResult.Status.OK, jsonStr);
	                result.setKeepCallback(true);
	                dataAvailableCallback.sendPluginResult(result);
	
	    		} catch(Exception e){
	    			if(e.getMessage()!=null)
	    				Log.e(TAG, e.getMessage());
	    			
	    		}
	    		
	    		
	            sendDataToSubscriber();
            }
    	}
    	/*
    	
        String data = readUntil(delimiter);
        if (data != null && data.length() > 0) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            dataAvailableCallback.sendPluginResult(result);

            sendDataToSubscriber();
        }*/
    }
    
    private String readUntil(String c) {
        String data = "";
        int index = buffer.indexOf(c, 0);
        if (index > -1) {
            data = buffer.substring(0, index + c.length());
            buffer.delete(0, index + c.length());
        }
        return data;
    }
	
    private String processBody(JSONObject json) throws JSONException {
    	Log.d(TAG, "processBody called");
    	String result = "";
    	if(json.has("sr"))
    		result = processStatusReport(json.getJSONObject("sr"));
    	return result;
    }
    
    private String processStatusReport(JSONObject sr) throws JSONException{
    	Log.d(TAG, "processStatusReport called");
    	String result = "";
    	JSONObject jResult = new JSONObject();
    	if (sr.has("posx")){
    		posx = sr.getDouble("posx");
    		jResult.put("x", posx);
    	}
    	if (sr.has("posy")){
			posy = sr.getDouble("posy");
			jResult.put("y", posy);
    	}
    	if (sr.has("posz")){
			posz = sr.getDouble("posz");
			jResult.put("z", posz);
    	}
    	if (sr.has("posa")){
			double t_posa = sr.getDouble("posa");
			Log.d(TAG, "t_posa = "+String.valueOf(t_posa));
			posa = (2.0*Math.PI*t_posa*rosa)/360.0;
			Log.d(TAG, "posa = "+String.valueOf(posa));
			jResult.put("a", posa);
    	}
    	if (sr.has("stat")){
			switch (sr.getInt("stat")){
			case 0:
				jResult.put("listening", 0);
				break;
			case 1:
				jResult.put("listening", 1);
				break;
			case 2:
				jResult.put("listening", 0);
				break;
			case 3:
				jResult.put("listening", 1);
				break;
			case 4:
				jResult.put("listening", 0);
				break;
			case 5:
				jResult.put("listening", 0);
				break;
			case 6:
				jResult.put("listening", 0);
				break;
			case 7:
				jResult.put("listening", 0);
				break;
			case 8:
				jResult.put("listening", 0);
				break;
			case 9:
				jResult.put("listening", 0);
				break;
			}
		}
		result = jResult.toString();
		LOG.d(TAG, "result: "+result);
    	return result;
    }
    
	public ExecuteResult disconnect(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			blueService.disconnect();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "disconnect failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult subscribe(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			//delimiter = args.getString(0);
			dataAvailableCallback = ((CallbackContext)listenerExtras[0]);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			((CallbackContext)listenerExtras[0]).sendPluginResult(pluginResult);
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			result.setFinished(false);
		}catch(Exception ex){
			Log.d(TAG, "subscribe failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult unsubscribe(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			//delimiter = null;
			dataAvailableCallback = null;
			((CallbackContext)listenerExtras[0]).success();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "unsubscribe failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult isenabled(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			if(bluetoothAdapter.isEnabled()){
				((CallbackContext)listenerExtras[0]).success();
			} else {
				((CallbackContext)listenerExtras[0]).error("Bluetooth is disabled.");
			}
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "isenabled failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult isconnected(Object[] listenerExtras){
		return blueService.getState(listenerExtras);
	}
	
	public ExecuteResult clear(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			buffer.setLength(0);
			((CallbackContext)listenerExtras[0]).success();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "clear failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult jog(CordovaArgs args, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			String args_String = args.getString(0);
			args_String.trim();
			JSONObject json = new JSONObject(args_String);
			Log.d(TAG, "json: "+json.toString());
			
			if(json.has("reset")) {
	    		LOG.d(TAG, "reset");
	    		Log.d(TAG, "reset");
	    		byte[] rst = {0x18};
	    		blueService.write(rst);
	    		((CallbackContext)listenerExtras[0]).success();
	    	} else if(json.has("stop")) {
	    		LOG.d(TAG, "stop");
	    		Log.d(TAG, "stop");
	    		blueService.write("!%\n".getBytes());
	    		((CallbackContext)listenerExtras[0]).success();
	    	} else if(json.has("power")) {
	    		LOG.d(TAG, "power");
	    		Log.d(TAG, "power");
	    		if(json.getBoolean("power")){
	    			blueService.write("$me\n".getBytes());
	    			blueService.write("{\"1pm\":\"1\"}\n".getBytes());
	    			blueService.write("{\"2pm\":\"1\"}\n".getBytes());
	    			blueService.write("{\"3pm\":\"1\"}\n".getBytes());
	    			blueService.write("{\"4pm\":\"1\"}\n".getBytes());
	    			power = true;
	    			((CallbackContext)listenerExtras[0]).success();
	                
	    		}else{
	    			blueService.write("$md\n".getBytes());
	    			power = false;
	    			((CallbackContext)listenerExtras[0]).success();
	    		}
	    	}
	    	
	    	String gogoStr = "";
	    	String gocode = "{\"gc\":\"G90G0";//\"}\n";
	    	if(json.has("x")||json.has("X")) {
	    		Log.d(TAG, "has x");
	    		Log.d(TAG, "and x="+json.getDouble("x"));
	    		double gox = json.getDouble("x");//*xmax;
	    		gogoStr = "x"+String.valueOf(gox);
	    		gocode+=gogoStr;
	    	}
	    	if(json.has("y")) {
	    		Log.d(TAG, "has y");
	    		Log.d(TAG, "and y="+json.getDouble("y"));
	    		double goy = json.getDouble("y");//*ymax;
	    		gogoStr = "y"+String.valueOf(goy);
	    		gocode+=gogoStr;
	    	}
	    	if(json.has("z")) {
	    		double goz = json.getDouble("z");//*zmax;
	    		gogoStr = "z"+String.valueOf(goz);
	    		gocode+=gogoStr;
	    	}
	    	if(json.has("a")) {
	    		blueService.write("{\"gc\":\"M5\"}\n".getBytes());
	    		double goa = json.getDouble("a");
	    		gogoStr = "a"+String.valueOf(goa);
	    		gocode+=gogoStr;
	    	}
	    	gocode+="\"}\n";
	    	Log.d(TAG, "gcode = "+gocode);
	    	blueService.write(gocode.getBytes());
	        ((CallbackContext)listenerExtras[0]).success();
			
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "jog failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult setdimensions(CordovaArgs args, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			JSONObject jsonObj = args.getJSONObject(0);
	    	
	    	if(jsonObj.has("xmax")) {
	    		xmax = jsonObj.getDouble("xmax");
	    	}
	    	if(jsonObj.has("ymax")) {
	    		ymax = jsonObj.getDouble("ymax");
	    	}
	    	if(jsonObj.has("zmax")) {
	    		zmax = jsonObj.getDouble("zmax");
	    	}
	    	if(jsonObj.has("amax")) {
	    		amax = jsonObj.getDouble("amax");
	    	}
	    	((CallbackContext)listenerExtras[0]).success();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "setdimensions failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult getdimensions(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			JSONObject json = new JSONObject();
	    	json.put("xmax", xmax);
	    	json.put("ymax", ymax);
	    	json.put("zmax", zmax);
	    	json.put("amax", amax);
	    	((CallbackContext)listenerExtras[0]).success(json);
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "getdimensions failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult home(Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			byte[] rst = {0x18};
			blueService.write(rst);
			final HomeStarRunner homey = new HomeStarRunner();
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(
						Void... params) {
					try{
						Thread.sleep(4000);
					}catch(Exception e){
						e.printStackTrace();
					}
					homey.run();
					return null;
				}
				
			}.execute();
	    	((CallbackContext)listenerExtras[0]).success();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "home failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult run(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		if(isBooming){
			Log.d(TAG, "already booming");
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, "already booming"));
			return result;
		}
		if(args==null){
			Log.d(TAG, "run failed, args null");
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, "null arg"));
			return result;
		}
		try{
			isBooming = true;
			Log.d(TAG, "isBooming -> true");
			String serviceName = OTBTServiceAlpha.class.getName();//"BOOM";
			
			Log.d(TAG, "Finding servicename " + serviceName);
			
			ServiceDetails service = null;
			Log.d(TAG, "Services contains " + this.mServices.size() + " records");
			
			if(this.mServices.containsKey(serviceName)) {
				Log.d(TAG, "Found existing ServiceDetails");
				service = this.mServices.get(serviceName);
			} else {
				Log.d(TAG, "Creating new ServiceDetails");
				service = new ServiceDetails(this.mContext, serviceName);
				this.mServices.put(serviceName, service);
			}
			
			if(!service.isInitialised())
				service.initialise();
			// TODO Start service and all that jazz
			service.startBooming(args, listener, listenerExtras);
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			
		}catch(Exception ex){
			Log.d(TAG, "isBooming -> false");
			isBooming = false;
			Log.d(TAG, "run failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult load(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			String bases = Environment.getExternalStorageDirectory().getPath() + "/OpenTrons";
			String name = args.getString(0);
			File storagePath = new File(Environment.getExternalStorageDirectory().getPath() + "/OpenTrons");
		    if(!storagePath.exists())
		    	storagePath.mkdirs();
		    File[] files = storagePath.listFiles();
		    Log.d(TAG, "FILE name = "+name);
		    int i = 0;
		    for(File file : files){
		    	Log.d(TAG, "file["+(i++)+"] = "+file.getName());
		    	if(file.getName().equals(name)){
		    		try{
		    			BufferedReader br = null;
		    			br = new BufferedReader(new FileReader(bases+"/"+file.getName()));
		    			StringBuilder sb = new StringBuilder();
		    			String line = "";
		    			while((line=br.readLine())!=null){
		    				sb.append(line);
		    			}
		    			br.close();
		    			JSONObject andy = new JSONObject(sb.toString());
		    			Log.d(TAG, "andy = "+andy.toString());
		    			((CallbackContext)listenerExtras[0]).success(andy);
		    			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		    			return result;
		    		}catch(Exception exx){
		    			Log.d(TAG, "load failed", exx);
		    			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, exx.getMessage()));
		    		}
		    	}
		    }
		    result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, "filename not found" ));
		}catch(Exception ex){
			Log.d(TAG, "load failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult save(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			String sp = Environment.getExternalStorageDirectory().getPath() + "/OpenTrons";
			File storagePath = new  File(Environment.getExternalStorageDirectory().getPath() + "/OpenTrons");
		    if(!storagePath.exists())
		    	storagePath.mkdirs();
		    String name = args.getString(0);//json.getString("name");
		    String job = args.getString(1);
		    String fileString = storagePath+"/"+name+".json";
		    Log.d(TAG, "trying to save here: "+fileString);
		    File file = new File(fileString);
		    file.createNewFile();
		    FileOutputStream outputStream;
		    try{
			    PrintWriter writer = new PrintWriter(file);
			    writer.print("");
			    writer.close();
			    
			    outputStream = new FileOutputStream(file, false);
			    outputStream.write(job.getBytes());
			    outputStream.close();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		    ((CallbackContext)listenerExtras[0]).success();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			
		}catch(Exception ex){
			Log.d(TAG, "save", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult listfiles(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			File storagePath = new File(Environment.getExternalStorageDirectory().getPath() + "/OpenTrons");
		    if(!storagePath.exists())
		    	storagePath.mkdirs();
		    File[] files = storagePath.listFiles();
		    if(files!=null){
			    StringBuilder sb = new StringBuilder();
			    for(File file : files){
			    	if(!file.isDirectory()&&file.getName().endsWith(".json")){
			    		if(sb.length()>0)
			    			sb.append(",");
			    		sb.append(file.getName());
			    	}
			    }
			    ((CallbackContext)listenerExtras[0]).success(sb.toString());
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		    }
		    Log.d(TAG, "listfiles failed");
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, "no files"));
		}catch(Exception ex){
			Log.d(TAG, "listfiles failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	
	
	public ExecuteResult stop(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			blueService.write("!%".getBytes());
			((CallbackContext)listenerExtras[0]).success();
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
		}catch(Exception ex){
			Log.d(TAG, "stop failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult pause(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			try{
				String serviceName = OTBTServiceAlpha.class.getName();//"BOOM";
				
				Log.d(TAG, "Finding servicename " + serviceName);
				
				ServiceDetails service = null;
				Log.d(TAG, "Services contains " + this.mServices.size() + " records");
				
				if(this.mServices.containsKey(serviceName)) {
					Log.d(TAG, "Found existing ServiceDetails");
					service = this.mServices.get(serviceName);
				} else {
					Log.d(TAG, "Creating new ServiceDetails");
					service = new ServiceDetails(this.mContext, serviceName);
					this.mServices.put(serviceName, service);
				}
				
				if(!service.isInitialised())
					service.initialise();
				
				service.pause(args, listener, listenerExtras);
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				
			}catch(Exception ex){
				Log.d(TAG, "run failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
		}catch(Exception ex){
			Log.d(TAG, "pause failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult resume(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			try{
				String serviceName = OTBTServiceAlpha.class.getName();//"BOOM";
				
				Log.d(TAG, "Finding servicename " + serviceName);
				
				ServiceDetails service = null;
				Log.d(TAG, "Services contains " + this.mServices.size() + " records");
				
				if(this.mServices.containsKey(serviceName)) {
					Log.d(TAG, "Found existing ServiceDetails");
					service = this.mServices.get(serviceName);
				} else {
					Log.d(TAG, "Creating new ServiceDetails");
					service = new ServiceDetails(this.mContext, serviceName);
					this.mServices.put(serviceName, service);
				}
				
				if(!service.isInitialised())
					service.initialise();
				
				service.resume(args, listener, listenerExtras);
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				
			}catch(Exception ex){
				Log.d(TAG, "run failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
		}catch(Exception ex){
			Log.d(TAG, "resume failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	public ExecuteResult kill(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
		ExecuteResult result = null;
		try{
			try{
				String serviceName = OTBTServiceAlpha.class.getName();//"BOOM";
				
				Log.d(TAG, "Finding servicename " + serviceName);
				
				ServiceDetails service = null;
				Log.d(TAG, "Services contains " + this.mServices.size() + " records");
				
				if(this.mServices.containsKey(serviceName)) {
					Log.d(TAG, "Found existing ServiceDetails");
					service = this.mServices.get(serviceName);
				} else {
					Log.d(TAG, "Creating new ServiceDetails");
					service = new ServiceDetails(this.mContext, serviceName);
					this.mServices.put(serviceName, service);
				}
				
				if(!service.isInitialised())
					service.initialise();
				
				service.kill(args, listener, listenerExtras);
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				
			}catch(Exception ex){
				Log.d(TAG, "run failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
		}catch(Exception ex){
			Log.d(TAG, "kill failed", ex);
			result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
		}
		return result;
	}
	
	private JSONObject createJSONResult(Boolean success, int errorCode, String errorMessage) {
		JSONObject result = new JSONObject();

		// Append the basic information
		try {
			result.put("Success", success);
			result.put("ErrorCode", errorCode);
			result.put("ErrorMessage", errorMessage);
		} catch (JSONException e) {
			Log.d(TAG, "Adding basic info to JSONObject failed", e);
		}
		/*
		if (this.mServiceConnected != null && this.mServiceConnected && this.isServiceRunning()) {
			try { result.put("ServiceRunning", true); } catch (Exception ex) {Log.d(LOCALTAG, "Adding ServiceRunning to JSONObject failed", ex);};
			try { result.put("TimerEnabled", isTimerEnabled()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerEnabled to JSONObject failed", ex);};
			try { result.put("Configuration", getConfiguration()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding Configuration to JSONObject failed", ex);};
			try { result.put("LatestResult", getLatestResult()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding LatestResult to JSONObject failed", ex);};
			try { result.put("TimerMilliseconds", getTimerMilliseconds()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerMilliseconds to JSONObject failed", ex);};
		} else {
			try { result.put("ServiceRunning", false); } catch (Exception ex) {Log.d(LOCALTAG, "Adding ServiceRunning to JSONObject failed", ex);};
			try { result.put("TimerEnabled", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerEnabled to JSONObject failed", ex);};
			try { result.put("Configuration", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding Configuration to JSONObject failed", ex);};
			try { result.put("LatestResult", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding LatestResult to JSONObject failed", ex);};
			try { result.put("TimerMilliseconds", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerMilliseconds to JSONObject failed", ex);};
		}
		
		try { result.put("RegisteredForBootStart", isRegisteredForBootStart()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding RegisteredForBootStart to JSONObject failed", ex);};
		try { result.put("RegisteredForUpdates", isRegisteredForUpdates()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding RegisteredForUpdates to JSONObject failed", ex);};
		*/	
		return result;
	}
	
	/*
	 ************************************************************************************************
	 * Internal Class 
	 ************************************************************************************************
	 */
	protected class ServiceDetails {
		/*
		 ************************************************************************************************
		 * Static values 
		 ************************************************************************************************
		 */
		public final String LOCALTAG = OTBTLogicAlpha.ServiceDetails.class.getSimpleName();
		
		/*
		 ************************************************************************************************
		 * Fields 
		 ************************************************************************************************
		 */
		private String mServiceName = "";
		private Context mContext;
		
		private OTBTApiAlpha mApi;
		
		private String mUniqueID = java.util.UUID.randomUUID().toString();
		
		private boolean mInitialised = false;
		
		private Intent mService = null;
		
		private Object mServiceConnectedLock = new Object();
		private Boolean mServiceConnected = null;
		
		private IUpdateListener mListener = null;
		private Object[] mListenerExtras = null;
		
		private boolean currentlyBooming = false;
				
		/*
		 ************************************************************************************************
		 * Constructors 
		 ************************************************************************************************
		 */
		public ServiceDetails(Context context, String serviceName)
		{
			this.mContext = context;
			this.mServiceName = serviceName;
		}
		
		public ExecuteResult connect(String macAddress) {
			Log.d(LOCALTAG, "Starting connect(String macAddress)");
			ExecuteResult result = null;
			
			try{
				Log.d(LOCALTAG, "Attempting to connect to "+macAddress);
				if(mApi==null){
					Log.d(LOCALTAG, "mApi is null... not good");
				}
				mApi.connect(macAddress);
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (Exception ex) {
				Log.d(LOCALTAG, "startBooming failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			Log.d(LOCALTAG, "Finished startBooming");
			return result;
		}

		/*
		 ************************************************************************************************
		 * Public Methods 
		 ************************************************************************************************
		 */
		public void initialise()
		{
			this.mInitialised = true;
			
			// If the service is running, then automatically bind to it
			if (this.isServiceRunning()) {
				startService();
			}
		}
		
		public boolean isInitialised()
		{
			return mInitialised;
		}

		public ExecuteResult startService()
		{
			Log.d(LOCALTAG, "Starting startService");
			ExecuteResult result = null;
			
			try {
				Log.d(LOCALTAG, "Attempting to bind to Service");
				if (this.bindToService()) {
					Log.d(LOCALTAG, "Bind worked");
					result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				} else {
					Log.d(LOCALTAG, "Bind Failed");
					result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_UNABLE_TO_BIND_TO_BACKGROUND_SERVICE_CODE, ERROR_UNABLE_TO_BIND_TO_BACKGROUND_SERVICE_MSG));
				}
			} catch (Exception ex) {
				Log.d(LOCALTAG, "startService failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			Log.d(LOCALTAG, "Finished startService");
			return result;
		}
		
		public ExecuteResult startBooming(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras)
		{
			Log.d(LOCALTAG, "Starting startBooming");
			ExecuteResult result = null;
			
			try{
				Log.d(LOCALTAG, "Attempting to start Booming");
				if(!this.currentlyBooming){
					Log.d(LOCALTAG, "not quite Booming yet");
					
					try{
						this.mService = new Intent(this.mServiceName);
						if(this.mServiceName.equals(redName)){
							Log.d(LOCALTAG, "it's the redName!");
							this.mService.setClass(this.mContext, OTBTServiceAlpha.class);
						}else{
							Log.d(LOCALTAG, "whoa nelly!: "+this.mServiceName);
						}
						this.mService.putExtra("args", args.getJSONObject(0).toString());
						//this.mService.putExtra("address", macAddress);
						Log.d(LOCALTAG, "Attempting to start service... " + this.mService);
						this.mContext.startService(this.mService);
						currentlyBooming = true;
						Log.d(LOCALTAG, "Attempting to bind to service");
						if (this.mContext.bindService(this.mService, serviceConnection, 1)) {
							Log.d(LOCALTAG, "Waiting for service connected lock");
							/*synchronized(mServiceConnectedLock) {
								while (mServiceConnected==null) {
									try {
										mServiceConnectedLock.wait();
									} catch (InterruptedException e) {
										Log.d(LOCALTAG, "Interrupt occurred while waiting for connection", e);
									}
								}
								//result = this.mServiceConnected;
								registerForUpdates(listener, listenerExtras);
							}*/
						}
					} catch(Exception ex) {
						Log.d(LOCALTAG, "bindToService failed", ex);
					}
					
					result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				} else {
					Log.d(LOCALTAG, "already Booming");
					result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				}
			} catch (Exception ex) {
				Log.d(LOCALTAG, "startBooming failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			Log.d(LOCALTAG, "Finished startBooming");
			return result;
		}
		
		public ExecuteResult pause(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
			Log.d(LOCALTAG, "starting pause");
			ExecuteResult result = null;
			try {
				mApi.pause();
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (RemoteException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			return result;
		}
		
		public ExecuteResult resume(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
			Log.d(LOCALTAG, "starting resume");
			ExecuteResult result = null;
			try {
				mApi.resume();
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (RemoteException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			return result;
		}
		
		public ExecuteResult kill(CordovaArgs args, IUpdateListener listener, Object[] listenerExtras){
			Log.d(LOCALTAG, "starting kill");
			ExecuteResult result = null;
			try {
				mApi.kill();
				currentlyBooming = false;
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (RemoteException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			return result;
		}
		
		
		public ExecuteResult write(byte[] msg) {
			Log.d(LOCALTAG, "starting write(byte[] msg)");
			ExecuteResult result = null;
			try {
				mApi.write(msg);
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (RemoteException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			return result;
		}
		
		public ExecuteResult disconnect(){
			Log.d(LOCALTAG, "starting disconnect()");
			ExecuteResult result = null;
			try{
				mApi.disconnect();
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (RemoteException ex) {
				ex.printStackTrace();
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			return result;
		}
		
		public ExecuteResult getState(Object[] listenerExtras){
			Log.d(LOCALTAG, "starting getState(Object[] listenerExtras)");
			ExecuteResult result = null;
			try{
				if(mApi.getState()==OTBTBlueServiceAlpha.STATE_CONNECTED){
					((CallbackContext)listenerExtras[0]).success();
				} else {
					((CallbackContext)listenerExtras[0]).error("Not connected.");
				}
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (RemoteException ex){
				ex.printStackTrace();
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			return result;
		}
		
		public ExecuteResult stopService()
		{
			ExecuteResult result = null;
			Log.d(LOCALTAG, "stopService called");
			this.currentlyBooming = false;
			Log.d(LOCALTAG, "currentlyBooming -> false");
			isBooming = false;
			Log.d(LOCALTAG, "isBooming -> false");
			try {
				
				Log.d("ServiceDetails", "Unbinding Service");
				this.mContext.unbindService(serviceConnection);
				
				Log.d("ServiceDetails", "Stopping service");
				if (this.mContext.stopService(this.mService))
				{
					Log.d("ServiceDetails", "Service stopped");
				} else {
					Log.d("ServiceDetails", "Service not stopped");
				}
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (Exception ex) {
				Log.d(LOCALTAG, "stopService failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			return result;
		}
		
		public ExecuteResult enableTimer(JSONArray data)
		{
			ExecuteResult result = null;

			int milliseconds = data.optInt(1, 60000);
			try {
				//mApi.enableTimer(milliseconds);
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (Exception ex){//(RemoteException ex) {
				Log.d(LOCALTAG, "enableTimer failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}

			return result;
		}
		
		public ExecuteResult disableTimer()
		{
			ExecuteResult result = null;
		
			try {
				//mApi.disableTimer();
				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (Exception ex){//(RemoteException ex) {
				Log.d(LOCALTAG, "disableTimer failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}

			return result;
		}
		
		public ExecuteResult registerForBootStart()
		{
			ExecuteResult result = null;
		
			try {
				//PropertyHelper.addBootService(this.mContext, this.mServiceName);

				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (Exception ex) {
				Log.d(LOCALTAG, "registerForBootStart failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}

			return result;
		}
		
		public ExecuteResult deregisterForBootStart()
		{
			ExecuteResult result = null;
		
			try {
				//PropertyHelper.removeBootService(this.mContext, this.mServiceName);

				result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			} catch (Exception ex) {
				Log.d(LOCALTAG, "deregisterForBootStart failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}

			return result;
		}
		
		public ExecuteResult setConfiguration(JSONArray data)
		{
			ExecuteResult result = null;
			
			try {
				if (this.isServiceRunning()) {
					Object obj;
					try {
						obj = data.get(1);
						//mApi.setConfiguration(obj.toString());
						result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
					} catch (JSONException e) {
						Log.d(LOCALTAG, "Processing config JSON from background service failed", e);
						result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, e.getMessage()));
					}
				} else {
					result = new ExecuteResult(ExecuteStatus.INVALID_ACTION, createJSONResult(false, ERROR_SERVICE_NOT_RUNNING_CODE, ERROR_SERVICE_NOT_RUNNING_MSG));
				}
			} catch (Exception ex){//(RemoteException ex) {
				Log.d(LOCALTAG, "setConfiguration failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			return result;
		}

		public ExecuteResult getStatus()
		{
			ExecuteResult result = null;
			
			result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
			
			return result;
		}
		
		public ExecuteResult runOnce()
		{
			ExecuteResult result = null;
			
			try {
				if (this.isServiceRunning()) {
					//mApi.run();
					result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
				} else {
					result = new ExecuteResult(ExecuteStatus.INVALID_ACTION, createJSONResult(false, ERROR_SERVICE_NOT_RUNNING_CODE, ERROR_SERVICE_NOT_RUNNING_MSG));
				}
			} catch (Exception ex){//(RemoteException ex) {
				Log.d(LOCALTAG, "runOnce failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			return result;
		}
		
		public ExecuteResult registerForUpdates(IUpdateListener listener, Object[] listenerExtras)
		{
			ExecuteResult result = null;
			try {
				
				// Check for if the listener is null
				// If it is then it will be because the Plugin version doesn't support the method
				if (listener == null) {
					result = new ExecuteResult(ExecuteStatus.INVALID_ACTION, createJSONResult(false, ERROR_ACTION_NOT_SUPPORTED__IN_PLUGIN_VERSION_CODE, ERROR_ACTION_NOT_SUPPORTED__IN_PLUGIN_VERSION_MSG));
				} else {
					
					// If a listener already exists, then we fist need to deregister the original
					// Ignore any failures (likely due to the listener not being available anymore)
					if (this.isRegisteredForUpdates()) 
						this.deregisterListener();
				
					this.mListener = listener;
					this.mListenerExtras = listenerExtras;

					result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG), false);
				}
			} catch (Exception ex) {
				Log.d(LOCALTAG, "regsiterForUpdates failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			return result;
		}
		
		public ExecuteResult deregisterForUpdates()
		{
			ExecuteResult result = null;
			try {
				if (this.isRegisteredForUpdates())
					if (this.deregisterListener())
						result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG));
					else
						result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_UNABLE_TO_CLOSED_LISTENER_CODE, ERROR_UNABLE_TO_CLOSED_LISTENER_MSG));
				else
					result = new ExecuteResult(ExecuteStatus.INVALID_ACTION, createJSONResult(false, ERROR_LISTENER_NOT_REGISTERED_CODE, ERROR_LISTENER_NOT_REGISTERED_MSG));
				
			} catch (Exception ex) {
				Log.d(LOCALTAG, "deregsiterForUpdates failed", ex);
				result = new ExecuteResult(ExecuteStatus.ERROR, createJSONResult(false, ERROR_EXCEPTION_CODE, ex.getMessage()));
			}
			
			return result;
		}
		
		
		
		
		
		
		/*
		 * Background Service specific methods
		 */
		public void close()
		{
			Log.d("ServiceDetails", "Close called");
			try {
				// Remove the lister to this publisher
				this.deregisterListener();
				
				Log.d("ServiceDetails", "Removing ServiceListener");
				mApi.removeListener(serviceListener);
				Log.d("ServiceDetails", "Removing ServiceConnection");
				this.mContext.unbindService(serviceConnection);
			} catch (Exception ex) {
				// catch any issues, typical for destroy routines
				// even if we failed to destroy something, we need to continue destroying
				Log.d(LOCALTAG, "close failed", ex);
				Log.d(LOCALTAG, "Ignoring exception - will continue");
			}
			Log.d("ServiceDetails", "Close finished");
		}
		
		private boolean deregisterListener() {
			boolean result = false;

			if (this.isRegisteredForUpdates()) {
				Log.d("ServiceDetails", "Listener deregistering");
				try {
					Log.d("ServiceDetails", "Listener closing");
					this.mListener.closeListener(new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG)), this.mListenerExtras);
					Log.d("ServiceDetails", "Listener closed");
				} catch (Exception ex) {
					Log.d("ServiceDetails", "Error occurred while closing the listener", ex);
				}
				
				this.mListener = null;
				this.mListenerExtras = null;
				Log.d("ServiceDetails", "Listener deregistered");
				
				result = true;
			}
			
			return result;
		}
		
		/*
		 ************************************************************************************************
		 * Private Methods 
		 ************************************************************************************************
		 */
		private boolean bindToService() {
			boolean result = false;
			
			Log.d(LOCALTAG, "Starting bindToService");
			
			try {
				this.mService = new Intent(this.mServiceName);
				if(this.mService==null){
					Log.d(LOCALTAG, "mService is a null");
				}
				if(this.mServiceName.equals(blueName))
					this.mService.setClass(mContext, OTBTBlueServiceAlpha.class);
				if(this.mServiceName.equals(redName))
					this.mService.setClass(mContext, OTBTServiceAlpha.class);
				Log.d(LOCALTAG, "Attempting to start service");
				this.mContext.startService(this.mService);
				
				Log.d(LOCALTAG, "Attempting to bind to service");
				if (this.mContext.bindService(this.mService, serviceConnection, 1)) {
					Log.d(LOCALTAG, "Waiting for service connected lock");
					synchronized(mServiceConnectedLock) {
						while (mServiceConnected==null) {
							try {
								mServiceConnectedLock.wait();
							} catch (InterruptedException e) {
								Log.d(LOCALTAG, "Interrupt occurred while waiting for connection", e);
							}
						}
						result = this.mServiceConnected;
					}
				}
			} catch (Exception ex) {
				Log.d(LOCALTAG, "bindToService failed", ex);
			}
			
			Log.d(LOCALTAG, "Finished bindToService");
			
			return result;
		}
		
		private ServiceConnection serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// that's how we get the client side of the IPC connection
				mApi = OTBTApiAlpha.Stub.asInterface(service);
				try {
					mApi.addListener(serviceListener);
				} catch (RemoteException e) {
					Log.d(LOCALTAG, "addListener failed", e);
				}
				
				synchronized(mServiceConnectedLock) {
					mServiceConnected = true;

					mServiceConnectedLock.notify();
				}

			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				synchronized(mServiceConnectedLock) {
					mServiceConnected = false;

					mServiceConnectedLock.notify();
				}
			}
		};
		
		private OTBTListenerAlpha.Stub serviceListener = new OTBTListenerAlpha.Stub() {
			@Override
			public void handleUpdate() throws RemoteException {
				handleLatestResult();
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
                   
                   if (dataAvailableCallback != null) {
                       sendDataToSubscriber();
                   }
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
                           notifyConnectionSuccess();
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
                   //msg.getData().getString(TOAST);
                   notifyConnectionLost(message);
                   break;
               case MESSAGE_JOB:
            	   String massage = bunt.getString("job_data");
            	   PluginResult resulto = new PluginResult(PluginResult.Status.OK, massage);
	               //result.setKeepCallback(true);
	               dataAvailableCallback.sendPluginResult(resulto);
            	   break;
				}
			}

			@Override
			public void shutMeDown() throws RemoteException {
				Log.d(TAG, "shutMeDown() called");
				//currentlyBooming = false;
				stopService();
			}
			
		};
// blah blah blah blah blah blah blah
		private void handleLatestResult() {
			Log.d("ServiceDetails", "Latest results received");
			
			if (this.isRegisteredForUpdates()) {
				Log.d("ServiceDetails", "Calling listener");
				
				ExecuteResult result = new ExecuteResult(ExecuteStatus.OK, createJSONResult(true, ERROR_NONE_CODE, ERROR_NONE_MSG), false);
				try {
					this.mListener.handleUpdate(result, this.mListenerExtras);
					Log.d("ServiceDetails", "Listener finished");
				} catch (Exception ex) {
					Log.d("ServiceDetails", "Listener failed", ex);
					Log.d("ServiceDetails", "Disabling listener");
					this.mListener = null;
					this.mListenerExtras = null;
				}
			} else {
				Log.d("ServiceDetails", "No action performed");
			}
		}
		
		private JSONObject createJSONResult(Boolean success, int errorCode, String errorMessage) {
			JSONObject result = new JSONObject();

			// Append the basic information
			try {
				result.put("Success", success);
				result.put("ErrorCode", errorCode);
				result.put("ErrorMessage", errorMessage);
			} catch (JSONException e) {
				Log.d(LOCALTAG, "Adding basic info to JSONObject failed", e);
			}

			if (this.mServiceConnected != null && this.mServiceConnected && this.isServiceRunning()) {
				try { result.put("ServiceRunning", true); } catch (Exception ex) {Log.d(LOCALTAG, "Adding ServiceRunning to JSONObject failed", ex);};
				try { result.put("TimerEnabled", isTimerEnabled()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerEnabled to JSONObject failed", ex);};
				try { result.put("Configuration", getConfiguration()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding Configuration to JSONObject failed", ex);};
				try { result.put("LatestResult", getLatestResult()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding LatestResult to JSONObject failed", ex);};
				try { result.put("TimerMilliseconds", getTimerMilliseconds()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerMilliseconds to JSONObject failed", ex);};
			} else {
				try { result.put("ServiceRunning", false); } catch (Exception ex) {Log.d(LOCALTAG, "Adding ServiceRunning to JSONObject failed", ex);};
				try { result.put("TimerEnabled", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerEnabled to JSONObject failed", ex);};
				try { result.put("Configuration", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding Configuration to JSONObject failed", ex);};
				try { result.put("LatestResult", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding LatestResult to JSONObject failed", ex);};
				try { result.put("TimerMilliseconds", null); } catch (Exception ex) {Log.d(LOCALTAG, "Adding TimerMilliseconds to JSONObject failed", ex);};
			}

			try { result.put("RegisteredForBootStart", isRegisteredForBootStart()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding RegisteredForBootStart to JSONObject failed", ex);};
			try { result.put("RegisteredForUpdates", isRegisteredForUpdates()); } catch (Exception ex) {Log.d(LOCALTAG, "Adding RegisteredForUpdates to JSONObject failed", ex);};
				
			return result;
		}
		
		private boolean isServiceRunning()
		{
			boolean result = false;
			
			try {
				// Return Plugin with ServiceRunning true/ false
				ActivityManager manager = (ActivityManager)this.mContext.getSystemService(Context.ACTIVITY_SERVICE); 
				for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) { 
					if (this.mServiceName.equals(service.service.getClassName())) { 
						result = true; 
					} 
				} 
			} catch (Exception ex) {
				Log.d(LOCALTAG, "isServiceRunning failed", ex);
			}

		    return result;
		}
		
		private Boolean isTimerEnabled()
		{
			Boolean result = false;
			
			try {
				//result = mApi.isTimerEnabled();
			} catch (Exception ex) {
				Log.d(LOCALTAG, "isTimerEnabled failed", ex);
			}
			
			return result;
		}
		
		private Boolean isRegisteredForBootStart()
		{
			Boolean result = false;
		
			try {
				//result = PropertyHelper.isBootService(this.mContext, this.mServiceName);
			} catch (Exception ex) {
				Log.d(LOCALTAG, "isRegisteredForBootStart failed", ex);
			}

			return result;
		}
		
		private Boolean isRegisteredForUpdates()
		{
			if (this.mListener == null)
				return false;
			else
				return true;
		}
		
		private JSONObject getConfiguration()
		{
			JSONObject result = null;
			
			try {
				String data = mApi.getConfiguration();
				result = new JSONObject(data);
			} catch (Exception ex) {
				Log.d(LOCALTAG, "getConfiguration failed", ex);
			}
			
			return result;
		}
		
		private JSONObject getLatestResult()
		{
			JSONObject result = null;

			try {
				String data = mApi.getLatestResult();
				result = new JSONObject(data);
			} catch (Exception ex) {
				Log.d(LOCALTAG, "getLatestResult failed", ex);
			}

			return result;
		}
		
		private int getTimerMilliseconds()
		{
			int result = -1;
			
			try {
				//result = mApi.getTimerMilliseconds();
			} catch (Exception ex) {
				Log.d(LOCALTAG, "getTimerMilliseconds failed", ex);
			}
			
			return result;
		}
		
	}

	protected class ExecuteResult {

		/*
		 ************************************************************************************************
		 * Fields 
		 ************************************************************************************************
		 */
		private ExecuteStatus mStatus;
		private JSONObject mData;
		private boolean mFinished = true;

		public ExecuteStatus getStatus() {
			return this.mStatus;
		}
		
		public void setStatus(ExecuteStatus pStatus) {
			this.mStatus = pStatus;
		}
		
		public JSONObject getData() {
			return this.mData;
		}
		
		public void setData(JSONObject pData) {
			this.mData = pData;
		}
		
		public boolean isFinished() {
			return this.mFinished;
		}

		public void setFinished(boolean pFinished) {
			this.mFinished = pFinished;
		}
		
		/*
		 ************************************************************************************************
		 * Constructors 
		 ************************************************************************************************
		 */
		public ExecuteResult(ExecuteStatus pStatus) {
			this.mStatus = pStatus;
		}
		
		public ExecuteResult(ExecuteStatus pStatus, JSONObject pData) {
			this.mStatus = pStatus;
			this.mData = pData;
		}

		public ExecuteResult(ExecuteStatus pStatus, JSONObject pData, boolean pFinished) {
			this.mStatus = pStatus;
			this.mData = pData;
			this.mFinished = pFinished;
		}

	}

	/*public interface IUpdateListener {
		public void handleUpdate(ExecuteResult logicResult, Object[] listenerExtras);
		public void closeListener(ExecuteResult logicResult, Object[] listenerExtras);
	}*/
	
	/*
	 ************************************************************************************************
	 * Enums 
	 ************************************************************************************************
	 */
	protected enum ExecuteStatus {
		OK,
		ERROR,
		INVALID_ACTION
	}
	
	@Override
	public void handleUpdate(ExecuteResult logicResult, Object[] listenerExtras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void closeListener(ExecuteResult logicResult, Object[] listenerExtras) {
		// TODO Auto-generated method stub
		
	}
	
	private class HomeStarRunner implements Runnable{

		@Override
		public void run() {
			blueService.write("{\"gc\":\"M5\"}\n".getBytes());
			blueService.write("{\"gc\":\"G28.2X0Y0Z0A0\"}\n".getBytes());
		}
		
	}
	
}

