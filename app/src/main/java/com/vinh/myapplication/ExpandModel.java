package com.vinh.myapplication;

public class ExpandModel {
    public String name;
    public boolean isGroup, hasChildren;
    public int typeID;

    public ExpandModel(String name, boolean isGroup, boolean hasChildren, int typeID) {
        this.name = name;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
        this.typeID = typeID;
    }
}
