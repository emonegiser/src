package com.arcgis.uploadofflinedata;

import android.content.Context;
import android.util.Log;

import com.arcgis.dao.DZZHDao;
import com.arcgis.dao.DZZHinfoDao;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.DZZHinfoEntity;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by EMonegiser on 2015/5/29.
 */
public class UploadDZZHinfoData {


    private  final  String TAG="UploadDZZHinfoData";
    Context context;
    DZZHinfoDao dzzHinfoDao;
    KsoapValidateHttp mksoapValidateHttp=null;

    public  UploadDZZHinfoData(Context context)
    {
        this.context=context;
        dzzHinfoDao=new DZZHinfoDao(context);
    }

    public void QueryAndUploadDZZHinfo()
    {
        mksoapValidateHttp=new KsoapValidateHttp(context);
        String result=null;
        if (dzzHinfoDao.queryAll()!=null)
        {
            Log.i(TAG, "------------------some records has been Queryed-------------------");
            List<Integer> ids= dzzHinfoDao.QueryIds();
            for (Integer id:ids)
            {
                DZZHinfoEntity dzzHinfoEntity= dzzHinfoDao.getDZZHEntityinfo(id);
                if (NetUtils.isNetworkAvailable(context))
                {
                    try {
                        result= mksoapValidateHttp.WebAddBJS_DZZH_PT2(dzzHinfoEntity.getObjId(),dzzHinfoEntity.getBh(),dzzHinfoEntity.getDdr(),dzzHinfoEntity.getScry(),dzzHinfoEntity.getFzzrr(),dzzHinfoEntity.getFzzrrTel(),dzzHinfoEntity.getJczrr(),dzzHinfoEntity.getJczzrTel(),dzzHinfoEntity.getXcms(),dzzHinfoEntity.getXcFiles(),
                                dzzHinfoEntity.getScjcsj(),dzzHinfoEntity.getBcjcsj(),dzzHinfoEntity.getJyqk(),dzzHinfoEntity.getWyl(),dzzHinfoEntity.getCzwt(),dzzHinfoEntity.getClyj(),dzzHinfoEntity.getCljg());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if (result!=null) {
                        Log.i(TAG, "------------------updata completed-------------------");
                        if (dzzHinfoDao.deleteDZZHinfo(id)){
                            Log.i(TAG, "------------------Deletedata completed-------------------");
                        }
                    }
                    else {
                        Log.i(TAG, "------------------updata failed-------------------");
                    }
                }
            }
        }
    }
}
