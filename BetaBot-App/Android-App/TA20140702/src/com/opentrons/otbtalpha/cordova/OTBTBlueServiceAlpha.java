package com.opentrons.otbtalpha.cordova;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class OTBTBlueServiceAlpha extends Service{
	public static final String TAG = OTBTBlueServiceAlpha.class.getSimpleName();
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
    
    
    
	private boolean mServiceInitialised = false;
	private List<OTBTListenerAlpha> mListeners = new ArrayList<OTBTListenerAlpha>();
	
	private final Object mResultLock = new Object();
	private JSONObject mLatestResult = null;
	
	
	// Well known SPP UUID
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
	
 // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

	
	
	
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
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onBind called");
		return apiEndpoint;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind called");
		boolean result = false;
		
		return result;
	}
	
	@Override  
	public void onCreate() {
		super.onCreate(); 
		Log.d(TAG, "onCreate() called");
		Log.i(TAG, "Service creating");
		//if(!mServiceInitialised) {
			mAdapter = BluetoothAdapter.getDefaultAdapter();
	        mState = STATE_NONE;
	        
		//}
		
		File storagePath = new File(Environment.getExternalStorageDirectory().getPath() + "/OpenTrons");
	    if(!storagePath.exists())
	    	storagePath.mkdirs();
		
		// Duplicating the call to initialiseService across onCreate and onStart
		// Done this to ensure that my initialisation code is called.
		// Found that the onStart was not called if Android was re-starting the service if killed
	    initialiseService();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "Service started");       
		
		// Duplicating the call to initialiseService across onCreate and onStart
		// Done this to ensure that my initialisation code is called.
		// Found that the onStart was not called if Android was re-starting the service if killed
		initialiseService();
		//args = intent.getStringExtra("args");
		//address = intent.getStringExtra("address");
		
		
	}

	@Override  
	public void onDestroy() {     
		super.onDestroy();     
		Log.i(TAG, "Service destroying");
		
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	}
	
	private void initialiseService() {
		
		if (!this.mServiceInitialised) {
			Log.i(TAG, "Initialising the service");
			
			
			this.mServiceInitialised = true;
		}

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
		public void removeListener(OTBTListenerAlpha listener)
				throws RemoteException {
			
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
			// NOOP
			return "noop";
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
			// NOOP
		}

		@Override
		public void pause() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void kill() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void write(byte[] data) throws RemoteException {
			// TODO Auto-generated method stub
			String outstring = "";
			try{
				outstring = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "data = " + outstring);
			// Create temporary object
			ConnectedThread r;
			synchronized (this) {
				if(mState != STATE_CONNECTED) return;
				r = mConnectedThread;
			}
			// Perform the write unsynchronized
			r.write(data);
			
		}

		@Override
		public void connect(String address) throws RemoteException {
			// TODO Auto-generated method stub
			BluetoothDevice device = mAdapter.getRemoteDevice(address);
			if (D) Log.d(TAG, "connect to: " + device);
			
	        // Cancel any thread attempting to make a connection
	        if (mState == STATE_CONNECTING) {
	            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	        }
	        
	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
	        
	        // Start the thread to connect with the given device
	        mConnectThread = new ConnectThread(device, true);
	        mConnectThread.start();
	        setState(STATE_CONNECTING);
		}

		@Override
		public int getState() throws RemoteException {
			return mState;
		}

		@Override
		public void stop() throws RemoteException {
			// NOOP
		}

		@Override
		public void disconnect() throws RemoteException {
			if (D) Log.d(TAG, "disconnect");

	        if (mConnectThread != null) {
	            mConnectThread.cancel();
	            mConnectThread = null;
	        }

	        if (mConnectedThread != null) {
	            mConnectedThread.cancel();
	            mConnectedThread = null;
	        }
	        
	        setState(STATE_NONE);
		}

		@Override
		public void resume() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		
	};
	
	
	
	/**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                if (secure) {
                    // tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                    tmp = device.createRfcommSocketToServiceRecord(UUID_SPP);
                } else {
                    //tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                    tmp = device.createInsecureRfcommSocketToServiceRecord(UUID_SPP);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (OTBTBlueServiceAlpha.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }
    
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String data = new String(buffer, 0, bytes);

                    // Send the new data String to the UI Activity
                    //mHandler.obtainMessage(OTBTLogicAlpha.MESSAGE_READ, data).sendToTarget();
                    Bundle bundle = new Bundle();
                    bundle.putInt("what", OTBTBlueServiceAlpha.MESSAGE_READ);
                    bundle.putString("message", data);
                    bundleAll(bundle);
                    
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    OTBTBlueServiceAlpha.this.start();
                    break;
                }
            }
        }
        
        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(OTBTLogicAlpha.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
    private void bundleAll(Bundle b){
    	try{
	    	for(OTBTListenerAlpha listener: mListeners){
	    		try {
					listener.sendBundle(b);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(OTBTLogicAlpha.MESSAGE_TOAST);
        
    	Bundle bundle = new Bundle();
        bundle.putString(OTBTBlueServiceAlpha.TOAST, "Unable to connect to device");
        bundle.putInt("what", OTBTBlueServiceAlpha.MESSAGE_TOAST);
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);
        bundleAll(bundle);
        // Start the service over to restart listening mode
        OTBTBlueServiceAlpha.this.start();
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(OTBTLogicAlpha.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(OTBTBlueServiceAlpha.TOAST, "Device connection was lost");
        bundle.putInt("what", OTBTBlueServiceAlpha.MESSAGE_TOAST);
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);
        bundleAll(bundle);
        // Start the service over to restart listening mode
        OTBTBlueServiceAlpha.this.start();
    }
    
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        //if (mSecureAcceptThread != null) {
        //    mSecureAcceptThread.cancel();
        //    mSecureAcceptThread = null;
        //}
        //if (mInsecureAcceptThread != null) {
        //    mInsecureAcceptThread.cancel();
        //    mInsecureAcceptThread = null;
        //}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        //Message msg = mHandler.obtainMessage(OTBTLogicAlpha.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(OTBTBlueServiceAlpha.DEVICE_NAME, device.getName());
        bundle.putInt("what", OTBTBlueServiceAlpha.MESSAGE_DEVICE_NAME);
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);
        bundleAll(bundle);
        setState(STATE_CONNECTED);
    }
    
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        //mHandler.obtainMessage(OTBTLogicAlpha.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        Bundle bundle = new Bundle();
        bundle.putInt("what", OTBTBlueServiceAlpha.MESSAGE_STATE_CHANGE);
        bundle.putInt("state", state);
        bundleAll(bundle);
    }
    
   /**
    * Start the chat service. Specifically start AcceptThread to begin a
    * session in listening (server) mode. Called by the Activity onResume() */
   public synchronized void start() {
       if (D) Log.d(TAG, "start");

       // Cancel any thread attempting to make a connection
       if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

       // Cancel any thread currently running a connection
       if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

       setState(STATE_NONE);

//     Listen isn't working with Arduino. Ignore since assuming the phone will initiate the connection.
//       setState(STATE_LISTEN);
//
//       // Start the thread to listen on a BluetoothServerSocket
//       if (mSecureAcceptThread == null) {
//           mSecureAcceptThread = new AcceptThread(true);
//           mSecureAcceptThread.start();
//       }
//       if (mInsecureAcceptThread == null) {
//           mInsecureAcceptThread = new AcceptThread(false);
//           mInsecureAcceptThread.start();
//       }
   }
    
}
