package com.example.techtycoon.Assistant;

class AppleBot2 extends AppleBot {
    private final static int goodPointImportance=5;
    private final static int newSlotImportance=12;
    private final static int badThingImportance=7;
    private final static int[] marketingImportance={20,9,6,3,2};

    AppleBot2(){
        super(goodPointImportance,newSlotImportance,badThingImportance,marketingImportance);
    }

    @Override
    public String getAssistantName() {
        return "Apple Bot 2";
    }
}
