package com.example.techtycoon;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM company")
    LiveData<List<Company>> getAllCompany();

    @Query("SELECT * FROM company")
    List<Company> getAllCompaniesList();

    @Query("SELECT * FROM company WHERE companyId = :id")
    List<Company> getCompany_byID(int id);

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

    @Query("SELECT * FROM device ORDER BY profit DESC")
    LiveData<List<Device>> orderedDevicesBy_Profit();

    @Query("SELECT * FROM device ORDER BY name")
    LiveData<List<Device>> orderedDevicesBy_Name();

    @Query("SELECT * FROM device ORDER BY (profit+cost) DESC")
    LiveData<List<Device>> orderedDevicesBy_Price();

    @Query("SELECT * FROM device ORDER BY (profit*soldPieces) DESC")
    LiveData<List<Device>> orderedDevicesBy_OverallIncome();

    //filtered
    //get all devices of one company
    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ")
    LiveData<List<Device>> getAllDevices_byCompanyIDs(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY soldPieces DESC")
    LiveData<List<Device>> orderedDevicesBy_SoldPieces(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY ram DESC")
    LiveData<List<Device>> orderedDevicesBy_Ram(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY memory DESC")
    LiveData<List<Device>> orderedDevicesBy_Memory(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY profit DESC")
    LiveData<List<Device>> orderedDevicesBy_Profit(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY name DESC")
    LiveData<List<Device>> orderedDevicesBy_Name(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY (profit+cost) DESC")
    LiveData<List<Device>> orderedDevicesBy_Price(int[] ownerIds);

    @Query("SELECT * FROM device WHERE ownerCompanyId IN (:ownerIds) ORDER BY (profit*soldPieces) DESC")
    LiveData<List<Device>> orderedDevicesBy_OverallIncome(int[] ownerIds);

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

    @Query("DELETE FROM device")
    void deleteAllDevice();

    @Query("DELETE FROM company")
    void deleteAllCompany();

    @Update
    void updateCompanies(Company... companies);

    @Query("UPDATE company SET  money = :money , levels = :lvlString  WHERE companyId LIKE :id ")
    void updateLevel(int id,String lvlString,int money);

    @Update
    void updateDevices(Device... devices);

    @Delete
    void deleteDevices(Device... devices);

}
