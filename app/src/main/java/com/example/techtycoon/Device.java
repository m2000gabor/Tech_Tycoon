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

    @ColumnInfo(name="ram")
    public int ram;

    @ColumnInfo(name="memory")
    public int memory;

    @ColumnInfo(name="soldPieces")
    public int soldPieces;

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

    public Device(String name,int profit,int cost,int ownerCompanyId,int ram, int memory) {
        this.name = name;
        this.profit = profit;
        this.cost=cost;
        this.ownerCompanyId=ownerCompanyId;
        this.ram=ram;
        this.memory=memory;
        this.soldPieces=0;
    }

    public void setBodyParams(int design,int materials,int colors,int ip,int bezels){
        this.design=design;
        this.material=materials;
        this.color=colors;
        this.ip=ip;
        this.bezel=bezels;
    }
    int[][] getParams(){
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

    String getDeviceName(){return this.name;}
    //public int  getDeviceId(){return this.id;}
    int getPrice(){return cost+profit;}

    static int[] mtxToArray(int[][] mtx){
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
}
