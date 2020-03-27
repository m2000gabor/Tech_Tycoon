package com.example.techtycoon.Assistant;

import android.util.Pair;

import com.example.techtycoon.Company;
import com.example.techtycoon.DetailsOfOneCompany;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AppleBot extends AbstractAssistant{

    AppleBot() { }

     Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret){
        /*
        monopol company
          3 key development points: slot, marketing, research
         release points: overpriced, unique(best tech); lower priced high-middle;
         goal: prices should increase in general in the market, higher attributes should be used by more people

         0: choose a longTerm goal
         1: revise the longTerm goal in the changed market, might change it
         2: determine a shortTerm/instant goal
         3: decide whether save money for the long term or react to the market
         */

        //status:"companyContext"?
        /* goal:
        -4: spread our unique, strong future
        -3: go up the prices
        -2: make more devices = buy new slot
        -1: become marketing giant
        0-NumOfAttr: develop a unique, strong attribute (attrId is stored)
         */
        int longTermGoal;

        //try to accomplish the long term goal
        boolean accomplished;
        boolean flag_newAttribute=false;
        boolean flag_newSlot=false;
        do {
            //Choose a longTerm goal
            longTermGoal=findLongTermGoal(companyList,myCompany);
            myCompany.logs = myCompany.logs+"Long term goal: "+longTermGoal + "\n";
            accomplished=false;
            if (longTermGoal != -100) {
                if (longTermGoal == -2 ) {
                    if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots) <= myCompany.money &&
                            DevelopmentValidator.nextSlotCost(myCompany.maxSlots) != -1){
                        myCompany.money -= DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                        myCompany.maxSlots++;
                        myCompany.logs = myCompany.logs + "The assistant bougth a new slot!\n";
                        flag_newSlot=true;
                        accomplished=true;
                    }
                } else if (longTermGoal == -1) {
                    LinkedList<Integer> marketings = new LinkedList<>();
                    for (Company c : companyList) {
                        if (!c.equals(myCompany)) {
                            marketings.add(c.marketing);
                        }
                    }
                    int i = 0;
                    while (getRegion(marketings, myCompany.marketing) < 4 && myCompany.money >=
                            DevelopmentValidator.calculateMarketingCost(myCompany.marketing)) {
                        myCompany.money -= DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
                        myCompany.marketing += 10;
                        i++;
                        accomplished=true;
                    }
                    myCompany.logs = myCompany.logs + "The assistant bougth " + i * 10 + " marketing!\n";
                } else if (myCompany.money >= DevelopmentValidator.getOneDevelopmentCost(longTermGoal, myCompany.getLevels_USE_THIS()[longTermGoal]) &&
                        DevelopmentValidator.getOneDevelopmentCost(longTermGoal, myCompany.getLevels_USE_THIS()[longTermGoal]) != -1) {
                    myCompany.money -= DevelopmentValidator.getOneDevelopmentCost(longTermGoal, myCompany.getLevels_USE_THIS()[longTermGoal]);
                    int[] levels = myCompany.getLevels_USE_THIS();
                    levels[longTermGoal]++;
                    myCompany.setLevels_USE_THIS(levels);
                    myCompany.logs = myCompany.logs + longTermGoal + ". attribute is upgraded!\n";
                    flag_newAttribute=true;
                    accomplished=true;
                }
            }
        }while (accomplished);

        //2: determine a shortTerm/instant goal (manage the devices/might release)
        //if an upgrade is made then release a device
        Device worstDev=myDevices.get(min_Overall(myDevices));
        if(flag_newAttribute){
            myCompany.logs = myCompany.logs + "An attribute is freshly developed.\nA new device with that attribute is made!\n";
            Device newDev= new Device(nameBuilder.buildName(worstDev.name,1)
                    ,(int) Math.round(max(myDevices.toArray(new Device[0]),-1)*1.2),0
                    ,myCompany.companyId,myCompany.getLevels_USE_THIS());
            newDev.cost=DeviceValidator.getOverallCost(newDev);
            if (myCompany.hasFreeSlot()) {
                ret.insert.add(newDev);
                myCompany.usedSlots++;
            } else {
                ret.delete.add(worstDev);
                ret.insert.add(newDev);
            }
            ret.UpdateCompanies.add(myCompany);
            return ret;
        }else if(flag_newSlot){
            Device newD = new Device(myDevices.get(max_Overall(myDevices)));
            myCompany.logs = myCompany.logs +"A new slot is available!\n Our most profitable device("+newD.name+") is cloned and realesed with higher profit.";
            newD.name= nameBuilder.buildName(newD.name,0);
            newD.profit*=1.1;
            myCompany.usedSlots++;
            ret.insert.add(newD);
            ret.UpdateCompanies.add(myCompany);
            return ret;
        }

        //works???
        myDevices.sort(Comparator.comparingInt((Device d)->d.profit*d.getSoldPieces() ));
        int[] sumOfLevels=new int[myDevices.size()];
        int maxSum=-1;
        for (int i = 0; i < myDevices.size(); i++) {
            sumOfLevels[i]=getSumOfLevels(myDevices.get(i));
            if(sumOfLevels[i]>maxSum){maxSum=sumOfLevels[i];}
        }

        //things for the release
        int companyContext=getCompanyContext(companyList,myCompany);
        //attrId,value

        int[] values=new int[Device.NUMBER_OF_ATTRIBUTES];

        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            boolean best=true;
            for (int j = 0; j < companyList.size(); j++) {
                int diff=myCompany.getLevels_USE_THIS()[i]-companyList.get(j).getLevels_USE_THIS()[i];
                if(diff>0){
                    values[i]+=diff;
                }else if(myCompany.getLevels_USE_THIS()[i]<companyList.get(j).getLevels_USE_THIS()[i]){
                    values[i]+=2*diff;
                    best=false;
                }
            }
            if(best){values[i]+=100;}
        }
        LinkedList<Pair<Integer,Integer>> bestAttrIds=new LinkedList<>();
        for (int i = 0; i < values.length; i++) {bestAttrIds.add(new Pair<>(i,values[i]));}
        bestAttrIds.sort((o1, o2) -> o2.second-o1.second );

        //device releaser
        if(myDevices.size() !=1) {
            switch (getRegion(Arrays.stream(sumOfLevels).boxed().collect(Collectors.toList()), maxSum)) {
                case 1:
                case 2:
                    //place a popular device promoting our unique feature
                    //find and copy a popular dev and add our feature,
                    // cut the prices if the companyContext is 1
                    int i=0;
                    //sort by popularity
                    myDevices.sort(Comparator.comparingInt((Device d)->d.getSoldPieces() ));

                    while(i<deviceList.size() && !producibleByTheCompany(myCompany,deviceList.get(i)) ){i++;}
                    if(i<deviceList.size()){
                        Device newD=new Device(deviceList.get(i));
                        myCompany.logs = myCompany.logs+"A popular device named"+deviceList.get(i).name+" is copied\n";
                        //cut price if company context is 1
                        if(companyContext==1){newD.profit*=0.8;
                            myCompany.logs = myCompany.logs+"The company's position is bad so copied device's profit is cut\n";}
                        //set our big feature(s)
                        //whats that?
                        newD.setFieldByNum(bestAttrIds.get(0).first,myCompany.getLevels_USE_THIS()[bestAttrIds.get(0).first]);
                        myCompany.logs = myCompany.logs+bestAttrIds.get(0)+". attribute is set to the new device\n";
                        for (int j = 0; bestAttrIds.get(j).second > 100; j++) {
                            newD.setFieldByNum(bestAttrIds.get(j).first,myCompany.getLevels_USE_THIS()[bestAttrIds.get(j).first]);
                            myCompany.logs = myCompany.logs+bestAttrIds.get(0)+". attribute is set to the new device\n";
                        }
                    }
                    break;
                case 3:
                case 4:
                    //our highend device is really good! Do nothing with it!
                    myCompany.logs = myCompany.logs+"Our high-end device goes well!\n";
                    //search for underperformer
                    for (int j = 0; j<myDevices.size(); j++) {
                        if(getRegion(deviceList.stream().map( (Device::getSoldPieces)).collect(Collectors.toList()), myDevices.get(j).getSoldPieces())<=2 &&
                                getRegion(deviceList.stream().map( (d -> d.getSoldPieces() * d.profit)).collect(Collectors.toList()),
                                        myDevices.get(j).getSoldPieces()*myDevices.get(j).profit) <=2 ){
                            myDevices.get(j).profit*=0.9;
                            ret.update.add(myDevices.get(j));
                            myCompany.logs = myCompany.logs+myDevices.get(j).name+" is underperforming. Little discount is made. \n";
                        }
                    }
                    break;
                case 5:
                    //our highend device is the best! Increase the prices!
                    myCompany.logs = myCompany.logs+"Our highend device is the best! Increase the profits by 5%!\n";
                    for (Device d :myDevices) {
                        d.profit *= 1.05;
                        ret.update.add(d);
                    }
                    //search for really underperformer and change it to a premium dev
                    for (int j = 0; j<myDevices.size(); j++) {
                        if(getRegion(deviceList.stream().map( (Device::getSoldPieces)).collect(Collectors.toList()), myDevices.get(j).getSoldPieces())<=1 &&
                                getRegion(deviceList.stream().map( (d -> d.getSoldPieces() * d.profit)).collect(Collectors.toList()),
                                        myDevices.get(j).getSoldPieces()*myDevices.get(j).profit) <=1 ){
                            myCompany.logs = myCompany.logs+myDevices.get(j).name+" is terribly underperforming. Replace it with a premium device \n";
                            //change it to premium
                            int maxInd=0;
                            for (int k = 0; k < myDevices.size(); k++) {
                                if(getSumOfLevels(myDevices.get(k))>getSumOfLevels(myDevices.get(maxInd))){maxInd=k;}
                            }
                            Device newD=new Device(myDevices.get(maxInd));
                            newD.profit*=1.5;
                            newD.name= nameBuilder.buildName(myDevices.get(maxInd).name,1);
                            ret.delete.add(myDevices.get(j));
                            ret.insert.add(myDevices.get(maxInd));
                        }
                    }
                    break;
            }
        }else{
            if(companyContext<=2){
                Device d=myDevices.get(0);
                d.profit*=0.9;
                ret.update.add(d);
            }
        }

        ret.UpdateCompanies.add(myCompany);
        return ret;
    }


    private int findLongTermGoal(List<Company> companyList,Company myCompany){
        //longtermGoals to choose from:
        /* goal:
        -2: make more devices = buy new slot
        -1: become marketing giant
        0-NumOfAttr: develop a unique, strong attribute (attrId is stored)
         */
        /*
          make a monopolous company
          3 key development points: slot, marketing, research
         release points: overpriced, unique(best tech); lower priced high-middle;
         goal: prices should increase in general in the market, higher attributes should be used by more people
         */
        int goal=-100;
        int importance=0;
        int costOfTheGoal=0;
        //are we the best in something?
        int[] myLevels=myCompany.getLevels_USE_THIS();
        int[] better=new int[myLevels.length];//then me
        int[] same=new int[myLevels.length];
        int[] worse=new int[myLevels.length];//then me
        for (int i=0;i<companyList.size();i++) {
            int[] others=companyList.get(i).getLevels_USE_THIS();
            for (int j=0;j<others.length;j++) {
                int diff=myLevels[j]-others[j];
                if(diff>0){
                    worse[j]+=diff;
                }else if(diff==0){same[j]++;
                }else{better[j]-=diff;}
            }
        }
        int uniqueStrengths=0;
        int goodPoints=0;
        int badThings=0;
        ArrayList<Integer> interestingAttrs=new ArrayList<>();
        for(int i =0;i<myLevels.length;i++){
            if(better[i]==0 && same[i]==0){uniqueStrengths++;interestingAttrs.add(i);
            }else if(better[i] ==0){
                goodPoints++;interestingAttrs.add(i);
            }else if(better[i]>worse[i]+same[i]){badThings++;interestingAttrs.add(i);}
        }

        //most goodpoint attribute
        if(goodPoints>0){
            //choose the cheapest one
            int minAttrId=-1;
            int i=uniqueStrengths;
            while(DevelopmentValidator.getOneDevelopmentCost(interestingAttrs.get(i),myCompany.getLevels_USE_THIS()[interestingAttrs.get(i)]) ==-1 &&
                    i<goodPoints){i++;}
            if(i!=goodPoints){minAttrId=interestingAttrs.get(i);}
            for(i++;i<goodPoints;i++){
                if(DevelopmentValidator.getOneDevelopmentCost(minAttrId,myCompany.getLevels_USE_THIS()[minAttrId])>
                        DevelopmentValidator.getOneDevelopmentCost(interestingAttrs.get(i),myCompany.getLevels_USE_THIS()[interestingAttrs.get(i)]) &&
                        DevelopmentValidator.getOneDevelopmentCost(interestingAttrs.get(i),myCompany.getLevels_USE_THIS()[interestingAttrs.get(i)]) !=-1){
                    minAttrId=interestingAttrs.get(i);
                }
            }
            if(minAttrId!=-1){
                goal=minAttrId;
                costOfTheGoal=DevelopmentValidator.getOneDevelopmentCost(goal,myCompany.getLevels_USE_THIS()[minAttrId]);
                importance=5;
            }
        }

        //if there s a bad
        if(badThings>0){
            //choose the cheapest one
            int i=uniqueStrengths+goodPoints;
            int minAttrId=interestingAttrs.get(i);
            for(i++;i<badThings;i++){
                if(DevelopmentValidator.getOneDevelopmentCost(minAttrId,myCompany.getLevels_USE_THIS()[minAttrId])>
                        DevelopmentValidator.getOneDevelopmentCost(interestingAttrs.get(i),myCompany.getLevels_USE_THIS()[interestingAttrs.get(i)]) ){
                    minAttrId=interestingAttrs.get(i);
                }
            }
            goal=minAttrId;
            costOfTheGoal=DevelopmentValidator.getOneDevelopmentCost(goal,myCompany.getLevels_USE_THIS()[minAttrId]);
            importance=6;
        }

        //marketing
        LinkedList<Integer> marketings=new LinkedList<>();
        for (Company c :companyList) {
            marketings.add(c.marketing);
        }
        if(badThings==0 && getRegion(marketings,myCompany.marketing)<4){
            if(importance<2*(5-getRegion(marketings,myCompany.marketing)) ) {
                goal = -1;
                costOfTheGoal=0;
                int wouldBeMarketing=myCompany.marketing;
                while (getRegion(marketings,wouldBeMarketing)<4){
                    costOfTheGoal+=DevelopmentValidator.calculateMarketingCost(wouldBeMarketing);
                    wouldBeMarketing+=10;
                }
                importance=2*(5-getRegion(marketings,myCompany.marketing));
            }
        }


        ///TODO Functional way: List<Integer> maxSlotFieldOfCompanies = companyList.stream().map( company -> company.maxSlots).collect(Collectors.toList());
        //newSlot
        if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots) !=-1){
            final int newSlotImportance=5;
            double timeToBuyANewSlot=DevelopmentValidator.nextSlotCost(myCompany.maxSlots)/(double)myCompany.lastProfit;
            double timeToReachTheGoal=costOfTheGoal/(double) myCompany.lastProfit;
            if((importance/timeToReachTheGoal)<(2*newSlotImportance/timeToBuyANewSlot)){
                goal=-2;
                costOfTheGoal=DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                importance=newSlotImportance;
            }
        }


        return goal;
    }

    private int getSumOfLevels(Device d){
        int sumOfLevels=0;
        for (int j = 0; j < Device.NUMBER_OF_ATTRIBUTES; j++) {
            sumOfLevels+=d.getFieldByNum(j);
        }
        return sumOfLevels;
    }

    private boolean producibleByTheCompany(Company c, Device d){
        boolean isProduceable=true;
        int[] cLevels=c.getLevels_USE_THIS();
        for (int i=0;i<cLevels.length && isProduceable;i++){
            isProduceable=cLevels[i]>=d.getFieldByNum(i);
        }
        return isProduceable;
    }

}
