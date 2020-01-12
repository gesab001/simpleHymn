package com.giovannisaberon.simplehymn;

public class HymnData {
    private int number;
    private String title;
//    private String[] verses;

    public HymnData(int number, String title){
        this.number= number;
        this.title = title;
    }

    public int getNumber(){
        return this.number;
    }

    public String getTitle(){
        return this.title;
    }

}

