package com.arcgis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.PZYDEntity;

import java.util.List;

/**
 * Created by mars on 2015/1/31.
 */
public class PZYDListAdapter extends BaseAdapter {
    List<PZYDEntity> PZYDEntity_list=null;
    LayoutInflater inflater=null;
    Context context=null;
    public PZYDListAdapter(List<PZYDEntity> PZYDEntity_list, Context context) {

        this.PZYDEntity_list = PZYDEntity_list;
        this.context=context;
        //this.inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inflater = LayoutInflater.from(context);
    }

    public void onDateChange(List<PZYDEntity> PZYDEntity_list) {
        this.PZYDEntity_list = PZYDEntity_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return PZYDEntity_list.size();
    }

    @Override
    public Object getItem(int position) {
        return PZYDEntity_list.get(position);
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
        String dzlx=PZYDEntity_list.get(position).getBH();
        String dzdd=PZYDEntity_list.get(position).getXZ()+","+PZYDEntity_list.get(position).getCUN();
        String dzsj=PZYDEntity_list.get(position).getPZSJ();
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
