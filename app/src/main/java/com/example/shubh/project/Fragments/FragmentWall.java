package com.example.shubh.project.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Activity.Dashboard;
import com.example.shubh.project.Activity.LoginActivity;
import com.example.shubh.project.Adapters.WallListAdapter;
import com.example.shubh.project.Core.HttpResponseListener;
import com.example.shubh.project.Core.MultiPartRequest;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Interfaces.PageOpened;
import com.example.shubh.project.Models.UploadSuccess;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.Models.WallInfo;
import com.example.shubh.project.Models.WallModel;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;
import com.kosalgeek.android.photoutil.CameraPhoto;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.shubh.project.Utils.FileUploadUtils.getDataColumn;
import static com.example.shubh.project.Utils.FileUploadUtils.isDownloadsDocument;
import static com.example.shubh.project.Utils.FileUploadUtils.isExternalStorageDocument;
import static com.example.shubh.project.Utils.FileUploadUtils.isMediaDocument;

public class FragmentWall extends Fragment implements View.OnClickListener, HttpResponseListener, PageOpened, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
    private UserInfo mUser;
    private FloatingActionButton fabChooseFile, fabCamera, fabGallery, fabOthers;
    private Animation fabOpen, fabClose, fabRotateClockwise, fabRotateAntiClockwise;
    private boolean isOpen = false;
    private String uploadTitle;
    private Dialog dialog;
    private String photoPath = "";
    private final int CAMERA_REQUEST = 13323;
    private final int GALLERY_REQUEST = 22131;
    private final int FILE_SELECT_CODE = 33212;
    private CameraPhoto cameraPhoto;
    private String filePath;
    private String uploadTitleForPic;
    private ListView lvWall;
    private WallListAdapter wallListAdapter;
    private List<WallInfo> mUploadList = new ArrayList<WallInfo>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private UploadSuccess uploadsSuccess;
    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentScrollState;
    private int pageNo = -1;
    private boolean doLoadMore = true;
    private ProgressBar pbProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = getArguments().getParcelable(Dashboard.USER_INFO_KEY);
        return inflater.inflate(R.layout.activity_fragment_wall, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVIews();
        if (mUploadList.isEmpty()) {
            getListView();
        } else {
            lvWall.setVisibility(View.VISIBLE);
        }

    }

    private void getListView() {


        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("userId", mUser.getUserID().toString().trim());
            bodyObj.put("pageNo", pageNo + 1);
            if (mUploadList.isEmpty()) {
                bodyObj.put("lastPostId", 0);
            } else {
                bodyObj.put("lastPostId", mUploadList.get(mUploadList.size() - 1).uploadDetails.UploadId);
            }

        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.GET_WALL_LIST_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                pbProgress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Gson gson = new Gson();
                WallModel wallModel = gson.fromJson(response.toString(), WallModel.class);
                if (wallModel.code == 1) {
                    mUploadList.addAll(wallModel.wallArray);
                    wallListAdapter.notifyDataSetChanged();
                    pageNo++;
                } else {
                    if (mUploadList.size() == 0) {
                        lvWall.setVisibility(View.GONE);
                    } else {
                        doLoadMore = false;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jRequest);
        lvWall.setVisibility(View.VISIBLE);
    }

    private void initVIews() {
        fabChooseFile = (FloatingActionButton) getActivity().findViewById(R.id.fab_choose_file);
        fabCamera = (FloatingActionButton) getActivity().findViewById(R.id.fab_camera);
        fabGallery = (FloatingActionButton) getActivity().findViewById(R.id.fab_gallery);
        fabOthers = (FloatingActionButton) getActivity().findViewById(R.id.fab_file_manager);
        fabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
        fabRotateAntiClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anticlockwise);
        cameraPhoto = new CameraPhoto(getActivity());
        lvWall = (ListView) getActivity().findViewById(R.id.lv_wall);
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        pbProgress = (ProgressBar) getActivity().findViewById(R.id.progressBarLoadingRecite);

        wallListAdapter = new WallListAdapter(getActivity(), mUploadList);
        lvWall.setAdapter(wallListAdapter);
        lvWall.setVisibility(View.GONE);

        TextView tv = (TextView) getView().findViewById(R.id.appName);
        tv.setText(mUser.getAlias_name());
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "Quikhand.ttf");
        tv.setTypeface(face);


        fabChooseFile.setOnClickListener(this);
        fabCamera.setOnClickListener(this);
        fabGallery.setOnClickListener(this);
        fabOthers.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        lvWall.setOnScrollListener(this);

    }

    String[] CAMERAPERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int CAMERA_PERMISSION_CODE = 3;
    String[] GALLERYPERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int GALLERY_PERMISSION_CODE = 2;
    String[] STORAGEPERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int STORAGE_PERMISSION_CODE = 1;

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fab_choose_file) {
            if (isOpen) {
                fabCamera.startAnimation(fabClose);
                fabGallery.startAnimation(fabClose);
                fabOthers.startAnimation(fabClose);
                fabChooseFile.startAnimation(fabRotateAntiClockwise);
                fabCamera.setClickable(false);
                fabGallery.setClickable(false);
                fabOthers.setClickable(false);
                isOpen = false;
            } else {
                fabCamera.startAnimation(fabOpen);
                fabGallery.startAnimation(fabOpen);
                fabOthers.startAnimation(fabOpen);
                fabChooseFile.startAnimation(fabRotateClockwise);
                fabCamera.setClickable(true);
                fabGallery.setClickable(true);
                fabOthers.setClickable(true);
                isOpen = true;
            }
        }
        if (v.getId() == R.id.fab_camera) {
            checkForCameraPermission();
        }
        if (v.getId() == R.id.fab_gallery) {
            checkForGalleryPermission();
        }
        if (v.getId() == R.id.fab_file_manager) {
            checkForStoragePermission();
        }
    }

    private void checkForStoragePermission() {
        if (!hasPermissions(getActivity(), STORAGEPERMISSIONS)) {
            requestPermissions(STORAGEPERMISSIONS, STORAGE_PERMISSION_CODE);
        } else {
            openStorage();
        }
    }

    private void openStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Please intall a file manager", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkForCameraPermission() {

        if (!hasPermissions(getActivity(), CAMERAPERMISSIONS)) {
            requestPermissions(CAMERAPERMISSIONS, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void checkForGalleryPermission() {

        if (!hasPermissions(getActivity(), GALLERYPERMISSIONS)) {
            requestPermissions(GALLERYPERMISSIONS, GALLERY_PERMISSION_CODE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void openCamera() {
        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(getActivity(), "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(getActivity(), "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openStorage();
                } else {
                    Toast.makeText(getActivity(), "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void multiPartRequest() {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setBoundary("BOUNDARY");
            File f = new File(filePath);
            FileBody fileBody = new FileBody(f);

            builder.addPart("fileToUplod", fileBody);
            builder.addPart("title", new StringBody(uploadTitle, ContentType.TEXT_PLAIN));
            builder.addPart("alias_name", new StringBody(mUser.getAlias_name().toString().trim(), ContentType.TEXT_PLAIN));
            MultiPartRequest request = new MultiPartRequest(Request.Method.POST, URLConstants.UPLOD_FILES_URL, 1, null, builder.build(), 300000, null, FragmentWall.this);
            showDialog();

            MySingleton.getInstance(getActivity()).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "OOps!! Something went wrong...", Toast.LENGTH_LONG).show();
        }
    }

    private void getTitle() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Title To Your Upload")
                .setCancelable(false);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTitleForPic = input.getText().toString().trim();

                if (uploadTitleForPic.length() == 0) {
                    Toast.makeText(getActivity(), "Title can't be empty ...!!!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        dialog.dismiss();
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        builder.setBoundary("BOUNDARY");
                        File f = new File(photoPath);
                        Bitmap bm = checkForRotation(photoPath,
                                decodeSampledBitmapFromResource(photoPath, 500, 500));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(b);
                        fos.flush();
                        fos.close();
                        FileBody fileBody = new FileBody(f);

                        builder.addPart("fileToUplod", fileBody);
                        builder.addPart("title", new StringBody(uploadTitleForPic, ContentType.TEXT_PLAIN));
                        builder.addPart("alias_name", new StringBody(mUser.getAlias_name().toString().trim(), ContentType.TEXT_PLAIN));

                        MultiPartRequest request = new MultiPartRequest(Request.Method.POST, URLConstants.UPLOD_FILES_URL, 1, null, builder.build(), 300000, null, FragmentWall.this);
                        showDialog();

                        MySingleton.getInstance(getActivity()).addToRequestQueue(request);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "OOps!! Something went wrong...!!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                photoPath = cameraPhoto.getPhotoPath();
                getTitle();
            } else if (requestCode == GALLERY_REQUEST) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn;
                    filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoPath = cursor.getString(columnIndex);
                    cursor.close();
                    getTitle();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Something went wrong while loading photo", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == FILE_SELECT_CODE) {
                String path = getPath(getActivity(), data.getData());

                if (!TextUtils.isEmpty(path)) {
                    if ((path.endsWith(".pdf") || path.endsWith(".txt")
                            || path.endsWith(".doc") || path.endsWith(".docs")
                            || path.endsWith(".ppt") || path.endsWith(".pptx")
                            || path.endsWith(".xls")
                    )) {
                        filePath = path;


                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Add Title To Your Upload")
                                .setCancelable(false);
                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadTitle = input.getText().toString();
                                if (uploadTitle.length() == 0) {
                                    Toast.makeText(getActivity(), "Title can't be empty...!!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    multiPartRequest();
                                    dialog.dismiss();
                                }
                            }
                        });


                    } else {
                        Toast.makeText(getActivity(), "Invalid file type selected ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No file selected ", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onResponseSuccess(Object response, int requestID) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            String code = jsonObject.getString("code");
            if (code.equals("0")) {
                Toast.makeText(getActivity(), "There was some error ...!!! Please try again later...!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "File successfully uploaded...!!!", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                uploadsSuccess = gson.fromJson(response.toString(), UploadSuccess.class);
                sendNotifications();
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void sendNotifications() {
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("alias_name", uploadsSuccess.alias_name);
            bodyObj.put("userId", uploadsSuccess.userID);
            bodyObj.put("fileName", uploadsSuccess.filename);
        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.SENDING_PUSH_NOTIFICATIONS_URL, bodyObj, null, null) {
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jRequest);

    }

    @Override
    public void onResponseError(VolleyError error, int requestID) {
        Toast.makeText(getActivity(), "Error occurred ...!!! please try again Later...!!!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();


        if (isOpen) {
            fabCamera.startAnimation(fabClose);
            fabGallery.startAnimation(fabClose);
            fabOthers.startAnimation(fabClose);
            fabChooseFile.startAnimation(fabRotateAntiClockwise);
            fabCamera.setClickable(false);
            fabGallery.setClickable(false);
            fabOthers.setClickable(false);
            isOpen = false;
        }
    }


    public static String getPath(final Context context, final Uri uri) {

        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private Bitmap checkForRotation(String filename, Bitmap bitmap) {
        Bitmap myBitmap = bitmap;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(filename);
            new ExifInterface(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                myBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                myBitmap = rotateImage(bitmap, 180);
                break;
        }
        return myBitmap;
    }

    private Bitmap rotateImage(Bitmap bitmap, int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    @Override
    public void refreshUploads() {

    }

    @Override
    public void onRefresh() {
        mUploadList.clear();
        pageNo = -1;
        doLoadMore = true;
        wallListAdapter.notifyDataSetChanged();
        lvWall.setVisibility(View.GONE);
        getListView();

        if (isOpen) {
            fabCamera.startAnimation(fabClose);
            fabGallery.startAnimation(fabClose);
            fabOthers.startAnimation(fabClose);
            fabChooseFile.startAnimation(fabRotateAntiClockwise);
            fabCamera.setClickable(false);
            fabGallery.setClickable(false);
            fabOthers.setClickable(false);
            isOpen = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/

            if (doLoadMore) {
                getListView();
                pbProgress.setVisibility(View.VISIBLE);
            }
        }
    }
}

