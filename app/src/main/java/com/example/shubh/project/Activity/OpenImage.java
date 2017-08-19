package com.example.shubh.project.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.URLConstants;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OpenImage extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;
    private ImageView ivFileOpen;
    private String url, fileName;
    private ImageButton ibBack;
    private ImageButton ibDownload;
    private String extension;
    private TextView tvFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        getSupportActionBar().hide();
        AppUtil.statusBarColorChange(OpenImage.this, ContextCompat.getColor(OpenImage.this, R.color.text_color_gray));

        Intent intent = getIntent();
        url = intent.getStringExtra("urlForWebView");
        fileName = intent.getStringExtra("fileName");
        extension = intent.getStringExtra("extension");

        initViews();
    }

    private void initViews() {
        ivFileOpen = (ImageView) findViewById(R.id.iv_file_open);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibDownload = (ImageButton) findViewById(R.id.ib_download);
        tvFileName = (TextView) findViewById(R.id.tv_file_name);

        Picasso.with(this).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + url).placeholder(R.drawable.place_holder).into(ivFileOpen);
        tvFileName.setText(fileName.toString());

        Typeface face = Typeface.createFromAsset(mContext.getAssets(),
                "Quikhand.ttf");
        tvFileName.setTypeface(face);

        ibBack.setOnClickListener(this);
        ibDownload.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_back) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == R.id.ib_download) {
            checkForWritePermissions();
        }
    }

    String[] WRITEPERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int WRITE_PERMISSION_CODE = 10;

    private void checkForWritePermissions() {
        if (!hasPermissions(mContext, WRITEPERMISSIONS)) {
            ActivityCompat.requestPermissions(this, WRITEPERMISSIONS, WRITE_PERMISSION_CODE);
        } else {
            download();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case WRITE_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                } else {
                    Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void download() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("You sure ? You want to download this file ?")
                .setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file_url = URLConstants.BASE_URL + "/" + url;
                DownLoadTask downLoadTask = new DownLoadTask();
                downLoadTask.execute(file_url);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    class DownLoadTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Download in progress...Please wait...!!!");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String path = params[0];
            int fileLength = 0;
            try {
                java.net.URL URL = new URL(path);
                URLConnection urlConnection = URL.openConnection();
                urlConnection.connect();
                fileLength = urlConnection.getContentLength();
                File softNotes = new File("sdcard");
                if (!softNotes.exists()) {
                    softNotes.mkdir();
                }
                File photos = new File("sdcard/softNotes/Images");
                if (!photos.exists()) {
                    photos.mkdir();
                }
                File inputFile = new File(photos, fileName + "." + extension);
                InputStream inputStream = new BufferedInputStream(URL.openStream(), 8192);
                byte[] data = new byte[1024];
                int total = 0, count = 0;
                OutputStream outputStream = new FileOutputStream(inputFile);
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    outputStream.write(data, 0, count);
                    int progress = (int) ((total * 100) / fileLength);
                    publishProgress(progress);
                }
                inputStream.close();
                outputStream.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
        }
    }
}
