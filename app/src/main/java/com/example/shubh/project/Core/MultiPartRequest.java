package com.example.shubh.project.Core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.shubh.project.Core.HttpResponseListener;
import com.example.shubh.project.Core.InternalListener;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Class for making a Multi Part request, which uploads files in chunks.
 */

public class MultiPartRequest<T> extends Request<T>
{
    private Map<String, String> mRequestHeader;
    private Class<T> mModelClass;
    private final Gson gson = new Gson();
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE_MULTIPART = String.format("multipart/form-data; boundary=BOUNDARY; charset=%s", PROTOCOL_CHARSET);
    private InternalListener mInternalListener;

    private HttpEntity entity;

    private MultiPartRequest()
    {
        super(Method.GET, null, null);
    }

    /**
     * @param method     int specifying service type. ex: Request.Method.GET
     * @param url        URL to hit.
     * @param requestID  manually set id to keep track of the url being hit, it is used to keep track of response object and response error.
     * @param header     header to send, if any.
     * @param entity     HttpEntity Object for request body.
     * @param timeOut    timeOut in milliseconds.
     * @param modelClass GSON model class object to populate data, pass null if raw String response is required
     * @param listener   Custom response listener class object, return type of response is an Object which can further be cast to desired type using urlID.
     */
    public MultiPartRequest(int method, @NonNull String url, int requestID, @Nullable Map<String, String> header, @NonNull HttpEntity entity, int timeOut, @Nullable Class<T> modelClass, HttpResponseListener listener)
    {
        super(method, url, listener == null ? null : new InternalListener(listener, requestID));
        mInternalListener = (InternalListener) this.getErrorListener();
        mRequestHeader = header;
        mModelClass = modelClass;
        this.entity = entity;
        setRetryPolicy(new DefaultRetryPolicy(timeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * @param method     int specifying service type. ex: Request.Method.GET
     * @param url        URL to hit.
     * @param requestID  manually set id to keep track of the url being hit, it is used to keep track of response object and response error.
     * @param header     header to send, if any.
     * @param entity     HttpEntity Object for request body.
     * @param modelClass GSON model class object to populate data, pass null if raw String response is required
     * @param listener   Custom response listener class object, return type of response is an Object which can further be cast to desired type using urlID.
     */
    public MultiPartRequest(int method, @NonNull String url, int requestID, @Nullable Map<String, String> header, @NonNull HttpEntity entity, @Nullable Class<T> modelClass, HttpResponseListener listener)
    {
        this(method, url, requestID, header, entity, 120000, modelClass, listener);   /*default time is 120 secs*/
    }

    /**
     * Executes on a worker thread, parsing is done here.
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            if (mModelClass == null)
            {
                return Response.success((T) responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
            else
            {
                return Response.success(gson.fromJson(responseString, mModelClass), HttpHeaderParser.parseCacheHeaders(response));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        }
        catch (Exception e)
        {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Executes on the main thread;
     */
    @Override
    protected void deliverResponse(T response)
    {
        if (mInternalListener != null)
        {
            mInternalListener.onResponse(response);
        }
    }

//@Override
//    protected void onFinish()
//    {
//        super.onFinish();
//        mInternalListener = null;
//    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        return mRequestHeader != null ? mRequestHeader : super.getHeaders();
    }

    @Override
    public String getBodyContentType()
    {
        return PROTOCOL_CONTENT_TYPE_MULTIPART;
    }

    @Override
    public byte[] getBody()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }


    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError)
    {
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null)
        {
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        return volleyError;
    }
}
