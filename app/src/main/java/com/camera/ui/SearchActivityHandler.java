package com.camera.ui;
import com.camera.client.R;
import com.camera.data.DataEngine;
import com.camera.threads.LocalCNNThread;
import com.camera.ui.SearchFrameView.ViewStatus;
import com.camera.util.CameraSource;

import android.app.AlertDialog;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class SearchActivityHandler  extends Handler{
	private final String logTag = "SearchActivityHandler";
	private final SearchActivity activity;
	private LocalCNNThread webProcessThread;
	
	private static final long nBusyAnimationDelay = 50L;
	
	Camera.PictureCallback jpegCallback = new Camera.PictureCallback(){
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i(logTag, "receive image data from camera");	
			
			if (ViewStatus.TakingPicture == activity.getFrameView().getStatus())
			{
		    	//update process animation
				activity.getFrameView().setStatus(ViewStatus.ProcessingPicture);
		    	Message msg = SearchActivityHandler.this.obtainMessage(
						R.id.invalidate, 0);
		    	SearchActivityHandler.this.sendMessageDelayed(msg, nBusyAnimationDelay);
				
		    	//write to disk
                int i = DataEngine.get().createImgFile(data);
				
				//create img filter
				activity.getFrameView().updateFilterBitmap(data, data.length);
				
				//upload to web
				Message uploadMsg = webProcessThread.getHandler().obtainMessage(R.id.process_img, i);
				uploadMsg.sendToTarget();
			}
		}
		
	};

	SearchActivityHandler(SearchActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void handleMessage(Message message) {
	    switch (message.what) {
	    case R.id.invalidate:
		    {
		    	if (ViewStatus.ProcessingPicture == activity.getFrameView().getStatus())
		    	{
		    		activity.getFrameView().invalidate();
			    	Message msg = this.obtainMessage(R.id.invalidate, 0);
			    	this.sendMessageDelayed(msg, nBusyAnimationDelay);
		    	}		    	
		    }
		    break;
	    case R.id.process_ok:
		    {
		    	if (ViewStatus.ProcessingPicture == activity.getFrameView().getStatus())
		    	{
                    activity.getFrameView().setStatus(ViewStatus.Normal);
                    CameraSource.get().startPreview();
                    activity.getFrameView().invalidate();

		    		int id = (Integer)message.obj;
                    AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                    adb.setMessage(id==0?"Cat":"Dog").setPositiveButton("OK", null).show();
		    	}
		    }
		    break;
	    case R.id.process_failed:
	    	{
	    		if (ViewStatus.ProcessingPicture == activity.getFrameView().getStatus())
	    		{
	    			activity.getFrameView().setStatus(ViewStatus.Normal);
	    			CameraSource.get().startPreview();
	    			activity.getFrameView().invalidate();
	    		}
	    		
	    		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
	    		adb.setMessage("Internal Error").setPositiveButton("OK", null).show();
	    	}
	    	break;
         default:
            break;
	    }
	}		
	
	public void start()
	{
		webProcessThread = new LocalCNNThread(this);
    	webProcessThread.start();
	}
	
	public void exitHandler() {
		webProcessThread.exitThread();
		
	    removeMessages(R.id.invalidate);	    
	    removeMessages(R.id.process_ok);
	    removeMessages(R.id.process_failed);
	 }
}
