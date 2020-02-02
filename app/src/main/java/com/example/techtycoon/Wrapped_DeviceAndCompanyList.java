package com.example.techtycoon;

import java.util.List;

public class Wrapped_DeviceAndCompanyList {
    public Wrapped_DeviceAndCompanyList(List<Device> devices, List<Company> companies) {
        this.devices = devices;
        this.companies = companies;
    }

    public List<Device> devices;
    public List<Company> companies;
}
