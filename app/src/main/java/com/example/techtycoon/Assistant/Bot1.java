package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import androidx.core.util.Pair;

import static com.example.techtycoon.Assistant.ToolsForAssistants.getCompanyContext;
import static com.example.techtycoon.Assistant.ToolsForAssistants.getRegion;
import static com.example.techtycoon.Assistant.ToolsForAssistants.max_Overall;
import static com.example.techtycoon.Assistant.ToolsForAssistants.min_Overall;
import static com.example.techtycoon.Assistant.ToolsForAssistants.nameBuilder;
import static com.example.techtycoon.Assistant.ToolsForAssistants.priceMaker;


class Bot1 implements AbstractAssistant {

    Bot1() {}

    @Override
    public List<String> getInputLabels() {
        return null;
    }

    @Override
    public String getAssistantName() {
        return "Bot1";
    }

    @Override
    public String getDefaultStatus() {
        return "";
    }

    public Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret){
        //profile of the assistant
        final double marketingImp=0.2;
        final int newSlotImp=10;
        final int[] devImportance={
                5,5,
                3,3,3,3,3,
                3,3,3,3
        };


        //context of my company
        //should the assist move, how important is it?
        int companyPosition= getCompanyContext(companyList,myCompany);
        //status:"companyContext,madeMoveId1,madeMoveId2..."
        String status=String.valueOf(companyPosition);
        int tryTimes=1;
        if(companyPosition==4){tryTimes++;
        }else if(companyPosition==3 ){tryTimes=3;
        }else if(companyPosition==2){tryTimes=4;
        }else if(companyPosition==1 ){tryTimes=Device.NUMBER_OF_ATTRIBUTES+2;}

        //find the most important goals
        LinkedList<Pair<Integer,Double>> pairs=new LinkedList<>();

        //get the importanceScore of the newSlot
        double newSlotContext=10-myCompany.usedSlots;
        double newSlotScore = newSlotImp * newSlotContext / DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
        if( DevelopmentValidator.nextSlotCost(myCompany.maxSlots) ==-1){newSlotScore=0;}
        pairs.add(new Pair<>(-2,newSlotScore));

        //get the marketing's context for the importance score
        LinkedList<Integer> marketings=new LinkedList<>();
        for (Company c :companyList) {
            if(c.equals(myCompany)){continue;}
            marketings.add(c.marketing);
        }
        double marketingContext = 2*getRegion(marketings,myCompany.marketing);
        if(marketingContext==6 || marketingContext==4 ){marketingContext=0;}
        if(marketingContext==8 || marketingContext==2){marketingContext=2;}

        //get the importanceScore of the marketing
        double marketingScore=marketingImp * marketingContext / (DevelopmentValidator.calculateMarketingCost(myCompany.marketing)) ;
        pairs.add(new Pair<>(-1,marketingScore));

        int k=0;
        for (Device.DeviceAttribute attribute:Device.getAllAttribute()) {
            LinkedList<Integer> list=new LinkedList<Integer>();
            for(Device d:deviceList){list.add(d.getFieldByAttribute(attribute));}
            double context=2*getRegion(list,myCompany.getLevelByAttribute(attribute));
            double score;
            if( DevelopmentValidator.getOneDevelopmentCost(attribute,myCompany.getLevelByAttribute(attribute))== -1){score=0;
            }else{score = devImportance[k] * context / DevelopmentValidator.getOneDevelopmentCost(attribute,
                    myCompany.getLevelByAttribute(attribute));}
            pairs.add(new Pair<>(k,score));
            k++;
        }
        //most important is at the 0 index
        Collections.sort(pairs, new Comparator<Pair<Integer,Double>>() {
            @Override
            public int compare(Pair<Integer,Double> o1, Pair<Integer,Double> o2) {
                return (int) Math.round((o2.second-o1.second)*1000000000);
            }
        });

        myCompany.logs=myCompany.logs+"Trying times: "+tryTimes+"\n";
        while (tryTimes>0) {
            int key=pairs.pop().first;
            if (key == -1) {
                myCompany.logs=myCompany.logs+"Trying to improve marketing...\n";
                //marketing is the most imp priority
                if ( (myCompany.money >= DevelopmentValidator.calculateMarketingCost(myCompany.marketing) &&
                        myCompany.lastProfit>DevelopmentValidator.calculateMarketingCost(myCompany.marketing))  ) {
                    myCompany.money -= DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
                    myCompany.marketing += 10;
                    myCompany.logs=myCompany.logs+"Assistant bought 10 marketing!\n";
                    status=status+";"+key;
                }
            } else if (key == -2) {
                myCompany.logs=myCompany.logs+"Trying to buy a new slot...\n";
                //new slot is the highest priority
                if (myCompany.money >= DevelopmentValidator.nextSlotCost(myCompany.maxSlots) &&
                        DevelopmentValidator.nextSlotCost(myCompany.maxSlots) !=-1
                ) {
                    myCompany.money -= DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                    myCompany.maxSlots += 1;
                    myCompany.logs=myCompany.logs+"Assistant bought a new slot!\n";
                    status=status+";"+key;
                }
            } else {
                myCompany.logs=myCompany.logs+"Trying to improve the "+key+". attribute...\n";
                //an attribute dev is the highest priority
                if (myCompany.money >= DevelopmentValidator.getOneDevelopmentCost(Device.getAllAttribute().get(key),
                        myCompany.getLevels_USE_THIS()[key]) &&
                        DevelopmentValidator.getOneDevelopmentCost(Device.getAllAttribute().get(key),myCompany.getLevels_USE_THIS()[key]) !=-1
                ) {
                    int[] lvls = myCompany.getLevels_USE_THIS();
                    myCompany.money -= DevelopmentValidator.getOneDevelopmentCost(Device.getAllAttribute().get(key),
                            myCompany.getLevels_USE_THIS()[key]);
                    lvls[key]++;
                    myCompany.setLevels_USE_THIS(lvls);
                    myCompany.logs=myCompany.logs+key +".attribute is updated by the assistant!\n";
                    status=status+";"+key;
                }
            }
            tryTimes--;
        }
        String[] newStatusArr=status.split(";");
        int[] newStatusInts=new int[newStatusArr.length];
        for (int i=0;i<newStatusArr.length && !newStatusArr[i].equals("");i++) {
            newStatusInts[i]=Integer.parseInt(newStatusArr[i]); }

        //add a new device if needed
        if(newStatusInts[0]<=2 || newStatusInts.length>2 || (newStatusInts.length==2 && newStatusInts[1]!=-1) ) {
            Device newDev = new Device(myDevices.get(max_Overall(myDevices)));
            newDev.name = nameBuilder.buildName(newDev.name, 1);
            for (int i = 1; i < newStatusInts.length; i++) {
                int attrId = newStatusInts[i];
                if(attrId >=0){
                    newDev.setFieldByAttribute(Device.getAllAttribute().get(attrId), myCompany.getLevels_USE_THIS()[attrId]);}
            }

            newDev.profit=priceMaker(newDev,deviceList,myDevices,newStatusInts[0]);
            newDev.cost= DeviceValidator.getOverallCost(newDev);

            if (myCompany.hasFreeSlot()) {
                ret.insert.add(newDev);
                myCompany.usedSlots++;
            } else {
                ret.delete.add(myDevices.get(min_Overall(myDevices)));
                ret.insert.add(newDev);
            }
        }
        ret.UpdateCompanies.add(myCompany);
        return ret;
    }
}
