package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;

import java.util.List;
import java.util.Random;

public class RandomXiaomiBot extends XiaomiBot{

    public RandomXiaomiBot(Version version) {
        super(version);
    }

    @Override
    protected List<Integer> getWeights(Company myCompany) {
        List<Integer> r=super.getWeights(myCompany);
        Random random=new Random();
        for(int i=0;i<r.size();i++){r.set(i,r.get(i)+random.nextInt((int)Math.round(r.get(i)*0.1)));}
        //r.forEach(i->i+=random.nextInt((int)Math.round(i)));
        return r;
    }

}
