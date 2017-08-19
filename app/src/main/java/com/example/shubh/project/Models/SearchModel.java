package com.example.shubh.project.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Shubh on 14-05-2017.
 */

public class SearchModel implements Parcelable {

    @SerializedName("code")
    public int code;


    @SerializedName("uploadArray")
    public List<SearchInfo> uploadArray;

    protected SearchModel(Parcel in) {
        code = in.readInt();
        uploadArray = in.createTypedArrayList(SearchInfo.CREATOR);
    }

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel in) {
            return new SearchModel(in);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeTypedList(uploadArray);
    }
}
