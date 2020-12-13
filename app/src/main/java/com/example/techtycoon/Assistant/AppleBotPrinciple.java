package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;

import static com.example.techtycoon.Assistant.ToolsForAssistants.avg;
import static com.example.techtycoon.Assistant.ToolsForAssistants.getRegion;
import static com.example.techtycoon.Assistant.ToolsForAssistants.getRegion100;
import static com.example.techtycoon.Assistant.ToolsForAssistants.maxIndex;
import static com.example.techtycoon.Assistant.ToolsForAssistants.minInd;
import static com.example.techtycoon.Assistant.ToolsForAssistants.minKer;
import static com.example.techtycoon.Assistant.ToolsForAssistants.tryToBuy_NewSlot;

class AppleBotPrinciple implements AbstractAssistant {
    private List<Principle> principles;

    AppleBotPrinciple(){
        principles=new LinkedList<>();

        //slot
        principles.add(new Principle() {
            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(myCompany.maxSlots==1){return 3;
                }else if(myCompany.maxSlots>5){return 1;
                }else{return 2;}
            }

            @Override
            public String name() {
                return "New slot";
            }

            @Override
            public int defaultWeight() {
                return 110;
            }

            @Override
            public boolean needsCleanInput() {
                return false;
            }

            @Nullable
            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(ToolsForAssistants.tryToBuy_NewSlot(myCompany)){
                    return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                }else{
                    return null;
                }
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
                return 100;
            }

            @Override
            public boolean needsCleanInput() {
                return false;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                List<Integer> marketingOfOthers=new LinkedList<>();
                for (Company c : allCompanies) {
                    if (c.companyId != myCompany.companyId) {
                        marketingOfOthers.add(c.marketing);
                    }
                }
                int region=getRegion(marketingOfOthers,myCompany.marketing);
                switch (region){
                    case 1:
                        return 0;
                    case 2:
                        return 1;
                    case 3:
                        return 3;
                    case 4:
                        return 4;
                    default:
                        return 2;
                }
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(ToolsForAssistants.tryToBuy_Marketing(myCompany)){
                    return new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                }else{return null;}
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
                return true;
            }

            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                if(myCompany.usedSlots<myCompany.maxSlots){return 5;
                }else{return 0;}
            }

            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                //fill with our best, increase profit by 2$
                Device ourBest=myDevices.get(maxIndex(myDevices, Device.DeviceAttribute.OVERALL_PROFIT));
                Device newDev=new Device(ourBest);
                newDev.name= ToolsForAssistants.nameBuilder.buildName(myDevices.get(0).name,1);
                newDev.profit+=2;
                myCompany.usedSlots++;
                myCompany.logs=myCompany.logs+"\nUnused slot is filled with a copy of our best selling device ("+ourBest.name+") with 2$ more profit";
                Wrapped_DeviceAndCompanyList r =new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                r.insert.add(newDev);
                return r;
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
                    if(DevelopmentValidator.getOneDevelopmentCost(attribute,myCompany.getLevelByAttribute(attribute))==-1){return 0;}
                    //needs clean input

                    int betterMax=0;
                    int sameCount=0;
                    for(Company c : allCompanies){
                        if(c.companyId==myCompany.companyId){continue;}
                        int diff=myCompany.getLevelByAttribute(attribute)-c.getLevelByAttribute(attribute);
                        if(diff==0){sameCount++;
                        }else if(betterMax<diff){betterMax=diff;}
                    }

