package com.example.shubh.project.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shubh.project.Activity.UserProfile;
import com.example.shubh.project.Models.SearchInfo;
import com.example.shubh.project.R;
import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shubh on 15-05-2017.
 */

public class SearchListViewAdapter extends BaseAdapter {

    private Activity activity;
    private List<SearchInfo> uploadArray;
    private String myID;

    public SearchListViewAdapter(FragmentActivity activity, List<SearchInfo> uploadArray, String userID) {
        this.activity = activity;
        this.uploadArray = uploadArray;
        myID = userID;


    }

    @Override
    public int getCount() {

        if (uploadArray.size() == 0) {
            return 5;
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

        public CircleImageView civSearchList;
        public TextView tvName, tvAliasName, tvEmail, tvUploadsCount;
        public FiftyShadesOf fs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.search_listview_row, parent, false);

            view.civSearchList = (CircleImageView) convertView.findViewById(R.id.list_view_img);
            view.tvAliasName = (TextView) convertView.findViewById(R.id.search_alias_name);
            view.tvName = (TextView) convertView.findViewById(R.id.search_name);
            view.tvEmail = (TextView) convertView.findViewById(R.id.search_email);
            view.tvUploadsCount = (TextView) convertView.findViewById(R.id.search_upload_count);
            view.fs = FiftyShadesOf.with(activity).
                    on(view.civSearchList, view.tvAliasName, view.tvName, view.tvEmail, view.tvUploadsCount) //views references
            ;
            convertView.setTag(view);

        } else {
            view = (ViewHolder) convertView.getTag();
        }
        if (uploadArray.size() == 0) {
            view.civSearchList.setImageDrawable(null);
            convertView.setOnClickListener(null);
            view.fs.fadein(false).start();

        } else {

            view.fs.stop();
            Picasso.with(activity).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + uploadArray.get(position).profPicURL).placeholder(R.drawable.place_holder).into(view.civSearchList);
            view.tvAliasName.setText(uploadArray.get(position).alias_name);
            view.tvName.setText(uploadArray.get(position).name);
            view.tvEmail.setText(uploadArray.get(position).email);
            view.tvUploadsCount.setText("Uploads : " + uploadArray.get(position).fileCount);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("userProfileData", uploadArray.get(position));
                    bundle.putString("myID", myID);
                    Intent intent = new Intent(activity, UserProfile.class);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }

        return convertView;
    }
}
