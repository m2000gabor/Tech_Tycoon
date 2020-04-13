package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;

import static com.example.techtycoon.Assistant.ToolsForAssistants.avg_marketing;

class CoAssistant_avgMarketing implements AbstractAssistant {
    CoAssistant_avgMarketing() {}

    @Override
    public List<String> getInputLabels() {
        return null;
    }

    @Override
    public String getAssistantName() {
        return "Average marketing";
    }

    @Override
    public String getDefaultStatus() {
        return "";
    }

    @Override
    public Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
            int marketingCost = DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
            if (myCompany.money >= marketingCost && myCompany.marketing < avg_marketing(companyList)) {
                myCompany.money -= marketingCost;
                myCompany.marketing += 10;
                myCompany.logs = myCompany.logs + "Assistant bought 10 marketing!\n";
            }
            ret.UpdateCompanies.add(myCompany);
        return ret;
    }
}
