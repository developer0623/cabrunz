package com.cabrunzltd.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.ImageOptions;

import com.cabrunzltd.user.component.MyFontTextView;
import com.cabrunzltd.user.component.MyTitleFontTextView;
import com.cabrunzltd.user.fragments.UberBaseFragmentRegister;
import com.cabrunzltd.user.parse.AsyncTaskCompleteListener;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.AppLog;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.LocationUpdateService;
import com.cabrunzltd.user.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.cabrunzltd.user.R.id.auto;
//import com.google.android.gms.internal.bn;
//import com.google.android.gms.plus.model.people.Person.Image;

//import com.paypal.android.MEP.PayPal;
//import com.paypal.android.MEP.PayPalActivity;
//import com.paypal.android.MEP.PayPalAdvancedPayment;
//import com.paypal.android.MEP.PayPalReceiverDetails;

/**
 * @author Hardik A Bhalodi
 */
@SuppressLint("NewApi")
abstract public class ActionBarBaseActivitiy extends AppCompatActivity
		implements OnClickListener, AsyncTaskCompleteListener {

	public ActionBar actionBar;
	private int mFragmentId = 0;
	private String mFragmentTag = null;

	public ImageButton btnNotification, btnActionMenu, btnadddestination;
	public MyTitleFontTextView tvTitle;
	private TextView tvTotalPrice;
	private String chosenPrice;
	public static AutoCompleteTextView etSource;
	public String currentFragment = "";
	public boolean isQuoted;

	private LinearLayout llQuotedPriceWrapper;

	public final int PAYPAL_RESPONSE = 100;

	public static final int USER_PAYMENT_CHOICE_ACTUAL = 1;
	public static final int USER_PAYMENT_CHOICE_QUOTED = 2;

	public static Dialog mDialog;
	public static CountDownTimer mCountDownTimer;

	private Activity activity;

	protected abstract boolean isValidate();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = this;
		actionBar = getSupportActionBar();
		isQuoted = false;
		// Custom Action Bar
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View customActionBarView = inflater.inflate(R.layout.custom_action_bar,
				null);
		btnNotification = (ImageButton) customActionBarView
				.findViewById(R.id.btnActionNotification);
		btnNotification.setOnClickListener(this);

		btnadddestination = (ImageButton) customActionBarView
				.findViewById(R.id.btnAdddestination);
		btnadddestination.setOnClickListener(this);
		btnadddestination.setBackgroundResource(R.drawable.blank_star);

		// btnadddestination = (ImageButton) customActionBarView
		// .findViewById(R.id.btnAdddestination);
		// btnadddestination.setOnClickListener(this);
		// btnadddestination.setBackgroundResource(R.drawable.create_contact);

		tvTitle = (MyTitleFontTextView) customActionBarView
				.findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);

		etSource = (AutoCompleteTextView) customActionBarView
				.findViewById(R.id.etEnterSouce);

		btnActionMenu = (ImageButton) customActionBarView
				.findViewById(R.id.btnActionMenu);
		btnActionMenu.setOnClickListener(this);

		try {

			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);

			//actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
			actionBar.setCustomView(customActionBarView);
			Toolbar parent =(Toolbar) customActionBarView.getParent();
			parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
