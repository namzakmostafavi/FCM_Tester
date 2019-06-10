package com.namzak.fcmtester.http;

/**
 * Created by Mohammadloo on 2017-05-30.
 */

public class HttpResponse {

    private String result = "";
    private int status = 0;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Status {
        public static final int OK = 200;
        public static final int ValidationError = 400;
        public static final int UnknownError = 460;
        public static final int Forbidden = 403;
        public static final int Created = 201;
    }
}
