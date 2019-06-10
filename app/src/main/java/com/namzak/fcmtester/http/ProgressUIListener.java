package com.namzak.fcmtester.http;

public interface ProgressUIListener {
    void onProgressChanged(int d, long fileSize, float progress, int i);

    void onProgressFinish();
}
