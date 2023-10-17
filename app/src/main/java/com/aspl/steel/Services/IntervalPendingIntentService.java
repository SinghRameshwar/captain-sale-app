package com.aspl.steel.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.MainActivity;
import com.aspl.steel.MyNotificationPublisher;
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/**
 *  Created by arnab on 5/5/16.
 */
public class IntervalPendingIntentService extends IntentService {
    public IntervalPendingIntentService() {
        super("IntervalPendingIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        String cid = sharedPref.getString("cid", "");
        if (cid.length() > 0) {
            LocationManager manager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = manager.getLastKnownLocation(manager.GPS_PROVIDER);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent logout_intent = new Intent(IntervalPendingIntentService.this, LogoutService.class);
                startService(logout_intent);
                stopSelf();
                Toast.makeText(this, "GPS is disabled. You will be log out", Toast.LENGTH_SHORT).show();
                return;

            } else if (location != null) {
                GlobalConfiguration.locationSendingDecision = !GlobalConfiguration.locationSendingDecision;
                if (!GlobalConfiguration.locationSendingDecision) {
                    return;
                }
                //Our location based code
                Log.e("Log", "Lat- " + location.getLatitude() + " and long- " + location.getLongitude());
                if (GlobalConfiguration.lastLocationLatitude == null || GlobalConfiguration.lastLocationLongitude == null) {
                    GlobalConfiguration.lastLocationLatitude = location.getLatitude();
                    GlobalConfiguration.lastLocationLongitude = location.getLongitude();
                }
                if (Math.abs(GlobalConfiguration.lastLocationLatitude - location.getLatitude()) > 0.001 && Math.abs(GlobalConfiguration.lastLocationLongitude - location.getLongitude()) > 0.001) {
                    Log.e("Case 1", GlobalConfiguration.lastLocationLatitude - location.getLatitude() + "");
                    sendTrackResult(location.getLatitude(), location.getLongitude(),location);
                } else {
                    Log.e("Case 2", "Nearly ideantical" + GlobalConfiguration.lastLocationLatitude + " and long " + GlobalConfiguration.lastLocationLongitude);
                    location.setLatitude(GlobalConfiguration.lastLocationLatitude);
                    location.setLongitude(GlobalConfiguration.lastLocationLongitude);
                    sendTrackResult(GlobalConfiguration.lastLocationLatitude, GlobalConfiguration.lastLocationLongitude,location);
                }
                GlobalConfiguration.lastLocationLatitude = location.getLatitude();
                GlobalConfiguration.lastLocationLongitude = location.getLongitude();
            } else {
                Log.e("Location Null:- ","Heloooooooo");
               // Toast.makeText(this, "Your Location not work", Toast.LENGTH_SHORT).show();
                int locationError =0;
                try {
                    locationError = sharedPref.getInt("locationError", 0);
                }catch (Exception e){}
                SharedPreferences.Editor editor = sharedPref.edit();
                if (locationError<=3) {
                    locationError +=1;
                    editor.putInt("locationError", locationError);
                    if (locationError==0) {
                        editor.putString("locationErrorTime", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                    }
                    editor.apply();
                }else {
                    editor.putInt("locationError", 0);
                    editor.apply();
                    scheduleNotification(getNotification("Your Location not work till "+sharedPref.getString("locationErrorTime", "")), 30000);
                }
                return;
            }
        } else {
            Intent logout_intent = new Intent(IntervalPendingIntentService.this, LogoutService.class);
            startService(logout_intent);
            Log.e("Logout Manual Error :", "Error");
            //stopService(new Intent(this, IntervalPendingIntentService.class));
        }
    }

    //API SERVICE CALL For Location seved on Server
    public String sendTrackResult(Double latitude, Double longitude,Location location) {

        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        HashMap<String, String> postDataParams = new HashMap<>();

        String cid = sharedPref.getString("cid", "");
        String segid = sharedPref.getString("segid", "");
        String keyCode = sharedPref.getString("storedKeyCode", "");
        String userType = sharedPref.getString("storedUserType", "");
        if (cid.length() == 0 || segid.length() == 0 || !(userType.equalsIgnoreCase("Salesman"))) {
            Log.e("Log", userType + " cid " + cid);
            return "";
        }
        // distance check if  0 then return
        String returnsStr = "";
        postDataParams = oflineLocationStore(latitude, longitude,location);
//        if (Double.parseDouble(postDataParams.get("distance"))<=0){
//            return "";
//        }

        if (!isNetworkAvailable(this)) {
            JSONArray offlineArr = new JSONArray();
            if (sharedPref.getString("offlineData", "").length() > 0) {
                try {
                    offlineArr = new JSONArray(sharedPref.getString("offlineData", ""));
                    JSONObject offlineObj = new JSONObject(postDataParams);
                    offlineArr.put(offlineObj);
                    SharedPreferences.Editor offlineEditer = sharedPref.edit();
                    offlineEditer.putString("offlineData", offlineArr + "");
                    offlineEditer.commit();

                } catch (JSONException e) {
                    Log.e("OfflineDataError :", e.toString());
                }
            } else {
                JSONObject offlineObj = new JSONObject(postDataParams);
                offlineArr.put(offlineObj);
                SharedPreferences.Editor offlineEditer = sharedPref.edit();
                offlineEditer.putString("offlineData", offlineArr + "");
                offlineEditer.commit();
            }
        } else {
            // Log.e("isNetworkAvailable", "true");
            String link = "";
            String dnsport = GlobalConfiguration.getDomainport();
            if (sharedPref.getString("offlineData", "").length() > 0) {
                link = "http://" + dnsport + "/SteelSales-war/Stl_Track_SalesmanArr_Loc_Api";
                try {
                    oflineLocationStore(latitude, longitude,location);
                    JSONArray offlineArr = new JSONArray(sharedPref.getString("offlineData", ""));
                    JSONObject offlineObj = new JSONObject(postDataParams);
                    offlineArr.put(offlineObj);
                    postDataParams.put("dataArr", offlineArr + "");
                } catch (JSONException e) {
                    // e.printStackTrace();
                    Log.e("OnLineDataError1 :", e.toString());
                }
            } else {
                link = "http://" + dnsport + "/SteelSales-war/Stl_Track_Salesman_Loc_Api";
            }
            postDataParams.put("cid", cid);
            postDataParams.put("segid", segid);
            postDataParams.put("uid", sharedPref.getString("storedUserId", ""));
            postDataParams.put("keyCode", keyCode);
            postDataParams.put("accuracy", location.getAccuracy()+"");

            returnsStr = SteelHelper.performPostCall(link, postDataParams);
            if (returnsStr.startsWith("Exception:")) {
                try {
                    Thread.sleep(1000 * 20 * 1);     //Sleep for 5 seconds and then call again for failed connection
                    returnsStr = SteelHelper.performPostCall(link, postDataParams);
                } catch (Exception ignored) {
                }
            }
            try {
                JSONObject jsonObject = new JSONObject(returnsStr);
                if (jsonObject.getString("type").equals("success")) {
                    SharedPreferences.Editor offlineEditer = sharedPref.edit();
                    offlineEditer.remove("offlineData");
                    offlineEditer.commit();
                   /* JSONObject objVal=jsonObject.getJSONObject("salesmanTrackRec");
                    writeFileOnInternalStorage(objVal.getString("location")+"\n"+objVal.getString("trackTime")+",   "+objVal.getString("time")+",   "+objVal.getString("distance")+"\n\n");
*/
                }
            } catch (JSONException e) {
                Log.e("Returns Data Error: ", e.toString());
            }
        }
        //Toast.makeText(this,returnsStr,Toast.LENGTH_SHORT).show();
        Log.e("Returns", returnsStr);
        return returnsStr;
    }

   //Not Inter connection Location Store Locally sync on net connection come Back
    HashMap oflineLocationStore(Double latitude, Double longitude,Location location) {
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        final HashMap<String, String> postDataParams = new HashMap<>();
        double distance_km = 0.0;
        if (sharedPref.getString("latitude", "").equals("0.0") && sharedPref.getString("longitude", "").equals("0.0")) {
            distance_km = 0.0;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("latitude", latitude + "");
            editor.putString("longitude", longitude + "");
            editor.commit();
        } else {
            double old_latitude = Double.parseDouble(sharedPref.getString("latitude", ""));
            double old_longitude = Double.parseDouble(sharedPref.getString("longitude", ""));
            distance_km = distance(old_latitude, old_longitude, latitude, longitude);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("latitude", latitude + "");
            editor.putString("longitude", longitude + "");
            editor.commit();
        }
        postDataParams.put("sLid", sharedPref.getString("storedSlid", ""));
        postDataParams.put("longitude", longitude + "");
        postDataParams.put("latitude", latitude + "");
        postDataParams.put("timeOfUpdate", new Date().getTime() + "");
        postDataParams.put("distance", distance_km + "");
        postDataParams.put("accuracy", location.getAccuracy()+"");

        String locationAddress = "";
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                String addressLine0 = addressList.get(0).getAddressLine(0);
                String addressLine1 = addressList.get(0).getAddressLine(1);
                String addressLine2 = addressList.get(0).getAddressLine(2);
                if (addressLine0 != null)
                    locationAddress += addressLine0;
                if (addressLine1 != null)
                    locationAddress += " " + addressLine1;
                if (addressLine2 != null)
                    locationAddress += " " + addressLine2;

            } catch (Exception ignored) {
            }
            //Log.e("Location",locationAddress);
        }
        postDataParams.put("location", locationAddress + "");
        return postDataParams;
    }

    // Network connection check
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Here getting distance in kilometers (km)
    private double distance(double old_latitude, double old_longitude, double latitude, double longitude) {
        Location locationA = new Location("point A");
        locationA.setLatitude(old_latitude);
        locationA.setLongitude(old_longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latitude);
        locationB.setLongitude(longitude);

        return (locationA.distanceTo(locationB));
    }

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    // schedule Notification
    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        int hour = 21;
        int minute = 00;
        String myTime = String.valueOf(hour) + ":" + String.valueOf(minute);
        Date date = null;
        // today at your defined time Calendar
        Calendar customCalendar = new GregorianCalendar();
        // set hours and minutes
        customCalendar.set(Calendar.HOUR_OF_DAY, hour);
        customCalendar.set(Calendar.MINUTE, minute);
        customCalendar.set(Calendar.SECOND, 0);
        customCalendar.set(Calendar.MILLISECOND, 0);
        Date customDate = customCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            date = sdf.parse(myTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeInMs = 0;
        if (date != null) {
            timeInMs = customDate.getTime();
        }
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        if (!sharedPref.getString("storedKeyCode", "").equals("")) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMs, pendingIntent);
        }
    }

    //For Natification When Location Not Work
    private Notification getNotification(String content) {
        Intent intentresult = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 1, new Intent[]{intentresult}, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle("BMA Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.captain_tmt);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}