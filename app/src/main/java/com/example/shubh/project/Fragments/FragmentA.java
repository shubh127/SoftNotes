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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shubh on 05-08-2016.
 */
public class FragmentA extends Fragment implements FieldValidator {
    private EditText fname;
    private TextInputLayout emailLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fname = (EditText) getView().findViewById(R.id.ed_pass);
        emailLayout = (TextInputLayout) getView().findViewById(R.id.til2);
    }

    @Override
    public boolean validate() {

        boolean result = false;
        String validemail = "[a-zA-Z ]+";
        String Email = fname.getText().toString();
    Matcher matcherObj = Pattern.compile(validemail).matcher(Email);
        if (matcherObj.matches()) {
                result = true;
                }
                return result;
                }

@Override
public void showError() {
        emailLayout.setError("       Invalid Name(Only alphabets and spaces)");
        }

@Override
public String getValue() {
        return fname.getText().toString().trim();
        }
        }
