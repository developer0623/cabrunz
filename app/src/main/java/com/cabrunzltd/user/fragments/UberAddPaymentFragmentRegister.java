package com.cabrunzltd.user.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cabrunzltd.user.MainDrawerActivity;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.cabrunzltd.user.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.util.StripeTextUtils;

import java.util.HashMap;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * @author Hardik A Bhalodi
 */
public class UberAddPaymentFragmentRegister extends UberBaseFragmentRegister {

	private Button btnAddPayment;
	private ImageView btnScan;
	private final int MY_SCAN_REQUEST_CODE = 111;
	private EditText etCreditCardNum, etCvc, etYear, etMonth;

	public static final String[] PREFIXES_AMERICAN_EXPRESS = { "34", "37" };
	public static final String[] PREFIXES_DISCOVER = { "60", "62", "64", "65" };
	public static final String[] PREFIXES_JCB = { "35" };
	public static final String[] PREFIXES_DINERS_CLUB = { "300", "301", "302",
			"303", "304", "305", "309", "36", "38", "37", "39" };
	public static final String[] PREFIXES_VISA = { "4" };
	public static final String[] PREFIXES_MASTERCARD = { "50", "51", "52",
			"53", "54", "55" };
	public static final String AMERICAN_EXPRESS = "American Express";
	public static final String DISCOVER = "Discover";
	public static final String JCB = "JCB";
	public static final String DINERS_CLUB = "Diners Club";
	public static final String VISA = "Visa";
	public static final String MASTERCARD = "MasterCard";
	public static final String UNKNOWN = "Unknown";
	public static final int MAX_LENGTH_STANDARD = 16;
	public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
	public static final int MAX_LENGTH_DINERS_CLUB = 14;
	private String type;
	private String token, id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		token = getArguments().getString(Const.Params.TOKEN);
		id = getArguments().getString(Const.Params.ID);
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
		activity.setTitle(getResources().getString(
				R.string.text_addpayment_small));
		activity.setIconMenu(R.drawable.ic_payment);
		activity.btnNotification.setVisibility(View.INVISIBLE);
		View view = inflater.inflate(R.layout.fragment_payment, container,
				false);
		btnAddPayment = (Button) view.findViewById(R.id.btnAddPayment);
		view.findViewById(R.id.btnPaymentSkip).setOnClickListener(this);
		btnScan = (ImageView) view.findViewById(R.id.btnScan);

