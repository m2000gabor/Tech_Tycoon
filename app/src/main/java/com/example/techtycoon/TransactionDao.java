package com.example.techtycoon;

import androidx.room.Dao;
import androidx.room.Transaction;

@Dao
public abstract class TransactionDao implements DeviceDao{
    @Transaction
    public void insertAndDeleteInTransaction(Company... companies) {
        // Anything inside this method runs in a single transaction.
        deleteAllCompany();
        deleteAllDevice();
        insertCompanies(companies);
        }

    @Transaction
    public void assistantTransaction(Company[] companies,Device[] insert,Device[] update,Device[] delete) {
        // Anything inside this method runs in a single transaction.
        updateCompanies(companies);
        deleteDevices(delete);
        insertDevices(insert);
        updateDevices(update);
    }
}
