package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.techtycoon.TabbedActivity.NAME_newestPartOfTheSeries;

public class ToolsForAssistants {

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

    /**
     *
     * @param deviceList
     * @return the minIndex of the device with the lowest income
     */
    static int min_Overall(List<Device> deviceList) {
        int minOverallIncome = deviceList.get(0).getIncome();
        int minIndex = 0;
        for (int i = 1; i < deviceList.size(); i++) {
            if (deviceList.get(i).getIncome() < minOverallIncome) {
                minOverallIncome = deviceList.get(i).getIncome();
                minIndex = i;
            }
        }
        return minIndex;
    }

    static int max_Overall(List<Device> deviceList) {
        int maxOverallIncome = deviceList.get(0).getIncome();
        int maxIndex = 0;
        for (int i = 1; i < deviceList.size(); i++) {
            if (deviceList.get(i).getIncome() > maxOverallIncome) {
                maxOverallIncome = deviceList.get(i).getIncome();
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    static double avg(List<Device> list, Device.DeviceAttribute attribute) {
        double s = 0;
        int soldP = 0;
        for (Device d : list) {
            s += d.getFieldByAttribute(attribute) * d.getSoldPieces();
            soldP += d.getSoldPieces();
        }
        return s / soldP;
    }

    //returns the min value
    static int min(List<Device> list, Device.DeviceAttribute type) {
        int minInd = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldByAttribute(type) < list.get(minInd).getFieldByAttribute(type)) {
                minInd = i;
            }
        }
        return list.get(minInd).getFieldByAttribute(type);
    }

    //returns the min index
    static int minKer(List<Integer> list) {
        int minInd = 0;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(minInd) > list.get(i)) {
                minInd = i;
            }
        }
        return minInd;
    }

    /**
     * @return the max value
     */
    static int max(List<Device> list, Device.DeviceAttribute type) {
        int maxInd = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldByAttribute(type) > list.get(maxInd).getFieldByAttribute(type)) {
                maxInd = i;
            }
        }
        return list.get(maxInd).getFieldByAttribute(type);
    }

    /**
     * @return the max index
     */
    static int maxIndex(List<Device> list, Device.DeviceAttribute type) {
        int maxInd = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldByAttribute(type) > list.get(maxInd).getFieldByAttribute(type)) {
                maxInd = i;
            }
        }
        return maxInd;
    }

    /**
     * @return the min index
     */
    static int minInd(List<Device> list, Device.DeviceAttribute type) {
        int minInd = 0;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getFieldByAttribute(type) < list.get(minInd).getFieldByAttribute(type)) {
                minInd = i;
            }
        }
        return minInd;
    }

    //tools
    static class nameBuilder {
        static String buildName(String prevName, int nameConvention) {
            String r;
            switch (nameConvention) {
                case 1:
                    String series = prevName.split("[0-9]", 2)[0];
                    //remove + which is not part of the series name
                    boolean foundAPlusSign=false;
                    while(series.charAt(series.length()-1) == '+'){
                        foundAPlusSign=true;
                        series=series.substring(0,series.length()-1);
                    }
                    if(foundAPlusSign){series=series+" ";}
                    if(series.charAt(series.length()-1) != ' '){series=series+" ";}
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
                default:r = prevName.concat("+");break;
            }
            return r;
        }
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

    /**
     *
     * @param list
     * @param value
     * @return num from 1 to 5
     */
    static int getRegion(List<Integer> list, int value) {
        if(list.size()==0){return 5;}
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
    static int getRegionWithout(List<Integer> list, int value,int without) {
        List<Integer> l = new LinkedList<>();
        boolean removeHappenned=false;
        for(Integer i : list){
            if(!removeHappenned||i ==without){removeHappenned=true;continue;
            }else{l.add(i);}
        }
        return getRegion(l,value);
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
    static int getRegion100(List<Device> devs, Device.DeviceAttribute attribute, int value) {
        List<Integer> list=devs.stream().map(d->d.getFieldByAttribute(attribute)).collect(Collectors.toList());
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
        LinkedList<LinkedList<Device>> listOfLists = new LinkedList<>();
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES + 1; i++) {
            listOfLists.add(new LinkedList<>());
        }
        for (Device d : allDevices) {
            int diff = newDevice.compareAttributes(d);
            listOfLists.get(diff).add(d);
        }

        if (listOfLists.get(0).size() > 1) {
            switch (myCompanyContext) {
                case 1:
                    profit = (int) Math.floor(min(listOfLists.get(0), Device.DeviceAttribute.PROFIT) * 0.8);
                    break;
                case 2:
                    profit = (int) Math.floor(min(listOfLists.get(0), Device.DeviceAttribute.PROFIT) * 0.9);
                    break;
                case 3:
                    profit = (int) Math.floor(avg(listOfLists.get(0), Device.DeviceAttribute.PROFIT));
                    break;
                case 4:
                    profit = (int) Math.floor(
                            (avg(listOfLists.get(0), Device.DeviceAttribute.PROFIT) +
                                    max(listOfLists.get(0), Device.DeviceAttribute.PROFIT))
                                    / 2);
                    break;
                case 5:
                    profit = (int) (Math.floor(max(listOfLists.get(0), Device.DeviceAttribute.PROFIT)) * 1.1);
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
                        tempProfit = (int) Math.floor(min(listOfLists.get(i), Device.DeviceAttribute.PROFIT) * 0.8);
                        break;
                    case 2:
                        tempProfit = (int) Math.floor(min(listOfLists.get(i), Device.DeviceAttribute.PROFIT) * 0.9);
                        break;
                    case 3:
                        tempProfit = (int) Math.floor(avg(listOfLists.get(i), Device.DeviceAttribute.PROFIT));
                        break;
                    case 4:
                        tempProfit = (int) Math.floor(
                                (avg(listOfLists.get(i), Device.DeviceAttribute.PROFIT) +
                                        max(listOfLists.get(i), Device.DeviceAttribute.PROFIT))
                                        / 2);
                        break;
                    case 5:
                        tempProfit = (int) Math.floor(1.1 * max(listOfLists.get(i), Device.DeviceAttribute.PROFIT));
                        break;
                }
                profit += (1 + (i * 5)) * tempProfit;
                div += 1 + (i * 5);
            }
            profit /= div;
        }

        return profit;
    }

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
}
