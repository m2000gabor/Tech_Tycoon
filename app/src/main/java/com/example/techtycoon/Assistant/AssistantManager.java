package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AssistantManager {
    private final static List<AbstractAssistant> ASSISTANTS= Arrays.asList(
            new CoAssistant_marketingGoal(),
            new CoAssistant_avgMarketing(),
            new AverageBot(),
            new XiaomiBot(),
            new AppleBot(),
            new AppleBot2(),
            new Bot1(),
            new AppleBotPrinciple()
    );

    public static List<String> getInputLabels(int assistantId){
        return ASSISTANTS.get(assistantId).getInputLabels();
    }

    public static String getDefaultStatus(int assistantId){
        return ASSISTANTS.get(assistantId).getDefaultStatus();
    }

    public static List<String> getAssistantNames(){
        List<String> names=new LinkedList<>();
        ASSISTANTS.forEach( a -> names.add(a.getAssistantName()));
        return names;
    }
    public static String getAssistantName(int assistantType){
        return ASSISTANTS.get(assistantType).getAssistantName();
    }

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
                if(myDevices.size()==0){continue;}

                AbstractAssistant aa=ASSISTANTS.get(companyList.get(i).assistantType);
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
