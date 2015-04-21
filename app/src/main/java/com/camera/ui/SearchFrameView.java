package com.camera.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.camera.client.R;
import com.camera.util.CanvasPaintHelper;
import com.camera.util.ImageFilter;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class SearchFrameView extends View{
	private final Paint paint;
	private final int maskColor;
	
	private final Drawable btnImg;
	private final Drawable btnPressedImg;
	
	private Rect screenRc = null;
	private Rect captureBtnRc = null;
	private Rect bottomAreaRc = null;
	private Rect cameraAreaRc = null;
	private List<Rect> gridlines = null;
	private Rect busyHighlightRc = null;
	
	enum ViewStatus{
		Normal,
		CaptureBtnDown,
		TakingPicture, 
		ProcessingPicture,		
	}
	private ViewStatus curStatus = ViewStatus.Normal;
	public void setStatus(ViewStatus status)
	{		
		curStatus = status;
	}
	public ViewStatus getStatus()
	{
		return curStatus;
	}
	
	private Bitmap filterBmp = null;
	public void updateFilterBitmap(byte[] data, int length)
	{
		if (null == data || 0 == length)
			filterBmp = null;
		else
		{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, length);

            Matrix matrix = new Matrix();
            matrix.postRotate(-90);

            Bitmap rotScaImg = Bitmap.createBitmap(bmp, 0, 0, bmp
                    .getWidth(), bmp.getHeight(), matrix, true);

            ImageFilter imgf = new ImageFilter();
            Bitmap filtRotScaImg = imgf.ApplyFilter(rotScaImg);

            filterBmp = Bitmap.createScaledBitmap(filtRotScaImg, screenRc
                    .width(), screenRc.height(), false);
		}		
	}

	public SearchFrameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint = new Paint();
		
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.cameraframe_mask);
		btnImg = resources.getDrawable(R.drawable.click_blue);
		btnPressedImg = resources.getDrawable(R.drawable.click_orange);
	}
	
	private void setupLayout(Canvas canvas)
	{
		if (null==gridlines)
		{			
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			screenRc = new Rect(0,0,width,height);
			
			bottomAreaRc = new Rect(width/5*4,0,width,height);
			cameraAreaRc = new Rect(0,0,bottomAreaRc.left,height);
			
			int btnWidth = 200;
			int left = bottomAreaRc.left+bottomAreaRc.width()/2-btnWidth/2;
			int top = bottomAreaRc.top+bottomAreaRc.height()/2-btnWidth/2;
			captureBtnRc = new Rect(left,top,left+btnWidth,top+btnWidth);			
			btnImg.setBounds(captureBtnRc);		
			btnPressedImg.setBounds(captureBtnRc);
			
			int nHorz = 2;
			int nVect = 5;
			gridlines = new ArrayList<Rect>();
			for (int i=1;i<=nHorz;i++)
			{
				int yPos = cameraAreaRc.height()/(nHorz+1)*i;
				Rect rc = new Rect(0,yPos,cameraAreaRc.width(),yPos+1);
				gridlines.add(rc);
			}
			for (int i=1;i<=nVect;i++)
			{
				int xPos = cameraAreaRc.width()/(nVect+1)*i;
				Rect rc = new Rect(xPos,0,xPos+1,cameraAreaRc.height());
				gridlines.add(rc);
			}
			
			busyHighlightRc = new Rect(0,0,50,height);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {		
		setupLayout(canvas);
		
		//draw bottom rect	
		paint.setColor(maskColor);
		canvas.drawRect(bottomAreaRc,paint);
				
		if (ViewStatus.ProcessingPicture == curStatus &&
				filterBmp != null)
		{
			//draw busystatus			
			canvas.drawBitmap(filterBmp, busyHighlightRc, busyHighlightRc, paint);
			
			busyHighlightRc.offset(10, 0);
			if (!cameraAreaRc.contains(busyHighlightRc))
			{
				busyHighlightRc.offset(-busyHighlightRc.left, 0);
			}
						
		}
		else
		{
			//draw grid lines
			paint.setColor(Color.BLACK);
			int nCount = gridlines.size();
			for (int i=0;i<nCount;i++)
			{
				canvas.drawRect(gridlines.get(i),paint);
			}
		}
		
		//draw camera frame
		paint.setColor(Color.WHITE);
		CanvasPaintHelper.drawFrameBorder(canvas, paint, cameraAreaRc);
		
		if (ViewStatus.CaptureBtnDown == curStatus)
			btnPressedImg.draw(canvas);
		else
			btnImg.draw(canvas);
	}
	
	public enum SearchFrameHitTestTarget{
		Unknown,
		CaptureBtn,
	}
	
	public SearchFrameHitTestTarget hitTest(int xPos,int yPos)
	{		
		SearchFrameHitTestTarget target = SearchFrameHitTestTarget.Unknown;

		if (captureBtnRc.contains(xPos, yPos)) {
			target = SearchFrameHitTestTarget.CaptureBtn;
		} 

		return target;
	}			
}
