package com.arcgis.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import com.arcgis.R;
import com.arcgis.activity.MainMap5Activity;

import java.util.Calendar;

/**
 * Created by mars on 2015/2/6.
 */
@SuppressLint("ValidFragment")
public  class  DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Context context;
    String edittextID;

    public DatePickerFragment(){}
    public DatePickerFragment(Context context,String edittextID) {
        this.context = context;
        this.edittextID=edittextID;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Log.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);
        String m="";
        String d="";
        if(month<10){
            m="0"+(month+1)+"";
        }else{
            m=month+"";
        }

        if(day<10){
            d="0"+day+"";
        }else{
            d=day+"";
        }

        EditText timeEdittext = (EditText) MainMap5Activity.getInstance().findViewById(R.id.EditTextsjjdsj);
        timeEdittext.setText(year+"-"+m+"-"+d);

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(MainMap5Activity.getInstance(), this, year, month, day);
    }
}
