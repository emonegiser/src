package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 地质灾害
 */
@DatabaseTable(tableName = "TB_DZZH")
public class DZZHEntity implements Serializable {
    @DatabaseField(generatedId = true,columnName = "idd")
    private int idd;

    @DatabaseField(columnName = "X",useGetSet = true)
    private String X;//x坐标

    @DatabaseField(columnName = "Y",useGetSet = true)
    private String Y; //y坐标

    @DatabaseField(columnName = "Jd",useGetSet = true)
    private String Jd;

    @DatabaseField(columnName = "Wd",useGetSet = true)
    private String Wd;

    @DatabaseField(columnName = "DZPTBH",useGetSet = true)
    private String DZPTBH; //地质灾害编号

    @DatabaseField(columnName = "XQ",useGetSet = true)
    private String XQ;//地质灾害名称

    @DatabaseField(columnName = "XZH",useGetSet = true)
    private String XZH;//地质灾害类型

    @DatabaseField(columnName = "CUN",useGetSet = true)
    private String CUN; //所属乡镇

    @DatabaseField(columnName = "ZU",useGetSet = true)
    private String ZU;//所属村

    @DatabaseField(columnName = "DNAME",useGetSet = true)
    private String DNAME;//详细位置

    @DatabaseField(columnName = "DZTYPE",useGetSet = true)
    private String DZTYPE;//稳定状态

    @DatabaseField(columnName = "GM",useGetSet = true)
    private String GM;//监测人

    @DatabaseField(columnName = "GMDJ",useGetSet = true)
    private String GMDJ;//联系电话

    @DatabaseField(columnName = "WXDX",useGetSet = true)
    private String WXDX;//图片

    @DatabaseField(columnName = "WXHS",useGetSet = true)
    private String WXHS;//视频

    @DatabaseField(columnName = "WXRK",useGetSet = true)
    private String WXRK;//发生时间

    @DatabaseField(columnName = "QZJJSS",useGetSet = true)
    private String QZJJSS;//上报时间

    @DatabaseField(columnName = "XQDJ",useGetSet = true)
    private String XQDJ;//措施建议

    @DatabaseField(columnName="CSFSSJ",useGetSet = true)
    private String CSFSSJ;//普查时间

    @DatabaseField(columnName ="YXYS",useGetSet = true)
    private String YXYS;//户数

    @DatabaseField(columnName ="FZZRNAME",useGetSet = true)
    private String FZZRNAME;//人数

    @DatabaseField(columnName = "FZZRTEL",useGetSet = true)
    private String FZZRTEL;//金额

    @DatabaseField(columnName = "JCZRNAME",useGetSet = true)
    private String JCZRNAME;//地名

    @DatabaseField(columnName = "JCZRTEL",useGetSet = true)
    private String JCZRTEL;//产生原因

    @DatabaseField(columnName = "DJRKYEAR",useGetSet = true)
    private String DJRKYEAR;//地灾规模

    @DatabaseField(columnName = "NCCS",useGetSet = true)
    private String NCCS;

    @DatabaseField(columnName = "BZ",useGetSet = true)
    private String BZ;//规模土石方量

    @DatabaseField(columnName = "picture",useGetSet = true)
    private String picture;//图片

    @DatabaseField(columnName = "cs",useGetSet = true)
    private String cs;//维护次数

    @DatabaseField(columnName = "whtime",useGetSet = true)
    private String whtime;//最近维护时间

    public DZZHEntity(){
        //orm needs
    }

    @Override
    public String toString() {
        return "DZZHEntity{" +
                "idd=" + idd +
                ", X='" + X + '\'' +
                ", Y='" + Y + '\'' +
                ", Jd='" + Jd + '\'' +
                ", Wd='" + Wd + '\'' +
                ", DZPTBH='" + DZPTBH + '\'' +
                ", XQ='" + XQ + '\'' +
                ", XZH='" + XZH + '\'' +
                ", CUN='" + CUN + '\'' +
                ", ZU='" + ZU + '\'' +
                ", DNAME='" + DNAME + '\'' +
                ", DZTYPE='" + DZTYPE + '\'' +
                ", GM='" + GM + '\'' +
                ", GMDJ='" + GMDJ + '\'' +
                ", WXDX='" + WXDX + '\'' +
                ", WXHS='" + WXHS + '\'' +
                ", WXRK='" + WXRK + '\'' +
                ", QZJJSS='" + QZJJSS + '\'' +
                ", XQDJ='" + XQDJ + '\'' +
                ", CSFSSJ='" + CSFSSJ + '\'' +
                ", YXYS='" + YXYS + '\'' +
                ", FZZRNAME='" + FZZRNAME + '\'' +
                ", FZZRTEL='" + FZZRTEL + '\'' +
                ", JCZRNAME='" + JCZRNAME + '\'' +
                ", JCZRTEL='" + JCZRTEL + '\'' +
                ", DJRKYEAR='" + DJRKYEAR + '\'' +
                ", NCCS='" + NCCS + '\'' +
                ", BZ='" + BZ + '\'' +
                ", picture='" + picture + '\'' +
                ", cs='" + cs + '\'' +
                ", whtime='" + whtime + '\'' +
                '}';
    }

