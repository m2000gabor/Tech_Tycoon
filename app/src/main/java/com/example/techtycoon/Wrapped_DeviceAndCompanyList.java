package com.example.techtycoon;

import java.util.LinkedList;
import java.util.List;

public class Wrapped_DeviceAndCompanyList {
    public Wrapped_DeviceAndCompanyList(List<Device> devices, List<Company> companies) {
        this.devices = devices;
        this.companies = companies;
        insert=new LinkedList<>();
        update=new LinkedList<>();
        delete=new LinkedList<>();
        UpdateCompanies=new LinkedList<>();
    }

    public List<Device> devices;
    public List<Device> insert;
    public List<Device> update;
    public List<Device> delete;
    public List<Company> companies;
    public List<Company> UpdateCompanies;
}
