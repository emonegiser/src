package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 储备用地
 */
@DatabaseTable(tableName = "TB_CBYD")
public class CBYDEntity implements Serializable {
   // {"OBJECTID_1":6962,"OBJECTID":null,"BH":"2939576","DKZL":null,"DKMJ":765802.60656476,
   // "PZSJ":"\/Date(1423065600000)\/","LGGYDJSJ":"\/Date(1423065600000)\/","ZDBCFY":0.00000000,
   // "DSFZWBCFY":0.00000000,"FFID":null,"PZWH":null,"XZ":"海子街镇","CUN":"插枪岩村","SFNRZFCB":"是",
   // "TDZSMC":"毕节市2009年度第九批次城镇建设用地","TDZSWH":"0","X":105.43978202,"Y":27.40619936,
   // "BPID":null,"BPMJ":null,"DTTF":"G48 G 015056,G48 G 015055","Shape":6961}

    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "OBJECTID",useGetSet = true)
    private String OBJECTID;
    @DatabaseField(columnName = "OBJECTID_1",useGetSet = true)
    private String OBJECTID_1;
    @DatabaseField(columnName = "BH",useGetSet = true)
    private String BH;
    @DatabaseField(columnName = "DKZL",useGetSet = true)
    private String DKZL;
    @DatabaseField(columnName = "DKMJ",useGetSet = true)
    private String DKMJ;
    //批准时间
    @DatabaseField(columnName = "PZSJ",useGetSet = true)
    private String PZSJ;
    @DatabaseField(columnName = "LGGYDJSJ",useGetSet = true)
    private String LGGYDJSJ;
    //土地补偿费用
    @DatabaseField(columnName = "ZDBCFY",useGetSet = true)
    private String ZDBCFY;
    //地上附着物补偿费用
    @DatabaseField(columnName = "DSFZWBCFY",useGetSet = true)
    private String DSFZWBCFY;
    @DatabaseField(columnName = "FFID",useGetSet = true)
    private String FFID;
    //批准文号
    @DatabaseField(columnName = "PZWH",useGetSet = true)
    private String PZWH;
    @DatabaseField(columnName = "XZ",useGetSet = true)
    private String XZ;
    @DatabaseField(columnName = "CUN",useGetSet = true)
    private String CUN;
    //是否纳入政府储备
    @DatabaseField(columnName = "SFNRZFCB",useGetSet = true)
    private String SFNRZFCB;
    @DatabaseField(columnName = "TDZSMC",useGetSet = true)
    private String TDZSMC;
    @DatabaseField(columnName = "TDZSWH",useGetSet = true)
    private String TDZSWH;
    @DatabaseField(columnName = "X",useGetSet = true)
    private String X;
    @DatabaseField(columnName = "Y",useGetSet = true)
    private String Y;
    @DatabaseField(columnName = "BPID",useGetSet = true)
    private String BPID;
    @DatabaseField(columnName = "BPMJ",useGetSet = true)
    private String BPMJ;
    @DatabaseField(columnName = "DTTF",useGetSet = true)
    private String DTTF;
    @DatabaseField(columnName = "Shape",useGetSet = true)
    private String Shape;
    @DatabaseField(columnName = "COORDS",useGetSet = true)
    private String coords;

    public CBYDEntity(){}

    @Override
    public String toString() {
        return "CBYDEntity{" +
                "id=" + id +
                ", OBJECTID='" + OBJECTID + '\'' +
                ", OBJECTID_1='" + OBJECTID_1 + '\'' +
                ", BH='" + BH + '\'' +
                ", DKZL='" + DKZL + '\'' +
                ", DKMJ='" + DKMJ + '\'' +
                ", PZSJ='" + PZSJ + '\'' +
                ", LGGYDJSJ='" + LGGYDJSJ + '\'' +
                ", ZDBCFY='" + ZDBCFY + '\'' +
                ", DSFZWBCFY='" + DSFZWBCFY + '\'' +
                ", FFID='" + FFID + '\'' +
                ", PZWH='" + PZWH + '\'' +
                ", XZ='" + XZ + '\'' +
                ", CUN='" + CUN + '\'' +
                ", SFNRZFCB='" + SFNRZFCB + '\'' +
                ", TDZSMC='" + TDZSMC + '\'' +
                ", TDZSWH='" + TDZSWH + '\'' +
                ", X='" + X + '\'' +
                ", Y='" + Y + '\'' +
                ", BPID='" + BPID + '\'' +
                ", BPMJ='" + BPMJ + '\'' +
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

    public String getOBJECTID_1() {
        return OBJECTID_1;
    }

    public void setOBJECTID_1(String OBJECTID_1) {
        this.OBJECTID_1 = OBJECTID_1;
    }

    public String getBH() {
        return BH;
    }

    public void setBH(String BH) {
        this.BH = BH;
    }

    public String getDKZL() {
        return DKZL;
    }

    public void setDKZL(String DKZL) {
        this.DKZL = DKZL;
    }

    public String getDKMJ() {
        return DKMJ;
    }

    public void setDKMJ(String DKMJ) {
        this.DKMJ = DKMJ;
    }

    public String getPZSJ() {
        return PZSJ;
    }

    public void setPZSJ(String PZSJ) {
        this.PZSJ = PZSJ;
    }

    public String getLGGYDJSJ() {
        return LGGYDJSJ;
    }

    public void setLGGYDJSJ(String LGGYDJSJ) {
        this.LGGYDJSJ = LGGYDJSJ;
    }

    public String getZDBCFY() {
        return ZDBCFY;
    }

    public void setZDBCFY(String ZDBCFY) {
        this.ZDBCFY = ZDBCFY;
    }

    public String getDSFZWBCFY() {
        return DSFZWBCFY;
    }

    public void setDSFZWBCFY(String DSFZWBCFY) {
        this.DSFZWBCFY = DSFZWBCFY;
    }

    public String getFFID() {
        return FFID;
    }

    public void setFFID(String FFID) {
        this.FFID = FFID;
    }

    public String getPZWH() {
        return PZWH;
    }

    public void setPZWH(String PZWH) {
        this.PZWH = PZWH;
    }

    public String getXZ() {
        return XZ;
    }

    public void setXZ(String XZ) {
        this.XZ = XZ;
    }

    public String getCUN() {
        return CUN;
    }

    public void setCUN(String CUN) {
        this.CUN = CUN;
    }

    public String getSFNRZFCB() {
        return SFNRZFCB;
    }

    public void setSFNRZFCB(String SFNRZFCB) {
        this.SFNRZFCB = SFNRZFCB;
    }

    public String getTDZSMC() {
        return TDZSMC;
    }

    public void setTDZSMC(String TDZSMC) {
        this.TDZSMC = TDZSMC;
    }

    public String getTDZSWH() {
        return TDZSWH;
    }

    public void setTDZSWH(String TDZSWH) {
        this.TDZSWH = TDZSWH;
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

    public String getBPID() {
        return BPID;
    }

    public void setBPID(String BPID) {
        this.BPID = BPID;
    }

    public String getBPMJ() {
        return BPMJ;
    }

    public void setBPMJ(String BPMJ) {
        this.BPMJ = BPMJ;
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
}
