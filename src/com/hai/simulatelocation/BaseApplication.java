package com.hai.simulatelocation;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

/**
 * 基础Application
 * 
 * @author www.1knet.com
 */
public class BaseApplication extends Application {
	private static final String TAG = "BaseApplication";

	public static final boolean debug = true;
	private static BaseApplication instance;
	
	public static BaseApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

}
