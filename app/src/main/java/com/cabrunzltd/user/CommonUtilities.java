/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cabrunzltd.user;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */

public final class CommonUtilities {

	/**
	 * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
	 */

	/**
	 * Google API project id registered to use GCM.  610954502970
	 */
	//public static final String SENDER_ID = "610954502970"; // "545172754837";
//	 public static final String SENDER_ID = "258832612834";//185627800666
	public static final String SENDER_ID = "1061302659630";

	/**128951785392
	 * 
	 * AIzaSyBy7M2kclFjOISdXFnTr6SHdRe3BYRn6g4
	 * Tag used on log messages.
	 */
	public static final String TAG = "GCMDemo";

	/**
	 * Intent used to display a message in the screen.
	 */
	public static final String DISPLAY_MESSAGE_ACTION = "com.uberorg.DISPLAY_MESSAGE";
	public static final String DISPLAY_REGISTER_GCM = "com.uberorg.REGISTER_GCM";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public static final String EXTRA_MESSAGE = "message";
	public static final String RESULT = "result";
	public static final String REGID = "regid";
	public static String aaa="";
	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
