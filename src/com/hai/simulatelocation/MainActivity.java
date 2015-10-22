package com.hai.simulatelocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hai.simulatelocation.location.LocationByBaiduMapAPI;
import com.hai.simulatelocation.service.LocationService;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class MainActivity extends BaseActivity{

	private TextView tv_location;
	
	private LocationByBaiduMapAPI locationByBaiduMapAPI;
	private MapView mv_baidu;
	private BaiduMap mBaiduMap;
	private Marker mMarker;
	private LatLng curLatlng;
	private GeoCoder mGeoCoder;
	
	private BitmapDescriptor bd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		
		final ContentResolver mContentResolver = getContentResolver();
		Settings.System.putInt( mContentResolver, Settings.System.WIFI_USE_STATIC_IP, 1);
		Settings.System.putString( mContentResolver, Settings.System.WIFI_STATIC_IP, "113.99.177.30");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				GetNetIp("http://www.cmyip.com/");
			}
		}).start();
		
		initView();
		initData();
		initMap();
	}
	
	private String GetNetIp(String ipaddr) {
		URL infoUrl = null;
		InputStream inStream = null;
		String ipLine = "";
		HttpURLConnection httpConnection = null;
		try {
			infoUrl = new URL(ipaddr);
			URLConnection connection = infoUrl.openConnection();
			httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
					strber.append(line + "\n");

				Pattern pattern = Pattern.compile(
						"((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
				Matcher matcher = pattern.matcher(strber.toString());
				if (matcher.find()) {
					ipLine = matcher.group();
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inStream.close();
				httpConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ipLine;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mv_baidu.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mv_baidu.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mv_baidu.onDestroy();
	}
	
	public void initView() {
		tv_location = findView(R.id.tv_location);
		mv_baidu = findView(R.id.mv_baidu);
		
	}
	
	private void initData() {
		Intent intent = new Intent(MainActivity.this, LocationService.class);
		startService(intent);
		
		bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		locationByBaiduMapAPI = new LocationByBaiduMapAPI(this, bdLocationListener);
		locationByBaiduMapAPI.startLocation();
	}
	
	private void initMap() {
		mBaiduMap = mv_baidu.getMap();
		mBaiduMap.setOnMapClickListener(onMapClickListener);
		
		// 初始化搜索模块，注册事件监听
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
		
		// 开启定位图层  
		mBaiduMap.setMyLocationEnabled(true);  
		
	}
	
	/**
	 * setCurrentMapLatLng 设置当前坐标
	 */
	private void setCurrentMapLatLng(LatLng latlng) {
		
		LatLng ll = new LatLng(latlng.latitude, latlng.longitude);
		curLatlng = latlng;
		if(mMarker == null){
			OverlayOptions oo = new MarkerOptions().position(ll).icon(bd).zIndex(9)
					.draggable(true);
			mMarker = (Marker) (mBaiduMap.addOverlay(oo));
		}else{
			mMarker.setPosition(latlng);
		}
		
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
		
		sendBroadcastMockLocation();

		// 根据经纬度坐标 找到实地信息，会在接口onGetReverseGeoCodeResult中呈现结果
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
	}
	
	private void sendBroadcastMockLocation() {
		Intent intent = new Intent(LocationService.ACTION_MOCK_LOCATION);
		intent.putExtra("latitude", curLatlng.latitude);
		intent.putExtra("longitude", curLatlng.longitude);
		sendBroadcast(intent);
	}

	/**
	 * 初次定位
	 */
	private BDLocationListener bdLocationListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mv_baidu == null) {
				return;
			}

			setCurrentMapLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
		}
	};
	
	private OnMapClickListener onMapClickListener = new OnMapClickListener() {
		
		@Override
		public boolean onMapPoiClick(MapPoi mapPoi) {
			// map view 销毁后不在处理新接收的位置
			if (mapPoi == null || mv_baidu == null) {
				return false;
			}

			LatLng ll = mapPoi.getPosition();
			setCurrentMapLatLng(new LatLng(ll.latitude, ll.longitude));
			return false;
		}
		
		@Override
		public void onMapClick(LatLng latlng) {
			// map view 销毁后不在处理新接收的位置
			if (latlng == null || mv_baidu == null) {
				return;
			}
	
			setCurrentMapLatLng(new LatLng(latlng.latitude, latlng.longitude));
		}
	};
	
	private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
		
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
			tv_location.setText(reverseGeoCodeResult.getAddress());
		}
		
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
			tv_location.setText(geoCodeResult.getAddress());
		}
	};
	
}
