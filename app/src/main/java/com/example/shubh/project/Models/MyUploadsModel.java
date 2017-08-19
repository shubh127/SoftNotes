package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Shubh on 11-05-2017.
 */

public class MyUploadsModel implements Parcelable {

    @SerializedName("code")
    public int code;

    @SerializedName("count")
    public int count;

    @SerializedName("uploadArray")
    public List<UploadInfo> uploadArray;

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(String followerCount) {
        this.followerCount = followerCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    @SerializedName("isFollowing")
    public String isFollowing;

    @SerializedName("followerCount")
    public String followerCount;

    @SerializedName("followingCount")
    public String followingCount;

    protected MyUploadsModel(Parcel in) {
        code = in.readInt();
        count = in.readInt();
        uploadArray = in.createTypedArrayList(UploadInfo.CREATOR);
        isFollowing = in.readString();
        followerCount = in.readString();
        followingCount = in.readString();

    }

    public static final Creator<MyUploadsModel> CREATOR = new Creator<MyUploadsModel>() {
        @Override
        public MyUploadsModel createFromParcel(Parcel in) {
            return new MyUploadsModel(in);
        }

        @Override
        public MyUploadsModel[] newArray(int size) {
            return new MyUploadsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeInt(count);
        dest.writeTypedList(uploadArray);
    }
}
