package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.techtycoon.Assistant.ToolsForAssistants.*;

class XiaomiBot implements AbstractAssistant{
    private List<Principle> principles;

    @Override
    public List<String> getInputLabels() {
        return principles.stream().map(Principle::name).collect(Collectors.toList());
    }

    @Override
    public String getAssistantName() {
        return "Xiaomi bot";
    }

    @Override
    public String getDefaultStatus() {
        StringBuilder sb=new StringBuilder();
        for (Principle p :principles) {
            sb.append(p.defaultWeight()).append(';');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    XiaomiBot(){
        principles=new LinkedList<>();
        //slots
        principles.add(new Principle() {
            @Override
            public String name() {
                return "New Slot";
            }

            @Override
            public int defaultWeight() {
                return 120;
            }

            @Override
            public boolean needsCleanInput() {return false;}

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int counter=0;
                for (Company c : allCompanies) {
                    if(c.companyId!=myCompany.companyId && c.maxSlots>myCompany.maxSlots){counter++;}
                }
                double percent=100*( ((double)counter) / (allCompanies.size()-1) );
                if(Double.isNaN(percent)){return 1;}
                if(percent==100){return 5;
                }else if(percent>75){return 4;
                }else if(percent>51){return 3;
                }else if(percent>30){return 2;
                }else {return 1;}
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
            public String name() {
                return "Marketing";
            }

            @Override
            public int defaultWeight() {
                return 90;
            }

            @Override
            public boolean needsCleanInput() {
                return false;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                List<Integer> marketingOfOthers=new LinkedList<>();
                for (Company c :
                        allCompanies) {
                    if (c.companyId != myCompany.companyId) {
                        marketingOfOthers.add(c.marketing);
                    }
                }
                int region=getRegion(marketingOfOthers,myCompany.marketing);
                if(Double.isNaN(region)){return 1;}

                switch (region){
                    case 1:
                        return 4;
                    case 2:
                    case 3:
                        return 2;
                    default:
                        return 1;
                }
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
            public String name() {
                return "Unused slot";
            }

            @Override
            public int defaultWeight() {
                return 1000;
            }

            @Override
            public boolean needsCleanInput() {
                return false;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(myCompany.usedSlots<myCompany.maxSlots){return 5;
                }else{return 0;}
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                List<Device> sortedAllDevices=allDevices.stream()
                        .sorted((a,b)->b.getOverallIncome()-a.getOverallIncome())
                        .filter(myCompany::producibleByTheCompany)
                        .collect(Collectors.toList());
                if(sortedAllDevices.get(0)!=null){
                    Device newDev=new Device(sortedAllDevices.get(0));
                    newDev.ownerCompanyId=myCompany.companyId;
                    newDev.name=nameBuilder.buildName(myDevices.get(0).name,1);
                    myCompany.usedSlots++;
                    myCompany.logs=myCompany.logs+"\nUnused slot is filled with a copy of a market-leader: "+sortedAllDevices.get(0).name;
                    Wrapped_DeviceAndCompanyList r =new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    r.insert.add(newDev);
                    return r;}
                return null;
            }
        });

        //Max of our devices' profit compared to others
        /*
        principles.add(new Principle() {
            @Override
            public String name() {
                return "Profit to others";
            }

            @Override
            public int defaultWeight() {
                return 100;
            }

            @Override
            public boolean needsCleanInput() {
                return false;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int maxInd=maxIndex(myDevices,-1);

                List<Integer> notMyDevicesProfit=new LinkedList<>();
                for (Device d :allDevices) {if(!myDevices.contains(d)){notMyDevicesProfit.add(d.profit);}}

                int region=getRegion(notMyDevicesProfit,myDevices.get(maxInd).profit);
                if(Double.isNaN(region)){return 1;}

                switch (region){
                    case 1:
                    case 2:
                        return 1;
                    case 3:
                        return 2;
                    case 4:
                        return 4;
                    default:
                        return 5;
                }
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                int maxInd=maxIndex(myDevices,-1);
                Device d=myDevices.get(maxInd);
                d.profit*=0.95;
                myCompany.logs=myCompany.logs+"\n"+d.name+"'s had too high profit -> lowered by 5%.";
                Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                r.update.add(d);
                return r;
            }
        });
        */

        //producible is before us
        principles.add(new Principle() {
            @Override
            public String name() {
                return "Price run";
            }

            @Override
            public int defaultWeight() {
                return 110;
            }

            @Override
            public boolean needsCleanInput() {
                return true;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device minDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallIncome()>dev.getOverallIncome()){
                        minDev=dev;
                    }
                }

                Device betterDev=null;
                for (Device d : allDevices) {
                    if(d.ownerCompanyId!=myCompany.companyId &&
                            minDev.getOverallIncome()<d.getOverallIncome() &&
                            myCompany.producibleByTheCompany(d)){
                        if(betterDev==null){betterDev=d;
                        }else if(d.getOverallIncome()>betterDev.getOverallIncome()){betterDev=d;}
                    }
                }
                if(betterDev==null){return 0;}

                if(betterDev.getOverallIncome()>minDev.getOverallIncome()*3){
                    return 4;
                }else if(betterDev.getOverallIncome()>minDev.getOverallIncome()*2){
                    return 3;
                }else if(betterDev.getOverallIncome()>minDev.getOverallIncome()*1.5){
                    return 2;
                }else if(betterDev.getOverallIncome()>minDev.getOverallIncome()*1.2){
                    return 1;
                }
                return 0;
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device minDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallIncome()>dev.getOverallIncome()){
                        minDev=dev;
                    }
                }

                Device betterDev=null;
                for (Device d : allDevices) {
                    if(d.ownerCompanyId!=myCompany.companyId &&
                            minDev.getOverallIncome()<d.getOverallIncome() &&
                            myCompany.producibleByTheCompany(d)){
                        if(betterDev==null){betterDev=d;
                        }else if(d.getOverallIncome()>betterDev.getOverallIncome()){betterDev=d;}
                    }
                }

                if(betterDev==null){
                   throw new IllegalStateException("There's no device like that! Why was this method called?");
                }else{
                    Device newDev=new Device(betterDev);
                    newDev.name=nameBuilder.buildName(myDevices.get(0).name,1);
                    newDev.ownerCompanyId=myCompany.getCompanyId();
                    newDev.profit*=0.95;
                    Wrapped_DeviceAndCompanyList w=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    w.delete.add(minDev);
                    w.insert.add(newDev);
                    return w;
                }
            }
        });

