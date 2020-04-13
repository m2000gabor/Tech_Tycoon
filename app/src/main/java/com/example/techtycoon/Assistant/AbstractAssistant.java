package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

interface AbstractAssistant{
    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret);
    List<String> getInputLabels();
    String getAssistantName();
    String getDefaultStatus();
}
