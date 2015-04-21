package com.camera.data;

import java.io.File;
import com.camera.util.IOHelper;

import android.content.Context;
import android.os.Environment;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class DataEngine {
	private File rootDir = null;

	private static DataEngine dataEngine = null;
	public static void init(Context context)
	{
		dataEngine = new DataEngine(context);
	}
	public static DataEngine get()
	{
		return dataEngine;
	}

	private DataEngine(Context context)
	{
		File cardDir = Environment.getExternalStorageDirectory();
		rootDir = new File(cardDir,"MobileSearchData");
		rootDir.mkdir();
	}

    public String getRootDir()
    {
        return rootDir + "//";
    }

    public String getImgFilePath(int id)
    {
        return rootDir + "//" + Integer.toString(id) + ".jpg";
    }

    public File getImgFile(int id)
    {
        return new File(rootDir, Integer.toString(id)+".jpg");
    }

    public int createImgFile(byte[] data)
    {
        File outputFile = getImgFile(0);
        IOHelper.saveFile(data, outputFile);
        return 0;
    }

    public void deleteImgFile(int id)
    {
        getImgFile(id).delete();
    }
}
