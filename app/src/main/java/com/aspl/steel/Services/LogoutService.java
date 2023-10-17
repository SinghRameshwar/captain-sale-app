package com.aspl.steel.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.MainActivity;
import com.aspl.steel.MyNotificationPublisher;
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 *  Created by Arnab Kar on 05 August 2016.
 */
public class LogoutService extends IntentService {
    public LogoutService() {
        super("LogoutService");
    }
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    @Override
    protected void onHandleIntent(Intent intent) {
        if(isNetworkAvailable(this)) {
            SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
            String tempKeyCode = sharedPref.getString("storedKeyCode", "");
            String attendanceId = sharedPref.getString("storedAttendanceId", "");
            stopService(new Intent(this, IntervalPendingIntentService.class));
            String dnsport = GlobalConfiguration.getDomainport();
            String link = "http://" + dnsport + "/SteelSales-war/Stl_Logout_Api";
            if (tempKeyCode.equals("") || attendanceId.equals("")) {
                return;
            }
            HashMap<String, String> hm = new HashMap<>();
            hm.put("keyCode", tempKeyCode);
            hm.put("attendanceId", attendanceId);
            String result = SteelHelper.performPostCall(link, hm);
            try {
                //Log.e("..111...." + getClass().getSimpleName(), result);
                JSONObject resObj= new JSONObject(result);
                if (resObj.getString("type").equalsIgnoreCase("success")) {

                    //Toast.makeText(this,resObj.getString("msg"),Toast.LENGTH_LONG).show();
                   // scheduleNotification(getNotification(resObj.getString("msg")));
                    stopService(new Intent(this, IntervalPendingIntentService.class));
                    sharedPref.edit().clear().apply();
                    stopBtService();

                    Intent intent1 = new Intent(this, LoginActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }else if(resObj.getString("type").equalsIgnoreCase("autherror")){

                    scheduleNotification(getNotification(resObj.getString("msg")));
                    stopService(new Intent(this, IntervalPendingIntentService.class));
                    sharedPref.edit().clear().apply();
                    stopBtService();

                    Intent intent1 = new Intent(this, LoginActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }else{
                    scheduleNotification(getNotification(result));
                }
            }catch (Exception e){
                Log.e("Logout Error: ",e.toString());
                scheduleNotification(getNotification(e.toString()));
            }
        }else{
            // Message For check NetConnection
            scheduleNotification(getNotification("Please Check your Internet connection !"));

        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    private void setNotificationData(String message) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher);

        notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .setContentTitle("BMA App")
                    .setContentText(message);
            sendNotification();

    }

    private void sendNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        notificationBuilder.setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        int notificationId = 1;
        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;
        notificationManager.notify(notificationId, notification);
    }

    private void scheduleNotification (Notification notification) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_MUTABLE ) ;

        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        if(!sharedPref.getString("storedKeyCode","").equals("")) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, 00, pendingIntent);
        }
    }
    private Notification getNotification (String content) {

        Intent intentresult = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivities(this,1, new Intent[]{intentresult},PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id );
        builder.setContentTitle( "BMA Notification" );
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.captain_tmt);
        builder.setAutoCancel(true) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID );
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }


    private void stopBtService() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent wakeIntent = PendingIntent.getService(this, 0, new Intent(this, MyNotificationPublisher.class), PendingIntent.FLAG_MUTABLE);
        wakeIntent.cancel();
        alarmManager.cancel(wakeIntent);
        Log.e(".......", "stopBtService cancel Notifications wakeIntent");
    }
}
