package com.camera.threads;

import com.camera.client.R;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class ThreadHandler extends Thread{
	private final String logTag = "ThreadHandler";
	private Handler handler;
	public Handler getHandler()
	{
		return handler;
	}
	
	public void exitThread()
	{	
		Log.i(logTag, "exitThread");
		onExit();
	
		Message quit = Message.obtain(handler, R.id.quit);
		quit.sendToTarget();
		try {
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	 public void run() {
	    Looper.prepare();
	    handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				if (R.id.quit == message.what)
				{
					Looper.myLooper().quit();
				}
				else
				{
					onHandleMessage(message);
				}
			}
		};
	    Looper.loop();
	}
	
	protected void onHandleMessage(Message message)
	{		
	}
	
	protected void onExit()
	{
	}
}
