package com.example.serverble;




import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
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
}
