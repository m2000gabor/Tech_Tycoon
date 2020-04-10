package com.example.techtycoon.Assistant;

import android.util.Pair;

import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class PrincipleBot{
    private final List<Integer> PRINCIPLE_WEIGHTS;
    private final ArrayList<Principle> principles;
    private final double minScoreToReact;

    PrincipleBot(double minScoreToReact,List<Integer> principleWeights,ArrayList<Principle> principles) {
        if(principleWeights.size() != principles.size()){
            throw new IllegalArgumentException("Weights and principles must have the same size");
        }
        this.minScoreToReact=minScoreToReact;
        this.PRINCIPLE_WEIGHTS = principleWeights;
        this.principles = principles;
    }

    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        boolean successfulRepair=false;
        //getScore*weight and do the most important
        do {
            LinkedList<Pair<Integer, Double>> principleImportance = new LinkedList<>();
            for (int i = 0; i < principles.size(); i++) {
                principleImportance.add(new Pair<>(i, principles.get(i).getScore(myCompany, myDevices, companyList, deviceList) * PRINCIPLE_WEIGHTS.get(i)));
            }
            principleImportance.sort((o1, o2) -> Double.compare(o2.second, o1.second));
            if(principleImportance.get(0).second < minScoreToReact){
                myCompany.logs=myCompany.logs+"\n"+principleImportance.get(0).first+" is not important enough.";
                break;}
            Wrapped_DeviceAndCompanyList result = principles.get(principleImportance.get(0).first).repair(myCompany, myDevices, companyList, deviceList);
            if (result == null) {
                successfulRepair=false;
                myCompany.logs=myCompany.logs+"\n"+principleImportance.get(0).first+". principle repairing was unsuccessful.";
            } else {
                myCompany=result.companies.get(0);
                myDevices=result.devices;
                successfulRepair=true;
                myCompany.logs=myCompany.logs+"\n"+principleImportance.get(0).first+". principle is successfully repaired";
            }
        }while(successfulRepair);
        ret.UpdateCompanies=Collections.singletonList(myCompany);
        ret.update=myDevices;
        return ret;
    }
}