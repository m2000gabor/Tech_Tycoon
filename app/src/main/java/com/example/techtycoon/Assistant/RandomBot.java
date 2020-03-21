package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;
import java.util.Random;

class RandomBot extends AbstractAssistant {
    @Override
    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {

                //random
                if (avg_lastProfit(companyList) > myCompany.lastProfit) {
                    Random randomGenerator = new Random(deviceList.hashCode());

                    int minOverallIncome = myDevices.get(0).getOverallIncome();
                    int minIndex = 0;
                    for (int i = 1; i < myDevices.size(); i++) {
                        if (myDevices.get(i).getOverallIncome() < minOverallIncome) {
                            minOverallIncome = myDevices.get(i).getOverallIncome();
                            minIndex = i;
                        }
                    }

                    //make newDev
                    String name =nameBuilder.buildName(myDevices.get(minIndex).name,1);
                    int[] memory = new int[Device.CHILDREN_OF_BUDGETS[0]];
                    for (int i = 0; i < Device.CHILDREN_OF_BUDGETS[0]; ++i) {
                        memory[i] = (int) Math.round(Math.pow(2, randomGenerator.nextInt(myCompany.getLevels_USE_THIS()[i]) - 1));
                        if (memory[i] == 0) {
                            memory[i]++;
                        }
                    }
                    int[] body = new int[Device.CHILDREN_OF_BUDGETS[1]];
                    for (int i = 0; i < Device.CHILDREN_OF_BUDGETS[1]; ++i) {
                        body[i] = randomGenerator.nextInt(myCompany.getLevels_USE_THIS()[i + 2]);
                        if (body[i] == 0) {
                            body[i]++;
                        }
                    }
                    //Device newDev = createValidDevice(name, 0, myCompany, memory, body);
                    int[] attributes=new int[Device.NUMBER_OF_ATTRIBUTES];
                    Device newDev = new Device(name,
                            (int) Math.round(myDevices.get(0).cost * (0.2 * randomGenerator.nextInt(10))),
                            myCompany.companyId,
                            attributes
                            );
                    ret.delete.add(myDevices.get(minIndex));
                    ret.insert.add(newDev);
                }
        return ret;
    }
}
