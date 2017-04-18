package com.cabrunzltd.user;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cabrunzltd.user.fragments.UberRegisterFragment;
import com.cabrunzltd.user.fragments.UberSignInFragment;
import com.cabrunzltd.user.utils.AndyUtils;
import com.cabrunzltd.user.utils.Const;
import com.cabrunzltd.user.utils.PreferenceHelper;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * @author Hardik A Bhalodi
 */
public class RegisterActivity extends ActionBarBaseActivitiy {

	public static Dialog mDialog;
	Permission[] permissions = new Permission[] { Permission.EMAIL };
	SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setPermissions(permissions).build();
	public PreferenceHelper phelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// actionBar.hide();
		setContentView(R.layout.register_activity);
		SimpleFacebook.setConfiguration(configuration);
		phelper = new PreferenceHelper(this);
		//actionBar.setDisplayShowCustomEnabled(true);

		setIcon(R.drawable.icon_back);

		if (getIntent().getBooleanExtra("isSignin", false)) {
			gotSignInFragment();
		} else {
			goToRegisterFragment();
		}

		// UberMainFragment mainFrag = new UberMainFragment();
		// addFragment(mainFrag, false, Const.FRAGMENT_MAIN);

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

	public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {
			AndyUtils.showCustomProgressDialog(this,
					getString(R.string.progress_loading), false, null);
			new GCMRegisterHendler(RegisterActivity.this,
					mHandleMessageReceiver);

		}
	}

	public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {

			if (mHandleMessageReceiver != null) {
				unregisterReceiver(mHandleMessageReceiver);
			}

		}

	}

	private void gotSignInFragment() {
		UberSignInFragment signInFrag = new UberSignInFragment();
		clearBackStack();
		addFragment(signInFrag, false, Const.FRAGMENT_SIGNIN);
	}

	private void goToRegisterFragment() {
		UberRegisterFragment regFrag = new UberRegisterFragment();
		clearBackStack();
		addFragment(regFrag, false, Const.FRAGMENT_REGISTER);
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

	public void showKeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(INPUT_METHOD_SERVICE);

		// check if no view has focus:
		// View view = activity.getCurrentFocus();
		// if (view != null) {
		inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
		// }
	}

}
