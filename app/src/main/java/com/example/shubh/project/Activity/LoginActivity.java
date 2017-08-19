package com.example.shubh.project.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.shubh.project.Utils.LoadingView;
import com.example.shubh.project.Utils.URLConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCancel;
    private Button signup;
    private Button Login;
    private EditText aliasName;
    private EditText password;
    private TextView forget;
    private String e;
    private String p;
    private ImageView ivShowHidePass;
    private boolean isHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();

    }

    private void initViews() {

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        signup = (Button) findViewById(R.id.btn_signup);
        aliasName = (EditText) findViewById(R.id.ed_alias_name);
        password = (EditText) findViewById(R.id.ed_pass);
        forget = (TextView) findViewById(R.id.foregetPassword);
        Login = (Button) findViewById(R.id.validate);
        ivShowHidePass = (ImageView) findViewById(R.id.iv_show_hide_pass);

        btnCancel.setOnClickListener(this);
        signup.setOnClickListener(this);
        Login.setOnClickListener(this);
        forget.setOnClickListener(this);
        ivShowHidePass.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (v.getId() == R.id.btn_signup) {
            Intent intent = new Intent(this, SingupActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (v.getId() == R.id.validate) {
            e = aliasName.getText().toString().trim();
            p = password.getText().toString().trim();

            if (e.equals("") || p.equals("")) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (AppUtil.internetConnected(this)) {
                    doLogin();
                } else {
                    Toast.makeText(this, "Please check your INTERNET CONNECTIVITY", Toast.LENGTH_SHORT);
                }
            }
        }
        if (v.getId() == R.id.foregetPassword) {
            Intent intent = new Intent(this, ForgetPassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (v.getId() == R.id.iv_show_hide_pass) {
            if (isHidden) {
                password.setTransformationMethod(null);
                ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.show));
                isHidden = false;
            } else {
                password.setTransformationMethod(new PasswordTransformationMethod());
                ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.hide));
                isHidden = true;
            }
        }
    }

    private void doLogin() {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("alias_name", e);
            bodyObj.put("password", p);
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
                        Toast.makeText(LoginActivity.this, "Alias Name or Password was incorrect...Please check...!", Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(response.toString(), UserInfo.class);
                        Dashboard.open(LoginActivity.this, userInfo, password.getText().toString().trim());
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
