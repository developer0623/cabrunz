package com.cabrunzltd.user.fragments;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.cabrunzltd.user.R;
import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.component.MyFontEdittextView;
import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.MultiPartRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.soundcloud.android.crop.Crop;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static com.cabrunzltd.user.app.AppController.TAG;
//import com.sromku.simple.fb.listeners.OnLoginListener;
//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;


/**
 * @author Hardik A Bhalodi
 */
public class UberRegisterFragment extends UberBaseFragmentRegister
		implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private Button btnNext;
	private ImageButton btnGPlush;
	private ImageView btnFb;
	private EditText etFName, etLName, etEmail, etBio, etZipCode, etAddress,
			etPassword, etNumber;
	private CircularImageView ivProPic;

	// Gplus
	private ConnectionResult mConnectionResult;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private static final int RC_SIGN_IN = 0;
	private boolean mSignInClicked;
	private Uri uri = null;
	private String imageFilePath;
	private String filePath = null;
	private Bitmap bmp;
	private SimpleFacebook mSimpleFacebook;
	private TextView spCCode;
	private ParseContent pContent;
	private String type = Const.MANUAL;
	private String socialId;
	private String socialUrl;
	ArrayList<String> list;
	private String country;


	private Bitmap mImageBitmap;
	private String stringimage;
	private String imageext;
	private String mCurrentPhotoPath;
	private ImageView mImageView;

	private MyFontTextView ettimezone;
	int timezone_pos = -1;
	String[] timezone_display, timezone_value;

	private static boolean mobileValidated;
	private MyFontEdittextView etVerificationCode;
	private static Dialog mDialog;
	private String regResponse;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
		//Scope scopePro = new
		//Scope("https://www.googleapis.com/auth/plus.me");
//		mGoogleApiClient = new GoogleApiClient.Builder(activity)
//				.addConnectionCallbacks(this)
//				.addOnConnectionFailedListener(this).addApi(Plus.API)
//				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();


		country = Locale.getDefault().getDisplayCountry();
		timezone_display = getResources().getStringArray(R.array.time_zone);
		timezone_value = getResources().getStringArray(R.array.time_zone_value);
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
		}


	}



	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 0) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {

			}
		}
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
		activity.setTitle(getResources()
				.getString(R.string.text_register_small));
		activity.setIconMenu(R.drawable.taxi);
		View view = inflater.inflate(R.layout.register, container, false);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		btnGPlush = (ImageButton) view.findViewById(R.id.btnGplus);
		btnFb = (ImageView) view.findViewById(R.id.btnFb);
		etEmail = (EditText) view.findViewById(R.id.etEmail);
		etFName = (EditText) view.findViewById(R.id.etFName);
		etLName = (EditText) view.findViewById(R.id.etLName);
		etBio = (EditText) view.findViewById(R.id.etBio);
		etNumber = (EditText) view.findViewById(R.id.etNumber);
		etZipCode = (EditText) view.findViewById(R.id.etZipCode);
		etAddress = (EditText) view.findViewById(R.id.etAddress);
		etPassword = (EditText) view.findViewById(R.id.etPassword);
		ivProPic = (CircularImageView) view.findViewById(R.id.ivChooseProPic);
		spCCode = (TextView) view.findViewById(R.id.spCCode);
		ettimezone = (MyFontTextView) view.findViewById(R.id.etTimezone);
		ettimezone.setOnClickListener(this);

		String timezonedata = TimeZone.getDefault().getID();

		for (int i = 0; i < timezone_value.length; i++) {
			if (timezone_value[i].equals(timezonedata)) {
				ettimezone.setText(timezone_display[i]);
				break;
			}
		}

		spCCode.setOnClickListener(this);
		ivProPic.setOnClickListener(this);
		btnGPlush.setOnClickListener(this);
		btnFb.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		return view;
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		mSignInClicked = false;
		btnGPlush.setEnabled(false);
		btnFb.setEnabled(false);
//		String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

//		String email = Plus.GoogleSignInApi.(mGoogleApiClient);
		Person currentPerson = Plus.PeopleApi
				.getCurrentPerson(mGoogleApiClient);

		String personName = currentPerson.getDisplayName();

		String personPhoto = currentPerson.getImage().toString();
		String personGooglePlusProfile = currentPerson.getUrl();
		socialId = currentPerson.getId();
		// etPassword.setEnabled(false);
		etPassword.setVisibility(View.GONE);
