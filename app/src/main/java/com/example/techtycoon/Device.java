package com.example.techtycoon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
    public enum DeviceAttribute {NAME,DEVICE_ID,OWNER_ID,PERFORMANCE_OVERALL,SCORE_STORAGE,SCORE_BODY,
        PRICE, OVERALL_PROFIT,SOLD_PIECES, PROFIT_PER_ITEM, HISTORY_SOLD_PIECES,
        STORAGE_RAM,STORAGE_MEMORY,
        BODY_DESIGN,BODY_MATERIAL,BODY_COLOR,BODY_IP,BODY_BEZEL,
        DISPLAY_RESOLUTION,DISPLAY_BRIGHTNESS,DISPLAY_REFRESH_RATE,DISPLAY_TECHNOLOGY
    };

    public enum DeviceBudget {STORAGE,BODY,DISPLAY};

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

    //Display
    @ColumnInfo
    public int resolution;

    @ColumnInfo
    public int brightness;

    @ColumnInfo
    public int refreshRate;

    @ColumnInfo
    public int displayTechnology;

    //todo introduce soft attributes. (attributes where not necessarily the bigger number is better)
    //todo create a safely usable device class

    /*
    @ColumnInfo
    public int displaySize;//3inch +

    //todo get density
    public int getPixelDensity(){return 0;}
*/





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

    public static Device getMinimalDevice(String name,int profit,int ownerId){
        int[] attributes=new int[ATTRIBUTES_WITH_NAME.size()];
        for(int i=0;i<attributes.length;i++){
            attributes[i]=1;
        }
        return new Device(name,profit,DeviceValidator.getOverallCost(attributes),ownerId,attributes);
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
    public int getOverallProfit(){ return soldPieces*profit; }

    public int getFieldByAttribute(DeviceAttribute attribute){
        int i=-1;
        switch (attribute){
            case OWNER_ID:i=this.ownerCompanyId;break;
            case DEVICE_ID:i=this.id;break;
            case PERFORMANCE_OVERALL:i=this.getScore_OverallPerformance();break;
            case SCORE_STORAGE:i=this.getScore(DeviceBudget.STORAGE);break;
            case SCORE_BODY:i=this.getScore(DeviceBudget.BODY);break;
            case PRICE:i=this.getPrice();break;
            case OVERALL_PROFIT:i=this.getOverallProfit();break;
            case SOLD_PIECES:i=this.soldPieces;break;
            case HISTORY_SOLD_PIECES:i=this.history_SoldPieces;break;
            case PROFIT_PER_ITEM:i=this.profit;break;
            case STORAGE_RAM:i=this.ram; break;
            case STORAGE_MEMORY:i=this.memory; break;
            case BODY_DESIGN:i=this.design; break;
            case BODY_MATERIAL:i=this.material; break;
            case BODY_COLOR: i=this.color;break;
            case BODY_IP: i=this.ip;break;
            case BODY_BEZEL: i=this.bezel;break;
            case DISPLAY_RESOLUTION:i=this.resolution; break;
            case DISPLAY_BRIGHTNESS:i=this.brightness; break;
            case DISPLAY_REFRESH_RATE: i=this.refreshRate;break;
            case DISPLAY_TECHNOLOGY: i=this.displayTechnology;break;
        }
        return i;
    }

    public void setFieldByAttribute(DeviceAttribute attr, int value){
        switch (attr){
            case PROFIT_PER_ITEM:this.profit=value;break;
            case STORAGE_RAM:this.ram=value; break;
            case STORAGE_MEMORY:this.memory=value; break;
            case BODY_DESIGN:this.design=value; break;
            case BODY_MATERIAL:this.material=value; break;
            case BODY_COLOR:this.color=value;break;
            case BODY_IP:this.ip=value;break;
            case BODY_BEZEL:this.bezel=value;break;
            case DISPLAY_RESOLUTION:this.resolution=value; break;
            case DISPLAY_BRIGHTNESS:this.brightness=value; break;
            case DISPLAY_REFRESH_RATE: this.refreshRate=value;break;
            case DISPLAY_TECHNOLOGY: this.displayTechnology=value;break;
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

    public int getScore(DeviceBudget budget){
        int sum=0;
        for(DeviceAttribute att:getAllAttribute_InBudget(budget)){
            sum+=getFieldByAttribute(att);
        }
        return sum;
    }
    public int getScore_OverallPerformance(){
        int sum=0;
        for(DeviceBudget b:DeviceBudget.values()){
            sum+=getScore(b);
        }
        return sum;
    }

    public static List<DeviceAttribute> getAllAttribute(){
        List<DeviceAttribute> list=new LinkedList<>();
        for (DeviceBudget b :
                DeviceBudget.values()) {
            list.addAll(getAllAttribute_InBudget(b));
        }
        return list;
    }

    public static List<DeviceAttribute> getAllAttribute_InBudget(DeviceBudget b){
        List<DeviceAttribute> list=new LinkedList<>();
        switch (b){
            case STORAGE:
                list.add(DeviceAttribute.STORAGE_RAM);
                list.add(DeviceAttribute.STORAGE_MEMORY);
                break;
            case BODY:
                list.add(DeviceAttribute.BODY_DESIGN);
                list.add(DeviceAttribute.BODY_MATERIAL);
                list.add(DeviceAttribute.BODY_COLOR);
                list.add(DeviceAttribute.BODY_IP);
                list.add(DeviceAttribute.BODY_BEZEL);
                break;
            case DISPLAY:
                list.add(DeviceAttribute.DISPLAY_RESOLUTION);
                list.add(DeviceAttribute.DISPLAY_BRIGHTNESS);
                list.add(DeviceAttribute.DISPLAY_REFRESH_RATE);
                list.add(DeviceAttribute.DISPLAY_TECHNOLOGY);
                break;
        }
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
            new Pair<>(DeviceAttribute.OVERALL_PROFIT,"Overall profit"),
            new Pair<>(DeviceAttribute.SOLD_PIECES,"Sold pieces"),
            new Pair<>(DeviceAttribute.HISTORY_SOLD_PIECES,"History - sold pieces"),
            new Pair<>(DeviceAttribute.PROFIT_PER_ITEM,"Profit/item"),
            new Pair<>(DeviceAttribute.STORAGE_RAM,"RAM"),
            new Pair<>(DeviceAttribute.STORAGE_MEMORY,"Memory"),
            new Pair<>(DeviceAttribute.BODY_DESIGN,"Design"),
            new Pair<>(DeviceAttribute.BODY_MATERIAL,"Material"),
            new Pair<>(DeviceAttribute.BODY_COLOR,"Color"),
            new Pair<>(DeviceAttribute.BODY_IP,"IP"),
            new Pair<>(DeviceAttribute.BODY_BEZEL,"Bezel"),
            new Pair<>(DeviceAttribute.DISPLAY_RESOLUTION,"Resolution"),
            new Pair<>(DeviceAttribute.DISPLAY_BRIGHTNESS,"Brightness"),
            new Pair<>(DeviceAttribute.DISPLAY_REFRESH_RATE,"Refresh rate"),
            new Pair<>(DeviceAttribute.DISPLAY_TECHNOLOGY,"Display technology")
    );

    public static DeviceAttribute attributeFromString(String s){
        return ATTRIBUTES_WITH_NAME.stream().filter(p-> Objects.equals(p.second, s)).collect(Collectors.toList()).get(0).first;
    }

    public static String attributeToString(DeviceAttribute attribute){
        try {
            return ATTRIBUTES_WITH_NAME.stream().filter(p -> p.first == attribute).collect(Collectors.toList()).get(0).second;
        }catch (IndexOutOfBoundsException e){return "error";}
    }

    public static String getStringFromAttributeLevel(DeviceAttribute attribute,int level){
        StringBuilder stringBuilder=new StringBuilder();
        switch (attribute){
            default:stringBuilder.append(String.format(Locale.getDefault(),"%d", level));break;
            case PRICE://intended overflow
            case PROFIT_PER_ITEM:
            case OVERALL_PROFIT:
                stringBuilder.append(String.format(Locale.getDefault(),"%d$", level));
                break;
            case STORAGE_RAM:
            case STORAGE_MEMORY:
                stringBuilder.append(String.format(Locale.getDefault(),"%dGB", (int) Math.round(Math.pow(2,level)) ));
                break;
        }
        return stringBuilder.toString();
    }

    /*
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){return false;}
        if(!(obj instanceof Device)){return false;}
        return ( ((Device) obj).id==this.id && this.compareAttributes((Device) obj)==0) ;
    }

    @Override
    public int hashCode() {
        return this.id *31+ Arrays.deepHashCode(this.getParams());
    }*/
}
