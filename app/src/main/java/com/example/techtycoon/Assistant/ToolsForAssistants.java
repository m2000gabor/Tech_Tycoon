package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;

import java.util.LinkedList;
import java.util.List;

import static com.example.techtycoon.TabbedActivity.NAME_newestPartOfTheSeries;

public class ToolsForAssistants {
    //tools
    static int avg_lastProfit(List<Company> companyList) {
        int sum = 0;
        for (Company c : companyList) {
            sum += c.lastProfit;
        }
        return sum / companyList.size();
    }

    static double avg_marketing(List<Company> companyList) {
        double sum = 0;
        for (Company c : companyList) {
            sum += c.marketing;
        }
        return sum / companyList.size();
    }

    static int min_Overall(List<Device> deviceList) {
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

    static int max_Overall(List<Device> deviceList) {
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

    static double avg(List<Device> list, int type) {
        double s = 0;
        int soldP = 0;
        for (Device d : list) {
            s += d.getFieldByNum(type) * d.getSoldPieces();
            soldP += d.getSoldPieces();
        }
        return s / soldP;
    }

    //returns the min value
    static int min(Device[] list, int type) {
        int minInd = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].getFieldByNum(type) < list[minInd].getFieldByNum(type)) {
                minInd = i;
            }
        }
        return list[minInd].getFieldByNum(type);
    }

    /**
     * @return the max value
     */
    static int max(List<Device> list, int type) {
        int maxInd = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldByNum(type) > list.get(maxInd).getFieldByNum(type)) {
                maxInd = i;
            }
        }
        return list.get(maxInd).getFieldByNum(type);
    }

    /**
     * @return the max index
     */
    static int maxIndex(List<Device> list, int type) {
        int maxInd = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldByNum(type) > list.get(maxInd).getFieldByNum(type)) {
                maxInd = i;
            }
        }
        return maxInd;
    }

    //tools
    static class nameBuilder {
        static String buildName(String prevName, int nameConvention) {
            String r;
            switch (nameConvention) {
                default:r = prevName.concat("+");break;
                case 1:
                    String series = prevName.split("[0-9]", 2)[0];
                    //remove + which is not part of the series name
                    boolean foundAPlusSign=false;
                    while(series.charAt(series.length()-1) == '+'){
                        foundAPlusSign=true;
                        series=series.substring(0,series.length()-1);
                    }
                    if(foundAPlusSign){series=series+" ";}
                    //find the series name in the table
                    if (NAME_newestPartOfTheSeries.containsKey(series)) {
                        Integer num = NAME_newestPartOfTheSeries.get(series);
                        num++;
                        NAME_newestPartOfTheSeries.replace(series, num);
                        r = series + num;
                    } else {
                        r = series.concat(" 2");
                        NAME_newestPartOfTheSeries.put(series + " ", 2);
                    }
                    break;
            }
            return r;
        }
    }

    public static boolean producibleByTheCompany(Company c, Device d){
        int[] cLevels=c.getLevels_USE_THIS();
        for (int i=0;i<cLevels.length;i++){
            if(cLevels[i]<d.getFieldByNum(i)){return false;}
        }
        return true;
    }

    /**
     * return the $param myCompany 's position
     *  1 - lowest income
     *  2 - lower then the avg
     *  3 - avg (nearly never)
     *  4 - above the avg
     *  5 - best
     * */
    static int getCompanyContext(List<Company> companies, Company myCompany) {
        int avg = 0;
        for (Company c : companies) {
            if (c.equals(myCompany)) {
                continue;
            }
            avg += c.lastProfit;
        }
        avg /= companies.size() - 1;
        int r = 1;
        if (myCompany.lastProfit > avg * 2) {
            r = 5;
        } else if (myCompany.lastProfit > avg * 1.2) {
            r = 4;
        } else if (myCompany.lastProfit > avg * 0.8) {
            r = 3;
        } else if (myCompany.lastProfit > avg * 0.4) {
            r = 2;
        }

        return r;
    }

    static int getRegion(List<Integer> list, int value) {
        int avg = 0;
        for (Integer n : list) {
            avg += n;
        }
        avg /= list.size();
        int r = 1;
        if (value > avg * 3) {
            r = 5;
        } else if (value > avg * 1.2) {
            r = 4;
        } else if (value > avg * 0.8) {
            r = 3;
        } else if (value > avg * 0.4) {
            r = 2;
        }

        return r;
    }

    static int getRegion100(List<Integer> list, int value) {
        int min = value;
        int max = value;
        for (Integer n : list) {
            if(n<min){min=n;
            }else if(n>max){max=n;}
        }
        return (int) Math.round(100*((double)(value-min))/(max-min));
    }

    static int priceMaker(Device newDevice, List<Device> allDevices, List<Device> myDevices, int myCompanyContext) {
        int profit = 0;

        //try to find similar devices
        LinkedList<LinkedList<Device>> listOfLists = new LinkedList<LinkedList<Device>>();
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES + 1; i++) {
            listOfLists.add(new LinkedList<Device>());
        }
        for (Device d : allDevices) {
            int diff = newDevice.equalAttributes(d);
            listOfLists.get(diff).add(d);
        }

        if (listOfLists.get(0).size() > 1) {
            switch (myCompanyContext) {
                case 1:
                    profit = (int) Math.floor(min(listOfLists.get(0).toArray(new Device[0]), -1) * 0.8);
                    break;
                case 2:
                    profit = (int) Math.floor(min(listOfLists.get(0).toArray(new Device[0]), -1) * 0.9);
                    break;
                case 3:
                    profit = (int) Math.floor(avg(listOfLists.get(0), -1));
                    break;
                case 4:
                    profit = (int) Math.floor(
                            (avg(listOfLists.get(0), -1) +
                                    max(listOfLists.get(0), -1))
                                    / 2);
                    break;
                case 5:
                    profit = (int) (Math.floor(max(listOfLists.get(0), -1)) * 1.1);
                    break;
            }
        } else if (listOfLists.get(0).size() == 1) {
            switch (myCompanyContext) {
                case 1:
                    profit = (int) Math.round(listOfLists.get(0).get(0).profit * 0.7);
                    break;
                case 2:
                    profit = (int) Math.round(listOfLists.get(0).get(0).profit * 0.9);
                    break;
                case 3:
                    profit = listOfLists.get(0).get(0).profit;
                    break;
                case 4:
                    profit = (int) Math.round(listOfLists.get(0).get(0).profit * 1.2);
                    break;
                case 5:
                    profit = (int) Math.round(listOfLists.get(0).get(0).profit * 1.4);
                    break;
            }
        } else {
            int div = 0;
            for (int i = 0; i < listOfLists.size(); i++) {
                if (listOfLists.get(i).size() == 0) {
                    continue;
                }
                int tempProfit = 0;
                switch (myCompanyContext) {
                    case 1:
                        tempProfit = (int) Math.floor(min(listOfLists.get(i).toArray(new Device[0]), -1) * 0.8);
                        break;
                    case 2:
                        tempProfit = (int) Math.floor(min(listOfLists.get(i).toArray(new Device[0]), -1) * 0.9);
                        break;
                    case 3:
                        tempProfit = (int) Math.floor(avg(listOfLists.get(i), -1));
                        break;
                    case 4:
                        tempProfit = (int) Math.floor(
                                (avg(listOfLists.get(i), -1) +
                                        max(listOfLists.get(i), -1))
                                        / 2);
                        break;
                    case 5:
                        tempProfit = (int) Math.floor(1.1 * max(listOfLists.get(i), -1));
                        break;
                }
                profit += (1 + (i * 5)) * tempProfit;
                div += 1 + (i * 5);
            }
            profit /= div;
        }

        return profit;
    }

    //Unused actions, but good templates
    static boolean tryToBuy_NewSlot(Company myCompany) {
        if (DevelopmentValidator.nextSlotCost(myCompany.maxSlots) <= myCompany.money &&
                DevelopmentValidator.nextSlotCost(myCompany.maxSlots) != -1) {
            myCompany.money -= DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
            myCompany.maxSlots++;
            myCompany.logs = myCompany.logs + "The assistant bougth a new slot!\n";
            return true;
        } else {
            return false;
        }
    }
    static boolean tryToBuy_Marketing(Company myCompany) {
        int marketingCost = DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
        if (myCompany.money >= marketingCost) {
            myCompany.money -= marketingCost;
            myCompany.marketing += 10;
            myCompany.logs = myCompany.logs + "The assistant bougth " + 10 + " marketing!\n";
            return true;
        } else {
            return false;
        }
    }
    static boolean tryToBuy_DevAttribute(Company myCompany, int attrId) {
        if (myCompany.money >= DevelopmentValidator.getOneDevelopmentCost(attrId, myCompany.getLevels_USE_THIS()[attrId]) &&
                DevelopmentValidator.getOneDevelopmentCost(attrId, myCompany.getLevels_USE_THIS()[attrId]) != -1) {
            myCompany.money -= DevelopmentValidator.getOneDevelopmentCost(attrId, myCompany.getLevels_USE_THIS()[attrId]);
            int[] levels = myCompany.getLevels_USE_THIS();
            levels[attrId]++;
            myCompany.setLevels_USE_THIS(levels);
            myCompany.logs = myCompany.logs + attrId + ". attribute is upgraded!\n";
            return true;
        } else {
            return false;
        }
    }
}
