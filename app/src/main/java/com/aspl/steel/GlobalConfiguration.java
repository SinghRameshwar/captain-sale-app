package com.aspl.steel;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 *  Created by Arnab Kar on 28/4/16.
 */
public class GlobalConfiguration {
    private static final boolean development_Version = false;         //Either true or false
    public static final boolean GPSFeatureEnabled=true;
    public static GoogleApiClient mGoogleApiClient;
    public static String getDomainport() {
        if(!development_Version) {
            return "captainmarketing.in";
        }else{
            return "ec2-3-7-170-219.ap-south-1.compute.amazonaws.com:90";
        }
    }
    //Data transfer based global codes
    public static Double lastLocationLatitude=null,lastLocationLongitude=null;
    public static Boolean locationSendingDecision=false;
    public static ArrayList<String> mOnlineMedia=new ArrayList<>();
}
