package com.aspl.steel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.aspl.steel.CrashReport.TopExceptionHandler;

import org.json.JSONObject;

import java.util.HashMap;

/**
 *  Created by Arnab Kar on 26/2/16.
 */
public class ChangePasswordActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    final String LOG_DBG=this.getClass().getSimpleName();
    TextView mCurrentPassword,mNewPassword,mNewPasswordConfirm;
    private UserPwdChangeTask mAuthTask = null;

    public void onPasswordChangeClick(View view){
        if(mAuthTask!=null){
            return;
        }
        mCurrentPassword=(TextView)findViewById(R.id.currentPassword);
        mNewPassword=(TextView)findViewById(R.id.newPassword);
        mNewPasswordConfirm=(TextView)findViewById(R.id.newPasswordConfirm);
        mCurrentPassword.setError(null);
        mNewPassword.setError(null);
        mNewPasswordConfirm.setError(null);

        if(mCurrentPassword.getText().toString().equals("")){
            mCurrentPassword.setError("Please enter current password");
            return;
        }
        if(mNewPassword.getText().toString().equals("")){
            mNewPassword.setError("Please enter new password");
            return;
        }
        if(!mNewPassword.getText().toString().equals(mNewPasswordConfirm.getText().toString())){
            mNewPasswordConfirm.setError("Password doesn't match");
            return;
        }

        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        String tempKeyCode=sharedPref.getString("storedKeyCode", "0");

        if(SteelHelper.isNetworkAvailable(this)){
            mAuthTask=new UserPwdChangeTask(this,1);
            mAuthTask.execute(tempKeyCode,mCurrentPassword.getText().toString(),mNewPassword.getText().toString());
        }
        else {
            Toast.makeText(this, R.string.no_internet_connectivity, Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar= (Toolbar)findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.userName)).setText("Welcome "+sharedPref.getString("storedName",""));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class UserPwdChangeTask extends AsyncTask<String, Void, String> {

        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        UserPwdChangeTask(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
            progressDialog.setIndeterminate(true);
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
                    String tempKeyCode = arg0[0];
                    String currentPassword = arg0[1];
                    String newPassword = arg0[2];
                    currentPassword=SteelHelper.sha1(currentPassword);
                    newPassword= SteelHelper.sha1(newPassword);
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_ChangePassword_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("currPassword", currentPassword);
                    hm.put("newPassword", newPassword);
                    hm.put("keyCode", tempKeyCode);
                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(LOG_DBG, result);
            JSONObject resultJson;
            String successFlag,reason;
            mAuthTask = null;

            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            try{
                resultJson=new JSONObject(result);
                successFlag=resultJson.getString("type");
                reason=resultJson.getString("msg");
            }catch (Exception e){
                resultJson=new JSONObject();
                successFlag="";
                reason="Unknown error";
            }
            //Successful
            if(result.contains("Exception:")||resultJson.toString().equals("{}")){
                Log.e(LOG_DBG, result);
                Toast.makeText(context, getResources().getString(R.string.UnableToConnectServer), Toast.LENGTH_LONG).show();
            }
            else if(successFlag.equals("error")){
                Toast.makeText(context,reason,Toast.LENGTH_LONG).show();
                mCurrentPassword.setError(reason);
                mNewPassword.requestFocus();
                if(reason.contains("Authorization")){
                   mCurrentPassword.setError("Password Incorrect");
                }
            }
            else if(successFlag.equals("success")){       //Valid Username or Password..!
                Toast.makeText(context,"Password changed succesfully",Toast.LENGTH_LONG).show();
                mCurrentPassword.setText("");
                mNewPassword.setText("");
                mNewPasswordConfirm.setText("");
            }
            else{
                Log.e(LOG_DBG, result);
                Snackbar.make(findViewById(R.id.container), getResources().getString(R.string.UnableToConnectServer), Snackbar.LENGTH_LONG)
                        .show();
            }

        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}
