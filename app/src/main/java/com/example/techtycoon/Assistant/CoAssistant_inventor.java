package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

class CoAssistant_inventor extends AbstractAssistant {
    @Override
    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        myCompany.assistantStatus="";
        //Inventor
                /*
                Name: developer, just self, inventor, no new slot
                Activated always
                Action tree:
                    if a development is available than purchase it
                 */
        for (int i=0;i<Device.NUMBER_OF_ATTRIBUTES;i++) {
            if(DevelopmentValidator.getOneDevelopmentCost(i,myCompany.getLevels_USE_THIS()[i])<=myCompany.money &&
                    DevelopmentValidator.getOneDevelopmentCost(i,myCompany.getLevels_USE_THIS()[i])!=-1
            ){
                myCompany.money-=DevelopmentValidator.getOneDevelopmentCost(i,myCompany.getLevels_USE_THIS()[i]);
                int[] levels=myCompany.getLevels_USE_THIS();
                levels[i]++;
                myCompany.setLevels_USE_THIS(levels);
                myCompany.assistantStatus="newTechnologyAvailable";
            }
        }
        if(myCompany.assistantStatus.equals("newTechnologyAvailable")){
            int minIndex=min_Overall(myDevices);
            int maxIndex=max_Overall(myDevices);
            Device newDev=new Device(myDevices.get(maxIndex));
            newDev.profit*=1.2;
            newDev.name= AbstractAssistant.nameBuilder.buildName(newDev.name,1);
            newDev.ram=myCompany.getLevels_USE_THIS()[0];
            newDev.memory=myCompany.getLevels_USE_THIS()[1];
            newDev.setBodyParams(myCompany.getLevels_USE_THIS()[2],
                    myCompany.getLevels_USE_THIS()[3],
                    myCompany.getLevels_USE_THIS()[4],
                    myCompany.getLevels_USE_THIS()[5],
                    myCompany.getLevels_USE_THIS()[6]
            );

            //release device
            if (myCompany.hasFreeSlot()) {
                ret.insert.add(newDev);
                myCompany.usedSlots+=ret.insert.size();
            } else {
                ret.delete.add(myDevices.get(minIndex));
                ret.insert.add(newDev);
            }
            myCompany.assistantStatus="nothing";
        }

        return ret;
    }
}
