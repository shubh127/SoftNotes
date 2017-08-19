package com.example.shubh.project.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Activity.Dashboard;
import com.example.shubh.project.Activity.EditProfile;
import com.example.shubh.project.Activity.Followers;
import com.example.shubh.project.Activity.ProfilePicture;
import com.example.shubh.project.Activity.SplashScreen;
import com.example.shubh.project.Adapters.GridviewAdapter;
import com.example.shubh.project.Core.HttpResponseListener;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Firebase.FirebaseInstanceIDService;
import com.example.shubh.project.Interfaces.PageOpened;
import com.example.shubh.project.Models.MyUploadsModel;
import com.example.shubh.project.Models.UploadInfo;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.SharedPreferenceManager;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

public class FragmentProfile extends Fragment implements View.OnClickListener, HttpResponseListener, OnMenuItemClickListener, PageOpened {
    private CircleImageView dp;
    private ImageButton revealSettings;
    private TextView editProfile, userImageProfile, uploadsCount, tvFollowers, tvFollowing;
    private LinearLayout followers, following, scrollableHead;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private Dashboard mContext;
    private UserInfo mUser;
    private String profilePicLink;
    private GridView gridView;
    private GridviewAdapter mAdapter;
    private List<UploadInfo> mUploadList = new ArrayList<UploadInfo>();
    private TextView tvNoResult;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = getArguments().getParcelable(USER_INFO_KEY);
        return inflater.inflate(R.layout.activity_fragment_profile, container, false);
    }

    private void requestForListData() {

        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("userId", mUser.getUserID().toString().trim());
        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.GET_OWN_LISTVIEW_ITEMS, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                MyUploadsModel myUploadsModel = gson.fromJson(response.toString(), MyUploadsModel.class);
                if (myUploadsModel.code == 1) {
                    tvFollowers.setText(myUploadsModel.followerCount);
                    tvFollowing.setText(myUploadsModel.followingCount);
                    mUploadList.clear();
                    mUploadList.addAll(myUploadsModel.uploadArray);
                    mAdapter.notifyDataSetChanged();
                    uploadsCount.setText(String.valueOf(myUploadsModel.uploadArray.size()));
                } else {
                    gridView.setVisibility(View.GONE);
                    tvFollowers.setText(myUploadsModel.followerCount);
                    tvFollowing.setText(myUploadsModel.followingCount);
                    mUploadList.clear();
                    mAdapter.notifyDataSetChanged();
                    tvNoResult.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gridView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jRequest);
        gridView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (Dashboard) getActivity();

        initViews();


    }

    private void initViews() {

        dp = (CircleImageView) getView().findViewById(R.id.profile_image);
        revealSettings = (ImageButton) getView().findViewById(R.id.revealSettings);
        editProfile = (TextView) getView().findViewById(R.id.editProfileBtn);
        TextView tv = (TextView) getView().findViewById(R.id.appName);
        followers = (LinearLayout) getView().findViewById(R.id.followers);
        following = (LinearLayout) getView().findViewById(R.id.following);
        gridView = (GridView) getActivity().findViewById(R.id.gv_own_uploads);
        uploadsCount = (TextView) getActivity().findViewById(R.id.uploads_count);
        tvNoResult = (TextView) getActivity().findViewById(R.id.tv_noresult);
        tvFollowers = (TextView) getActivity().findViewById(R.id.tv_followers);
        tvFollowing = (TextView) getActivity().findViewById(R.id.tv_following);
        scrollableHead = (LinearLayout) getActivity().findViewById(R.id.scrollable_head);

        mAdapter = new GridviewAdapter(getActivity(), mUploadList, true, uploadsCount);
        gridView.setAdapter(mAdapter);
        gridView.setVisibility(View.GONE);

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "Quikhand.ttf");
        tv.setTypeface(face);


        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject share = new MenuObject("Share with friends");
        share.setResource(R.drawable.icn_3);

        MenuObject rate = new MenuObject("Rate us");
        rate.setResource(R.drawable.icn_2);

        MenuObject help = new MenuObject("Help");
        help.setResource(R.drawable.icn_4);

        MenuObject write = new MenuObject("Write to us");
        write.setResource(R.drawable.icn_1);

        MenuObject logout = new MenuObject("Logout");
        logout.setResource(R.drawable.icn_5);

        List<MenuObject> menuObjects = new ArrayList<>();
        menuObjects.add(close);
        menuObjects.add(share);
        menuObjects.add(rate);
        menuObjects.add(help);
        menuObjects.add(write);
        menuObjects.add(logout);

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) 120);
        menuParams.setMenuObjects(menuObjects);
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);

        userImageProfile = (TextView) getView().findViewById(R.id.userNameProfile);
        userImageProfile.setText(mUser.getName());

        dp.setOnClickListener(this);
        revealSettings.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        followers.setOnClickListener(this);
        following.setOnClickListener(this);
        mMenuDialogFragment.setItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_image) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(USER_INFO_KEY, mUser);
            Intent intent = new Intent(getActivity(), ProfilePicture.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (v.getId() == R.id.revealSettings) {
            mMenuDialogFragment.show(mContext.getSupportFragmentManager(), "ContextMenuDialogFragment");
        }
        if (v.getId() == R.id.editProfileBtn) {
            EditProfile.Open(mContext, mUser);
        }
        if (v.getId() == R.id.followers) {
            if (tvFollowers.getText().toString().trim().equalsIgnoreCase("0")) {
            } else {
                Intent intent = new Intent(getActivity(), Followers.class);
                intent.putExtra("userID", mUser.getUserID());
                intent.putExtra("myID", mUser.getUserID());
                intent.putExtra("connectionCode", "followers");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        if (v.getId() == R.id.following) {
            if (tvFollowing.getText().toString().trim().equalsIgnoreCase("0")) {
            } else {
                Intent intent = new Intent(getActivity(), Followers.class);
                intent.putExtra("userID", mUser.getUserID());
                intent.putExtra("myID", mUser.getUserID());
                intent.putExtra("connectionCode", "followings");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    @Override
    public void onResponseSuccess(Object response, int requestID) {

    }

    @Override
    public void onResponseError(VolleyError error, int requestID) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = ((Dashboard) getActivity()).getUserModel();
        profilePicLink = mUser.getProfilePicURL();
        Picasso.with(getActivity()).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + profilePicLink).placeholder(R.drawable.place_holder).into(dp);
        mUploadList.clear();
        mAdapter.notifyDataSetChanged();
        gridView.setVisibility(View.GONE);
        requestForListData();
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                String sharebody = "Tired of copying notes the old way ?" +
                        "Download Softnotes here:***link***" +
                        "and share notes over internet";
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SoftNotes Invitation");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 2:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=dolphin.developers.com"));
                Intent chooser = Intent.createChooser(i, "opening market");
                startActivity(chooser);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 3:
                Toast.makeText(getActivity(), "Sorry !!! Cannot help you at the movement...!!!", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setData(Uri.parse("mailto:"));
                String[] to = {"shubh.behal76@gmail.com"};
                mailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "This was sent from App");
                mailIntent.putExtra(Intent.EXTRA_TEXT, "Type your queries here...!!! We'll get back to you as soon as we can...!!!THANKS !!! ");
                mailIntent.setType("message/rfc822");
                chooser = Intent.createChooser(mailIntent, "Send Email Via");
                startActivity(chooser);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 5:
                final SharedPreferenceManager spm = new SharedPreferenceManager(getActivity(), null);

                final JSONObject bodyObj = new JSONObject();
                try {
                    bodyObj.put("alias_name", spm.getString(SharedPreferenceManager.P_ALIAS_NAME, ""));
                    bodyObj.put("token", " ");
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
                                spm.setString(SharedPreferenceManager.P_ALIAS_NAME, "");
                                spm.setString(SharedPreferenceManager.P_PASSWORD, "");
                                Intent intent = new Intent(getActivity(), SplashScreen.class);
                                startActivity(intent);
                                getActivity().finish();
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                MySingleton.getInstance(getActivity()).addToRequestQueue(jRequest);

                break;
        }
    }

    @Override
    public void refreshUploads() {
        mUploadList.clear();
        mAdapter.notifyDataSetChanged();
        gridView.setVisibility(View.GONE);
        requestForListData();
    }
}
