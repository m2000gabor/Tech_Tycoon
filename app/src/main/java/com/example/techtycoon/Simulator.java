package com.example.techtycoon;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;


public class Simulator {
    //todo There's no place for low profit companies
    //todo write unit tests
    ///Simulator 2.8 - new value and price ratio handling

    List<Device> deviceList;
    List<Company> companyList;
    private static final double AVG_CHANGING_SPEED =0.15;//isnt it too slow?
    public double[] attrAverages;
    public double lastAvgPrice;
    private double customerNum;

    public Simulator(List<Device> deviceList, List<Company> companyList, double lastAvgPrice, double[] attributeAvgs){
        this.deviceList = deviceList;
        this.companyList =companyList;
        this.lastAvgPrice=lastAvgPrice;
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
            for (int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
                sumAttributes[j]+=deviceList.get(i).getSoldPieces()*deviceList.get(i).getFieldByNum(j);
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
        for(int i = 0; i< Device.NUMBER_OF_ATTRIBUTES; i++) {
            if (Double.isNaN(attrAverages[i]) || attrAverages[i] <= 0) {
                attrAverages[i] =(double) deviceList.get(0).getFieldByNum(i);
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
                sumAttributes[j]+=sold[i]*deviceList.get(i).getFieldByNum(j);
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
        ///priceValueWeights: [0] is the number of customers per month, [1] is price, [2] is value
        ///ram,mem,design,material,color,ip,bezel


        //Profiles:
        customerNum=0;
        //todo profile class


        int[] midRange={6,6,5,3,3,3,3};
        int midCust=1200;
        customerNum+=midCust;
        sellingToOneProfile2(sold,deviceList,compsToDevList, new double[]{midCust,1.5,2},midRange);

        int[] top={10,10,4,6,3,4,4};
        int topCust=300;
        customerNum+=topCust;
        sellingToOneProfile2(sold,deviceList,compsToDevList,new double[]{topCust,1.7,4},top);

        int[] cheep={5,5,2,2,2,3,2};
        int cheapCust=500;
        customerNum+=cheep[0];
        sellingToOneProfile2(sold,deviceList,compsToDevList,new double[]{cheapCust,3,2.5},cheep);

        int[] beauty={2,2,10,7,8,2,5};
        int beautyCust=50;
        customerNum+=beautyCust;
        sellingToOneProfile2(sold,deviceList,compsToDevList, new double[]{beautyCust,1.5,2},beauty);

    }

    private void sellingToOneProfile2(int[] sold,List<Device> deviceList,List<Company> companiesToDevList,double[] otherWeights,int[] attrWeights){
        int customerNum=(int) otherWeights[0];
        int length=deviceList.size();
        double[] point=new double[length];

        //calculate points
        double price;
        double value;
        double sumPoints=0;
        for (int i=0;i<length;i++){
            //calc price
            if(deviceList.get(i).getPrice()>2*lastAvgPrice){
                price=(double) fx(Math.pow(deviceList.get(i).getPrice(),(deviceList.get(i).getPrice()/lastAvgPrice)-1), lastAvgPrice);
            }else{
                price=(double) fx(deviceList.get(i).getPrice(), lastAvgPrice);
            }

            //calc value
            value=0;
            for(int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
                value+= (double) Math.pow(fx(deviceList.get(i).getFieldByNum(j), attrAverages[j]),attrWeights[j]*0.2);
            }

            //value/price ration correction
            price=Math.pow(price,otherWeights[1]);//how much the price differs
            value=Math.pow(value,otherWeights[2]);//how much the tech quality differs

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
            return Math.pow(norm, 2);
        //}else if(norm>2){return Math.pow(norm-2,2)+fx(2*average,average);
        }else{return Math.pow(norm-1,0.5)+fx(average,average);}
    }

    //x =ertek, szigma=meredekseg, mu kozeppont
    private static double gauss(double x, double szigma, double mu){
        return (1/(szigma*Math.sqrt(2*Math.PI)))*(Math.exp((-1*Math.pow(x-mu,2))/(2*Math.pow(szigma,2))));
    }

}