                    if(betterMax==0){
                        if(sameCount==0){
                            return 1;
                        }else if(sameCount<allCompanies.size()/2){return 2;
                        }else{return 4;}
                    }else if(betterMax == 1){
                        if(sameCount<allCompanies.size()/2){return 2;
                        }else{return 3;}
                    }else{
                        int region = getRegion(allCompanies.stream()
                                    .filter(c -> c.companyId!=myCompany.companyId)
                                    .map(c -> c.getLevelByAttribute(attribute))
                                    .collect(Collectors.toList())
                                ,myCompany.getLevelByAttribute(attribute));
                        if(region==1){
                            return 5;
                        }else if(region==2){
                            return 4;
                        }else{return 2;}
                    }
                }

                @Override
                public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                    if(myCompany.incrementLevel(attribute)){
                        myCompany.logs=myCompany.logs+"\n"+attribute+" level is updated";
                    }else{return null;}

                    int maxLevel=myDevices.get(0).getFieldByAttribute(attribute);
                    Device maxLevelDev=myDevices.get(0);
                    for (Device d: myDevices) {
                        if(d.getFieldByAttribute(attribute)>maxLevel){
                            maxLevel=d.getFieldByAttribute(attribute);maxLevelDev=d;}
                    }

                    Device newDev=new Device(maxLevelDev);
                    newDev.name= ToolsForAssistants.nameBuilder.buildName(maxLevelDev.name,1);
                    newDev.setFieldByAttribute(attribute,myCompany.getLevelByAttribute(attribute));
                    newDev.cost= DeviceValidator.getOverallCost(newDev);
                    Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                    r.delete.add(maxLevelDev);
                    r.insert.add(newDev);
                    return r;
                }
            });
        }

        //dev with highest perf makes the most income
        principles.add(new Principle() {
            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device highestPerf=myDevices.get(0);
                Device highestInc=myDevices.get(0);
                Device minInc=myDevices.get(0);
                for (Device d : myDevices) {
                    if(d.getScore_OverallPerformance()>highestPerf.getScore_OverallPerformance()){
                        highestPerf=d;
                    }
                    if(d.getOverallProfit()>highestInc.getOverallProfit()){highestInc=d;
                    }else if(d.getOverallProfit()<minInc.getOverallProfit()){minInc=d;}
                }
                if(highestInc.id==highestPerf.id){return 0;
                }else if(highestPerf.id==minInc.id){
                    return 5;
                }else{
                    return 3;
                }
            }

            @Override
            public String name() {
                return "Best goes best";
            }

            @Override
            public int defaultWeight() {
                return 110;
            }

            @Override
            public boolean needsCleanInput() {
                return true;
            }

            @Nullable
            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                Device highestPerf=myDevices.get(0);
                Device highestInc=myDevices.get(0);
                Device minInc=myDevices.get(0);
                for (Device d : myDevices) {
                    if(d.getScore_OverallPerformance()>highestPerf.getScore_OverallPerformance()){
                        highestPerf=d;
                    }
                    if(d.getOverallProfit()>highestInc.getOverallProfit()){highestInc=d;
                    }else if(d.getOverallProfit()<minInc.getOverallProfit()){minInc=d;}
                }

                Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                if(highestPerf.id==minInc.id ){
                    if(highestPerf.profit<=10){
                        r.update.add(highestPerf);
                    }else{
                        if(highestInc.getOverallProfit()>minInc.getOverallProfit()*3){highestPerf.profit*=0.7;
                        }else{
                            highestPerf.profit*=0.8;
                        }
                        r.update.add(highestPerf);
                        myCompany.logs=myCompany.logs+"Our highest performance device ("+highestPerf.name+") has the worst income\n"+
                        "Profit is reduced dramatically.";
                    }
                }else{
                    List<Integer> soldItems=allDevices.stream().map(Device::getSoldPieces).collect(Collectors.toList());
                    Device finalHighestPerf = highestPerf;
                    List<Device> mostPopular=allDevices.stream()
                            .filter(d ->
                                    getRegion100(soldItems, finalHighestPerf.getSoldPieces())
                                            <getRegion100(soldItems,d.getSoldPieces()) &&
                                            myCompany.producibleByTheCompany(d)
                                    )
                            .sorted((o1, o2) -> o2.profit-o1.profit)//works?
                            .collect(Collectors.toList());
                    if(mostPopular.isEmpty()){return null;}

                    //what should we promote?
                    Device.DeviceAttribute bestAttr=null;
                    double maxDiff=0;
                    for(Device.DeviceAttribute att: Device.getAllAttribute()){
                        double diff=myCompany.getLevelByAttribute(att)-avg(allDevices,att);
                        if(diff>maxDiff){maxDiff=diff;bestAttr=att;}
                    }
                    if(bestAttr==null){return null;}
                    //make
                    boolean found=false;
                    for (int i=0;i<mostPopular.size() && !found;++i){
                        if(mostPopular.get(i).getFieldByAttribute(bestAttr)<highestPerf.getFieldByAttribute(bestAttr)){
                            Device newDev=new Device(mostPopular.get(i));
                            newDev.ownerCompanyId=myCompany.companyId;
                            newDev.setFieldByAttribute(bestAttr,myCompany.getLevelByAttribute(bestAttr));
                            newDev.cost=DeviceValidator.getOverallCost(newDev);
                            if(newDev.profit>=10){newDev.profit*=0.9;}
                            myCompany.logs=myCompany.logs+"Our least profitable device("+minInc.name+") is replaced with the copy of a popular device ("+mostPopular.get(i).name+
                            ").\nOur best attribute ("+bestAttr+") is added.";
                            r.delete.add(minInc);
                            r.insert.add(newDev);
                            found=true;
                        }
                    }
                    if(!found){return null;}
                }
                return r;
            }
        });

        //extremely low profit
        principles.add(new Principle() {
            @Override
            public int getScore(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                List<Integer> l=allCompanies.stream().map(c->c.lastProfit).collect(Collectors.toList());
                int region=getRegion100(l,myCompany.lastProfit);
                if(region<10){
                    return 6;
                }else if(region<20){
                    return 5;
                }else if(region<30){
                    return 3;
                }else{return 0;}
            }

            @Override
            public String name() {
                return "Extremely low profit";
            }

            @Override
            public int defaultWeight() {
                return 110;
            }

            @Override
            public boolean needsCleanInput() {
                return true;
            }

            @Nullable
            @Override
            public Wrapped_DeviceAndCompanyList repair(Company myCompany, List<Device> myDevices, List<Company> allCompanies, List<Device> allDevices) {
                List<Integer> profits=allCompanies.stream().map(c->c.lastProfit).collect(Collectors.toList());
                List<Integer> slots=allCompanies.stream().map(c->c.maxSlots).collect(Collectors.toList());
                Wrapped_DeviceAndCompanyList r=new Wrapped_DeviceAndCompanyList(myDevices,myCompany);
                if(minKer(slots)==myCompany.maxSlots){
                    if(tryToBuy_NewSlot(myCompany)){
                        return r;
                    }else{return null;}
                }
                int region=getRegion100(profits,myCompany.lastProfit);
                if(region<10){
                    allDevices.sort((x,y)->y.getOverallProfit()-x.getOverallProfit());
                    for (Device d :allDevices) {
                        if(myCompany.producibleByTheCompany(d)){
                            Device minDev=myDevices.get(minInd(myDevices, Device.DeviceAttribute.OVERALL_PROFIT));
                            Device newDev=new Device(d);
                            if(newDev.profit>=10){newDev.profit*=0.8;}
                            newDev.ownerCompanyId=myCompany.companyId;
                            newDev.name= ToolsForAssistants.nameBuilder.buildName(myDevices.get(0).name,1);
                            r.delete.add(minDev);
                            r.insert.add(newDev);
                            return r;
                        }
                    }
                }else if(region<20){
                    for (Device d :myDevices) {if(d.profit>=10){d.profit*=0.7;} }
                    r.update.addAll(myDevices);
                }else if(region<30){
                    for (Device d :myDevices) {if(d.profit>=10){d.profit*=0.8;}}
                    r.update.addAll(myDevices);
                }
                return r;
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

    @Override
    public List<String> getInputLabels() {
        return principles.stream().map(Principle::name).collect(Collectors.toList());
    }

    @Override
    public String getAssistantName() {
        return "Apple's principle bot";
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
}
