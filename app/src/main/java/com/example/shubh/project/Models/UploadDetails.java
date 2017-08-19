package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pbadmin on 21/5/17.
 */
public class UploadDetails implements Parcelable {

    @SerializedName("fileType")
    public String fileType;

    @SerializedName("subjectName")
    public String subjectName;

    @SerializedName("timeStamp")
    public String timeStamp;

    @SerializedName("uploadId")
    public String UploadId;

    @SerializedName("filePath")
    public String filePath;

    protected UploadDetails(Parcel in) {
        fileType = in.readString();
        subjectName = in.readString();
        timeStamp = in.readString();
        UploadId = in.readString();
        filePath = in.readString();
    }

    public static final Creator<UploadDetails> CREATOR = new Creator<UploadDetails>() {
        @Override
        public UploadDetails createFromParcel(Parcel in) {
            return new UploadDetails(in);
        }

        @Override
        public UploadDetails[] newArray(int size) {
            return new UploadDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileType);
        parcel.writeString(subjectName);
        parcel.writeString(timeStamp);
        parcel.writeString(UploadId);
        parcel.writeString(filePath);
    }
}
