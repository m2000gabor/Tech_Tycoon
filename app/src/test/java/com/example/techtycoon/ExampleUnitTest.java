package com.example.techtycoon;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

//todo implement a lot of tests
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

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
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            defaultAttributes[i]=1;
        }

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
}