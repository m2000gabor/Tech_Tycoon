package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DetailsOfOneCompany;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;

import androidx.room.ColumnInfo;

import static com.example.techtycoon.TabbedActivity.NAME_newestPartOfTheSeries;

public class Assistant {
    //todo this class should be an abstract class
    //todo customizable bots
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
        Wrapped_DeviceAndCompanyList ret=new Wrapped_DeviceAndCompanyList(myDevices,companyList);
        myCompany.logs="";
        //Basic assistant moves (all assistant start with these)
        if(myDevices.size()==0){return ret;}
        if(myCompany.hasFreeSlot()){
            Device newDev=new Device(myDevices.get(max_Overall(myDevices)));
            newDev.profit*=1.2;
            newDev.name=nameBuilder.buildName(newDev.name,0);
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
               Name: Avg
                Description: if something is worse than the avg try to update it
                Action tree:
                    -if something is worse than the avg then add to the toDoList
                    -order the list by the importance (specified in the profile)
                    -try to the everything from the most important to the least one
                Details:
                    -if the profit is low than decrease the profits on the devices
                */
                AverageBot averageBot=new AverageBot();
                ret=averageBot.work(companyList, deviceList,myDevices,myCompany,ret);
                break;
            case 4:
                /*
                notWorking
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
                break;

                 */
            case 5:
                //Inventor
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
                Bot1 bot1=new Bot1();
                ret=bot1.work(companyList, deviceList,myDevices,myCompany,ret);
                break;
            case 7:
                AppleBot appleBot=new AppleBot();
                ret=appleBot.work(companyList, deviceList,myDevices,myCompany,ret);
                break;
        }

        //for every assistant
        return ret;
    }

    static int avg_lastProfit(List<Company> companyList){
        int sum=0;
        for (Company c:companyList) {
            sum+=c.lastProfit;
        }
        return sum/companyList.size();
    }
    static double avg_marketing(List<Company> companyList){
        double sum=0;
        for (Company c:companyList) {
            sum+=c.marketing;
        }
        return sum/companyList.size();
    }
    static int min_Overall(List<Device> deviceList){
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
    static int max_Overall(List<Device> deviceList){
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
    static double avg(List<Device> list,int type){
        double s=0;
        int soldP=0;
        for (Device d :list) {
            s+=d.getFieldByNum(type)*d.soldPieces;
            soldP+=d.soldPieces;
        }
        return s/soldP;
    }
    //returns the min value
    static int min(Device[] list,int type){
        int minInd=0;
        for (int i=0;i<list.length;i++) {
            if(list[i].getFieldByNum(type)<list[minInd].getFieldByNum(type)){minInd=i;}
        }
        return list[minInd].getFieldByNum(type);
    }
    //returns the max value
    static int max(Device[] list,int type){
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
    static int getCompanyContext(List<Company> companies,Company myCompany){
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
    static int getRegion(List<Integer> list,int value){
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
    static int priceMaker(Device newDevice, List<Device> allDevices, List<Device> myDevices, int myCompanyContext){
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
                    profit=(int) Math.floor(avg(listOfLists.get(0),-1));
                    break;
                case 4:
                    profit=(int) Math.floor(
                            (avg(listOfLists.get(0),-1)+
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
                        tempProfit=(int) Math.floor(avg(listOfLists.get(i),-1));
                        break;
                    case 4:
                        tempProfit=(int) Math.floor(
                                (avg(listOfLists.get(i),-1)+
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

    //Unused actions, but good templates
    private boolean tryToBuy_NewSlot(Company myCompany){
        if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots) <= myCompany.money &&
                DevelopmentValidator.nextSlotCost(myCompany.maxSlots) != -1){
            myCompany.money -= DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
            myCompany.maxSlots++;
            myCompany.logs = myCompany.logs + "The assistant bougth a new slot!\n";
            return true;
        }else {return false;}
    }
    private boolean tryToBuy_Marketing(Company myCompany){
        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        if(myCompany.money >= marketingCost) {
            myCompany.money -= marketingCost;
            myCompany.marketing += 10;
            myCompany.logs = myCompany.logs + "The assistant bougth " + 10 + " marketing!\n";
            return true;
        }else {return false;}
    }
    private boolean tryToBuy_DevAttribute(Company myCompany,int attrId){
        if (myCompany.money >= DevelopmentValidator.getOneDevelopmentCost(attrId, myCompany.getLevels_USE_THIS()[attrId]) &&
                DevelopmentValidator.getOneDevelopmentCost(attrId, myCompany.getLevels_USE_THIS()[attrId]) != -1) {
            myCompany.money -= DevelopmentValidator.getOneDevelopmentCost(attrId, myCompany.getLevels_USE_THIS()[attrId]);
            int[] levels = myCompany.getLevels_USE_THIS();
            levels[attrId]++;
            myCompany.setLevels_USE_THIS(levels);
            myCompany.logs = myCompany.logs + attrId + ". attribute is upgraded!\n";
            return true;
        }else {return false;}
    }

    //coAssistants
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

}
