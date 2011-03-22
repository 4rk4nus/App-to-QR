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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class App {

	static int TYPE_INSTALLED = 0;
	static int TYPE_DB = 1;
	
	int type = TYPE_INSTALLED;
	long idDB;
	Bitmap bmp;

	int resIdImage;
	int index;
	String name;
	String packageName;
	String imageFile;
	
	public App () {
		
	}
	
	public Bitmap getBitmap() {
		if (type == TYPE_INSTALLED) {
			return bmp;
		} else {
			return BitmapFactory.decodeFile(imageFile);
		}
	}
	
}