//		etEmail.setText(email);
		type = Const.SOCIAL_GOOGLE;
		// etFName.setText(personName);
		if (personName.contains(" ")) {
			String[] split = personName.split(" ");
			etFName.setText(split[0]);
			etLName.setText(split[1]);
		} else {
			etFName.setText(personName);
		}
		if (!TextUtils.isEmpty(personPhoto)
				|| !personPhoto.equalsIgnoreCase("null")) {
			socialUrl = personPhoto;
			AQuery aQuery = new AQuery(activity);
			aQuery.id(ivProPic).image(personPhoto, getAqueryOption());
		} else {
			socialUrl = null;
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		pContent = new ParseContent(activity);
		AppLog.Log(Const.TAG, country);
		list = pContent.parseCountryCodes();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains(country)) {
				spCCode.setText((list.get(i).substring(0,
						list.get(i).indexOf(" "))));
			}
		}
		if (TextUtils.isEmpty(spCCode.getText())) {
			spCCode.setText((list.get(0).substring(0, list.get(0).indexOf(" "))));
		}

		String countryzipcode = GetCountryZipCode();
		if (!countryzipcode.equals("")) {
			spCCode.setText(countryzipcode);
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		activity.phelper
		.putCurrentFragment(Const.FRAGMENT_REGISTER);
		activity.actionBar.setTitle(getString(R.string.text_register_small));
		activity.currentFragment = Const.FRAGMENT_REGISTER;
		mSimpleFacebook = SimpleFacebook.getInstance(activity);
		try {
			if (mobileValidated && mDialog != null) {
				mDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		socialUrl = null;
		switch (v.getId()) {

		case R.id.btnGplus:

			mSignInClicked = true;
			if (!mGoogleApiClient.isConnecting()) {
				AndyUtils.showCustomProgressDialog(activity,
						getString(R.string.text_getting_info), true, null);
				mGoogleApiClient.connect();
			}
			break;
		case R.id.btnNext:

			if (isValidate()) {

				register(type, socialId);

			}
			break;
		case R.id.ivChooseProPic:
			showPictureDialog();
			break;
		case R.id.spCCode:
			Builder countryBuilder = new Builder(activity);
			countryBuilder.setTitle("Country codes");

			final String[] array = new String[list.size()];
			list.toArray(array);
			countryBuilder.setItems(array,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							spCCode.setText(array[which].substring(0,
									array[which].indexOf(" ")));
						}
					}).show();
			break;
		case R.id.etTimezone:
			Builder timezonebuilder = new Builder(activity);
			timezonebuilder.setTitle("Time Zones");

			timezonebuilder.setItems(timezone_display,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ettimezone.setText(timezone_display[which]);
							timezone_pos = which;
						}
					}).show();

			break;

		default:
			break;
		}
	}

	private void showPictureDialog() {
		Builder dialog = new Builder(activity);
		dialog.setTitle(getString(R.string.text_choosepicture));
		String[] items = { getString(R.string.text_gallary),
				getString(R.string.text_camera) };

		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					choosePhotoFromGallary();
					break;
				case 1:
					takePhotoFromCamera();
					break;

				}
			}
		});
		dialog.show();
	}

	private void resolveSignInError() {

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				activity.startIntentSenderForResult(mConnectionResult
						.getResolution().getIntentSender(), RC_SIGN_IN, null,
						0, 0, 0, Const.FRAGMENT_REGISTER);
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
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}

	}



//	 private void gotoMyThingFragment(String token, String id) {
//	 UberMyThingFragmentRegister fragMything = new
//	 UberMyThingFragmentRegister();
//	 Bundle bundle = new Bundle();
//	 bundle.putString(Const.Params.TOKEN, token);
//	 bundle.putString(Const.Params.ID, id);
//	 fragMything.setArguments(bundle);
//	 activity.addFragment(fragMything, false,
//	 Const.FRAGMENT_MYTHING_REGISTER);
//	 }

