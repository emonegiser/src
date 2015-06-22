package com.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.CBYDEntity;

import java.util.List;

/**
 * Created by mars on 2015/1/31.
 */
public class CBYDListAdapter extends BaseAdapter {
    List<CBYDEntity> CBYDEntity_list=null;
    LayoutInflater inflater=null;
    Context context=null;
    public CBYDListAdapter(List<CBYDEntity> CBYDEntity_list, Context context) {

        this.CBYDEntity_list = CBYDEntity_list;
        this.context=context;
        //this.inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(context);
    }

    public void onDateChange(List<CBYDEntity> CBYDEntity_list) {
        this.CBYDEntity_list = CBYDEntity_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return CBYDEntity_list.size();
    }

    @Override
    public Object getItem(int position) {
        return CBYDEntity_list.get(position);
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
        String dzlx=CBYDEntity_list.get(position).getBH();
        String dzdd=CBYDEntity_list.get(position).getXZ()+","+CBYDEntity_list.get(position).getCUN();
        String dzsj=CBYDEntity_list.get(position).getPZSJ();
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
