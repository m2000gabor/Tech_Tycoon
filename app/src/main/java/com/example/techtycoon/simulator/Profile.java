package com.example.techtycoon.simulator;

import com.example.techtycoon.Device;

public interface Profile {
    int getNumberOfCustomers();
    double getScore(Device device,double[] attrAverages);
    double getPriceBias();
    double getValueBias();
}
