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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.URLConstants;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OpenFile extends AppCompatActivity implements View.OnClickListener {
    private WebView wvFileOpen;
    private String url, fileName, extension;
    private ImageButton ibBack, ibDownload;
    private TextView tvFileName;
    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        getSupportActionBar().hide();


        Intent intent = getIntent();
        url = intent.getStringExtra("urlForWebView");
        fileName = intent.getStringExtra("fileName");
        extension = intent.getStringExtra("extension");

        initViews();

    }

    private void initViews() {
        AppUtil.statusBarColorChange(OpenFile.this, ContextCompat.getColor(OpenFile.this, R.color.text_color_gray));

        wvFileOpen = (WebView) findViewById(R.id.wv_file_open);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibDownload = (ImageButton) findViewById(R.id.ib_download);
        tvFileName = (TextView) findViewById(R.id.tv_file_name);

        wvFileOpen.getSettings().setUseWideViewPort(false);

        wvFileOpen.getSettings().setJavaScriptEnabled(true);
        wvFileOpen.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLConstants.BASE_URL + "/" + url);

        tvFileName.setText(fileName.toString());

        Typeface face = Typeface.createFromAsset(OpenFile.this.getAssets(),
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
            File inputFile = null;
            String path = params[0];
            int fileLength = 0;
            try {
                URL URL = new URL(path);
                URLConnection urlConnection = URL.openConnection();
                urlConnection.connect();
                fileLength = urlConnection.getContentLength();
                File softNotes = new File("sdcard");
                if (!softNotes.exists()) {
                    softNotes.mkdir();
                }
                if (extension.equalsIgnoreCase("pdf")) {
                    File PDF = new File("sdcard/softNotes/PDFs");
                    if (!PDF.exists()) {
                        PDF.mkdir();
                    }
                    inputFile = new File(PDF, fileName + "." + extension);
                } else if (extension.equalsIgnoreCase("txt")) {
                    File Text = new File("sdcard/softNotes/Texts");
                    if (!Text.exists()) {
                        Text.mkdir();
                    }
                    inputFile = new File(Text, fileName + "." + extension);
                } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docs")) {
                    File Documents = new File("sdcard/softNotes/Documents");
                    if (!Documents.exists()) {
                        Documents.mkdir();
                    }
                    inputFile = new File(Documents, fileName + "." + extension);
                } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
                    File PowerPointPresentations = new File("sdcard/softNotes/Powerpoint Presentations");
                    if (!PowerPointPresentations.exists()) {
                        PowerPointPresentations.mkdir();
                    }
                    inputFile = new File(PowerPointPresentations, fileName + "." + extension);
                } else if (extension.equalsIgnoreCase("xls")) {
                    File excelSheets = new File("sdcard/softNotes/Excel Sheets");
                    if (!excelSheets.exists()) {
                        excelSheets.mkdir();
                    }
                    inputFile = new File(excelSheets, fileName + "." + extension);
                }


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
            super.onPostExecute(result);
            progressDialog.hide();
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
        }
    }
}