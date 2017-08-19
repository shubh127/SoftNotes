package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pbadmin on 21/5/17.
 */
public class WallInfo implements Parcelable {

    @SerializedName("userDetails")
    public SearchInfo userDetails;

    @SerializedName("uploadDetails")
    public UploadDetails uploadDetails;

    protected WallInfo(Parcel in) {
        userDetails = in.readParcelable(UserDetails.class.getClassLoader());
        uploadDetails = in.readParcelable(UploadDetails.class.getClassLoader());
    }

    public static final Creator<WallInfo> CREATOR = new Creator<WallInfo>() {
        @Override
        public WallInfo createFromParcel(Parcel in) {
            return new WallInfo(in);
        }

        @Override
        public WallInfo[] newArray(int size) {
            return new WallInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(userDetails, i);
        parcel.writeParcelable(uploadDetails, i);
    }
}
