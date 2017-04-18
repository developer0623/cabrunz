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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.cabrunzltd.user.R;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;
//import com.google.android.gms.gcm.GcmPubSub;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService  {

	@SuppressWarnings("hiding")

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	public void onHandleIntent(Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		try {
			synchronized (TAG) {
				//InstanceID instanceID = InstanceID.getInstance(this);
				//String token = instanceID.getToken(getString(R.string.applicationId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
				SharedPreferences settings =
						getSharedPreferences("" + Const.TAG, MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				// Log.d("device token", token);
				//editor.putString("deviceToken", token);
				editor.commit();

			}
		} catch (Exception e) {

		}
		//
	}



	//@Override
	protected void onRegistered(Context context, String registrationId) {
		// Log.i(TAG, "Device registered: regId = " + registrationId);
		CommonUtilities.displayMessage(context, "Device Registerd");
		// SSConstanants.DEVICE_TOKEN=registrationId;
		// Create object of SharedPreferences.
		new PreferenceHelper(context).putDeviceToken(registrationId);
		// System.out.println(registrationId + "========>>>>>>");
		AppLog.Log(Const.TAG, registrationId);
		publishResults(registrationId, Activity.RESULT_OK);
		// GCMRegisterHendler.onRegComplete(registrationId);

		/*************************
		 * ParseObject pObj = new ParseObject("PushNoti");
		 * pObj.put("DeviceToken",registrationId); pObj.put("InRange",true);
		 * pObj.put("DeviceType","android"); //pObj.put("ACL","");
		 * pObj.saveInBackground(); displayMessage(context,
		 * getString(R.string.gcm_registered)); //
		 * ServerUtilities.register(context, registrationId);
		 * 
		 */
	}

	///////////@Override
	protected void onUnregistered(Context context, String registrationId) {
		// Log.i(TAG, "Device unregistered");
		CommonUtilities.displayMessage(context, "Device Unregistered");
//		if (GCMRegistrar.isRegisteredOnServer(context)) {
//
//		} else {
//			// This callback results from the call to unregister made on
//			// ServerUtilities when the registration to the server failed.
//			// Log.i(TAG, "Ignoring unregister callback");
//		}
	}

	//@Override
	protected void onMessage(Context context, Intent intent) {
		// Log.e(TAG, "Received message");
		// String message = getString(R.string.gcm_message);

		Log.e("push notification", intent.getExtras() + "");
		Log.d("mahi", "push message" + intent.getExtras());
		Log.d("pavan", "intent " + intent.getExtras());
		String message = intent.getExtras().getString("message");
		String team = intent.getExtras().getString("team");
		String title = intent.getExtras().getString("title");
		Intent pushIntent = new Intent(Const.INTENT_WALKER_STATUS);
		pushIntent.putExtra(Const.EXTRA_WALKER_STATUS, team);

		CommonUtilities.displayMessage(context, message);

		generateNotification(context, message);

		LocalBroadcastManager.getInstance(context)
				.sendBroadcast(pushIntent);
		

		int unique_id;
		String team2 = "";
		String pushid = "";
		String name = "";
		String picture = "";
		String friend_id = "";
		String actual_total = "";
		try {
			team2 = intent.getExtras().getString("team");
			JSONObject aa = new JSONObject(team2);
			if (aa.has("push_id")) {

				pushid = aa.getString("push_id");

				if (pushid.equals("9")) {
					actual_total = aa.getString("actual_total");
				} else
					name = aa.getString("pay_user_name");
				picture = aa.getString("pay_user_image");
				friend_id = aa.getString("pay_user_id");
			}
			if (aa.has("unique_id"))
				unique_id = aa.getInt("unique_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		

			
/*
			// notifies user
			if (unique_id == 10) {
				pushIntent = new Intent(Const.INTENT_WALKER_STATUS);
				pushIntent.putExtra("message", team);
				generateNotification(context, message, unique_id, team);
			} else if (unique_id == 1) {

				generateNotification(context, message, unique_id);

			}*/
		

		if (pushid.equals("3")) {
			Intent i = new Intent("PAY_FRIEND");
			i.putExtra("name", name);
			i.putExtra("picture", picture);
			i.putExtra("friend_id", friend_id);
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		} else if (pushid.equals("4")) {
			Intent i = new Intent("PAY_ACCEPT");
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		} else if (pushid.equals("10")) {
			Intent i = new Intent("PAY_REJECT");
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		} else if (pushid.equals("6")) {
			Intent i = new Intent("NOT_RESPOND_FOR_USER");
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		} else if (pushid.equals("5")) {
			Intent i = new Intent("NOT_RESPOND_FOR_FRIEND");
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		} else if (pushid.equals("8")) {
			Intent i = new Intent("pay_success_user");
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		} else if (pushid.equals("9")) {
			Intent i = new Intent("PAY_SUCCESS");
			i.putExtra("actual_total", actual_total);
			context.sendBroadcast(i);
			generateNotification(context, message);
			LocalBroadcastManager.getInstance(context)
			.sendBroadcast(i);

		}

	}

	//////////////@Override
	protected void onDeletedMessages(Context context, int total) {
		// Log.i(TAG, "Received deleted messages notification");
		String message = "message deleted " + total;
		CommonUtilities.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	////////////////@Override
	public void onError(Context context, String errorId) {
		// Log.i(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));
	}

//	@Override
//	protected boolean onRecoverableError(Context context, String errorId) {
//		// log message
//		// Log.i(TAG, "Received recoverable error: " + errorId);
//		// displayMessage(context,
//		// getString(R.string.gcm_recoverable_error, errorId));
//		return super.onRecoverableError(context, errorId);
//	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */

	private static void generateNotification(Context context, String message) {

		// System.out.println("this is message " + message);
		// System.out.println("NOTIFICATION RECEIVED!!!!!!!!!!!!!!" + message);
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = null;

		notificationIntent = new Intent(context, MainDrawerActivity.class);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		//
		LocalBroadcastManager.getInstance(context).sendBroadcast(
				notificationIntent);

		////////////////////notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// System.out.println("notification====>" + message);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledARGB = 0x00000000;
		notification.ledOnMS = 0;
		notification.ledOffMS = 0;
		notificationManager.notify(0, notification);
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = pm.newWakeLock(
				PowerManager.FULL_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.ON_AFTER_RELEASE, "WakeLock");
		wakeLock.acquire();
		wakeLock.release();

	}


	private void publishResults(String regid, int result) {
		Intent intent = new Intent(CommonUtilities.DISPLAY_REGISTER_GCM);
		intent.putExtra(CommonUtilities.RESULT, result);
		intent.putExtra(CommonUtilities.REGID, regid);
		System.out.println("sending broad cast");
		/////////////////sendBroadcast(intent);
	}

}
