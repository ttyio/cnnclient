package com.camera.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vincenth(ttyio@hotmail.com)
 */
public class IOHelper {
	public static int pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[100000];
		int nread, total = 0;
		synchronized (in) {
			while ((nread = in.read(buf, 0, buf.length)) >= 0) {
				out.write(buf, 0, nread);
				total += nread;
			}
		}
		out.flush();
		return total;
	}

	public static boolean saveFile(InputStream inputStream,File outputFile)
	{
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			IOHelper.pipe(inputStream,bos);
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}

	public static boolean saveFile(byte[] data,File outputFile)
	{
		try {
			FileOutputStream output = new FileOutputStream(outputFile);
			output.write(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
