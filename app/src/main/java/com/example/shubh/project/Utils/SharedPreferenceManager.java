package com.example.shubh.project.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.example.shubh.project.BuildConfig;

import java.util.Set;

/**
 * Helper class for SharedPreferences, it has all the basic functionality, it can be modified according to use case.
 */
public class SharedPreferenceManager {
    private String sharedPrefsFile = BuildConfig.APPLICATION_ID;
    private Editor mEditor;
    private SharedPreferences mPreferences;
    private Context mContext;

    public static final String P_ALIAS_NAME = "P_ALIAS_NAME";
    public static final String P_PASSWORD = "P_PASSWORD";
    public static final String FCM_TOKEN = "FCM_TOKEN";

    private SharedPreferenceManager() {
        /**
         * Private constructor
         */
    }

    /**
     * @param context  Current context.
     * @param fileName name of the file to use, passing null will use default name specified.
     */
    public SharedPreferenceManager(@NonNull Context context, @Nullable String fileName) {
        mContext = context;
        sharedPrefsFile = (fileName == null) ? BuildConfig.APPLICATION_ID : (BuildConfig.APPLICATION_ID + "." + fileName);
        initEditor();
    }

    /**
     * gets invoked internally from the constructor.
     */
    private void initEditor() {
        mPreferences = mContext.getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public SharedPreferences getPreferences() {
        return mPreferences;
    }

    public Editor getEditor() {
        return mEditor;
    }

    public void setBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public void setString(String key, String value) {
        mEditor.putString(key, value.trim());
        mEditor.commit();
    }

    public void setInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public void setFloat(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.commit();
    }

    public void setLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public void setStringSet(String key, Set<String> value) {
        mEditor.putStringSet(key, value);
        mEditor.commit();
    }

    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void clearPreferences() {
        mEditor.clear().commit();
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public Long getLong(String key, Long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return mPreferences.getStringSet(key, defValue);
    }

    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    public static void RemoveAll(Context con) {
//
//        SharedPreferenceManager spm = new SharedPreferenceManager(con, null);
//        spm.setString(STORE_KEY, "");
//        spm.setString(STORE_CITY, "");
//        spm.setString(STORE_PIN, "");
//        spm.setString(NAME_KEY, "");
//        spm.setString(USER_ID_KEY, "");
//        spm.setString(EMAIL_KEY, "");
//        spm.setString(PWD_KEY, "");
//        spm.setString(PHONE_KEY, "");
//        spm.setLong(CART_UPDATION_TIME, 0);
//        spm.setBoolean(SharedPreferenceManager.DO_DELETE_CART, false);

    }


}
