package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;

public class AssistantManager {
    List<Company> companies;
    List<Device> devices;
    public List<Assistant> assistants;

    public AssistantManager(){
        companies=new LinkedList<>();
        devices=new LinkedList<>();
        assistants=new LinkedList<>();
    };

    public Wrapped_DeviceAndCompanyList trigger(List<Company> companyList, List<Device> deviceList){
        //Company[] compsForUpdate=new Company[companyList.size()];
        List<Company> compsForUpdate=new LinkedList<>();
        for (int i=0;i<companyList.size();i++){
            if (companyList.get(i).hasAssistant){
                compsForUpdate.add(companyList.get(i).assistant.timeToDo(companyList,deviceList));
            }else {
                compsForUpdate.add(companyList.get(i));
            }
        }
        return new Wrapped_DeviceAndCompanyList(deviceList,compsForUpdate);
    };

    private void addAssistant(int companyId,int goal){
        Assistant assistant=new Assistant(companyId,goal) {
            @Override
            public Company timeToDo(List<Company> companyList, List<Device> deviceList) {
                super.timeToDo(companyList, deviceList);
                Company myCompany= companyList.get(this.bossCompanyID);
                if (myCompany.money>=10000){
                    myCompany.money-=10000;
                    myCompany.marketing+=10;
                }
                return myCompany;
            }
        };
        assistants.add(assistant);
    }

    private void deleteAssistant(int companyId){
        int i=0;
        while (i<assistants.size() && assistants.get(i).bossCompanyID != companyId){i++;}
        assistants.remove(i);
    }
}
