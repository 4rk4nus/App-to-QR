package com.javielinux.apptoqr;

import java.io.File;

import android.content.Context;
import android.widget.Toast;

public class Utils {

	static public String appDirectory = "/sdcard/app2qr/";
	static public String packageName = "com.javielinux.apptoqr";
	
	static public int TYPE_FAVORITES = 0;
	static public int TYPE_FOR_INSTALL = 1;
	
    static public void createAppDirectory() {
    	try {
    		File dir = new File(appDirectory);
    		if (!dir.exists()) dir.mkdir();
    		File fileNomedia = new File(appDirectory+".nomedia");
            if (!fileNomedia.exists()) fileNomedia.createNewFile();
    	} catch (Exception ioe) {
    		ioe.printStackTrace();
    	}
    }
    
    static void showMessage(Context context, String msg) {
	    Toast.makeText(context, 
	    		msg, 
	            Toast.LENGTH_LONG).show();
    }
    
    static String getPathIcon() {
    	int count = 1;
		String tokenFile = "app_1";
		String file = Utils.appDirectory + tokenFile+".png";
		File f = new File(file);
		while (f.exists()) {
			count++;
			tokenFile = "search_"+count;
			file = Utils.appDirectory + tokenFile+".png";
			f = new File(file);
		}
    	return file;
    }
	
}
