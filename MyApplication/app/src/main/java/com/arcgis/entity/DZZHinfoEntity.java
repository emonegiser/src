package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by pang congcong on 2015/5/28.
 */

/**
 objId=null ;
 bh;//灾害点编号
 ddr;//带队人
 scry;//随从人员
 fzzrr;//防灾责任人
 fzzrrTel;//防灾责任人电话
 jczrr;//监测责任人
 jczzrTel;//监测责任人电话
 xcms;//巡查描述
 xcFiles;//巡查文件
 */
@DatabaseTable(tableName = "TB_DZZHinfo")
public class DZZHinfoEntity implements Serializable {
    @DatabaseField(generatedId = true,columnName = "idd",useGetSet = true)
    private int idd;

    @DatabaseField(columnName = "OBJID",useGetSet = true)
    private String objId;

    @DatabaseField(columnName = "BH",useGetSet = true)
    private String bh;

    @DatabaseField(columnName = "DDR",useGetSet = true)
    private String ddr;

    @DatabaseField(columnName = "SCRY",useGetSet = true)
    private String scry;

    @DatabaseField(columnName = "SCJCSJ",useGetSet = true)
    private String scjcsj;//上次监测数据

    @DatabaseField(columnName = "BCJCSJ",useGetSet = true)
    private String bcjcsj;//本次监测数据

    @DatabaseField(columnName = "JYQK",useGetSet = true)
    private String jyqk;//降雨情况

    @DatabaseField(columnName = "WYL",useGetSet = true)
    private String wyl;//位移量

    @DatabaseField(columnName = "CZWT",useGetSet = true)
    private String czwt;//存在问题

    @DatabaseField(columnName = "CLYJ",useGetSet = true)
    private String clyj;//处理意见

    @DatabaseField(columnName = "CLJG",useGetSet = true)
    private String cljg;//处理结果

    @DatabaseField(columnName = "FZZRR",useGetSet = true)
    private String fzzrr;

    @DatabaseField(columnName = "FZZRRTEL",useGetSet = true)
    private String fzzrrTel;

    @DatabaseField(columnName = "JCZRR",useGetSet = true)
    private String jczrr;

    @DatabaseField(columnName = "JCZZRTEL",useGetSet = true)
    private String jczzrTel;

    @DatabaseField(columnName = "XCMS",useGetSet = true)
    private String xcms;

    @DatabaseField(columnName = "XCFILES",useGetSet = true)
    private String xcFiles;

    @Override
    public String toString() {
        return "DZZHinfoEntity{" +
                "idd=" + idd +
                ", objId='" + objId + '\'' +
                ", bh='" + bh + '\'' +
                ", ddr='" + ddr + '\'' +
                ", scry='" + scry + '\'' +
                ", scjcsj='" + scjcsj + '\'' +
                ", bcjcsj='" + bcjcsj + '\'' +
                ", jyqk='" + jyqk + '\'' +
                ", wyl='" + wyl + '\'' +
                ", czwt='" + czwt + '\'' +
                ", clyj='" + clyj + '\'' +
                ", cljg='" + cljg + '\'' +
                ", fzzrr='" + fzzrr + '\'' +
                ", fzzrrTel='" + fzzrrTel + '\'' +
                ", jczrr='" + jczrr + '\'' +
                ", jczzrTel='" + jczzrTel + '\'' +
                ", xcms='" + xcms + '\'' +
                ", xcFiles='" + xcFiles + '\'' +
                '}';
    }

    public int getIdd() {
        return idd;
    }

    public void setIdd(int idd) {
        this.idd = idd;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getDdr() {
        return ddr;
    }

    public void setDdr(String ddr) {
        this.ddr = ddr;
    }

    public String getScry() {
        return scry;
    }

    public void setScry(String scry) {
        this.scry = scry;
    }

    public String getScjcsj() {
        return scjcsj;
    }

    public void setScjcsj(String scjcsj) {
        this.scjcsj = scjcsj;
    }

    public String getBcjcsj() {
        return bcjcsj;
    }

    public void setBcjcsj(String bcjcsj) {
        this.bcjcsj = bcjcsj;
    }

    public String getJyqk() {
        return jyqk;
    }

    public void setJyqk(String jyqk) {
        this.jyqk = jyqk;
    }

    public String getWyl() {
        return wyl;
    }

    public void setWyl(String wyl) {
        this.wyl = wyl;
    }

    public String getCzwt() {
        return czwt;
    }

    public void setCzwt(String czwt) {
        this.czwt = czwt;
    }

    public String getClyj() {
        return clyj;
    }

    public void setClyj(String clyj) {
        this.clyj = clyj;
    }

    public String getFzzrr() {
        return fzzrr;
    }

    public void setFzzrr(String fzzrr) {
        this.fzzrr = fzzrr;
    }

    public String getCljg() {
        return cljg;
    }

    public void setCljg(String cljg) {
        this.cljg = cljg;
    }

    public String getFzzrrTel() {
        return fzzrrTel;
    }

    public void setFzzrrTel(String fzzrrTel) {
        this.fzzrrTel = fzzrrTel;
    }

    public String getJczrr() {
        return jczrr;
    }

    public void setJczrr(String jczrr) {
        this.jczrr = jczrr;
    }

    public String getJczzrTel() {
        return jczzrTel;
    }

    public void setJczzrTel(String jczzrTel) {
        this.jczzrTel = jczzrTel;
    }

    public String getXcms() {
        return xcms;
    }

    public void setXcms(String xcms) {
        this.xcms = xcms;
    }

    public String getXcFiles() {
        return xcFiles;
    }

    public void setXcFiles(String xcFiles) {
        this.xcFiles = xcFiles;
    }
}
