package com.example.techtycoon;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class Simulator {
    ///Simulator 2.3.
    //TODO implement other profiles
    //maybe there should be zones defined by some percentage, or above a min value/price ratio

    private DeviceViewModel deviceViewModel;
    double lastAvgPrice;

    Simulator(DeviceViewModel model,double lastAvgPr){this.deviceViewModel=model;this.lastAvgPrice=lastAvgPr;}

    void simulate(){
        List<Company> companyList =deviceViewModel.getAllCompaniesList();
        List<Device> deviceList=deviceViewModel.getAllDevicesList();

        if(lastAvgPrice==0){lastAvgPrice=deviceList.get(0).getPrice();}

        int[] sold=new int[deviceList.size()];
        for(int i=0;i<deviceList.size();i++){sold[i]=0; deviceList.get(i).soldPieces=0;}
        selling(sold,deviceList);
        //save the number of sold items
        for(int i=0;i<deviceList.size();i++){
            Device device =deviceList.get(i);
            device.soldPieces=sold[i];
            deviceViewModel.updateDevices(device);
        }
        //null companies last profit
        for(int i=0;i<companyList.size();i++){
            Company company =companyList.get(i);
            company.lastProfit=0;
            deviceViewModel.updateCompanies(company);
        }

        earning(sold,deviceList,companyList);
        Company[] varargs =companyList.toArray(new Company[0]);

        //apply changes
        deviceViewModel.updateCompanies(varargs);

    }

    private void selling(int[] sold, @NotNull List<Device> deviceList){
        ///int[0] is the number of customers per month!
        ///custmNum,price,ram,mem
        //Profiles:
        int[] midRange={100,5,5,5};
        sellingToOneProfile2(sold,deviceList,midRange);

        /*
        int[] cheep={200,10,2,2};
        sellingToOneProfile2(sold,deviceList,cheep);

        int[] status_symbol={25,3,6,5};
        sellingToOneProfile2(sold,deviceList,status_symbol);

        int[] photo={1,4,2,10};
        sellingToOneProfile2(sold,deviceList,photo);*/
    }

    private void sellingToOneProfile2(int[] sold, @NotNull List<Device> deviceList,int[] weights){
        int customerNum=weights[0];
        int length=deviceList.size();
        double[] point=new double[length];
        //sums
        //double avgPrice=0;
        double avgRam=0;
        double avgMemory=0;

        //calculate avgs
        for (int i=0;i<length;i++){
            //avgPrice+=deviceList.get(i).getPrice();
            avgRam+=log2(deviceList.get(i).ram)+1;
            avgMemory+=log2(deviceList.get(i).memory)+1;
        }
        //avgPrice=avgPrice/length;
        avgRam=avgRam/length;
        avgMemory=avgMemory/length;


        //calculate points
        double price;
        double value;
        double sumPoints=0;
        for (int i=0;i<length;i++){
            //price=1/Math.pow(fx(deviceList.get(i).getPrice(), lastAvgPrice)+1, 5);
            price=(double) weights[1]*fx(deviceList.get(i).getPrice(), lastAvgPrice);
            value=(double) weights[2]*fx(log2(deviceList.get(i).ram)+1, avgRam);
            value=value + (double) weights[3]*fx(log2(deviceList.get(i).memory)+1, avgMemory);
            if(value-price>0){point[i]=value-price;
            }else{point[i]=0;}
            sumPoints+=point[i];
        }
        double avgSumPoints=sumPoints/length;
        sumPoints=0;
        for (int i=0;i<length;i++){
            point[i]*=1+gauss(point[i],3,avgSumPoints);
            sumPoints+=point[i];
        }


        //calculate sold numbers
        for (int i=0;i<length;i++){
            sold[i]+=(int) Math.round(customerNum*point[i]/sumPoints);
        }

        //save shared pref
        double sumPrice=0;
        for (int i=0;i<length;i++){ sumPrice+=sold[i]*deviceList.get(i).getPrice(); }
        lastAvgPrice=sumPrice/(customerNum);
    }

    private void earning(int[] sold, @NotNull List<Device> deviceList,@NotNull List<Company> companyList){
        for (int i=0;i<deviceList.size();i++){

            int j=0;
            while(j<companyList.size() && companyList.get(j).companyId != deviceList.get(i).ownerCompanyId){j++;}
            companyList.get(j).money+=sold[i]*deviceList.get(i).profit;
            companyList.get(j).lastProfit+=sold[i]*deviceList.get(i).profit;
        }
    }

    static int log2(int x) { return (int) Math.round(Math.log(x) / Math.log(2) + 1e-10); }
    private static double log2 (double x) { return (Math.log(x) / Math.log(2) + 1e-10); }

    private static double fx(double value,double average) {
        double norm=value/average;
        if(norm<=1.0) {
            return Math.pow(norm,2);
        }else if(norm > 2.0){return fx(2*average,average);
        }else{return Math.pow(norm-1,0.5)+fx(average,average);}
    }

    //x =ertek, szigma=meredekseg, mu kozeppont
    private static double gauss(double x, double szigma, double mu){
        return (1/(szigma*Math.sqrt(2*Math.PI)))*(Math.exp((-1*Math.pow(x-mu,2))/(2*Math.pow(szigma,2))));
    }

}
