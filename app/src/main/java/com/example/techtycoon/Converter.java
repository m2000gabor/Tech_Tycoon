package com.example.techtycoon;

public class Converter {

    public static String intArrayToString(int[] array) {
        String r="";
        for (int i=0;i<array.length;i++) {
            r= r + Integer.toString(array[i]) + ';';
        }
        return r;
    }

    public static int[] stringToIntArray(String stored) {
        String[] strings=stored.split(";");
        int[] ints=new int[strings.length];
        for (int i=0;i<strings.length;i++) {
            ints[i]=Integer.parseInt(strings[i]);
        }
        return ints;
    }

    /*
    //converters
    public static int[] mtxToArray(int[][] mtx){
        int length=0;
        for (int i=0;i<DeviceBudget.values().length;i++){
            length+=CHILDREN_OF_BUDGETS[i];
        }
        int[] arr =new int[length];
        int placeInArr=0;

        for (int i=0;i<DeviceBudget.values().length;i++){
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
        for (int i=0;i<DeviceBudget.values().length;i++){
            if (maxLength<CHILDREN_OF_BUDGETS[i]){maxLength=CHILDREN_OF_BUDGETS[i];}
        }
        int[][] mtx=new int[DeviceBudget.values().length][maxLength];

        int placeInArr=0;
        for (int i=0;i<DeviceBudget.values().length;i++){
            int j=0;
            while (j<CHILDREN_OF_BUDGETS[i]){
                mtx[i][j]=arr[placeInArr];
                placeInArr++;
                j++;
            }
        }

        return mtx;
    }
*/

}
