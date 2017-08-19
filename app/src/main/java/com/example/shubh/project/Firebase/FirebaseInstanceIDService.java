package com.example.shubh.project.Firebase;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Activity.Dashboard;
import com.example.shubh.project.Activity.LoginActivity;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.Utils.SharedPreferenceManager;
import com.example.shubh.project.Utils.URLConstants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    String token;
    SharedPreferenceManager spm;

    @Override
    public void onTokenRefresh() {

        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(">>>>>", token);
        spm = new SharedPreferenceManager(FirebaseInstanceIDService.this, null);
        spm.setString(SharedPreferenceManager.FCM_TOKEN, token);
        if (!TextUtils.isEmpty(spm.getString(SharedPreferenceManager.FCM_TOKEN, ""))) {
            UpdateGCMKeyOnServer();
        }
    }

    private void UpdateGCMKeyOnServer() {
        //token && alias_name
        //userinfo ch tokken update krna
        //1 or 0
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("alias_name", spm.getString(SharedPreferenceManager.P_ALIAS_NAME, ""));
            bodyObj.put("token", token);
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
        MySingleton.getInstance(FirebaseInstanceIDService.this).addToRequestQueue(jRequest);
    }

//    private void registerToken(String token) {
//        Log.d(">>>>>", token);
//
//        String userId = new SharedPreferenceManager(getApplicationContext(), null).getString(SharedPreferenceManager.USER_ID, "");
//
//        if (!userId.isEmpty() && CheckNetworkStatus.isOnline(getApplicationContext())) {
//            try {
//                JSONObject loginBody = new JSONObject();
//                loginBody.put("deviceid", token);
//                loginBody.put("user_id", userId);
//                CustomHttpRequest request = new CustomHttpRequest(Request.Method.POST, URLConstants.NOTIFICATION, URLConstants.NOTIFICATION_ID, null, loginBody.toString(), null, this);
//                MyApplication.getQueue(getApplicationContext()).add(request);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
////            Toast.makeText(mActivity, R.string.check_internet, Toast.LENGTH_SHORT).show();
//        }
//
//
//        /*OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token",token)
//                .build();
//
//        Request request = new Request.Builder()
//                .url("http://192.168.1.71/fcm/register.php")
//                .post(body)
//                .build();
//
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }*/
//    }

//    @Override
//    public void onResponseSuccess(Object response, int requestID) {
//       new SharedPreferenceManager(getApplicationContext(), null).setBoolean(SharedPreferenceManager.NOTIFICATION_SAVED, true);
//
//    }

//    @Override
//    public void onResponseError(VolleyError error, int requestID) {
//
//    }
}