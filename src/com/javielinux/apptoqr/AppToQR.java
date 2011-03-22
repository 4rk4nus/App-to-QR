/*
 * Copyright (C) 2008  Javier Perez Pacheco
 *
 * App to QR: Para compartir aplicaciones en Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Javier Perez Pacheco
 * Cadiz (Spain)
 * javi.pacheco@gmail.com
 *
 */

package com.javielinux.apptoqr;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.apptoqr.AppInstalledAsyncTask.AppInstalledAsyncTaskResponder;

public class AppToQR extends Activity implements AppInstalledAsyncTaskResponder {
	
	private static final int DIALOG_ITEM_INSTALLED = 1;
	private static final int DIALOG_ITEM_OTHER = 2;
	
	private static final int SHAREALL_ID = Menu.FIRST;
	
	private int selectPos = -1;
	
	private RowAppAdapter mInstallesApps;
	private RowAppDBAdapter mFavoritesApps;
	private RowAppDBAdapter mForInstalledApps;
	private ProgressDialog pd = null;
	
	private ListView mListViewInstalledApps, mListViewFavoritesApps, mListViewLaterApps;
	
	private TextView mNoFavorites, mNoLater;
	
	private WorkspaceView mWorkView;
	
	private LinearLayout mLLContent;
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ITEM_INSTALLED:
            return new AlertDialog.Builder(AppToQR.this)
                .setTitle(R.string.select_action)
                .setItems(R.array.select_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                        	showCodeQR();
                        } else if (which==1) {
                        	shareQR();
                        } else if (which==2) {
                        	shareLink();
                        } else if (which==3) {
                        	copyToClipboard();
                        	Utils.showMessage(AppToQR.this, AppToQR.this.getString(R.string.to_clipboard));
                        } else if (which==4) {
                        	searchMarket();
                        } else if (which==5) {
                        	goToBubiloop();
                        } else if (which==6) {
                        	Utils.showMessage(AppToQR.this, AppToQR.this.getString(R.string.image_save) + " " + saveCodeQR());
                        } else if (which==7) {
                        	addToFavorites();
                        }
                    }
                })
                .create();
        case DIALOG_ITEM_OTHER:
            return new AlertDialog.Builder(AppToQR.this)
                .setTitle(R.string.select_action)
                .setItems(R.array.select_actions_others, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                        	showCodeQR();
                        } else if (which==1) {
                        	shareQR();
                        } else if (which==2) {
                        	shareLink();
                        } else if (which==3) {
                        	copyToClipboard();
                        	Utils.showMessage(AppToQR.this, AppToQR.this.getString(R.string.to_clipboard));
                        } else if (which==4) {
                        	searchMarket();
                        } else if (which==5) {
                        	goToBubiloop();
                        } else if (which==6) {
                        	Utils.showMessage(AppToQR.this, AppToQR.this.getString(R.string.image_save) + " " + saveCodeQR());
                        } else if (which==7) {
                        	removeApp();
                        }
                    }
                })
                .create();
        }
        return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        setContentView(R.layout.tabbed);
        
        Utils.createAppDirectory();
        
        mLLContent = (LinearLayout)findViewById(R.id.app_content);
        
        mWorkView = new WorkspaceView(this, null);
        mWorkView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        mListViewInstalledApps = new ListView(this);
        mListViewInstalledApps.setFastScrollEnabled(true);
        mListViewInstalledApps.setTextFilterEnabled(true);
        
        mListViewInstalledApps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClick(mListViewInstalledApps, view, position, id);				
			}
        	
        });
        
        mListViewFavoritesApps = new ListView(this);
        mListViewFavoritesApps.setFastScrollEnabled(true);
        mListViewFavoritesApps.setTextFilterEnabled(true);
        
        mListViewFavoritesApps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClick(mListViewFavoritesApps, view, position, id);				
			}
        	
        });
                
        mNoFavorites = new TextView(this);
        mNoFavorites.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mNoFavorites.setGravity(Gravity.CENTER);
        mNoFavorites.setClickable(true);
        mNoFavorites.setText(R.string.no_favorites);
        
        mListViewLaterApps = new ListView(this);
        mListViewLaterApps.setFastScrollEnabled(true);
        mListViewLaterApps.setTextFilterEnabled(true);
        
        mListViewLaterApps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClick(mListViewLaterApps, view, position, id);				
			}
        	
        });
        
        mNoLater = new TextView(this);
        mNoLater.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mNoLater.setGravity(Gravity.CENTER);
        mNoLater.setClickable(true);
        mNoLater.setText(R.string.no_for_installed);
        
        pd = ProgressDialog.show(this, this.getText(R.string.loading), this.getText(R.string.loading_app), true, false);
                
		new AppInstalledAsyncTask(this, this).execute();
                
		super.onCreate(savedInstanceState);
		
    }
    
    void refreshViews() {
    	int screen = mWorkView.getCurrentScreen();
    	mWorkView.removeAllViews();
    	mWorkView.clearTabs();
        mWorkView.addView(mListViewInstalledApps);
        int countFavorites = DataFramework.getInstance().getEntityListCount("apps", "type="+Utils.TYPE_FAVORITES);
        int countForInstalled = DataFramework.getInstance().getEntityListCount("apps", "type="+Utils.TYPE_FOR_INSTALL);
        if (countFavorites>0) {
        	mFavoritesApps = new RowAppDBAdapter(this, DataFramework.getInstance().getEntityList("apps", "type="+Utils.TYPE_FAVORITES, "name asc"));
        	mListViewFavoritesApps.setAdapter(mFavoritesApps);
        	mWorkView.addView(mListViewFavoritesApps);
        } else {
        	mWorkView.addView(mNoFavorites);
        }
        if (countForInstalled>0) {
        	mForInstalledApps = new RowAppDBAdapter(this, DataFramework.getInstance().getEntityList("apps", "type="+Utils.TYPE_FOR_INSTALL, "name asc"));
        	mListViewLaterApps.setAdapter(mForInstalledApps);
        	mWorkView.addView(mListViewLaterApps);
        } else {
        	mWorkView.addView(mNoLater);
        }
        
        //mWorkView.removeAllViews();
		
        mWorkView.configTabs((LinearLayout)findViewById(R.id.app_tabs), R.drawable.tab, R.drawable.tab_sel, Color.GRAY, Color.WHITE, 
				new String[]{getString(R.string.installed), getString(R.string.favorites), getString(R.string.for_installed)});
		
        mLLContent.removeAllViews();
        
        mLLContent.addView(mWorkView);
        
        mWorkView.scrollToScreen(screen);
    }
    
    /**
     * Crea el menu
     * 
     * @param menu Menu
     * @return Boleano
     * 
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SHAREALL_ID, 0,  R.string.share_all)
			.setIcon(android.R.drawable.ic_menu_share);
        return true;
    }

    /**
     * Se ejecuta al pulsar un boton del menu
     * 
     * @param featureId
     * @param item boton pulsado del menu
     * @return Si se ha pulsado una opcion
     * 
     */
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case SHAREALL_ID:
        	shareAll();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    

	@Override
	public void appInstalledLoading() {		
	}

	@Override
	public void appInstalledCancelled() {		
	}

	@Override
	public void appInstalledLoaded(RowAppAdapter apps) {
		this.mInstallesApps = apps;
		mListViewInstalledApps.setAdapter(apps);
		refreshViews();
		if (pd!=null) {
			pd.dismiss();
			pd = null;
		}
	}     
	
	private App getAppSelected() {
    	App content;
    	int screen = mWorkView.getCurrentScreen();
    	if (screen == 0) {
    		content = (App)mInstallesApps.getItem(selectPos);
    	} else if (screen == 1) {
    		content = mFavoritesApps.getApp(selectPos);
    	} else {
    		content = mForInstalledApps.getApp(selectPos);
    	}
    	return content;
	}
	
	private void showCodeQR() {
		if (selectPos>=0) {
			
			App content	= getAppSelected();
			
	    	String appURL = "market://search?q=pname:" + content.packageName;
	    		    	
	    	Intent intent = new Intent(this, QRCode.class);
	    	intent.putExtra("package_name", content.packageName);
	    	intent.putExtra("app_name", content.name);
	        intent.putExtra("app_url", appURL);
	        intent.putExtra("icon", content.resIdImage);
	        intent.putExtra("index", content.index);
	        startActivity(intent);
		}
	}
	
	private void addToFavorites() {		
		if (selectPos>=0) {
	    	App content = (App)mInstallesApps.getItem(selectPos);
	    	
	    	String path = Utils.getPathIcon();
	    	try {
				FileOutputStream outPath;
				outPath = new FileOutputStream(path);
				content.bmp.compress(Bitmap.CompressFormat.PNG, 90, outPath);
				outPath.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	    	Entity ent = new Entity("apps");
	    	ent.setValue("type", Utils.TYPE_FAVORITES);
	    	ent.setValue("name", content.name);
	    	ent.setValue("package", content.packageName);
	    	ent.setValue("icon", path);
	    	ent.save();
	    	Utils.showMessage(this, getString(R.string.ok_favorites));
	    	refreshViews();
		}
	}
	
	private void removeApp() {
		if (selectPos>=0) {
			App content	= getAppSelected();
	    	Entity ent = new Entity("apps", content.idDB);
	    	ent.delete();
	    	Utils.showMessage(this, getString(R.string.ok_remove_app));
	    	refreshViews();
		}
	}
	
	private String saveCodeQR() {
		String filename = "";
		
		if (selectPos>=0) {
			App content	= getAppSelected();
	    	
	    	String appURL = "http://chart.apis.google.com/chart?cht=qr&chs=250x250&chl=market://search?q=pname:" + content.packageName;
	    
	    	URL urlImage;
			try {
				
				filename = Utils.appDirectory + content.name + ".png";
				
				urlImage = new URL(appURL);
				URLConnection conn = urlImage.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				Bitmap bmp = BitmapFactory.decodeStream(bis, null, null);
				
				FileOutputStream out = new FileOutputStream(filename);
		        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
				
				bis.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return filename;
	}
	
	private void shareQR() {
		if (selectPos>=0) {
			
			App content	= getAppSelected();
	    	
	    	String appURL = "http://market.android.com/search?q=pname:" + content.packageName;
			
			String body = this.getResources().getString(R.string.share_body_name) + " " + content.name
						+ "\n" + this.getResources().getString(R.string.share_body_url) + " " + appURL;
			
			Intent msg=new Intent(Intent.ACTION_SEND);
			msg.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.share_subject));
			msg.putExtra(Intent.EXTRA_TEXT, body);
			String file = saveCodeQR();
			if (file.equals("")) {
				msg.setType("text/plain");
			} else {
				msg.setType("image/png");
				msg.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
			}
    		startActivity(msg);
    	}
	}
	
	private void shareLink() {
		if (selectPos>=0) {
			
			App content	= getAppSelected();
	    	
	    	String appURL = "http://market.android.com/search?q=pname:" + content.packageName;
			
			String body = this.getResources().getString(R.string.share_body_name) + " " + content.name
						+ "\n" + this.getResources().getString(R.string.share_body_url) + " " + appURL;
			
			Intent msg=new Intent(Intent.ACTION_SEND);
			msg.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.share_subject));
			msg.putExtra(Intent.EXTRA_TEXT, body);
			msg.setType("text/plain");
    		startActivity(msg);
    	}
	}
	
	private void shareAll() {
		
		String body = "";
		
		for (int i=0; i<mInstallesApps.getCount(); i++) {
			App content = (App)mInstallesApps.getItem(i);
			String appURL = "http://market.android.com/search?q=pname:" + content.packageName;
			
			body += this.getResources().getString(R.string.share_body_name) + " " + content.name
				+ "\n" + this.getResources().getString(R.string.share_body_url) + " " + appURL + "\n\n";
		}
		
		Intent msg=new Intent(Intent.ACTION_SEND);
		msg.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.share_subject_all));
		msg.putExtra(Intent.EXTRA_TEXT, body);
		msg.setType("text/plain");
		startActivity(msg);

	}
	
	private void copyToClipboard() {
		if (selectPos>=0) {
			App content	= getAppSelected();
	    	String appURL = "market://search?q=pname:" + content.packageName;
	    	ClipboardManager clipboard =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
	        clipboard.setText(appURL);
		}
	}
	
	private void searchMarket() {
		if (selectPos>=0) {
			App content	= getAppSelected();
	    	String appURL = "market://search?q=pname:" + content.packageName;
	    	Intent i = new Intent(Intent.ACTION_VIEW);
	    	Uri u = Uri.parse(appURL);
	    	i.setData(u);
	    	startActivity(i);
		}
	}
	
	private void goToBubiloop() {
		if (selectPos>=0) {
			App content	= getAppSelected();
	    	String appURL = "http://bubiloop.com/search/pname:" + content.packageName;
	    	Intent i = new Intent(Intent.ACTION_VIEW);
	    	Uri u = Uri.parse(appURL);
	    	i.setData(u);
	    	startActivity(i);
		}
	}

    protected void onListItemClick (ListView l, View v, int position, long id) {
    	selectPos = position;
    	if (mListViewInstalledApps==l) {
    		showDialog(DIALOG_ITEM_INSTALLED);
    	} else {
    		showDialog(DIALOG_ITEM_OTHER);
    	}
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
}