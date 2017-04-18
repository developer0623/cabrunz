package com.cabrunzltd.user;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.component.MyFontEdittextView;
import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.db.DBHelper;
import com.cabrunzltd.user.models.User;
import com.cabrunzltd.user.parse.AsyncTaskCompleteListener;
import com.cabrunzltd.user.parse.MultiPartRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author Kishan H Dhamat
 * 
 */
public class ProfileActivity extends ActionBarBaseActivitiy implements
		OnClickListener, AsyncTaskCompleteListener {
	private MyFontEdittextView etProfileFname, etProfileLName, etProfileEmail,
			etProfileNumber, etProfileAddress, etProfileBio, etProfileZipcode,
			etProfilePassword;
	private ImageView ivProfile;
	private MyFontButton tvProfileSubmit;
	private DBHelper dbHelper;
	private Uri uri = null;
	private AQuery aQuery;
	private String profileImageData, profileImageFilePath, loginType;
	private Bitmap profilePicBitmap;
	private PreferenceHelper preferenceHelper;
	private ImageOptions imageOptions;
	private final String TAG = "profileActivity";

	private MyFontTextView ettimezone;
	int timezone_pos = -1;
	String[] timezone_display, timezone_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		setTitle(getString(R.string.text_profile));
		setIconMenu(R.drawable.nav_profile);
		setIcon(R.drawable.icon_back);
		// actionBar.setTitle(getString(R.string.text_profile));
		// findViewById(R.id.llProfileSocial).setVisibility(View.GONE);
		// findViewById(R.id.etProfilePassword).setVisibility(View.GONE);
		// findViewById(R.id.tvProfileSubmit).setVisibility(View.GONE);
		findViewById(R.id.tvProfileCountryCode).setVisibility(View.GONE);
		// findViewById(R.id.llseprateView).setVisibility(View.GONE);
		etProfileFname = (MyFontEdittextView) findViewById(R.id.etProfileFName);
		etProfileLName = (MyFontEdittextView) findViewById(R.id.etProfileLName);
		etProfileEmail = (MyFontEdittextView) findViewById(R.id.etProfileEmail);
		etProfilePassword = (MyFontEdittextView) findViewById(R.id.etProfilePassword);
		etProfileNumber = (MyFontEdittextView) findViewById(R.id.etProfileNumber);
		etProfileBio = (MyFontEdittextView) findViewById(R.id.etProfileBio);
		etProfileAddress = (MyFontEdittextView) findViewById(R.id.etProfileAddress);
		etProfileZipcode = (MyFontEdittextView) findViewById(R.id.etProfileZipCode);
		ettimezone = (MyFontTextView) findViewById(R.id.etprofileTimezone);
		ettimezone.setOnClickListener(this);
		tvProfileSubmit = (MyFontButton) findViewById(R.id.tvProfileSubmit);
		ivProfile = (ImageView) findViewById(R.id.ivProfileProfile);
		ivProfile.setOnClickListener(this);
		tvProfileSubmit.setOnClickListener(this);
		tvProfileSubmit.setText(getResources().getString(
				R.string.text_edit_profile));

		preferenceHelper = new PreferenceHelper(this);
		// socialId = preferenceHelper.getSocialId();
		loginType = preferenceHelper.getLoginBy();

		AppLog.Log(Const.TAG, "Login type==+> " + loginType);
		if (loginType.equals(Const.MANUAL)) {
			etProfilePassword.setVisibility(View.VISIBLE);
			etProfilePassword.setText(preferenceHelper.getPassword());
		}
		aQuery = new AQuery(this);
		disableViews();
		imageOptions = new ImageOptions();
		imageOptions.memCache = true;
		imageOptions.fileCache = true;
		imageOptions.targetWidth = 200;
		imageOptions.fallback = R.drawable.default_user;

		timezone_display = getResources().getStringArray(R.array.time_zone);
		timezone_value = getResources().getStringArray(R.array.time_zone_value);
		setData();
	}

	private void disableViews() {
		etProfileFname.setEnabled(false);
		etProfileLName.setEnabled(false);
		etProfileEmail.setEnabled(false);
		etProfileNumber.setEnabled(false);
		etProfileBio.setEnabled(false);
		etProfileAddress.setEnabled(false);
		etProfileZipcode.setEnabled(false);
		etProfilePassword.setEnabled(false);
		ivProfile.setEnabled(false);
		ettimezone.setEnabled(false);
	}

	private void enableViews() {
		etProfileFname.setEnabled(true);
		etProfileLName.setEnabled(true);
		// etProfileEmail.setEnabled(true);
		etProfileNumber.setEnabled(true);
		etProfileBio.setEnabled(true);
		etProfileAddress.setEnabled(true);
		etProfileZipcode.setEnabled(true);
		etProfilePassword.setEnabled(true);
		ivProfile.setEnabled(true);
		ettimezone.setEnabled(true);
	}

	private void setData() {
		// TODO Auto-generated method stub
		dbHelper = new DBHelper(getApplicationContext());
		User user = dbHelper.getUser();
		if (user != null){
			try {
				aQuery.id(ivProfile).progress(R.id.pBar)
						.image(user.getPicture(), imageOptions);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		etProfileFname.setText(user.getFname());
		etProfileLName.setText(user.getLname());
		etProfileEmail.setText(user.getEmail());
		etProfileNumber.setText(user.getContact());
		}
		// ettimezone.setText(user.getTimezone());
		for (int i = 0; i < timezone_value.length; i++) {
			if (timezone_value[i].equals(user.getTimezone())) {
				ettimezone.setText(timezone_display[i]);
				break;
			}
		}

		etProfileBio.setText(user.getBio());
		etProfileAddress.setText(user.getAddress());
		etProfileZipcode.setText(user.getZipcode());
		Log.e("xxx", "from profile user   " + user.getPicture());
		if (user.getPicture() != null) {
			aQuery.id(R.id.ivProfileProfile).image(user.getPicture(), true,
					true, 200, 0, new BitmapAjaxCallback() {

						@Override
						public void callback(String url, ImageView iv,
								Bitmap bm, AjaxStatus status) {
							AppLog.Log(TAG, "URL FROM AQUERY::" + url);
							if (url != null && !url.equals("")) {
								profileImageData = aQuery.getCachedFile(url)
										.getPath();
								AppLog.Log(TAG, "URL path FROM AQUERY::" + url);
								iv.setImageBitmap(bm);
							}

						}

					});
		}

	}

	private void onUpdateButtonClick() {
		if (etProfileFname.getText().length() == 0) {
			AndyUtils.showToast(
					getResources().getString(R.string.text_enter_name), this);
			return;
		} else if (etProfileLName.getText().length() == 0) {
			AndyUtils.showToast(
					getResources().getString(R.string.text_enter_lname), this);
			return;
		} else if (etProfileEmail.getText().length() == 0) {
			AndyUtils.showToast(
					getResources().getString(R.string.text_enter_email), this);
			return;
		} else if (!AndyUtils.eMailValidation(etProfileEmail.getText()
				.toString())) {
			AndyUtils.showToast(
					getResources().getString(R.string.text_enter_valid_email),
					this);
			return;
		} else if (etProfilePassword.getVisibility() == View.VISIBLE) {
			if (etProfilePassword.getText().length() > 0
					&& etProfilePassword.getText().length() < 6) {
				AndyUtils.showToast(
						getResources().getString(
								R.string.text_enter_password_valid), this);
				return;
			}

		}

		// if (etProfilePassword.getVisibility() == View.VISIBLE) {
		// if (!TextUtils.isEmpty(profileImageData)) {
		// profileImageData = null;
		// profileImageData = aQuery.getCachedFile(profileImageData)
		// .getPath();
		// }
		// }

		if (etProfileNumber.getText().length() == 0) {
			AndyUtils.showToast(
					getResources().getString(R.string.text_enter_number), this);
			return;
		}/*
		 * else if (profileImageData == null || profileImageData.equals("")) {
		 * AndyUtils.showToast( getResources().getString(R.string.text_pro_pic),
		 * this); return; }
		 */else {
			updateSimpleProfile(loginType);
		}
	}

	private void updateSimpleProfile(String type) {

		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					this);
			return;
		}

		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.progress_update_profile),
				false, null);

		if (type.equals(Const.MANUAL)) {
			AppLog.Log(TAG, "Simple Profile update method");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
			map.put(Const.Params.ID, preferenceHelper.getUserId());
			map.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
			map.put(Const.Params.FIRSTNAME, etProfileFname.getText().toString());
			map.put(Const.Params.LAST_NAME, etProfileLName.getText().toString());
			// map.put(Const.Params.EMAIL, etProfileEmail.getText().toString());
			map.put(Const.Params.PASSWORD, etProfilePassword.getText()
					.toString());
			map.put(Const.Params.PICTURE, profileImageData);
			map.put(Const.Params.PHONE, etProfileNumber.getText().toString());
			map.put(Const.Params.BIO, etProfileBio.getText().toString());
			map.put(Const.Params.ADDRESS, etProfileAddress.getText().toString());
			map.put(Const.Params.STATE, "");
			map.put(Const.Params.COUNTRY, "");
			map.put(Const.Params.ZIPCODE, etProfileZipcode.getText().toString()
					.trim());
			if (!TextUtils.isEmpty(ettimezone.getText()) && timezone_pos != -1)
				map.put(Const.Params.TIMEZONE, timezone_value[timezone_pos]);

			new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
					this);
		} else {
			updateSocialProfile(type);
		}
	}

	private void updateSocialProfile(String loginType) {
		AppLog.Log(TAG, "profile social update  method");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
		map.put(Const.Params.ID, preferenceHelper.getUserId());
		map.put(Const.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(Const.Params.FIRSTNAME, etProfileFname.getText().toString());
		map.put(Const.Params.LAST_NAME, etProfileLName.getText().toString());
		map.put(Const.Params.ADDRESS, etProfileAddress.getText().toString());
		// map.put(Const.Params.EMAIL, etProfileEmail.getText().toString());
		map.put(Const.Params.PHONE, etProfileNumber.getText().toString());
		map.put(Const.Params.PICTURE, profileImageData);
		map.put(Const.Params.STATE, "");
		map.put(Const.Params.COUNTRY, "");
		map.put(Const.Params.BIO, etProfileBio.getText().toString());
		map.put(Const.Params.ZIPCODE, etProfileZipcode.getText().toString()
				.trim());
		if (!TextUtils.isEmpty(ettimezone.getText()) && timezone_pos != -1)
			map.put(Const.Params.TIMEZONE, timezone_value[timezone_pos]);
		new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
				this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvProfileSubmit:
			if (tvProfileSubmit
					.getText()
					.toString()
					.equals(getResources()
							.getString(R.string.text_edit_profile))) {
				enableViews();
				etProfileFname.requestFocus();
				tvProfileSubmit.setText(getResources().getString(
						R.string.text_update_profile));
			} else {
				onUpdateButtonClick();
			}
			break;
		case R.id.ivProfileProfile:
			showPictureDialog();
			break;
		case R.id.btnActionNotification:
			// onBackPressed();
			startActivity(new Intent(ProfileActivity.this,
					MainDrawerActivity.class));
			break;
		case R.id.etprofileTimezone:
			Builder timezonebuilder = new Builder(this);
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
		Builder pictureDialog = new Builder(this);
		pictureDialog.setTitle(getResources().getString(
				R.string.text_choosepicture));
		String[] pictureDialogItems = {
				getResources().getString(R.string.text_gallary),
				getResources().getString(R.string.text_camera) };

		pictureDialog.setItems(pictureDialogItems,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
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
		pictureDialog.show();
	}

	private void choosePhotoFromGallary() {

		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// intent.addCategory(Intent.CATEGORY_OPENABLE);
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(galleryIntent, Const.CHOOSE_PHOTO);

	}

	private void takePhotoFromCamera() {
		Calendar cal = Calendar.getInstance();
		File file = new File(Environment.getExternalStorageDirectory(),
				(cal.getTimeInMillis() + ".jpg"));

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		uri = Uri.fromFile(file);
		Intent cameraIntent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(cameraIntent, Const.TAKE_PHOTO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Const.CHOOSE_PHOTO) {
			if (data != null) {
				Uri contentURI = data.getData();

				AppLog.Log(TAG, "Choose photo on activity result");
				beginCrop(contentURI);

			}

		} else if (requestCode == Const.TAKE_PHOTO) {

			if (uri != null) {
				profileImageFilePath = uri.getPath();
				AppLog.Log(TAG, "Take photo on activity result");
				beginCrop(uri);

			} else {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.toast_unable_to_selct_image),
						Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == Crop.REQUEST_CROP) {
			AppLog.Log(TAG, "Crop photo on activity result");
			handleCrop(resultCode, data);
		}
	}

	private String getRealPathFromURI(Uri contentURI) {
		String result;
		Cursor cursor = getContentResolver().query(contentURI, null, null,
				null, null);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uberdriverforx.parse.AsyncTaskCompleteListener#onTaskCompleted(java
	 * .lang.String, int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
	Log.e(TAG, ""+response);
		switch (serviceCode) {
		case Const.ServiceCode.UPDATE_PROFILE:
			AndyUtils.removeCustomProgressDialog();
			if (new ParseContent(this).isSuccessWithStoreId(response)) {
				Toast.makeText(this, getString(R.string.toast_update_success),
						Toast.LENGTH_SHORT).show();
				new DBHelper(this).deleteUser();
				new ParseContent(this).parseUserAndStoreToDb(response);
				new PreferenceHelper(this).putPassword(etProfilePassword
						.getText().toString());

			} else {
				Toast.makeText(this, getString(R.string.toast_update_failed),
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	private void beginCrop(Uri source) {
		// Uri outputUri = Uri.fromFile(new File(registerActivity.getCacheDir(),
		// "cropped"));
		Uri outputUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), (Calendar.getInstance()
				.getTimeInMillis() + ".jpg")));
		Crop.of(source,outputUri).asSquare().start(this);
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			AppLog.Log(Const.TAG, "Handle crop");
			profileImageData = getRealPathFromURI(Crop.getOutput(result));
			ivProfile.setImageURI(Crop.getOutput(result));
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		startActivity(new Intent(ProfileActivity.this, MainDrawerActivity.class));
	}
}
