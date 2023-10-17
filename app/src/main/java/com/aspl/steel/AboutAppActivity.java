package com.aspl.steel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.aspl.steel.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aspl.steel.CrashReport.CrashReportSender;
import com.aspl.steel.CrashReport.TopExceptionHandler;
import java.util.Calendar;

/**
 *  Created by Arnab Kar on 21 July 2016.
 */
public class AboutAppActivity extends AppCompatActivity {

    LinearLayout privacy_policy_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_about_app);
        Toolbar toolbar= (Toolbar)findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /*SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.userName)).setText("Welcome "+sharedPref.getString("storedName",""));*/
        privacy_policy_lay=(LinearLayout)findViewById(R.id.privacy_policy_lay);
        privacy_policy_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://captainmarketing.in/SteelSales-war/PrivacyPolicy.jsp");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        String versionName = BuildConfig.VERSION_NAME;
        TextView appversion=(TextView)findViewById(R.id.appversion);
        appversion.setText("Version: "+versionName);
        TextView appyear=(TextView)findViewById(R.id.appyear);
        appyear.setText("©"+Calendar.getInstance().get(Calendar.YEAR)+"BMA Steel.");



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
    public void onUpdateClick(View view){
        String dnsport=GlobalConfiguration.getDomainport();
        String url="http://"+dnsport+"/SteelSales-war/Steel%20Sales%20Online%20Deliverable%202.apk";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    public void onCrashReportClick(View view){
        new CrashReportSender(this).sendReport("");
    }
    public void onPrivacyPolicyClick(View view){
        /*String url = "https://realbooks.in/privace-policy.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/
    }
}
