package com.example.shubh.project.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Activity.LoginActivity;
import com.example.shubh.project.Activity.OpenFile;
import com.example.shubh.project.Activity.OpenImage;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Models.UploadInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.URLConstants;
import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shubh on 11-05-2017.
 */

public class GridviewAdapter extends BaseAdapter {

    private TextView tvUploadCount;
    private Activity activity;
    private List<UploadInfo> uploadArray;
    private boolean showRemoveButton;


    public GridviewAdapter(FragmentActivity activity, List<UploadInfo> uploadArray, boolean showRemoveButton, TextView uploadsCount) {
        this.activity = activity;
        this.uploadArray = uploadArray;
        this.showRemoveButton = showRemoveButton;
        tvUploadCount = uploadsCount;
    }

    @Override
    public int getCount() {

        if (uploadArray.size() == 0) {
            return 9;
        }

        return uploadArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        private ImageView ivGridView, recycleBin;
        private TextView tvGridView;
        public FiftyShadesOf fs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.gridview_row, null);

            view.tvGridView = (TextView) convertView.findViewById(R.id.tv_grid_view);
            view.ivGridView = (ImageView) convertView.findViewById(R.id.iv_grid_view);
            view.recycleBin = (ImageView) convertView.findViewById(R.id.dust_bin);
            view.fs = FiftyShadesOf.with(activity).
                    on(convertView) //views references
            ;
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        if (uploadArray.size() == 0) {
            view.ivGridView.setImageDrawable(null);
            convertView.setOnClickListener(null);
            view.fs.fadein(false).start();

        } else {
            view.fs.stop();
            view.tvGridView.setText(uploadArray.get(position).subjectName);
            if (uploadArray.get(position).fileType.equalsIgnoreCase("pdf")) {
                view.ivGridView.setImageResource(R.drawable.pdf);
            } else if (uploadArray.get(position).fileType.equalsIgnoreCase("txt")) {
                view.ivGridView.setImageResource(R.drawable.text);
            } else if (uploadArray.get(position).fileType.equalsIgnoreCase("doc") || uploadArray.get(position).fileType.equalsIgnoreCase("docs")) {
                view.ivGridView.setImageResource(R.drawable.doc);
            } else if (uploadArray.get(position).fileType.equalsIgnoreCase("ppt") || uploadArray.get(position).fileType.equalsIgnoreCase("pptx")) {
                view.ivGridView.setImageResource(R.drawable.ppt);
            } else if (uploadArray.get(position).fileType.equalsIgnoreCase("xls")) {
                view.ivGridView.setImageResource(R.drawable.exel);
            } else {
                Picasso.with(activity).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + uploadArray.get(position).filePath).placeholder(R.drawable.unknown).into(view.ivGridView);
            }

            if (showRemoveButton) {
                view.recycleBin.setVisibility(View.VISIBLE);
            } else {
                view.recycleBin.setVisibility(View.GONE);
            }

            view.recycleBin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("You sure? You want to delete this ?")
                            .setCancelable(false);


                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialogBox, int which) {

                            final Dialog dialog = new Dialog(activity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setContentView(R.layout.layout_dialog);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();


                            JSONObject bodyObj = new JSONObject();
                            try {
                                bodyObj.put("uploadID", uploadArray.get(position).uploadId);
                            } catch (JSONException e1) {

                            }
                            JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.REMOVE_UPLOAD_URL, bodyObj, new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        String code = jsonObject.getString("code");
                                        if (code.equals("0")) {
                                            Toast.makeText(activity, "Something went wrong ...!!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            UploadInfo uploadToRemove = null;
                                            String uploadID = jsonObject.getString("uploadID");
                                            for (UploadInfo uploadInfo : uploadArray) {
                                                if (uploadInfo.uploadId.equalsIgnoreCase(uploadID)) {
                                                    uploadToRemove = uploadInfo;
                                                    break;
                                                }
                                            }
                                            if (uploadToRemove != null) {
                                                uploadArray.remove(uploadToRemove);
                                                notifyDataSetChanged();
                                                Toast.makeText(activity, "File Successfully Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                            tvUploadCount.setText("" + uploadArray.size());
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
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
                            MySingleton.getInstance(activity).addToRequestQueue(jRequest);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogBox, int which) {
                            dialogBox.cancel();
                        }
                    });
                    final AlertDialog dialogBox = builder.create();
                    dialogBox.show();
                }
            });
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadArray.get(position).fileType.equalsIgnoreCase("pdf")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", uploadArray.get(position).filePath);
                    intent.putExtra("fileName", uploadArray.get(position).subjectName);
                    intent.putExtra("extension", uploadArray.get(position).fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (uploadArray.get(position).fileType.equalsIgnoreCase("txt")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", uploadArray.get(position).filePath);
                    intent.putExtra("fileName", uploadArray.get(position).subjectName);
                    intent.putExtra("extension", uploadArray.get(position).fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (uploadArray.get(position).fileType.equalsIgnoreCase("doc") || uploadArray.get(position).fileType.equalsIgnoreCase("docs")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", uploadArray.get(position).filePath);
                    intent.putExtra("fileName", uploadArray.get(position).subjectName);
                    intent.putExtra("extension", uploadArray.get(position).fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (uploadArray.get(position).fileType.equalsIgnoreCase("ppt") || uploadArray.get(position).fileType.equalsIgnoreCase("pptx")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", uploadArray.get(position).filePath);
                    intent.putExtra("fileName", uploadArray.get(position).subjectName);
                    intent.putExtra("extension", uploadArray.get(position).fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (uploadArray.get(position).fileType.equalsIgnoreCase("xls")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", uploadArray.get(position).filePath);
                    intent.putExtra("fileName", uploadArray.get(position).subjectName);
                    intent.putExtra("extension", uploadArray.get(position).fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (uploadArray.get(position).fileType.equalsIgnoreCase("jpg") || uploadArray.get(position).fileType.equalsIgnoreCase("jpeg") || uploadArray.get(position).fileType.equalsIgnoreCase("png")) {
                    Intent intent = new Intent(activity, OpenImage.class);
                    intent.putExtra("urlForWebView", uploadArray.get(position).filePath);
                    intent.putExtra("fileName", uploadArray.get(position).subjectName);
                    intent.putExtra("extension", uploadArray.get(position).fileType);

                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(activity, "Unknown file type...!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return convertView;
    }
}
