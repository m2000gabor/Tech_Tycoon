package com.example.techtycoon;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Simulator {
    private DeviceViewModel deviceViewModel;

    public Simulator(DeviceViewModel model){this.deviceViewModel=model;}

    public void simulate(){
        List<Company> companyList =deviceViewModel.getAllCompaniesList();
        List<Device> deviceList=deviceViewModel.getAllDevicesList();
        if(companyList.isEmpty()){return;}
        int[] sold=new int[deviceList.size()];
        selling(sold,deviceList);
        earning(sold,deviceList,companyList);
        Company[] varargs =companyList.toArray(new Company[0]);

        //apply changes
        deviceViewModel.updateCompanies(varargs);

    }

    private void selling(int[] sold, @NotNull List<Device> deviceList){
        for (int i=0;i<deviceList.size();i++){
            sold[i]=10;
        }
    }

    private void earning(int[] sold, @NotNull List<Device> deviceList,@NotNull List<Company> companyList){
        for (int i=0;i<deviceList.size();i++){
            int j=0;
            while(j<companyList.size() && companyList.get(j).companyId != deviceList.get(i).ownerCompanyId){j++;}
            companyList.get(j).money+=sold[i]*deviceList.get(i).profit;
        }
    }
}
