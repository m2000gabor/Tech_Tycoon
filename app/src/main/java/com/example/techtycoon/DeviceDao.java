package com.example.techtycoon;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM company")
    LiveData<List<Company>> getAllCompany();

    @Query("SELECT * FROM company")
    List<Company> getAllCompaniesList();

    @Query("SELECT * FROM device")
    LiveData<List<Device>> getAllDevices();

    @Query("SELECT * FROM device")
    List<Device> getAllDevicesList();

    //Ordered ones
    @Query("SELECT * FROM device ORDER BY soldPieces DESC")
    LiveData<List<Device>> orderedDevicesBy_SoldPieces();

    @Query("SELECT * FROM device ORDER BY ram DESC")
    LiveData<List<Device>> orderedDevicesBy_Ram();

    @Query("SELECT * FROM device ORDER BY memory DESC")
    LiveData<List<Device>> orderedDevicesBy_Memory();

    //get all devices of one company
    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds)")
    LiveData<List<Device>> getAllDevices_byCompanyIDs(int[] ownerIds);

    /*
    @Query("SELECT * FROM device WHERE id IN (:deviceIds)")
    LiveData<List<Device>> loadAllByIds(int[] deviceIds);

    @Query("SELECT * FROM device WHERE name LIKE :first AND " +
            "profit LIKE :last LIMIT 1")
    Device findByName(String first, String last);*/

    @Query("DELETE FROM device WHERE id = :Id")
    void deleteOneDeviceById(int Id);

    @Query("DELETE FROM company WHERE companyId = :Id")
    void delOneCompanyById(int Id);

    @Insert
    void insertOneDevice(Device device);

    @Insert
    void insertCompanies(Company... companies);

    /*
    @Delete
    void delete(Device device);*/

    @Query("DELETE FROM device")
    void deleteAllDevice();

    @Query("DELETE FROM company")
    void deleteAllCompany();

    @Update
    void updateCompanies(Company... companies);

    @Update
    void updateDevices(Device... devices);
}
