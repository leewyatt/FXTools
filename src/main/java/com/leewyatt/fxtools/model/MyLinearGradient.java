package com.leewyatt.fxtools.model;

import java.util.List;

/**
 * @author LeeWyatt
 */
public class MyLinearGradient {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private List<MyStop> myStops;


    public MyLinearGradient(double startX, double startY, double endX, double endY, List<MyStop> myStops) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.myStops = myStops;
    }

    public MyLinearGradient() {
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public List<MyStop> getStops() {
        return myStops;
    }

    public void setStops(List<MyStop> myStops) {
        this.myStops = myStops;
    }

}
