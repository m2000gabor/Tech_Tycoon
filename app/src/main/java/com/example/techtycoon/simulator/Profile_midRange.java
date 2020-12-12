package com.example.techtycoon.simulator;

import com.example.techtycoon.Device;

import static com.example.techtycoon.simulator.Simulator.fx;

class Profile_midRange implements Profile {
    private static int[] attrWeights={
            7,6,//storage
            5,3,3,3,3,//body
            5,3,2,2//design
    };

    @Override
    public int getNumberOfCustomers() {return 1200;}

    @Override
    public double getScore(Device device,double[] attrAverages) {
        double value=0;
        for(int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
            value+= (double) Math.pow(fx(device.getFieldByAttribute(Device.getAllAttribute().get(j)), attrAverages[j]),attrWeights[j]*0.2);
        }
        return value;
    }

    @Override
    public double getPriceBias() {
        return 1.5;
    }

    @Override
    public double getValueBias() {
        return 2;
    }
}
