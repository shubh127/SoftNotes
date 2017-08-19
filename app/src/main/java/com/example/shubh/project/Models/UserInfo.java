package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shubh on 15-11-2016.
 */

public class UserInfo implements Parcelable {

    @SerializedName("userID")
    private String userID;

    @SerializedName("alias_name")
    private String alias_name;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("block")
    private String block;

    @SerializedName("profilePicURL")
    private String profilePicURL;

    @SerializedName("sex")
    private String sex;

    @SerializedName("dobOfBirth")
    private String dateOfBirth;

    @SerializedName("phone")
    private String phone;

    @SerializedName("college")
    private String college;

    @SerializedName("timeStamp")
    private String timeStamp;

    public UserInfo(Parcel in) {
        userID = in.readString();
        alias_name = in.readString();
        name = in.readString();
        email = in.readString();
        block = in.readString();
        profilePicURL = in.readString();
        sex = in.readString();
        dateOfBirth = in.readString();
        phone = in.readString();
        college = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public UserInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(alias_name);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(block);
        dest.writeString(profilePicURL);
        dest.writeString(sex);
        dest.writeString(dateOfBirth);
        dest.writeString(phone);
        dest.writeString(college);
        dest.writeString(timeStamp);
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAlias_name() {
        return alias_name;
    }

    public void setAlias_name(String alias_name) {
        this.alias_name = alias_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
