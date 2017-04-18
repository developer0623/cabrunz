package com.cabrunzltd.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.splunk.mint.Mint;

//import org.apache.http.util.TextUtils;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    private TextView btnSignIn, btnRegister;
    private PreferenceHelper pHelper;
    private boolean isReceiverRegister;
    private int oldOptions;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		/*
		 * BugSenseHandler.initAndStartSession(MainActivity.this, "18c8b221");
		 * BugSenseHandler.initAndStartSession(MainActivity.this, "0ac4dfbb");
		 */
        Mint.initAndStartSession(MainActivity.this, "bac6b0da");
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));




        //CustomActivityOnCrash.install(this);
        if (isExternalStorageWritable() == false) {
            Toast.makeText(getApplicationContext(),
                    "No SD Card Please insert !", Toast.LENGTH_LONG).show();

        } else {

            if (!TextUtils.isEmpty(new PreferenceHelper(this).getUserId())) {
                if (new PreferenceHelper(this).isPamentRequest()) {
                    startActivity(new Intent(this, FriendPayActivity.class));
                    this.finish();
                    return;
                } else {
                    startActivity(new Intent(this, MainDrawerActivity.class));
                    this.finish();
                    return;
                }
            }
        }
        isReceiverRegister = false;
        pHelper = new PreferenceHelper(this);
        setContentView(R.layout.activity_main);

        btnSignIn = (TextView) findViewById(R.id.btnSignIn);
        btnRegister = (TextView) findViewById(R.id.btnRegister);
        // rlLoginRegisterLayout = (RelativeLayout) view
        // .findViewById(R.id.rlLoginRegisterLayout);
        // tvMainBottomView = (MyFontTextView) view
        // .findViewById(R.id.tvMainBottomView);
        ////////btnSignIn.setOnClickListener(this);
        ////////btnRegister.setOnClickListener(this);

        if (TextUtils.isEmpty(pHelper.getDeviceToken())) {
            isReceiverRegister = true;
            AndyUtils.showCustomProgressDialog(this, getString(R.string.progress_loading), false, null);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    AndyUtils.removeCustomProgressDialog();

                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    Toast.makeText(MainActivity.this, CommonUtilities.aaa, Toast.LENGTH_SHORT).show();

                    CommonUtilities.displayMessage(context, "Device Registerd");
                    new PreferenceHelper(context).putDeviceToken(CommonUtilities.aaa);
                    if (sentToken) {
                        Toast.makeText(MainActivity.this, "gcm success",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.register_gcm_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

            registerReceiver();
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

//            registerGcmReceiver(mRegistrationBroadcastReceiver);
        }
    }

    private void registerReceiver(){
        if(isReceiverRegister) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegister = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegister = true;
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
//
//    public void registerGcmReceiver(BroadcastReceiver mRegistrationBroadcastReceiver) {
//        if (mRegistrationBroadcastReceiver != null) {
//            AndyUtils.showCustomProgressDialog(this, getString(R.string.progress_loading), false, null);
//            new GCMRegisterHendler(this, mHandleMessageReceiver);
//
//        }
//    }

//    public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
//        if (mHandleMessageReceiver != null) {
//
//            if (mHandleMessageReceiver != null) {
//                unregisterReceiver(mHandleMessageReceiver);
//            }
//
//        }
//
//    }

//    private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            AndyUtils.removeCustomProgressDialog();
//            if (intent.getAction().equals(CommonUtilities.DISPLAY_REGISTER_GCM)) {
//                Bundle bundle = intent.getExtras();
//                if (bundle != null) {
//
//                    int resultCode = bundle.getInt(CommonUtilities.RESULT);
//                    if (resultCode == Activity.RESULT_OK) {
//
//                    } else {
//                        Toast.makeText(MainActivity.this,
//                                getString(R.string.register_gcm_failed),
//                                Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//
//                }
//            }
//        }
//    };

//    private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            AndyUtils.removeCustomProgressDialog();
//            if (intent.getAction().equals(CommonUtilities.DISPLAY_REGISTER_GCM)) {
//                Bundle bundle = intent.getExtras();
//                if (bundle != null) {
//
//                    int resultCode = bundle.getInt(CommonUtilities.RESULT);
//                    if (resultCode == Activity.RESULT_OK) {
//
//                    } else {
//                        Toast.makeText(MainActivity.this,
//                                getString(R.string.register_gcm_failed),
//                                Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//
//                }
//            }
//        }
//    };

    //@Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent startRegisterActivity = new Intent(MainActivity.this,
                RegisterActivity.class);
        switch (v.getId()) {
            case R.id.btnSignIn:
                startRegisterActivity.putExtra("isSignin", true);
                break;
            case R.id.btnRegister:
                startRegisterActivity.putExtra("isSignin", false);
                break;
        }
        startActivity(startRegisterActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void signin_clicked(View v){
//        Intent startRegisterActivity = new Intent(MainActivity.this,
//                RegisterActivity.class);
//        startRegisterActivity.putExtra("isSignin", true);
        Intent i=new Intent(this, RegisterActivity.class);
        i.putExtra("isSignin", true);
        startActivityForResult(i,1);

        finish();
    }

    public void register_clicked(View v){
//        Intent startRegisterActivity = new Intent(MainActivity.this,
//                RegisterActivity.class);
//        startRegisterActivity.putExtra("isSignin", false);
        Intent i=new Intent(this, RegisterActivity.class);
        i.putExtra("isSignin", false);
        startActivityForResult(i,1);

        finish();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
//        if (isReceiverRegister) {
//            unregisterGcmReceiver(mHandleMessageReceiver);
//            isReceiverRegister = false;
//        }
        super.onDestroy();
    }
}
