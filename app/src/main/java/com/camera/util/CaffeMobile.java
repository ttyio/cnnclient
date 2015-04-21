package com.camera.util;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class CaffeMobile {
    public native void enableLog(boolean enabled);
    public native int loadModel(String modelPath, String weightsPath);
    public native int predictImage(String imgPath);
}
