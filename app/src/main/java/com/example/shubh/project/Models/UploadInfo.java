package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shubh on 11-05-2017.
 */

public class UploadInfo implements Parcelable{
    @SerializedName("uploadId")
    public String uploadId;

    @SerializedName("subjectName")
    public String subjectName;

    @SerializedName("fileType")
    public String fileType;

    @SerializedName("filePath")
    public String filePath;

    @SerializedName("block")
    public String block;

    @SerializedName("removedMaterial")
    public String removedMaterial;

    @SerializedName("timeStamp")
    public String timeStamp;

    protected UploadInfo(Parcel in) {
        uploadId = in.readString();
        subjectName = in.readString();
        fileType = in.readString();
        filePath = in.readString();
        block = in.readString();
        removedMaterial = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<UploadInfo> CREATOR = new Creator<UploadInfo>() {
        @Override
        public UploadInfo createFromParcel(Parcel in) {
            return new UploadInfo(in);
        }

        @Override
        public UploadInfo[] newArray(int size) {
            return new UploadInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uploadId);
        dest.writeString(subjectName);
        dest.writeString(fileType);
        dest.writeString(filePath);
        dest.writeString(block);
        dest.writeString(removedMaterial);
        dest.writeString(timeStamp);
    }
}
