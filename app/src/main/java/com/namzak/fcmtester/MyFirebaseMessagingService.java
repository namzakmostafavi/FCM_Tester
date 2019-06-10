package com.namzak.fcmtester;
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.namzak.fcmtester.http.HttpRequest;
import com.namzak.fcmtester.http.HttpResponse;
//import com.google.firebase.quickstart.fcm.R;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private MyLogs myLogs;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        if (myLogs == null)
            myLogs = new MyLogs(this);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        myLogs.info("From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            myLogs.info("Message data payload: " + remoteMessage.getData());


            String body = remoteMessage.getData().get("body");
            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("body", body);
            sendBroadcast(intent);
//            try {
//                JSONObject jBody = new JSONObject(body);
//                final long pingId = jBody.optLong("pingId");
//                myLogs.info("receiving message with pingId= " + pingId);
//
//                String url = MainActivity.BASE_URL + "/FcmAck";
//                JSONObject json = new JSONObject();
//                try {
//                    @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getContentResolver(),
//                            Settings.Secure.ANDROID_ID);
//                    json.put("pingId", pingId);
//                    json.put("androidId", android_id);
//                    json.put("name", Build.BOARD + " " + Build.MODEL);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                new HttpRequest().Post(url, json.toString()).setOnResultListener(new HttpRequest.OnResultListener() {
//                    @Override
//                    public void onFailure() {
//                        myLogs.info("Network Error! sending Ack with pingId " + pingId);
//                    }
//
//                    @Override
//                    public void onPreRequest() {
//
//                    }
//
//                    @Override
//                    public void onResponse(HttpResponse response) {
//                        myLogs.info("Sending ACK message = " + response.getResult());
//                    }
//                }).execute();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
//        // [START dispatch_job]
//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .build();
//        WorkManager.getInstance().beginWith(work).enqueue();
//        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        if (myLogs == null)
            myLogs = new MyLogs(this);
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

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}