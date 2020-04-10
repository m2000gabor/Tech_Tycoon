package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.techtycoon.Assistant.ToolsForAssistants.*;

class XiaomiBot implements AbstractAssistant{
    private ArrayList<Principle> principles=new ArrayList<>();

    @Override
    public List<String> getInputHints() {
        return Arrays.asList(
                "New slot importance",
                "Marketing",
                "Unused slot",
                "Low profit on devices to others",
                "Ram",
                "Memory",
                "Design",
                "Material",
                "Color",
                "IP",
                "Bezel"
        );
    }

    @Override
    public String getAssistantName() {
        return "Xiaomi bot";
    }

    XiaomiBot(){
        //slots
        principles.add(new Principle() {
            @Override
            public double getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int counter=0;
                for (Company c : allCompanies) {
                    if(c.companyId!=myCompany.companyId && c.maxSlots>=myCompany.maxSlots){counter++;}
                }
                return 100*( ((double)counter) / (allCompanies.size()-1) );
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots)<= myCompany.money &&
                        DevelopmentValidator.nextSlotCost(myCompany.maxSlots)!=-1 ){
                    myCompany.money-=DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                    myCompany.maxSlots++;
                    myCompany.logs=myCompany.logs+"\nA new slot is bought!";
                    return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                }
                return null;
            }
        });

        //marketing
        principles.add(new Principle() {
            @Override
            public double getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int counter=0;
                for (Company c : allCompanies) {
                    if(c.companyId!=myCompany.companyId && c.marketing>=myCompany.marketing){counter++;}
                }
                return 100*( ((double)counter) / (allCompanies.size()-1) );
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(DevelopmentValidator.calculateMarketingCost(myCompany.marketing)<= myCompany.money &&
                        DevelopmentValidator.calculateMarketingCost(myCompany.marketing)!=-1 ){
                    myCompany.money-=DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
                    myCompany.marketing+=10;
                    myCompany.logs=myCompany.logs+"\n10 marketing is bought!";
                    return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                }
                return null;
            }
        });

        //unused slot
        principles.add(new Principle() {
            @Override
            public double getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(myCompany.usedSlots<myCompany.maxSlots){return 100;
                }else{return 0;}
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                List<Device> sortedAllDevices=allDevices.stream()
                        .sorted((a,b)->b.getOverallIncome()-a.getOverallIncome())
                        .filter(d->producibleByTheCompany(myCompany,d))
                        .collect(Collectors.toList());
                if(sortedAllDevices.get(0)!=null){
                    Device newDev=new Device(sortedAllDevices.get(0));
                    myDevices.add(newDev);
                    myCompany.usedSlots++;
                    myCompany.logs=myCompany.logs+"\nUnused slot is filled with a copy of a market-leader!";
                    return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);}
                return null;
            }
        });

        //Max of our devices' profit compared to others
        principles.add(new Principle() {
            @Override
            public double getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int maxInd=maxIndex(myDevices,-1);

                List<Integer> notMyDevicesProfit=new LinkedList<>();
                for (Device d :allDevices) {if(!myDevices.contains(d)){notMyDevicesProfit.add(d.profit);}}

                return (double) getRegion100(notMyDevicesProfit,myDevices.get(maxInd).profit);
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int maxInd=maxIndex(myDevices,-1);
                Device d=myDevices.get(maxInd);
                d.profit-=1;
                myCompany.logs=myCompany.logs+"\n"+d.name+"'s had too high profit -> lowered by 1$.";
                return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
            }
        });

        //attriutes
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            final int finalI = i;
            principles.add(new Principle() {
                @Override
                public double getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                    int counter=0;
                    for (Company c : allCompanies) {
                        if(c.companyId!=myCompany.companyId && c.getLevels_USE_THIS()[finalI]>=myCompany.getLevels_USE_THIS()[finalI]){counter++;}
                    }
                    return 100*( ((double)counter) / (allCompanies.size()-1) );
                }

                @Override
                public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                    if(myCompany.incrementLevel(finalI)){
                        myCompany.logs=myCompany.logs+"\n"+finalI+". level is updated";
                        return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        List<Integer> weights=new LinkedList<>();
        String[] tmp=myCompany.assistantStatus.split(";");
        for (String str :tmp) { weights.add(Integer.parseInt(str));}
        PrincipleBot p=new PrincipleBot(20,weights,principles);
        return p.work(companyList, deviceList, myDevices, myCompany, ret);
    }
}