    public String getXQ() {
        return XQ;
    }

    public void setXQ(String XQ) {
        this.XQ = XQ;
    }

    public int getIdd() {
        return idd;
    }

    public void setIdd(int idd) {
        this.idd = idd;
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

    public String getJd() {
        return Jd;
    }

    public void setJd(String jd) {
        Jd = jd;
    }

    public String getWd() {
        return Wd;
    }

    public void setWd(String wd) {
        Wd = wd;
    }

    public String getDZPTBH() {
        return DZPTBH;
    }

    public void setDZPTBH(String DZPTBH) {
        this.DZPTBH = DZPTBH;
    }

    public String getXZH() {
        return XZH;
    }

    public void setXZH(String XZH) {
        this.XZH = XZH;
    }

    public String getCUN() {
        return CUN;
    }

    public void setCUN(String CUN) {
        this.CUN = CUN;
    }

    public String getZU() {
        return ZU;
    }

    public void setZU(String ZU) {
        this.ZU = ZU;
    }

    public String getDNAME() {
        return DNAME;
    }

    public void setDNAME(String DNAME) {
        this.DNAME = DNAME;
    }

    public String getDZTYPE() {
        return DZTYPE;
    }

    public void setDZTYPE(String DZTYPE) {
        this.DZTYPE = DZTYPE;
    }

    public String getGM() {
        return GM;
    }

    public void setGM(String GM) {
        this.GM = GM;
    }

    public String getGMDJ() {
        return GMDJ;
    }

    public void setGMDJ(String GMDJ) {
        this.GMDJ = GMDJ;
    }

    public String getWXDX() {
        return WXDX;
    }

    public void setWXDX(String WXDX) {
        this.WXDX = WXDX;
    }

    public String getWXHS() {
        return WXHS;
    }

    public void setWXHS(String WXHS) {
        this.WXHS = WXHS;
    }

    public String getWXRK() {
        return WXRK;
    }

    public void setWXRK(String WXRK) {
        this.WXRK = WXRK;
    }

    public String getQZJJSS() {
        return QZJJSS;
    }

    public void setQZJJSS(String QZJJSS) {
        this.QZJJSS = QZJJSS;
    }

    public String getXQDJ() {
        return XQDJ;
    }

    public void setXQDJ(String XQDJ) {
        this.XQDJ = XQDJ;
    }

    public String getCSFSSJ() {
        return CSFSSJ;
    }

    public void setCSFSSJ(String CSFSSJ) {
        this.CSFSSJ = CSFSSJ;
    }

    public String getYXYS() {
        return YXYS;
    }

    public void setYXYS(String YXYS) {
        this.YXYS = YXYS;
    }

    public String getFZZRNAME() {
        return FZZRNAME;
    }

    public void setFZZRNAME(String FZZRNAME) {
        this.FZZRNAME = FZZRNAME;
    }

    public String getFZZRTEL() {
        return FZZRTEL;
    }

    public void setFZZRTEL(String FZZRTEL) {
        this.FZZRTEL = FZZRTEL;
    }

    public String getJCZRNAME() {
        return JCZRNAME;
    }

    public void setJCZRNAME(String JCZRNAME) {
        this.JCZRNAME = JCZRNAME;
    }

    public String getJCZRTEL() {
        return JCZRTEL;
    }

    public void setJCZRTEL(String JCZRTEL) {
        this.JCZRTEL = JCZRTEL;
    }

    public String getDJRKYEAR() {
        return DJRKYEAR;
    }

    public void setDJRKYEAR(String DJRKYEAR) {
        this.DJRKYEAR = DJRKYEAR;
    }

    public String getNCCS() {
        return NCCS;
    }

    public void setNCCS(String NCCS) {
        this.NCCS = NCCS;
    }

    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getWhtime() {
        return whtime;
    }

    public void setWhtime(String whtime) {
        this.whtime = whtime;
    }
}
