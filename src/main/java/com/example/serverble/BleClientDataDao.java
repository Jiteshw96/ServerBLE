package com.example.serverble;




import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BleClientDataDao {

    @Query("SELECT * FROM ble_data_client order by id desc ")
    List<BleDataFromClient> getAll();


    @Query("SELECT COUNT(*) from ble_data_client")
    int countUsers();

    @Insert
    void insertAll(BleDataFromClient... users);

    @Delete
    void delete(BleDataFromClient user);

    @Query("DELETE FROM ble_data_client")
    public void nukeTable();

    @Query("SELECT COUNT(*) from ble_data_device")
    int countDevice();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevice(BleDataDeviceInfo... device);
    @Delete
    void delete(BleDataDeviceInfo device);
    @Query("SELECT * FROM ble_data_device")
    List<BleDataDeviceInfo> getAllDevice();

    @Query("SELECT COUNT(*) from ble_data_device where devicemacaddress = :devicemacaddress")
    int countDeviceAgainstMacAddress(String devicemacaddress);

    @Query("SELECT * FROM ble_data_device where devicemacaddress = :devicemacaddress")
    BleDataDeviceInfo getDeviceAgainstMacAddress(String devicemacaddress);

}
