package com.example.shubh.project.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Adapters.MyAdapter;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Interfaces.FieldValidator;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button cancel, signin, next;
    private ViewPager viewPager = null;
    private String name, alias_name, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        getSupportActionBar().hide();

        initViews();

    }

    private void initViews() {

        viewPager = (ViewPager) findViewById(R.id.pager);
        cancel = (Button) findViewById(R.id.btn_cancel);
        signin = (Button) findViewById(R.id.btn_signin);
        next = (Button) findViewById(R.id.validate);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdapter(fragmentManager));
        viewPager.beginFakeDrag();


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    cancel.setText("Cancel");
                } else {
                    cancel.setText("Back");
                }
                if (position == 3) {
                    next.setText("Done");
                } else {
                    next.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        cancel.setOnClickListener(this);
        signin.setOnClickListener(this);
        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signin) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        if (v.getId() == R.id.btn_cancel) {
            if (viewPager.getCurrentItem() == 0) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        }

        if (v.getId() == R.id.validate) {
            if (((FieldValidator) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem())).validate()) {

                if (viewPager.getCurrentItem() == 0) {
                    name = ((FieldValidator) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem())).getValue();
                } else if (viewPager.getCurrentItem() == 1) {
                    alias_name = ((FieldValidator) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem())).getValue();
                } else if (viewPager.getCurrentItem() == 2) {
                    email = ((FieldValidator) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem())).getValue();
                } else if (viewPager.getCurrentItem() == 3) {
                    password = ((FieldValidator) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem())).getValue();
                }

                if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    final Dialog dialog = new Dialog(SingupActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setContentView(R.layout.layout_dialog);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    JSONObject bodyObj = new JSONObject();
                    try {
                        bodyObj.put("name", name);
                        bodyObj.put("alias_name", alias_name);
                        bodyObj.put("email", email);
                        bodyObj.put("password", password);
                    } catch (JSONException e1) {

                    }

                    JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.REGISTER_URL, bodyObj, new Response.Listener() {

                        @Override
                        public void onResponse(Object response) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                String code = jsonObject.getString("code");
                                if (code.equals("reg_failed")) {
                                    Toast.makeText(SingupActivity.this, "email or aliasName already registered with us...!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Gson gson = new Gson();
                                    UserInfo userInfo = gson.fromJson(response.toString(), UserInfo.class);
                                    Dashboard.open(SingupActivity.this, userInfo, password);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            Toast.makeText(SingupActivity.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
                    MySingleton.getInstance(SingupActivity.this).addToRequestQueue(jRequest);
                }
            } else {
                ((FieldValidator) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem())).showError();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}



