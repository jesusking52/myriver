package com.riverauction.riverauction.feature.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kyunghee on 2016. 7. 28..
 *
 * Transform String Address to GPS Location (Lat, Lon)
 */
public class GeocodeUtils {

    // Use Google Geocoder
    private Geocoder gc;

    public GeocodeUtils(Context context){
        gc = new Geocoder(context, Locale.KOREAN);
    }

    public Address getGPSfromAddress(String strAddress){

        List<Address> addressList = null;

        try{

            if(!gc.isPresent()){
                return null;
            }

            addressList = gc.getFromLocationName(strAddress, 1);

            if((addressList != null) && (addressList.size() > 0)){
                return addressList.get(0);
            }
        }
        catch(IOException ex){
            Log.e("GeocodeUtils", ex.getMessage(), ex);
        }

        return null;
    }
}
