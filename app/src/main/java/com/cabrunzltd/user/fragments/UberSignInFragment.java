package com.cabrunzltd.user.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cabrunzltd.user.MainDrawerActivity;
import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.component.MyFontEdittextView;
import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.cabrunzltd.user.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import net.simonvt.menudrawer.MenuDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
public class UberSignInFragment extends UberBaseFragmentRegister
		implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private MyFontEdittextView etEmail, etPassword;
	private MyFontButton btnSignIn;
	private ImageButton btnGPlus;
	private ImageView btnFb;

	// Gplus
	private ConnectionResult mConnectionResult;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private static final int RC_SIGN_IN = 0;
	private boolean mSignInClicked;

	// FB
	private SimpleFacebook mSimpleFacebook;

	private Button btnForgetPassowrd;
	private MyFontEdittextView etVerificationCode;
	private Dialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
		// Scope scopePro = new
		// Scope("https://www.googleapis.com/auth/plus.me");
		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		activity.phelper
				.putCurrentFragment(Const.FRAGMENT_SIGNIN);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity.setTitle(getResources().getString(R.string.text_signin_small));
		activity.setIconMenu(R.drawable.taxi);
		View view = inflater.inflate(R.layout.login, container, false);
		etEmail = (MyFontEdittextView) view.findViewById(R.id.etEmail);
		etPassword = (MyFontEdittextView) view.findViewById(R.id.etPassword);
		btnSignIn = (MyFontButton) view.findViewById(R.id.btnSignIn);
		btnGPlus = (ImageButton) view.findViewById(R.id.btnGplus);
		btnFb = (ImageView) view.findViewById(R.id.btnFb);
		btnForgetPassowrd = (Button) view.findViewById(R.id.btnForgetPassword);
		btnForgetPassowrd.setOnClickListener(this);
		btnGPlus.setOnClickListener(this);
		btnSignIn.setOnClickListener(this);
		btnFb.setOnClickListener(this);
		btnSignIn.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		activity.currentFragment = Const.FRAGMENT_SIGNIN;

		activity.actionBar.setTitle(getString(R.string.text_signin_small));
		mSimpleFacebook = SimpleFacebook.getInstance(activity);
		activity.setIcon(R.drawable.icon_back);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.btnFb:
