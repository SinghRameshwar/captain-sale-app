package com.aspl.steel;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.aspl.steel.AttendanceRptGrp.AttendanceRptList;
import com.aspl.steel.BDEDailyGroup.BDEDailyEntryView;
import com.aspl.steel.DealerVisitPRtp.DealerVisitPRptList;
import com.aspl.steel.EngineerVisitG.EngineerVisitRtp;
import com.aspl.steel.LocationTrkReportGrp.LocationTrckRptList;
import com.aspl.steel.MasionMeetStatus.MasionMeetStatusView;
import com.aspl.steel.MasonMeetComplation.MasionMeetCompletionEntry;
import com.aspl.steel.MasonMeetInitateGp.MasionMeetInitiate;
import com.aspl.steel.MyLeadG.MyLeadList;
import com.aspl.steel.NewDealerVisit.NewDealerVisitView;
import com.aspl.steel.NewLeadGp.NewLeadEntry;
import com.aspl.steel.NewLeadGp.NewLeadStatus;
import com.aspl.steel.PreOrderNewG.NewPreOrderEntry;
import com.aspl.steel.PreOrderNewG.NewPreOrderEntry_SAP;
import com.aspl.steel.Pre_OrderG.PreOrderEntryView;
import com.aspl.steel.Pre_OrderG.PreOrderEntryView_SAP;
import com.aspl.steel.Pre_OrderG.PreOrderListView;
import com.aspl.steel.QrScannerGroup.QrScaneToDetailsView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.Services.IntervalPendingIntentService;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.fragments.AboutPhone;
import com.aspl.steel.salesmanvisit.salesmanvisit_view;
import com.aspl.steel.fragments.MonthlySalesReportFragment;
import com.aspl.steel.fragments.DailySalesConfirmListingFragment;
import com.aspl.steel.fragments.DashboardFragment;
import com.aspl.steel.fragments.DailySaleConfirmationFragment;
import com.aspl.steel.fragments.RequisitionListingFragment;
import com.aspl.steel.fragments.ShareCurrentLoc;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.aspl.steel.GlobalConfiguration.mGoogleApiClient;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    final String LOG_DBG = getClass().getSimpleName();
    boolean doubleBackToExitPressedOnce;
    DrawerLayout mDrawerLayout;
    private String mReturningWithResult, return_name, return_id;
    private int REQUEST_DAILYSALE_ENTRY = 21, REQUEST_SALESMAN_AUTOCOMPLETE = 41, REQUEST_DEALER_AUTOCOMPLETE = 22, REQUEST_REQUISITION_ENTRY = 23;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = true;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5*60*1000;
    private FusedLocationProviderClient mFusedLocationClient;

    public void addItemtoList(View view) {
        if (findViewById(R.id.search_salesman).getTag() == null || findViewById(R.id.search_salesman).getTag().equals("0")) {
            Toast.makeText(this, "Only salesman data can be saved", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject dailySalesMainTemplate = ((Controller) getApplication()).dailySalesMainTemplate;
        JSONObject templateToEdit;
        if (dailySalesMainTemplate == null) {
            Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            templateToEdit = dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj");
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, DailySaleEntryActivity.class);
        intent.putExtra("EditFlag", false);
        intent.putExtra("templateJsonString", templateToEdit.toString());
        if (findViewById(R.id.search_salesman).getVisibility() == View.VISIBLE) {
            intent.putExtra("sLid", findViewById(R.id.search_salesman).getTag().toString());
        }
        startActivityForResult(intent, REQUEST_DAILYSALE_ENTRY);
    }

    public void narrationClick(View view) {
        final TextView narrationField = ((TextView) findViewById(R.id.txtNarration));
        final EditText txtNarration = new EditText(this);
        txtNarration.setText(narrationField.getText());
        new AlertDialog.Builder(this)
                .setTitle("Narration")
                .setView(txtNarration)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String data = txtNarration.getText().toString();
                        narrationField.setText(data);
                        findViewById(R.id.save).performClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /************   Location Tongale On Off Didection    ***************/
        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        String formattedDate = df.format(c);
        scheduleNotification(getNotification("You have not logged out in BMA app " + formattedDate), 30000);
        //"You have not logged out in BMA app (DD-MMM)"..where DD is the Day of the month and MMM is Month short form like today will be 24-May

        /**************** application Global Configuration ************/
        if (GlobalConfiguration.GPSFeatureEnabled) {
            createLocationRequest();
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
        }

        Fragment DashboardFragment = new DashboardFragment();
        SteelHelper.replaceFragment(this, DashboardFragment, false);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        {
            View header = navigationView.getHeaderView(0);
            SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
            TextView username = (TextView) header.findViewById(R.id.userName);
            TextView subtitle = (TextView) header.findViewById(R.id.subtitle);
            username.setText(sharedPref.getString("salesman_name", ""));
            subtitle.setText(sharedPref.getString("storedmobile", ""));

            // Dynamic menu code starts here
            Menu menu = navigationView.getMenu();
            menu.clear();
            menu.setGroupCheckable(1, false, true);

            String strMenuObj = sharedPref.getString("storedMenu", "");
            String storedisAsm = sharedPref.getString("storedisAsm", "");

            try {
                JSONArray menuArr = new JSONArray(strMenuObj);
                boolean AboutUsDisplayed = false;
                for (int i = 0; i < menuArr.length(); i++) {
                    JSONObject menuObj = menuArr.getJSONObject(i);
                    if (menuObj.getString("label").equals("Master")) {
                        //do nothing for master in app
                    } else if (menuObj.getString("appKey").equals("97")) {
                        //This is a single item
                        menu.add(0, 97, Menu.NONE, menuObj.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_home_24dp));

                    } else if (menuObj.getString("appKey").equals("95")) {

                        SubMenu subMenu = menu.addSubMenu(menuObj.getString("label"));
                        JSONArray submenuItems = menuObj.getJSONArray("group");
                        for (int j = 0; j < submenuItems.length(); j++) {
                            JSONObject submenuItem = submenuItems.getJSONObject(j);

                            //Dealer point sales confirmation
                            if (submenuItem.getInt("appKey") == 1) {
                                subMenu.add(1, 1, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sales_entry_24dp));
                            }
                            //"Requisition Entry"
                            else if (submenuItem.getInt("appKey") == 4) {
                                subMenu.add(1, 4, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_steel_sale_edit_24dp));
                            }
                            //"Dealer Visit "
                            else if (submenuItem.getInt("appKey") == 5) {
                                subMenu.add(1, 103, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));
                            }
                            //"New Dealer Visit "
                            else if (submenuItem.getInt("appKey") == 6) {
                                subMenu.add(1, 104, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                //Engineer Visit
                            } else if (submenuItem.getInt("appKey") == 7) {
                                subMenu.add(1, 107, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // "Mason Meet Inititate "
                            } else if (submenuItem.getInt("appKey") == 8) {
                                subMenu.add(1, 108, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                //"Mason Meet Completion "
                            } else if (submenuItem.getInt("appKey") == 9) {
                                subMenu.add(1, 109, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // "Pre order"
                            } else if (submenuItem.getInt("appKey") == 10) {
                                subMenu.add(1, 106, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // New Lead Create
                            } else if (submenuItem.getInt("appKey") == 11) {
                                subMenu.add(1, 111, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // My Lead
                            } else if (submenuItem.getInt("appKey") == 12) {
                                subMenu.add(1, 113, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                            } else if (submenuItem.getInt("appKey") == 13) {
                                // QR Scanner
                                subMenu.add(1, 114, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                //BDE Daily Activity Report
                            } else if (submenuItem.getInt("appKey") == 14) {
                                subMenu.add(1, 115, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // Pre Order New development
                            } else if (submenuItem.getInt("appKey") == 15) {
                                subMenu.add(1, 117, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // New pre Order SAP
                            } else if (submenuItem.getInt("appKey") == 16) {
                                subMenu.add(1, 120, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // New pre Order Distributor SAP
                            } else if (submenuItem.getInt("appKey") == 17) {
                                subMenu.add(1, 121, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                            } else {
                                Log.e(LOG_DBG, submenuItem.toString());
                                //Dealer Point Sale Listing
                                //      subMenu.add(1, 2, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_steel_sale_edit_24dp));
                                //Projected Dealer Point Report
                                //      subMenu.add(1, 3, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_steel_sale_edit_24dp));

                            }

                        }
                    } else if (menuObj.getString("appKey").equals("96")) {

                        //This is a group
                        SubMenu subMenu = menu.addSubMenu(menuObj.getString("label"));
                        JSONArray submenuItems = menuObj.getJSONArray("group");
                        for (int j = 0; j < submenuItems.length(); j++) {
                            JSONObject submenuItem = submenuItems.getJSONObject(j);

                            //Dealer Visit Plan Report
                            if (submenuItem.getInt("appKey") == 1) {
                                subMenu.add(1, 105, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                //Mason Meet Status
                            } else if (submenuItem.getInt("appKey") == 2) {
                                subMenu.add(1, 110, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                //Lead Status
                            } else if (submenuItem.getInt("appKey") == 4) {
                                subMenu.add(1, 112, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                // Pre-Order Listing
                            } else if (submenuItem.getInt("appKey") == 5) {
                                subMenu.add(1, 116, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                                //Attendance Report List
                            } else if (submenuItem.getInt("appKey") == 6) {
                                subMenu.add(1, 118, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                            } else if (submenuItem.getInt("appKey") == 7) {
                                subMenu.add(1, 119, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.calendar));

                            } else {
                                Log.e(LOG_DBG, submenuItem.toString());
                            }
                        }

                    } else if (menuObj.getString("label").equals("Settings")) {
                        //This is a group
                        SubMenu subMenu = menu.addSubMenu(menuObj.getString("label"));
                        JSONArray submenuItems = menuObj.getJSONArray("group");
                        for (int j = 0; j < submenuItems.length(); j++) {
                            JSONObject submenuItem = submenuItems.getJSONObject(j);

                            if (submenuItem.getInt("appKey") == 98) {
                                if (!AboutUsDisplayed) {
                                    subMenu.add(1, 100, Menu.NONE, "About").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_setting_light));
                                    AboutUsDisplayed = true;
                                }
                                //"Change Password"
                                subMenu.add(1, 98, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_lock_outline_24dp));
                                subMenu.add(1, 101, Menu.NONE, "Current Location").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_create_24dp));
                                subMenu.add(1, 102, Menu.NONE, "About Phone").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_phone));
                            } else if (submenuItem.getInt("appKey") == 99) {
                                //"Logout"
                                subMenu.add(1, 99, Menu.NONE, submenuItem.getString("label")).setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_power_off));
                            } else {
                                Log.e(LOG_DBG, submenuItem.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //Code for logout as menu not able to initialize
                Log.e(LOG_DBG, e.getLocalizedMessage());
                Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }

            // Dynamic menu code ends here

            /*
            Menu menu= navigationView.getMenu();
            menu.clear();
            menu.setGroupCheckable(1, false, true);
            menu.add(0, 1, Menu.NONE, "Home").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_home_24dp));
            menu.addSubMenu("Transaction").add(1, 2, 2, "Dealer Point Sale Confirmation").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sales_entry_24dp));
            SubMenu report=menu.addSubMenu("Report");
            report.add(1, 3, Menu.NONE, "Dealer Point Sale Listing").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_steel_sale_edit_24dp));
            report.add(1, 4, Menu.NONE, "Projected Dealer Point Report").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_steel_sale_edit_24dp));

            SubMenu settings=menu.addSubMenu("Settings");
            settings.add(3,5,Menu.NONE,"Change Password").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_lock_outline_24dp));
            settings.add(3,6,Menu.NONE,"Logout").setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_power_off));
            */
        }
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (doubleBackToExitPressedOnce) {
                    finish();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            //Toast.makeText(this,"Password change",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,ChangePasswordActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.logout){
            SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
            sharedPref.edit().clear().apply();
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);

        //97 stands for home
        if (id == 97) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(DashboardFragment.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new DashboardFragment();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        }
        // 1 stands for steel sale confirmation
        else if (id == 1) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(DailySaleConfirmationFragment.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new DailySaleConfirmationFragment();
                Bundle bundle = new Bundle();
                String sLid = sharedPref.getString("storedSlid", "0");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String dt = sdf.format(new Date());
                bundle.putString("dt", dt);
                bundle.putBoolean("backDateEdit", true);
                bundle.putString("salesman_id", sLid);
                bundle.putString("salesman_name", "");
                fragment.setArguments(bundle);
                SteelHelper.replaceFragment(this, fragment, true);
            }
        }
        // 2 stands for daily sales listing
        else if (id == 2) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(DailySalesConfirmListingFragment.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new DailySalesConfirmListingFragment();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        }
        // 3 stands for monthly sales report
        else if (id == 3) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MonthlySalesReportFragment.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new MonthlySalesReportFragment();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        }
        //4 stands for requisition entry
        else if (id == 4) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(RequisitionListingFragment.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new RequisitionListingFragment();
                Bundle bundle = new Bundle();
                String sLid = sharedPref.getString("storedSlid", "0");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String dt = sdf.format(new Date());
                bundle.putString("dt", dt);
                bundle.putBoolean("backDateEdit", true);
                bundle.putString("salesman_id", sLid);
                bundle.putString("salesman_name", "");
                fragment.setArguments(bundle);
                SteelHelper.replaceFragment(this, fragment, true);
            }
        }
        //100 stands for About us
        else if (id == 100) {
            Intent intent = new Intent(this, AboutAppActivity.class);
            startActivity(intent);
            return true;
        }
        //98 stands for change password
        else if (id == 98) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            return true;
        }
        //99 stands for logout
        else if (id == 99) {
            if (isNetworkAvailable(this)) {
                Intent logout_intent = new Intent(this, LogoutService.class);
                String tempKeyCode = sharedPref.getString("storedKeyCode", "");
                String attendanceId = sharedPref.getString("storedAttendanceId", "");
                logout_intent.putExtra("tempKeyCode", tempKeyCode);
                logout_intent.putExtra("attendanceId", attendanceId);
                startService(logout_intent);
                unregisterReceiver(mGpsSwitchStateReceiver);
            } else {
                logoutMessage("Please Check your Internet connection !");
            }
        } else if (id == 101) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ShareCurrentLoc.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new ShareCurrentLoc();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 102) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(AboutPhone.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new AboutPhone();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 103) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(salesmanvisit_view.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new salesmanvisit_view();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 104) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NewDealerVisitView.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new NewDealerVisitView();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 105) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(DealerVisitPRptList.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new DealerVisitPRptList();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 106) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(PreOrderEntryView.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new PreOrderEntryView();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 107) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(EngineerVisitRtp.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new EngineerVisitRtp();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 108) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MasionMeetInitiate.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new MasionMeetInitiate();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 109) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MasionMeetCompletionEntry.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new MasionMeetCompletionEntry();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 110) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MasionMeetStatusView.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new MasionMeetStatusView();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 111) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NewLeadEntry.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new NewLeadEntry();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 112) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NewLeadStatus.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new NewLeadStatus();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 113) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MyLeadList.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new MyLeadList();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 114) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(QrScaneToDetailsView.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new QrScaneToDetailsView();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 115) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(BDEDailyEntryView.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new BDEDailyEntryView();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 116) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(PreOrderListView.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new PreOrderListView();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 117) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NewPreOrderEntry.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new NewPreOrderEntry();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 118) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(AttendanceRptList.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new AttendanceRptList();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 119) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(LocationTrckRptList.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new LocationTrckRptList();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 120) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(PreOrderEntryView_SAP.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new PreOrderEntryView_SAP();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else if (id == 121) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NewPreOrderEntry_SAP.class.getName());
            if (fragment != null && fragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
            } else {
                fragment = new NewPreOrderEntry_SAP();
                SteelHelper.replaceFragment(this, fragment, true);
            }
        } else {
            Toast.makeText(MainActivity.this, "Please Check your Internet connection !", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void logoutMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Logout Alert")
                .setMessage(message)
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DAILYSALE_ENTRY) {
            if (resultCode == RESULT_OK)
                mReturningWithResult = "Daily Entry Updated";
        } else if (requestCode == REQUEST_REQUISITION_ENTRY) {
            if (resultCode == RESULT_OK)
                mReturningWithResult = "Requisition Entry Updated";
        } else if (requestCode == REQUEST_SALESMAN_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                mReturningWithResult = "SALESMAN_AUTOCOMPLETE";
                return_name = data.getExtras().getString("name");
                return_id = data.getExtras().getString("id");
            }
        } else if (requestCode == REQUEST_DEALER_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                mReturningWithResult = "DEALER_AUTOCOMPLETE";
                return_name = data.getExtras().getString("name");
                return_id = data.getExtras().getString("id");
            }
        } else {
            mReturningWithResult = null;
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (mReturningWithResult != null) {
            if (mReturningWithResult.equals("Daily Entry Updated")) {
                findViewById(R.id.save).performClick();
            }
            if (mReturningWithResult.equals("Requisition Entry Updated")) {
                findViewById(R.id.save).performClick();
            }
            if (mReturningWithResult.equals("SALESMAN_AUTOCOMPLETE")) {
                TextView tvSearch = ((TextView) findViewById(R.id.search));
                if (tvSearch != null) {
                    tvSearch.setTag(return_id);
                    tvSearch.setText(return_name);
                }
                TextView tvSearch2 = ((TextView) findViewById(R.id.search_salesman));
                Button performSearch = (Button) findViewById(R.id.performSearch);
                if (tvSearch2 != null) {
                    tvSearch2.setTag(return_id);
                    tvSearch2.setText(return_name);
                    performSearch.performClick();
                }
            }
            if (mReturningWithResult.equals("DEALER_AUTOCOMPLETE")) {
                TextView tvSearch = ((TextView) findViewById(R.id.search));
                Button performSearch = (Button) findViewById(R.id.performSearch);
                if (tvSearch != null) {
                    tvSearch.setTag(return_id);
                    tvSearch.setText(return_name);
                    performSearch.performClick();
                }
            }
            mReturningWithResult = "";
        }

    }

    /**
     * Following broadcast receiver is to listen the Location button toggle state in Android.
     */
    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // Make an action or refresh an already managed state.
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
                    Intent logout_intent = new Intent(context, LogoutService.class);
                    String tempKeyCode = sharedPref.getString("storedKeyCode", "");
                    String attendanceId = sharedPref.getString("storedAttendanceId", "");
                    logout_intent.putExtra("tempKeyCode", tempKeyCode);
                    logout_intent.putExtra("attendanceId", attendanceId);
                    startService(logout_intent);
                    unregisterReceiver(mGpsSwitchStateReceiver);

                }
            }
        }
    };


    // Location in background method is belo
    public boolean startLocationUpdates() {
//        Timer timer = new Timer();
//        TimerTask hourlyTask = new TimerTask() {
//            @Override
//            public void run() {

                Intent intervalIntent = new Intent(MainActivity.this, IntervalPendingIntentService.class);
                PendingIntent intervalPendingIntent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intervalPendingIntent = PendingIntent.getService(MainActivity.this, 0, intervalIntent, PendingIntent.FLAG_MUTABLE);
                } else {
                    intervalPendingIntent = PendingIntent.getService(MainActivity.this, 0, intervalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Your Location not work........1", Toast.LENGTH_SHORT).show();

                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, intervalPendingIntent);
//            }
//        };
//        timer.schedule (hourlyTask, 0l, 1000*60*60);

        return true;
    }


    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /* DailySaleConfirmation related end */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            if (GlobalConfiguration.GPSFeatureEnabled && mGoogleApiClient.isConnected()){
                startService(new Intent(this, IntervalPendingIntentService.class));
                startLocationUpdates();
            }
        }catch (Exception e){Log.e("Location Error: ",e.toString());}
    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        String userType=sharedPref.getString("storedUserType", "");
        if(!(userType.equalsIgnoreCase("Salesman")) && GlobalConfiguration.GPSFeatureEnabled){
            try {
                stopLocationUpdates();
            }catch (Exception e){}
        }
        mCurrentLocation=location;
        //Toast.makeText(this,"Lat- "+location.getLatitude()+" Long- "+location.getLongitude(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (GlobalConfiguration.GPSFeatureEnabled && mRequestingLocationUpdates) {
            startLocationUpdates();
            //Toast.makeText(this,"Lat- "+" onConnected ",Toast.LENGTH_SHORT).show();
        }
    }

    // Notification schedule
    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_MUTABLE ) ;

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
        if(!sharedPref.getString("storedKeyCode","").equals("")) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMs, pendingIntent);
        }
    }

    // Notification Create Method
    private Notification getNotification (String content) {
        Intent intentresult = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivities(this,1, new Intent[]{intentresult},PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "BMA Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable.captain_tmt) ;
        builder.setAutoCancel(true) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        builder.setContentIntent(pendingIntent);
        return builder.build() ;
    }







    // Mobile Network Check
    void mobileDataEnableDisableAction(){
        try {
           // WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //if (wifiManager.isWifiEnabled())

            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            String networkType = "";
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "WIFI";
            }else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkType = "mobile";
            }
            //Log.e("...........",networkType);
        }
        catch (Exception e) {
            Log.e("............",e.toString());
            // TODO do whatever error handling you want here
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