//	 private void gotoPaymentFragment(String id, String token) {
//	 UberAddPaymentFragmentRegister paymentFragment = new
//	 UberAddPaymentFragmentRegister();
//	 Bundle bundle = new Bundle();
//	 bundle.putString(Const.Params.TOKEN, token);
//	 bundle.putString(Const.Params.ID, id);
//	 paymentFragment.setArguments(bundle);
//	 activity.addFragment(paymentFragment, false,
//	 Const.FRAGMENT_PAYMENT_REGISTER);
//	 }
	private void goToReffralCodeFragment(String id, String token) {
		ReffralCodeFragment reffralCodeFragment = new ReffralCodeFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Const.Params.TOKEN, token);
		bundle.putString(Const.Params.ID, id);
		reffralCodeFragment.setArguments(bundle);
		activity.addFragment(reffralCodeFragment, false,
				Const.FRAGMENT_REFFREAL);
	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		String msg = null;
		if (TextUtils.isEmpty(etFName.getText().toString())) {
			msg = getString(R.string.text_enter_name);
			etFName.requestFocus();
		} else if (TextUtils.isEmpty(etLName.getText().toString())) {
			msg = getString(R.string.text_enter_lname);
			etLName.requestFocus();
		} else if (TextUtils.isEmpty(etEmail.getText().toString())) {
			msg = getString(R.string.text_enter_email);
			etEmail.requestFocus();
		} else if (!AndyUtils.eMailValidation((etEmail.getText().toString()))) {
			msg = getString(R.string.text_enter_valid_email);
			etEmail.requestFocus();
		} else if (etPassword.getVisibility() == View.VISIBLE) {
			if (TextUtils.isEmpty(etPassword.getText().toString())) {
				msg = getString(R.string.text_enter_password);
				etPassword.requestFocus();
			} else if (etPassword.getText().toString().length() < 6) {
				msg = getString(R.string.text_enter_password_valid);
				etPassword.requestFocus();
			}
		} else if (TextUtils.isEmpty(etAddress.getText().toString())) {
			msg = getString(R.string.text_enter_adress);
			etAddress.requestFocus();
		}
		if (filePath == null) {
			msg = getString(R.string.text_pro_pic);
			showPictureDialog();
		}
		if (msg != null) {
			Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			return false;

		}
		if (etPassword.getVisibility() == View.GONE) {
			if (!TextUtils.isEmpty(socialUrl)) {
				filePath = null;
				filePath = new AQuery(activity).getCachedFile(socialUrl)
						.getAbsolutePath();
			}
		}
		if (TextUtils.isEmpty(etNumber.getText().toString())) {
			msg = getString(R.string.text_enter_number);
			etNumber.requestFocus();
		}/*
		 * else if (TextUtils.isEmpty(filePath)) { msg =
		 * getString(R.string.text_pro_pic);
		 *
		 * }
		 */

		if (msg == null) {
			return true;
		}
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		return false;
	}

	private void getProfile() {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_getting_info), true, null);
		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onComplete(Profile profile) {
				AndyUtils.removeCustomProgressDialog();
				Log.i("Uber", "My profile id = " + profile.getId());
				btnGPlush.setEnabled(false);
				btnFb.setEnabled(false);
				etEmail.setText(profile.getEmail());
				etFName.setText(profile.getFirstName());
				etLName.setText(profile.getLastName());
				socialId = profile.getId();
				type = Const.SOCIAL_FACEBOOK;
				// etPassword.setEnabled(false);
				etPassword.setVisibility(View.GONE);

				if (!TextUtils.isEmpty(profile.getPicture())
						|| !profile.getPicture().equalsIgnoreCase("null")) {
					socialUrl = profile.getPicture();
					AQuery aQuery = new AQuery(activity);
					aQuery.id(ivProPic).image(profile.getPicture(),
							getAqueryOption());
				} else {
					socialUrl = null;
				}

			}
		});
	}

	private void register(String type, String id) {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		if (type.equalsIgnoreCase(Const.MANUAL)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL, Const.ServiceType.REGISTER);
			map.put(Const.Params.FIRSTNAME, etFName.getText().toString());
			map.put(Const.Params.LAST_NAME, etLName.getText().toString());
			map.put(Const.Params.EMAIL, etEmail.getText().toString());
			map.put(Const.Params.PASSWORD, etPassword.getText().toString());
//			map.put(Const.Params.PICTURE, filePath);
			map.put(Const.Params.PICTURE, stringimage);
			map.put(Const.Params.PICTUREEXT, imageext);
			map.put(Const.Params.PHONE, spCCode.getText().toString()
					+ etNumber.getText().toString());
			map.put(Const.Params.DEVICE_TOKEN,
					activity.phelper.getDeviceToken());
			map.put(Const.Params.ADDRESS, etAddress.getText().toString());
			map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
			if (!TextUtils.isEmpty(ettimezone.getText()) && timezone_pos != -1)
				map.put(Const.Params.TIMEZONE, timezone_value[timezone_pos]);

			 map.put(Const.Params.BIO, etBio.getText().toString());
			 map.put(Const.Params.ZIPCODE, etZipCode.getText().toString());
			 map.put(Const.Params.STATE, "");
			 map.put(Const.Params.COUNTRY, "");
			map.put(Const.Params.LOGIN_BY, Const.MANUAL);
			AndyUtils.showCustomProgressDialog(activity,
					getString(R.string.text_registering), true, null);
			Log.d("mahi", map.toString());
			new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER, this);
