package com.example.techtycoon.simulator;

import com.example.techtycoon.Device;

import static com.example.techtycoon.simulator.Simulator.fx;

class Profile_cheap implements Profile {
    private static int[] attrWeights={
            5,5,
            2,2,2,3,2,
            3,2,1,1
    };

    @Override
    public int getNumberOfCustomers() {return 500;}

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
        return 3;
    }

    @Override
    public double getValueBias() {
        return 2.5;
    }
}
