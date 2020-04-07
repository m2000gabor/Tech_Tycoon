package com.example.techtycoon.Assistant;

class AppleBot2 extends AppleBot {
    final static int goodPointImportance=5;
    final static int newSlotImportance=12;
    final static int badThingImportance=7;
    final static int[] marketingImportance={20,9,6,3,2};
    AppleBot2(){
        super(goodPointImportance,newSlotImportance,badThingImportance,marketingImportance);
    }
}