//			goToReffralCodeFragment(activity.phelper.getUserId(),
//					activity.phelper.getSessionToken());
		} else {
			//registerSocial(id, type);
		}

	}

	private void registerSocial(final String id, final String type) {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.REGISTER);
		map.put(Const.Params.FIRSTNAME, etFName.getText().toString());
		map.put(Const.Params.LAST_NAME, etLName.getText().toString());
		map.put(Const.Params.EMAIL, etEmail.getText().toString());
		map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
		map.put(Const.Params.PICTURE, filePath);
		map.put(Const.Params.PHONE, spCCode.getText().toString()
				+ etNumber.getText().toString());
		map.put(Const.Params.DEVICE_TOKEN, activity.phelper.getDeviceToken());
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.ADDRESS, etAddress.getText().toString());
		map.put(Const.Params.BIO, etBio.getText().toString());
		map.put(Const.Params.ZIPCODE, etZipCode.getText().toString());
		map.put(Const.Params.STATE, "");
		map.put(Const.Params.COUNTRY, "");
		map.put(Const.Params.LOGIN_BY, type);
		if (!TextUtils.isEmpty(ettimezone.getText()) && timezone_pos != -1)
			map.put(Const.Params.TIMEZONE, timezone_value[timezone_pos]);

		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_registering), true, null);
		new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER, this);

	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// System.out.println(response + "<-------");
		switch (serviceCode) {
		case Const.ServiceCode.REGISTER:

			AndyUtils.removeCustomProgressDialog();


				if (pContent.isSuccess(response)) {
					Toast.makeText(activity,
							getString(R.string.toast_register_success),
							Toast.LENGTH_SHORT).show();
					// activity.phelper.putPassword(etPassword.getText().toString());
					pContent.parseUserAndStoreToDb(response);
					activity.phelper.putPassword(etPassword.getText().toString());
				}

			try {
				if (pContent.isSuccess(response)) {
					regResponse = response;
					JSONObject jsonObject = null;
					jsonObject = new JSONObject(response);
					int otp = jsonObject.getInt("otp_code");
					String tokan = jsonObject.getString(Const.Params.TOKEN);
					String id = jsonObject.getString(Const.Params.ID);
					showMobileNumberValidationDialog(otp, tokan, id);
					mobileValidated = true;
					//activity.phelper.putPassword(etPassword.getText().toString());
					// pContent.parseUserAndStoreToDb(response);
					//if (mDialog.isShowing())
					//mDialog.dismiss();
					//activity.phelper.putPassword(etPassword.getText().toString());

				} else {

					Toast.makeText(activity,
							getString(R.string.toast_register_failed),
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				Log.e("MSG", "json parse error");
			}

			break;
		case Const.ServiceCode.RESEND:

			if (pContent.isSuccess(response)) {
				Log.d("mahi", "json parse" + response);
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(activity, "Successfully sent.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, "Sent failed.", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case Const.ServiceCode.VERIFY:
			AndyUtils.removeCustomProgressDialog();
			if (pContent.isSuccess(response)) {
				Toast.makeText(activity,
						getString(R.string.toast_register_success),
						Toast.LENGTH_SHORT).show();
				pContent.isSuccessWithStoreId(regResponse);
				mDialog.dismiss();
				mobileValidated = false;
				new PreferenceHelper(activity).putIs_Verified("1");
				goToReffralCodeFragment(activity.phelper.getUserId(),
						activity.phelper.getSessionToken());

			} else {
				Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT)
						.show();
			}

			break;

		}
	}

	private void choosePhotoFromGallary() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 112);
		//beginCrop(Uri.EMPTY);

//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("image/*");
//		startActivityForResult(intent, 112);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  // prefix
				".jpg",         // suffix
				storageDir      // directory
		);


		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;







	}

	private void takePhotoFromCamera() {

		File photoFile = null;
		try {
			photoFile = createImageFile();
		} catch (IOException ex) {
			// Error occurred while creating the File
			Log.i(TAG, "IOException");
		}
		// Continue only if the File was successfully created
		if (photoFile != null) {

//			Uri photoURI = FileProvider.getUriForFile(getActivity(),
//					"com.example.android.fileprovider",
//					photoFile);
			Intent ii = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			ii.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(ii, 113);
		}
	}

