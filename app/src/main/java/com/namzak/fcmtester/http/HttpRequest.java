package com.namzak.fcmtester.http;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;


import com.namzak.fcmtester.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mohammadloo on 2017-05-31.
 */

public class HttpRequest {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private String authorizationToken = "";
    private HttpResponse httpResponse = new HttpResponse();
    private OkHttpClient client = new OkHttpClient();
    private FormBody.Builder builder = new FormBody.Builder();
    private Request request;
    private String mUrl = "";
    private ContentValues contentValues;
    private String jsonValue;
    private Request.Builder requestBuilder = new Request.Builder();
    private OnResultListener mResultListener;

    public HttpRequest() {
    }

    public final HttpRequest setAuthToken(String token) {
        this.authorizationToken = token;
        return this;
    }

    public final HttpRequest Get(String url) {
        this.mUrl = url;
        return this;
    }

    public final HttpRequest Post(String url, ContentValues values) {
        this.mUrl = url;
        contentValues = values;
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }
        RequestBody body = builder.build();
        requestBuilder.post(body);
        return this;
    }

    public final HttpRequest Post(String url, String json) {
        this.mUrl = url;
        jsonValue = json;
        RequestBody body = RequestBody.create(JSON, json);
        requestBuilder.post(body);
        return this;
    }


    public final HttpRequest Headers(ContentValues values) {
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        return this;
    }

    public final HttpRequest Delete(String url, ContentValues values) {
        this.mUrl = url;
        contentValues = values;
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }
        RequestBody body = builder.build();
        requestBuilder.delete(body);
        return this;
    }

    public void execute() {

        if (mResultListener != null)
            mResultListener.onPreRequest();

        if (!authorizationToken.equals("")) {
            requestBuilder
                    .url(mUrl)
//                    .addHeader("User-Agent", Build.BRAND + " / " + Build.MODEL + " / " + Build.VERSION.SDK_INT)
                    .addHeader("Authorization", "Token " + authorizationToken);
        } else {
            requestBuilder
                    .url(mUrl);
//                    .addHeader("User-Agent", Build.BRAND + " / " + Build.MODEL + " / " + Build.VERSION.SDK_INT);
        }
        request = requestBuilder.build();
        client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (BuildConfig.DEBUG) {
                    if (contentValues != null)
                        Log.d("@HttpRequest-reponse", mUrl + " - " + authorizationToken + " - " + contentValues.toString() + " - " + e.getMessage());
                    else if (!jsonValue.isEmpty())
                        Log.d("@HttpRequest-reponse", mUrl + " - " + authorizationToken + " - " + jsonValue + " - " + e.getMessage());
                    else
                        Log.d("@HttpRequest-reponse", mUrl + " - " + authorizationToken + " - " + e.getMessage());
                } else if (mResultListener != null)
                    mResultListener.onFailure();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                httpResponse.setResult(response.body().string());
                httpResponse.setStatus(response.code());
                if (BuildConfig.DEBUG && httpResponse.getResult() != null) {
                    String msg = "";
                    if (contentValues != null)
                        msg = mUrl + " - " + authorizationToken + " - " + response.code() + " - " + contentValues.toString() + " - " + httpResponse.getResult();
                    else if (!jsonValue.isEmpty())
                        msg = mUrl + " - " + authorizationToken + " - " + response.code() + " - " + jsonValue + " - " + httpResponse.getResult();
                    else
                        msg = mUrl + " - " + authorizationToken + " - " + response.code() + " - " + httpResponse.getResult();
                    Log.d("@HttpRequest-reponse", msg);
                }
                if (mResultListener != null)
                    mResultListener.onResponse(httpResponse);
            }
        });
    }

    public interface OnResultListener {
        void onFailure();

        void onPreRequest();

        void onResponse(HttpResponse response);
    }

    public HttpRequest setOnResultListener(OnResultListener eventListener) {
        mResultListener = eventListener;
        return this;
    }

}
