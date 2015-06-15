package com.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.DZZHYJEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by mars on 2015/1/31.
 */
public class DZZHYJAlertListAdapter extends BaseAdapter {
    List<Map<String,String>> dataList=null;
    LayoutInflater inflater=null;
    Context context=null;
    public DZZHYJAlertListAdapter(Context context) {


        this.context=context;
        //this.inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(context);
    }

    public void addData(List<Map<String,String>>  dataList) {
        this.dataList = dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList==null?0: dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder=null;

        if(null==convertView){
            convertView=inflater.inflate(R.layout.item_alert_type, null);
            vHolder=new ViewHolder();
            vHolder.textview1=(TextView) convertView.findViewById(R.id.type);
            convertView.setTag(vHolder);
        }else{
            vHolder=(ViewHolder) convertView.getTag();
        }
        String type=dataList.get(position).get("item");//ZHMC
        vHolder.textview1.setText(type);

        return convertView;
    }
    class ViewHolder{
        TextView textview1;
    }
}