//	private void takePhotoFromCamera() {
//		Calendar cal = Calendar.getInstance();
//		File file = new File(Environment.getExternalStorageDirectory(),
//				(cal.getTimeInMillis() + ".jpg"));
//		if (!file.exists()) {
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//
//			file.delete();
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		uri = Uri.fromFile(file);
//		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//		startActivityForResult(i, 113);
//	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
			case RC_SIGN_IN:
				if (resultCode != RESULT_OK) {
					mSignInClicked = false;
					AndyUtils.removeCustomProgressDialog();
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
				break;

			default:
				mSimpleFacebook.onActivityResult( requestCode, resultCode, data);
				if (mSimpleFacebook.isLogin()) {
					getProfile();
				} else {
					Toast.makeText(activity, "facebook login failed",
							Toast.LENGTH_SHORT).show();
				}

				super.onActivityResult(requestCode, resultCode, data);
				break;
			case Const.CHOOSE_PHOTO:
				if (data != null) {

					uri = data.getData();
					if (uri != null) {
						activity.setFbTag(Const.FRAGMENT_REGISTER);
						beginCrop(uri);
						//handleCrop(resultCode,data);
					} else {
						Toast.makeText(activity, "unable to select image",
								Toast.LENGTH_LONG).show();
					}

				}
				break;
			case Const.TAKE_PHOTO:


					activity.setFbTag(Const.FRAGMENT_REGISTER);
					beginCrop(Uri.parse(mCurrentPhotoPath));
//					mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
//					mImageView.setImageBitmap(mImageBitmap);


				break;
			case Crop.REQUEST_CROP:

				AppLog.Log(Const.TAG, "Crop photo on activity result");
				if (data != null)
					handleCrop(resultCode, data);

				break;
		}

	}



	private String getRealPathFromURI(Uri contentURI) {
		String result;
		Cursor cursor = activity.getContentResolver().query(contentURI, null,
				null, null, null);

		if (cursor == null) { // Source is Dropbox or other similar local file
								// path
			result = contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	private ImageOptions getAqueryOption() {
		ImageOptions options = new ImageOptions();
		options.targetWidth = 200;
		options.memCache = true;
		options.fallback = R.drawable.default_user;
		options.fileCache = true;
		return options;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.UberBaseFragmentRegister#OnBackPressed()
	 */
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
	private void beginCrop(Uri source) {
		//Uri outputUri = Uri.fromFile(new File(registerActivity.getCacheDir(),"cropped"));
//		Uri outputUri = Uri.fromFile(new File(Environment
//				.getExternalStorageDirectory(), (Calendar.getInstance()
//				.getTimeInMillis() + ".jpg")));


		Uri outputUri = Uri.fromFile(new File(getContext().getCacheDir(), (Calendar.getInstance()
				.getTimeInMillis() + ".jpg")));

//        Uri outputUri = source;



//		Crop crop = new Crop(source, outputUri);
//		new Crop(source, outputUri).asSquare().start(activity);
		Crop.of(source,outputUri).asSquare().start(getContext(), this, 6709);
		//new Crop(source,outputUri).asSquare().start(activity);

		//getRotatedBitmap(File.createTempFile(filePath));
		//handleCrop(RESULT_OK,);
		//Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		filePath = getRealPathFromURI(Crop.getOutput(i));
//		Bitmap map = getRotatedBitmap(new File(filePath));
//		ivProPic.setImageBitmap(map);
//		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.setData(source);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//		startActivityForResult(i, 6709);
	}

	public static Bitmap getRotatedBitmap(File file) {
		Bitmap bitmap = null;
		try {
			File f = file;
			ExifInterface exif = new ExifInterface(f.getPath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int angle = 0;

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				angle = 90;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				angle = 180;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				angle = 270;
			}

			Matrix mat = new Matrix();
			mat.postRotate(angle);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;

			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
					null, options);
			bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), mat, true);
			ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 20,
					outstudentstreamOutputStream);

		} catch (IOException e) {
			Log.w("TAG", "-- Error in setting image");
		} catch (OutOfMemoryError oom) {
			Log.w("TAG", "-- OOM Error in setting image");
		}
		return bitmap;
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode ==RESULT_OK) {
			AppLog.Log(Const.TAG, "Handle crop");
			filePath = getRealPathFromURI(Crop.getOutput(result));
			Bitmap map = getRotatedBitmap(new File(filePath));

			String filenameArray[] = filePath.split("\\.");
			imageext = filenameArray[filenameArray.length-1];

			ivProPic.setImageURI(Crop.getOutput(result));
			ivProPic.setImageBitmap(map);
			stringimage = getStringImage(map);
			AppLog.Log(Const.TAG, stringimage);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(activity, Crop.getError(result).getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}


	public String getStringImage(Bitmap bmp){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}



	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) getActivity()
				.getSystemService(getActivity().TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	private void showMobileNumberValidationDialog(final int otp_code,
			final String tokan, final String id) {

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
					AndyUtils.showToast(
							getResources().getString(R.string.no_internet),
							activity);
					return;
				}
				AndyUtils.showCustomProgressDialog(activity, getResources()
						.getString(R.string.text_wait), false, null);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Const.URL, Const.ServiceType.RESEND);
				map.put(Const.Params.ID, id);
				map.put(Const.Params.TOKEN, tokan);
				Log.d("mahi", "R OTP" + map);
				new HttpRequester(activity, map, Const.ServiceCode.RESEND,
						UberRegisterFragment.this);
			}
		});
		btnVerify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etVerificationCode.getText().length() != 0) {
					if (!AndyUtils.isNetworkAvailable(activity)) {
						AndyUtils.showToast(
								getResources().getString(R.string.no_internet),
								activity);
						return;
					}
					AndyUtils.showCustomProgressDialog(activity, getResources()
							.getString(R.string.text_signing), false, null);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(Const.URL, Const.ServiceType.VERIFY);
					map.put(Const.Params.ID, id);
					map.put(Const.Params.TOKEN, tokan);
					map.put("otp_code", etVerificationCode.getText().toString());
					Log.e("", "V OTP" + map);
					new HttpRequester(activity, map, Const.ServiceCode.VERIFY,
							UberRegisterFragment.this);
				} else {
					Toast.makeText(activity, "Enter Your OTP Code",
							Toast.LENGTH_SHORT).show();
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
		//
		// AlertDialog.Builder dialogBuilder = new Builder(activity);
		// dialogBuilder.setTitle("Validate Mobile Number");
		// final View view = activity.getLayoutInflater().inflate(
		// R.layout.mobile_validation_layout, null);
		// dialogBuilder.setView(view);
		// dialogBuilder.setPositiveButton("OK", new OnClickListener() {
		// EditText etCodeText = (EditText) view
		// .findViewById(R.id.validationCode);
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// String code = etCodeText.getText().toString();
		// if (code.equals(String.valueOf(otp_code))) {
		// Toast.makeText(activity,
		// "Mobile Number successfully validated",
		// Toast.LENGTH_LONG).show();
		// mobileValidated = true;
		// if (pContent.isSuccessWithStoreId(response)) {
		// Toast.makeText(activity,
		// getString(R.string.toast_register_success),
		// Toast.LENGTH_SHORT).show();
		// // activity.phelper.putPassword(etPassword.getText().toString());
		// pContent.parseUserAndStoreToDb(response);
		// activity.phelper.putPassword(etPassword.getText()
		// .toString());
		// // JSONObject jsonObject;
		// // try {
		// // jsonObject = new JSONObject(response);
		// // activity.phelper.putSessionToken(jsonObject
		// // .getString(Const.Params.TOKEN));
		// // activity.phelper.putUserId(jsonObject
		// // .getString(Const.Params.ID));
		// // gotoPaymentFragment(
		// //
		// // jsonObject.getString(Const.Params.ID),
		// // jsonObject.getString(Const.Params.TOKEN));
		// goToReffralCodeFragment(activity.phelper.getUserId(),
		// activity.phelper.getSessionToken());
		// // } catch (JSONException e) {
		// // // TODO Auto-generated catch block
		// // e.printStackTrace();
		// // }
		//
		// } else {
		// Toast.makeText(activity,
		// getString(R.string.toast_register_failed),
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		//
		// }
		// }).setNegativeButton("Cancel", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// mobileValidated = false;
		// Toast.makeText(activity, "Mobile Number not validated",
		// Toast.LENGTH_LONG).show();
		// }
		// }).setNeutralButton("Re-send Code", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// Toast.makeText(activity, "Re-sending validation code",
		// Toast.LENGTH_LONG).show();
		// }
		// }).show();
		// }
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onPause();

	}
}
