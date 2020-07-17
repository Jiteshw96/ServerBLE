package com.example.serverble;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.ArrayList;

public class ServerBleApplication extends Application {
    public static final String CHANNEL_ID = "serverService";
    ArrayList<Integer> msgColorList = new ArrayList();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status = " ";

    public String getClientMsg() {
        return ClientMsg;
    }

    public void setClientMsg(String msg) {
        this.ClientMsg = msg;
    }

    private String ClientMsg = "";
    private String serverMsg = "";

    public String getServerMsg() {
        return serverMsg;
    }

    public void setServerMsg(String serverMsg) {
        this.serverMsg = serverMsg;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        int[] colors = getApplicationContext().getResources().getIntArray(R.array.colors);

        for (int i = 0; i < colors.length; i++) {
            msgColorList.add(colors[i]);
        }
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}