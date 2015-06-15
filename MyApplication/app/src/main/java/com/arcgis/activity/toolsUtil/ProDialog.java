package com.arcgis.activity.toolsUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.arcgis.R;


public class ProDialog extends Dialog {


	public ProDialog(Context context, View convertView) {
		super(context, R.style.ProDialogStyle);
		setContentView(convertView);
		setCanceledOnTouchOutside(false);
	}
}
