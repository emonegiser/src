package com.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.DZZHEntity;

import java.util.List;

/**
 * Created by mars on 2015/1/31.
 */
public class DZListAdapter extends BaseAdapter {
    List<DZZHEntity> DZEntity_list=null;
    LayoutInflater inflater=null;
    Context context=null;
    String dzsj;
    String cs;
    public DZListAdapter(List<DZZHEntity> DZEntity_list,Context context) {

        this.DZEntity_list = DZEntity_list;
        this.context=context;
        //this.inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(context);
    }

    public void onDateChange(List<DZZHEntity> DZEntity_list) {
        this.DZEntity_list = DZEntity_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return DZEntity_list.size();
    }

    @Override
    public Object getItem(int position) {
        return DZEntity_list.get(position);
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
            vHolder.textview4=(TextView) convertView.findViewById(R.id.dzcs);
            convertView.setTag(vHolder);
        }else{
            vHolder=(ViewHolder) convertView.getTag();
        }

       // DZEntity_list.get(position).getDZPTBH();

        String dzlx=DZEntity_list.get(position).getDZTYPE();
        String dzdd=DZEntity_list.get(position).getXZH()+DZEntity_list.get(position).getCUN()+DZEntity_list.get(position).getZU();
        if(!DZEntity_list.get(position).getWhtime().equals("0")){
            dzsj=DZEntity_list.get(position).getWhtime();
        }else {
            dzsj=DZEntity_list.get(position).getCSFSSJ();
        }
        if (!DZEntity_list.get(position).getCs().equals("0")){
            int CS= Integer.valueOf(DZEntity_list.get(position).getCs()).intValue()+1;
            cs=Integer.toString(CS);
        }else{
            cs="1";
        }
        vHolder.textview1.setText(dzlx);
        vHolder.textview2.setText(dzdd);
        vHolder.textview3.setText(dzsj);
        vHolder.textview4.setText(cs);
        return convertView;
    }

    class ViewHolder{
        TextView textview1;
        TextView textview2;
        TextView textview3;
        TextView textview4;

    }
}
