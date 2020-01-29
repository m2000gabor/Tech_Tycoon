package com.example.techtycoon;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class Simulator {
    ///Simulator 2.5. - new profile
    //TODO implement other profiles
    //maybe there should be zones defined by some percentage, or above a min value/price ratio

    private DeviceViewModel deviceViewModel;
    double lastAvgPrice;
    double lastAvgRam;
    double lastAvgMemory;

    Simulator(DeviceViewModel model,double lastAvgPr,double lastAvgR,double lastAvgMem){
        this.deviceViewModel=model;
        this.lastAvgPrice=lastAvgPr;
        this.lastAvgRam=lastAvgR;
        this.lastAvgMemory=lastAvgMem;
    }

    void simulate(){
        List<Company> companyList =deviceViewModel.getAllCompaniesList();
        List<Device> deviceList=deviceViewModel.getAllDevicesList();

        if(lastAvgPrice==0){lastAvgPrice=deviceList.get(0).getPrice();}
        if(lastAvgRam==0){lastAvgRam=deviceList.get(0).ram;}
        if(lastAvgMemory==0){lastAvgMemory=deviceList.get(0).memory;}

        int[] sold=new int[deviceList.size()];
        for(int i=0;i<deviceList.size();i++){sold[i]=0; deviceList.get(i).soldPieces=0;}

        selling(sold,deviceList);

        //save the number of sold items
        for(int i=0;i<deviceList.size();i++){deviceList.get(i).soldPieces=sold[i];}
        //null companies last profit
        for(int i=0;i<companyList.size();i++){companyList.get(i).lastProfit=0; }

        earning(sold,deviceList,companyList);

        Company[] varargsComp =companyList.toArray(new Company[0]);
        Device[] varargsDev=deviceList.toArray(new Device[0]);

        //apply changes
        deviceViewModel.updateCompanies(varargsComp);
        deviceViewModel.updateDevices(varargsDev);

    }

    private void selling(int[] sold, @NotNull List<Device> deviceList){
        ///int[0] is the number of customers per month!
        ///custmNum,price,ram,mem

        //Profiles:
        int[] midRange={1000,5,5,5};
        sellingToOneProfile2(sold,deviceList,midRange);

        int[] top={200,5,10,10};
        sellingToOneProfile2(sold,deviceList,top);

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

        //calculate points
        double price;
        double value;
        double sumPoints=0;
        for (int i=0;i<length;i++){
            //price=1/Math.pow(fx(deviceList.get(i).getPrice(), lastAvgPrice)+1, 5);
            price=(double) fx(deviceList.get(i).getPrice(), lastAvgPrice);
            price=Math.pow(price,weights[1]*0.2);
            value=(double) Math.pow(fx(log2(deviceList.get(i).ram)+1, lastAvgRam),weights[2]*0.2);
            value+= (double) Math.pow(fx(log2(deviceList.get(i).memory)+1, lastAvgMemory),weights[3]*0.2);
            point[i]=value/price;
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

        //save shared pref
        double sumPrice=0;
        double sumRam=0;
        double sumMem=0;
        for (int i=0;i<length;i++){
            sumPrice+=sold[i]*deviceList.get(i).getPrice();
            sumRam+=sold[i]*log2(deviceList.get(i).ram);
            sumMem+=sold[i]*log2(deviceList.get(i).memory);

        }
        lastAvgPrice=0.2*(lastAvgPrice-(sumPrice/customerNum));
        lastAvgRam=0.2*(lastAvgRam-(sumRam/customerNum));
        lastAvgMemory=0.2*(lastAvgMemory-(sumMem/customerNum));
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
        //}else if(norm > 2.0){return fx(2*average,average);
        }else{return Math.pow(norm-1,0.5)+fx(average,average);}
    }

    //x =ertek, szigma=meredekseg, mu kozeppont
    private static double gauss(double x, double szigma, double mu){
        return (1/(szigma*Math.sqrt(2*Math.PI)))*(Math.exp((-1*Math.pow(x-mu,2))/(2*Math.pow(szigma,2))));
    }

}
