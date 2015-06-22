package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 卫片执法
 */
@DatabaseTable(tableName = "TB_XCSB")
public class XCSBEntity implements Serializable {

    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "X",useGetSet = true)
    private String X;
    @DatabaseField(columnName = "Y",useGetSet = true)
    private String Y;
    @DatabaseField(columnName = "TIME",useGetSet = true)
    private String time;
    @DatabaseField(columnName = "ACCU",useGetSet = true)
    private String accu;
    @DatabaseField(columnName = "YHM",useGetSet = true)
    private String yhm;


    public XCSBEntity() {
    }

    @Override
    public String toString() {
        return "XCSBEntity{" +
                "id=" + id +
                ", X='" + X + '\'' +
                ", Y='" + Y + '\'' +
                ", time='" + time + '\'' +
                ", accu='" + accu + '\'' +
                ", yhm='" + yhm + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccu() {
        return accu;
    }

    public void setAccu(String accu) {
        this.accu = accu;
    }

    public String getYhm() {
        return yhm;
    }

    public void setYhm(String yhm) {
        this.yhm = yhm;
    }
}
