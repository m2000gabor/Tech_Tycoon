package com.example.techtycoon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//TODO new attributes
//TODO save the name of the owner company

@Entity
public class Device {
    public static final int NUMBER_OF_BUDGETS=2; //rammemory,body
    public static final int[] CHILDREN_OF_BUDGETS={2,5}; //rammemory,body
    public final static int NUMBER_OF_ATTRIBUTES =7;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profit")
    public int profit;

    @ColumnInfo(name = "cost")
    public int cost;

    @ColumnInfo(name="ownerCompanyId")
    public int ownerCompanyId;

    @ColumnInfo(name="soldPieces")
    public int soldPieces;


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
        for(int i=0;i<Device.NUMBER_OF_ATTRIBUTES;i++){
            setFieldByNum(i,attributes[i]);
        }
        this.soldPieces=0;
    }
    public Device(String name,int profit,int cost,int ownerCompanyId) {
        this.name = name;
        this.profit = profit;
        this.cost=cost;
        this.ownerCompanyId=ownerCompanyId;
        this.soldPieces=0;
    }

    public Device(Device d) {
        this.id=-1;
        this.name = d.name;
        this.profit = d.profit;
        this.cost=d.cost;
        this.ownerCompanyId=d.ownerCompanyId;
        this.soldPieces=0;

        for(int i=0;i<NUMBER_OF_ATTRIBUTES;i++){
            setFieldByNum(i,d.getFieldByNum(i));
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

    public int getPrice(){return cost+profit;}
    public int getOverallIncome(){ return soldPieces*profit; }

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

    //-1 returns the profit
    public int getFieldByNum(int attrID){
        int i=-1;
        switch (attrID){
            case -1:i=this.profit;break;
            case 0:i=this.ram; break;
            case 1:i=this.memory; break;
            case 2:i=this.design; break;
            case 3:i=this.material; break;
            case 4: i=this.color;break;
            case 5: i=this.ip;break;
            case 6: i=this.bezel;break;
        }
        return i;
    }

    public void setFieldByNum(int attrID,int value){
        switch (attrID){
            case -1:this.profit=value;break;
            case 0:this.ram=value; break;
            case 1:this.memory=value; break;
            case 2:this.design=value; break;
            case 3:this.material=value; break;
            case 4:this.color=value;break;
            case 5:this.ip=value;break;
            case 6:this.bezel=value;break;
        }
    }

    /*
     * returns the difference between the attributes of 2 device
     */
    public int equalAttributes(Device d){
        int diff=0;
        for (int i=0;i<NUMBER_OF_ATTRIBUTES;i++){
            if(this.getFieldByNum(i)!=d.getFieldByNum(i)){diff++;}
        }
        return diff;
    }

    public final int getScore_Storage(){
        return ram+memory;
    }
    public final int getScore_Body(){
        return design+material+color+ip+bezel;
    }

}
