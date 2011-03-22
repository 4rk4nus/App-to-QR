package com.javielinux.apptoqr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.javielinux.apptoqr.AddAppForInstallAsyncTask.AddAppForInstalAsyncTaskResponder;

public class AppLater extends Activity implements AddAppForInstalAsyncTaskResponder {

	private String mNamePackage;
	private ProgressDialog pd;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
        String action = intent.getAction();
        
        String app = "";
        
        if (Intent.ACTION_SEND.equals(action)) {
        	
        	if ("text/plain".equals(intent.getType())) {
        		app = intent.getStringExtra(Intent.EXTRA_TEXT);
        	}
        }
        
        mNamePackage = getPackage(app);
        
        if (mNamePackage.equals("")) {
        	Utils.showMessage(this, getString(R.string.url_no_valid));
        } else {
        	pd = ProgressDialog.show(this, this.getText(R.string.searching), this.getText(R.string.searching_app), true, false);
        	new AddAppForInstallAsyncTask(this, this).execute(mNamePackage);
        	//searchAndroidMarket(mNamePackage);
        }
        
	}
	
	private String getPackage(String url) {
		
		if (url.startsWith("https://market.android.com/details?id=") ||
				url.startsWith("http://market.android.com/details?id=") ||
				url.startsWith("market://details?id=")) {
			url = url.replace("https://market.android.com/details?id=", "");
			url = url.replace("http://market.android.com/details?id=", "");
			url = url.replace("market://details?id=", "");
			if (url.contains("&")) {
				url = url.substring(0,url.indexOf("&"));
			}
			return url;
		}
		
		return "";
	}
	

	@Override
	public void appForInstallLoading() {

	}

	@Override
	public void appForInstallCancelled() {

	}

	@Override
	public void appForInstallLoaded(Integer out) {
		if (pd!=null) {
			pd.dismiss();
			pd = null;
		}
		if (out==AddAppForInstallAsyncTask.TYPE_OUT_PACKAGE_NO_FOUND) {
			Utils.showMessage(this, getString(R.string.package_no_found) + " " + mNamePackage);
		} else if (out==AddAppForInstallAsyncTask.TYPE_OUT_GOOD) {
			Utils.showMessage(this, getString(R.string.url_saved));
		} else {
			Utils.showMessage(this, "Error no especificado");
		}
        setResult(RESULT_OK);
        finish();
	}
	
}
