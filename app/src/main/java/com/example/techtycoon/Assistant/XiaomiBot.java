package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
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
                return 110;
            }

            @Override
            public boolean needsCleanInput() {return false;}

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                //Based on: how many companies have more slots then us
                if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots)==-1){return 0;}
                int counter=0;
                for (Company c : allCompanies) {
                    if(c.companyId!=myCompany.companyId && c.maxSlots>myCompany.maxSlots){counter++;}
                }
                double percent=100*( ((double)counter) / (allCompanies.size()-1) );
                if(Double.isNaN(percent)){return 1;}
                if(percent>75){return 4;
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
                //based on: in which region our marketing score lies. Preferred: region2,3 out of 5
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
                if(myCompany.usedSlots>=myCompany.maxSlots){throw new IllegalStateException("There is no unused slot. Why is this method called?");}
                List<Device> sortedAllDevices=allDevices.stream()
                        .sorted((a,b)->b.getOverallProfit()-a.getOverallProfit())
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

        //producible is before us - price run
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
                //producible is before us
                Device minDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallProfit()>dev.getOverallProfit()){
                        minDev=dev;
                    }
                }
                //search for a better and producible device
                Device betterDev=null;
                for (Device d : allDevices) {
                    if(d.ownerCompanyId!=myCompany.companyId &&
                            minDev.getOverallProfit()<d.getOverallProfit() &&
                            myCompany.producibleByTheCompany(d)){
                        if(betterDev==null){betterDev=d;
                        }else if(d.getOverallProfit()>betterDev.getOverallProfit()){betterDev=d;}
                    }
                }
                if(betterDev==null){return 0;} //none is found
                if(betterDev.getOverallProfit()>minDev.getOverallProfit()*3){
                    return 4;
                }else if(betterDev.getOverallProfit()>minDev.getOverallProfit()*2){
                    return 3;
                }else if(betterDev.getOverallProfit()>minDev.getOverallProfit()*1.5){
                    return 2;
                }else if(betterDev.getOverallProfit()>minDev.getOverallProfit()*1.2){
                    return 1;
                }
                return 0;
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device minDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallProfit()>dev.getOverallProfit()){
                        minDev=dev;
                    }
                }

                Device betterDev=null;
                for (Device d : allDevices) {
                    if(d.ownerCompanyId!=myCompany.companyId &&
                            minDev.getOverallProfit()<d.getOverallProfit() &&
                            myCompany.producibleByTheCompany(d)){
                        if(betterDev==null){betterDev=d;
                        }else if(d.getOverallProfit()>betterDev.getOverallProfit()){betterDev=d;}
                    }
                }

                if(betterDev==null){
                   throw new IllegalStateException("There's no device like that! Why was this method called?");
                }else{
                    Device newDev=new Device(betterDev);
                    newDev.name=nameBuilder.buildName(myDevices.get(0).name,1);
                    newDev.ownerCompanyId=myCompany.getCompanyId();
                    if(newDev.profit<3){
                        newDev.profit=10;
                    }else{newDev.profit*=0.95;}
                    Wrapped_DeviceAndCompanyList w=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    w.delete.add(minDev);
                    w.insert.add(newDev);
                    return w;
                }
            }
        });

        //clone the best with higher profit
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
                //activation: minDev compared to maxDev
                Device minDev=myDevices.get(0);
                Device maxDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallProfit()>dev.getOverallProfit()*1.1){
                        minDev=dev;
                    }else if(maxDev.getOverallProfit()*1.1<dev.getOverallProfit()){
                        maxDev=dev;
                    }
                }

                if(minDev.id==maxDev.id){return 0;}

                if(maxDev.getOverallProfit()>minDev.getOverallProfit()*5){
                    return 5;
                }else if(maxDev.getOverallProfit()>minDev.getOverallProfit()*3){
                    return 4;
                }else if(maxDev.getOverallProfit()>minDev.getOverallProfit()*2){
                    return 3;
                }else if(maxDev.getOverallProfit()>minDev.getOverallProfit()*1.5){
                    return 2;
                }else if(maxDev.getOverallProfit()>minDev.getOverallProfit()*1.2){
                    return 1;
                }
                return 0;
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device minDev=myDevices.get(0);
                Device maxDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallProfit()>dev.getOverallProfit()){
                        minDev=dev;
                    }else if(maxDev.getOverallProfit()<dev.getOverallProfit()){
                        maxDev=dev;
                    }
                }

                if(minDev.id==maxDev.id){
                    throw new IllegalStateException("There's no device like that! Why was this method called?");
                }else{
                    Device newDev=new Device(maxDev);
                    if(maxDev.getOverallProfit()>1.5*minDev.getOverallProfit()){
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
        for (Device.DeviceAttribute attribute : Device.getAllAttribute()) {
            principles.add(new Principle() {
                @Override
                public String name() {
                    return "Devices' attribute"+attribute;
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
                    //based on: how many devices have better income and same or higher attribute (same and higher are handled differently)
                    if(DevelopmentValidator.getOneDevelopmentCost(attribute,myCompany.getLevelByAttribute(attribute))==-1){return 0;}
                    //needs clean input
                    int minLevels=10000;
                    int minInd=0;
                    for (int i = 0; i < myDevices.size(); i++) {
                        if(myDevices.get(i).getFieldByAttribute(attribute)<minLevels){
                            minLevels=myDevices.get(i).getFieldByAttribute(attribute);minInd=i;}
                    }
                    Device minDevice=myDevices.get(minInd);

                    int betterIncome=0;
                    int betterAttributeLevel=0;
                    int sameAttributeLevel=0;
                        for(int i=0;i<allDevices.size();i++){
                            if(allDevices.get(i).ownerCompanyId==myCompany.companyId){continue;}
                            if(minDevice.getOverallProfit()<allDevices.get(i).getOverallProfit() )
                            {
                                betterIncome++;
                                if(minDevice.getFieldByAttribute(attribute)<allDevices.get(i).getFieldByAttribute(attribute)){
                                    betterAttributeLevel++;
                                }else if(minDevice.getFieldByAttribute(attribute)==allDevices.get(i).getFieldByAttribute(attribute)){
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
                    if(myCompany.incrementLevel(attribute)){
                        myCompany.logs=myCompany.logs+"\n"+attribute+" level is updated";
                    }else{return null;}

                    int minLevels=10000;
                    int minInd=0;
                    for (int i = 0; i < myDevices.size(); i++) {
                        if(myDevices.get(i).getFieldByAttribute(attribute)<minLevels){
                            minLevels=myDevices.get(i).getFieldByAttribute(attribute);minInd=i;}
                    }
                    Device minDevice=myDevices.get(minInd);
                    Device newDev=new Device(minDevice);
                    newDev.name=nameBuilder.buildName(minDevice.name,1);
                    newDev.setFieldByAttribute(attribute,minDevice.getFieldByAttribute(attribute)+1);
                    newDev.cost= DeviceValidator.getOverallCost(newDev);
                    Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    r.delete.add(minDevice);
                    r.insert.add(newDev);
                    return r;
                }
            });
        }

        //extremely low profit
        principles.add(new Principle() {
            @Override
            public String name() {
                return "Low profit";
            }

            @Override
            public int defaultWeight() {
                return 120;
            }

            @Override
            public boolean needsCleanInput() {
                return true;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                //profit region is really low compared to others
                int region=getRegionWithout(allCompanies.stream().map(c -> c.lastProfit).collect(Collectors.toList()), myCompany.lastProfit,myCompany.lastProfit);
                if(region==1){
                    return 5;
                }else if(region==2){
                    return 2;
                }else{return 0;}
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                //profit region is really low compared to others
                //try to clone a device with higher profit from another company
                //try to
                Device minDev=myDevices.get(0);
                for (Device dev : myDevices) {
                    if(minDev.getOverallProfit()>dev.getOverallProfit()){
                        minDev=dev;
                    }
                }

                Device betterDev=null;
                for (Device d : allDevices) {
                    if(d.ownerCompanyId!=myCompany.companyId &&
                            minDev.getOverallProfit()<d.getOverallProfit() &&
                            myCompany.producibleByTheCompany(d)){

                        //if the minDev has been made based on this d then skip
                        if(minDev.compareAttributes(d)==0 && minDev.profit==d.profit*1.1){ continue;}

                        if(betterDev==null){betterDev=d;
                        }else if(d.getOverallProfit()>betterDev.getOverallProfit()){betterDev=d;}
                    }
                }

                if(betterDev==null){
                    //make a minimal device
                    Device newDev=new Device("minimalDevice",10,0,myCompany.companyId);
                    for(Device.DeviceAttribute attribute : Device.getAllAttribute()){
                    newDev.setFieldByAttribute(attribute,1);}
                    newDev.name=nameBuilder.buildName(myDevices.get(0).name,1);
                    newDev.cost=DeviceValidator.getOverallCost(newDev);
                    Wrapped_DeviceAndCompanyList w=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    w.delete.add(minDev);
                    w.insert.add(newDev);
                    return w;
                }else{
                    Device newDev=new Device(betterDev);
                    newDev.name=nameBuilder.buildName(myDevices.get(0).name,1);
                    newDev.ownerCompanyId=myCompany.getCompanyId();
                    if(newDev.profit<3){
                        newDev.profit=10;
                    }else{newDev.profit*=1.1;}
                    Wrapped_DeviceAndCompanyList w=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    w.delete.add(minDev);
                    w.insert.add(newDev);
                    return w;
                }
            }
        });

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
