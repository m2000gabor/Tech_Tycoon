package com.example.techtycoon.simulator;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Simulator {
    //todo profit is fallen after some turns
    //todo write unit tests
    ///Simulator 3.0.0 - profiles introduced

    private List<Device> deviceList;
    private List<Company> companyList;
    private static final double AVG_CHANGING_SPEED =0.3;
    public double[] attrAverages;
    public double lastAvgPrice;
    private double customerNum;

    public Simulator(List<Device> deviceList, List<Company> companyList, double lastAvgPrice, double[] attributeAvgs){
        this.deviceList = deviceList;
        this.companyList =companyList;
        this.lastAvgPrice=lastAvgPrice;
        if(attributeAvgs.length!=Device.getAllAttribute().size()){
            throw new ArrayIndexOutOfBoundsException("Not enough averages");
        }
        this.attrAverages=attributeAvgs.clone();
    }

    public static Simulator getInstance(List<Device> deviceList, List<Company> companyList){
        double sumPrice=0;
        double customerNum=0;
        double[] sumAttributes=new double[Device.NUMBER_OF_ATTRIBUTES];
        for (int i=0;i<Device.NUMBER_OF_ATTRIBUTES;i++){sumAttributes[i]=0;}
        for (int i=0;i<deviceList.size();i++){
            customerNum+=deviceList.get(i).getSoldPieces();
            sumPrice+=deviceList.get(i).getSoldPieces()*deviceList.get(i).getPrice();
            int j=0;
            for (Device.DeviceAttribute a : Device.getAllAttribute()){
                sumAttributes[j++]+=deviceList.get(i).getSoldPieces()*deviceList.get(i).getFieldByAttribute(a);
            }
        }

        double lastAvgPrice= (sumPrice/customerNum);
        double[] attrAverages=new double[Device.NUMBER_OF_ATTRIBUTES];
        for (int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
            attrAverages[j]+= sumAttributes[j]/customerNum;
        }
        return new Simulator(deviceList,companyList,lastAvgPrice,attrAverages);
    }

    public Wrapped_DeviceAndCompanyList simulate(){
        if(Double.isNaN(lastAvgPrice) || lastAvgPrice<=0){lastAvgPrice=deviceList.get(0).getPrice();}
        for(int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            if (Double.isNaN(attrAverages[i]) || attrAverages[i] <= 0) {
                attrAverages[i] =(double) deviceList.get(0).getFieldByAttribute(Device.getAllAttribute().get(i));
            }
        }

        int[] sold=new int[deviceList.size()];

        selling(sold,deviceList,companyList);

        //save the number of sold items
        for(int i=0;i<deviceList.size();i++){deviceList.get(i).setSoldPieces(sold[i]);}
        //null companies last profit
        for(int i=0;i<companyList.size();i++){
            companyList.get(i).lastProfit=0;
            companyList.get(i).marketing-=2;
            if(companyList.get(i).marketing<0){companyList.get(i).marketing=0;}
        }

        earning(sold,deviceList,companyList);

        //set averages
        double sumPrice=0;
        double[] sumAttributes=new double[Device.NUMBER_OF_ATTRIBUTES];
        for (int i=0;i<Device.NUMBER_OF_ATTRIBUTES;i++){sumAttributes[i]=0;}
        for (int i=0;i<deviceList.size();i++){
            sumPrice+=sold[i]*deviceList.get(i).getPrice();
            for (int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
                sumAttributes[j]+=sold[i]*deviceList.get(i).getFieldByAttribute(Device.getAllAttribute().get(j));
            }
        }

        lastAvgPrice+= AVG_CHANGING_SPEED *((sumPrice/customerNum)-lastAvgPrice);
        for (int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
            attrAverages[j]+= AVG_CHANGING_SPEED *((sumAttributes[j]/customerNum)-attrAverages[j]);
        }

        return new Wrapped_DeviceAndCompanyList(deviceList,companyList);
    }

    private void selling(int[] sold, @NotNull List<Device> deviceList,List<Company> companyList){
        List<Company> compsToDevList=new LinkedList<>();
        for (int i=0;i<deviceList.size();i++){
            int j=0;
            while (companyList.get(j).companyId != deviceList.get(i).ownerCompanyId){j++;}
            compsToDevList.add(companyList.get(j));
        }

        //Profiles:
        customerNum=0;
        List<Profile> profiles= Arrays.asList(new Profile_midRange(),new Profile_top(),new Profile_cheap(),new Profil_beauty());
        for (Profile p :profiles) {
            customerNum+=p.getNumberOfCustomers();
            sellingToOneProfile_v3(sold,deviceList,compsToDevList,p);
        }

    }

    private void sellingToOneProfile_v3(int[] sold,List<Device> deviceList,List<Company> companiesToDevList,Profile profile){
        int customerNum=profile.getNumberOfCustomers();
        double[] point=new double[deviceList.size()];

        //calculate points
        double price;
        double value;
        double sumPoints=0;
        for (int i=0;i<deviceList.size();i++){
            //calc price
            if(deviceList.get(i).getPrice()>2*lastAvgPrice){
                price=(double) fx(Math.pow(deviceList.get(i).getPrice(),(deviceList.get(i).getPrice()/lastAvgPrice)-1), lastAvgPrice);
            }else{
                price=(double) fx(deviceList.get(i).getPrice(), lastAvgPrice);
            }

            //calc value
            value=profile.getScore(deviceList.get(i),attrAverages);

            //value/price ration correction
            price=Math.pow(price,profile.getPriceBias());//how much the price differs
            value=Math.pow(value,profile.getValueBias());//how much the tech quality differs

            point[i]=(value/price)*(1+(companiesToDevList.get(i).marketing*0.005) );
            sumPoints+=point[i];
        }
        double avgPoints=sumPoints/deviceList.size();
        sumPoints=0;
        for (int i=0;i<deviceList.size();i++){
            //point[i]*=1+gauss(point[i],1,avgPoints);
            point[i]*=fx(point[i],avgPoints);
            sumPoints+=point[i];
        }

        //calculate sold numbers
        for (int i=0;i<deviceList.size();i++){
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
    static double log2 (double x) { return (Math.log(x) / Math.log(2) + 1e-10); }

    static double fx(double value,double average) {
        double norm=value/average;
        if(norm<=1.0) {
            return Math.pow(norm, 2);
        //}else if(norm>2){return Math.pow(norm-2,2)+fx(2*average,average);
        }else{return Math.pow(norm-1,0.5)+fx(average,average);}
    }

    //x =ertek, szigma=meredekseg, mu kozeppont
    static double gauss(double x, double szigma, double mu){
        return (1/(szigma*Math.sqrt(2*Math.PI)))*(Math.exp((-1*Math.pow(x-mu,2))/(2*Math.pow(szigma,2))));
    }

}
