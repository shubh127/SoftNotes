package com.example.shubh.project.Fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shubh.project.Activity.Dashboard;
import com.example.shubh.project.Activity.LoginActivity;
import com.example.shubh.project.Adapters.SearchListViewAdapter;
import com.example.shubh.project.Core.MySingleton;
import com.example.shubh.project.Interfaces.PageOpened;
import com.example.shubh.project.Models.SearchInfo;
import com.example.shubh.project.Models.SearchModel;
import com.example.shubh.project.Models.UserInfo;
import com.example.shubh.project.R;
import com.example.shubh.project.Utils.URLConstants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

public class FragmentSearch extends Fragment implements View.OnClickListener, PageOpened {
    private Button btSearch;
    private EditText etSearch;
    private Dashboard mContext;
    private ListView lvSearch;
    private SearchListViewAdapter mAdapter;
    private List<SearchInfo> mUploadList = new ArrayList<SearchInfo>();
    private TextView tvNoResult;
    private UserInfo userInfo;
    private Timer timer;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userInfo = getArguments().getParcelable(USER_INFO_KEY);
        initViews();
    }

    private void initViews() {
        mContext = (Dashboard) getActivity();
        btSearch = (Button) getActivity().findViewById(R.id.bt_search);
        etSearch = (EditText) getActivity().findViewById(R.id.et_search);
        lvSearch = (ListView) getActivity().findViewById(R.id.lv_search);
        tvNoResult = (TextView) getActivity().findViewById(R.id.tv_noresult);

        mAdapter = new SearchListViewAdapter(getActivity(), mUploadList, userInfo.getUserID());
        lvSearch.setAdapter(mAdapter);
        lvSearch.setVisibility(View.GONE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (etSearch.getText().toString().length() > 2) {
                                    hitRequestToSearch();
                                } else {
                                    mUploadList.clear();
                                    lvSearch.setVisibility(View.GONE);
                                    mAdapter.notifyDataSetChanged();
                                }
                                timer.cancel();
                            }
                        });
                    }
                }, 1000);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_search) {
            if (etSearch.getText().toString().trim().length() == 0) {
                Toast.makeText(mContext, "Type something to search ...cant be empty!!!", Toast.LENGTH_SHORT).show();
            } else {
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.layout_dialog);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                hitRequestToSearch();
            }
        }
    }

    private void hitRequestToSearch() {

        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("searchContent", etSearch.getText().toString().trim());
            bodyObj.put("userID", userInfo.getUserID().toString().trim());
        } catch (JSONException e1) {

        }
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URLConstants.GET_SEARCH_RESULTS_URL, bodyObj, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

                Gson gson = new Gson();
                SearchModel searchModel = gson.fromJson(response.toString(), SearchModel.class);
                if (searchModel.code == 1) {
                    mUploadList.clear();
                    mUploadList.addAll(searchModel.uploadArray);
                    mAdapter.notifyDataSetChanged();
                    tvNoResult.setVisibility(View.GONE);
                } else {
                    lvSearch.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                lvSearch.setVisibility(View.GONE);
                Toast.makeText(mContext, "Error...Check internet connectivity..!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };
        jRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        MySingleton.getInstance(mContext).addToRequestQueue(jRequest);
        lvSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshUploads() {
        etSearch.setText("");
        mUploadList.clear();
        lvSearch.setVisibility(View.GONE);
        tvNoResult.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        if (tvNoResult.getVisibility() == View.VISIBLE) {
            tvNoResult.setVisibility(View.INVISIBLE);
        }
    }
}

