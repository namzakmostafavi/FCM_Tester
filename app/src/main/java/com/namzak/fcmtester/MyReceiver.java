package com.namzak.fcmtester;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.namzak.fcmtester.http.HttpRequest;
import com.namzak.fcmtester.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class MyReceiver extends WakefulBroadcastReceiver {

    private MyLogs myLogs;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (myLogs == null)
            myLogs = new MyLogs(context);

        try {
            String body = intent.getStringExtra("body");
            JSONObject jBody = new JSONObject(body);
            final long pingId = jBody.optLong("pingId");
            myLogs.info("receiving message with pingId= " + pingId);

            String url = MainActivity.BASE_URL + "/FcmAck";
            JSONObject json = new JSONObject();
            try {
                @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                json.put("pingId", pingId);
                json.put("androidId", android_id);
                json.put("name", Build.BOARD + " " + Build.MODEL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HttpRequest().Post(url, json.toString()).setOnResultListener(new HttpRequest.OnResultListener() {
                @Override
                public void onFailure() {
                    myLogs.info("Network Error! sending Ack with pingId " + pingId);
                }

                @Override
                public void onPreRequest() {

                }

                @Override
                public void onResponse(HttpResponse response) {
                    myLogs.info("Sending ACK message = " + response.getResult());
                }
            }).execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
