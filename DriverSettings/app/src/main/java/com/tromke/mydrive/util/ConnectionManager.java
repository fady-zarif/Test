/* ************************************************
 * CopyRight Brillio. All rights reserved
 * 
 * Filename			ConnectionManager.java
 * Revised			May 28, 2014  2:13:51 PM
 * Revision			Revision 0.1
 * Description      
 * ************************************************/
package com.tromke.mydrive.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManager {
	private Context context;
	private static ConnectionManager instance;

	public ConnectionManager(Context context) {
		this.context = context;
	}

	public static ConnectionManager getInstance(Context context) {
		if (instance == null) {
			instance = new ConnectionManager(context);
		}
		return instance;
	}

	public boolean isDeviceConnectedToInternet() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
}
