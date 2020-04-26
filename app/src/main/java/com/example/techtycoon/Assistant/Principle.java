package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

import androidx.annotation.Nullable;

interface Principle {
    /**
     * @return a value between 1 and 5; 0 if not needed
     */
    int getScore(Company myCompany, List<Device> myDevices,List<Company> allCompanies,List<Device> allDevices);
    String name();
    int defaultWeight();//default importance is 100
    boolean needsCleanInput();//no updated and inserted devices before this repair

    @Nullable
    Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices,List<Company> allCompanies,List<Device> allDevices);
}
