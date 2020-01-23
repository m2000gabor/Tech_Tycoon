package com.example.techtycoon;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DeviceViewModel extends AndroidViewModel {

    private DeviceRepository mRepository;

    private LiveData<List<Device>> mAllDevices;
    private LiveData<List<Company>> mAllCompanies;

    public DeviceViewModel (Application application) {
        super(application);
        mRepository = new DeviceRepository(application);
        mAllDevices = mRepository.getAllDevices();
        mAllCompanies=mRepository.getAllCompanies();
    }

    LiveData<List<Device>> getAllDevices() { return mAllDevices; }
    LiveData<List<Company>> getAllCompanies() { return mAllCompanies; }
    List<Company> getAllCompaniesList(){return mRepository.getAllCompaniesList();}
    List<Device> getAllDevicesList(){return mRepository.getAllDevicesList();}


    public void insertDevice(Device device) { mRepository.insertDevice(device); }
    public void insertCompany(Company company) { mRepository.insertCompany(company); }

    public void updateCompanies(Company... companies) { mRepository.updateCompanies(companies); }
    public void updateDevices(Device... devices) { mRepository.updateDevices(devices); }

    public void delOneDeviceById(int id) { mRepository.deleteOneDeviceById(id); }
    public void delOneCompanyById(int id) { mRepository.delOneCompanyById(id); }

    public void deleteAll(){mRepository.deleteAll();}
}
