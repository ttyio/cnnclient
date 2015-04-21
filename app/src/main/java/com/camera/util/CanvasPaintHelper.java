package com.camera.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public final class CanvasPaintHelper {
	
	public static void drawFrameBorder(Canvas canvas,Paint paint,Rect frame)
	{
		canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2,
				paint);
		canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
				frame.bottom - 1, paint);
		canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
				frame.bottom - 1, paint);
		canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
				frame.bottom + 1, paint);
	}
	
	public static void drawButtonText(Canvas canvas,Paint paint,Rect btnRect,String text)
	{
		int nLen = text.length();
		canvas.drawText(text, 
				btnRect.left+btnRect.width()/2-4*nLen, 
				btnRect.top+btnRect.height()/2+nLen, 
				paint);
	}
}
