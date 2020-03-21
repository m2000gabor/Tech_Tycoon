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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import androidx.room.ColumnInfo;

import static com.example.techtycoon.TabbedActivity.NAME_newestPartOfTheSeries;

public class Assistant {
    @ColumnInfo
    public int assistantType;

    @ColumnInfo
    public int bossCompanyID;

    @ColumnInfo
    public int assistantGoal;

    @ColumnInfo
    public String status;

    public Assistant(int bossCompanyID,int assistantGoal,int assistantType){
        this.bossCompanyID =bossCompanyID;
        this.assistantGoal =assistantGoal;
        this.assistantType =assistantType;
        this.status ="undefined";
    }

    Wrapped_DeviceAndCompanyList timeToDo(List<Company> companyList, List<Device> deviceList,List<Device> myDevices,Company myCompany){
        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        Wrapped_DeviceAndCompanyList ret=new Wrapped_DeviceAndCompanyList(myDevices,companyList);
        myCompany.logs="";
        //Basic assistant moves (all assistant start with these)
        if(myDevices.size()==0){return ret;}
        if(myCompany.hasFreeSlot()){
            Device newDev=new Device(myDevices.get(max_Overall(myDevices)));
            newDev.profit*=1.2;
            newDev.name=nameBuilder.buildName(newDev.name,1);
            ret.insert.add(newDev);
            myCompany.usedSlots+=ret.insert.size();
        }
        //specific assistant commands
        //if empty?
        switch (assistantType) {
            /*
             * marketing goal
             * marketing holder
             * position holder
             * EMPTY random
             * EMPTY trend follower
             * EMPTY inventor
             * EMPTY marketing god
             * EMPTY price cut
             *
             * */
            case 1:
                coAssistant_marketingGoal(myCompany, ret);
                break;
            case 2:
                coAssistant_avgMarketing(companyList,myCompany,ret);
                break;
            case 3:
                /*
                Name: avg, just self, no invention, position holder
                Activated if company's profit lower than the average
                Action tree:
                    -if marketing is lower than avg, than buy one
                    -Otherwise: publish their last model with lower costs
                 */
                int allLastProfitAvg = avg_lastProfit(companyList);
                if (allLastProfitAvg > myCompany.lastProfit) {
                    if (myCompany.money >= marketingCost && myCompany.marketing < avg_marketing(companyList)) {
                        myCompany.money -= marketingCost;
                        myCompany.marketing += 10;
                    } else {//if not marketing than the devices are bad
                        int maxOverallIncome = myDevices.get(0).getOverallIncome();
                        int minOverallIncome = maxOverallIncome;
                        int maxIndex = 0;
                        int minIndex = 0;
                        for (int i = 1; i < myDevices.size(); i++) {
                            if (myDevices.get(i).getOverallIncome() > maxOverallIncome) {
                                maxOverallIncome = myDevices.get(i).getOverallIncome();
                                maxIndex = i;
                            } else if (myDevices.get(i).getOverallIncome() < minOverallIncome) {
                                minOverallIncome = myDevices.get(i).getOverallIncome();
                                minIndex = i;
                            }
                        }
                        Device newDev = new Device(myDevices.get(maxIndex));
                        newDev.profit *= 0.9;
                        newDev.name = nameBuilder.buildName(newDev.name,1);
                        if (myCompany.hasFreeSlot()) {
                            ret.insert.add(newDev);
                            myCompany.usedSlots+=ret.insert.size();
                        } else {
                            ret.delete.add(myDevices.get(minIndex));
                            ret.insert.add(newDev);
                        }
                    }
                }
                break;
            case 4:
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
                    Device newDev = createValidDevice(name, 0, myCompany, memory, body);
                    newDev.profit = (int) Math.round(newDev.cost * (0.2 * randomGenerator.nextInt(10)));
                    ret.delete.add(myDevices.get(minIndex));
                    ret.insert.add(newDev);
                }
                break;
            case 5:
                /*
                Name: developer, just self, inventor, no new slot
                Activated always
                Action tree:
                    if a development is available than purchase it
                 */
                for (int i=0;i<Device.NUMBER_OF_ATTRIBUTES;i++) {
                    if(DevelopmentValidator.getOneDevelopmentCost(i,myCompany.getLevels_USE_THIS()[i])<=myCompany.money &&
                            DevelopmentValidator.getOneDevelopmentCost(i,myCompany.getLevels_USE_THIS()[i])!=-1
                    ){
                        myCompany.money-=DevelopmentValidator.getOneDevelopmentCost(i,myCompany.getLevels_USE_THIS()[i]);
                        int[] levels=myCompany.getLevels_USE_THIS();
                        levels[i]++;
                        myCompany.setLevels_USE_THIS(levels);
                        this.status="newTechnologyAvailable";
                    }
                }
                if(status.equals("newTechnologyAvailable")){
                    int minIndex=min_Overall(myDevices);
                    int maxIndex=max_Overall(myDevices);
                    Device newDev=new Device(myDevices.get(maxIndex));
                    newDev.profit*=1.2;
                    newDev.name=nameBuilder.buildName(newDev.name,1);
                    newDev.ram=myCompany.getLevels_USE_THIS()[0];
                    newDev.memory=myCompany.getLevels_USE_THIS()[1];
                    newDev.setBodyParams(myCompany.getLevels_USE_THIS()[2],
                            myCompany.getLevels_USE_THIS()[3],
                            myCompany.getLevels_USE_THIS()[4],
                            myCompany.getLevels_USE_THIS()[5],
                            myCompany.getLevels_USE_THIS()[6]
                    );

                    //release device
                    if (myCompany.hasFreeSlot()) {
                        ret.insert.add(newDev);
                        myCompany.usedSlots+=ret.insert.size();
                    } else {
                        ret.delete.add(myDevices.get(minIndex));
                        ret.insert.add(newDev);
                    }
                    status="nothing";
                }
                break;
            case 6:
                ret=assistant6(companyList, deviceList,myDevices,myCompany,ret);
                break;
            case 7:
                ret=assistant7(companyList, deviceList,myDevices,myCompany,ret);
                break;
        }

