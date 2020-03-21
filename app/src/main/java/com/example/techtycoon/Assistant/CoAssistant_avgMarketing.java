package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DetailsOfOneCompany;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

class CoAssistant_avgMarketing extends AbstractAssistant {
    CoAssistant_avgMarketing() {
    }

    @Override
    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
            int marketingCost = DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
            if (myCompany.money >= marketingCost && myCompany.marketing < avg_marketing(companyList)) {
                myCompany.money -= marketingCost;
                myCompany.marketing += 10;
                myCompany.logs = myCompany.logs + "Assistant bought 10 marketing!\n";
            }
            ret.UpdateCompanies.add(myCompany);
        return ret;
    }
}
