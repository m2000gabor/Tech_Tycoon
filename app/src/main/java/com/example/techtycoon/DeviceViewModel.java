package com.example.techtycoon;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DeviceViewModel extends AndroidViewModel {
    //todo companyViewModel

    private DeviceRepository mRepository;
    private LiveData<List<Device>> mAllDevices;
    private LiveData<List<Company>> mAllCompanies;

    public DeviceViewModel (Application application) {
        super(application);
        mRepository = new DeviceRepository(application);
        //mAllDevices = mRepository.getAllDevices();
        mAllDevices = mRepository.mutable_getAllDevices();
        mAllCompanies=mRepository.getAllCompanies();
    }
    //return mutableAlldevice
    LiveData<List<Device>> mutable_getAllDevices() { return mAllDevices; }


    //LiveData<List<Device>> getAllDevices() { return mAllDevices; }
    LiveData<List<Company>> getAllCompanies() { return mAllCompanies; }
    List<Company> getAllCompaniesList(){return mRepository.getAllCompaniesList();}
    Company getCompany_byID(int ID){return mRepository.getCompany_byID(ID);}
    List<Device> getAllDevicesList(){return mRepository.getAllDevicesList();}

    void orderDevices_ByCode2 (int code) { mRepository.setSortBy(code);}

    void filter_byCompanyID(int ownerId){mRepository.setFilter(ownerId);}


    void insertDevice(Device device) { mRepository.insertDevice(device); }
    void insertCompanies(Company... companies) { mRepository.insertCompanies(companies); }

    void updateCompanies(Company... companies) { mRepository.updateCompanies(companies); }
    void updateDevices(Device... devices) { mRepository.updateDevices(devices); }

    void updateLevel(int id, int[] levels,int money){mRepository.updateLevel(id,levels,money);}

    void delOneDeviceById(int id) { mRepository.deleteOneDeviceById(id); }
    void delOneCompanyById(int id) { mRepository.delOneCompanyById(id); }

    void deleteAll(){
        mRepository.deleteAll();
            }

    void startAgain(Company... companies){
        mRepository.startAgain(companies);
    }

    void assistantToDatabase(Wrapped_DeviceAndCompanyList assistantResults){

        mRepository.assistantToDatabase(assistantResults.UpdateCompanies.toArray(new Company[0]),
                assistantResults.insert.toArray(new Device[0]),
                assistantResults.update.toArray(new Device[0]),
                assistantResults.delete.toArray(new Device[0]));
    }
}
