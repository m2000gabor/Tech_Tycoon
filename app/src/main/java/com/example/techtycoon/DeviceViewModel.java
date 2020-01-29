package com.example.techtycoon;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class DeviceViewModel extends AndroidViewModel {

    private DeviceRepository mRepository;
    private LiveData<List<Device>> mAllDevices;
    private LiveData<List<Company>> mAllCompanies;

    private MutableLiveData<List<Device>> mutableAllDevices;

    public DeviceViewModel (Application application) {
        super(application);
        mRepository = new DeviceRepository(application);
        mAllDevices = mRepository.getAllDevices();
        mAllCompanies=mRepository.getAllCompanies();

        mutableAllDevices=new MutableLiveData<>();
        mAllDevices.observeForever(new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> deviceList) {
                mutableAllDevices.setValue(deviceList);
            }
        });
    }
    LiveData<List<Device>> mutable_getAllDevices() { return mutableAllDevices; }


    LiveData<List<Device>> getAllDevices() { return mAllDevices; }
    LiveData<List<Company>> getAllCompanies() { return mAllCompanies; }
    List<Company> getAllCompaniesList(){return mRepository.getAllCompaniesList();}
    List<Device> getAllDevicesList(){return mRepository.getAllDevicesList();}

    void orderDevices_ByCode(int code){
        mAllDevices = mRepository.orderDevices_ByCode(code);
        mAllDevices.observeForever(new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> deviceList) {
                mutableAllDevices.setValue(deviceList);
            }
        });
    }

    LiveData<List<Device>> filter_byCompanyIDs(int[] ownerIds){return mRepository.getAllDevices_byCompanyIDs(ownerIds);}


    void insertDevice(Device device) { mRepository.insertDevice(device); }
    void insertCompanies(Company... companies) { mRepository.insertCompanies(companies); }

    void updateCompanies(Company... companies) { mRepository.updateCompanies(companies); }
    void updateDevices(Device... devices) { mRepository.updateDevices(devices); }

    void updateLevel(int id, int[] levels,int money){mRepository.updateLevel(id,levels,money);}

    void delOneDeviceById(int id) { mRepository.deleteOneDeviceById(id); }
    void delOneCompanyById(int id) { mRepository.delOneCompanyById(id); }

    void deleteAll(){mRepository.deleteAll();}
}
