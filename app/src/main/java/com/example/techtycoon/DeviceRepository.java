package com.example.techtycoon;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//TODO make the reposotory to the only source of data

class DeviceRepository {

    private DeviceDao mDao;
    private LiveData<List<Device>> mAllDevs;
    private LiveData<List<Company>> mAllComps;


    DeviceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.deviceDao();
        mAllDevs = mDao.getAllDevices();
        mAllComps=mDao.getAllCompany();
    }


    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Device>> getAllDevices() { return mAllDevs; }
    LiveData<List<Company>> getAllCompanies() { return mAllComps;}

    LiveData<List<Device>> orderedDevicesBy_SoldPieces(){ return mDao.orderedDevicesBy_SoldPieces(); }
    LiveData<List<Device>> orderedDevicesBy_Ram(){ return mDao.orderedDevicesBy_Ram(); }
    LiveData<List<Device>> orderedDevicesBy_Memory(){ return mDao.orderedDevicesBy_Memory(); }

    LiveData<List<Device>> getAllDevices_byCompanyIDs(int[] ownerIds){ return mDao.getAllDevices_byCompanyIDs(ownerIds); }

    List<Company> getAllCompaniesList() {
        List<Company> def=new LinkedList<Company>();
        try {
            return new getAllCompsAsyncTask(mDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return def;
    }
    List<Device> getAllDevicesList() {
        List<Device> def=new LinkedList<Device>();
        try {
            return new getAllDevsAsyncTask(mDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return def;
    }




    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insertDevice(Device dev) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertOneDevice(dev);
        });
    }
    void insertCompanies(Company... companies) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertCompanies(companies);
        });
    }

    void updateCompanies(Company... companies){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.updateCompanies(companies);
        });
    }
    void updateDevices(Device... devices){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.updateDevices(devices);
        });
    }


    void deleteOneDeviceById(int id){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.deleteOneDeviceById(id);
        });
    }
    void delOneCompanyById(int id){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.delOneCompanyById(id);
        });
    }

    void deleteAll(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.deleteAllDevice();
        });
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.deleteAllCompany();
        });
    }


    //asynctask classes
    private static class getAllCompsAsyncTask extends android.os.AsyncTask<Void, Void, List<Company> > {
        private DeviceDao mAsyncTaskDao;
        getAllCompsAsyncTask(DeviceDao dao) { mAsyncTaskDao = dao; }
        @Override
        protected List<Company>  doInBackground(Void... params) {
            return mAsyncTaskDao.getAllCompaniesList();
        }
    }

    private static class getAllDevsAsyncTask extends android.os.AsyncTask<Void, Void, List<Device> > {
        private DeviceDao mAsyncTaskDao;
        getAllDevsAsyncTask(DeviceDao dao) { mAsyncTaskDao = dao; }
        @Override
        protected List<Device>  doInBackground(Void... params) {
            return mAsyncTaskDao.getAllDevicesList();
        }
    }


    //byCode
    LiveData<List<Device>> orderDevices_ByCode(int requestCode){
        switch (requestCode){
            default: mAllDevs=mDao.getAllDevices();break;
            case 1: mAllDevs=mDao.orderedDevicesBy_SoldPieces(); break;
            case 2: mAllDevs=mDao.orderedDevicesBy_Ram(); break;
            case 3: mAllDevs=mDao.orderedDevicesBy_Memory(); break;
        }
        return mAllDevs;

    }

}
