package com.example.smashedin;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.async.SmashedAsyncClient;
import com.example.async.SmashedAsyncClient.OnResponseListener;
import com.example.reviews.ReviewData;
import com.example.smashed.CSmartImageView;
import com.example.smashed.GridImageSkelAdapter;
import com.example.smashed.ResponseParser;
import com.example.smashed.Singleton;
import com.google.android.gms.internal.gh;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class ReviewPagerFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private PullToRefreshGridView oGrid;
    private PullToRefreshGridView ohGrid;
    private PullToRefreshListView reviewsGrid;
    private LatLng HAMBURG = new LatLng(53.558, 9.927);
    private LatLng KIEL = new LatLng(53.551, 9.993);
    private String m_oType = "";
    private String m_StrResponse;
    private GoogleMap map;
    private Fragment m_curFragment;
    private View mapview;
    private View photosview;
    private View infoview;
    private View ohview;
    private View reviewview;
    private ReviewData oRevData;
    public static ReviewPagerFragment newInstance(String content, ReviewData reviewData) {
        ReviewPagerFragment fragment = new ReviewPagerFragment();
        fragment.oRevData = reviewData;
        fragment.mContent = content;
        return fragment;
    }

    private String mContent = "???";
    private Fragment mainFragment; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFragment = this;
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_curFragment = this;
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
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setBackgroundColor(0xAA111111);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(1);
        layout.addView(text);
        layout.addView(text1);
        return layout;
    }
    
}
