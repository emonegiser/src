package com.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.GYPZYDEntity;

import java.util.List;

/**
 * Created by mars on 2015/1/31.
 */
public class GYPZYDListAdapter extends BaseAdapter {
    List<GYPZYDEntity> GYPZYDEntity_list=null;
    LayoutInflater inflater=null;
    Context context=null;
    public GYPZYDListAdapter(List<GYPZYDEntity> GYPZYDEntity_list, Context context) {

        this.GYPZYDEntity_list = GYPZYDEntity_list;
        this.context=context;
        //this.inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(context);
    }

    public void onDateChange(List<GYPZYDEntity> GYPZYDEntity_list) {
        this.GYPZYDEntity_list = GYPZYDEntity_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return GYPZYDEntity_list.size();
    }

    @Override
    public Object getItem(int position) {
        return GYPZYDEntity_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder=null;

        if(null==convertView){
            convertView=inflater.inflate(R.layout.dzdetail_item, null);

            vHolder=new ViewHolder();
            vHolder.textview1=(TextView) convertView.findViewById(R.id.dzlx);
            vHolder.textview2=(TextView) convertView.findViewById(R.id.dzdd);
            vHolder.textview3=(TextView) convertView.findViewById(R.id.dzsj);
            convertView.setTag(vHolder);
        }else{
            vHolder=(ViewHolder) convertView.getTag();
        }
        String dzlx=GYPZYDEntity_list.get(position).getGYYDBH();
        String dzdd=GYPZYDEntity_list.get(position).getXZ()+","+GYPZYDEntity_list.get(position).getCUN();
        String dzsj=GYPZYDEntity_list.get(position).getGDPFSJ();
        vHolder.textview1.setText(dzlx);
        vHolder.textview2.setText(dzdd);
        vHolder.textview3.setText(dzsj);
        return convertView;
    }

    class ViewHolder{
        TextView textview1;
        TextView textview2;
        TextView textview3;
    }
}
