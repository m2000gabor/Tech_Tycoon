package com.example.techtycoon;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;

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

    public List<Company> getAllCompaniesList() {
        List<Company> def=new LinkedList<Company>();
        try {
            return new getAllCompsAsyncTask(mDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return def;
    }

    private static class getAllCompsAsyncTask extends android.os.AsyncTask<Void, Void, List<Company> > {
        private DeviceDao mAsyncTaskDao;
        getAllCompsAsyncTask(DeviceDao dao) { mAsyncTaskDao = dao; }
        @Override
        protected List<Company>  doInBackground(Void... params) {
            return mAsyncTaskDao.getAllCompaniesList();
        }
    }

    public List<Device> getAllDevicesList() {
        List<Device> def=new LinkedList<Device>();
        try {
            return new getAllDevsAsyncTask(mDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return def;
    }

    private static class getAllDevsAsyncTask extends android.os.AsyncTask<Void, Void, List<Device> > {
        private DeviceDao mAsyncTaskDao;
        getAllDevsAsyncTask(DeviceDao dao) { mAsyncTaskDao = dao; }
        @Override
        protected List<Device>  doInBackground(Void... params) {
            return mAsyncTaskDao.getAllDevicesList();
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insertDevice(Device dev) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertOneDevice(dev);
        });
    }
    void insertCompany(Company company) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insertOneCompany(company);
        });
    }

    void updateCompanies(Company... companies){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.updateCompanies(companies);
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
}
