package com.example.shubh.project.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shubh.project.Activity.OpenFile;
import com.example.shubh.project.Activity.OpenImage;
import com.example.shubh.project.Activity.UserProfile;
import com.example.shubh.project.Models.WallInfo;
import com.example.shubh.project.R;
import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by pbadmin on 21/5/17.
 */
public class WallListAdapter extends BaseAdapter {
    private Activity activity;
    private List<WallInfo> mUploadList;

    public WallListAdapter(FragmentActivity activity, List<WallInfo> mUploadList) {
        this.activity = activity;
        this.mUploadList = mUploadList;

    }

    @Override
    public int getCount() {

        if (mUploadList.size() == 0) {
            return 7;
        }
        return mUploadList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder {
        public CircleImageView civDisplayPicture;
        public ImageView ivFileType;
        public TextView tvName, tvFileName, tvTime;
        public FiftyShadesOf fs;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        long timeInMilliseconds = 0;
        LayoutInflater inflator = activity.getLayoutInflater();

        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.lv_wall_ui, parent, false);

            view.civDisplayPicture = (CircleImageView) convertView.findViewById(R.id.civ_display_picture);
            view.ivFileType = (ImageView) convertView.findViewById(R.id.iv_file_type);
            view.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            view.tvFileName = (TextView) convertView.findViewById(R.id.tv_file_nAme);
            view.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            view.fs = FiftyShadesOf.with(activity).
                    on(convertView) //views references
            ;
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        if (mUploadList.size() == 0) {
            view.civDisplayPicture.setImageDrawable(null);
            view.ivFileType.setImageDrawable(null);
            convertView.setOnClickListener(null);
            view.fs.fadein(false).start();

        } else {
            view.fs.stop();
            Picasso.with(activity).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + mUploadList.get(position).userDetails.profPicURL).placeholder(R.drawable.place_holder).into(view.civDisplayPicture);
            if (Build.VERSION.SDK_INT >= 24) {
                view.tvName.setText(Html.fromHtml("<i><b>" + mUploadList.get(position).userDetails.name + "</i></b>" + " has uploaded a file ", Html.FROM_HTML_MODE_LEGACY));
                view.tvFileName.setText(Html.fromHtml("Named - " + "<i><b>" + mUploadList.get(position).uploadDetails.subjectName + "." + mUploadList.get(position).uploadDetails.fileType + "</b></i>", Html.FROM_HTML_MODE_LEGACY));

            } else {
                view.tvName.setText(Html.fromHtml("<i><b>" + mUploadList.get(position).userDetails.name + "</i></b>" + " has uploaded a file "));
                view.tvFileName.setText(Html.fromHtml("Named - " + "<i><b>" + mUploadList.get(position).uploadDetails.subjectName + "." + mUploadList.get(position).uploadDetails.fileType + "</b></i>"));

            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date mDate = sdf.parse(mUploadList.get(position).uploadDetails.timeStamp);
                timeInMilliseconds = mDate.getTime();
                String timeSince = DateUtils.getRelativeTimeSpanString(timeInMilliseconds, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
                view.tvTime.setText(timeSince);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("pdf")) {
                view.ivFileType.setImageResource(R.drawable.pdf);
            } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("txt")) {
                view.ivFileType.setImageResource(R.drawable.text);
            } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("doc") || mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("docs")) {
                view.ivFileType.setImageResource(R.drawable.doc);
            } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("ppt") || mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("pptx")) {
                view.ivFileType.setImageResource(R.drawable.ppt);
            } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("xls")) {
                view.ivFileType.setImageResource(R.drawable.exel);
            } else {
                Picasso.with(activity).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + mUploadList.get(position).uploadDetails.filePath).placeholder(R.drawable.unknown).into(view.ivFileType);
            }
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("pdf")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", mUploadList.get(position).uploadDetails.filePath);
                    intent.putExtra("fileName", mUploadList.get(position).uploadDetails.subjectName);
                    intent.putExtra("extension", mUploadList.get(position).uploadDetails.fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("txt")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", mUploadList.get(position).uploadDetails.filePath);
                    intent.putExtra("fileName", mUploadList.get(position).uploadDetails.subjectName);
                    intent.putExtra("extension", mUploadList.get(position).uploadDetails.fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("doc") || mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("docs")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", mUploadList.get(position).uploadDetails.filePath);
                    intent.putExtra("fileName", mUploadList.get(position).uploadDetails.subjectName);
                    intent.putExtra("extension", mUploadList.get(position).uploadDetails.fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("ppt") || mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("pptx")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", mUploadList.get(position).uploadDetails.filePath);
                    intent.putExtra("fileName", mUploadList.get(position).uploadDetails.subjectName);
                    intent.putExtra("extension", mUploadList.get(position).uploadDetails.fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("xls")) {
                    Intent intent = new Intent(activity, OpenFile.class);
                    intent.putExtra("urlForWebView", mUploadList.get(position).uploadDetails.filePath);
                    intent.putExtra("fileName", mUploadList.get(position).uploadDetails.subjectName);
                    intent.putExtra("extension", mUploadList.get(position).uploadDetails.fileType);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("jpg") || mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("jpeg") || mUploadList.get(position).uploadDetails.fileType.equalsIgnoreCase("png")) {
                    Intent intent = new Intent(activity, OpenImage.class);
                    intent.putExtra("urlForWebView", mUploadList.get(position).uploadDetails.filePath);
                    intent.putExtra("fileName", mUploadList.get(position).uploadDetails.subjectName);
                    intent.putExtra("extension", mUploadList.get(position).uploadDetails.fileType);

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
