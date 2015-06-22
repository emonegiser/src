package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**巡查人员实体类
 * Created by EMonegiser on 2015/6/7.
 */
@DatabaseTable(tableName = "TB_XCRY")
public class XCRYEntity  implements Serializable{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "RYBH",useGetSet = true)
    private String rybh;

    @DatabaseField(columnName = "NAME",useGetSet = true)
    private String Name;

    @DatabaseField(columnName = "BM",useGetSet = true)
    private String bm;

    @DatabaseField(columnName = "TEL",useGetSet = true)
    private String Tel;

    public XCRYEntity(int id, String rybh, String name, String bm, String tel) {
        this.id = id;
        this.rybh = rybh;
        Name = name;
        this.bm = bm;
        Tel = tel;
    }

    public XCRYEntity() {
    }

    @Override
    public String toString() {
        return "XCRYEntity{" +
                "id=" + id +
                ", rybh='" + rybh + '\'' +
                ", Name='" + Name + '\'' +
                ", bm='" + bm + '\'' +
                ", Tel='" + Tel + '\'' +
                '}';
    }

    public String getRybh() {
        return rybh;
    }

    public void setRybh(String rybh) {
        this.rybh = rybh;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public int getId() {
        return id;
    }

}
