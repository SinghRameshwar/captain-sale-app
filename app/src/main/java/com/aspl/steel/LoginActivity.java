package com.aspl.steel;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.CrashReport.TopExceptionHandler;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private final String LOG_DBG = this.getClass().getSimpleName();
    final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE=2;
    final int BACKGROUND_LOCATION_CODE=3;
    ProgressDialog progressDialog;
    UserLoginTask mAuthTask;
    Dialog myDialog;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    //@Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.bg_location_inc) LinearLayout bg_location_inc;

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void backgroundLocationPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app needs the permission to track \"Precise Location\". This is needed for the Sales Person's location tracking while visiting the market and customers. Please click on \"Yes\" to go to the Permissions Page and Permit \"Always Allow\" location Info for Captain Steel Sales Force in the Privacy section.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        if (ActivityCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessagePowerMode() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You must turn OFF the battery optimisation to use this app. Please click on YES to go to Optimisation setting and turn it off")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       // startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:"+getPackageName())));
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), 0);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDialog = new Dialog(this);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        if(!sharedPref.getString("storedKeyCode","").equals("")){

            if (!sharedPref.getString("cid", "").equals("") && !sharedPref.getString("segid", "").equals("") && !sharedPref.getString("storedMenu","").equals("")) {
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    onLoginFailed();
                    return;
                }

                boolean GPSFeatureEnabled=GlobalConfiguration.GPSFeatureEnabled;
                int ACCESS_COARSE_PERMISSION = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                int ACCESS_FINE_PERMISSION = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

                if(ACCESS_COARSE_PERMISSION== PackageManager.PERMISSION_GRANTED && ACCESS_FINE_PERMISSION==PackageManager.PERMISSION_GRANTED){
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        String packageName = getPackageName();
                        if (GPSFeatureEnabled && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Toast.makeText(LoginActivity.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                            buildAlertMessageNoGps();

                        }else if (Build.VERSION.SDK_INT > 22){//Build.VERSION_CODES.M) {
                            PowerManager pm = (PowerManager) getSystemService(PowerManager.class);//POWER_SERVICE);
                            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                buildAlertMessagePowerMode();
                            } else {
                                login();
                            }
                        }else {
                            login();
                        }
                }else {
                    // New Add For background Loction Get in Android > 11
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        bg_location_inc.setVisibility(View.VISIBLE);
                        TextView btn_deny = bg_location_inc.findViewById(R.id.btn_deny);
                        btn_deny.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bg_location_inc.setVisibility(View.GONE);
                            }
                        });

                        // Accept Location Button
                        TextView btn_accept = bg_location_inc.findViewById(R.id.btn_accept);
                        btn_accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(LoginActivity.this,new String[]{
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                               // Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                Manifest.permission.ACCESS_FINE_LOCATION},
                                       BACKGROUND_LOCATION_CODE);
                            }
                        });
                    }else{
                        ActivityCompat.requestPermissions(LoginActivity.this,new String[]{
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                   }
                }
            }
        });

    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(SteelHelper.isNetworkAvailable(this)){
            if(mAuthTask!=null){
                onLoginFailed();
                return;
            }
            mAuthTask = new UserLoginTask(this, 1);
            mAuthTask.execute(email, password);
        } else {
            Toast.makeText(this , R.string.no_internet_connectivity, Toast.LENGTH_LONG).show();
            onLoginFailed();
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==2){
            boolean permission=true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    permission = false;
                    break;
                }
            }
            if(permission){
                login();
            }else {
                Toast.makeText(this,"Cannot start with insufficient permissions",Toast.LENGTH_SHORT).show();
            }

            // New Add For background Loction Get in Android > 11
        } else if (requestCode==3) {
            bg_location_inc.setVisibility(View.GONE);
            boolean permission=true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    permission = false;
                    break;
                }
            }
            if(permission){
                locationSettingPopup();
            }else {
                Toast.makeText(this,"Cannot start with insufficient permissions",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void onLoginSuccess(String result) {
        _loginButton.setEnabled(true);
        Intent intent=new Intent(this,CompanySelectionActivity.class);
        intent.putExtra("loginJson",result);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        // Toast.makeText(getBaseContext(), "Login unsuccessful", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("Enter a username");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Enter a password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        UserLoginTask(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.Authenticating));
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... arg0 ) {
            if(byGetOrPost == 0){ //means by Get Method
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " -> Get disabled for security reasons");
            }
            else{
                try{
                    String username = arg0[0];
                    String password = arg0[1];
                    password=SteelHelper.sha1(password);
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_Login_Api_v2";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("userName",username);
                    hm.put("password", password);
                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject resultJson;
            String successFlag;
            mAuthTask = null;
            //Log.e("............",result);

            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            try{
                 //Log.e("MainActivity",result);
                resultJson=new JSONObject(result);
                successFlag=resultJson.getString("type");
            }catch (Exception e){
                successFlag="";
            }
            //Successful
            if(result.contains("Exception:")||result.equals("{}")){
                Log.e("MainActivity",result);
                Toast.makeText(context,getResources().getString(R.string.UnableToConnectServer), Toast.LENGTH_LONG).show();
                onLoginFailed();
            }else if(successFlag.equals("error")){
                Toast.makeText(context,"Wrong Username / Password ",Toast.LENGTH_LONG).show();
                _passwordText.setError(getString(R.string.error_incorrect_password));
                _passwordText.requestFocus();
                onLoginFailed();
            }else if(successFlag.equals("success")){
                String tempKeyCode,uid,email,uname,sLid,userType,attendanceId,locationDiff,isAsm,salesman_name,mobile,pre_order_url,sap_auth_user,sap_auth_algo,sap_auth_pwd;
                try{
                    JSONObject mainObj=new JSONObject(result);
                    tempKeyCode=mainObj.getString("keyCode");
                    attendanceId=mainObj.getString("attendanceId");
                    uid=mainObj.getString("uid");
                    email=mainObj.getString("email");
                    uname=mainObj.getString("uname");
                    sLid=mainObj.getString("sLid");
                    isAsm=mainObj.getString("isAsm");
                    salesman_name=mainObj.getString("salesman_name");
                    userType=mainObj.getString("userType");
                    mobile=mainObj.getString("mobile");
                    pre_order_url=mainObj.getString("pre_order_url");
                    sap_auth_user=mainObj.getString("sap_auth_user");
                    sap_auth_algo=mainObj.getString("sap_auth_algo");
                    sap_auth_pwd=mainObj.getString("sap_auth_pwd");
                    try{
                        locationDiff=mainObj.getString("locationDiff");
                    }catch (Exception e){
                        locationDiff="500";
                    }
                }catch (Exception e){
                    tempKeyCode="0";
                    attendanceId="";
                    uid="0";
                    email="";
                    uname="";
                    sLid="0";
                    isAsm="0";
                    salesman_name="";
                    userType="Salesman";
                    locationDiff="500";
                    mobile ="";
                    sap_auth_pwd = "";
                    pre_order_url = "";
                    sap_auth_user = "";
                    sap_auth_algo = "";
                }
                //Log.e("TempKeyCode", tempKeyCode);
                SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("storedKeyCode", tempKeyCode);
                editor.putString("storedAttendanceId", attendanceId);
                editor.putString("storedUserId", uid);
                editor.putString("storedSlid", sLid);
                editor.putString("storedisAsm", isAsm);
                editor.putString("salesman_name", salesman_name);
                editor.putString("storedEmailId",email);
                editor.putString("storedName",uname);
                editor.putString("storedmobile",mobile);
                editor.putString("storedUserType",userType);
                editor.putString("latitude","0.0");
                editor.putString("longitude","0.0");
                editor.putString("locationDiff",locationDiff);
                editor.putString("sap_auth_algo",sap_auth_algo);
                editor.putString("sap_auth_user",sap_auth_user);
                editor.putString("pre_order_url",pre_order_url);
                editor.putString("sap_auth_pwd",sap_auth_pwd);

                Log.e("Usertype",userType);
                editor.apply();
                onLoginSuccess(result);
            }else{
                Log.e(LOG_DBG, result);
                Snackbar.make(findViewById(R.id.container), getResources().getString(R.string.UnableToConnectServer), Snackbar.LENGTH_LONG).show();
                onLoginFailed();
            }

        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();
            onLoginFailed();
        }
    }


    void locationSettingPopup(){
            myDialog.setContentView(R.layout.update_location_setting_popup);
            myDialog.setCancelable(false);
            TextView btn_nothanks=(TextView)myDialog.findViewById(R.id.btn_nothanks);
            TextView btn_updateSettings=(TextView)myDialog.findViewById(R.id.btn_updateSettings);
            TextView text_message = (TextView)myDialog.findViewById(R.id.text_message);

            SpannableString str = new SpannableString("Allow us to access your location All the time so we can provide personal recommendations");
            str.setSpan(new BackgroundColorSpan(Color.YELLOW), 33, 45, 0);
            text_message.setText(str);

            btn_nothanks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            btn_updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (ActivityCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }
            }});

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
    }
}