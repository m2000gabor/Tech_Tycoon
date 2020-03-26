package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DetailsOfOneCompany;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

class CoAssistant_marketingGoal extends AbstractAssistant{
    CoAssistant_marketingGoal() {}

    @Override
    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        int assistantGoal=Integer.parseInt(myCompany.assistantStatus);
        int i=0;
        while (myCompany.money >= DevelopmentValidator.calculateMarketingCost(myCompany.marketing)
                && myCompany.marketing < assistantGoal) {
            myCompany.money -= DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
            myCompany.marketing += 10;
            i++;
        }
        myCompany.logs = myCompany.logs + "Assistant bought "+i*10+" marketing!\n";
        ret.UpdateCompanies.add(myCompany);
        return ret;
    }
}
