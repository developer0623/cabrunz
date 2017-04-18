/**
 * 
 */

package com.cabrunzltd.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabrunzltd.user.adapter.PaymentListAdapter;
import com.cabrunzltd.user.models.Cards;
import com.cabrunzltd.user.parse.HttpRequester;
import com.cabrunzltd.user.parse.ParseContent;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import com.stripe.android.util.StripeTextUtils;

// import com.stripe.android.util.TextUtils;
//import com.stripe.exception.AuthenticationException;


import java.util.ArrayList;
import java.util.HashMap;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class UberViewPaymentActivity extends ActionBarBaseActivitiy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onCreate(android.os.Bundle)
	 */
	private ListView listViewPayment;
	private PaymentListAdapter adapter;
	private ArrayList<Cards> listCards;
	private int REQUEST_ADD_CARD = 1;
	private LinearLayout tvNoHistory;
	private TextView tvHeaderText;
	private View v;
	private ImageView btnAddNewPayment;
	private Context context;
	private Button btnAddPayment;
	private ImageView btnScan;
	private final int MY_SCAN_REQUEST_CODE = 111;
	private EditText etCreditCardNum, etCvc, etYear, etMonth,edtCreditCardNumberName;
	// private String patternVisa = "^4[0-9]{12}(?:[0-9]{3})?$";
	// private String patternMasterCard = "^5[1-5][0-9]{14}$";
	// private String patternAmericanExpress = "^3[47][0-9]{13}$";
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_add_card);

		setTitle(getString(R.string.text_cards));
		setIcon(R.drawable.icon_back);
		setIconMenu(R.drawable.ic_payment);

		listViewPayment = (ListView) findViewById(R.id.listViewPayment);
		tvNoHistory = (LinearLayout) findViewById(R.id.llEmptyView);
		tvHeaderText = (TextView) findViewById(R.id.tvHeaderText);
		// btnAddNewPayment = (ImageView) findViewById(R.id.btnAddNewPayment);
		// btnAddNewPayment.setOnClickListener(this);
		v = findViewById(R.id.line);
		v.setVisibility(View.GONE);
		listCards = new ArrayList<Cards>();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
		// actionBar.setTitle(getString(R.string.text_cards));
		adapter = new PaymentListAdapter(this, listCards);
		listViewPayment.setAdapter(adapter);

		listViewPayment
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int pos, long arg3) {

						final Cards cardsingle = (Cards) listViewPayment
								.getItemAtPosition(pos);

						if (listCards.size() <= 1) {

							Toast.makeText(
									getApplicationContext(),
									"Failed to delete card ! There is only one default card !",
									Toast.LENGTH_LONG).show();
						} else {

							new AlertDialog.Builder(context)
									.setTitle("Delete entry")
									.setMessage(
											"Are you sure you want to delete card *****"
													+ cardsingle.getLastFour()
													+ "?")
									.setPositiveButton(
											android.R.string.yes,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													removecards(cardsingle
															.getId());
												}
											})
									.setNegativeButton(
											android.R.string.no,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													// do nothing
												}
											})

									.show();
						}

						return true;
					}

				});

		btnAddPayment = (Button) findViewById(R.id.btnAddPayment);
		// btnPaymentSkip = (Button) findViewById(R.id.btnPaymentSkip);
		// btnPaymentSkip.setVisibility(View.GONE);
		btnScan = (ImageView) findViewById(R.id.btnScan);
		etCreditCardNum = (EditText) findViewById(R.id.edtRegisterCreditCardNumber);
		edtCreditCardNumberName = (EditText) findViewById(R.id.edtCreditCardNumberName);
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
				// else if (type.equals(DINERS_CLUB)) {
				// etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
				// getResources().getDrawable(
				// R.drawable.ub__creditcard_discover), null,
				// null, null);
				//
				// }
				else {
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
		etCvc = (EditText) findViewById(R.id.edtRegistercvc);
		etYear = (EditText) findViewById(R.id.edtRegisterexpYear);
		etMonth = (EditText) findViewById(R.id.edtRegisterexpMonth);
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
		// findViewById(R.id.btnPaymentSkip).setOnClickListener(this);

		getCards();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnAddPayment:
			if (isValidate()) {
				saveCreditCard();
			}
			break;
		case R.id.btnScan:
			scan();
			break;
		case R.id.btnActionNotification:
			onBackPressed();
			break;
		case R.id.btnAddNewPayment:
			startActivityForResult(new Intent(this,
					UberAddPaymentActivity.class), REQUEST_ADD_CARD);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		if (etCreditCardNum.getText().length() == 0
				|| etCvc.getText().length() == 0
				|| etMonth.getText().length() == 0
				|| etYear.getText().length() == 0) {
			AndyUtils.showToast("Enter Proper data", this);
			return false;
		}
		return true;
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

	private void scan() {
		Intent scanIntent = new Intent(this, CardIOActivity.class);

		// required for authentication with card.io
		//////scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN,Const.MY_CARDIO_APP_TOKEN);

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

		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this
		// activity.
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}

	public void saveCreditCard() {

		Card card = new Card(etCreditCardNum.getText().toString(),
				Integer.parseInt(etMonth.getText().toString()),
				Integer.parseInt(etYear.getText().toString()), etCvc.getText()
						.toString());
		boolean validation = card.validateCard();
		if (validation) {
			AndyUtils.showCustomProgressDialog(this,
					getString(R.string.adding_payment), false, null);
			Stripe stripe = null;
			stripe = new Stripe(getBaseContext(), Const.PUBLISHABLE_KEY);
			stripe.createToken(card,
					new TokenCallback() {
						public void onSuccess(Token token) {
							// getTokenList().addToList(token);
							// AndyUtils.showToast(token.getId(),
							// UberAddPaymentActivity.this);
							String lastFour = etCreditCardNum.getText()
									.toString();
							lastFour = lastFour.substring(lastFour.length() - 4);
							addCard(token.getId(), lastFour);
							// finishProgress();

						}

						public void onError(Exception error) {
							AndyUtils.showToast("Error",
									UberViewPaymentActivity.this);
							// finishProgress();
							AndyUtils.removeCustomProgressDialog();
						}
					});
		} else if (!card.validateNumber()) {
			// handleError("The card number that you entered is invalid");
			AndyUtils.showToast("The card number that you entered is invalid",
					this);
		} else if (!card.validateExpiryDate()) {
			// handleError("");
			AndyUtils.showToast(
					"The expiration date that you entered is invalid", this);
		} else if (!card.validateCVC()) {
			// handleError("");
			AndyUtils.showToast("The CVC code that you entered is invalid",
					this);

		} else {
			// handleError("");
			AndyUtils.showToast(
					"The card details that you entered are invalid", this);
		}
	}

	private void addCard(String stipeToken, String lastFour) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.ADD_CARD);
		map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(this).getSessionToken());
		map.put(Const.Params.STRIPE_TOKEN, stipeToken);
		map.put(Const.Params.LAST_FOUR, lastFour);
		new HttpRequester(this, map, Const.ServiceCode.ADD_CARD, this);
	}

	private void removecards(int cardid) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.REMOVE_CARD);
		map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
		map.put(Const.Params.TOKEN,
				new PreferenceHelper(this).getSessionToken());
		map.put(Const.Params.CARD_ID, String.valueOf(cardid));

		Log.d("hey", "in remove card");
		new HttpRequester(this, map, Const.ServiceCode.REMOVE_CARD, this);
	}

	private void getCards() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.progress_loading), false, null);
		Log.d("user details", "ID=  " + new PreferenceHelper(this).getUserId()
				+ "TOKEN=  " + new PreferenceHelper(this).getSessionToken());

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_CARDS + Const.Params.ID + "="
						+ new PreferenceHelper(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ new PreferenceHelper(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.GET_CARDS, true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		Log.d("user detail", response);
		switch (serviceCode) {
		case Const.ServiceCode.GET_CARDS:
			Log.d("NIKHIL", "get cards" + response);
			if (new ParseContent(this).isSuccess(response)) {
				listCards.clear();
				new ParseContent(this).parseCards(response, listCards);
				if (listCards.size() > 0) {
					listViewPayment.setVisibility(View.VISIBLE);
					tvNoHistory.setVisibility(View.GONE);
					v.setVisibility(View.VISIBLE);
					tvHeaderText.setVisibility(View.VISIBLE);
					// btnAddNewPayment.setImageResource(R.drawable.another_card);
				}
				adapter.notifyDataSetChanged();
			} else {
				listViewPayment.setVisibility(View.GONE);
				tvNoHistory.setVisibility(View.VISIBLE);
				tvHeaderText.setVisibility(View.GONE);
				v.setVisibility(View.GONE);
				// btnAddNewPayment.setImageResource(R.drawable.add_credit);
			}
			break;

		case Const.ServiceCode.ADD_CARD:

			if (new ParseContent(this).isSuccess(response)) {
				AndyUtils.showToast(getString(R.string.text_add_card_scucess),
						this);
				getCards();
				etCreditCardNum.getText().clear();
				etMonth.getText().clear();
				etCvc.getText().clear();
				etYear.getText().clear();
				edtCreditCardNumberName.getText().clear();
				
				setResult(RESULT_OK);
			} else {
				AndyUtils.showToast(
						getString(R.string.text_not_add_card_unscucess), this);
				setResult(RESULT_CANCELED);
			}
			break;

		case Const.ServiceCode.REMOVE_CARD:
			Log.d("hey", response);
			Log.d("hey",
					String.valueOf(new ParseContent(this).isSuccess(response)));
			if (new ParseContent(this).isSuccess(response)) {
				Log.d("hey", "hmmm");
				getCards();
				Toast.makeText(context, "Card removed successfully",
						Toast.LENGTH_LONG).show();
			}
			break;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			getCards();
			break;
		case MY_SCAN_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null
						&& data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data
							.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

					// Never log a raw card number. Avoid displaying it, but if
					// necessary use getFormattedCardNumber()
					// resultStr = "Card Number: " +
					// scanResult.getRedactedCardNumber()
					// + "\n";
					etCreditCardNum.setText(scanResult.getRedactedCardNumber());

					// Do something with the raw number, e.g.:
					// myService.setCardNumber( scanResult.cardNumber );

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
						// Never log or display a CVV
						// resultStr += "CVV has " + scanResult.cvv.length()
						// + " digits.\n";
						etCvc.setText(scanResult.cvv);
					}

					// if (scanResult.postalCode != null) {
					// resultStr += "Postal Code: " + scanResult.postalCode +
					// "\n";
					// }
				} else {
					// resultStr = "Scan was canceled.";
					AndyUtils.showToast("Scan was cancelled.", this);
				}
			} else {
				AndyUtils.showToast("Scan was unsuccessful.", this);
			}
			break;

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
