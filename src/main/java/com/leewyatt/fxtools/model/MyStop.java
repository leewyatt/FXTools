package com.leewyatt.fxtools.model;

/**
 * @author LeeWyatt
 */
public class MyStop {
    private double offset;
    private MyColor myColor;

    public MyStop() {
    }

    public MyStop(double offset, MyColor myColor) {
        this.offset = offset;
        this.myColor = myColor;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public MyColor getMyColor() {
        return myColor;
    }

    public void setMyColor(MyColor myColor) {
        this.myColor = myColor;
    }
}
