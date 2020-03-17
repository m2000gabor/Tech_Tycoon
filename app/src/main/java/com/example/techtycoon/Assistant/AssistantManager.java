package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;

public class AssistantManager {

    public AssistantManager(){
    };

    public Wrapped_DeviceAndCompanyList trigger(List<Company> companyList, List<Device> deviceList){
        Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(deviceList,companyList);;
        for (int i=0;i<companyList.size();i++){
            if (companyList.get(i).hasAssistant){
                List<Device> myDevices=new LinkedList<>();
                for (Device d : deviceList) {
                    if (d.ownerCompanyId == companyList.get(i).companyId) {
                        myDevices.add(d);
                    }
                }
                Wrapped_DeviceAndCompanyList updatesFromAnAssistant =companyList.get(i).assistant.timeToDo(companyList,deviceList,myDevices,companyList.get(i));
                r.insert.addAll(updatesFromAnAssistant.insert);
                r.delete.addAll(updatesFromAnAssistant.delete);
                r.update.addAll(updatesFromAnAssistant.update);
                r.UpdateCompanies.addAll(updatesFromAnAssistant.UpdateCompanies);
            }
        }
        return r;
    };
}
