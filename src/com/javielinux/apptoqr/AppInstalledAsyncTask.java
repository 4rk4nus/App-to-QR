package com.javielinux.apptoqr;

import android.content.Context;
import android.os.AsyncTask;

public class AppInstalledAsyncTask extends AsyncTask<Void, Void, RowAppAdapter> {

	public interface AppInstalledAsyncTaskResponder {
		public void appInstalledLoading();
		public void appInstalledCancelled();
		public void appInstalledLoaded(RowAppAdapter apps);
	}
	
	private Context mContext;

	private AppInstalledAsyncTaskResponder responder;

	public AppInstalledAsyncTask(Context context, AppInstalledAsyncTaskResponder responder) {
		this.mContext = context;
		this.responder = responder;
	}

	@Override
	protected RowAppAdapter doInBackground(Void... args) {
		try {
			return new RowAppAdapter(mContext);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.appInstalledLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.appInstalledCancelled();
	}

	@Override
	protected void onPostExecute(RowAppAdapter apps) {
		super.onPostExecute(apps);
		responder.appInstalledLoaded(apps);
	}

}
