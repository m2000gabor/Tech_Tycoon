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


}
