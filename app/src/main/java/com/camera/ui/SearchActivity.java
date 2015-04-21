package com.camera.ui;

import com.camera.client.R;
import com.camera.data.DataEngine;
import com.camera.ui.SearchFrameView.SearchFrameHitTestTarget;
import com.camera.ui.SearchFrameView.ViewStatus;
import com.camera.util.CameraSource;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class SearchActivity extends Activity implements Callback,OnTouchListener{
	private final String logTag = "SearchActivity";

	private SearchActivityHandler handler = null;
	public SearchActivityHandler getHandler()
	{
		if (null == handler)
			handler = new SearchActivityHandler(this);
		return handler;
	}
	
	private SearchFrameView frameview = null;	
	public SearchFrameView getFrameView()
	{
		if (null == frameview)
		{
			frameview = (SearchFrameView)findViewById(R.id.searchframe_view);
		}
		return frameview;
	}	

    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
    	Log.i(logTag, "surfaceChanged");
    	    	
    	CameraSource.get().stopPreview();
    	CameraSource.get().setCameraPreviewSize(width,height);   
    	CameraSource.get().startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {	
		Log.i(logTag, "surfaceCreated");
        		
		CameraSource.get().openDriver(holder);		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(logTag, "surfaceDestroyed");
				
		CameraSource.get().stopPreview();	
		CameraSource.get().closeDriver();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 

        DataEngine.init(this);
            	                	
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.searchsurfaces_view);
     	SurfaceHolder surfaceHolder = surfaceView.getHolder();
    	surfaceHolder.addCallback(this);
    	
    	getFrameView().setOnTouchListener(this);

    	getHandler().start();  
    }
    
    @Override
	protected void onResume() {    	
		super.onResume();				
		CameraSource.get().startPreview();
	}
    
    @Override
	protected void onPause() {
    	super.onPause();		
    	CameraSource.get().stopPreview();	
	}
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		getHandler().exitHandler();
		finish();
	}
    
    @Override
	public void onConfigurationChanged(Configuration config) {
		//ignore screen rotation event
	    super.onConfigurationChanged(config);
	}

    @Override
	public boolean onTouch(View v, MotionEvent event) {   
    	
    	float xPos = event.getX();
    	float yPos = event.getY();
    	SearchFrameHitTestTarget target = getFrameView().hitTest((int) xPos, (int) yPos);
    	if (SearchFrameHitTestTarget.CaptureBtn == target)
    	{	
    		switch (event.getAction())
    		{
    		case MotionEvent.ACTION_DOWN:
    			if (ViewStatus.Normal == getFrameView().getStatus())
    			{
    				getFrameView().setStatus(ViewStatus.CaptureBtnDown);
    				getFrameView().invalidate();
    			}
    			break;
    		case MotionEvent.ACTION_UP:
    			if (ViewStatus.CaptureBtnDown == getFrameView().getStatus())
    			{
    				getFrameView().setStatus(ViewStatus.TakingPicture);
        			CameraSource.get().takePicture(getHandler().jpegCallback);
    			}
    		}    		
    	}
    	else
    	{
    		if (MotionEvent.ACTION_MOVE == event.getAction() &&
    				ViewStatus.CaptureBtnDown == getFrameView().getStatus())
    		{    			
    			getFrameView().setStatus(ViewStatus.Normal);
				getFrameView().invalidate();
    		}
    	}
    	
    	return true;		
	}

}