        //make the best with higher profit
        principles.add(new Principle() {
            @Override
            public String name() {
                return "Clone the best";
            }

            @Override
            public int defaultWeight() {
                return 111;
            }

            @Override
            public boolean needsCleanInput() {
                return true;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device minDev=myDevices.get(0);
                Device maxDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallIncome()>dev.getOverallIncome()*1.1){
                        minDev=dev;
                    }else if(maxDev.getOverallIncome()*1.1<dev.getOverallIncome()){
                        maxDev=dev;
                    }
                }

                if(minDev.id==maxDev.id){return 0;}

                if(maxDev.getOverallIncome()>minDev.getOverallIncome()*5){
                    return 5;
                }else if(maxDev.getOverallIncome()>minDev.getOverallIncome()*3){
                    return 4;
                }else if(maxDev.getOverallIncome()>minDev.getOverallIncome()*2){
                    return 3;
                }else if(maxDev.getOverallIncome()>minDev.getOverallIncome()*1.5){
                    return 2;
                }else if(maxDev.getOverallIncome()>minDev.getOverallIncome()*1.2){
                    return 1;
                }
                return 0;
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device minDev=myDevices.get(0);
                Device maxDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallIncome()>dev.getOverallIncome()){
                        minDev=dev;
                    }else if(maxDev.getOverallIncome()<dev.getOverallIncome()){
                        maxDev=dev;
                    }
                }

                if(minDev.id==maxDev.id){
                    throw new IllegalStateException("There's no device like that! Why was this method called?");
                }else{
                    Device newDev=new Device(maxDev);
                    if(maxDev.getOverallIncome()>1.5*minDev.getOverallIncome()){
                        newDev.name=nameBuilder.buildName(myDevices.get(0).name,0);
                        newDev.profit+=5;
                    }else{
                        newDev.name=nameBuilder.buildName(myDevices.get(0).name,0);
                        newDev.profit+=2;
                    }

                    myCompany.logs="\n"+myCompany.logs+maxDev.name+" is cloned and replaced the "+minDev.name;
                    Wrapped_DeviceAndCompanyList w=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    w.delete.add(minDev);
                    w.insert.add(newDev);
                    return w;
                }
            }
        });

        //attributes
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            final int finalI = i;
            principles.add(new Principle() {
                @Override
                public String name() {
                    return "Devices' attribute"+finalI;
                }

                @Override
                public int defaultWeight() {
                    return 100;
                }

                @Override
                public boolean needsCleanInput() {
                    return true;
                }

                @Override
                public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                    //needs clean input
                    int minLevels=10000;
                    int minInd=0;
                    for (int i = 0; i < myDevices.size(); i++) {
                        if(myDevices.get(i).getFieldByNum(finalI)<minLevels){
                            minLevels=myDevices.get(i).getFieldByNum(finalI);minInd=i;}
                    }
                    Device minDevice=myDevices.get(minInd);

                    int betterIncome=0;
                    int betterAttributeLevel=0;
                    int sameAttributeLevel=0;
                        for(int i=0;i<allDevices.size();i++){
                            if(allDevices.get(i).ownerCompanyId==myCompany.companyId){continue;}
                            if(minDevice.getOverallIncome()<allDevices.get(i).getOverallIncome()
                            ){
                                betterIncome++;
                                if(minDevice.getFieldByNum(finalI)<allDevices.get(i).getFieldByNum(finalI)){
                                    betterAttributeLevel++;
                                }else if(minDevice.getFieldByNum(finalI)==allDevices.get(i).getFieldByNum(finalI)){
                                    sameAttributeLevel++;
                                }
                            }
                        }
                        double better=100*((double)betterAttributeLevel/betterIncome);
                        double same=100*((double)sameAttributeLevel/betterIncome);

                        if(better==100){
                            return 5;
                        }else if(better>80){
                            return 4;
                        }else if(better>51 || better+same==100){
                            return 3;
                        }else if(better>35 || better+same>70){
                            return 2;
                        }else {return 1;}
                }

                @Override
                public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                    if(myCompany.incrementLevel(finalI)){
                        myCompany.logs=myCompany.logs+"\n"+finalI+". level is updated";
                    }else{return null;}

                    int minLevels=10000;
                    int minInd=0;
                    for (int i = 0; i < myDevices.size(); i++) {
                        if(myDevices.get(i).getFieldByNum(finalI)<minLevels){
                            minLevels=myDevices.get(i).getFieldByNum(finalI);minInd=i;}
                    }
                    Device minDevice=myDevices.get(minInd);
                    Device newDev=new Device(minDevice);
                    newDev.name=nameBuilder.buildName(minDevice.name,1);
                    newDev.setFieldByNum(finalI,minDevice.getFieldByNum(finalI)+1);
                    Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    r.delete.add(minDevice);
                    r.insert.add(newDev);
                    return r;
                }
            });
        }

    }

    @Override
    public Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        List<Integer> weights=new LinkedList<>();
        String[] status=myCompany.assistantStatus.split(";");
        for (String s : status) {
            weights.add(Integer.parseInt(s));
        }
        PrincipleBot p=new PrincipleBot(100,weights,principles);
        return p.work(companyList, deviceList, myDevices, myCompany, ret);
    }
}
