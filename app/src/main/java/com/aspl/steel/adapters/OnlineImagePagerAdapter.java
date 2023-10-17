package com.aspl.steel.adapters;

import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.aspl.steel.SteelHelper;

import java.util.ArrayList;

/**
 *  Created by Arnab Kar on 08 July 2016.
 */
public class OnlineImagePagerAdapter extends PagerAdapter {
    AppCompatActivity context;
    private ArrayList<String> mOnlineMedia;
    public OnlineImagePagerAdapter(AppCompatActivity context, ArrayList<String> mOnlineMedia) {
        this.context=context;
        this.mOnlineMedia=mOnlineMedia;
    }
    @Override
    public int getCount() {
        return mOnlineMedia.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        /*DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        try{
            Bitmap bitmap = createScaledBitmap(mMedia.get(position).getPath(), width, height);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
        }*/
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        imageView.setImageBitmap(SteelHelper.convertToImage(context,SteelHelper.resizeBase64Image(mOnlineMedia.get(position),width,height)));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (ImageView) object;
        BitmapDrawable bd = (BitmapDrawable)imageView.getDrawable();
        try {
            bd.getBitmap().recycle();
        }catch (Exception ignored){}
        imageView.setImageBitmap(null);
        container.removeView((ImageView) object);
    }
}