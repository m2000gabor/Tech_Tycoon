package com.example.techtycoon;

import android.util.Log;

import com.example.techtycoon.Assistant.AssistantManager;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

//todo implement a lot of tests
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

//@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    private static int unique_id=100;
    @Test
    public void simulator_equalChances(){
        //make the companies
        List<Company> companyList=new LinkedList<>();
        Company c1=new Company("c1",1,MainActivity.STARTING_LEVELS);
        c1.companyId=1;
        companyList.add(c1);
        Company c2=new Company("c2",1,MainActivity.STARTING_LEVELS);
        c2.companyId=2;
        companyList.add(c2);

        //make the attributes for the devices
        int[] defaultAttributes=new int[Device.NUMBER_OF_ATTRIBUTES];
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {defaultAttributes[i]=1;}

        //make the devices
        List<Device> deviceList=new LinkedList<>();
        Device d1=new Device("dev1",10,20,1,defaultAttributes);
        Device d2=new Device("dev2",10,20,2,defaultAttributes);
        deviceList.add(d1);
        deviceList.add(d2);

        Simulator simulator=Simulator.getInstance(deviceList,companyList);
        Wrapped_DeviceAndCompanyList results=simulator.simulate();

        assertTrue(0==results.companies.get(0).lastProfit-results.companies.get(0).lastProfit);
        assertTrue(0==results.devices.get(0).getSoldPieces()-results.devices.get(0).getSoldPieces());
    }

    @Test
    public void turns_100(){
        final int NUM_OF_TURNS=100;
        final int NUMBER_OF_COMPANIES=5;
        final List<Integer> assistantList= Arrays.asList(2,3,5,6,7);

        //Mockito

        //make the companies
        List<Company> companyList=new LinkedList<>();
        for(int i=0;i<NUMBER_OF_COMPANIES;i++){
            Company c=new Company("c"+Integer.toString(i),1,MainActivity.STARTING_LEVELS);
            c.companyId=i;
            c.assistantType=assistantList.get(i);
            c.assistantStatus=AssistantManager.getDefaultStatus(c.assistantType);
            companyList.add(c);
        }

        //assistants
        AssistantManager assistantManager=new AssistantManager();

        //Devices

        //make the attributes for the devices
        int[] defaultAttributes=new int[Device.NUMBER_OF_ATTRIBUTES];
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {defaultAttributes[i]=1;}

        //make 1 device for every company
        List<Device> deviceList=new LinkedList<>();
        for(int i=0;i<NUMBER_OF_COMPANIES;i++){
            Device d=new Device("devOf"+Integer.toString(i),100,20,i,defaultAttributes);
            companyList.get(i).usedSlots=1;
            d.id=i*1000;
            d.cost=DeviceValidator.getOverallCost(d);
            deviceList.add(d);
        }

        for (int i = 0; i < NUM_OF_TURNS; i++) {
            Simulator simulator=Simulator.getInstance(deviceList,companyList);
            //simulate
            Wrapped_DeviceAndCompanyList results=simulator.simulate();
            //update the database
            results.UpdateCompanies.addAll(results.companies);
            updateTheDatabase(deviceList,companyList,results);
            //check simulation results
            int[] numOfDev =new int[NUMBER_OF_COMPANIES];
            for (Device d :deviceList) {
                numOfDev[d.ownerCompanyId]++;
                assertTrue("Failed at turn "+i,d.profit>0);
                assertEquals(d.cost, DeviceValidator.getOverallCost(d));
            }
            for (Company c :companyList) {
                assertTrue(c.money >= 0);
                assertTrue(c.marketing >= 0);
                assertTrue(c.usedSlots<=c.maxSlots);
                assertEquals(c.name+" failed at turn "+i,c.usedSlots,numOfDev[c.companyId]);
            }



            //assistants act
            Wrapped_DeviceAndCompanyList resultOfAssistants=assistantManager.trigger(results.companies,results.devices);
            // update the database
            updateTheDatabase(deviceList,companyList,resultOfAssistants);
            //check assistants

            //check general things
        }

        //results
        companyList.sort((o1, o2) -> o2.getMarketValue()-o1.getMarketValue());
        System.out.println("Results of the test");
        for(Company c: companyList){
            System.out.println(c.name+"("+AssistantManager.getAssistantName(c.assistantType)+")"+"\tvalue: "+c.getMarketValue());
        }

    }

    private void updateTheDatabase(List<Device> deviceList_old,List<Company> companyList_old,
                                   Wrapped_DeviceAndCompanyList results_new){
        for (Device d_new:results_new.update) {
            for (int j=0;j<deviceList_old.size();j++) {
                if(deviceList_old.get(j).id == d_new.id){deviceList_old.set(j,d_new);}
            }
        }
        for(Device d:results_new.insert){d.id=unique_id++;}
        deviceList_old.addAll(results_new.insert);
        deviceList_old.removeAll(results_new.delete);

        companyList_old.clear();
        companyList_old.addAll(results_new.UpdateCompanies);
    }

}