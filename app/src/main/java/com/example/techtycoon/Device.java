package com.example.techtycoon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.core.util.Pair;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//TODO new attributes
//TODO save the name of the owner company
//todo extended device class with statistic

@Entity
public class Device {
    public enum DeviceAttribute {NAME,DEVICE_ID,OWNER_ID,PERFORMANCE_OVERALL,SCORE_STORAGE,SCORE_BODY,PRICE,INCOME,SOLD_PIECES,PROFIT,
        HISTORY_SOLD_PIECES,
        STORAGE_RAM,STORAGE_MEMORY,BODY_DESIGN,BODY_MATERIAL,BODY_COLOR,BODY_IP,BODY_BEZEL};

    public static final int NUMBER_OF_BUDGETS=2; //rammemory,body
    public static final int[] CHILDREN_OF_BUDGETS={2,5}; //rammemory,body
    public final static int NUMBER_OF_ATTRIBUTES = getAllAttribute().size();

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profit")
    public int profit;

    @ColumnInfo(name = "cost")
    public int cost;//todo shouldnt be updated manually

    @ColumnInfo(name="ownerCompanyId")
    public int ownerCompanyId;

    @ColumnInfo(name="soldPieces")
    private int soldPieces;

    @ColumnInfo
    public int history_SoldPieces;

    @ColumnInfo(name="trend")
    private int trend;

    //attributes

    //memory
    //Db store ram n memory like their level 1,2,3,4,5 and not 32,64
    @ColumnInfo(name="ram")
    public int ram;

    @ColumnInfo(name="memory")
    public int memory;

    //Body
    @ColumnInfo
    public int design;

    @ColumnInfo
    public int material;

    @ColumnInfo
    public int color;

    @ColumnInfo
    public int ip;

    @ColumnInfo
    public int bezel;

    /*

    //Display
    @ColumnInfo
    public int displaySize;

    @ColumnInfo
    public int resolution;

    //todo get density

    @ColumnInfo
    public int displayTechnology;

    @ColumnInfo
    public int brightness;

    @ColumnInfo
    public boolean refreshRate;*/


    public Device(String name,int profit,int cost,int ownerCompanyId,int[] attributes) {
        if(attributes==null){attributes=new int[Device.NUMBER_OF_ATTRIBUTES];}
        this.name = name;
        this.profit = profit;
        this.cost=cost;
        this.ownerCompanyId=ownerCompanyId;
        int i=0;
        for(DeviceAttribute a : getAllAttribute()){
            setFieldByAttribute(a,attributes[i++]);
        }
        this.soldPieces=0;
        this.history_SoldPieces=0;
    }

    public Device(String name,int profit,int cost,int ownerCompanyId) {
        this.name = name;
        this.profit = profit;
        this.cost=cost;
        this.ownerCompanyId=ownerCompanyId;
        this.soldPieces=0;
        this.history_SoldPieces=0;
    }

    public Device(Device d) {
        this.id=0;
        this.name = d.name;
        this.profit = d.profit;
        this.cost=d.cost;
        this.ownerCompanyId=d.ownerCompanyId;
        this.soldPieces=0;
        this.history_SoldPieces=0;
        this.trend=d.trend;

        for(DeviceAttribute a : getAllAttribute()){
            setFieldByAttribute(a,d.getFieldByAttribute(a));
        }
    }

    public void setBodyParams(int design,int materials,int colors,int ip,int bezels){
        this.design=design;
        this.material=materials;
        this.color=colors;
        this.ip=ip;
        this.bezel=bezels;
    }
    public void setBodyParams(int[] bodyParams){
        this.setBodyParams(bodyParams[0],bodyParams[1],bodyParams[2],bodyParams[3],bodyParams[4]);
    }

    public int[][] getParams(){
        return new int[][]{
                {this.ram,
                    this.memory,},

                {this.design,
                    this.material,
                    this.color,
                    this.ip,
                    this.bezel}
        };
    }

    //field setters getters
    public int getSoldPieces() {
        return soldPieces;
    }
    public void setSoldPieces(int soldPieces) {
        trend=soldPieces-this.soldPieces;
        this.soldPieces = soldPieces;
        history_SoldPieces+=soldPieces;
    }
    public int getTrend() {
        return trend;
    }
    public void setTrend(int trend) {
        this.trend=trend;
    }
    public int getPrice(){return cost+profit;}
    public int getIncome(){ return soldPieces*profit; }

    //converters
    public static int[] mtxToArray(int[][] mtx){
        int length=0;
        for (int i=0;i<NUMBER_OF_BUDGETS;i++){
            length+=CHILDREN_OF_BUDGETS[i];
        }
        int[] arr =new int[length];
        int placeInArr=0;

        for (int i=0;i<NUMBER_OF_BUDGETS;i++){
            int j=0;
            while (j<CHILDREN_OF_BUDGETS[i]){
                arr[placeInArr++]=mtx[i][j];
                j++;
            }
        }
        return arr;
    }
    static int[][] intArrayToMtx(int[] arr){
        int maxLength=0;
        for (int i=0;i<NUMBER_OF_BUDGETS;i++){
            if (maxLength<CHILDREN_OF_BUDGETS[i]){maxLength=CHILDREN_OF_BUDGETS[i];}
        }
        int[][] mtx=new int[NUMBER_OF_BUDGETS][maxLength];

        int placeInArr=0;
        for (int i=0;i<NUMBER_OF_BUDGETS;i++){
            int j=0;
            while (j<CHILDREN_OF_BUDGETS[i]){
                mtx[i][j]=arr[placeInArr];
                placeInArr++;
                j++;
            }
        }

        return mtx;
    }


