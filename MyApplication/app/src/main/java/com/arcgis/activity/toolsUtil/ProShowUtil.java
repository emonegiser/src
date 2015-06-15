package com.arcgis.activity.toolsUtil;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.arcgis.R;


/**
 * UIhelper
 * @author ethan
 * @date 2014年7月5日
 */
public class ProShowUtil {


	private static ProDialog progressDialog;

	public static void showLoading(Context context,String msg) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			View contentView = LayoutInflater.from(context).inflate(R.layout.pro_loading_dialog, null);
			TextView textView = (TextView) contentView.findViewById(R.id.tip);
			if (msg != null && !msg.equals("")) {
				textView.setText(msg);
			}
			progressDialog = new ProDialog(context, contentView);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}

	}

	public static void closeLoading() {
		try {
			
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss(); 
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static ProDialog stringSelectorDialog = null;


	public static void closeStringArraySelector(Context context) {
		if (stringSelectorDialog != null && stringSelectorDialog.isShowing()) {
			stringSelectorDialog.dismiss();
		}
	}

	public static Dialog dateSelectorDialog = null;
	public static Dialog timeSelectorDialog = null;

	/**
	 * 日期对话框
	 * @param context
	 * @param date
	 * @param callBack
	 */
	public static void showDateSelectorDialog(Context context, Date date,
			final DatePickerDialog.OnDateSetListener callBack) {

		Calendar l = Calendar.getInstance();

		if (date != null) {
			l.setTime(date);
		}

		if (dateSelectorDialog == null || !dateSelectorDialog.isShowing()) {
			dateSelectorDialog = new DatePickerDialog(context, callBack,
					l.get(Calendar.YEAR),
					l.get(Calendar.MONTH),
					l.get(Calendar.DAY_OF_MONTH));

			dateSelectorDialog.show();
		}

	}
	
	/**
	 * 时间对话框
	 * @param context
	 * @param date
	 * @param callBack
	 */
	public static void showTimeSelectorDialog(Context context,final TimePickerDialog.OnTimeSetListener callBack) {
		
		Calendar l = Calendar.getInstance();
		
		l.setTime(new Date());
		
		if (timeSelectorDialog == null || !timeSelectorDialog.isShowing()) {
			timeSelectorDialog = new TimePickerDialog(context, callBack,
					l.get(Calendar.HOUR_OF_DAY),
					l.get(Calendar.MINUTE), true);
			
			timeSelectorDialog.show();
		}
		
	}

	public static void closeDateSelectorDialog() {
		if (dateSelectorDialog != null && dateSelectorDialog.isShowing()) {
			dateSelectorDialog.dismiss();
		}
	}
	public static void closeTimeSelectorDialog() {
		if (timeSelectorDialog != null && timeSelectorDialog.isShowing()) {
			timeSelectorDialog.dismiss();
		}
	}

}
