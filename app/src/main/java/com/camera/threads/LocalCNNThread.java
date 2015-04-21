package com.camera.threads;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.camera.data.DataEngine;
import com.camera.util.CaffeMobile;

import com.camera.client.R;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class LocalCNNThread  extends ThreadHandler{
    private final String logTag = "LocalCNNThread";
    private CaffeMobile caffeMobile;

    private Handler requestHandler = null;
    public LocalCNNThread(Handler handler)
    {
        requestHandler = handler;

        caffeMobile = new CaffeMobile();
        String prototxt = DataEngine.get().getRootDir()+"deploy.prototxt";
        String model = DataEngine.get().getRootDir()+"dogcat.caffemodel";
        caffeMobile.loadModel(prototxt, model);
    }

    static {
        System.load( DataEngine.get().getRootDir()+ "libcaffe.so");
        System.load( DataEngine.get().getRootDir()+ "libcaffe_jni.so");
    }

    @Override
    protected void onHandleMessage(Message message)
    {
        switch (message.what) {
            case R.id.process_img:
                Log.i(logTag, "receive R.id.upload_img");

                int id = (Integer) message.obj;
                int predict = caffeMobile.predictImage(DataEngine.get().getImgFilePath(id));

                Message uploadOKMsg = requestHandler.obtainMessage(R.id.process_ok, predict);
                uploadOKMsg.sendToTarget();

                DataEngine.get().deleteImgFile(id);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onExit()
    {
        getHandler().removeMessages(R.id.process_img);
    }
}
