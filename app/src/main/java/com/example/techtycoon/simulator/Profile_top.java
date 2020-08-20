package com.example.techtycoon.simulator;

import com.example.techtycoon.Device;

import static com.example.techtycoon.simulator.Simulator.fx;

class Profile_top implements Profile {
    private static int[] attrWeights={10,10,4,6,3,4,4};

    @Override
    public int getNumberOfCustomers() {return 300;}

    @Override
    public double getScore(Device device, double[] attrAverages) {
        double value=0;
        for(int j = 0; j< Device.NUMBER_OF_ATTRIBUTES; j++){
            value+= (double) Math.pow(fx(device.getFieldByAttribute(Device.getAllAttribute().get(j)), attrAverages[j]),attrWeights[j]*0.2);
        }
        return value;
    }

    @Override
    public double getPriceBias() {
        return 1.7;
    }

    @Override
    public double getValueBias() {
        return 4;
    }
}
