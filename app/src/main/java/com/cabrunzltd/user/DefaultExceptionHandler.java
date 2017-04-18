package com.cabrunzltd.user;

import android.app.Activity;
import android.content.Intent;

import java.lang.Thread.UncaughtExceptionHandler;


/**
 * This custom class is used to handle exception.
 *
 * 
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler {
 
    private UncaughtExceptionHandler defaultUEH;
    Activity activity;
 
    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }
 
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
 
        try {
 
            Intent intent = new Intent(activity, MainDrawerActivity.class);
 
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
 
//            PendingIntent pendingIntent = PendingIntent.getActivity(
//            		activity, 0, intent, intent.getFlags());
 
                        //Following code will restart your application after 2 seconds
//            AlarmManager mgr = (AlarmManager) AppController.getInstance().getBaseContext()
//                    .getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
//                    pendingIntent);
 
                        //This will finish your activity manually
            activity.finish();
 
                        //This will stop your application and take out from it.
            System.exit(2);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}