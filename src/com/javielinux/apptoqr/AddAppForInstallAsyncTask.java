package com.javielinux.apptoqr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.GetImageRequest;
import com.gc.android.market.api.model.Market.GetImageRequest.AppImageUsage;
import com.gc.android.market.api.model.Market.GetImageResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

public class AddAppForInstallAsyncTask extends AsyncTask<String, Void, Integer> {
	
	public static int TYPE_OUT_GOOD = 0;
	public static int TYPE_OUT_PACKAGE_NO_FOUND = 1;
	public static int TYPE_OUT_ERROR = 2;

	public interface AddAppForInstalAsyncTaskResponder {
		public void appForInstallLoading();
		public void appForInstallCancelled();
		public void appForInstallLoaded(Integer apps);
	}
	
	private int out = TYPE_OUT_ERROR;

	private AddAppForInstalAsyncTaskResponder responder;
	
	private String query;
	private String mTitle;
	private String mNamePackage;

	public AddAppForInstallAsyncTask(Context context, AddAppForInstalAsyncTaskResponder responder) {
		this.responder = responder;
		
        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	protected Integer doInBackground(String... args) {
		try {
			query = args[0];
			searchAndroidMarket();
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			return TYPE_OUT_ERROR;
		}
	}
	
	
	private void searchAndroidMarket() {
		MarketSession session = new MarketSession();
		session.login("user@gmail.com","password");
    	//session.getContext.setAndroidId(myAndroidId);
		
		System.out.println("Buscando: " + query);

    	AppsRequest appsRequest = AppsRequest.newBuilder()
	        .setQuery(query)
	        .setStartIndex(0).setEntriesCount(10)
	        .setWithExtendedInfo(true)
	        .build();

    	session.append(appsRequest, new Callback<AppsResponse>() {
			@Override
			public void onResult(ResponseContext context, AppsResponse response) {
				
				if (response.getEntriesCount()>0) {
					mTitle = response.getApp(0).getTitle();
					mNamePackage = response.getApp(0).getPackageName();
					System.out.println("Vamos a buscar el icono de: " + response.getApp(0).getId());
					saveWithIcon(response.getApp(0).getId());
				} else {
					out = TYPE_OUT_PACKAGE_NO_FOUND;
					//Utils.showMessage(mContext, mContext.getString(R.string.package_no_found) + " " + mNamePackage);
				}
			}
		});
    	session.flush();
    }
	
	private void saveWithIcon(String id) {
		System.out.println("Buscando icono");
		
		MarketSession session = new MarketSession();
		session.login("javielinux@gmail.com","jl2002lj");
		
		GetImageRequest imgReq = GetImageRequest.newBuilder().setAppId(id)
			.setImageUsage(AppImageUsage.ICON)
			.setImageId("1")
			.build();
	
		session.append(imgReq, new Callback<GetImageResponse>() {
		     
			@Override
			public void onResult(ResponseContext context, GetImageResponse response) {
				try {
					System.out.println("Icono encontrado");
			    	String path = Utils.getPathIcon();
			    	try {					
						
						Bitmap bmp = BitmapFactory.decodeByteArray(response.getImageData().toByteArray(), 
								0, response.getImageData().toByteArray().length);
						
						FileOutputStream outPath;
						outPath = new FileOutputStream(path);
						bmp.compress(Bitmap.CompressFormat.PNG, 90, outPath);
						outPath.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
			    	Entity ent = new Entity("apps");
			    	ent.setValue("type", Utils.TYPE_FOR_INSTALL);
			    	ent.setValue("name", mTitle);
			    	ent.setValue("package", mNamePackage);
			    	ent.setValue("icon", path);
			    	ent.save();
			    	out = TYPE_OUT_GOOD;
					//Utils.showMessage(mContext, mContext.getString(R.string.url_saved));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		session.flush();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.appForInstallLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.appForInstallCancelled();
	}

	@Override
	protected void onPostExecute(Integer ok) {
		super.onPostExecute(ok);
		DataFramework.getInstance().close();
		responder.appForInstallLoaded(ok);
	}

}
