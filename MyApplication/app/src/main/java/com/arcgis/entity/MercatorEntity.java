package com.arcgis.entity;

/**
 * Created by mars on 2015/1/30.
 */
public class MercatorEntity {
    private double x;
    private double y;

    public MercatorEntity(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public MercatorEntity() {

    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