        //for every assistant
        for (Device d :ret.insert) {
            DeviceValidator deviceValidator=new DeviceValidator(d);
            d.cost = deviceValidator.getOverallCost();
        }
        for (Device d :ret.update) {
            DeviceValidator deviceValidator=new DeviceValidator(d);
            d.cost = deviceValidator.getOverallCost();
        }

        //ret.UpdateCompanies.add(myCompany);
        return ret;
    }

    private Device createValidDevice(String name,int profit,Company myCompany,int[] memory,int[] body){
        Device newDev=new Device(name,profit,0,myCompany.companyId,memory[0],memory[1]);
        //insert attributes
        newDev.setBodyParams(body[0],body[1],body[2],body[3],body[4]);

        //final calculation
        DeviceValidator deviceValidator=new DeviceValidator(newDev);
        newDev.cost=deviceValidator.getOverallCost();

        //validate
        if (!DeviceValidator.validate(myCompany, newDev)) throw new AssertionError();
        return newDev;
    }

    private Device createValidDevice(String name,int profit,Company myCompany,int[] params){
        Device newDev=new Device(name,profit,0,myCompany.companyId,params[0],params[1]);

        //insert attributes
        for(int i=2;i<Device.NUMBER_OF_ATTRIBUTES;i++){
            newDev.setFieldByNum(i,params[i]);
        }

        //final calculation
        DeviceValidator deviceValidator=new DeviceValidator(newDev);
        newDev.cost=deviceValidator.getOverallCost();

        //validate
        if (!DeviceValidator.validate(myCompany, newDev)) throw new AssertionError();
        return newDev;
    }

    private int avg_lastProfit(List<Company> companyList){
        int sum=0;
        for (Company c:companyList) {
            sum+=c.lastProfit;
        }
        return sum/companyList.size();
    }
    private double avg_marketing(List<Company> companyList){
        double sum=0;
        for (Company c:companyList) {
            sum+=c.marketing;
        }
        return sum/companyList.size();
    }
    private int min_Overall(List<Device> deviceList){
        int minOverallIncome = deviceList.get(0).getOverallIncome();
        int minIndex = 0;
        for (int i = 1; i < deviceList.size(); i++) {
            if (deviceList.get(i).getOverallIncome() < minOverallIncome) {
                minOverallIncome = deviceList.get(i).getOverallIncome();
                minIndex = i;
            }
        }
        return minIndex;
    }
    private int max_Overall(List<Device> deviceList){
        int maxOverallIncome = deviceList.get(0).getOverallIncome();
        int maxIndex = 0;
        for (int i = 1; i < deviceList.size(); i++) {
            if (deviceList.get(i).getOverallIncome() > maxOverallIncome) {
                maxOverallIncome = deviceList.get(i).getOverallIncome();
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    private static double avg(Device[] list,int type){
        double s=0;
        int soldP=0;
        for (Device d :list) {
            s+=d.getFieldByNum(type)*d.soldPieces;
            soldP+=d.soldPieces;
        }
        return s/soldP;
    }
    //returns the min value
    private static int min(Device[] list,int type){
        int minInd=0;
        for (int i=0;i<list.length;i++) {
            if(list[i].getFieldByNum(type)<list[minInd].getFieldByNum(type)){minInd=i;}
        }
        return list[minInd].getFieldByNum(type);
    }
    //returns the max value
    private static int max(Device[] list,int type){
        int maxInd=0;
        for (int i=0;i<list.length;i++) {
            if(list[i].getFieldByNum(type)>list[maxInd].getFieldByNum(type)){maxInd=i;}
        }
        return list[maxInd].getFieldByNum(type);
    }

    //tools
    private static class nameBuilder{
        static String buildName(String prevName,int nameConvention){
            String r=prevName.concat("+");
            switch (nameConvention) {
                case 1:
                    String series=prevName.split("[0-9]",2)[0];
                    if (NAME_newestPartOfTheSeries.containsKey(series)){
                        Integer num=NAME_newestPartOfTheSeries.get(series);
                        num++;
                        NAME_newestPartOfTheSeries.replace(series,num);
                        r=series+num;
                    }else{
                        r=series.concat(" 2");
                        NAME_newestPartOfTheSeries.put(series+" ",2);
                    }
            }
            return r;
        }
    }

    /*return the $param myCompany 's position
    *  1 - lowest income
    *  2 - lower then the avg
    *  3 - avg (nearly never)
    *  4 - above the avg
    *  5 - best
    * */
    private int getCompanyContext(List<Company> companies,Company myCompany){
        int avg=0;
        for (Company c : companies) {
            if(c.equals(myCompany)){continue;}
            avg += c.lastProfit;
        }
        avg/=companies.size()-1;
        int r=1;
        if(myCompany.lastProfit>avg*2){r=5;
        }else if(myCompany.lastProfit>avg*1.2){r=4;
        }else if(myCompany.lastProfit>avg*0.8){r=3;
        }else if(myCompany.lastProfit>avg*0.4){r=2;}

        return r;
    }

    private int getRegion(List<Integer> list,int value){
        int avg=0;
        for (Integer n : list) {avg += n;}
        avg/=list.size();
        int r=1;
        if(value>avg*3){r=5;
        }else if(value>avg*1.2){r=4;
        }else if(value>avg*0.8){r=3;
        }else if(value>avg*0.4){r=2;}

        return r;
    }

    private int priceMaker(Device newDevice, List<Device> allDevices, List<Device> myDevices, int myCompanyContext){
        int profit=0;

        //try to find similar devices
        LinkedList<LinkedList<Device>> listOfLists=new LinkedList<LinkedList<Device>>();
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES+1; i++) {listOfLists.add(new LinkedList<Device>());}
        for (Device d:allDevices) {
            int diff=newDevice.equalAttributes(d);
            listOfLists.get(diff).add(d);
        }

        if (listOfLists.get(0).size()>1) {
            switch (myCompanyContext) {
                case 1:
                    profit=(int) Math.floor(min(listOfLists.get(0).toArray(new Device[0]),-1)*0.8);
                    break;
                case 2:
                    profit=(int) Math.floor(min(listOfLists.get(0).toArray(new Device[0]),-1)*0.9);
                    break;
                case 3:
                    profit=(int) Math.floor(avg(listOfLists.get(0).toArray(new Device[0]),-1));
                    break;
                case 4:
                    profit=(int) Math.floor(
                            (avg(listOfLists.get(0).toArray(new Device[0]),-1)+
                                    max(listOfLists.get(0).toArray(new Device[0]),-1))
                                    /2);
                    break;
                case 5:
                    profit=(int) (Math.floor(max(listOfLists.get(0).toArray(new Device[0]),-1))*1.1);
                    break;
            }
        }else if(listOfLists.get(0).size()==1){
            switch (myCompanyContext) {
                case 1:
                    profit=(int) Math.round(listOfLists.get(0).get(0).profit*0.7);
                    break;
                case 2:
                    profit=(int) Math.round(listOfLists.get(0).get(0).profit*0.9);
                    break;
                case 3:
                    profit=listOfLists.get(0).get(0).profit;
                    break;
                case 4:
                    profit=(int) Math.round(listOfLists.get(0).get(0).profit*1.2);
                    break;
                case 5:
                    profit=(int) Math.round(listOfLists.get(0).get(0).profit*1.4);
                    break;
            }
        }else{
            int div=0;
            for (int i=0;i<listOfLists.size();i++) {
                if(listOfLists.get(i).size()==0){continue;}
                int tempProfit=0;
                switch (myCompanyContext) {
                    case 1:
                        tempProfit=(int) Math.floor(min(listOfLists.get(i).toArray(new Device[0]),-1)*0.8);
                        break;
                    case 2:
                        tempProfit=(int) Math.floor(min(listOfLists.get(i).toArray(new Device[0]),-1)*0.9);
                        break;
                    case 3:
                        tempProfit=(int) Math.floor(avg(listOfLists.get(i).toArray(new Device[0]),-1));
                        break;
                    case 4:
                        tempProfit=(int) Math.floor(
                                (avg(listOfLists.get(i).toArray(new Device[0]),-1)+
                                        max(listOfLists.get(i).toArray(new Device[0]),-1))
                                        /2);
                        break;
                    case 5:
                        tempProfit=(int) Math.floor(1.1*max(listOfLists.get(i).toArray(new Device[0]),-1));
                        break;
                }
                profit+=(1+(i*5))*tempProfit;
                div+=1+(i*5);
            }
            profit/=div;
        }

        return profit;
    }

    private void coAssistant_marketingGoal(Company myCompany,Wrapped_DeviceAndCompanyList ret){
        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        if (myCompany.money >= marketingCost && myCompany.marketing < assistantGoal) {
            myCompany.money -= marketingCost;
            myCompany.marketing += 10;
            myCompany.logs=myCompany.logs+"Assistant bought 10 marketing!\n";
        }
        ret.UpdateCompanies.add(myCompany);
    }

    private void coAssistant_avgMarketing(List<Company> companyList,Company myCompany,Wrapped_DeviceAndCompanyList ret){
        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        if (myCompany.money >= marketingCost && myCompany.marketing < avg_marketing(companyList)) {
            myCompany.money -= marketingCost;
            myCompany.marketing += 10;
            myCompany.logs=myCompany.logs+"Assistant bought 10 marketing!\n";
        }
        ret.UpdateCompanies.add(myCompany);
    }

    private Wrapped_DeviceAndCompanyList assistant6(List<Company> companyList, List<Device> deviceList,
            List<Device> myDevices,Company myCompany,Wrapped_DeviceAndCompanyList ret){

        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        //profile of the assistant
        final int[] devImportance={5,5,3,3,3,3,3};
        final double marketingImp=0.2;
        final int newSlotImp=10;

        //status:"companyContext,madeMoveId1,madeMoveId2..."
        String oldStatus=this.status;

        //context of my company
        //should the assist move, how important is it?
        int companyPosition=getCompanyContext(companyList,myCompany);
        this.status=String.valueOf(companyPosition);
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
        double marketingScore=marketingImp * marketingContext / marketingCost ;
        pairs.add(new Pair<>(-1,marketingScore));

        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
                    /*double context = Math.min(Math.abs(
                            avg(deviceList.toArray(new Device[0]), i) - avg(myDevices.toArray(new Device[0]), i)) /
                            (avg(deviceList.toArray(new Device[0]), i) * 10), 10);*/
            LinkedList<Integer> list=new LinkedList<Integer>();
            for(Device d:deviceList){list.add(d.getFieldByNum(i));}
            double context=2*getRegion(list,myCompany.getLevels_USE_THIS()[i]);
            double score = devImportance[i] * context / DevelopmentValidator.getOneDevelopmentCost(i,
                    myCompany.getLevels_USE_THIS()[i]);
            pairs.add(new Pair<>(i,score));
        }
        //most important is at the 0 index
        //Arrays.sort(importanceScores,Collections.reverseOrder());
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
                if (myCompany.money >= marketingCost) {
                    myCompany.money -= marketingCost;
                    myCompany.marketing += 10;
                    myCompany.logs=myCompany.logs+"Assistant bought 10 marketing!\n";
                    this.status=status+";"+key;
                }
            } else if (key == -2) {
                myCompany.logs=myCompany.logs+"Trying to buy a new slot...\n";
                //new slot is the highest priority
                if (myCompany.money >= DevelopmentValidator.nextSlotCost(myCompany.maxSlots)) {
                    myCompany.money -= DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                    myCompany.maxSlots += 1;
                    myCompany.logs=myCompany.logs+"Assistant bought a new slot!\n";
                    this.status=status+";"+key;
                }
            } else {
                myCompany.logs=myCompany.logs+"Trying to improve the "+key+". attribute...\n";
                //an attribute dev is the highest priority
                if (myCompany.money >= DevelopmentValidator.getOneDevelopmentCost(key,
                        myCompany.getLevels_USE_THIS()[key])) {
                    int[] lvls = myCompany.getLevels_USE_THIS();
                    myCompany.money -= DevelopmentValidator.getOneDevelopmentCost(key,
                            myCompany.getLevels_USE_THIS()[key]);
                    lvls[key]++;
                    myCompany.setLevels_USE_THIS(lvls);
                    myCompany.logs=myCompany.logs+"\n"+key +".attribute is updated by the assistant!\n";
                    this.status=status+";"+key;
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
                    newDev.setFieldByNum(attrId, myCompany.getLevels_USE_THIS()[attrId]);}
            }

            newDev.profit=priceMaker(newDev,deviceList,myDevices,newStatusInts[0]);

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

    private Wrapped_DeviceAndCompanyList assistant7(List<Company> companyList, List<Device> deviceList,
                                                    List<Device> myDevices,Company myCompany,Wrapped_DeviceAndCompanyList ret){
        /*
        monopolous company
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
        myCompany.logs="";


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
                            DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing)) {
                        myCompany.money -= DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
                        myCompany.marketing += 10;
                        i++;
                    }
                    myCompany.logs = myCompany.logs + "The assistant bougth " + i * 10 + " marketing!\n";
                    accomplished=true;
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
        Device worstDev=myDevices.get(max_Overall(myDevices));
        if(flag_newAttribute){
            myCompany.logs = myCompany.logs + "An attribute is freshly developed.\n A new device with that attribute is made!\n";
            Device newDev= createValidDevice(nameBuilder.buildName(worstDev.name,1)
                    ,(int) Math.round(max(myDevices.toArray(new Device[0]),-1)*1.2)
                    ,myCompany,myCompany.getLevels_USE_THIS());

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
            newD.name=nameBuilder.buildName(newD.name,0);
            newD.profit*=1.1;
            myCompany.usedSlots++;
            ret.insert.add(newD);
            ret.UpdateCompanies.add(myCompany);
            return ret;
        }

        //works???
        myDevices.sort(Comparator.comparingInt((Device d)->d.profit*d.soldPieces ));
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
                    myDevices.sort(Comparator.comparingInt((Device d)->d.soldPieces ));

                    while(i<deviceList.size() && !produceableByTheCompany(myCompany,deviceList.get(i)) ){i++;}
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
                        if(getRegion(deviceList.stream().map( (device -> device.soldPieces)).collect(Collectors.toList()), myDevices.get(j).soldPieces)<=2 &&
                                getRegion(deviceList.stream().map( (d -> d.soldPieces * d.profit)).collect(Collectors.toList()),
                                        myDevices.get(j).soldPieces*myDevices.get(j).profit) <=2 ){
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
                        if(getRegion(deviceList.stream().map( (device -> device.soldPieces)).collect(Collectors.toList()), myDevices.get(j).soldPieces)<=1 &&
                                getRegion(deviceList.stream().map( (d -> d.soldPieces * d.profit)).collect(Collectors.toList()),
                                        myDevices.get(j).soldPieces*myDevices.get(j).profit) <=1 ){
                            myCompany.logs = myCompany.logs+myDevices.get(j).name+" is terribly underperforming. Replace it with a premium device \n";
                            //change it to premium
                            int maxInd=0;
                            for (int k = 0; k < myDevices.size(); k++) {
                                if(getSumOfLevels(myDevices.get(k))>getSumOfLevels(myDevices.get(maxInd))){maxInd=k;}
                            }
                            Device newD=new Device(myDevices.get(maxInd));
                            newD.profit*=1.5;
                            newD.name=nameBuilder.buildName(myDevices.get(maxInd).name,1);
                            ret.delete.add(myDevices.get(j));
                            ret.insert.add(myDevices.get(maxInd));
                        }
                    }
                    break;
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
            if(!c.equals(myCompany)){marketings.add(c.marketing);}
        }
        if(badThings==0 && getRegion(marketings,myCompany.marketing)<4){
            if(importance<2*(5-getRegion(marketings,myCompany.marketing)) ) {
                goal = -1;
                costOfTheGoal=0;
                int wouldBeMarketing=myCompany.marketing;
                while (getRegion(marketings,wouldBeMarketing)<4){
                    costOfTheGoal+=DetailsOfOneCompany.calculateMarketingCost(wouldBeMarketing);
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

    private boolean produceableByTheCompany(Company c,Device d){
        boolean isProduceable=true;
        int[] cLevels=c.getLevels_USE_THIS();
        for (int i=0;i<cLevels.length && isProduceable;i++){
            isProduceable=cLevels[i]>=d.getFieldByNum(i);
        }
        return isProduceable;
    }
}
