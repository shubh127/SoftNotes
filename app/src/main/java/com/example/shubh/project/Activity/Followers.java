package com.example.shubh.project.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Adapters.ConnectionsAdapter;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.SearchInfo;
import com.example.shubh.project.Models.SearchModel;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Followers extends AppCompatActivity implements View.OnClickListener {
    private ListView lvConnections;
    private ConnectionsAdapter mAdapter;
    private List<SearchInfo> mUploadList = new ArrayList<>();
    private Context mContext = this;
    private String myID, connectionCode;
    private SearchModel searchModel;
    private String userID;
    private ImageButton ibBack;
    private TextView tvFollowerOrFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        myID = intent.getStringExtra("myID");
        connectionCode = intent.getStringExtra("connectionCode");
        userID = intent.getStringExtra("userID");

        initViews();

    }

    private void requestForConnectionsList() {
//        final Dialog dialog = new Dialog(Followers.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.layout_dialog);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("userID", userID);
            bodyObj.put("connectionCode", connectionCode);
        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.GET_CONNECTIONS_LIST_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
                Gson gson = new Gson();
                searchModel = gson.fromJson(response.toString(), SearchModel.class);
                if (searchModel.code == 1) {
                    mUploadList.clear();
                    mUploadList.addAll(searchModel.uploadArray);
                    mAdapter.notifyDataSetChanged();
                    lvConnections.setVisibility(View.VISIBLE);
                } else {
                    mUploadList.clear();
                    mAdapter.notifyDataSetChanged();
                    lvConnections.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                lvConnections.setVisibility(View.GONE);
                Toast.makeText(Followers.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(Followers.this).addToRequestQueue(jRequest);
        lvConnections.setVisibility(View.VISIBLE);
    }

    private void initViews() {

        AppUtil.statusBarColorChange(Followers.this, ContextCompat.getColor(Followers.this, R.color.brown));

        lvConnections = (ListView) findViewById(R.id.lv_connections);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvFollowerOrFollowing = (TextView) findViewById(R.id.tv_follower_or_following);

        if (connectionCode.equalsIgnoreCase("followers")) {
            tvFollowerOrFollowing.setText("Followers");
        } else {
            tvFollowerOrFollowing.setText("Followings");
        }

        Typeface face = Typeface.createFromAsset(mContext.getAssets(),
                "Quikhand.ttf");
        tvFollowerOrFollowing.setTypeface(face);

        mAdapter = new ConnectionsAdapter(Followers.this, myID, mUploadList);
        lvConnections.setAdapter(mAdapter);
        lvConnections.setVisibility(View.GONE);

        ibBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUploadList.clear();
        lvConnections.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        requestForConnectionsList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_back) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
