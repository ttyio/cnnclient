package com.camera.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class ImageFilter {
      // edge detection
	  private int[][] filterMatrix = { 
              {0 ,1, 0},
              {1,-4,1},
              { 0,1,0}};
      private int width = 3;
      private int height = 3;
      private int offset = 0;
      private int alpha = 100;
      private int filter=50;
            
      
      public ImageFilter()
      {
      }

      public Bitmap ApplyFilter(Bitmap input)
      {
          Bitmap tempBitmap = input;
          Bitmap newBitmap = Bitmap.createBitmap(tempBitmap.getWidth(), tempBitmap.getHeight(),Bitmap.Config.ARGB_8888);
          int inputWidth=input.getWidth();
          int inputHeight=input.getHeight();

          for (int x = 0; x < inputWidth; ++x)
          {
              for (int y = 0; y < inputHeight; ++y)
              {             
                  int weight = 0;
                  int grayScale = 0;
                  int xCurrent = -this.width / 2;
                  for (int x2 = 0; x2 < this.width; ++x2)
                  {
                      if (xCurrent + x < inputWidth && xCurrent + x >= 0)
                      {
                          int yCurrent = -this.height / 2;
                          for (int y2 = 0; y2 < this.height; ++y2)
                          {
                              if (yCurrent + y < inputHeight && yCurrent + y >= 0)
                              {
                                  //get the pixel from the original image
                                  int originalColor = tempBitmap.getPixel(xCurrent + x, yCurrent + y);
                                  
                                  //create the gray scale version of the pixel
                                  int g = ((Color.red(originalColor) * 299) + (Color.green(originalColor) *587)+ (Color.blue(originalColor) * 114)+500)/1000;
                                  grayScale += this.filterMatrix[x2][y2] * g;
                                  weight += this.filterMatrix[x2][y2];
                              }
                              ++yCurrent;
                          }
                      }
                      ++xCurrent;
                  }
                  //Color meanPixel = tempBitmap.setPixel(x, y);
                  int meanPixel=Color.argb(0, 0, 0, 0);
                  if (weight ==0)
                      weight = 1;
                  if (weight > 0)
                  {
                      int luminate = DetectRangle(weight, grayScale);
                      //meanPixel = Color.FromArgb(alpha,luminate, luminate, luminate);
                      if (luminate <this.filter)
                          meanPixel = Color.argb(this.alpha, 0, 0, 0);
                      else
                          meanPixel = Color.argb(255, 255, 0, 0);
                    
                  }
                  newBitmap.setPixel(x, y, meanPixel);
              }
          }
          return newBitmap;
      }

      private int DetectRangle(int Weight, int value)
      {
          int result = value / Weight + this.offset;
          if (result <= 0)
              result = 0;
          else if (result >= 255)
              result = 255;
          return result;
      }



}
