package com.aspl.steel;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.adapters.ImagePagerAdapter;
import com.aspl.steel.adapters.OnlineImagePagerAdapter;

import java.util.ArrayList;

/**
 *  Created by Arnab Kar on 02 July 2016.
 */
public class MultipleURIImageBrowserActivity extends AppCompatActivity {
    int position=0;
    ArrayList<Uri> mMedia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        setContentView(R.layout.activity_multiple_uri_img_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final TextView screenTitle=(TextView)findViewById(R.id.screenTitle);
        position=getIntent().getExtras().getInt("position");
        screenTitle.setText("Attachment "+(position+1));
        ViewPager pager=(ViewPager)findViewById(R.id.pager);

        mMedia= (ArrayList<Uri>) getIntent().getExtras().getSerializable("imageURIs");
        if(mMedia!=null){
            ImagePagerAdapter adapter=new ImagePagerAdapter(this,mMedia);
            pager.setAdapter(adapter);
            pager.setCurrentItem(position);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    screenTitle.setText("Attachment "+(position+1));
                    MultipleURIImageBrowserActivity.this.position=position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }else {
            ArrayList<String> mOnlineMedia= GlobalConfiguration.mOnlineMedia;
            OnlineImagePagerAdapter adapter=new OnlineImagePagerAdapter(this,mOnlineMedia);
            pager.setAdapter(adapter);
            pager.setCurrentItem(position);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    screenTitle.setText("Attachment "+(position+1));
                    MultipleURIImageBrowserActivity.this.position=position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multiple_uri_img_browser_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_delete){
            //mMedia.remove(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete");
            builder.setMessage("Sure to delete image?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do do my action here
                    Intent data = new Intent();
                    data.putExtra("positionToRemove", position);
                    setResult(RESULT_OK, data);
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }else if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}