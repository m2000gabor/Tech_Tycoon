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

    @Query("SELECT * FROM company WHERE companyId = :id")
    Company getCompany_byID(int id);

    @Query("SELECT * FROM company WHERE companyId = :id")
    LiveData<Company> getLiveCompany_byID(int id);

    @Query("SELECT * FROM device WHERE id = :id")
    Device getDevice_byID(int id);

    @Query("SELECT * FROM device")
    LiveData<List<Device>> getAllDevices();

    @Query("SELECT * FROM device")
    List<Device> getAllDevicesList();

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
    void insertDevices(Device... devices);

    @Insert
    void insertCompanies(Company... companies);

    @Update
    void updateCompanies(Company... companies);

    @Query("UPDATE company SET  money = :money , levels = :lvlString  WHERE companyId LIKE :id ")
    void updateLevel(int id,String lvlString,int money);

    @Update
    void updateDevices(Device... devices);

    //Deletions

    @Delete
    void deleteDevices(Device... devices);

    @Query("DELETE FROM device")
    void deleteAllDevice();

    @Query("DELETE FROM company")
    void deleteAllCompany();

    @Query("DELETE FROM device WHERE ownerCompanyId =:companyId")
    void deleteDevicesFromCompany(int companyId);

}