		etCreditCardNum = (EditText) view
				.findViewById(R.id.edtRegisterCreditCardNumber);
		etCreditCardNum.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (StripeTextUtils.isBlank(s.toString())) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, null);
				}
				type = getType(s.toString());

				if (type.equals(VISA)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_visa), null,
							null, null);

				} else if (type.equals(MASTERCARD)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_mastercard),
							null, null, null);

				} else if (type.equals(AMERICAN_EXPRESS)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_amex), null,
							null, null);

				} else if (type.equals(DISCOVER)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_discover), null,
							null, null);

				}
				else
				{
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, null);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		etCvc = (EditText) view.findViewById(R.id.edtRegistercvc);
		etYear = (EditText) view.findViewById(R.id.edtRegisterexpYear);
		etMonth = (EditText) view.findViewById(R.id.edtRegisterexpMonth);
		etYear.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (etYear.getText().toString().length() == 2) {
					etCvc.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		etMonth.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (etMonth.getText().toString().length() == 2) {
					etYear.requestFocus();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		btnScan.setOnClickListener(this);
		btnAddPayment.setOnClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
	
		new PreferenceHelper(activity)
		.putCurrentFragment(Const.FRAGMENT_PAYMENT_REGISTER);
		activity.actionBar.setTitle(getString(R.string.text_addpayment_small));
		super.onResume();
		activity.currentFragment = Const.FRAGMENT_PAYMENT_REGISTER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		etCreditCardNum.requestFocus();
		activity.showKeyboard(etCreditCardNum);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnAddPayment:
			if (isValidate()) {
				try {
					saveCreditCard();
				} catch (AuthenticationException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.btnScan:
			scan();
			break;
		case R.id.btnPaymentSkip:
			OnBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		if (etCreditCardNum.getText().length() == 0
				|| etCvc.getText().length() == 0
				|| etMonth.getText().length() == 0
				|| etYear.getText().length() == 0) {
			AndyUtils.showToast("Enter Proper data", activity);
			return false;
		}
		return true;
	}

	private void scan() {
		Intent scanIntent = new Intent(activity, CardIOActivity.class);

		// required for authentication with card.io
		//scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);

		// customize these values to suit your needs.
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default:
																		// true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default:
																		// false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default:
																				// false

		// hides the manual entry button
		// if set, developers should provide their own manual entry
		// mechanism in
		// the app
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default:
																				// false
		scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false);

		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this
		// activity.
		activity.startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE,
				Const.FRAGMENT_PAYMENT_REGISTER);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MY_SCAN_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null
						&& data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data
							.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

					etCreditCardNum.setText(scanResult.getRedactedCardNumber());


					if (scanResult.isExpiryValid()) {
						// resultStr += "Expiration Date: " +
						// scanResult.expiryMonth
						// +
						// "/"
						// + scanResult.expiryYear + "\n";
						etMonth.setText(scanResult.expiryMonth + "");

						etYear.setText(scanResult.expiryYear + "");
					}

					if (scanResult.cvv != null) {

						etCvc.setText(scanResult.cvv);
					}

				} else {
					AndyUtils.showToast("Scan was canceled.", activity);
				}
			} else {
				AndyUtils.showToast("Scan was uncessfull.", activity);
			}
			break;

		}

	}

	public void saveCreditCard() throws AuthenticationException {

		Card card = new Card(etCreditCardNum.getText().toString(),
				Integer.parseInt(etMonth.getText().toString()),
				Integer.parseInt(etYear.getText().toString()), etCvc.getText()
						.toString());

		boolean validation = card.validateCard();
		if (validation) {
			AndyUtils.showCustomProgressDialog(activity,
					getString(R.string.adding_payment), false, null);
			Stripe stripe1 = new Stripe(getContext(), Const.PUBLISHABLE_KEY);
//			stripe1.setDefaultPublishableKey(Const.PUBLISHABLE_KEY);
			stripe1.createToken(card,
					new TokenCallback() {
						public void onSuccess(Token token) {

							String lastFour = etCreditCardNum.getText()
									.toString().toString();
							lastFour = lastFour.substring(lastFour.length() - 4);
							addCard(token.getId(), lastFour);
						}

						public void onError(Exception error) {
							AndyUtils.showToast("Error", activity);
							// finishProgress();
							AndyUtils.removeCustomProgressDialog();
						}
					});
		} else if (!card.validateNumber()) {
			// handleError("The card number that you entered is invalid");
			AndyUtils.showToast("The card number that you entered is invalid",
					activity);
		} else if (!card.validateExpiryDate()) {
			// handleError("");
			AndyUtils
					.showToast(
							"The expiration date that you entered is invalid",
							activity);
		} else if (!card.validateCVC()) {
			// handleError("");
			AndyUtils.showToast("The CVC code that you entered is invalid",
					activity);

		} else {
			// handleError("");
			AndyUtils.showToast(
					"The card details that you entered are invalid", activity);
		}
	}

	public String getType(String number) {
		if (!StripeTextUtils.isBlank(number)) {
			if (StripeTextUtils.hasAnyPrefix(number, PREFIXES_AMERICAN_EXPRESS)) {
				return AMERICAN_EXPRESS;
			} else if (StripeTextUtils.hasAnyPrefix(number, PREFIXES_DISCOVER)) {
				return DISCOVER;
			} else if (StripeTextUtils.hasAnyPrefix(number, PREFIXES_JCB)) {
				return JCB;
			} else if (StripeTextUtils.hasAnyPrefix(number, PREFIXES_DINERS_CLUB)) {
				return DINERS_CLUB;
			} else if (StripeTextUtils.hasAnyPrefix(number, PREFIXES_VISA)) {
				return VISA;
			} else if (StripeTextUtils.hasAnyPrefix(number, PREFIXES_MASTERCARD)) {
				return MASTERCARD;
			} else {
				return UNKNOWN;
			}
		}
		return UNKNOWN;

	}

	private void addCard(String stipeToken, String lastFour) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.ADD_CARD);
		map.put(Const.Params.ID, id);
		map.put(Const.Params.TOKEN, token);
		map.put(Const.Params.STRIPE_TOKEN, stipeToken);
		map.put(Const.Params.LAST_FOUR, lastFour);

		new HttpRequester(activity, map, Const.ServiceCode.ADD_CARD, this);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {
		case Const.ServiceCode.ADD_CARD:

			if (new ParseContent(activity).isSuccess(response)) {
				AndyUtils.showToast(getString(R.string.text_add_card_scucess),
						activity);
				activity.startActivity(new Intent(activity,
						MainDrawerActivity.class));
			} else
				AndyUtils.showToast(
						getString(R.string.text_not_add_card_unscucess),
						activity);
			activity.finish();
			// activity.removeAllFragment(new UberMainFragment(), false,
			// Const.FRAGMENT_MAIN);
			break;
		default:
			break;
		}
	}

}
