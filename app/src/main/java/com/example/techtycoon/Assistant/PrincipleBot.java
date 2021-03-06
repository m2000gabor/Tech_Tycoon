package com.example.techtycoon.Assistant;


import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.Wrapped_DeviceAndCompanyList;

import java.util.LinkedList;
import java.util.List;

import androidx.core.util.Pair;

class PrincipleBot{
    private List<Integer> PRINCIPLE_WEIGHTS;
    private List<Principle> principles;
    private double minScoreToReact;

    PrincipleBot(double minScoreToReact,List<Integer> principleWeights,List<Principle> principles) {
        if(principleWeights.size() != principles.size()){
            throw new IllegalArgumentException("Weights and principles must have the same size");
        }
        this.minScoreToReact=minScoreToReact;
        this.PRINCIPLE_WEIGHTS = principleWeights;
        this.principles = principles;
    }

    Wrapped_DeviceAndCompanyList work(List<Company> companyList, List<Device> deviceList, List<Device> myDevices, Company myCompany, Wrapped_DeviceAndCompanyList ret) {
        boolean resume;
        do {
            boolean clean=ret.insert.isEmpty() && ret.update.isEmpty() && ret.delete.isEmpty() ;
            //getScore*weight and do the most important
            //index in principles,score
            LinkedList<Pair<Integer, Integer>> principleImportance = new LinkedList<>();
            //get scores from the principles
            for (int i = 0; i < principles.size(); i++) {
                if(principles.get(i).needsCleanInput() && !clean){continue;}
                int score=principles.get(i).getScore(myCompany, myDevices, companyList, deviceList) * PRINCIPLE_WEIGHTS.get(i);
                if(Double.isNaN(score)){continue;}
                //Pair<Integer,Integer> p=new Pair<Integer, Integer>(i, score);
                Pair<Integer,Integer> p= Pair.create(i, score);
                principleImportance.add(p);
            }
            principleImportance.sort((o1, o2) -> Double.compare(o2.second, o1.second));

            //whether is it reach the minReactScore
            if (principleImportance.get(0).second < minScoreToReact) {
                myCompany.logs = myCompany.logs + "\n" + principles.get(principleImportance.get(0).first).name() + " is not important enough. Score: "+principleImportance.get(0).second;

                break;
                //if the repair would need a clean input
            }else if(principles.get(principleImportance.get(0).first).needsCleanInput() && !clean){
                myCompany.logs = myCompany.logs + "\n" + principles.get(principleImportance.get(0).first).name() + " needs clean input.";
                break;
            }

            //do the most important
            Wrapped_DeviceAndCompanyList result = principles.get(principleImportance.get(0).first).repair(myCompany, myDevices, companyList, deviceList);
            if (result == null) {
                resume=false;
                myCompany.logs = myCompany.logs + "\n" + principles.get(principleImportance.get(0).first).name() + " principle repairing was unsuccessful.";
            } else {
                resume=true;
                //ret = result;
                ret.insert.addAll(result.insert);
                ret.update.addAll(result.update);
                ret.delete.addAll(result.delete);

                myCompany=result.companies.get(0);
                myCompany.logs = myCompany.logs + "\n" + principles.get(principleImportance.get(0).first).name() + " principle is successfully repaired";
            }
        }while(resume);
        ret.UpdateCompanies.add(myCompany);
        return ret;
    }
}