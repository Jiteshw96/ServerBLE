package com.example.serverble;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ble_data_device")
public class BleDataDeviceInfo {



    @ColumnInfo(name = "devicename")
    private String devicename="";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "devicemacaddress")
    private String devicemacaddress="";

    @ColumnInfo(name = "color")
    private String color="";

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
