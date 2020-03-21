package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;

import static com.example.techtycoon.Assistant.AbstractAssistant.max_Overall;

public class AssistantManager {

    public AssistantManager(){
    };

    public Wrapped_DeviceAndCompanyList trigger(List<Company> companyList, List<Device> deviceList){
        Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(deviceList,companyList);
        for (int i=0;i<companyList.size();i++){
            if (companyList.get(i).assistantType !=-1){
                List<Device> myDevices=new LinkedList<>();
                for (Device d : deviceList) {
                    if (d.ownerCompanyId == companyList.get(i).companyId) {
                        myDevices.add(d);
                    }
                }

                Company myCompany=companyList.get(i);
                Wrapped_DeviceAndCompanyList updatesFromAnAssistant=new Wrapped_DeviceAndCompanyList(myDevices,companyList);
                myCompany.logs="";
                //Basic assistant moves (all assistant start with these)
                if(myDevices.size()==0){return updatesFromAnAssistant;}
                if(myCompany.hasFreeSlot()){
                    Device newDev=new Device(myDevices.get(max_Overall(myDevices)));
                    newDev.profit*=1.2;
                    newDev.name= AbstractAssistant.nameBuilder.buildName(newDev.name,0);
                    updatesFromAnAssistant.insert.add(newDev);
                    myCompany.usedSlots+=updatesFromAnAssistant.insert.size();
                }

                AbstractAssistant aa=new AbstractAssistant() {
                    @Override
                    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
                        return ret;
                    }
                };

                switch (companyList.get(i).assistantType){
                    case 1:
                        aa=new CoAssistant_marketingGoal();
                        break;
                    case 2:
                        aa=new CoAssistant_avgMarketing();
                        break;
                    case 3:
                        aa=new AverageBot();
                        break;
                    case 4:
                        aa=new RandomBot();
                        break;
                    case 5:
                        aa=new CoAssistant_inventor();
                        break;
                    case 6:
                        aa= new Bot1();
                        break;
                    case 7:
                        aa=new AppleBot();
                        break;
                }
                updatesFromAnAssistant=aa.work(companyList, deviceList,myDevices,myCompany,updatesFromAnAssistant);
                r.insert.addAll(updatesFromAnAssistant.insert);
                r.delete.addAll(updatesFromAnAssistant.delete);
                r.update.addAll(updatesFromAnAssistant.update);
                r.UpdateCompanies.addAll(updatesFromAnAssistant.UpdateCompanies);
            }
        }
        return r;
    };
}
