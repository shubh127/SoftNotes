package com.example.shubh.project.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.shubh.project.Interfaces.FieldValidator;
import com.example.shubh.project.R;

/**
 * Created by Shubh on 05-08-2016.
 */
public class FragmentD extends Fragment implements FieldValidator, View.OnClickListener {

    private ImageView ivShowHidePass;
    private EditText pass;
    private TextInputLayout layout;
    private boolean isHidden = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_d, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pass = (EditText) getView().findViewById(R.id.ed_pass);
        layout = (TextInputLayout) getView().findViewById(R.id.til2);
        ivShowHidePass = (ImageView) getView().findViewById(R.id.iv_show_hide_pass);

        ivShowHidePass.setOnClickListener(this);
    }

    @Override
    public boolean validate() {
        boolean result = false;
        if (pass.getText().toString().trim().length() >= 6) {
            result = true;
        }
        return result;
    }

    @Override
    public void showError() {
        layout.setError("       Password too short(Length>5)");
    }

    @Override
    public String getValue() {
        return pass.getText().toString().trim();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_show_hide_pass) {

            if (isHidden) {
                pass.setTransformationMethod(null);
                ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.show));
                isHidden = false;
            } else {
                pass.setTransformationMethod(new PasswordTransformationMethod());
                ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.hide));
                isHidden = true;
            }

        }
    }
}