//			if (!mSimpleFacebook.isLogin()) {
//				activity.setFbTag(Const.FRAGMENT_SIGNIN);
//				mSimpleFacebook.login(new OnLoginListener() {
//
//					@Override
//					public void onFail(String arg0) {
//						// TODO Auto-generated method stub
//						Toast.makeText(activity, "fb login failed",
//								Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onException(Throwable arg0) {
//						// TODO Auto-generated method stub
//
//					}
//
//					//@Override
//					public void onThinking() {
//						// TODO Auto-generated method stub
//
//					}
//
//					//@Override
//					public void onNotAcceptingPermissions(MenuDrawer.Type arg0) {
//						// TODO Auto-generated method stub
//						// Log.w("UBER",
//						// String.format(
//						// "You didn't accept %s permissions",
//						// arg0.name()));
//					}
//
//					//@Override
//					public void onLogin() {
//						// TODO Auto-generated method stub
//						Toast.makeText(activity, "success", Toast.LENGTH_SHORT)
//								.show();
//					}
//				});
//			} else {
//				getProfile();
//			}
//			break;
//		case R.id.btnGplus:
//			mSignInClicked = true;
//			if (!mGoogleApiClient.isConnecting()) {
//				AndyUtils.showCustomProgressDialog(activity,
//						getString(R.string.text_getting_info), true, null);
//
//				mGoogleApiClient.connect();
//			}
//			break;
			case R.id.btnSignIn:
				if (isValidate()) {
					login();
				}
				break;
			case R.id.btnForgetPassword:
				activity.addFragment(new ForgetPasswordFragment(), true,
						Const.FOREGETPASS_FRAGMENT_TAG);
				break;

			default:
				break;
		}
	}

	private void getProfile() {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_getting_info), true, null);

		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onComplete(Profile profile) {
				AndyUtils.removeCustomProgressDialog();
				Log.i("Uber", "My profile id = " + profile.getId());
				btnGPlus.setEnabled(false);
				btnFb.setEnabled(false);
				loginSocial(profile.getId(), Const.SOCIAL_FACEBOOK);
			}
		});
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.

			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all

				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		mSignInClicked = false;
		btnGPlus.setEnabled(false);

		if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		Person currentPerson = Plus.PeopleApi
				.getCurrentPerson(mGoogleApiClient);

		String personName = currentPerson.getDisplayName();

		String personPhoto = currentPerson.getImage().toString();
		String personGooglePlusProfile = currentPerson.getUrl();
		loginSocial(currentPerson.getId(), Const.SOCIAL_GOOGLE);
		signIn();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				activity.startIntentSenderForResult(mConnectionResult
						.getResolution().getIntentSender(), RC_SIGN_IN, null,
						0, 0, 0, Const.FRAGMENT_SIGNIN);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == RC_SIGN_IN) {

			if (resultCode != Activity.RESULT_OK) {
				mSignInClicked = false;
				AndyUtils.removeCustomProgressDialog();
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else {

			//mSimpleFacebook.onActivityResult(activity, requestCode, resultCode, data);
			if (mSimpleFacebook.isLogin()) {
				getProfile();
			} else {
				Toast.makeText(activity, "facebook login failed",
						Toast.LENGTH_SHORT).show();
			}

			super.onActivityResult(requestCode, resultCode, data);

		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		String msg = null;
		if (TextUtils.isEmpty(etEmail.getText().toString())) {
			msg = getResources().getString(R.string.text_enter_email);
		} else if (!AndyUtils.eMailValidation(etEmail.getText().toString())) {
			msg = getResources().getString(R.string.text_enter_valid_email);
		}
		if (TextUtils.isEmpty(etPassword.getText().toString())) {
			msg = getResources().getString(R.string.text_enter_password);
		}
		if (msg == null)
			return true;

		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		return false;
	}

	 private void signIn() {
	 Intent intent = new Intent(activity, MainDrawerActivity.class);
	 startActivity(intent);
	 activity.finish();
	 }

	private void login() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getResources().getString(R.string.text_signing), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.EMAIL, etEmail.getText().toString());
		map.put(Const.Params.PASSWORD, etPassword.getText().toString());
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				new PreferenceHelper(activity).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, Const.MANUAL);
		Log.e("", ""+map);
		new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);

	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getResources().getString(R.string.text_signin), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				new PreferenceHelper(activity).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, loginType);
		new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);

	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		Log.e("", ""+response);
		ParseContent parseContent = new ParseContent(activity);
		AndyUtils.removeCustomProgressDialog();
		super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {
		case Const.ServiceCode.LOGIN:
			//Log.v("mahi", "login"+response);
			//09-23 16:14:37.857: E/(15495): {"success":false,"error":"Verify the OTP Code","error_code":210}
			//09-23 17:19:31.406: E/(16900): {"success":false,"error":"Verify the OTP Code","error_code":210,"owner_id":"59","token":"2y10PEY3z95S8KJ05068LM7R5unVpzpitSNegtOLzh42XFdNypowPB","otp_code":"235279"}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if(jsonObject.has("error_code")){
				if(jsonObject.getInt("error_code") == 210){
					int otp = jsonObject.getInt("otp_code");
					String tokan = jsonObject
							.getString(Const.Params.TOKEN);
					String id = jsonObject
							.getString("owner_id");
					showMobileNumberValidationDialog(otp,tokan,id);
				}
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (parseContent.isSuccessWithStoreId(response)) {
				parseContent.parseUserAndStoreToDb(response);
				new PreferenceHelper(activity).putPassword(etPassword.getText()
						.toString());
				startActivity(new Intent(activity, MainDrawerActivity.class));
				activity.finish();
			} else {
				AndyUtils.showToast(
						getResources().getString(R.string.signin_failed),
						activity);
				btnFb.setEnabled(true);
				btnGPlus.setEnabled(true);
			}
			break;
		case Const.ServiceCode.VERIFY:
			AndyUtils.removeCustomProgressDialog();
			if (parseContent.isSuccess(response)&&isValidate()) {
				mDialog.dismiss();
				login();
			}
			break;
		case Const.ServiceCode.RESEND:
			if(parseContent.isSuccess(response)){
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(activity, "Successfully sent.", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(activity, "Sent failed.", Toast.LENGTH_SHORT).show();
			}
		default:
			break;
		}
	}
	private void showMobileNumberValidationDialog(
			final int otp_code,final String tokan,final String id) {

		
		
		mDialog = new Dialog(getActivity(),
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// mDialog.getWindow().

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		mDialog.setCancelable(false);
		

		LayoutInflater li = LayoutInflater.from(getActivity());
		View addressView = li.inflate(R.layout.popup_otp, null);

		mDialog.setContentView(addressView);

		MyFontTextView tvVerfy = (MyFontTextView) addressView
				.findViewById(R.id.tvVerify);

		MyFontButton btnVerify = (MyFontButton) addressView
				.findViewById(R.id.btnVerify);

		MyFontButton resend = (MyFontButton) addressView
				.findViewById(R.id.resend);
		etVerificationCode = (MyFontEdittextView) addressView
				.findViewById(R.id.etVerificationCode);

		ImageView btnBack = (ImageView) addressView.findViewById(R.id.btnback);
		resend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!AndyUtils.isNetworkAvailable(activity)) {
					AndyUtils.showToast(getResources().getString(R.string.no_internet),
							activity);
					return;
				}
				AndyUtils.showCustomProgressDialog(activity,
						getResources().getString(R.string.text_wait), false, null);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Const.URL, Const.ServiceType.RESEND);
				map.put(Const.Params.ID, id);
				map.put(Const.Params.TOKEN,tokan);
				Log.e("", "R OTP"+map);
				new HttpRequester(activity, map, Const.ServiceCode.RESEND, UberSignInFragment.this);
			}
		});
		btnVerify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(etVerificationCode.getText().length() !=0){
				if (!AndyUtils.isNetworkAvailable(activity)) {
					AndyUtils.showToast(getResources().getString(R.string.no_internet),
							activity);
					return;
				}
				AndyUtils.showCustomProgressDialog(activity,
						getResources().getString(R.string.text_signing), false, null);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Const.URL, Const.ServiceType.VERIFY);
				map.put(Const.Params.ID, id);
				map.put(Const.Params.TOKEN,tokan);
				map.put("otp_code", etVerificationCode.getText().toString());
				Log.e("", "V OTP"+map);
				new HttpRequester(activity, map, Const.ServiceCode.VERIFY, UberSignInFragment.this);
				}else{
					Toast.makeText(activity, "Enter Your OTP Code", Toast.LENGTH_SHORT).show();
				}
			}

		});
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				Toast.makeText(activity, "Mobile Number not validated",
						Toast.LENGTH_LONG).show();
			}

		});
		mDialog.show();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mDialog!=null){
			mDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if(mDialog!=null){
			mDialog.dismiss();
		}
		super.onPause();
		
	}

	@Override
	public boolean OnBackPressed() {
		// TODO Auto-generated method stub
		// activity.removeAllFragment(new UberMainFragment(), false,
		// Const.FRAGMENT_MAIN);
		activity.goToMainActivity();
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragmentRegister#OnBackPressed()
	 */

}
