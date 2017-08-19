package com.example.shubh.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.shubh.project.Adapters.MyNewAdapter;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Firebase.FirebaseInstanceIDService;
import com.example.shubh.project.Interfaces.PageOpened;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.ResizeAnimation;
import com.example.shubh.project.Utils.SharedPreferenceManager;
import com.example.shubh.project.Utils.URLConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity implements View.OnClickListener, BottomNavigationBar.OnTabSelectedListener {

    public static final String USER_INFO_KEY = "USER_INFO_KEY";
    private ViewPager viewPager;
    private BottomNavigationBar bottomNavigationBar;
    private UserInfo userInfo;
    private static SharedPreferenceManager spm;

    public static void open(Activity activity, UserInfo userInfo, String password) {

        spm = new SharedPreferenceManager(activity, null);
        spm.setString(SharedPreferenceManager.P_ALIAS_NAME, userInfo.getAlias_name());
        spm.setString(SharedPreferenceManager.P_PASSWORD, password);

        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_INFO_KEY, userInfo);
        Intent intent = new Intent(activity, Dashboard.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
        if (SplashScreen.getInstance() != null) {
            SplashScreen.getInstance().finish();
        }
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        updateToken();

    }

    private void updateToken() {
        if (!TextUtils.isEmpty(spm.getString(SharedPreferenceManager.FCM_TOKEN, ""))) {
            JSONObject bodyObj = new JSONObject();
            try {
                bodyObj.put("alias_name", spm.getString(SharedPreferenceManager.P_ALIAS_NAME, ""));
                bodyObj.put("token", spm.getString(SharedPreferenceManager.FCM_TOKEN, ""));
            } catch (JSONException e1) {

            }

            JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.UPDATE_TOKEN_URL, bodyObj, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String code = jsonObject.getString("code");
                        if (code.equals("0")) {
                        } else {
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");

                    return headers;
                }
            };
            jRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            MySingleton.getInstance(Dashboard.this).addToRequestQueue(jRequest);
        }
    }

    private void initViews() {

        userInfo = getIntent().getExtras().getParcelable(USER_INFO_KEY);
        addingbottomNavigationBar();
    }

    private void addingbottomNavigationBar() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyNewAdapter(fragmentManager, userInfo));
        AppUtil.statusBarColorChange(Dashboard.this, ContextCompat.getColor(Dashboard.this, R.color.orange));
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.search_icon, "Search").setActiveColorResource(R.color.teal))
                .addItem(new BottomNavigationItem(R.drawable.user_icon, "Profile").setActiveColorResource(R.color.blue))
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    AppUtil.statusBarColorChange(Dashboard.this, ContextCompat.getColor(Dashboard.this, R.color.orange));
                    Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
                    if (page != null && page instanceof PageOpened) {
                        ((PageOpened) page).refreshUploads();
                    }
                } else if (position == 1) {
                    AppUtil.statusBarColorChange(Dashboard.this, ContextCompat.getColor(Dashboard.this, R.color.teal));
                    Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
                    if (page != null && page instanceof PageOpened) {
                        ((PageOpened) page).refreshUploads();
                    }
                } else if (position == 2) {
                    AppUtil.statusBarColorChange(Dashboard.this, ContextCompat.getColor(Dashboard.this, R.color.blue));
                    Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
                    if (page != null && page instanceof PageOpened) {
                        ((PageOpened) page).refreshUploads();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    public BottomNavigationBar getBottomNavigation() {
        return bottomNavigationBar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EditProfile.EDIT_PROFILE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                userInfo = data.getExtras().getParcelable("userInfo");
            }

        }
    }

    public UserInfo getUserModel() {
        return userInfo;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

