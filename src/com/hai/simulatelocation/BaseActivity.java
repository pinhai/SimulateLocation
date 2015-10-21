package com.hai.simulatelocation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 基础activity， 所有的Activity都要继承自这个基类
 * 
 */
@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity {

	public String TAG = "BaseActivity";
	protected Handler mHandler = new Handler();
	public FragmentManager mFragManager = null;
	public ImageButton imgbtn_title_back;
	public TextView tv_title;
	public Button btn_title;
	public static boolean isForeground = false;
	public ProgressDialog progressDialog;
	private ProgressDialog progressDialog2;

	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public static final int notifiId = 11;
	public static NotificationManager notificationManager;

	public void initTitle(String title) {
		imgbtn_title_back = (ImageButton) findViewById(R.id.imgbtn_title_back);
		imgbtn_title_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);
		btn_title = (Button) findViewById(R.id.btn_title);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getWindow().getDecorView().setSystemUiVisibility(10);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T findView(int id){
		return (T)findViewById(id);
	}

	public void showDialog(String str) {	
		progressDialog = new ProgressDialog(BaseActivity.this);
		progressDialog.setMessage(str);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void dissmissDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 点击返回键，将结束当前Activity
	 */
	public void showProgressDialog(){
		if(progressDialog2 == null){
			progressDialog2 = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
			progressDialog2.setTitle(getString(R.string.prompt));
			progressDialog2.setMessage(getString(R.string.loading));
			progressDialog2.setCancelable(true);
			progressDialog2.setCanceledOnTouchOutside(false);
		}
		progressDialog2.show();
		progressDialog2.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
	}
	
	public void dismissProgressDialog(){
		if(progressDialog2 != null){
			progressDialog2.dismiss();
		}
	}
	
	public void showProgressDialog(String str, boolean cancelable){
		if(progressDialog2 == null){
			progressDialog2 = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
			progressDialog2.setTitle(getString(R.string.prompt));
			progressDialog2.setMessage(str);
			progressDialog2.setCancelable(cancelable);
		}
		progressDialog2.show();
		setDialogDismissListener();
	}
	
	public boolean isShowingProgressDialog(){
		if(progressDialog2 != null){
			return progressDialog2.isShowing();
		}
		return false;
	}
	
	private void setDialogDismissListener(){
		if(progressDialog2 != null){
			progressDialog2.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					getWindow().getDecorView().setSystemUiVisibility(10);
				}
			});
		}
	}

	protected String mEntryClass;

	public String getEntryClass() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// moveTaskToBack(false);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过父类BaseActivity发送广播
	 */
	public void sendAction(String action) {
		Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	/**
	 * 通过父类BaseActivity发送广播，含有Bundle数据
	 */
	public void sendAction(String action, Bundle pBundle) {
		Intent intent = new Intent(action);
		intent.putExtras(pBundle);
		sendBroadcast(intent);
	}

	/*
	 * 关闭SQLite一定要在这里调用 (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ShareSDK.stopSDK(this);
	}
}
