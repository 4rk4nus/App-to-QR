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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.Entity;

public class RowAppDBAdapter extends ArrayAdapter<Entity> {

    private Context mContext;
    private ArrayList<Entity> list; 
	
    public RowAppDBAdapter(Context mContext, ArrayList<Entity> statii) {
    	super(mContext, android.R.layout.simple_list_item_1, statii);
        this.mContext = mContext;        
        list = statii;
    
    }
        
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Entity getItem(int position) {
        return list.get(position);
	}
	
	public App getApp(int position) {
        Entity ent = list.get(position);
        App a = new App();
        a.type = App.TYPE_DB;
        a.idDB = ent.getId();
		a.imageFile = ent.getString("icon");
		a.name = ent.getString("name");
		a.packageName = ent.getString("package");
		return a;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = list.get(position);
        View v;
        
        if (convertView==null)
        	v = View.inflate(mContext, R.layout.app_row, null);
        else 
        	v = convertView;
        
        ImageView img = (ImageView)v.findViewById(R.id.image);
        img.setImageResource(R.drawable.icon);
        try {
        	img.setImageBitmap(BitmapFactory.decodeFile(item.getString("icon")));
        } catch (Exception e) {
        	
        }
        
        TextView fn = (TextView)v.findViewById(R.id.name);       
        fn.setText(item.getString("name"));
        
        return v;
	}

}
