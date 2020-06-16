package com.example.serverble;

import android.app.Application;
public class ServerBleApplication extends Application {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