    public int getFieldByAttribute(DeviceAttribute attribute){
        int i=-1;
        switch (attribute){
            case OWNER_ID:i=this.ownerCompanyId;break;
            case DEVICE_ID:i=this.id;break;
            case PERFORMANCE_OVERALL:i=this.getScore_OverallPerformance();break;
            case SCORE_STORAGE:i=this.getScore_Storage();break;
            case SCORE_BODY:i=this.getScore_Body();break;
            case PRICE:i=this.getPrice();break;
            case INCOME:i=this.getIncome();break;
            case SOLD_PIECES:i=this.soldPieces;break;
            case HISTORY_SOLD_PIECES:i=this.history_SoldPieces;break;
            case PROFIT:i=this.profit;break;
            case STORAGE_RAM:i=this.ram; break;
            case STORAGE_MEMORY:i=this.memory; break;
            case BODY_DESIGN:i=this.design; break;
            case BODY_MATERIAL:i=this.material; break;
            case BODY_COLOR: i=this.color;break;
            case BODY_IP: i=this.ip;break;
            case BODY_BEZEL: i=this.bezel;break;
        }
        return i;
    }

    public void setFieldByAttribute(DeviceAttribute attr, int value){
        switch (attr){
            case PROFIT:this.profit=value;break;
            case STORAGE_RAM:this.ram=value; break;
            case STORAGE_MEMORY:this.memory=value; break;
            case BODY_DESIGN:this.design=value; break;
            case BODY_MATERIAL:this.material=value; break;
            case BODY_COLOR:this.color=value;break;
            case BODY_IP:this.ip=value;break;
            case BODY_BEZEL:this.bezel=value;break;
        }
    }

    /*
     * returns the difference between the attributes of 2 device
     */
    public int compareAttributes(Device d){
        int diff=0;
        for (int i=0;i<NUMBER_OF_ATTRIBUTES;i++){
            if(this.getFieldByAttribute(DeviceAttribute.values()[i])!=d.getFieldByAttribute(DeviceAttribute.values()[i])){diff++;}
        }
        return diff;
    }

    public final int getScore_Storage(){
        return ram+memory;
    }
    public final int getScore_Body(){
        return design+material+color+ip+bezel;
    }
    public final int getScore_OverallPerformance(){
        return getScore_Body()+getScore_Storage();
    }

    public static List<DeviceAttribute> getBodyAttributes(){
        List<DeviceAttribute> list=new LinkedList<>();
        list.add(DeviceAttribute.BODY_DESIGN);
        list.add(DeviceAttribute.BODY_MATERIAL);
        list.add(DeviceAttribute.BODY_COLOR);
        list.add(DeviceAttribute.BODY_IP);
        list.add(DeviceAttribute.BODY_BEZEL);
        return list;
    }

    public static List<DeviceAttribute> getStorageAttributes(){
        List<DeviceAttribute> list=new LinkedList<>();
        list.add(DeviceAttribute.STORAGE_RAM);
        list.add(DeviceAttribute.STORAGE_MEMORY);
        return list;
    }

    public static List<DeviceAttribute> getAllAttribute(){
        List<DeviceAttribute> list=new LinkedList<>();
        list.addAll(getStorageAttributes());
        list.addAll(getBodyAttributes());
        return list;
    }

    final static private List<Pair<DeviceAttribute,String>> ATTRIBUTES_WITH_NAME=Arrays.asList(
            new Pair<>(DeviceAttribute.NAME,"Name"),
            new Pair<>(DeviceAttribute.OWNER_ID,"Owner ID"),
            new Pair<>(DeviceAttribute.DEVICE_ID,"Device ID"),
            new Pair<>(DeviceAttribute.PERFORMANCE_OVERALL,"Performance"),
            new Pair<>(DeviceAttribute.SCORE_STORAGE,"Storage"),
            new Pair<>(DeviceAttribute.SCORE_BODY,"Body"),
            new Pair<>(DeviceAttribute.PRICE,"Price"),
            new Pair<>(DeviceAttribute.INCOME,"Income"),
            new Pair<>(DeviceAttribute.SOLD_PIECES,"Sold pieces"),
            new Pair<>(DeviceAttribute.HISTORY_SOLD_PIECES,"History - sold pieces"),
            new Pair<>(DeviceAttribute.PROFIT,"Profit"),
            new Pair<>(DeviceAttribute.STORAGE_RAM,"RAM"),
            new Pair<>(DeviceAttribute.STORAGE_MEMORY,"Memory"),
            new Pair<>(DeviceAttribute.BODY_DESIGN,"Design"),
            new Pair<>(DeviceAttribute.BODY_MATERIAL,"Material"),
            new Pair<>(DeviceAttribute.BODY_COLOR,"Color"),
            new Pair<>(DeviceAttribute.BODY_IP,"IP"),
            new Pair<>(DeviceAttribute.BODY_BEZEL,"Bezel")
    );

    public static DeviceAttribute attributeFromString(String s){
        return ATTRIBUTES_WITH_NAME.stream().filter(p-> Objects.equals(p.second, s)).collect(Collectors.toList()).get(0).first;
    }

    public static String attributeToString(DeviceAttribute attribute){
        try {
            return ATTRIBUTES_WITH_NAME.stream().filter(p -> p.first == attribute).collect(Collectors.toList()).get(0).second;
        }catch (IndexOutOfBoundsException e){return "error";}
    }
}
