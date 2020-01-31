package com.example.techtycoon;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;


class Simulator {
    ///Simulator 2.6. - generified
    //TODO implement other profiles
    //todo find out why crashes
    //maybe there should be zones defined by some percentage, or above a min value/price ratio

    private DeviceViewModel deviceViewModel;
    private static final int ATTRIBUTES_IN_ARRAY =5;
    private static final double MARKET_SPEED=0.1;
    double[] averages;
    double lastAvgPrice;
    double lastAvgRam;
    double lastAvgMemory;
    private double customerNum;

    Simulator(DeviceViewModel model,double lastAvgPr,double lastAvgR,double lastAvgMem,double[] bodyAvgs){
        this.deviceViewModel=model;
        this.lastAvgPrice=lastAvgPr;
        this.lastAvgRam=lastAvgR;
        this.lastAvgMemory=lastAvgMem;
        this.averages=bodyAvgs;
    }

    void simulate(){
        List<Company> companyList =deviceViewModel.getAllCompaniesList();
        List<Device> deviceList=deviceViewModel.getAllDevicesList();

        if(lastAvgPrice<=0){lastAvgPrice=deviceList.get(0).getPrice();}
        if(lastAvgRam<=0){lastAvgRam=deviceList.get(0).ram;}
        if(lastAvgMemory<=0){lastAvgMemory=deviceList.get(0).memory;}
        for(int i = 0; i< ATTRIBUTES_IN_ARRAY; i++) {
            if (averages[i] <= 0) {
                averages[i] = deviceList.get(0).getParams()[1][i];
            }
        }

        int[] sold=new int[deviceList.size()];
        for(int i=0;i<deviceList.size();i++){sold[i]=0; deviceList.get(i).soldPieces=0;}

        selling(sold,deviceList,companyList);

        //save the number of sold items
        for(int i=0;i<deviceList.size();i++){deviceList.get(i).soldPieces=sold[i];}
        //null companies last profit
        for(int i=0;i<companyList.size();i++){
            companyList.get(i).lastProfit=0;
            companyList.get(i).marketing-=2;
        }

        earning(sold,deviceList,companyList);

        Company[] varargsComp =companyList.toArray(new Company[0]);
        Device[] varargsDev=deviceList.toArray(new Device[0]);

        //apply changes
        deviceViewModel.updateCompanies(varargsComp);
        deviceViewModel.updateDevices(varargsDev);

        //set averages
        double sumPrice=0;
        double sumRam=0;
        double sumMem=0;
        double[] sumBody=new double[ATTRIBUTES_IN_ARRAY];
        for (int i=0;i<ATTRIBUTES_IN_ARRAY;i++){sumBody[i]=0;}
        for (int i=0;i<deviceList.size();i++){
            sumPrice+=sold[i]*deviceList.get(i).getPrice();
            sumRam+=sold[i]*(log2(deviceList.get(i).ram)+1);
            sumMem+=sold[i]*(log2(deviceList.get(i).memory)+1);
            for (int j = 0; j< ATTRIBUTES_IN_ARRAY; j++){
                sumBody[j]+=sold[i]*deviceList.get(i).getParams()[1][j];
            }
        }

        lastAvgPrice+=MARKET_SPEED*((sumPrice/customerNum)-lastAvgPrice);
        lastAvgRam+=MARKET_SPEED*((sumRam/customerNum)-lastAvgRam);
        lastAvgMemory+=MARKET_SPEED*((sumMem/customerNum)-lastAvgMemory);
        for (int j = 0; j< ATTRIBUTES_IN_ARRAY; j++){
            averages[j]+=MARKET_SPEED*((sumBody[j]/customerNum)-averages[j]);
        }

    }

    private void selling(int[] sold, @NotNull List<Device> deviceList,List<Company> companyList){
        List<Company> compsToDevList=new LinkedList<>();
        for (int i=0;i<deviceList.size();i++){
            int j=0;
            while (companyList.get(j).companyId != deviceList.get(i).ownerCompanyId){j++;}
            compsToDevList.add(companyList.get(j));
        }
        ///int[0] is the number of customers per month!
        ///custmNum,price,ram,mem

        //Profiles:
        int[] midRange={1000,5,5,5,4,2,2,2,2};
        sellingToOneProfile2(sold,deviceList,compsToDevList,midRange);

        int[] top={200,5,10,10,3,5,3,3,3};
        sellingToOneProfile2(sold,deviceList,compsToDevList,top);

        customerNum=1200;

        /*
        int[] cheep={200,10,2,2};
        sellingToOneProfile2(sold,deviceList,cheep);

        int[] status_symbol={25,3,6,5};
        sellingToOneProfile2(sold,deviceList,status_symbol);

        int[] photo={1,4,2,10};
        sellingToOneProfile2(sold,deviceList,photo);*/
    }

    private void sellingToOneProfile2(int[] sold,List<Device> deviceList,List<Company> companiesToDevList,int[] weights){
        int customerNum=weights[0];
        int length=deviceList.size();
        double[] point=new double[length];

        //calculate points
        double price;
        double value;
        double sumPoints=0;
        for (int i=0;i<length;i++){
            price=(double) fx(deviceList.get(i).getPrice(), lastAvgPrice);
            price=Math.pow(price,weights[1]*0.2);
            value=(double) Math.pow(fx(log2(deviceList.get(i).ram)+1, lastAvgRam),weights[2]*0.2);
            value+= (double) Math.pow(fx(log2(deviceList.get(i).memory)+1, lastAvgMemory),weights[3]*0.2);

            for(int j = 0; j< ATTRIBUTES_IN_ARRAY; j++){
                value+= (double) Math.pow(fx(deviceList.get(i).getParams()[1][j], averages[j]),weights[j+3]*0.2);
            }

            point[i]=(value/price)*(1+(companiesToDevList.get(i).marketing*0.001) );
            sumPoints+=point[i];
        }
        double avgPoints=sumPoints/length;
        sumPoints=0;
        for (int i=0;i<length;i++){
            //point[i]*=1+gauss(point[i],1,avgPoints);
            point[i]*=fx(point[i],avgPoints);
            sumPoints+=point[i];
        }


        //calculate sold numbers
        for (int i=0;i<length;i++){
            sold[i]+=(int) Math.round(customerNum*point[i]/sumPoints);
        }


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
        }else{return Math.pow(norm-1,0.5)+fx(average,average);}
    }

    //x =ertek, szigma=meredekseg, mu kozeppont
    private static double gauss(double x, double szigma, double mu){
        return (1/(szigma*Math.sqrt(2*Math.PI)))*(Math.exp((-1*Math.pow(x-mu,2))/(2*Math.pow(szigma,2))));
    }

}
