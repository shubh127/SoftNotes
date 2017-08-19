package com.example.shubh.project.Activity;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubh.project.Models.SearchInfo;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.squareup.picasso.Picasso;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;
import static com.example.shubh.project.R.drawable.dp;

public class UserProfilePicture extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibClose;
    private TextView tvAliasName;
    private String profilePicURL;
    private SearchInfo mUser;
    private ImageView ivUserProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_picture);
        getSupportActionBar().hide();

        initViews();
    }

    private void initViews() {

        AppUtil.statusBarColorChange(UserProfilePicture.this, ContextCompat.getColor(UserProfilePicture.this, R.color.black));


        ibClose = (ImageButton) findViewById(R.id.ib_close);
        ivUserProfilePicture = (ImageView) findViewById(R.id.iv_user_profile_picture);
        tvAliasName = (TextView) findViewById(R.id.tv_alias_name);

        mUser = getIntent().getExtras().getParcelable(USER_INFO_KEY);

        profilePicURL = mUser.profPicURL;
        tvAliasName.setText(mUser.alias_name);

        Typeface face = Typeface.createFromAsset(UserProfilePicture.this.getAssets(),
                "Quikhand.ttf");
        tvAliasName.setTypeface(face);

        Picasso.with(this).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + profilePicURL).placeholder(R.drawable.placeholder).into(ivUserProfilePicture);

        ibClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_close) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
