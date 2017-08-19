package com.example.shubh.project.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by pbadmin on 21/5/17.
 */
public class ConnectionsAdapter extends BaseAdapter {
    private Activity activity;
    private String myID;
    private List<SearchInfo> uploadArray;

    public ConnectionsAdapter(Activity activity, String myID, List<SearchInfo> uploadArray) {
        this.activity = activity;
        this.myID = myID;
        this.uploadArray = uploadArray;

    }

    @Override
    public int getCount() {
        if (uploadArray.size() == 0) {
            return 5;
        }
        return uploadArray.size();
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
        private CircleImageView civConnectionList;
        private TextView tvConnectionList;
        public FiftyShadesOf fs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.connection_list_ui, null);

            view.civConnectionList = (CircleImageView) convertView.findViewById(R.id.civ_connection_list);
            view.tvConnectionList = (TextView) convertView.findViewById(R.id.tv_connection_list);
            view.fs = FiftyShadesOf.with(activity).
                    on(convertView) //views references
            ;

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        if (uploadArray.size() == 0) {
            view.civConnectionList.setImageDrawable(null);
            convertView.setOnClickListener(null);
            view.fs.fadein(false).start();

        } else {
            view.fs.stop();
            view.tvConnectionList.setText(uploadArray.get(position).name);
            Picasso.with(activity).load("http://ec2-54-213-147-33.us-west-2.compute.amazonaws.com/" + uploadArray.get(position).profPicURL).placeholder(R.drawable.placeholder).into(view.civConnectionList);

        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("userProfileData", uploadArray.get(position));
                bundle.putString("myID", myID);
                Intent intent = new Intent(activity, UserProfile.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return convertView;
    }
}
