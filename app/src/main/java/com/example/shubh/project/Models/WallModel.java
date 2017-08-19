package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pbadmin on 21/5/17.
 */
public class WallModel implements Parcelable {
    @SerializedName("code")
    public int code;


    @SerializedName("uploadArray")
    public List<WallInfo> wallArray;

    protected WallModel(Parcel in) {
        code = in.readInt();
        wallArray = in.createTypedArrayList(WallInfo.CREATOR);
    }

    public static final Creator<WallModel> CREATOR = new Creator<WallModel>() {
        @Override
        public WallModel createFromParcel(Parcel in) {
            return new WallModel(in);
        }

        @Override
        public WallModel[] newArray(int size) {
            return new WallModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeTypedList(wallArray);
    }
}
