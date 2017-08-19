package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shubham on 04-06-2017.
 */

public class UploadSuccess implements Parcelable {

    @SerializedName("code")
    public String code;

    @SerializedName("userID")
    public String userID;

    @SerializedName("filename")
    public String filename;

    @SerializedName("alias_name")
    public String alias_name;

    protected UploadSuccess(Parcel in) {
        code = in.readString();
        userID = in.readString();
        filename = in.readString();
        alias_name = in.readString();
    }

    public static final Creator<UploadSuccess> CREATOR = new Creator<UploadSuccess>() {
        @Override
        public UploadSuccess createFromParcel(Parcel in) {
            return new UploadSuccess(in);
        }

        @Override
        public UploadSuccess[] newArray(int size) {
            return new UploadSuccess[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(code);
        parcel.writeString(userID);
        parcel.writeString(filename);
        parcel.writeString(alias_name);
    }
}
