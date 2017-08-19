package com.example.shubh.project.Activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.shubh.project.Utils.SharedPreferenceManager;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private TextView tvHeading;
    private EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private Button btCancel, btConfirm;
    private Context mContext = this;
    private UserInfo userInfo;
    private ImageView ivShowHidePass1, ivShowHidePass2, ivShowHidePass3;
    private boolean isHidden1 = true, isHidden2 = true, isHidden3 = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        AppUtil.statusBarColorChange(ChangePassword.this, ContextCompat.getColor(ChangePassword.this, R.color.blue));
        userInfo = getIntent().getExtras().getParcelable(USER_INFO_KEY);

        initViews();
    }

    private void initViews() {

        getSupportActionBar().hide();


        tvHeading = (TextView) findViewById(R.id.tv_heading);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConfirmNewPassword = (EditText) findViewById(R.id.et_confirm_new_password);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        btConfirm = (Button) findViewById(R.id.bt_confirm);
        ivShowHidePass1 = (ImageView) findViewById(R.id.iv_show_hide_pass_1);
        ivShowHidePass2 = (ImageView) findViewById(R.id.iv_show_hide_pass_2);
        ivShowHidePass3 = (ImageView) findViewById(R.id.iv_show_hide_pass_3);

        Typeface face = Typeface.createFromAsset(mContext.getAssets(),
                "Quikhand.ttf");
        tvHeading.setTypeface(face);


        btCancel.setOnClickListener((View.OnClickListener) mContext);
        btConfirm.setOnClickListener((View.OnClickListener) mContext);
        ivShowHidePass1.setOnClickListener((View.OnClickListener) mContext);
        ivShowHidePass2.setOnClickListener((View.OnClickListener) mContext);
        ivShowHidePass3.setOnClickListener((View.OnClickListener) mContext);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_cancel) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (v.getId() == R.id.bt_confirm) {

            if (etOldPassword.getText().toString().trim().length() == 0) {
                etOldPassword.setError("Field Required");
                etOldPassword.requestFocus();
            } else if (etNewPassword.getText().toString().trim().length() == 0) {
                etNewPassword.setError("Field Required");
                etNewPassword.requestFocus();
                if (etNewPassword.getText().toString().trim().length() < 6) {
                    etNewPassword.setError("Too short ...length must be > 6 ");
                    etNewPassword.requestFocus();
                }
            } else if (etConfirmNewPassword.getText().toString().trim().length() == 0) {
                etConfirmNewPassword.setError("Field Required");
                etConfirmNewPassword.requestFocus();
            } else {
                if (etConfirmNewPassword.getText().toString().trim().equals(etNewPassword.getText().toString().trim())) {
                    changePassword();
                } else {
                    etNewPassword.setError("Does not match ");
                    etConfirmNewPassword.setError("Does not match");
                    etNewPassword.requestFocus();
                    etConfirmNewPassword.requestFocus();
                }
            }
        }
        if (v.getId() == R.id.iv_show_hide_pass_1) {
            if (isHidden1) {
                etOldPassword.setTransformationMethod(null);
                ivShowHidePass1.setImageDrawable(getResources().getDrawable(R.drawable.show));
                isHidden1 = false;
            } else {
                etOldPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivShowHidePass1.setImageDrawable(getResources().getDrawable(R.drawable.hide));
                isHidden1 = true;
            }
        }
        if (v.getId() == R.id.iv_show_hide_pass_2) {
            if (isHidden2) {
                etNewPassword.setTransformationMethod(null);
                ivShowHidePass2.setImageDrawable(getResources().getDrawable(R.drawable.show));
                isHidden2 = false;
            } else {
                etNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivShowHidePass2.setImageDrawable(getResources().getDrawable(R.drawable.hide));
                isHidden2 = true;
            }
        }
        if (v.getId() == R.id.iv_show_hide_pass_3) {
            if (isHidden3) {
                etConfirmNewPassword.setTransformationMethod(null);
                ivShowHidePass3.setImageDrawable(getResources().getDrawable(R.drawable.show));
                isHidden3 = false;
            } else {
                etConfirmNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivShowHidePass3.setImageDrawable(getResources().getDrawable(R.drawable.hide));
                isHidden3 = true;
            }
        }
    }

    private void changePassword() {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("alias_name", userInfo.getAlias_name().toString());
            bodyObj.put("oldPassword", etOldPassword.getText().toString().trim());
            bodyObj.put("newPassword", etNewPassword.getText().toString().trim());
        } catch (JSONException e1) {
            Toast.makeText(mContext, "Some problem occurred...Please try again later...!!!", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.CHANGE_PASSWORD_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        Toast.makeText(mContext, "Your old password is incorrect...please correct and try again ...!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferenceManager spm = new SharedPreferenceManager(ChangePassword.this, null);
                        spm.setString(SharedPreferenceManager.P_PASSWORD, etNewPassword.getText().toString().trim());

                        Toast.makeText(mContext, "Password has been changed ...!!!", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(mContext, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(mContext).addToRequestQueue(jRequest);
    }


}

