package com.example.techtycoon;


import com.example.techtycoon.Assistant.AssistantManager;
import com.example.techtycoon.simulator.Simulator;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class BotOptimizerTest {
    private static int unique_id=100;
    final int testedAssistant=3;

    @Test
    public void printDefaultWeights(){
        System.out.println(AssistantManager.getInputLabels(testedAssistant));
        System.out.println(AssistantManager.getDefaultStatus(testedAssistant));
    }

    private String getWeights(int variant){
        String r="110;90;1000;110;111;100;100;100;100;100;100;100;100;100;100;100;120";

        //String r="110;90;1000;110;111;100;100;100;100;100;100;100;100;100;100;100;120";
        return r;
    }


    private List<Company> initGhostCompanies(int ghostsNum,List<Integer> assistantList){
        //make the ghost companies
        List<Company> companyList=new LinkedList<>();
        for(int i=0;i<ghostsNum;i++){
            Company c=new Company("c"+Integer.toString(i),1,MainActivity.STARTING_LEVELS);
            c.companyId=i;
            c.assistantType=assistantList.get(i);
            c.assistantStatus=AssistantManager.getDefaultStatus(c.assistantType);
            companyList.add(c);
        }
        return companyList;
    }
    private Company createVariant(int variantNum,int ghostsNum){
        Company c=new Company("variant"+Integer.toString(variantNum),1,MainActivity.STARTING_LEVELS);
        c.companyId=ghostsNum;
        c.assistantType=testedAssistant;
        c.assistantStatus=getWeights(variantNum);
        return c;
    }

    @Test
    public void run(){
        final int NUM_OF_TURNS=700;
        final List<Integer> ghostAssistantList= Arrays.asList(2,5,6,7,3,8);
        final int NUMBER_OF_BOT_VARIANT=10;//adjusted
        final int NUMBER_OF_GHOST_COMPANIES=ghostAssistantList.size();//not adjusted

        int bestRound=-1;
        int bestResult=0;

        for(int i=0;i<NUMBER_OF_BOT_VARIANT;i++){
            List<Company> companyList=initGhostCompanies(NUMBER_OF_GHOST_COMPANIES,ghostAssistantList);
            Company variant=createVariant(i,NUMBER_OF_GHOST_COMPANIES);
            companyList.add(variant);
            int result=runOneSimulation(companyList,NUM_OF_TURNS,variant);
            if(result>bestResult){
                bestRound=i;
                bestResult=result;
            }
        }

        System.out.println("\n--------------------\n\nBest value: "+bestResult+" With variant"+bestRound);
        System.out.println("Best default status would be: "+getWeights(bestRound));
    }



    public int runOneSimulation(List<Company> companyList,int numOfTurns,Company tested){
        Company variant=tested;
        AssistantManager assistantManager=new AssistantManager();

        //Devices

        //make the attributes for the devices
        int[] defaultAttributes=new int[Device.NUMBER_OF_ATTRIBUTES];
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {defaultAttributes[i]=1;}

        //make 1 device for every company
        List<Device> deviceList=new LinkedList<>();
        for(int i=0;i<companyList.size();i++){
            Device d=new Device("devOf"+Integer.toString(i),100,20,i,defaultAttributes);
            companyList.get(i).usedSlots=1;
            d.id=i*1000;
            d.cost=DeviceValidator.getOverallCost(d);
            deviceList.add(d);
        }

        //starting params for the simulator
        Simulator simulator=Simulator.getInstance(deviceList,companyList);

        for (int i = 0; i < numOfTurns; i++) {
            int beforeSimulation=deviceList.size();
            //simulate
            Wrapped_DeviceAndCompanyList results=simulator.simulate();
            assertEquals(beforeSimulation,deviceList.size());

            //update the database
            results.UpdateCompanies.addAll(results.companies);
            updateTheDatabase(deviceList,companyList,results);
            assertEquals(beforeSimulation,deviceList.size());
            //check simulation results
            int[] numOfDev =new int[companyList.size()];
            for (Device d :deviceList) {
                numOfDev[d.ownerCompanyId]++;
                assertTrue("Device "+d.name+" failed at turn "+i+" made by "+d.ownerCompanyId,d.profit>0);
                assertEquals(d.cost, DeviceValidator.getOverallCost(d));
            }

            StringBuilder stringBuilder = new StringBuilder("name\tusedSlots\treally used\n");
            for (Company c :companyList) {
                stringBuilder.append("\n").append(c.name).append("\t").append(c.usedSlots).append("\t").append(numOfDev[c.companyId]);
            }

            for (Company c :companyList) {
                assertTrue(c.name+" failed at turn "+i+" With money: "+c.money+" With log:\n"+c.logs
                        ,c.money >= 0);
                assertTrue(c.name+" failed at turn "+i,c.marketing >= 0);
                assertTrue(c.name+" failed at turn "+i,c.usedSlots<=c.maxSlots);

                assertEquals(c.name+" failed at turn "+i+"\n"+stringBuilder.toString(),c.usedSlots,numOfDev[c.companyId]);
            }


            int beforeAssistants=deviceList.size();
            //assistants act
            Wrapped_DeviceAndCompanyList resultOfAssistants=assistantManager.trigger(results.companies,results.devices);
            // update the database
            assertEquals("Failed in turn: "+i,beforeAssistants,resultOfAssistants.devices.size());
            int afterAssistantsWillBe=resultOfAssistants.insert.size()+resultOfAssistants.devices.size()-resultOfAssistants.delete.size();
            updateTheDatabase(deviceList,companyList,resultOfAssistants);
            assertEquals("Failed in turn: "+i,afterAssistantsWillBe,deviceList.size());

            //check assistants

            //check general things
        }

        //results
        companyList.sort((o1, o2) -> o2.getMarketValue()-o1.getMarketValue());
        System.out.println("\n-----------------");
        System.out.println("Results of the test after "+numOfTurns+" turns:");
        for(Company c: companyList){
            System.out.println(c.name+"("+AssistantManager.getAssistantName(c.assistantType)+")"+
                    "\tvalue: "+c.getMarketValue()+
                    "\tlast profit: "+c.lastProfit+
                    "\tmarketing: "+c.marketing+
                    "\tslots: "+c.usedSlots
            );
        }

        //return companyList.indexOf(variant)+1;
        return variant.getMarketValue();
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