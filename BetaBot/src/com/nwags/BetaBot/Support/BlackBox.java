package com.nwags.BetaBot.Support;

import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;


public class BlackBox 
{
	private static final String TAG = "BlackBox";
	private BufferedOutputStream debugOut;
	private File logFile;
	private final Semaphore writeLock = new Semaphore(1, true);
	
	public void close()
	{
		if(this.debugOut == null)
			return;
		
		try
		{
			this.debugOut.close();
			this.debugOut = null;
			return;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void open()
	{
		if(this.debugOut != null)
			return;
		
		Log.d(TAG, "debug = " + Environment.getExternalStorageDirectory().getPath()+"/OpenTrons/Debug");
		this.logFile = new File(Environment.getExternalStorageDirectory().getPath()+"/OpenTrons/Debug", "tinyg-" + Process.myPid() + ".txt");
		try
		{
			this.debugOut = new BufferedOutputStream(new FileOutputStream(this.logFile));
			return;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public void write(String dir, String cmd)
	{
		if(this.debugOut == null)
			return;
		
		try
		{
			this.writeLock.acquire();
			this.debugOut.write(dir.getBytes(), 0, dir.length());
			this.debugOut.write(cmd.getBytes(), 0, cmd.length());
			this.writeLock.release();
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
