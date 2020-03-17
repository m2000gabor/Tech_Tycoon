package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DetailsOfOneCompany;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.List;
import java.util.Random;

import androidx.room.ColumnInfo;

//todo developing throw an assistant
public class Assistant {
    @ColumnInfo
    public int assistantType;

    @ColumnInfo
    public int bossCompanyID;

    @ColumnInfo
    public int marketingGoal;

    @ColumnInfo
    public String status;

    public Assistant(int bossCompanyID,int marketingGoal,int assistantType){
        this.bossCompanyID =bossCompanyID;
        this.marketingGoal =marketingGoal;
        this.assistantType =assistantType;
        this.status ="undefined";
    }

    Wrapped_DeviceAndCompanyList timeToDo(List<Company> companyList, List<Device> deviceList,List<Device> myDevices,Company myCompany){
        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        Wrapped_DeviceAndCompanyList ret=new Wrapped_DeviceAndCompanyList(myDevices,companyList);
        //Basic assistant moves (all assistant start with these)
        if (0==marketingCost){
            do{
                myCompany.marketing+=10;
                marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
            }while(marketingCost==0);
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
                if (myCompany.money >= marketingCost && myCompany.marketing < marketingGoal) {
                    myCompany.money -= marketingCost;
                    myCompany.marketing += 10;
                }
                break;
            case 2:
                if (myCompany.money >= marketingCost && myCompany.marketing < avg_marketing(companyList)) {
                    myCompany.money -= marketingCost;
                    myCompany.marketing += 10;
                }
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
                Name: developer, just self, inventor
                Activated always
                Action tree:
                    if a development is available than purchase it
                    //todo if status is newTechnologyAvailable than publish a new device model
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
                    if (myCompany.hasFreeSlot()) {
                        ret.insert.add(newDev);
                    } else {
                        ret.delete.add(myDevices.get(minIndex));
                        ret.insert.add(newDev);
                    }
                    status="nothing";
                }
                break;
        }
        ret.UpdateCompanies.add(myCompany);
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

    private int avg_lastProfit(List<Company> companyList){
        int sum=0;
        for (Company c:companyList) {
            sum+=c.lastProfit;
        }
        return sum/companyList.size();
    }

    private int avg_marketing(List<Company> companyList){
        int sum=0;
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

    private static class nameBuilder{
        static String buildName(String prevName,int nameConvention){
            String r=prevName.concat("+");
            switch (nameConvention) {
                case 1:
                    String[] prv=prevName.split("[0-9]",2);
                    if (prv.length ==2){
                        prv[1]=prevName.substring(prv[0].length());
                        String name=prv[0];
                        byte num=Byte.parseByte(String.valueOf(prv[1]));
                        num++;
                        r=name+num;
                    }else{
                        r=prevName.concat(" 2");
                    }
            }
            return r;
        }
    }

}
