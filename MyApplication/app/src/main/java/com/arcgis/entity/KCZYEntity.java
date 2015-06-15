package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 矿产资源
 */
@DatabaseTable(tableName = "TB_KCZY")
public class KCZYEntity implements Serializable {

    @DatabaseField(generatedId = true,columnName = "ID")
    private int id;
    @DatabaseField(columnName = "X",useGetSet = true)
    private String px;//x坐标
    @DatabaseField(columnName = "Y",useGetSet = true)
    private String py; //y坐标
    @DatabaseField(columnName = "N",useGetSet = true)
    private String pn;
    @DatabaseField(columnName = "E",useGetSet = true)
    private String pe;
    @DatabaseField(columnName = "KCBH",useGetSet = true)
    private String kcNo; //矿产编号
    @DatabaseField(columnName = "KCNAME",useGetSet = true)
    private String kcName;//矿产名称
    @DatabaseField(columnName = "KCTYPE",useGetSet = true)
    private String kcType;//矿产类型
    @DatabaseField(columnName = "XZ",useGetSet = true)
    private String ssxz; //所属乡镇
    @DatabaseField(columnName = "CUN",useGetSet = true)
    private String sscun;//所属村
    @DatabaseField(columnName = "XXWZ",useGetSet = true)
    private String xxwz;//详细位置
    @DatabaseField(columnName = "JCR",useGetSet = true)
    private String jianceren;//监测人
    @DatabaseField(columnName = "LXPHONE",useGetSet = true)
    private String phone;//联系电话
    @DatabaseField(columnName = "TPPATH",useGetSet = true)
    private String photoPath;//图片
    @DatabaseField(columnName = "SPPATH",useGetSet = true)
    private String videoPath;//视频
    @DatabaseField(columnName = "ADDTIME",useGetSet = true)
    private String addtime;//添加时间
    @DatabaseField(columnName = "KCAREA",useGetSet = true)
    private String kcmj;//矿产面积
    @DatabaseField(columnName = "ISLAW",useGetSet = true)
    private String SFHF;//是否合法
    @DatabaseField(columnName = "KCCL",useGetSet = true)
    private String kccl;//矿产储量
    @DatabaseField(columnName = "BZ",useGetSet = true)
    private String bz;//备注
    @DatabaseField(columnName = "KCCLZT",useGetSet = true)
    private String clzt;//处理状态
    @DatabaseField(columnName = "COORDS",useGetSet = true)
    private String coords;//多边形坐标

    public KCZYEntity(){}

    @Override
    public String toString() {
        return "KCZYEntity{" +
                "id=" + id +
                ", px='" + px + '\'' +
                ", py='" + py + '\'' +
                ", pn='" + pn + '\'' +
                ", pe='" + pe + '\'' +
                ", kcNo='" + kcNo + '\'' +
                ", kcName='" + kcName + '\'' +
                ", kcType='" + kcType + '\'' +
                ", ssxz='" + ssxz + '\'' +
                ", sscun='" + sscun + '\'' +
                ", xxwz='" + xxwz + '\'' +
                ", jianceren='" + jianceren + '\'' +
                ", phone='" + phone + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", addtime='" + addtime + '\'' +
                ", kcmj='" + kcmj + '\'' +
                ", SFHF='" + SFHF + '\'' +
                ", kccl='" + kccl + '\'' +
                ", bz='" + bz + '\'' +
                ", clzt='" + clzt + '\'' +
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

    public String getKccl() {
        return kccl;
    }

    public void setKccl(String kccl) {
        this.kccl = kccl;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getClzt() {
        return clzt;
    }

    public void setClzt(String clzt) {
        this.clzt = clzt;
    }

    public String getSFHF() {
        return SFHF;
    }

    public void setSFHF(String SFHF) {
        this.SFHF = SFHF;
    }

    public String getKcmj() {
        return kcmj;
    }

    public void setKcmj(String kcmj) {
        this.kcmj = kcmj;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
    }

    public String getPy() {
        return py;
    }

    public void setPy(String py) {
        this.py = py;
    }

    public String getSscun() {
        return sscun;
    }

    public void setSscun(String sscun) {
        this.sscun = sscun;
    }

    public String getXxwz() {
        return xxwz;
    }

    public void setXxwz(String xxwz) {
        this.xxwz = xxwz;
    }

    public String getSsxz() {
        return ssxz;
    }

    public void setSsxz(String ssxz) {
        this.ssxz = ssxz;
    }

    public String getJianceren() {
        return jianceren;
    }

    public void setJianceren(String jianceren) {
        this.jianceren = jianceren;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getKcNo() {
        return kcNo;
    }

    public void setKcNo(String kcNo) {
        this.kcNo = kcNo;
    }

    public String getKcName() {
        return kcName;
    }

    public void setKcName(String kcName) {
        this.kcName = kcName;
    }

    public String getKcType() {
        return kcType;
    }

    public void setKcType(String kcType) {
        this.kcType = kcType;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
