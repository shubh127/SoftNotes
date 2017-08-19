package com.example.shubh.project.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.squareup.picasso.Picasso;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

public class ProfilePicture extends AppCompatActivity implements View.OnClickListener {
    private ImageButton close;
    private TextView aliasName;
    private String profilePicURL;
    private UserInfo mUser;
    private ImageView dp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        getSupportActionBar().hide();

        initViews();
    }

    private void initViews() {

        AppUtil.statusBarColorChange(ProfilePicture.this, ContextCompat.getColor(ProfilePicture.this, R.color.black));


        close = (ImageButton) findViewById(R.id.close);
        dp = (ImageView) findViewById(R.id.profile_image);
        aliasName = (TextView) findViewById(R.id.aliasName);

        mUser = getIntent().getExtras().getParcelable(USER_INFO_KEY);

        profilePicURL = mUser.getProfilePicURL();
        aliasName.setText(mUser.getAlias_name());

        Typeface face = Typeface.createFromAsset(ProfilePicture.this.getAssets(),
                "Quikhand.ttf");
        aliasName.setTypeface(face);

        Picasso.with(this).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + profilePicURL).placeholder(R.drawable.placeholder).into(dp);

        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
