/**
 * 
 */
package com.cabrunzltd.user;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.cabrunzltd.user.utils.Const;


/**
 * @author Kishan H DhamatO
 * 
 */
public class MenuDescActivity extends ActionBarBaseActivitiy {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_desc_activity);
		btnActionMenu.setVisibility(View.INVISIBLE);
		setIcon(R.drawable.back);
		setIconMenu(R.drawable.taxi);
		setTitle(getIntent().getStringExtra(Const.Params.TITLE));
		webView = (WebView) findViewById(R.id.wvDesc);
		webView.loadData(getIntent().getStringExtra(Const.Params.CONTENT),
				"text/html", "utf-8");

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
}
