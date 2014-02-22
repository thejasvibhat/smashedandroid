package com.example.reviews;

import java.util.ArrayList;

import com.example.async.SmashedAsyncClient;
import com.loopj.android.http.PersistentCookieStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ListReviewDataSingleton {
    private static ListReviewDataSingleton mInstance = null;
    public ArrayList<ReviewData>venueList = new ArrayList<ReviewData>();
    private ListReviewDataSingleton(){

    }
    public static ListReviewDataSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new ListReviewDataSingleton();
        }
        return mInstance;
    }

}
