package com.aspl.steel.pojo;

/**
 *  Created by Arnab Kar on 29/2/16.
 */
public class ItemPojo {
    int id;
    String title="";
    ItemPojo(int id,String title){
        this.id=id;
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public int getId(){
        return id;
    }
}
