package com.example.techtycoon;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

//todo remove unattached observers since they cause delays and lag

class DeviceRepository {
    private int orderBy=0;
    private int filter=-1;

    private DeviceDao mDao;
    private LiveData<List<Device>> mAllDevs;
    private LiveData<List<Company>> mAllComps;

    private MutableLiveData<List<Device>> mutableAllDevices;
    private Observer observer;
    private boolean hasObserver=false;


    DeviceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.deviceDao();

        mAllComps=mDao.getAllCompany();

        mutableAllDevices=new MutableLiveData<List<Device>>();
        setSortBy(orderBy);
    }

    //mutables
    LiveData<List<Device>> mutable_getAllDevices() { return mutableAllDevices; }

    void setSortBy(int sortBy){
        orderBy=sortBy;
        getDevices_with_sort_filter();
    }

    void setFilter(int filterBy){
        filter=filterBy;
        getDevices_with_sort_filter();
    }



    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Device>> getAllDevices() { return mAllDevs; }
    LiveData<List<Company>> getAllCompanies() { return mAllComps;}


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
    Company getCompany_byID(int id){
        try {
            return new getCompByIdAsyncTask(mDao,id).execute().get().get(0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Company();
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

    void updateLevel(int id,int[] lvls,int money){AppDatabase.databaseWriteExecutor.execute(() -> {
        mDao.updateLevel(id,Converter.intArrayToString(lvls),money);
    });};


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
    private static class getCompByIdAsyncTask extends android.os.AsyncTask<Void, Void, List<Company> >{
        private DeviceDao mAsyncTaskDao;
        private int id;
        getCompByIdAsyncTask(DeviceDao dao,int ID) { mAsyncTaskDao = dao; this.id=ID;}
        @Override
        protected List<Company>  doInBackground(Void... params) {
            return mAsyncTaskDao.getCompany_byID(id);
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


    //depend on the last sort and filter settings
    private void getDevices_with_sort_filter(){
        if(hasObserver){mAllDevs.removeObserver(observer);};
        if(filter==-1){
            switch (orderBy){
                default: mAllDevs=mDao.getAllDevices();break;
                case 1: mAllDevs=mDao.orderedDevicesBy_SoldPieces(); break;
                case 2: mAllDevs=mDao.orderedDevicesBy_Ram(); break;
                case 3: mAllDevs=mDao.orderedDevicesBy_Memory(); break;
                case 4: mAllDevs=mDao.orderedDevicesBy_Profit(); break;
                case 5:mAllDevs=mDao.orderedDevicesBy_Name(); break;
                case 6: mAllDevs=mDao.orderedDevicesBy_Price(); break;
                case 7: mAllDevs=mDao.orderedDevicesBy_OverallIncome(); break;

            }
        } else{
            switch (orderBy){
                default: mAllDevs=mDao.getAllDevices_byCompanyIDs(new int[]{filter});break;
                case 1: mAllDevs=mDao.orderedDevicesBy_SoldPieces(new int[]{filter}); break;
                case 2: mAllDevs=mDao.orderedDevicesBy_Ram(new int[]{filter}); break;
                case 3: mAllDevs=mDao.orderedDevicesBy_Memory(new int[]{filter}); break;
                case 4: mAllDevs=mDao.orderedDevicesBy_Profit(new int[]{filter}); break;
                case 5: mAllDevs=mDao.orderedDevicesBy_Name(new int[]{filter}); break;
                case 6: mAllDevs=mDao.orderedDevicesBy_Price(new int[]{filter}); break;
                case 7: mAllDevs=mDao.orderedDevicesBy_OverallIncome(new int[]{filter}); break;
            }
        }

        observer=new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> deviceList) {
                mutableAllDevices.setValue(deviceList);
            }
        };

        mAllDevs.observeForever(observer);
        hasObserver=true;
    }

}
