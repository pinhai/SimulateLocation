package com.hai.simulatelocation.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * 定位服务
 * @author Administrator
 *
 */
public class LocationService extends Service {

	private LocationManager locationManager;
	private String mMockProviderName = LocationManager.NETWORK_PROVIDER;
	private double latitude = 0, longitude = 0;
	
	private Thread mockLocationThread;
	public boolean isMockLocation = true;
	
	public static final String ACTION_MOCK_LOCATION = "com.action.ACTION_MOCK_LOCATION";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		initBroadcast();
		initLocation();
		startMockLocationThread();
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	private void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_MOCK_LOCATION);
		registerReceiver(receiver, intentFilter);
	}

	/**
	 *  初始化 位置模拟
	 * 
	 */
	private void initLocation() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addTestProvider(mMockProviderName, false, true, false,
				false, true, true, true, 0, 5);
		locationManager.setTestProviderEnabled(mMockProviderName, true);
		locationManager.requestLocationUpdates(mMockProviderName, 0, 0, locationListener);
	}
	
	private void startMockLocationThread() {
		mockLocationThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(isMockLocation){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(longitude != 0 && latitude != 0){
						setLocation(longitude, latitude);
					}
				}
			}
		});
		mockLocationThread.start();
	}
	
	/**
	 * 设置当前虚拟的位置
	 * @param longitude
	 * @param latitude
	 */
	@SuppressLint("NewApi")
	private void setLocation(double longitude, double latitude) {
		Location location = new Location(mMockProviderName);
		location.setTime(System.currentTimeMillis());
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		location.setAltitude(2.0f);
		location.setAccuracy(3.0f);
		location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
		locationManager.setTestProviderLocation(mMockProviderName, location);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION_MOCK_LOCATION)){
				latitude = intent.getDoubleExtra("latitude", 0);
				longitude = intent.getDoubleExtra("longitude", 0);
				
			}
		}
	};
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}
	};

}
