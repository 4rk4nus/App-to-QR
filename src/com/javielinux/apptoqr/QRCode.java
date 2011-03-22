package com.javielinux.apptoqr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class QRCode extends Activity {

	private ImageView imgQR, imgIcon;
	private TextView txtName, txtPackage;
	
	private String appName = "";
	private String packageName = "";
	private String urlMarket = "";
	private String urlPage = "";
	private int resIdIcon = 0;
	private int index = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       	setContentView(R.layout.qrcode);
       	
       	if (savedInstanceState != null) {
       		appName = savedInstanceState.getString("app_name");
       		packageName = savedInstanceState.getString("package_name");
       		urlMarket = savedInstanceState.getString("app_url");
       		resIdIcon = savedInstanceState.getInt("icon");
       		index = savedInstanceState.getInt("index");
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			appName = extras.getString("app_name");
       			packageName = extras.getString("package_name");
       			urlMarket = extras.getString("app_url");
           		resIdIcon = extras.getInt("icon");
           		index = extras.getInt("index");
       		}
       	}
       	
       	urlPage = "http://chart.apis.google.com/chart?cht=qr&chs=250x250&chl=" + urlMarket;
       	
       	imgIcon = (ImageView) this.findViewById(R.id.img_icon);
       	imgQR = (ImageView) this.findViewById(R.id.img_qr);
       	txtName = (TextView) this.findViewById(R.id.app_name);
       	txtPackage = (TextView) this.findViewById(R.id.package_name);
       	
       	txtName.setText(appName);
       	txtPackage.setText(packageName);
       	
       	List<ApplicationInfo> listInfo = new ArrayList<ApplicationInfo>();
       	PackageManager appInfo = this.getPackageManager();
    	listInfo = appInfo.getInstalledApplications(0);
    	ApplicationInfo content = listInfo.get(index);
    	
    	imgIcon.setImageDrawable(appInfo.getDrawable(packageName, resIdIcon, content));
    	
    	try {
    		URL img = new URL(urlPage); 
			imgQR.setImageBitmap(BitmapFactory.decodeStream(img.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		} 

    }
	
}
