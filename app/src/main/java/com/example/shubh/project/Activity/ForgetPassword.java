package com.example.shubh.project.Activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    private Button cancel, send;
    private EditText emailET;
    private TextInputLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().hide();
        initViews();
    }

    private void initViews() {
        AppUtil.statusBarColorChange(ForgetPassword.this, ContextCompat.getColor(ForgetPassword.this, R.color.blue));


        cancel = (Button) findViewById(R.id.btn_cancel);
        send = (Button) findViewById(R.id.send);
        emailET = (EditText) findViewById(R.id.ed_email);
        layout = (TextInputLayout) findViewById(R.id.til1);

        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (v.getId() == R.id.send) {
            if (validateData()) {
                if (AppUtil.internetConnected(this)) {
                    sendNewPasswordRequest();
                } else {
                    Toast.makeText(this, "Please check your INTERNET CONNECTIVITY", Toast.LENGTH_SHORT);
                }
            } else {
                layout.setError("       Invalid Email Pattern (something@domain.com");
            }
        }
    }

    private void sendNewPasswordRequest() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("email", emailET.getText().toString().trim());

        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.FORGOT_PASSWORD, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        Toast.makeText(ForgetPassword.this, "Email entered is not registered with us...!!!", Toast.LENGTH_SHORT).show();
                    } else if (code.equals("00")) {
                        Toast.makeText(ForgetPassword.this, "Some problem occurred ...Please try again later...!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgetPassword.this, "Password has been sent to entered email ...Please check...!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(ForgetPassword.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(this).addToRequestQueue(jRequest);
    }

    private boolean validateData() {
        boolean result = true;

        if (!TextUtils.isEmpty(emailET.getText().toString().trim()) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString().trim()).matches()) {
            layout.setErrorEnabled(false);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
