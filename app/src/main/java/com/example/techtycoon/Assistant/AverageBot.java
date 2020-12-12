package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.Converter;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.core.util.Pair;

import static com.example.techtycoon.Assistant.ToolsForAssistants.avg;
import static com.example.techtycoon.Assistant.ToolsForAssistants.avg_marketing;
import static com.example.techtycoon.Assistant.ToolsForAssistants.getRegion;
import static com.example.techtycoon.Assistant.ToolsForAssistants.min_Overall;
import static com.example.techtycoon.Assistant.ToolsForAssistants.nameBuilder;

/*
               Name: Avg
                Description: if something is worse than the avg try to update it
                Action tree:
                    -if something is worse than the avg then add to the toDoList
                    -order the list by the importance (specified in the profile)
                    -try to the everything from the most important to the least one
                Details:
                    -if the profit is low than decrease the profits on the devices
        */
public class AverageBot implements AbstractAssistant {
    private int[] profile;

    AverageBot(){}

    @Override
    public List<String> getInputLabels() {
        return Arrays.asList(
                "New Slot",
                "Marketing",
                "Ram",
                "Memory",
                "Design",
                "Material",
                "Color",
                "IP",
                "Bezels"
        );
    }

    @Override
    public String getAssistantName() {
        return "Average Bot";
    }

    @Override
    public String getDefaultStatus() {
        return Converter.intArrayToString(new int[]{
                10,5,//newslot,marketing
                3,4,//storage
                2,2,2,2,2,//body
                2,2,2,2//display
        });
    }

    private int getImportance(int attrId){
        return profile[attrId+2];
    }

    public  Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        //get profile
        String[] s=myCompany.assistantStatus.split(";");
        profile=new int[Device.NUMBER_OF_ATTRIBUTES+2];
        for(int i =0;i<s.length;i++) {
            profile[i] = Integer.parseInt(s[i]);
        }

        int marketingCost = DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
        //first: attrId; second: importance from the profile
        LinkedList<Pair<Integer,Integer>> toDoList=new LinkedList<>();

        //marketing
        if(myCompany.marketing+10 < avg_marketing(companyList)){
            toDoList.add(new Pair<>(-1,getImportance(-1)));
        }

        //newSlot
        double avgSlot=0;
        for (Company c :companyList) {avgSlot += c.maxSlots; }
        avgSlot/=companyList.size();
        if(myCompany.maxSlots<avgSlot){
            toDoList.add(new Pair<>(-2,getImportance(-2)));
        }

        //profit
        if(getRegion(companyList.stream().map(c->c.lastProfit).collect(Collectors.toList()),
                myCompany.lastProfit)<=2){
            toDoList.add(new Pair<>(-3,0));
        }

        //dev attributes
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            Device.DeviceAttribute attribute=Device.getAllAttribute().get(i);
            if(myCompany.getLevels_USE_THIS()[i]<avg(deviceList,attribute)){
                toDoList.add(new Pair<>(i,getImportance(i)));
            }
        }

        //sort
        toDoList.sort( (p1,p2) -> p2.second-p1.second );

        //try to complete the tasks
        for (Integer i :
                toDoList.stream().map((p) -> p.first).collect(Collectors.toList())) {
            if(i==-3){
                myCompany.logs = myCompany.logs + "Our profit is low. A discount is made on all devices.\n";
                //company's profit is low
                //decrease the profits on all devices
                for (Device d : myDevices) {
                    d.profit=(int) Math.ceil(d.profit*0.9);
                    ret.update.add(d);
                }
                break;
            }else if (i == -2) {
                //try to buy a new slot
                myCompany.logs = myCompany.logs + "Try to buy a new slot...\n";
                if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots) <= myCompany.money &&
                        DevelopmentValidator.nextSlotCost(myCompany.maxSlots) != -1){
                    myCompany.money -= DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                    myCompany.maxSlots++;
                    myCompany.logs = myCompany.logs + "The assistant bougth a new slot!\n";

                    //device manager
                    Device newDev = new Device(
                            nameBuilder.buildName(myDevices.get(0).name,1),
                            (int) Math.round(avg(deviceList, Device.DeviceAttribute.PROFIT_PER_ITEM)),0,
                            myCompany.companyId,
                            myCompany.getLevels_USE_THIS()
                    );
                    newDev.cost=DeviceValidator.getOverallCost(newDev);
                    ret.insert.add(newDev);
                    myCompany.usedSlots++;
                }
            }else if(i==-1){
                //try to buy marketing
                myCompany.logs = myCompany.logs + "Try to buy marketing...\n";
                while (myCompany.money >= marketingCost && myCompany.marketing+10 <= avg_marketing(companyList)) {
                    myCompany.money -= marketingCost;
                    myCompany.marketing += 10;
                    myCompany.logs = myCompany.logs + "The assistant bougth " + 10 + " marketing!\n";
                    marketingCost = DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
                }
            }else{
                //try to upgrade a device attribute
                myCompany.logs = myCompany.logs + "Try to upgrade the "+i+". attribute...\n";
                if (myCompany.money >= DevelopmentValidator.getOneDevelopmentCost(Device.getAllAttribute().get(i), myCompany.getLevels_USE_THIS()[i]) &&
                        DevelopmentValidator.getOneDevelopmentCost(Device.getAllAttribute().get(i), myCompany.getLevels_USE_THIS()[i]) != -1) {
                    myCompany.money -= DevelopmentValidator.getOneDevelopmentCost(Device.getAllAttribute().get(i), myCompany.getLevels_USE_THIS()[i]);
                    int[] levels = myCompany.getLevels_USE_THIS();
                    levels[i]++;
                    myCompany.setLevels_USE_THIS(levels);
                    myCompany.logs = myCompany.logs + i + ". attribute is upgraded!\n";

                    //device manager
                    Device newDev = new Device(
                            nameBuilder.buildName(myDevices.get(0).name,1),
                            (int) Math.round(avg(deviceList, Device.DeviceAttribute.PROFIT_PER_ITEM)),0,
                            myCompany.companyId,
                            myCompany.getLevels_USE_THIS()
                    );
                    newDev.cost= DeviceValidator.getOverallCost(newDev);

                    if (myCompany.hasFreeSlot()) {
                        ret.insert.add(newDev);
                        myCompany.usedSlots++;
                    } else {
                        ret.delete.add(myDevices.get(min_Overall(myDevices)));
                        ret.insert.add(newDev);
                        break;
                    }

                }
            }
        }

        ret.UpdateCompanies.add(myCompany);
        return ret;
    }

}
