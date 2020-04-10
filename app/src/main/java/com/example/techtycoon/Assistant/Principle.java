package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

import androidx.annotation.Nullable;

interface Principle {
    double getScore(Company myCompany, List<Device> myDevices,List<Company> allCompanies,List<Device> allDevices);

    @Nullable
    Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices,List<Company> allCompanies,List<Device> allDevices);
}
