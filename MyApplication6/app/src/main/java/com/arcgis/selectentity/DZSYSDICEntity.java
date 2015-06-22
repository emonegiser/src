package com.arcgis.selectentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mars on 2015/2/13.
 */
//地灾规模
@DatabaseTable(tableName = "TB_SYSTEMDICTIONARY")
public class DZSYSDICEntity {

    @DatabaseField(generatedId = true,columnName = "ID")
    private int id;
    @DatabaseField(columnName = "NAME",useGetSet = true)
    private String name;

    @DatabaseField(columnName = "TYPE",useGetSet = true)
    private String type;

    public DZSYSDICEntity() {
    }

    @Override
    public String toString() {
        return "DZScaleEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
