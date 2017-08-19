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
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Adapters.GridviewAdapter;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.MyUploadsModel;
import com.example.shubh.project.Models.SearchInfo;
import com.example.shubh.project.Models.UploadInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;
    private SearchInfo userProfileData;
    private CircleImageView civProfilePicture;
    private TextView tvAliasName, tvName, tvEmail, tvCollege, tvPhone, tvSex, tvDateOfBirth, tvFollower, tvFollowing, tvUploadsCount, tvNoResult;
    private GridView gvUsersUploads;
    private Button btnFollow;
    private GridviewAdapter mAdapter;
    private List<UploadInfo> mUploadList = new ArrayList<UploadInfo>();
    private String myID;
    private MyUploadsModel myUploadsModel;
    private LinearLayout llFollowers, llFollowings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        AppUtil.statusBarColorChange(UserProfile.this, ContextCompat.getColor(mContext, R.color.purple));
        userProfileData = getIntent().getExtras().getParcelable("userProfileData");
        myID = getIntent().getExtras().getString("myID");
        initViews();
        requestForListData();

    }

    private void requestForListData() {


        final Dialog dialog = new Dialog(UserProfile.this);

        final JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("myID", myID);
            bodyObj.put("userId", userProfileData.userID.toString().trim());
        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.GET_PROFILE_LISTVIEW_ITEMS_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                myUploadsModel = gson.fromJson(response.toString(), MyUploadsModel.class);
                if (userProfileData.fileCount != null && !userProfileData.fileCount.toString().trim().isEmpty()) {
                    tvUploadsCount.setText(userProfileData.fileCount);
                }
                if (myUploadsModel.followerCount != null && !myUploadsModel.followerCount.toString().trim().isEmpty()) {
                    tvFollower.setText(myUploadsModel.followerCount);
                }
                if (myUploadsModel.followingCount != null && !myUploadsModel.followingCount.toString().trim().isEmpty()) {
                    tvFollowing.setText(myUploadsModel.followingCount);
                }
                if (myUploadsModel.code == 1) {
                    mUploadList.clear();
                    mUploadList.addAll(myUploadsModel.uploadArray);
                    mAdapter.notifyDataSetChanged();
                    tvNoResult.setVisibility(View.GONE);
                    tvUploadsCount.setText("" + myUploadsModel.uploadArray.size());
                } else {
                    mUploadList.clear();
                    mAdapter.notifyDataSetChanged();
                    tvNoResult.setVisibility(View.VISIBLE);
                    tvUploadsCount.setText("0");
                    gvUsersUploads.setVisibility(View.GONE);
                }
                if (myUploadsModel.isFollowing == null) {
                    btnFollow.setVisibility(View.GONE);
                } else if (myUploadsModel.isFollowing.equalsIgnoreCase("1")) {
                    btnFollow.setVisibility(View.VISIBLE);
                    btnFollow.setText("UnFollow");
                } else {
                    btnFollow.setVisibility(View.VISIBLE);
                    btnFollow.setText("Follow");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gvUsersUploads.setVisibility(View.GONE);
                Toast.makeText(UserProfile.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(UserProfile.this).addToRequestQueue(jRequest);
        gvUsersUploads.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        getSupportActionBar().hide();

        civProfilePicture = (CircleImageView) findViewById(R.id.civ_profile_picture);
        tvAliasName = (TextView) findViewById(R.id.tv_alias_name);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvCollege = (TextView) findViewById(R.id.tv_college);
        tvPhone = (TextView) findViewById(R.id.tv_contact);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvDateOfBirth = (TextView) findViewById(R.id.tv_dob);
        tvFollower = (TextView) findViewById(R.id.tv_followers);
        tvFollowing = (TextView) findViewById(R.id.tv_following);
        tvUploadsCount = (TextView) findViewById(R.id.tv_uploads_count);
        gvUsersUploads = (GridView) findViewById(R.id.gv_users_upload);
        btnFollow = (Button) findViewById(R.id.btn_follow);
        tvNoResult = (TextView) findViewById(R.id.tv_noresult);
        llFollowers = (LinearLayout) findViewById(R.id.followers);
        llFollowings = (LinearLayout) findViewById(R.id.following);

        mAdapter = new GridviewAdapter(UserProfile.this, mUploadList, false, tvUploadsCount);
        gvUsersUploads.setAdapter(mAdapter);
        gvUsersUploads.setVisibility(View.GONE);

        setViews();

        civProfilePicture.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowings.setOnClickListener(this);
    }

    private void setViews() {
        Typeface face = Typeface.createFromAsset(mContext.getAssets(),
                "Quikhand.ttf");
        tvAliasName.setTypeface(face);


        tvAliasName.setText(userProfileData.alias_name);
        tvName.setText(userProfileData.name);
        tvEmail.setText("(" + userProfileData.email + ")");
        Picasso.with(this).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + userProfileData.profPicURL).placeholder(R.drawable.place_holder).into(civProfilePicture);

        if (userProfileData.dateOfBirth != null && !userProfileData.dateOfBirth.isEmpty()) {
            tvDateOfBirth.setText(userProfileData.dateOfBirth);
        }
        if (userProfileData.sex != null && !userProfileData.sex.toString().trim().isEmpty()) {
            tvSex.setText(userProfileData.sex);
        }
        if (userProfileData.college != null && !userProfileData.college.toString().trim().isEmpty()) {
            tvCollege.setText(userProfileData.college);
        }
        if (userProfileData.phone != null && !userProfileData.phone.toString().trim().isEmpty()) {
            tvPhone.setText(userProfileData.phone);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_follow) {
            sendFollowUnfollowRequest();
        }
        if (v.getId() == R.id.followers) {
            if (tvFollower.getText().toString().trim().equalsIgnoreCase("0")) {

            } else {
                Intent intent = new Intent(UserProfile.this, Followers.class);
                intent.putExtra("userID", userProfileData.userID);
                intent.putExtra("myID", myID);
                intent.putExtra("connectionCode", "followers");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        if (v.getId() == R.id.following) {
            if (tvFollowing.getText().toString().trim().equalsIgnoreCase("0")) {
            } else {
                Intent intent = new Intent(UserProfile.this, Followers.class);
                intent.putExtra("myID", myID);
                intent.putExtra("userID", userProfileData.userID);
                intent.putExtra("connectionCode", "followings");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        if (v.getId() == R.id.civ_profile_picture) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(USER_INFO_KEY, userProfileData);
            Intent intent = new Intent(mContext, UserProfilePicture.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void sendFollowUnfollowRequest() {

        final Dialog dialog = new Dialog(UserProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String doFollow;
        if (btnFollow.getText().toString().equalsIgnoreCase("follow")) {
            doFollow = "1";
        } else {
            doFollow = "0";
        }

        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("myID", myID);
            bodyObj.put("userId", userProfileData.userID.toString().trim());
            bodyObj.put("doFollow", doFollow);
        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.CONNECTION_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {

                        if (btnFollow.getText().toString().equalsIgnoreCase("Follow")) {
                            btnFollow.setText("UnFollow");
                            myUploadsModel.followerCount = String.valueOf(Integer.parseInt(myUploadsModel.followerCount) + 1);
                            tvFollower.setText(myUploadsModel.followerCount);
                        } else if (btnFollow.getText().toString().equalsIgnoreCase("UnFollow")) {
                            btnFollow.setText("Follow");
                            myUploadsModel.followerCount = String.valueOf(Integer.parseInt(myUploadsModel.followerCount) - 1);
                            tvFollower.setText(myUploadsModel.followerCount);
                        }

                    } else {
                        Toast.makeText(mContext, "Something went wrong...!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(UserProfile.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        })

        {
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
        MySingleton.getInstance(UserProfile.this).addToRequestQueue(jRequest);
    }
}
