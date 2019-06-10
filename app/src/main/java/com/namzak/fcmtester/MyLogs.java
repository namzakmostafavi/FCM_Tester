package com.namzak.fcmtester;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyLogs {
    private RandomAccessFile randomAccessFile = null;
    private Context mContext;
    private SimpleDateFormat dateFormat;
    private long seek;

    public MyLogs(Context mContext) {
        this.mContext = mContext;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
    }

    private void open() {
        try {
            String name = mContext.getFilesDir().getPath() + "/logs.txt";
            File file = new File(name);
            if (!file.exists())
                file.createNewFile();
            seek = file.length();
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info(String message) {
        open();
        Calendar calendar = Calendar.getInstance();
        String text = dateFormat.format(calendar.getTime()) + "-" + (System.currentTimeMillis() / 1000);
        text += " [" + Thread.currentThread().getId() + "] " + message + "\n";
        if (randomAccessFile != null) {
            try {
                randomAccessFile.seek(seek);
                randomAccessFile.writeUTF(text);
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("@UDP-PUSH", text);
    }
}
