package com.example.shubh.project.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.shubh.project.Interfaces.FieldValidator;
import com.example.shubh.project.R;

import org.apache.http.util.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shubh on 05-08-2016.
 */
public class FragmentC extends Fragment implements FieldValidator {
    private EditText num;
    private TextInputLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_c, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        num = (EditText) getView().findViewById(R.id.ed_pass);
        layout = (TextInputLayout) getView().findViewById(R.id.til2);
    }

    @Override
    public boolean validate() {
        boolean result = false;
        String Email = num.getText().toString();
        if (!TextUtils.isEmpty(Email) && android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            result = true;
        }
        return result;
    }

    @Override
    public void showError() {
        layout.setError("       Invalid Email Pattern (something@domain.com)");
    }

    @Override
    public String getValue() {
        return num.getText().toString().trim();
    }
}