//			parent.setMargin(0,0,0,0);
			parent.setContentInsetsAbsolute(0,0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setFbTag(String tag) {

		mFragmentId = 0;
		mFragmentTag = tag;
	}

	public void startLocationUpdateService() {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		Intent intent = new Intent(this, LocationUpdateService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
//		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//				1000 * 30, pintent);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				1000 * 10, pintent);

	}

	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Fragment fragment = null;

		Log.d("pavan", "response");

		if (requestCode == PAYPAL_RESPONSE) {

			switch (resultCode) {
			case Activity.RESULT_OK:
				// The payment succeeded
				String payKey = data
						.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
				Log.d("pavan", "success " + payKey);
				sendPaypalData(payKey);
				Toast.makeText(getApplicationContext(),
						"Payment done succesfully ", Toast.LENGTH_LONG).show();

				btnConfirm.setText(getString(R.string.text_close));
				// Tell the user their payment succeeded
				break;
			case Activity.RESULT_CANCELED:
				Toast.makeText(getApplicationContext(),
						"Payment Canceled , Try again ", Toast.LENGTH_LONG)
						.show();

				break;
			case PayPalActivity.RESULT_FAILURE:
				Toast.makeText(getApplicationContext(),
						"Payment failed , Try again ", Toast.LENGTH_LONG)
						.show();

				break;
			}

		} else {

			if (mFragmentId > 0) {
				fragment = getSupportFragmentManager().findFragmentById(
						mFragmentId);
			} else if (mFragmentTag != null
					&& !mFragmentTag.equalsIgnoreCase("")) {
				fragment = getSupportFragmentManager().findFragmentByTag(
						mFragmentTag);
			}
			if (fragment != null) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}

		}

	}*/

	public void startActivityForResult(Intent intent, int requestCode,
			int fragmentId) {

		mFragmentId = fragmentId;
		mFragmentTag = null;
		super.startActivityForResult(intent, requestCode);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			int fragmentId, Bundle options) {

		mFragmentId = fragmentId;
		mFragmentTag = null;
		super.startActivityForResult(intent, requestCode, options);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag, Bundle options) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode, options);
	}

	public void startIntentSenderForResult(Intent intent, int requestCode,
			String fragmentTag, Bundle options) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	@Deprecated
	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags) throws SendIntentException {
		// TODO Auto-generated method stub
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags);
	}

	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, String fragmentTag)
			throws SendIntentException {

		// TODO Auto-generated method stub
		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags);
	}

	@Override
	@Deprecated
	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, Bundle options)
			throws SendIntentException {
		// TODO Auto-generated method stub
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags, options);
	}

	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, Bundle options, String fragmentTag)
			throws SendIntentException {

		// TODO Auto-generated method stub
		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags, options);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		switch (serviceCode) {
		case Const.ServiceCode.USER_PAYMENT:
			try {
				AndyUtils.removeCustomProgressDialog();
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("success").equals("true")) {
					if (btnConfirm.getText().toString()
							.equals(getString(R.string.pay_with_paypal))) {
						//////////initLibrary();

						// PayPalButtonClick(primary_id, primary_amount,
						// secoundry_id,
						// secoundry_amount);

					} else {
						mDialog.dismiss();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case Const.ServiceCode.SEARCH_USER:
			AndyUtils.removeCustomProgressDialog();
			try {
				JSONObject jsonObject = new JSONObject(response);
				Log.d("MSG", "SearchUser" + response);
				if (jsonObject.getString("success").equals("true")) {
					int friendId = jsonObject.getJSONArray("search_user")
							.getJSONObject(0).getInt(Const.Params.ID);
					callRequestFriendApi(friendId);
				} else {
					Toast.makeText(this,
							"Your friend's email verification failed",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case Const.ServiceCode.REQUEST_SEND_FRIEND:
			try {
				JSONObject jsonObject = new JSONObject(response);
				Log.d("NIKHIL", "REQUEST_SEND_FRIEND" + response);
				if (jsonObject.getString("success").equals("true")) {
					mCountDownTimer = new CountDownTimer(5000, 1000) {

						@Override
						public void onTick(long millisUntilFinished) {
							Log.i("TIMER", "Seconds left "
									+ (millisUntilFinished / 1000));
						}

						@Override
						public void onFinish() {
							AndyUtils.removeCustomProgressDialog();
							callCheckFriendPaymentApi();
						}
					};
					mCountDownTimer.start();
				} else if (!jsonObject.getBoolean("success")) {

					AndyUtils.removeCustomProgressDialog();
					Toast.makeText(activity, jsonObject.getString("error"),
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(activity,
							"Your friend could not be reached at the moment",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case Const.ServiceCode.CHECK_FRIEND_PAYMENT:
			try {
				JSONObject jsonObject = new JSONObject(response);
				Log.d("MSG", "CheckFriendResponse:" + response);
				if (jsonObject.getString("success").equals("true")) {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public void addFragment(Fragment fragment, boolean addToBackStack,
			String tag) {

		try {
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			ft.setCustomAnimations(R.anim.slide_in_right,
					R.anim.slide_out_left, R.anim.slide_in_left,
					R.anim.slide_out_right);
			if (addToBackStack) {

				ft.addToBackStack(tag);

			}
			ft.replace(R.id.content_frame, fragment, tag);
			ft.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addFragmentWithStateLoss(Fragment fragment,
			boolean addToBackStack, String tag) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		if (addToBackStack) {

			ft.addToBackStack(tag);

		}
		ft.replace(R.id.content_frame, fragment, tag);
		ft.commitAllowingStateLoss();
	}

	public void removeAllFragment(Fragment replaceFragment,
			boolean addToBackStack, String tag) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		manager.popBackStackImmediate(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);

		if (addToBackStack) {

			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, replaceFragment);
		ft.commitAllowingStateLoss();

	}

	public void clearBackStackImmidiate() {

		FragmentManager manager = getSupportFragmentManager();

		manager.popBackStackImmediate(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);

	}

	public void clearBackStack() {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			FragmentManager.BackStackEntry first = manager
					.getBackStackEntryAt(0);
			manager.popBackStack(first.getId(),
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(new PreferenceHelper(activity).getCurrentFragment())
				&& !new PreferenceHelper(activity).getCurrentFragment().equals(Const.FRAGMENT_MAP)
				&& !new PreferenceHelper(activity).getCurrentFragment().equals(Const.FRAGMENT_TRIP)) {
			FragmentManager manager = getSupportFragmentManager();
			UberBaseFragmentRegister frag = ((UberBaseFragmentRegister) manager
					.findFragmentByTag(new PreferenceHelper(activity).getCurrentFragment()));

			if (frag != null && frag.isVisible())
				frag.OnBackPressed();
			else
				super.onBackPressed();
		} else {
			// if(mDialog == null)
			super.onBackPressed();
		}
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return true;
	}

	protected ImageOptions getAqueryOption() {
		ImageOptions options = new ImageOptions();
		options.targetWidth = 200;
		options.memCache = true;
		options.fallback = R.drawable.default_user;
		options.fileCache = true;
		return options;
	}

	Button btnConfirm;
	private Button btnReject;
	private boolean isQuotedPrice;

	public void showBillDialog(String timeCost, String total, String distCost,
			String basePrice, String time, String distance,
			String perdistancecost, String pertimecost, String btnTitle,
			String payment_mode, final String primary_id,
			final String secoundry_id, final String primary_amount,
			final String secoundry_amount, double actual_total,
			final int n, String promo,
			String promo_discount, String actual_price) {

		////////initLibrary();
		// if (_paypalLibraryInit) {
		// showPayPalButton();
		// } else {
		// // finish();
		// }

		mDialog = new Dialog(ActionBarBaseActivitiy.this, android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.layout_invoice);
		// mDialog.setCancelable(false);
		llQuotedPriceWrapper = (LinearLayout) mDialog
				.findViewById(R.id.llQuotedPriceWrapper);
		View quotedPriceBaseLine = (View) mDialog
				.findViewById(R.id.quotedPriceBaseLine);

		/*if (!quotedPrice.equals("0.00")) {
			isQuoted = true;
		}

		try {
			if (isQuoted) {
				llQuotedPriceWrapper.setVisibility(View.GONE);
				quotedPriceBaseLine.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
		//
		String basePricetmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(basePrice)));
		// quotedPrice = "75.00"; // HARD CODED FOR NOW
		final String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(total)));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(distCost)));
		String timeCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(timeCost)));

		String actualtotal = String.valueOf(decimalFormat.format(actual_total));
		double yousave = Math.abs(actual_total - Double.parseDouble(totalTmp));

		isQuotedPrice = true;
		AppLog.Log("Distance", distance);
		AppLog.Log("Time", time);

		((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(" "
				+ basePrice);
		// QUOTED PRICE SETTEXT
		final LinearLayout promolayout = (LinearLayout) mDialog
				.findViewById(R.id.promolayout);
		/*((TextView) mDialog.findViewById(R.id.tvQuotedPrice)).setText(" "
				+ quotedPrice);*/
		if (promo.equals("1")) {
			promolayout.setVisibility(View.VISIBLE);
			((TextView) mDialog.findViewById(R.id.tvactualPrice)).setText(" "
					+ actual_price);
			((TextView) mDialog.findViewById(R.id.tvdiscountPrice)).setText(" "
					+ promo_discount);
		} else
			promolayout.setVisibility(View.GONE);

		Log.d("mahi", "distance" + distance);

		((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
				.setText(perdistancecost + " "
						+ getResources().getString(R.string.text_cost_per_mile));

		Log.d("mahi", "time" + time);

		((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
				.setText(pertimecost + " "
						+ getResources().getString(R.string.text_cost_per_min));
		((TextView) mDialog.findViewById(R.id.tvDis1)).setText(" "
				+ distCostTmp);
		((TextView) mDialog.findViewById(R.id.tvtim)).setText(" " + time + " "
				+ "min");
		((TextView) mDialog.findViewById(R.id.tvdis)).setText(" "
				+ String.format("%.3f", Double.parseDouble(distance)) + " "
				+ "kms");

		((TextView) mDialog.findViewById(R.id.tvTime1)).setText(" "
				+ timeCostTmp);
		if (Integer.valueOf(payment_mode) == 1) {
			((MyFontTextView) mDialog.findViewById(R.id.payment_type))
					.setText(getResources().getString(R.string.cash_payment)
							+ " " + "NGN " + totalTmp + " "
							+ "cash to the driver.");
		} else
			((MyFontTextView) mDialog.findViewById(R.id.payment_type))
					.setText(getResources().getString(R.string.card_payment));

		// ((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(currency +
		// " "
		// + totalTmp);
		tvTotalPrice = (TextView) mDialog.findViewById(R.id.tvTotal1);
		tvTotalPrice.setText(" " + totalTmp);
		/*tvTotalPrice.setText(" " + quotedPrice);

		if (isQuoted) {
			tvTotalPrice.setText(" " + totalTmp);
		} else {
			tvTotalPrice.setText(" " + totalTmp);
		}*/
		// ((TextView) mDialog.findViewById(R.id.tvtotalcostvalue))
		// .setText(currency + " " + actualtotal);

		// ((TextView)
		// mDialog.findViewById(R.id.tvyousavevalue)).setText(currency
		// + " " + String.valueOf(decimalFormat.format(yousave)));

		btnConfirm = (Button) mDialog.findViewById(R.id.btnPay);
		btnReject = (Button) mDialog.findViewById(R.id.btnReject);
		if (!TextUtils.isEmpty(btnTitle)) {
			btnConfirm.setText(btnTitle);
		}

		if (payment_mode.equals("2")) {

			btnConfirm.setText(getString(R.string.pay_with_paypal));
		}

		if (!isQuoted) {
			btnReject.setVisibility(View.GONE);
		}
		btnReject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isQuotedPrice = false;
				btnReject.setVisibility(View.GONE);
				tvTotalPrice.setText(" " + totalTmp);

			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				// TODO Auto-generated method stub
				/*
				 * if (n == 1) { showPriceChooserAlertDialog(mDialog,
				 * quotedPrice, primary_id, primary_amount, secoundry_id,
				 * secoundry_amount); } else { if (!isQuoted) {
				 * callUserPaymentApi(USER_PAYMENT_CHOICE_ACTUAL); } else {
				 * callUserPaymentApi(USER_PAYMENT_CHOICE_QUOTED); } }
				 */
				//
				// showPriceChooserAlertDialog(mDialog, currency, quotedPrice,
				// primary_id, primary_amount, secoundry_id,
				// secoundry_amount);
				// callUserPaymentApi(USER_PAYMENT_CHOICE_ACTUAL);
				// if (btnConfirm.getText().toString()
				// .equals(getString(R.string.pay_with_paypal))) {
				// initLibrary();
				//
				// PayPalButtonClick(primary_id, primary_amount, secoundry_id,
				// secoundry_amount);
				//
				// } else {
				// mDialog.dismiss();
				// }
				// config accourding to payment types

			}
		});

		mDialog.show();

	}

	private void showPriceChooserAlertDialog(final Dialog mDialog,
			final String quotedPrice, final String primary_id,
			final String primary_amount, final String secoundry_id,
			final String secoundry_amount) {

		final Dialog dialog = new Dialog(this);
		View view = this.getLayoutInflater().inflate(
				R.layout.quoted_price_layout, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final RadioButton rbActualPrice;
		final RadioButton rbQuotedPrice;

		final RadioButton rbPayeeSelf;
		final RadioButton rbPayeeFriend;
		final RadioGroup rbgPayeeChoice;

		final EditText etFriendEmail;

		final MyFontTextView tvSelect;

		tvSelect = (MyFontTextView) view.findViewById(R.id.tvSelect);
		etFriendEmail = (EditText) view.findViewById(R.id.etFriendEmail);
		// etFriendEmail.setVisibility(View.GONE);

		rbActualPrice = (RadioButton) view.findViewById(R.id.priceChoiceActual);
		rbActualPrice.setText("Actual Price:  " + tvTotalPrice.getText());
		rbActualPrice.setChecked(true);

		rbQuotedPrice = (RadioButton) view.findViewById(R.id.priceChoiceQuoted);
		rbQuotedPrice.setText("Driver Quoted:  " + " " + quotedPrice);

		rbPayeeSelf = (RadioButton) view.findViewById(R.id.payeeChoiceSelf);
		rbPayeeSelf.setChecked(true);

		rbPayeeFriend = (RadioButton) view.findViewById(R.id.payeeChoiceFriend);

		rbgPayeeChoice = (RadioGroup) view.findViewById(R.id.payeeChoice);
		rbgPayeeChoice
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == rbPayeeFriend.getId()) {
							etFriendEmail.setVisibility(View.VISIBLE);
						} else {
							etFriendEmail.setVisibility(View.GONE);
						}
					}
				});

		if (!isQuoted) {
			rbQuotedPrice.setVisibility(View.GONE);
		}

		tvSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (etFriendEmail.length() != 0) {
					verifyEmail(etFriendEmail.getText().toString());
				} else {
					//Toast.makeText(activity, "Enter Your Friend Email", 3).show();
				}
				// if (!isQuoted) {
				// if (rbPayeeSelf.isChecked()) {
				// callUserPaymentApi(USER_PAYMENT_CHOICE_ACTUAL);
				// } else {
				// verifyEmail(etFriendEmail.getText().toString());
				// }
				// } else {
				// if (rbPayeeSelf.isChecked()) {
				// callUserPaymentApi(USER_PAYMENT_CHOICE_QUOTED);
				// } else {
				// verifyEmail(etFriendEmail.getText().toString());
				// }
				// }

			}
		});
		dialog.setContentView(view);

		dialog.show();
	}

	public void setTitle(String str) {
		tvTitle.setText(str);
	}

	public void setIconMenu(int img) {
		btnActionMenu.setImageResource(img);
	}

	public void setIcon(int img) {
		btnNotification.setImageResource(img);
	}

	public void goToMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
		finish();
	}

	/*public void initLibrary() {
		PayPal pp = PayPal.getInstance();
		if (pp == null) {

			pp = PayPal.initWithAppID(this, Const.PAYPAL_CLIENT_ID,
					PayPal.ENV_SANDBOX);

		}
	}*/

	public void PayPalButtonClick(String primary_id, String primary_amount,
			String secoundry_id, String secoundry_amount) {
		// Create a basic PayPal payment

		// PayPalPayment newPayment = new PayPalPayment();
		// newPayment.setSubtotal(new BigDecimal("1.0"));
		// newPayment.setCurrencyType("USD");
		// newPayment.setRecipient("npavankumar34@gmail.com");
		// newPayment.setMerchantName("My Company");
		// Log.d("pavan", "calling intent");
		// if( PayPal.getInstance()!=null){
		// Log.d("pavan", "in if");
		// Intent paypalIntent = PayPal.getInstance().checkout(newPayment,
		// this);
		// startActivityForResult(paypalIntent, 1);
		//

		Log.d("pavan", "primary " + primary_id);
		Log.d("pavan", "primary_amount " + primary_amount);

		Log.d("pavan", "secoundry_amount " + secoundry_amount);
		Log.d("pavan", "secoundry_id " + secoundry_id);

///////		PayPalReceiverDetails receiver0, receiver1;
//		receiver0 = new PayPalReceiverDetails();
//		receiver0.setRecipient(primary_id);
//		receiver0.setSubtotal(new BigDecimal(primary_amount));
//
//		receiver1 = new PayPalReceiverDetails();
//		receiver1.setRecipient(secoundry_id);
//		receiver1.setSubtotal(new BigDecimal(secoundry_amount));
//
//		PayPalAdvancedPayment advPayment = new PayPalAdvancedPayment();
//		advPayment.setCurrencyType(Const.CURRENCY_TYPE);
//
//		if (!primary_amount.equals("0"))
//			advPayment.getReceivers().add(receiver0);
//		if (!secoundry_amount.equals("0"))
//			advPayment.getReceivers().add(receiver1);
//		Intent paypalIntent = PayPal.getInstance().checkout(advPayment, this);
///////		this.startActivityForResult(paypalIntent, PAYPAL_RESPONSE);

	}

	private void sendPaypalData(String resonse) {
		if (!AndyUtils.isNetworkAvailable(getApplicationContext())) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					(Activity) getApplicationContext());
			return;
		}
		// AndyUtils.showCustomProgressDialog((ActionBarBaseActivitiy)getBaseContext(),
		// getString(R.string.text_contacting), true, this);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SEND_PAYPAL_RESPONSE);
		map.put(Const.Params.TOKEN, new PreferenceHelper(
				getApplicationContext()).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(getApplicationContext()).getUserId());
		map.put(Const.Params.PAYPAL_DATA, resonse);
		map.put(Const.Params.REQUEST_ID, "1");
		// map.put(Const.Params.DISTANCE, "0");
		new HttpRequester(getApplicationContext(), map,
				Const.ServiceCode.UPDATE_PAYPAL_ID, this);
	}

	private void callUserPaymentApi(int paymentCode) {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.please_wait), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.USER_PAYMENT);
		map.put(Const.Params.TOKEN, new PreferenceHelper(
				getApplicationContext()).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(getApplicationContext()).getUserId());
		map.put(Const.Params.AMOUNT_STATUS, String.valueOf(paymentCode));
		map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(
				getApplicationContext()).getRequestId()));
		// map.put(Const.Params.DISTANCE, "0");
		Log.e("", "callUserPaymentApi:" + map);
		new HttpRequester(getApplicationContext(), map,
				Const.ServiceCode.USER_PAYMENT, this);
	}

	private void verifyEmail(String email) {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.please_wait), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SEARCH_USER);
		map.put(Const.Params.TOKEN, new PreferenceHelper(
				getApplicationContext()).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(getApplicationContext()).getUserId());
		map.put(Const.Params.EMAIL, String.valueOf(email));
		Log.e("", "verifyEmail:" + map);
		new HttpRequester(getApplicationContext(), map,
				Const.ServiceCode.SEARCH_USER, this);
	}

	private void callRequestFriendApi(int friendId) {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.requesting_friend), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.REQUEST_SEND_FRIEND);
		map.put(Const.Params.TOKEN, new PreferenceHelper(
				getApplicationContext()).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(getApplicationContext()).getUserId());
		map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(
				getApplicationContext()).getRequestId()));
		map.put(Const.Params.FRIEND_ID, String.valueOf(friendId));
		new HttpRequester(getApplicationContext(), map,
				Const.ServiceCode.REQUEST_SEND_FRIEND, this);
	}

	private void callCheckFriendPaymentApi() {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.requesting_friend), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CHECK_FRIEND_PAYMENT);
		map.put(Const.Params.TOKEN, new PreferenceHelper(
				getApplicationContext()).getSessionToken());
		map.put(Const.Params.ID,
				new PreferenceHelper(getApplicationContext()).getUserId());
		map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(
				getApplicationContext()).getRequestId()));
		new HttpRequester(getApplicationContext(), map,
				Const.ServiceCode.CHECK_FRIEND_PAYMENT, this);
	}

}
