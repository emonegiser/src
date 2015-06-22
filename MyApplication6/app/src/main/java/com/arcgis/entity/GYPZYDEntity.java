package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 供应用地
 */
@DatabaseTable(tableName = "TB_GYYD")
public class GYPZYDEntity implements Serializable {
    /**
     * {"OBJECTID":1654,"GYYDBH":"1267662","PZWH":null,"FFID":19,"GYMJ":4238464.66165644,"GDPFMC":"1",
     * "GDPFWH":"2","GDPFSJ":"\/Date(1422720000000)\/","TDYT":"批发零售用地","RJL":0.00000000,
     * "JZMD":0.00000000,"LHBL":0.00000000,"GDFS":"划拨","YDDWMC":"2","YDXMMC":"2","CJJK":0.00000000,
     * "HTBH":"7714375","HTYDJDSJ":"\/Date(1422720000000)\/","HTYDDGSJ":"\/Date(1422720000000)\/",
     * "HTYDJGSJ":"\/Date(1422720000000)\/","SJJDSJ":"\/Date(1422720000000)\/","SQKGSJ":"\/Date(1422720000000)\/",
     * "SJKGSJ":"\/Date(1422720000000)\/","SJJGSJ":"\/Date(1422720000000)\/","JGTDHYSJ":"\/Date(1422720000000)\/",
     * "JGTDHYQK":"2","X":105.18571907,"Y":27.15129870,"XZ":"千溪乡","CUN":"千朗村","PID":null,"BID":null,
     * "BMJ":null,"PMJ":null,"DTTF":"G48 G 021052,G48 G 021051","Shape":1654}
     */

    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "OBJECTID",useGetSet = true)
    private String OBJECTID;
    @DatabaseField(columnName = "GYYDBH",useGetSet = true)
    private String GYYDBH;
    @DatabaseField(columnName = "PZWH",useGetSet = true)
    private String PZWH;
    @DatabaseField(columnName = "FFID",useGetSet = true)
    private String FFID;
    @DatabaseField(columnName = "GYMJ",useGetSet = true)
    private String GYMJ;
    @DatabaseField(columnName = "GDPFMC",useGetSet = true)
    private String GDPFMC;
    @DatabaseField(columnName = "GDPFWH",useGetSet = true)
    private String GDPFWH;
    @DatabaseField(columnName = "GDPFSJ",useGetSet = true)
    private String GDPFSJ;
    @DatabaseField(columnName = "TDYT",useGetSet = true)
    private String TDYT;
    @DatabaseField(columnName = "RJL",useGetSet = true)
    private String RJL;
    @DatabaseField(columnName = "JZMD",useGetSet = true)
    private String JZMD;
    @DatabaseField(columnName = "LHBL",useGetSet = true)
    private String LHBL;
    @DatabaseField(columnName = "GDFS",useGetSet = true)
    private String GDFS;
    @DatabaseField(columnName = "YDDWMC",useGetSet = true)
    private String YDDWMC;
    @DatabaseField(columnName = "YDXMMC",useGetSet = true)
    private String YDXMMC;
    @DatabaseField(columnName = "CJJK",useGetSet = true)
    private String CJJK;
    @DatabaseField(columnName = "HTBH",useGetSet = true)
    private String HTBH;
    @DatabaseField(columnName = "HTYDJDSJ",useGetSet = true)
    private String HTYDJDSJ;
    @DatabaseField(columnName = "HTYDDGSJ",useGetSet = true)
    private String HTYDDGSJ;
    @DatabaseField(columnName = "HTYDJGSJ",useGetSet = true)
    private String HTYDJGSJ;
    @DatabaseField(columnName = "SJJDSJ",useGetSet = true)
    private String SJJDSJ;
    @DatabaseField(columnName = "SQKGSJ",useGetSet = true)
    private String SQKGSJ;
    @DatabaseField(columnName = "SJKGSJ",useGetSet = true)
    private String SJKGSJ;
    @DatabaseField(columnName = "SJJGSJ",useGetSet = true)
    private String SJJGSJ;
    @DatabaseField(columnName = "JGTDHYSJ",useGetSet = true)
    private String JGTDHYSJ;
    @DatabaseField(columnName = "JGTDHYQK",useGetSet = true)
    private String JGTDHYQK;
    @DatabaseField(columnName = "X",useGetSet = true)
    private String X;
    @DatabaseField(columnName = "Y",useGetSet = true)
    private String Y;
    @DatabaseField(columnName = "XZ",useGetSet = true)
    private String XZ;
    @DatabaseField(columnName = "CUN",useGetSet = true)
    private String CUN;
    @DatabaseField(columnName = "PID",useGetSet = true)
    private String PID;
    @DatabaseField(columnName = "BID",useGetSet = true)
    private String BID;
    @DatabaseField(columnName = "BMJ",useGetSet = true)
    private String BMJ;
    @DatabaseField(columnName = "PMJ",useGetSet = true)
    private String PMJ;
    @DatabaseField(columnName = "DTTF",useGetSet = true)
    private String DTTF;
    @DatabaseField(columnName = "Shape",useGetSet = true)
    private String Shape;
    @DatabaseField(columnName = "COORDS",useGetSet = true)
    private String coords;

    public GYPZYDEntity() {
    }

    @Override
    public String toString() {
        return "GYPZYDEntity{" +
                "id=" + id +
                ", OBJECTID='" + OBJECTID + '\'' +
                ", GYYDBH='" + GYYDBH + '\'' +
                ", PZWH='" + PZWH + '\'' +
                ", FFID='" + FFID + '\'' +
                ", GYMJ='" + GYMJ + '\'' +
                ", GDPFMC='" + GDPFMC + '\'' +
                ", GDPFWH='" + GDPFWH + '\'' +
                ", GDPFSJ='" + GDPFSJ + '\'' +
                ", TDYT='" + TDYT + '\'' +
                ", RJL='" + RJL + '\'' +
                ", JZMD='" + JZMD + '\'' +
                ", LHBL='" + LHBL + '\'' +
                ", GDFS='" + GDFS + '\'' +
                ", YDDWMC='" + YDDWMC + '\'' +
                ", YDXMMC='" + YDXMMC + '\'' +
                ", CJJK='" + CJJK + '\'' +
                ", HTBH='" + HTBH + '\'' +
                ", HTYDJDSJ='" + HTYDJDSJ + '\'' +
                ", HTYDDGSJ='" + HTYDDGSJ + '\'' +
                ", HTYDJGSJ='" + HTYDJGSJ + '\'' +
                ", SJJDSJ='" + SJJDSJ + '\'' +
                ", SQKGSJ='" + SQKGSJ + '\'' +
                ", SJKGSJ='" + SJKGSJ + '\'' +
                ", SJJGSJ='" + SJJGSJ + '\'' +
                ", JGTDHYSJ='" + JGTDHYSJ + '\'' +
                ", JGTDHYQK='" + JGTDHYQK + '\'' +
                ", X='" + X + '\'' +
                ", Y='" + Y + '\'' +
                ", XZ='" + XZ + '\'' +
                ", CUN='" + CUN + '\'' +
                ", PID='" + PID + '\'' +
                ", BID='" + BID + '\'' +
                ", BMJ='" + BMJ + '\'' +
                ", PMJ='" + PMJ + '\'' +
                ", DTTF='" + DTTF + '\'' +
                ", Shape='" + Shape + '\'' +
                ", coords='" + coords + '\'' +
                '}';
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOBJECTID() {
        return OBJECTID;
    }

    public void setOBJECTID(String OBJECTID) {
        this.OBJECTID = OBJECTID;
    }

    public String getFFID() {
        return FFID;
    }

    public void setFFID(String FFID) {
        this.FFID = FFID;
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

    public String getCUN() {
        return CUN;
    }

    public void setCUN(String CUN) {
        this.CUN = CUN;
    }

    public String getXZ() {
        return XZ;
    }

    public void setXZ(String XZ) {
        this.XZ = XZ;
    }

    public String getDTTF() {
        return DTTF;
    }

    public void setDTTF(String DTTF) {
        this.DTTF = DTTF;
    }

    public String getShape() {
        return Shape;
    }

    public void setShape(String shape) {
        Shape = shape;
    }

    public String getGYYDBH() {
        return GYYDBH;
    }

    public void setGYYDBH(String GYYDBH) {
        this.GYYDBH = GYYDBH;
    }

    public String getPZWH() {
        return PZWH;
    }

    public void setPZWH(String PZWH) {
        this.PZWH = PZWH;
    }

    public String getGYMJ() {
        return GYMJ;
    }

    public void setGYMJ(String GYMJ) {
        this.GYMJ = GYMJ;
    }

    public String getGDPFMC() {
        return GDPFMC;
    }

    public void setGDPFMC(String GDPFMC) {
        this.GDPFMC = GDPFMC;
    }

    public String getGDPFWH() {
        return GDPFWH;
    }

    public void setGDPFWH(String GDPFWH) {
        this.GDPFWH = GDPFWH;
    }

    public String getGDPFSJ() {
        return GDPFSJ;
    }

    public void setGDPFSJ(String GDPFSJ) {
        this.GDPFSJ = GDPFSJ;
    }

    public String getTDYT() {
        return TDYT;
    }

    public void setTDYT(String TDYT) {
        this.TDYT = TDYT;
    }

    public String getRJL() {
        return RJL;
    }

    public void setRJL(String RJL) {
        this.RJL = RJL;
    }

    public String getJZMD() {
        return JZMD;
    }

    public void setJZMD(String JZMD) {
        this.JZMD = JZMD;
    }

    public String getLHBL() {
        return LHBL;
    }

    public void setLHBL(String LHBL) {
        this.LHBL = LHBL;
    }

    public String getGDFS() {
        return GDFS;
    }

    public void setGDFS(String GDFS) {
        this.GDFS = GDFS;
    }

    public String getYDDWMC() {
        return YDDWMC;
    }

    public void setYDDWMC(String YDDWMC) {
        this.YDDWMC = YDDWMC;
    }

    public String getYDXMMC() {
        return YDXMMC;
    }

    public void setYDXMMC(String YDXMMC) {
        this.YDXMMC = YDXMMC;
    }

    public String getCJJK() {
        return CJJK;
    }

    public void setCJJK(String CJJK) {
        this.CJJK = CJJK;
    }

    public String getHTBH() {
        return HTBH;
    }

    public void setHTBH(String HTBH) {
        this.HTBH = HTBH;
    }

    public String getHTYDJDSJ() {
        return HTYDJDSJ;
    }

    public void setHTYDJDSJ(String HTYDJDSJ) {
        this.HTYDJDSJ = HTYDJDSJ;
    }

    public String getHTYDDGSJ() {
        return HTYDDGSJ;
    }

    public void setHTYDDGSJ(String HTYDDGSJ) {
        this.HTYDDGSJ = HTYDDGSJ;
    }

    public String getHTYDJGSJ() {
        return HTYDJGSJ;
    }

    public void setHTYDJGSJ(String HTYDJGSJ) {
        this.HTYDJGSJ = HTYDJGSJ;
    }

    public String getSJJDSJ() {
        return SJJDSJ;
    }

    public void setSJJDSJ(String SJJDSJ) {
        this.SJJDSJ = SJJDSJ;
    }

    public String getSQKGSJ() {
        return SQKGSJ;
    }

    public void setSQKGSJ(String SQKGSJ) {
        this.SQKGSJ = SQKGSJ;
    }

    public String getSJKGSJ() {
        return SJKGSJ;
    }

    public void setSJKGSJ(String SJKGSJ) {
        this.SJKGSJ = SJKGSJ;
    }

    public String getSJJGSJ() {
        return SJJGSJ;
    }

    public void setSJJGSJ(String SJJGSJ) {
        this.SJJGSJ = SJJGSJ;
    }

    public String getJGTDHYSJ() {
        return JGTDHYSJ;
    }

    public void setJGTDHYSJ(String JGTDHYSJ) {
        this.JGTDHYSJ = JGTDHYSJ;
    }

    public String getJGTDHYQK() {
        return JGTDHYQK;
    }

    public void setJGTDHYQK(String JGTDHYQK) {
        this.JGTDHYQK = JGTDHYQK;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getBID() {
        return BID;
    }

    public void setBID(String BID) {
        this.BID = BID;
    }

    public String getBMJ() {
        return BMJ;
    }

    public void setBMJ(String BMJ) {
        this.BMJ = BMJ;
    }

    public String getPMJ() {
        return PMJ;
    }

    public void setPMJ(String PMJ) {
        this.PMJ = PMJ;
    }
}
