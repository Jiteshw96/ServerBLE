package com.example.serverble;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ble_data_client")
public class BleDataFromClient {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "message")
    private String message="";

    @ColumnInfo(name = "devicename")
    private String devicename="";

    @ColumnInfo(name = "devicemacaddress")
    private String devicemacaddress="";

    @ColumnInfo(name = "time")
    private String time="";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDevicemacaddress() {
        return devicemacaddress;
    }

    public void setDevicemacaddress(String devicemacaddress) {
        this.devicemacaddress = devicemacaddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
