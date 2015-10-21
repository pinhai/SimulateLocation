package com.hai.simulatelocation.location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import android.content.Context;

/**
 * 通过百度地图API定位
 * @author Administrator
 *
 */
public class LocationByBaiduMapAPI {

	private LocationClient mLocationClient;
	/**
	 * <p>Hight_Accuracy:高精度定位模式下，会同时使用GPS、Wifi和基站定位，返回的是当前条件下精度最好的定位结果</string></p>
     * <p>Battery_Saving:低功耗定位模式下，仅使用网络定位即Wifi和基站定位，返回的是当前条件下精度最好的网络定位结果</string></p>
     * <p>Device_Sensors:仅用设备定位模式下，只使用用户的GPS进行定位。这个模式下，由于GPS芯片锁定需要时间，首次定位速度会需要一定的时间</string></p>
	 */
	private LocationMode mTempMode = LocationMode.Hight_Accuracy;
    private String mTempcoor="gcj02";
    
    public static final String CoorType_GCJ02 = "gcj02";	//国家测绘局标准
    public static final String CoorType_BD0911 = "bd09ll";	//百度经纬度标准
    public static final String CoorType_BD09 = "bd09";	//百度墨卡托标准
	
	public LocationByBaiduMapAPI(Context context, BDLocationListener locationListener){
		this(context, LocationMode.Hight_Accuracy, CoorType_GCJ02, locationListener);
	}
	
	public LocationByBaiduMapAPI(Context context, LocationMode tempMode, String tempcoor, BDLocationListener locationListener){
		mTempMode = tempMode;
		mTempcoor = tempcoor;
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(locationListener);
		initLocation();
	}
	
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(mTempMode);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType(mTempcoor);//可选，默认gcj02，设置返回的定位结果坐标系，
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }
	
	public void startLocation(){
		mLocationClient.start();
	}
	
	public void stopLocation(){
		mLocationClient.stop();
	}
	
}
