package com.cabrunzltd.user;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cabrunzltd.user.component.MyFontButton;
import com.cabrunzltd.user.component.MyFontEdittextView;
import com.cabrunzltd.user.parse.AsyncTaskCompleteListener;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class promotionActivity extends ActionBarBaseActivitiy implements
		AsyncTaskCompleteListener {

	private MyFontEdittextView promofield;
	private MyFontButton apply;
	private TextView ledger;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotions);
		setTitle("PROMOTIONS");


		ActionBar actionBar= getActionBar();

		actionBar.setIcon(R.drawable.promo);
		promofield = (MyFontEdittextView) findViewById(R.id.promotionpromo);
		apply = (MyFontButton) findViewById(R.id.promotionpromoApply);
		ledger = (TextView) findViewById(R.id.creditbalance);
		apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (promofield.getText().length() == 0) {
					AndyUtils.showToast("Please enter Promo Code", context);
				} else if (!AndyUtils.isNetworkAvailable(context)) {
					AndyUtils.showToast(
							getResources().getString(
									R.string.dialog_no_inter_message), context);
				} else {
					applyReffralCode();
				}

			}
		});
		getledger();

	}

	private void applyReffralCode() {

		AndyUtils.showCustomProgressDialog(context,
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.APPLY_REFFRAL_CODE);
		map.put(Const.Params.REFERRAL_CODE, promofield.getText().toString());
		map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.APPLY_REFFRAL_CODE, this);

	}

	void getledger() {
		Log.d("yyy", "in ledger");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_CREDITS + Const.Params.ID + "="
						+ new PreferenceHelper(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(this).getSessionToken());

		new HttpRequester(this, map, Const.ServiceCode.GET_CREDITS, true, this);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub

		Log.e("", "" + response);
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.APPLY_REFFRAL_CODE:
			promofield.setText("");
			if (new ParseContent(this).isSuccess(response)) {
				Toast.makeText(context, "Applied Successfully",
						Toast.LENGTH_LONG).show();
				getledger();
			}
			break;
		case Const.ServiceCode.GET_CREDITS:
			if (response != null) {
				if (new ParseContent(this).isSuccess(response)) {
					try {
						JSONObject jsonobject = new JSONObject(response);
						JSONObject creditobject = jsonobject
								.getJSONObject("credits");
						String currency = creditobject.getString("currency");
						ledger.setText(currency + " "
								+ creditobject.getString("balance"));

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnActionNotification:
				onBackPressed();
				break;

			default:
				break;
		}
	}
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
