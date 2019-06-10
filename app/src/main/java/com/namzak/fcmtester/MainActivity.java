package com.namzak.fcmtester;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.namzak.fcmtester.http.HttpRequest;
import com.namzak.fcmtester.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyFCM-MainActivity";
    public static final String BASE_URL = "http://cars.microlino.com:50981/udpservice";
//    public static final String BASE_URL = "http://10.0.2.2:8080";

    private MyLogs myLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (myLogs == null)
            myLogs = new MyLogs(this);
        sendFCMToken();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {     //  Prompt the user to disable battery optimization
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                startActivity(intent);
//            }
//        }

        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StringBuilder text = new StringBuilder();
                    BufferedReader br = new BufferedReader(new FileReader(getFilesDir().getPath() + "/logs.txt"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();

                    TextView tv = findViewById(R.id.tvText);
                    tv.setText(text.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir().getPath() + "/logs.txt");
                if (file.exists())
                    file.delete();
            }
        });

    }

    private void sendFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d(TAG, "token = " + token);
                        String url = MainActivity.BASE_URL + "/FCMToken";
                        JSONObject json = new JSONObject();
                        try {
                            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getContentResolver(),
                                    Settings.Secure.ANDROID_ID);
                            json.put("token", token);
                            json.put("androidId", android_id);
                            json.put("name", Build.BOARD + " " + Build.MODEL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new HttpRequest().Post(url, json.toString()).setOnResultListener(new HttpRequest.OnResultListener() {
                            @Override
                            public void onFailure() {
                                myLogs.info("Network Error! sending fcm token to server");
                            }

                            @Override
                            public void onPreRequest() {

                            }

                            @Override
                            public void onResponse(HttpResponse response) {
                                myLogs.info("Sending fcm token message = " + response.getResult());
                            }
                        }).execute();
                    }
                });
    }
}
