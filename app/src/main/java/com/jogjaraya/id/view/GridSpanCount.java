package com.jogjaraya.id.view;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

public class GridSpanCount {
    public static int getGridSpanCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
//        float cellWidth = activity.getResources().getDimension(R.dimen.item_product_width);
        float cellWidth = (displayMetrics.widthPixels)/2;
        return Math.round(screenWidth / cellWidth);
    }

    public static int getGridSpanCountIcon(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
//        float cellWidth = activity.getResources().getDimension(R.dimen.item_product_width);
        float cellWidth = (displayMetrics.widthPixels)/5;
        return Math.round(screenWidth / cellWidth);
    }
}
