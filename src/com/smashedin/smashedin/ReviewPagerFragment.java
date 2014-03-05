package com.smashedin.smashedin;

import com.smashedin.reviews.ReviewData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class ReviewPagerFragment extends Fragment {
    private ReviewData oRevData;
    public static ReviewPagerFragment newInstance(String content, ReviewData reviewData) {
        ReviewPagerFragment fragment = new ReviewPagerFragment();
        fragment.oRevData = reviewData;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String disText = oRevData.review;
        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        text.setTextColor(0xFFFFFFFF);
        text.setText(disText);
        text.setTextSize(10 * getResources().getDisplayMetrics().density);
        text.setPadding(20, 20, 20, 20);
        String dString1 = "For "+oRevData.barname+" by "+oRevData.name;
        TextView text1 = new TextView(getActivity());
        text1.setGravity(Gravity.RIGHT);
        text1.setTextColor(0xFFFFFFFF);
        text1.setText(dString1);
        text1.setTextSize(8 * getResources().getDisplayMetrics().density);
        text1.setPadding(20, 20, 20, 20);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(0xAA111111);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(1);
        layout.addView(text);
        layout.addView(text1);
        return layout;
    }
    
}
