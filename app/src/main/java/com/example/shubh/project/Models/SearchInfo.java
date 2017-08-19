package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shubh on 15-05-2017.
 */

public class SearchInfo implements Parcelable {

    @SerializedName("alias_name")
    public String alias_name;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("profPicURL")
    public String profPicURL;

    @SerializedName("dateOfBirth")
    public String dateOfBirth;

    @SerializedName("sex")
    public String sex;

    @SerializedName("phone")
    public String phone;

    @SerializedName("college")
    public String college;

    @SerializedName("fileCount")
    public String fileCount;

    @SerializedName("userID")
    public String userID;

    protected SearchInfo(Parcel in) {
        alias_name = in.readString();
        name = in.readString();
        email = in.readString();
        profPicURL = in.readString();
        dateOfBirth = in.readString();
        sex = in.readString();
        phone = in.readString();
        college = in.readString();
        fileCount = in.readString();
        userID = in.readString();

    }

    public static final Creator<SearchInfo> CREATOR = new Creator<SearchInfo>() {
        @Override
        public SearchInfo createFromParcel(Parcel in) {
            return new SearchInfo(in);
        }

        @Override
        public SearchInfo[] newArray(int size) {
            return new SearchInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(alias_name);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(profPicURL);
        dest.writeString(dateOfBirth);
        dest.writeString(sex);
        dest.writeString(phone);
        dest.writeString(college);
        dest.writeString(fileCount);
        dest.writeString(userID);
    }
}
