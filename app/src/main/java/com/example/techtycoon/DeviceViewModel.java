package com.example.techtycoon;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DeviceViewModel extends AndroidViewModel {
    //todo rethink the usage of the viewModel
    //todo use some validation

    private DeviceRepository mRepository;
    private LiveData<List<Device>> mAllDevices;
    private LiveData<List<Company>> mAllCompanies;

    public DeviceViewModel (Application application) {
        super(application);
        mRepository = new DeviceRepository(application);
        mAllDevices = mRepository.mutable_getAllDevices();
        mAllCompanies=mRepository.getAllCompanies();
    }
    //return mutableAlldevice
    LiveData<List<Device>> mutable_getAllDevices() { return mAllDevices; }

    public LiveData<List<Company>> getAllCompanies() { return mAllCompanies; }
    public List<Company> getAllCompaniesList(){return mRepository.getAllCompaniesList();}
    Company getCompany_byID(int ID){return mRepository.getCompany_byID(ID);}
    LiveData<Company> getLiveCompany_byID(int ID){return mRepository.getLiveCompany_byID(ID);}
    Device getDevice_byID(int ID){return mRepository.getDevice_byID(ID);}
    public List<Device> getAllDevicesList(){return mRepository.getAllDevicesList();}

    void orderDevices_ByCode2 (Device.DeviceAttribute code,boolean desc) { mRepository.setSortBy(code,desc);}
    void setOrder(boolean isDescending){mRepository.setDesc(isDescending);}

    void filterBy(Device.DeviceAttribute attribute,List<Integer> values){mRepository.setAdvancedFilter(attribute,values);}


    void insertDevice(Device device) { mRepository.insertDevice(device); }
    void insertDevices(Device... devices) { mRepository.insertDevices(devices); }
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

    public void assistantToDatabase(Wrapped_DeviceAndCompanyList assistantResults){

        mRepository.assistantToDatabase(
                assistantResults.UpdateCompanies.toArray(new Company[0]),
                assistantResults.insert.toArray(new Device[0]),
                assistantResults.update.toArray(new Device[0]),
                assistantResults.delete.toArray(new Device[0]));
    }
}
