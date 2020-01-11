package com.giovannisaberon.simplehymn;

public class HymnData {
    private int number;
    private String title;
    private String[] verses;

    public HymnData(int number, String title, String[] verses){
        this.number= number;
        this.title = title;
        this.verses = verses;
    }

    public int getNumber(){
        return this.number;
    }

    public String getTitle(){
        return this.title;
    }

    public String[] getVerses(){
        return this.verses;
    }

}

