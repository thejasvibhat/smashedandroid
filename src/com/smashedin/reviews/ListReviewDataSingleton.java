package com.smashedin.reviews;

import java.util.ArrayList;

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
