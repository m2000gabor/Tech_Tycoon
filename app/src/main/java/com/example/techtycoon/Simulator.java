package com.example.techtycoon;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class Simulator {
    private static final int NUM_OF_DEVICE_FIELDS = 3;
    private DeviceViewModel deviceViewModel;

    Simulator(DeviceViewModel model){this.deviceViewModel=model;}

    void simulate(){
        List<Company> companyList =deviceViewModel.getAllCompaniesList();
        List<Device> deviceList=deviceViewModel.getAllDevicesList();
        int[] sold=new int[deviceList.size()];
        for(int i=0;i<deviceList.size();i++){sold[i]=0; deviceList.get(i).soldPieces=0;}
        selling(sold,deviceList);
        //save the number of sold items
        for(int i=0;i<deviceList.size();i++){
            Device device =deviceList.get(i);
            device.soldPieces=sold[i];
            deviceViewModel.updateDevices(device);
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
        int[] midRange={100,5,3,2};
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
        double avgPrice=0;
        double avgRam=0;
        double avgMemory=0;

        //calculate avgs
        for (int i=0;i<length;i++){
            avgPrice+=deviceList.get(i).getPrice();
            avgRam+=log2(deviceList.get(i).ram);
            avgMemory+=log2(deviceList.get(i).memory);
        }
        avgPrice=avgPrice/length;
        avgRam=avgRam/length;
        avgMemory=avgMemory/length;


        //calculate points
        double price;
        double value;
        double sumPoints=0;
        for (int i=0;i<length;i++){
            value=0;
            price=weights[1]*fx(deviceList.get(i).getPrice(),avgPrice);
            value+=weights[2]*log2(fx(deviceList.get(i).ram,avgRam));
            value+=weights[3]*log2(fx(deviceList.get(i).memory,avgMemory));
            point[i]=value/price;
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
        }
    }

    private static int log2(int x) { return (int) (Math.log(x) / Math.log(2) + 1e-10); }
    private static double log2 (double x) { return (Math.log(x) / Math.log(2) + 1e-10); }

    //egyenes aranyossaghoz pl:RAM
    private static double fx(double value,double average) {
        if(value<=average){
            return Math.pow(value,2);
        }else{return Math.sqrt(value)+fx(average,average);}
    }

    //forditott aranyossaghoz pl: Price
    private static double gx(double value,double average) {
        if(value<=average){
            return Math.pow(value,2);
        }else{return Math.sqrt(value)+fx(average,average);}
    }

}
