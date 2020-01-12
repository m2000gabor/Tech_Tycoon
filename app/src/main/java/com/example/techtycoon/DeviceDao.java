package com.example.techtycoon;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Query("SELECT * FROM device WHERE id IN (:deviceIds)")
    LiveData<List<Device>> loadAllByIds(int[] deviceIds);

    /*@Query("SELECT * FROM device WHERE name LIKE :first AND " +
            "profit LIKE :last LIMIT 1")
    Device findByName(String first, String last);*/
    @Query("DELETE FROM device WHERE id = :Id")
    void deleteOneDeviceById(int Id);

    @Query("DELETE FROM company WHERE companyId = :Id")
    void delOneCompanyById(int Id);

    @Insert
    void insertOneDevice(Device device);

    @Insert
    void insertOneCompany(Company company);

    /*
    @Insert
    void insertAll(Device... devices);

    @Delete
    void delete(Device device);*/

    @Query("DELETE FROM device")
    void deleteAllDevice();

    @Query("DELETE FROM company")
    void deleteAllCompany();

    @Update
    void updateCompanies(Company... companies);

}
