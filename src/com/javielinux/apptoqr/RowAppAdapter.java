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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RowAppAdapter extends BaseAdapter {

    private Context mContext;
    private List<App> list; 
	
    public RowAppAdapter(Context mContext)
    {
        this.mContext = mContext;
        
        List<ApplicationInfo> listInfo = new ArrayList<ApplicationInfo>();
        
        list = new ArrayList<App>();
    	list.clear();
    	PackageManager appInfo = mContext.getPackageManager();
    	listInfo = appInfo.getInstalledApplications(0);
    	Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(appInfo));


    	for (int index=0; index<listInfo.size(); index++) {
    		try {
    			ApplicationInfo content = listInfo.get(index);
    			if ( (content.flags != ApplicationInfo.FLAG_SYSTEM) && content.enabled) {
    				if (content.icon!=0) {
	    				App a = new App();
	    				a.index = index;
	    				a.type = App.TYPE_INSTALLED;
	    				a.resIdImage = content.icon;
	    				//a.bmp = BitmapFactory.decodeResource(mContext.getResources(), content.icon);
	    				//a.image = mContext.getPackageManager().getDrawable(content.packageName, content.icon, content);
	    				Drawable d = mContext.getPackageManager().getDrawable(content.packageName, content.icon, content);
	    				a.bmp = ((BitmapDrawable)d).getBitmap();
	    				a.name = (String) mContext.getPackageManager().getApplicationLabel(content);
	    				a.packageName = content.packageName;
	    				list.add(a);
    				}
    			}
    		} catch (Exception e) {
    			
    		}
	    } 
    
    }
        
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
        return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		App item = list.get(position);
        View v;
        
        if (convertView==null)
        	v = View.inflate(mContext, R.layout.app_row, null);
        else 
        	v = convertView;
        
        ImageView img = (ImageView)v.findViewById(R.id.image);
       	img.setImageBitmap(item.bmp);
        
        TextView fn = (TextView)v.findViewById(R.id.name);       
        fn.setText(item.name);
        
        return v;
	}

}
