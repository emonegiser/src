package com.arcgis.uploadofflinedata;

import android.content.Context;
import android.util.Log;

import com.arcgis.dao.DZZHDao;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by pangcongcong  on 2015/5/26.
 */
public class UploadDZZHData {

    private  final  String TAG="UploadDZZHData";
    Context context;
    DZZHDao dzzhDao;
    KsoapValidateHttp mksoapValidateHttp=null;

    public  UploadDZZHData(Context context)
    {
        this.context=context;
        dzzhDao=new DZZHDao(context);
    }



    public void QueryAndUploadDZZH()
    {
        mksoapValidateHttp=new KsoapValidateHttp(context);
        String result=null;
        if (dzzhDao.queryAll()!=null)
        {
            Log.i(TAG, "------------------some records has been Queryed-------------------");
            List<Integer> ids= dzzhDao.QueryIds();
            for (Integer id:ids)
            {
                DZZHEntity dzzhEntity= dzzhDao.getDZZHEntity(id);
                if (NetUtils.isNetworkAvailable(context))
                {
                    try {
                        result= mksoapValidateHttp.WebAddBJS_DZZH_PT(dzzhEntity.getX(),dzzhEntity.getY(),dzzhEntity.getDZPTBH(),dzzhEntity.getXQ(),dzzhEntity.getXZH(),dzzhEntity.getCUN(),dzzhEntity.getZU(),
                                dzzhEntity.getDNAME(),dzzhEntity.getDZTYPE(),
                                dzzhEntity.getGM(),dzzhEntity.getGMDJ(),dzzhEntity.getWXDX(),dzzhEntity.getWXHS(),dzzhEntity.getWXRK(),dzzhEntity.getQZJJSS(),dzzhEntity.getXQDJ(),dzzhEntity.getCSFSSJ(),dzzhEntity.getYXYS(),
                                dzzhEntity.getFZZRNAME(),dzzhEntity.getFZZRTEL(),dzzhEntity.getJCZRNAME(),dzzhEntity.getJCZRTEL(),dzzhEntity.getDJRKYEAR(),dzzhEntity.getNCCS(),dzzhEntity.getBZ());

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if (result!=null) {
                        Log.i(TAG, "------------------updata completed-------------------");
                        if (dzzhDao.deleteDZZH(id)) {
                            Log.i(TAG, "------------------DeleteData completed-------------------");
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
