package com.example.shubh.project.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Core.HttpResponseListener;
import com.example.shubh.project.Core.MultiPartRequest;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.AppUtil;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

public class EditProfile extends AppCompatActivity implements View.OnClickListener, HttpResponseListener {
    public static final int EDIT_PROFILE_CODE = 19002;
    private CircleImageView dp;
    private Dialog dialog;
    private TextView changePassword;
    private CameraPhoto cameraPhoto;
    private final int CAMERA_REQUEST = 13323;
    private final int GALLERY_REQUEST = 22131;
    private String photoPath = "";
    public static final int RESULT_OK = -1;
    private ImageButton close, ibDone;
    private EditText editProfileName, editProfileAliasName, editProfilePhone, editProfileCollege, editProfileEmail;
    private RadioButton rbMaleValue, rbFemaleValue;
    private TextView tvDobValue;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String year1 = String.valueOf(year);
            String month1 = String.valueOf(monthOfYear + 1);
            String day1 = String.valueOf(dayOfMonth);
            tvDobValue = (TextView) findViewById(R.id.tv_dob_value);
            tvDobValue.setText(day1 + "/" + month1 + "/" + year1);
        }
    };
    private UserInfo mUser;
    private String gender;
    private String profilePicLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        initViews();

    }

    private void initViews() {
        AppUtil.statusBarColorChange(EditProfile.this, ContextCompat.getColor(EditProfile.this, R.color.salmon));

        mUser = getIntent().getExtras().getParcelable(USER_INFO_KEY);

        cameraPhoto = new CameraPhoto(this);
        dp = (CircleImageView) findViewById(R.id.profile_image_edit);
        close = (ImageButton) findViewById(R.id.cancel);
        ibDone = (ImageButton) findViewById(R.id.ib_done);
        editProfileName = (EditText) findViewById(R.id.editProfileName);
        editProfileAliasName = (EditText) findViewById(R.id.editProfileAliasName);
        editProfilePhone = (EditText) findViewById(R.id.editProfilePhone);
        editProfileCollege = (EditText) findViewById(R.id.editProfileCollege);
        editProfileEmail = (EditText) findViewById(R.id.editProfileEmail);
        tvDobValue = (TextView) findViewById(R.id.tv_dob_value);
        rbFemaleValue = (RadioButton) findViewById(R.id.rb_female_value);
        rbMaleValue = (RadioButton) findViewById(R.id.rb_male_value);
        changePassword = (TextView) findViewById(R.id.change_password);

        TextView tv = (TextView) findViewById(R.id.editProfileHead);
        Typeface face = Typeface.createFromAsset(EditProfile.this.getAssets(),
                "Quikhand.ttf");
        tv.setTypeface(face);


        dp.setOnClickListener(this);
        close.setOnClickListener(this);
        ibDone.setOnClickListener(this);
        tvDobValue.setOnClickListener(this);
        changePassword.setOnClickListener(this);

        setValues();

    }


    private void setValues() {

        if (mUser.getAlias_name() != null && !mUser.getAlias_name().isEmpty()) {
            editProfileAliasName.setText(mUser.getAlias_name());
            editProfileAliasName.setEnabled(false);
            editProfileEmail.setFocusable(false);
        }
        if (mUser.getName() != null && !mUser.getName().isEmpty()) {
            editProfileName.setText(mUser.getName());
            editProfileName.setEnabled(false);
            editProfileEmail.setFocusable(false);
        }
        if (mUser.getPhone() != null && !mUser.getPhone().isEmpty()) {
            editProfileEmail.setFocusable(false);
            editProfilePhone.setText(mUser.getPhone());
        }
        if (mUser.getCollege() != null && !mUser.getCollege().isEmpty()) {
            editProfileEmail.setFocusable(false);
            editProfileCollege.setText(mUser.getCollege());
        }
        if (mUser.getEmail() != null && !mUser.getEmail().isEmpty()) {
            editProfileEmail.setText(mUser.getEmail());
            editProfileEmail.setEnabled(false);
            editProfileEmail.setFocusable(false);
        }
        if (mUser.getDateOfBirth() != null && !mUser.getDateOfBirth().isEmpty()) {
            tvDobValue.setText(mUser.getDateOfBirth());
        }
        if (mUser.getSex() != null && mUser.getSex().equalsIgnoreCase("Male")) {
            rbMaleValue.setChecked(true);
        }
        if (mUser.getSex() != null && mUser.getSex().equalsIgnoreCase("Female")) {
            rbFemaleValue.setChecked(true);
        }
        if (mUser.getProfilePicURL() != null && !mUser.getProfilePicURL().isEmpty()) {
            profilePicLink = mUser.getProfilePicURL();
            Picasso.with(this).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + profilePicLink).placeholder(R.drawable.place_holder).into(dp);
        }
    }

    String[] CAMERAPERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] GALLERYPERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int CAMERA_PERMISSION_CODE = 3;
    final int GALLERY_PERMISSION_CODE = 2;

    private void checkForCameraPermission() {

        if (!hasPermissions(EditProfile.this, CAMERAPERMISSIONS)) {
            ActivityCompat.requestPermissions(this, CAMERAPERMISSIONS, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void checkForGalleryPermission() {

        if (!hasPermissions(EditProfile.this, GALLERYPERMISSIONS)) {
            ActivityCompat.requestPermissions(this, GALLERYPERMISSIONS, GALLERY_PERMISSION_CODE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void openCamera() {
        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (IOException e) {
            Toast.makeText(EditProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }

            }

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
    public void onClick(View v) {
        if (v.getId() == R.id.profile_image_edit) {
            final CharSequence[] items = {"Take Photo", "Choose from Library", "Remove Photo", "Cancel"};
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditProfile.this);
            builder.setTitle("Change Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (items[which] == "Take Photo") {

                        checkForCameraPermission();

                    } else if (items[which] == "Choose from Library") {

                        checkForGalleryPermission();

                    } else if (items[which] == "Cancel") {

                        dialog.dismiss();

                    } else if (items[which] == "Remove Photo") {
                        removeDp();
                    }
                }
            });
            builder.setCancelable(false);
            builder.show();
        }

        if (v.getId() == R.id.cancel) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        if (v.getId() == R.id.tv_dob_value) {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    android.R.style.Theme_DeviceDefault_Dialog,
                    datePickerListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));

            datePicker.setCancelable(false);
            datePicker.setTitle("Select the date");

            datePicker.show();
        }

        if (v.getId() == R.id.ib_done) {

            if (editProfilePhone.getText().toString().trim().length() != 10) {
                Toast.makeText(EditProfile.this, "Phone Number must be of 10 diits", Toast.LENGTH_SHORT).show();
                editProfilePhone.setError("Field Required");
                editProfilePhone.requestFocus();
            } else {
                updateProfile();
            }
        }

        if (v.getId() == R.id.change_password) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(USER_INFO_KEY, mUser);
            Intent intent = new Intent(this, ChangePassword.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    private void removeDp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure ,you want to remove your picture ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestToRemoveDp();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void requestToRemoveDp() {
        final Dialog dialog = new Dialog(EditProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("alias_name", mUser.getAlias_name());

        } catch (JSONException e1) {
            Toast.makeText(EditProfile.this, "Something went wrong...Please try again later...!!!", Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.REMOVE_DP_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        Toast.makeText(EditProfile.this, "Something went wrong...Please try again later...!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(response.toString(), UserInfo.class);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("userInfo", userInfo);
                        setResult(Activity.RESULT_OK, returnIntent);
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

                Toast.makeText(EditProfile.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(EditProfile.this).addToRequestQueue(jRequest);
    }

    private void updateProfile() {
        if (tvDobValue.getText().toString().trim().equalsIgnoreCase("DD-MM-YYYY")) {
            Toast.makeText(this, "Please select your DOB", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(editProfilePhone.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your Phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(editProfileCollege.getText().toString().trim())) {
            Toast.makeText(this, "Please enter your College name", Toast.LENGTH_SHORT).show();
        } else if (!(rbFemaleValue.isChecked()) && (!rbMaleValue.isChecked())) {
            Toast.makeText(this, "Please select your gender !!!", Toast.LENGTH_SHORT).show();
        } else {
            updateProfileRequest();
        }
    }

    private void updateProfileRequest() {
        final Dialog dialog = new Dialog(EditProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("sex", rbFemaleValue.isChecked() ? "Female" : "Male");
            bodyObj.put("dob", tvDobValue.getText().toString().trim());
            bodyObj.put("phone", editProfilePhone.getText().toString().trim());
            bodyObj.put("college", editProfileCollege.getText().toString().trim());
            bodyObj.put("alias_name", editProfileAliasName.getText().toString().trim());
        } catch (JSONException e1) {

        }

        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.UPDATE_PROFILE_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(response.toString(), UserInfo.class);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("userInfo", userInfo);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Toast.makeText(EditProfile.this, "try again later", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(EditProfile.this, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(EditProfile.this).addToRequestQueue(jRequest);
    }

    private void hitMultipart() {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setBoundary("BOUNDARY");
            File f = new File(photoPath);
            Bitmap bm = checkForRotation(photoPath,
                    decodeSampledBitmapFromResource(photoPath, 1024, 1024));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(b);
            fos.flush();
            fos.close();
            FileBody fileBody = new FileBody(f);

            builder.addPart("profilePic", fileBody);
            builder.addPart("alias_name", new StringBody(editProfileAliasName.getText().toString().trim(), ContentType.TEXT_PLAIN));

            MultiPartRequest request = new MultiPartRequest(Request.Method.POST, "http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/uplod.php", 1, null, builder.build(), 300000, null, this);
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.layout_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            MySingleton.getInstance(this).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "OOps!! Something went wrong...!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                photoPath = cameraPhoto.getPhotoPath();
                uploadProfilePicRequest();
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).getBitmap();
                    dp.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(EditProfile.this, "Something went wrong while loading photo", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == GALLERY_REQUEST) {

                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn;
                    filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoPath = cursor.getString(columnIndex);
                    Bitmap bitmap = ImageLoader.init().from(photoPath).getBitmap();
                    dp.setImageBitmap(bitmap);
                    cursor.close();
                    uploadProfilePicRequest();
                } catch (Exception e) {
                    Toast.makeText(EditProfile.this, "Something went wrong while loading photo", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadProfilePicRequest() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure ,You wana change your profile picture?");


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitMultipart();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onResponseSuccess(Object response, int requestID) {
        dialog.dismiss();

        if (requestID == 1) {
            JSONObject jsonObject = null;
            String code;
            try {
                jsonObject = new JSONObject(response.toString());
                code = jsonObject.getString("code");
                if (code.equals("1")) {
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(response.toString(), UserInfo.class);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("userInfo", userInfo);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onResponseError(VolleyError error, int requestID) {
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rb_male_value:
                if (checked)
                    gender = "Male";
                break;
            case R.id.rb_female_value:
                if (checked)
                    gender = "Female";
                break;
        }
    }

    public static void Open(Activity mContext, UserInfo user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_INFO_KEY, user);
        Intent intent = new Intent(mContext, EditProfile.class);
        intent.putExtras(bundle);
        mContext.startActivityForResult(intent, EDIT_PROFILE_CODE);
        mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
