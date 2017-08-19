package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pbadmin on 21/5/17.
 */
public class UserDetails implements Parcelable {

    @SerializedName("sex")
    public String sex;

    @SerializedName("alias_name")
    public String alias_name;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("college")
    public String college;

    @SerializedName("profPicURL")
    public String profilePicURL;

    @SerializedName("dateOfBirth")
    public String dateOfBirth;

    @SerializedName("phone")
    public String phone;

    @SerializedName("userID")
    public String userID;

    protected UserDetails(Parcel in) {
        sex = in.readString();
        alias_name = in.readString();
        name = in.readString();
        email = in.readString();
        college = in.readString();
        profilePicURL = in.readString();
        dateOfBirth = in.readString();
        phone = in.readString();
        userID = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sex);
        parcel.writeString(alias_name);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(college);
        parcel.writeString(profilePicURL);
        parcel.writeString(dateOfBirth);
        parcel.writeString(phone);
        parcel.writeString(userID);
    }
}
