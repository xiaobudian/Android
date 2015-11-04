package com.example.joinusad.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2015/11/4.
 */
public class LocationHelper {

    public static String getLocationProvider(LocationManager locationManager) {
        String locationProvider = null;
        List<String> providers = locationManager.getAllProviders();
        //获取所有可用的位置提供器
        if(providers.contains(LocationManager.GPS_PROVIDER)
                &&locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)
                &&locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        return locationProvider;

    }
}
