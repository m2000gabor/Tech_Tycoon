package com.example.techtycoon;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

//todo ?remove unattached observers since they cause delays and lag?

class DeviceRepository {
    private int orderBy=0;
    private int filter=-1;
    private boolean desc=true;

    private DeviceDao mDao;
    private TransactionDao mTransactionDao;
    private LiveData<List<Device>> mAllDevs;
    private List<Device> cachedAllDevices;
    private LiveData<List<Company>> mAllComps;

    private MutableLiveData<List<Device>> mutableAllDevices;


    DeviceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.deviceDao();
        mTransactionDao = db.transactionDao();

        mAllComps=mDao.getAllCompany();
        mAllDevs=mDao.getAllDevices();

        mAllDevs.observeForever(new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                cachedAllDevices=devices;
                mutableAllDevices.setValue(orderDevices(devices));
            }
        });

        mutableAllDevices=new MutableLiveData<List<Device>>();
        setSortBy(orderBy,true);
    }

    //get the mutable
    LiveData<List<Device>> mutable_getAllDevices() { return mutableAllDevices; }

    void setSortBy(int sortId,boolean desc){
        orderBy=sortId;
        this.desc=desc;
        mutableAllDevices.setValue(orderDevices(cachedAllDevices));
    }

    void setFilter(int filterById){
        filter=filterById;
        mutableAllDevices.setValue(orderDevices(cachedAllDevices));
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
        mutableAllDevices.setValue(orderDevices(cachedAllDevices));
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Company>> getAllCompanies() { return mAllComps;}

    List<Company> getAllCompaniesList() {
        List<Company> def=new LinkedList<Company>();
        try {
            return new getAllCompsAsyncTask(mDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return def;
    }
    Device getDevice_byID(int id){
        try {
            return new getDevByIdAsyncTask(mDao,id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    Company getCompany_byID(int id){
        try {
            return new getCompByIdAsyncTask(mDao,id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Company();
    }
    LiveData<Company> getLiveCompany_byID(int id){
        try {
            return new getLiveCompByIdAsyncTask(mDao,id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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
    void insertDevices(Device... devices) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertDevices(devices);
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
        List<Device> deviceList=mutableAllDevices.getValue();
        List<Company> companyList=getAllCompaniesList();

        int i=0;
        while (deviceList.get(i).id != id){i++;}

        int j=0;
        while(deviceList.get(i).ownerCompanyId != companyList.get(j).companyId){j++;}
        Company newComp=companyList.get(j);
        newComp.usedSlots--;
        updateCompanies(newComp);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.deleteOneDeviceById(id);
        });
    }
    void delOneCompanyById(int id){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.delOneCompanyById(id);
            mDao.deleteDevicesFromCompany(id);
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

    void startAgain(Company... companies){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTransactionDao.insertAndDeleteInTransaction(companies);
        });
    }

    void assistantToDatabase(Company[] companies,Device[] insert,Device[] update,Device[] delete){
        for (Device d : insert) { d.id = 0; d.setSoldPieces(0); }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTransactionDao.assistantTransaction(companies,insert,update,delete);
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
    private static class getCompByIdAsyncTask extends android.os.AsyncTask<Void, Void, Company >{
        private DeviceDao mAsyncTaskDao;
        private int id;
        getCompByIdAsyncTask(DeviceDao dao,int ID) { mAsyncTaskDao = dao; this.id=ID;}
        @Override
        protected Company  doInBackground(Void... params) {
            return mAsyncTaskDao.getCompany_byID(id);
        }
    }
    private static class getLiveCompByIdAsyncTask extends android.os.AsyncTask<Void, Void, LiveData<Company> >{
        private DeviceDao mAsyncTaskDao;
        private int id;
        getLiveCompByIdAsyncTask(DeviceDao dao,int ID) { mAsyncTaskDao = dao; this.id=ID;}
        @Override
        protected LiveData<Company> doInBackground(Void... params) {
            return mAsyncTaskDao.getLiveCompany_byID(id);
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
    private static class getDevByIdAsyncTask extends android.os.AsyncTask<Void, Void, Device >{
        private DeviceDao mAsyncTaskDao;
        private int id;
        getDevByIdAsyncTask(DeviceDao dao,int ID) { mAsyncTaskDao = dao; this.id=ID;}
        @Override
        protected Device  doInBackground(Void... params) {
            return mAsyncTaskDao.getDevice_byID(id);
        }
    }

    private List<Device> orderDevices(List<Device> devices){
        try {
            if (filter != -1) {
                devices = devices.stream().filter(device -> device.ownerCompanyId == filter).collect(Collectors.toList());
            }
            if (orderBy == -100) {
                devices.sort((a, b) -> a.name.compareTo(b.name));
            } else if (desc) {
                devices.sort((a, b) -> b.getFieldByNum(orderBy) - a.getFieldByNum(orderBy));
            } else {
                devices.sort((a, b) -> a.getFieldByNum(orderBy) - b.getFieldByNum(orderBy));
            }
        }catch (NullPointerException ignored){}
        return devices;
    }
}
