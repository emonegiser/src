package com.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.entity.YJSPXXEntity;
import com.arcgis.entity.YJSPXXSEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mars on 2015/1/31.
 */
public class YJZSPJXXListAdapter extends BaseAdapter {
    List<YJSPXXSEntity> dataList=null;
    LayoutInflater inflater=null;
    Context context=null;
    public YJZSPJXXListAdapter(Context context) {


        this.context=context;
        //this.inflaSer =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(context);
    }

    public void addData(List<YJSPXXSEntity> dataList) {
        if(dataList==null){
            this.dataList = new ArrayList<>();
            this.notifyDataSetChanged();
        }else {
            this.dataList = dataList;
            this.notifyDataSetChanged();
        }
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
            convertView=inflater.inflate(R.layout.item_dzzhyj_list, null);

            vHolder=new ViewHolder();
            vHolder.textview1=(TextView) convertView.findViewById(R.id.type);
            vHolder.textview2=(TextView) convertView.findViewById(R.id.address);
            vHolder.textview3=(TextView) convertView.findViewById(R.id.time);
            convertView.setTag(vHolder);
        }else{
            vHolder=(ViewHolder) convertView.getTag();
        }
        String type=dataList.get(position).getDi_type();//ZHMC
        String address=dataList.get(position).getDi_Add();
        String time=null;
        if(!dataList.get(position).getDi_Time().equals("")){
             time=dataList.get(position).getDi_Time().split(" ")[0];
        }
        vHolder.textview1.setText(type);
        vHolder.textview2.setText(address);
        vHolder.textview3.setText(time);
        return convertView;
    }

    class ViewHolder{
        TextView textview1;
        TextView textview2;
        TextView textview3;
    }
}
