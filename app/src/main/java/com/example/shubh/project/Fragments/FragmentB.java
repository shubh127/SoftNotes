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

/**
 * Created by Shubh on 05-08-2016.
 */
public class FragmentB extends Fragment implements FieldValidator {
    private EditText email;
    private TextInputLayout emailLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        email = (EditText) getView().findViewById(R.id.ed_pass);
        emailLayout = (TextInputLayout) getView().findViewById(R.id.til2);
    }


    @Override
    public boolean validate() {
        boolean result = false;
        if (email.getText().toString().trim().length() >= 6 && !email.getText().toString().trim().contains(" ")) {
            result = true;
        }
        return result;
    }

    @Override
    public void showError() {
        emailLayout.setError("       Too short ( length>6 and no spaces anywhere)");
    }

    @Override
    public String getValue() {
        return email.getText().toString().trim();
    }
}
