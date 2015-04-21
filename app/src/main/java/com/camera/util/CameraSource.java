package com.camera.util;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.SurfaceHolder;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class CameraSource {		
	private Camera camera = null;

	private static CameraSource cameraSource = null;	
	public static CameraSource get() {
		if (cameraSource == null) {
			cameraSource = new CameraSource();
		}
		return cameraSource;
	}
	
	enum CameraStatus{
		Init,
		DriverOpen,
		Preview		
	}
	
	private CameraStatus curStatus = CameraStatus.Init;
	
	public void openDriver(SurfaceHolder holder){
		if (CameraStatus.Init == curStatus)
		{
			camera = Camera.open();
			curStatus = CameraStatus.DriverOpen;
			
			try {				
				camera.setPreviewDisplay(holder);								
			} catch (IOException e) {				
				e.printStackTrace();
				closeDriver();
			}												
		}
	}

	public void closeDriver() {		
		if (CameraStatus.DriverOpen == curStatus)
		{
			camera.release();
			camera = null;
			curStatus = CameraStatus.Init;
		}
	}
		
	public void takePicture(final PictureCallback cb)
	{		
		if (CameraStatus.Preview == curStatus)
		{
			Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
				public void onAutoFocus(boolean success, Camera camera) {					
					camera.takePicture(null, null, cb);
					curStatus = CameraStatus.DriverOpen;
				}
			};			
			camera.autoFocus(autoFocusCallback);
		}
	}

	public void startPreview() {
		if (CameraStatus.DriverOpen == curStatus) {
			camera.startPreview();
			curStatus = CameraStatus.Preview;
		}
	}

	public void stopPreview() {
		if (CameraStatus.Preview == curStatus) {
			camera.stopPreview();
			curStatus = CameraStatus.DriverOpen;
		}
	}
	
	public void setCameraPreviewSize(int x,int y) {

		if (CameraStatus.DriverOpen == curStatus)
		{
			Camera.Parameters parameters = camera.getParameters();

            // preview
            List<android.hardware.Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            android.hardware.Camera.Size lastSize = previewSizes.get(0);
            for(int i=0; i<previewSizes.size(); ++i){
                if(previewSizes.get(i).width > x){
                    break;
                }
                lastSize = previewSizes.get(i);
            }
			parameters.setPreviewSize(lastSize.width, lastSize.height);

            // picture size
            List<android.hardware.Camera.Size> picSizes = parameters.getSupportedPictureSizes();
            parameters.setPictureSize(picSizes.get(0).width, picSizes.get(0).height);

			parameters.setRotation(90);
	
			// turn the flash off
            parameters.setFlashMode("auto");
	
			// Set zoom to 2x
            parameters.setZoom(2);

            // auto focus
            parameters.setFocusMode("auto");

			try {
			    camera.setParameters(parameters);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			startPreview();
		}
	}
	
}
