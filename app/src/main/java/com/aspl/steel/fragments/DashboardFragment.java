package com.aspl.steel.fragments;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

/**
 *  Created by Arnab on 22 February 2016.
 */
public class DashboardFragment extends Fragment {

    DrawerLayout mDrawerLayout;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private CameraSource cameraSource;
    private int REQUEST_CODE =12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_home, container, false);
        updateCheckMethod();
        initLogin(rootView);
        return rootView;
    }

    private void updateCheckMethod(){
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getActivity());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,getActivity(),REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        updateCheckMethod();
//    }


    private void initLogin(View rootView) {
        ((TextView) rootView.findViewById(R.id.screenTitle)).setText("Home");
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.my_actionbar_toolbar);
        ImageView barcodeScane = (ImageView) rootView.findViewById(R.id.barcode_scan);
        barcodeScane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /* ......... Old Requirement......*/

//                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                       Fragment fragment = new ScannedBarcodeActivity();
//                        SteelHelper.replaceFragment((AppCompatActivity) getActivity(), fragment, true);
//                    } else {
//                        ActivityCompat.requestPermissions(getActivity(), new
//                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                    }


                    /* ......... New Requirement......*/
                    AttendanceAtDealer fragment = new AttendanceAtDealer();
                    SteelHelper.replaceFragment((AppCompatActivity) getActivity(), fragment, true);



                }catch (Exception e){Log.e("Scann Bar Code Button: ",e.toString());}

            }
        });

        if(mToolbar!=null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            mDrawerLayout=(DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
            //This draws the menu icon on the title that looks like 3 horizontal lines(=)
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),  mDrawerLayout, mToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close
            );
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
    }
}
