package com.example.shubh.project.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dynamitechetan.flowinggradient.FlowingGradientClass;
import com.example.shubh.project.BuildConfig;
import com.example.shubh.project.Core.HttpHandler;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.ResizeAnimation;
import com.example.shubh.project.Utils.SharedPreferenceManager;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {
    private Button signup;
    private Button signin;
    private static final String DEFAULT = "";
    private LinearLayout buttons_splash;
    private static SplashScreen instance;
    private ImageView logo;
    private LinearLayout ll_splash;
    private int height;
    private LinearLayout ll;
    private String Alias_name;
    private String Password;
    private String versionUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        instance = this;
        initViews();
        if (AppUtil.internetConnected(this)) {
            new VersionCheck().execute();
        } else {
            autoLoginCheck();
            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void autoLoginCheck() {

        SharedPreferenceManager spm = new SharedPreferenceManager(this, null);
        Alias_name = spm.getString(SharedPreferenceManager.P_ALIAS_NAME, DEFAULT);
        Password = spm.getString(SharedPreferenceManager.P_PASSWORD, DEFAULT);

        if (!Alias_name.equals(DEFAULT) && !Password.equals(DEFAULT)) {
            if (AppUtil.internetConnected(this)) {
                performAutoLogin();
            } else {
                showSplashWithAnimations();
            }
        } else {
            showSplashWithAnimations();
        }

    }


    private void performAutoLogin() {
        final Dialog dialog = new Dialog(SplashScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("alias_name", Alias_name);
            bodyObj.put("password", Password);
        } catch (JSONException e1) {
        }
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.LOGIN_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("login_failed")) {
                        showSplashWithAnimations();
                    } else {
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(response.toString(), UserInfo.class);
                        Dashboard.open(SplashScreen.this, userInfo, Password);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                showSplashWithAnimations();
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
        MySingleton.getInstance(SplashScreen.this).addToRequestQueue(jRequest);
    }

    private void initViews() {

        signup = (Button) findViewById(R.id.btn_signup);
        signin = (Button) findViewById(R.id.btn_singin);
        buttons_splash = (LinearLayout) findViewById(R.id.buttons_splash);
        logo = (ImageView) findViewById(R.id.logo);
        ll_splash = (LinearLayout) findViewById(R.id.rr_splash);
        ll = (LinearLayout) findViewById(R.id.main_layout);

        FlowingGradientClass grad = new FlowingGradientClass();
        grad.setBackgroundResource(R.drawable.translate)
                .onLinearLayout(ll)
                .setTransitionDuration(2000)
                .start();

        getDisplaySize();

        signup.setOnClickListener(this);
        signin.setOnClickListener(this);


    }

    private void getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;
    }

    private void showSplashWithAnimations() {

        logo.setVisibility(View.VISIBLE);
        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        logo.startAnimation(animFadein);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ResizeAnimation resizeAnimation = new ResizeAnimation(
                        ll_splash,
                        height / 2,
                        0
                );
                resizeAnimation.setDuration(2000);
                resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        buttons_splash.setVisibility(View.VISIBLE);
                        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.fade_in);
                        buttons_splash.startAnimation(animFadein);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                ll_splash.startAnimation(resizeAnimation);
            }
        }, 2000);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signup) {
            Intent intent = new Intent(this, SingupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (v.getId() == R.id.btn_singin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public static SplashScreen getInstance() {
        return instance;
    }

    private class VersionCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler hh = new HttpHandler();
            String jsonStr = hh.makeServiceCall(URLConstants.VERSION_CHEK_URL);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray version = jsonObject.getJSONArray("Version");
                    for (int i = 0; i < version.length(); i++) {
                        JSONObject v = version.getJSONObject(i);
                        versionUpdate = v.getString("version");
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String versionName = BuildConfig.VERSION_NAME;

            if (versionUpdate.equals(versionName)) {


                autoLoginCheck();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setTitle("Our App got Update");
                builder.setIcon(R.drawable.homeicon);
                builder.setCancelable(false);
                builder.setMessage("New version available, select update to update our app")
                        .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final String appName = getPackageName();

                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                                }

                                finish();

                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();


            }
        }
    }

}


