package com.opentrons.otbtalpha.cordova;

import android.util.Log;




// kludgy imports to support 2.9 and 3.0 due to package changes
import org.apache.cordova.*;
import org.apache.cordova.api.*;
// import org.apache.cordova.CordovaArgs;
// import org.apache.cordova.CordovaPlugin;
// import org.apache.cordova.CallbackContext;
// import org.apache.cordova.PluginResult;
// import org.apache.cordova.LOG;
import org.json.JSONException;

import com.opentrons.otbtalpha.cordova.OTBTLogicAlpha.ExecuteResult;
import com.opentrons.otbtalpha.cordova.OTBTLogicAlpha.ExecuteStatus;


/**
 * Cordova Plugin for Serial Communication with Opentrons over Bluetooth (based off of Don Coleman's
 * BluetoothSerial plugin)
 */

public class OTBTAlpha extends CordovaPlugin implements IUpdateListener {

	/*
	 ************************************************************************************************
	 * Static values 
	 ************************************************************************************************
	 */
	// Debugging
    private static final String TAG = OTBTAlpha.class.getSimpleName();
    private static final boolean D = true;

   
    private OTBTLogicAlpha mLogic = null;
    
    

    /*
	 ************************************************************************************************
	 * Overriden Methods 
	 ************************************************************************************************
	 */
    @Override
    public boolean execute(final String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
    	
        LOG.d(TAG, "action = " + action);

        boolean result = false;
        
        if(this.mLogic == null)
        	this.mLogic = new OTBTLogicAlpha(this.cordova.getActivity());
        
        try{
        	if(this.mLogic.isActionValid(action)){
        		final IUpdateListener listener = this;
        		final Object[] listenerExtras = new Object[] { callbackContext };
        		
        		cordova.getThreadPool().execute(new Runnable(){
        			@Override
        			public void run() {
        				ExecuteResult logicResult = mLogic.execute(action, args, listener, listenerExtras);
        				
        				Log.d(TAG, "logicResult = " + logicResult.toString());
        				
        				PluginResult pluginResult = transformResult(logicResult);
        				
        				Log.d(TAG, "pluginResult = " + pluginResult.toString());
        				Log.d(TAG, "pluginResult.getMessage() = " + pluginResult.getMessage());
        				if(pluginResult.getKeepCallback())
        					Log.d(TAG, "Keep Callback");
        				else
        					Log.d(TAG, "Don't keep Callback");
        				
        				callbackContext.sendPluginResult(pluginResult);
        			}
        		});
        		
        		result = true;
        	} else {
        		result = false;
        	}
        	
        } catch (Exception ex) {
        	Log.d(TAG, "Exception - " + ex.getMessage());
        }
        
        return result;
        
    // ***************************************************************************    
        
    }
    
    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if(this.mLogic != null) {
        	this.mLogic.onDestroy();
        	this.mLogic = null;
        }
        
        
        // **************************************
        if (otbtworker != null) {
            otbtworker.stop();
        }
    }
	*/
    
    /*
	 ************************************************************************************************
	 * Public Methods 
	 ************************************************************************************************
	 */
    
	public void handleUpdate(ExecuteResult logicResult, Object[] listenerExtras) {
		Log.d(TAG, "Starting handleUpdate");
		sendUpdateToListener(logicResult, listenerExtras);
		Log.d(TAG, "Finished handleUpdate");
	}
	
	public void closeListener(ExecuteResult logicResult, Object[] listenerExtras) {
		Log.d(TAG, "Starting closeListener");
		sendUpdateToListener(logicResult, listenerExtras);
		Log.d(TAG, "Finished closeListener");
	}
	
	/*
	 ************************************************************************************************
	 * Private Methods 
	 ************************************************************************************************
	 */
	private void sendUpdateToListener(ExecuteResult logicResult, Object[] listenerExtras) {
		try {
			if (listenerExtras != null && listenerExtras.length > 0) {
				Log.d(TAG, "Sending update");
				CallbackContext callback = (CallbackContext)listenerExtras[0];
		
				callback.sendPluginResult(transformResult(logicResult));
				Log.d(TAG, "Sent update");
			}
		} catch (Exception ex) {
			Log.d(TAG, "Sending update failed", ex);
		}
	}
	
	private PluginResult transformResult(ExecuteResult logicResult) {
		PluginResult pluginResult = null;
		
		Log.d(TAG, "Start of transformResult");
		if (logicResult.getStatus() == ExecuteStatus.OK) {
			Log.d(TAG, "Status is OK");
			
			if (logicResult.getData() == null) {
				Log.d(TAG, "We dont have data");
				pluginResult = new PluginResult(PluginResult.Status.OK);
			} else {
				Log.d(TAG, "We have data");
				pluginResult = new PluginResult(PluginResult.Status.OK, logicResult.getData());
			}
		}

		if (logicResult.getStatus() == ExecuteStatus.ERROR) {
			Log.d(TAG, "Status is ERROR");
			
			if (logicResult.getData() == null) {
				Log.d(TAG, "We dont have data");
				pluginResult = new PluginResult(PluginResult.Status.ERROR, "Unknown error");
			} else {
				Log.d(TAG, "We have data");
				pluginResult = new PluginResult(PluginResult.Status.ERROR, logicResult.getData());
			}
		}
		
		if (logicResult.getStatus() == ExecuteStatus.INVALID_ACTION) {
			Log.d(TAG, "Status is INVALID_ACTION");
			
			if (logicResult.getData() == null) {
				Log.d(TAG, "We have data");
				pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION, "Unknown error");
			} else {
				Log.d(TAG, "We dont have data");
				pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION, logicResult.getData());
			}
		}
		
		if (!logicResult.isFinished()) {
			Log.d(TAG, "Keep Callback set to true");
			pluginResult.setKeepCallback(true);
		}
		
		Log.d(TAG, "End of transformResult");
		return pluginResult;
	}
    
}
