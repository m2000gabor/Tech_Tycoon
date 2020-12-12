package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;
import java.util.Random;

import static com.example.techtycoon.Assistant.ToolsForAssistants.avg_lastProfit;
import static com.example.techtycoon.Assistant.ToolsForAssistants.nameBuilder;

class RandomBot implements AbstractAssistant {
//todo implement or delete
    @Override
    public List<String> getInputLabels() {
        return null;
    }

    @Override
    public String getAssistantName() {
        return "Random bot";
    }

    @Override
    public String getDefaultStatus() {
        return "";
    }

    @Override
    public Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
                //not working
        //activates only when profit is below the average, no development, no new slot
                //random
                if (avg_lastProfit(companyList) > myCompany.lastProfit) {
                    Random randomGenerator = new Random(deviceList.hashCode());

                    int minOverallIncome = myDevices.get(0).getOverallProfit();
                    int minIndex = 0;
                    for (int i = 1; i < myDevices.size(); i++) {
                        if (myDevices.get(i).getOverallProfit() < minOverallIncome) {
                            minOverallIncome = myDevices.get(i).getOverallProfit();
                            minIndex = i;
                        }
                    }

                    //make newDev
                    String name =nameBuilder.buildName(myDevices.get(minIndex).name,1);
                    int[] memory = new int[Device.getAllAttribute_InBudget(Device.DeviceBudget.STORAGE).size()];
                    for (int i = 0; i < Device.getAllAttribute_InBudget(Device.DeviceBudget.STORAGE).size(); ++i) {
                        memory[i] = (int) Math.round(Math.pow(2, randomGenerator.nextInt(myCompany.getLevels_USE_THIS()[i]) - 1));
                        if (memory[i] == 0) {
                            memory[i]++;
                        }
                    }
                    int[] body = new int[Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).size()];
                    for (int i = 0; i < Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).size(); ++i) {
                        body[i] = randomGenerator.nextInt(myCompany.getLevels_USE_THIS()[i + 2]);
                        if (body[i] == 0) {
                            body[i]++;
                        }
                    }

                    int[] attributes=new int[Device.NUMBER_OF_ATTRIBUTES];
                    Device newDev = new Device(name,
                            (int) Math.round(myDevices.get(0).cost * (0.2 * randomGenerator.nextInt(10))),0,
                            myCompany.companyId,
                            attributes
                            );
                    newDev.cost=DeviceValidator.getOverallCost(newDev);
                    ret.delete.add(myDevices.get(minIndex));
                    ret.insert.add(newDev);
                }
        return ret;
    }
}
