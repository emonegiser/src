package com.arcgis.httputil;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.entity.DZZHYJImageEntity;
import com.arcgis.entity.YJSPXXEntity;
import com.arcgis.entity.YJSPXXSEntity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jcifs.util.Base64;

public class KsoapValidateHttp {

    private static final String NAMESPACE = "http://tempuri.org/";
    private Context context=null;

    public KsoapValidateHttp(Context context) {
        this.context=context;

        SharedPreferences CONFSYS_INFO =context.getSharedPreferences("CONFSYS_INFO",context.MODE_PRIVATE);

        if(CONFSYS_INFO.getString("WPZF",null)!=null && CONFSYS_INFO.getString("WPZF",null).length()>0){
            ConstantVar.WPZFURL=CONFSYS_INFO.getString("WPZF","");
        }

        if(CONFSYS_INFO.getString("XCRW",null)!=null && CONFSYS_INFO.getString("XCRW",null).length()>0){
            ConstantVar.XCRWURL=CONFSYS_INFO.getString("XCRW","");
        }

        if(CONFSYS_INFO.getString("CBYD",null)!=null && CONFSYS_INFO.getString("CBYD",null).length()>0){
            ConstantVar.CBYDURL=CONFSYS_INFO.getString("CBYD","");
        }

        if(CONFSYS_INFO.getString("GYYD",null)!=null && CONFSYS_INFO.getString("GYYD",null).length()>0){
            ConstantVar.GYYDURL=CONFSYS_INFO.getString("GYYD","");
        }

        if(CONFSYS_INFO.getString("PZYD",null)!=null && CONFSYS_INFO.getString("PZYD",null).length()>0){
            ConstantVar.PZYDURL=CONFSYS_INFO.getString("PZYD","");
        }

        if(CONFSYS_INFO.getString("SBYD",null)!=null && CONFSYS_INFO.getString("SBYD",null).length()>0){
            ConstantVar.SBYDURL=CONFSYS_INFO.getString("SBYD","");
        }

        if(CONFSYS_INFO.getString("KCZY",null)!=null && CONFSYS_INFO.getString("KCZY",null).length()>0){
            ConstantVar.KCURL=CONFSYS_INFO.getString("KCZY","");
        }

        if(CONFSYS_INFO.getString("DZZH",null)!=null && CONFSYS_INFO.getString("DZZH",null).length()>0){
            ConstantVar.DZZHQUERYURL=CONFSYS_INFO.getString("DZZH","");
        }
        if(CONFSYS_INFO.getString("LOGIN",null)!=null && CONFSYS_INFO.getString("LOGIN",null).length()>0){
            ConstantVar.LOGINURL=CONFSYS_INFO.getString("LOGIN","");
        }

        //地图
        if(CONFSYS_INFO.getString("XZQH",null)!=null && CONFSYS_INFO.getString("XZQH",null).length()>0){
            ConstantVar.DZZHMAPURL=CONFSYS_INFO.getString("XZQH","");
        }
        if(CONFSYS_INFO.getString("XZQH",null)!=null && CONFSYS_INFO.getString("XZQH",null).length()>0){
            ConstantVar.XZQHMAPURL=CONFSYS_INFO.getString("XZQH","");
        }
        if(CONFSYS_INFO.getString("SDYX",null)!=null && CONFSYS_INFO.getString("SDYX",null).length()>0){
            ConstantVar.IMAGEURL=CONFSYS_INFO.getString("SDYX","");
        }
        if(CONFSYS_INFO.getString( "URL",null)!=null && CONFSYS_INFO.getString("URL",null).length()>0){
            ConstantVar.DZZHYJ=CONFSYS_INFO.getString("URL","");
        }
        if(CONFSYS_INFO.getString( "ZSURL",null)!=null && CONFSYS_INFO.getString("ZSURL",null).length()>0){
            ConstantVar.YJZS=CONFSYS_INFO.getString("ZSURL","");
        }
    }

    public String WebUserExitState(String pid) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webUserExitState";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("pid", pid);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String WebGetLoginUserInfo(String uid,String pwd) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLoginUserInfo";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("uid", uid);
            rpc.addProperty("pwd", pwd);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                // Log.i("login",rsltStr);
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String WebgetLoginUsertable() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "WebgetLoginUsertable";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本号
     */
    public String WebGetVercode() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webreadCode";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 添加地质灾害点
     * @throws java.io.IOException
     *  <X>string</X>
    <Y>string</Y>
    <DZPTBH>string</DZPTBH>
    <XQ>string</XQ>
    <XZH>string</XZH>
    <CUN>string</CUN>
    <ZU>string</ZU>
    <DNAME>string</DNAME>
    <DZTYPE>string</DZTYPE>
    <GM>string</GM>
    <GMDJ>string</GMDJ>
    <WXDX>string</WXDX>
    <WXHS>string</WXHS>
    <WXRK>string</WXRK>
    <QZJJSS>string</QZJJSS>
    <XQDJ>string</XQDJ>
    <CSFSSJ>string</CSFSSJ>
    <YXYS>string</YXYS>
    <FZZRNAME>string</FZZRNAME>
    <FZZRTEL>string</FZZRTEL>
    <JCZRNAME>string</JCZRNAME>
    <JCZRTEL>string</JCZRTEL>
    <DJRKYEAR>string</DJRKYEAR>
    <NCCS>string</NCCS>
    <BZ>string</BZ>

     */

    public String WebAddBJS_DZZH_PT(String x,String y,String dzptbh,String xq,String xzh,String cun,String zu,String dname,String dztype,
                                    String gm,String gmdj,String wxdx,String wxhs,String wxrk,String qzjjss,String xqdj,String csfssj,String yxys,
                                    String fzzrname,String fzzrtel,String jczrname,String jczrtel,String djrkyear,String nccs,String bz)
            throws IOException {
        String rsltStr="";
        String METHOD_NAME = "webAddBJS_DZZH_PT";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            //地址
            rpc.addProperty("X", x.replaceAll(",",""));
            //规模
            rpc.addProperty("Y", y.replaceAll(",",""));
            //普查时间
            rpc.addProperty("DZPTBH", dzptbh);
            //金额
            rpc.addProperty("XQ", xq);
            //所属乡镇
            rpc.addProperty("XZH", xzh);
            //编号
            rpc.addProperty("CUN", cun);
            //房屋
            rpc.addProperty("ZU", zu);
            //人数
            rpc.addProperty("DNAME", dname);
            //联系方式
            rpc.addProperty("DZTYPE", dztype);
            //灾害类型
            rpc.addProperty("GM", gm);
            //产生原因
            rpc.addProperty("GMDJ", gmdj);
            //稳定状态
            rpc.addProperty("WXDX", wxdx);
            //监测人
            rpc.addProperty("WXHS", wxhs);
            //处理意见
            rpc.addProperty("WXRK", wxrk);
            rpc.addProperty("QZJJSS", qzjjss);
            rpc.addProperty("XQDJ", xqdj);
            rpc.addProperty("CSFSSJ", csfssj);
            rpc.addProperty("YXYS", yxys);
            rpc.addProperty("FZZRNAME", fzzrname);
            rpc.addProperty("FZZRTEL", fzzrtel);
            rpc.addProperty("JCZRNAME", jczrname);
            rpc.addProperty("JCZRTEL", jczrtel);
            rpc.addProperty("DJRKYEAR", djrkyear);
            rpc.addProperty("NCCS", nccs);
            rpc.addProperty("BZ", bz);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String WebAddBJS_DZZH_PT2(String objId,String bh,String ddr,String scry,String fzzrr,
                                     String fzzrrTel,String jczrr,String jczzrTel,String xcms,String xcFiles,
                                     String scjcsj,String bcjcsj,String jyqk,String wyl,String czwt,String clyj,String cljg)
            throws IOException {
        String rsltStr="";
        String METHOD_NAME = "webAddBJS_DZZH_PT2";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("OBJID", objId);
            rpc.addProperty("BH", bh);
            rpc.addProperty("DDR", ddr);
            rpc.addProperty("SCRY", scry);
            rpc.addProperty("FZZRR", fzzrr);
            rpc.addProperty("FZZRRTEL", fzzrrTel);
            rpc.addProperty("JZZRR", jczrr);
            rpc.addProperty("JZZRRTEL", jczzrTel);
            rpc.addProperty("XCMS", xcms);
            rpc.addProperty("XCFILES", xcFiles);
            rpc.addProperty("SCJCSJ", scjcsj);
            rpc.addProperty("BCJCSJ", bcjcsj);
            rpc.addProperty("JYQK", jyqk);
            rpc.addProperty("WYL", wyl);
            rpc.addProperty("CZWT", czwt);
            rpc.addProperty("CLYJ", clyj);
            rpc.addProperty("CLJG", cljg);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据灾害类型查询最后一条灾害点编号信息
     * @param type
     * @return
     * @throws java.io.IOException
     */
    public String QueryDisByIdd(String type) throws IOException{
        String rsltStr="";
        String METHOD_NAME ="QueryDisasterByIdd";
        SoapPrimitive detail;
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("type", type);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 添加辅助面
     <id>string</id>
     */
    public String AddPolygon(String dzptbh,String coordstr,String type)
            throws IOException {
        String rsltStr="";
        String METHOD_NAME = "webAddPolygon";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("dzptbh", dzptbh);
            rpc.addProperty("coordstr", coordstr);
            rpc.addProperty("type", type);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 多张相片上传
     <id>string</id>
     <type>stringy</type>
     < FileName>string</ FileName>
     <photobsBase64>string</photobsBase64>
     */
    public String DZWebUploadVediosPictures(String id, String type, String FileName,String photobsBase64) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="webUpLoadFile";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("id", id);
            rpc.addProperty("type", type);
//            rpc.addProperty("vedioname", vedioname);
            rpc.addProperty("fileName", FileName);
            rpc.addProperty("byteFile", photobsBase64);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.UPLOAD,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 多张照片添加(地质灾害点)
     <pictureName>string</pictureName>
     < UpPerson>stringy</ UpPerson>
     < objId>string</ objId>
     <remark>string</remark>
     */
    //pictureName=string&UpPerson=string&objId=string&remark=string
    //  WebUpLoadFilePictures
    public String SysPicAdd(String pictureName, String UpPerson, String objId,String remark) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="WebUpLoadFilePictures";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("pictureName", pictureName);
            rpc.addProperty("UpPerson", UpPerson);
            rpc.addProperty("objId", objId);
            rpc.addProperty("remark", remark);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 多张照片添加(矿产)
     <pictureName>string</pictureName>
     < UpPerson>stringy</ UpPerson>
     < objId>string</ objId>
     <remark>string</remark>
     */
    //pictureName=string&UpPerson=string&objId=string&remark=string
    //  WebUpLoadFilePictures
    public String KCPicAdd(String pictureName, String UpPerson, String objId,String remark) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="AddMineralPic";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("picName", pictureName);
            rpc.addProperty("UP_PERSON", UpPerson);
            rpc.addProperty("OBJID_SPA", objId);
            rpc.addProperty("remark", remark);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 多张照片添加(应急指挥专用)
     <pictureName>string</pictureName>
     < UpPerson>stringy</ UpPerson>
     < objId>string</ objId>
     <remark>string</remark>
     */
    //pictureName=string&UpPerson=string&objId=string&remark=string
    //  WebUpLoadFilePictures
    public String SysPicAddYJ(String objId,String UpPerson,String pictureName) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="AddAGPicture";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("SId",objId );
            rpc.addProperty("SName", UpPerson);
            rpc.addProperty("pictureName",pictureName);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.PicAddYJ,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 多张照片下载
     <id>string</id>
     < functionName>string</ functionName>
     < fileName>string</fileName>
     */
    public String SysDownLoadPic(String id,String functionName,String fileName) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="WebDownLoad";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("id", id);
            rpc.addProperty("functionName",functionName);
            rpc.addProperty("fileName", fileName);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.UPLOAD,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取稳定状态
     * @return
     * @throws java.io.IOException
     */
    public String WebGetDZ_WDNAME() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetDZ_WDNAME";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取地质灾害类型
     * @return
     * @throws java.io.IOException
     */
    public String WebGetTB_DZZHTYPE() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_DZZHTYPE";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取地址灾害规模
     * @return
     * @throws java.io.IOException
     */
    public String WebGetTB_SCALE() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_SCALE";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //根据坐标定位乡镇 webGetXZByXY

    public String WebGetXZByXY(String x,String y) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetXZByXY";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("x", x);
            rpc.addProperty("y", y);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //根据坐标定位乡镇 webGetXZByXY及生成任务编号
    public String WebGetXZByXY2(String x,String y) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetXZByXY2";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("x", x);
            rpc.addProperty("y", y);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据坐标定位村社 webGetCSByXY
    public String WebGetCSByXY(String x,String y) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetCSByXY";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("x", x);
            rpc.addProperty("y", y);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //查询地址灾害点 webGetTB_FIELDSINDEX
    public String WebGetTB_FIELDSINDEX(String xz) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_FIELDSINDEX";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("xz", xz);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //查询地质灾害点巡查时间及次数
    public String WebDZZHGettimeandcs(String dzptbh) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webDZZHGettimeandcs";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("dzptbh", dzptbh);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询矿产资源
    public String WebGetMinieral(String kcmc,String kclx,String ssz) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetMinieral";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("kcmc", kcmc);
            rpc.addProperty("kclx", kclx);
            rpc.addProperty("ssz", ssz);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String WebAddMinieral(String mineid, String minename, String minetype,String man,
                                 String tel, String bexz, String becun,
                                 String minevol, String minearea, String mineclzt,
                                 String inlaw, String handletime, String handleremarks
            ,String xy, String address) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="webAddMinieral";
        SoapPrimitive detail;

        try {

            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("mineid", mineid);
            rpc.addProperty("minename", minename);
            rpc.addProperty("minetype", minetype);
            rpc.addProperty("man", man);
            rpc.addProperty("tel", tel);
            rpc.addProperty("bexz", bexz);
            rpc.addProperty("becun", becun);
            rpc.addProperty("minevol", minevol);
            rpc.addProperty("minearea", minearea);
            rpc.addProperty("mineclzt", mineclzt);
            rpc.addProperty("inlaw", inlaw);
            rpc.addProperty("handletime", handletime);
            rpc.addProperty("handleremarks", handleremarks);
            rpc.addProperty("xy", xy);
            rpc.addProperty("address", address);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //上传照片和视频
    /**
     <ckbh>string</ckbh>
     <vedio>base64Binary</vedio>
     <vedioname>string</vedioname>
     <picture>base64Binary</picture>
     <picname>string</picname>
     */
    public String WebUploadVediosPictures(String ckbh, String vedio, String vedioname,String picture,
                                          String picname) throws IOException {
        String rsltStr="";
        String METHOD_NAME ="webUploadVediosPictures";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("ckbh", ckbh);
            rpc.addProperty("vedio", vedio);
            rpc.addProperty("vedioname", vedioname);
            rpc.addProperty("picture", picture);
            rpc.addProperty("picname", picname);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL,600*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //获取开采类型
    public String WebGetKCLX() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetKCLX";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取处理状态
    public String WebGetTB_MINECLZT() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_MINECLZT";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //获取村信息
    public String WebGetTB_CUN() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_CUN";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取乡镇信息
    public String WebGetTB_XZ() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_XZ";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取乡镇名称和编号
    public String WebGetXZName() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "GetXZName";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询矿产资源
    public String WebGetLandPosition(String kcbh) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandPosition";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("CKBH", kcbh);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据面坐标得到面所在的村、镇、面积： webGetAreaAddress(105.28275 27.30648;105.28481 27.26804;105.30919 27.26956;)
    public String WebGetAreaAddress(String coords) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetAreaAddress";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", coords);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.KCURL,15*1000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String WebGetBHIAreaZhenCun(String coords) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetBHIAreaZhenCun";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", coords);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.SBYDURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取报批批次
    public String WebGettb_jsydbp() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGettb_jsydbp";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.SBYDURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //首先验证是否符合规划，在调用添加方法；地块是否符合规划; getCalSFFHGH(地块坐标,编号，面积，镇、村)
    /**
     *
     * @param XY
     * @param bh
     * @param dkmj
     * @param xz
     * @param cun
     * @return
     * @throws java.io.IOException
     */
    public String WebGetCalSFFHGH(String XY,String bh,String dkmj,String xz,String cun) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetCalSFFHGH";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", XY);
            rpc.addProperty("bh", bh);
            rpc.addProperty("dkmj", dkmj);
            rpc.addProperty("xz", xz);
            rpc.addProperty("cun", cun);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.SBYDURL);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //新增上报用地; webAddLandReported(地块坐标,编号，面积，镇、村,报批批次，补偿费用，地上附着物，报送时间)
    public String WebAddLandReported(String XY,String bh,String dkmj,String xz,String cun,
                                     String tdzsmc,String zdbcfy,String dsfzwbcf,String bssj) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webAddLandReported";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", XY);
            rpc.addProperty("bh", bh);
            rpc.addProperty("mj", dkmj);
            rpc.addProperty("xz", xz);
            rpc.addProperty("cun", cun);
            rpc.addProperty("tdzsmc", tdzsmc);
            rpc.addProperty("zdbcfy", zdbcfy);
            rpc.addProperty("dsfzwbcf", dsfzwbcf);
            rpc.addProperty("bssj", bssj);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.SBYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //报批用地查询; webGetLandReported()
    public String WebGetLandReported(String year,String zhen,String cun) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandReported";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("year", year);
            rpc.addProperty("zhen", zhen);
            rpc.addProperty("cun", cun);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.SBYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //上报土地多边形坐标 <bh>string</bh>
    public String WebGetLandPosition(String bh,String tdzsmc) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandPosition";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("bh", bh);
            rpc.addProperty("tdzsmc", tdzsmc);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.SBYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //添加批准用地
    public String WebAddPZYD(String XY,String bh,String xz,String cun,String dkmj,String tdzsmc,String tdzswh,
                             String pzsj,String lggydjsj,String zdbcfy,String sdfzwbcf,String sfnrzfcb) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webAddPZYD";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("bh", bh);
            rpc.addProperty("tdzsmc", tdzsmc);
            rpc.addProperty("XY", XY);
            rpc.addProperty("xz", xz);
            rpc.addProperty("cun", cun);
            rpc.addProperty("dkmj", dkmj);
            rpc.addProperty("tdzswh", tdzswh);
            rpc.addProperty("pzsj", pzsj);
            rpc.addProperty("lggydjsj", lggydjsj);
            rpc.addProperty("zdbcfy", zdbcfy);
            rpc.addProperty("sdfzwbcf", sdfzwbcf);
            rpc.addProperty("sfnrzfcb", sfnrzfcb);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;

            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.PZYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询批准用地
    public String WebGetLandRatify(String pznf,String tdzsmc,String dz) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandRatify";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("tdzsmc", tdzsmc);
            rpc.addProperty("pznf", pznf);
            rpc.addProperty("dz", dz);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.PZYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //批准用地坐标
    public String PZYDWebGetLandPosition(String bh,String tdzsmc) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandPosition";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("tdzsmc", tdzsmc);
            rpc.addProperty("bh", bh);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.PZYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //添加供应用地
    /**
     <XY>string</XY>
     <gyydId>string</gyydId>
     <gyydArea>string</gyydArea>
     <rjl>string</rjl>
     <jsmd>string</jsmd>
     <jsmd>string</lhbl>
     <cjMoney>string</cjMoney>
     <gdpfName>string</gdpfName>
     <gdpfCode>string</gdpfCode>
     <gdpfTime>string</gdpfTime>
     <tdyt>string</tdyt>
     <htId>string</htId>
     <gyfs>string</gyfs>
     <yddwName>string</yddwName>
     <ydxmName>string</ydxmName>
     <htqdTime>string</htqdTime>
     <htydJDTime>string</htydJDTime>
     <htydDGTime>string</htydDGTime>
     <htydJGTime>string</htydJGTime>
     <SJJDSJ>string</SJJDSJ>
     <SQKGSJ>string</SQKGSJ>
     <SJKGSJ>string</SJKGSJ>
     <SJJGSJ>string</SJJGSJ>
     <JGHYSJ>string</JGHYSJ>
     <JGHYQK>string</JGHYQK>
     */

    //添加供应用地
    public String WebAddLandProvision(String XY,String gyydId,String gyydArea,String rjl,String jsmd,
                                      String cjMoney,String gdpfName,String gdpfCode,String gdpfTime,
                                      String tdyt,String htId,String gyfs,String yddwName,
                                      String ydxmName,String htqdTime,String htydJDTime,String htydDGTime,
                                      String htydJGTime,String SJJDSJ,String SQKGSJ,String SJKGSJ,
                                      String SJJGSJ,String JGHYSJ,String JGHYQK) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webAddLandProvision";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", XY);
            rpc.addProperty("gyydId", gyydId);
            rpc.addProperty("gyydArea", gyydArea);
            rpc.addProperty("rjl", rjl);
            rpc.addProperty("jsmd", jsmd);
            rpc.addProperty("cjMoney", cjMoney);
            rpc.addProperty("gdpfName", gdpfName);
            rpc.addProperty("gdpfCode", gdpfCode);
            rpc.addProperty("gdpfTime", gdpfTime);
            rpc.addProperty("tdyt", tdyt);
            rpc.addProperty("htId", htId);
            rpc.addProperty("gyfs", gyfs);
            rpc.addProperty("yddwName", yddwName);
            rpc.addProperty("ydxmName", ydxmName);
            rpc.addProperty("htqdTime", htqdTime);
            rpc.addProperty("htydJDTime", htydJDTime);
            rpc.addProperty("htydDGTime", htydDGTime);
            rpc.addProperty("htydJGTime", htydJGTime);
            rpc.addProperty("SJJDSJ", SJJDSJ);
            rpc.addProperty("SQKGSJ", SQKGSJ);
            rpc.addProperty("SJKGSJ", SJKGSJ);
            rpc.addProperty("SJJGSJ", SJJGSJ);
            rpc.addProperty("JGHYSJ", JGHYSJ);
            rpc.addProperty("JGHYQK", JGHYQK);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.GYYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //获取批准用地面积，编号，合同编号
    public String WebGetBHIArea(String XY) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetBHIArea";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", XY);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.GYYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //供应方式
    public String WebGetJSYDGDFS() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetJSYDGDFS";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.GYYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //土地用途
    public String WebGetTB_LANDUSE() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTB_LANDUSE";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.GYYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  <year>string</year>
     <pfmc>string</pfmc>
     <cun>string</cun>
     */
    public String WebGetLandProvision(String year,String pfmc,String cun) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandProvision";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("year", year);
            rpc.addProperty("pfmc", pfmc);
            rpc.addProperty("cun", cun);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.GYYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取供应用地坐标

    /**
     *
     *  <bh>string</bh>
     <GDPFMC>string</GDPFMC>
     */

    public String WebGYYDGetLandPosition(String bh,String GDPFMC) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandPosition";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("bh", bh);
            rpc.addProperty("GDPFMC", GDPFMC);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.GYYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //查询批准用地; webGetLandReserve(年,村,征收名称)<cbnf>string</cbnf>

    public String WebGetLandReserve(String cbnf,String tdzsmc,String cun) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandReserve";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("cbnf", cbnf);
            rpc.addProperty("tdzsmc", tdzsmc);
            rpc.addProperty("cun", cun);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.CBYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //地块定位:webGetLandPosition(地块编号, 征收名称)
    // <bh>string</bh> <tdzsmc>string</tdzsmc>

    public String WebCBYDGetLandPosition(String bh,String tdzsmc) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandPosition";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("bh", bh);
            rpc.addProperty("tdzsmc", tdzsmc);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.CBYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //储备用地村
    public String WebGetLandReserveCun() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetLandReserveCun";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.CBYDURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取任务状态
    public String WebGetTaskState() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTaskState";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //任务类型
    public String WebGetTaskType() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetTaskType";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //查询任务巡查
    /**
     * <pid>string</pid>
     <rwxz>string</rwxz>
     <rwlx>string</rwlx>
     <zt>string</zt>
     <rwrq>string</rwrq>年
     */
    public String WebGetInspectionMission(String pid,String rwxz,String rwlx,String zt,String rwrq) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetInspectionMission";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("pid",pid);
            rpc.addProperty("rwxz",rwxz);
            rpc.addProperty("rwlx",rwlx);
            rpc.addProperty("zt",zt);
            rpc.addProperty("rwrq",rwrq);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//地质灾害查询任务
    /**
     * <pid>string</pid>

     */
    public String WebGetInspectionMission2(String pid) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetInspectionMission2";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("pid",pid);
//            rpc.addProperty("rwxz",rwxz);
//            rpc.addProperty("rwlx",rwlx);
//            rpc.addProperty("zt",zt);
//            rpc.addProperty("rwrq",rwrq);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *修改任务(包括地质灾害任务、矿产、决策)
     * <bh>string</bh>
     <receiverid>string</receiverid>
     <rq>string</rq>
     <content>string</content>
     <files>string</files>
     */
    public String WebUpdateInspectionMission(String bh,String receiverid,String content,String files) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webUpdateInspectionMission2";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("bh",bh);
            rpc.addProperty("receiverid",receiverid);
//            rpc.addProperty("rq",rq);
            rpc.addProperty("content",content);
            rpc.addProperty("files",files);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *新建任务(包括地质灾害任务、矿产、决策)
     * <bh>string</bh>
     <receiverid>string</receiverid>
     <rq>string</rq>
     <content>string</content>
     <files>string</files>
     */
    public String webAddInspectionMission2(String receiverid,String rwbh,String rwlx,String rwnr,String resultcontent,String photoname,String x,String y) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webAddInspectionMission2";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("reciverids",receiverid);
            rpc.addProperty("bh",rwbh);
            rpc.addProperty("rwlx",rwlx);
            rpc.addProperty("rwnr",rwnr);
            rpc.addProperty("resultcontent",resultcontent);
            rpc.addProperty("rwfiles",photoname);
            rpc.addProperty("x",x);
            rpc.addProperty("y",y);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//    /**
//     *
//     新建任务
//     */
//public String webAddInspectionMission2(String receiverid,String rwbh,String rwlx,String rwnr,String resultcontent,String photoname,String x,String y) throws IOException {
//
//    String rsltStr="";
//    String METHOD_NAME = "webAddInspectionMission2";
//    SoapPrimitive detail;
//
//    try {
//        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
//        rpc.addProperty("receiverids");
//        rpc.addProperty("receiverid",receiverid);
////            rpc.addProperty("rq",rq);
//        rpc.addProperty("content",content);
//        rpc.addProperty("files",files);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.bodyOut = rpc;
//        envelope.dotNet = true;
//        envelope.setOutputSoapObject(rpc);
//
//        HttpTransportSE ht = new HttpTransportSE(ConstantVar.XCRWURL,300000);
//
//        ht.debug = true;
//        ht.call(NAMESPACE+METHOD_NAME, envelope);
//        detail = (SoapPrimitive) envelope.getResponse();
//        if (detail != null) {
//            rsltStr=detail.toString();
//            return rsltStr;
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//    return null;
//}



    /**
     *  <year>string</year>
     <zt>string</>
     <xz>string</xz>
     查询卫片任务; webGetWeiChipTask(年,状态，乡镇)
     */
    public String WebGetWeiChipTask(String year,String zt,String xz) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetWeiChipTask";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("year",year);
            rpc.addProperty("zt",zt);
            rpc.addProperty("xz",xz);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.WPZFURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //卫片任务状态
    public String WebGetWeiChipState() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetWeiChipState";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.WPZFURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //http://192.168.1.155:8083/server/WebDisasterTask.asmx/
    /**
     *weichipid
     * @return
     * @throws java.io.IOException
     */
    public String WebGetWeiChip(String weichipid) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetWeiChip";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("weichipid",weichipid);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.WPZFURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //巡查上报
    /**
     *<userid>string</userid>
     <x>float</x>
     <y>float</y>
     <remark>string</remark>
     addTB_YDXC
     GPS巡查路径上报
     */

    public String AddTB_YDXC(String userid,String x,String y,String remark,String type) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "addTB_YDXC2";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("userid",userid);
            rpc.addProperty("x",x);
            rpc.addProperty("y",y);
            rpc.addProperty("remark",remark);
            rpc.addProperty("type",type);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.LOGINURL,300*1000);

            ht.debug = true;
            ht.call(NAMESPACE + METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //地灾预警任务
    public String GetDzzhyjdata() throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetDisasterTask";
        SoapPrimitive detail;
        Log.i("net", "getData");
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
//            rpc.addProperty("zhmc", name);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHYJ,300000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //修改地址应急救援
    public String ChangeDzzhyjdata(DZZHYJEntity entity) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webUpdateDisasterTask";
        SoapPrimitive detail;
        Log.i("net", "getData");
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("xxwz", entity.getXXWZ());
            rpc.addProperty("id", entity.getID());
            rpc.addProperty("zhmc",entity.getZHMC());
            rpc.addProperty("zhdj",entity.getZHDJ());
            rpc.addProperty("bjrq",entity.getBJRQ());
            rpc.addProperty("bz",entity.getBZ());
            rpc.addProperty("images",entity.getIMAGES());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHYJ,300000);
            //     ht.getPath();
            //   Log.i("msg",ht.getPath());
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                Log.i("data",rsltStr);
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String upImageDzzhyjdata(DZZHYJImageEntity Entity){
        String uploadBuffer = null;
        String result=null;
        try {

            FileInputStream fis = new FileInputStream(Entity.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count = 0;
            while((count = fis.read(buffer)) >= 0){
                baos.write(buffer, 0, count);
            }
            uploadBuffer = new String(Base64.encode(baos.toByteArray()));  //进行Base64编码
            result=ConnectWebService(Entity,uploadBuffer);

            Log.i("connectWebService", "start");
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.i("result",result);
        return result;
    }
    //修改地址应急救援
    public byte[] DownDzzhyjdata(String id,String name) throws IOException {

        String rsltStr="";
        byte[] result;
        String METHOD_NAME = "webGetDisasterImageByte";
        byte[] detail;
        Log.i("net", "getData");
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("id",id);
            rpc.addProperty("imagename", name);
            Log.i("msg",id+"|"+name);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHYJ,300000);
            //     ht.getPath();
            //   Log.i("msg",ht.getPath());
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            ArrayList<byte[]> byteLis=new ArrayList<>();
            byteLis.add(android.util.Base64.decode(response.toString(), android.util.Base64.DEFAULT));
            return android.util.Base64.decode(response.toString(),  android.util.Base64.DEFAULT);//compress(byteLis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String ConnectWebService(DZZHYJImageEntity bean,String imgBuffer) {
        String METHOD_NAME = "webAddDisasterTaskImage";
        //以下就是 调用过程了，不明白的话 请看相关webservice文档
        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
        soapObject.addProperty("imagename",bean.getName());
        soapObject.addProperty("byteimage",imgBuffer);
        Log.i("image",imgBuffer.toString());
        soapObject.addProperty("id",bean.getId());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE httpTranstation = new HttpTransportSE(ConstantVar.DZZHYJ);
        try {
            httpTranstation.call(NAMESPACE+METHOD_NAME, envelope);
            Object result = envelope.getResponse();
            Log.i("connectWebService", result.toString());
            return  result.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }



    //完善应急值守报警信息
    public String AddYJZSdata(YJSPXXEntity entity) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webAddEmgDutyWarn";
        SoapPrimitive detail;
        Log.i("net", "getData");
        try {
            /// <param name="X">double经度</param>
            /// <param name="Y">double纬度</param>
            /// <param name="di_type">灾害类型</param>
            /// <param name="di_add">地址</param>
            /// <param name="di_Casualty">伤亡人数</param>
            /// <param name="di_EconomicLoss">直接损失</param>
            /// <param name="di_Relocate">转移人数</param>
            /// <param name="di_IndirectLoss">间接损失</param>
            /// <param name="remark">备注</param>
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("X", entity.getX());
            rpc.addProperty("Y", entity.getY());
            rpc.addProperty("di_type", entity.getDi_type());
            rpc.addProperty("di_add", entity.getDi_add());
            rpc.addProperty("di_Casualty", entity.getDi_Casualty());
            rpc.addProperty("di_EconomicLoss", entity.getDi_EconomicLoss());
            rpc.addProperty("di_Relocate", entity.getDi_Relocate());
            rpc.addProperty("di_IndirectLoss", entity.getDi_IndirectLoss());
            rpc.addProperty("remark",entity.getRemark());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(ConstantVar.YJZS,300000);
            //  http://172.71.0.3:8083/server/WebEmgDutyWarn.asmx
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                Log.i("data",rsltStr);
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* webGetEmgDutyWarn
     查询完善应急值守报警信息*/
    public String GetDzYJZSdata(String name,String time,String address) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webGetEmgDutyWarn";
        SoapPrimitive detail;
        Log.i("net", "getData");
        try {
            /// <param name="dz">乡镇；下拉框选择及手动填写(选填)</param>
            /// <param name="zhlx">灾害类型，可手动填写，默认(小型、中型、大型)(选填)</param>
            /// <param name="time">日期yyyy-mm-dd(选填)</param>
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("dz", address);
            rpc.addProperty("zhlx", name);
            rpc.addProperty("time", time);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(ConstantVar.YJZS,300000);
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                Log.i("data",rsltStr);
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*    webUpdateDisaster
    修改灾害信息*/
//完善应急值守报警信息
    public String UpdateYJZSdata(YJSPXXSEntity entity) throws IOException {

        String rsltStr="";
        String METHOD_NAME = "webUpdateDisaster";
        SoapPrimitive detail;
        Log.i("net", "getData");
        try {
            /// <summary>
            /// 修改灾害信息
            /// </summary>
            /// <param name="id">编号</param>
            /// <param name="di_Add">地址</param>
            /// <param name="di_Type">灾害类型</param>
            /// <param name="di_Casualty">伤亡人数</param>
            /// <param name="di_EconomicLoss">直接损失</param>
            /// <param name="di_Relocate">转移人数</param>
            /// <param name="di_IndirectLoss">间接损失</param>
            /// <param name="remark">备注</param>
            /// <returns></returns>
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
      /*  rpc.addProperty("X", entity.getX());
        rpc.addProperty("Y", entity.getDi_type());*/
            rpc.addProperty("di_Type", entity.getDi_type());
            rpc.addProperty("di_Add", entity.getDi_Add());
            rpc.addProperty("di_Casualty", entity.getDi_Casualty());
            rpc.addProperty("di_EconomicLoss", entity.getDi_EconomicLoss());
            rpc.addProperty("di_Relocate", entity.getDi_Relocate());
            rpc.addProperty("di_IndirectLoss", entity.getDi_IndirectLoss());
            rpc.addProperty("id",entity.getId());
            rpc.addProperty("remark",entity.getRemark());;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(ConstantVar.YJZS,300000);
            //  http://172.71.0.3:8083/server/WebEmgDutyWarn.asmx
            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                Log.i("data",rsltStr);
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static final byte[] compress(ArrayList<byte[]> byteList) {
        if (byteList == null)
            return null;

        byte[] compressed = null;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;

        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            for (int i=0;i<byteList.size();i++) {
                zout.putNextEntry(new ZipEntry("" + i));
                ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out1);
                oos.writeObject(byteList.get(i));

                byte[] bytes = out1.toByteArray();//图片一大于2M就报内存溢出

                //不使用ByteArrayOutputStream的话，zout报内存溢出
                zout.write(bytes);
                zout.closeEntry();
            }
            compressed = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            compressed = null;
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return compressed;
    }

    //占压分析  WebLandDecisionAnalysis.asmx(地块坐标,编号，面积，镇、村,报批批次，补偿费用，地上附着物，报送时间)
    public String WebLandDecisionAnalysis(String XY,String bh,String mj,String xz,String cun,String tdzsmc,String zdbcfy,String dsfzwbcf,String bssj
    )throws IOException
    {
        String rsltStr="";
        String METHOD_NAME = "LandDecisionAnalysis";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("XY", XY);
            rpc.addProperty("bh", bh);
            rpc.addProperty("mj", mj);
            rpc.addProperty("xz", xz);
            rpc.addProperty("cun", cun);
            rpc.addProperty("tdzsmc", tdzsmc);
            rpc.addProperty("zdbcfy", zdbcfy);
            rpc.addProperty("dsfzwbcf", dsfzwbcf);
            rpc.addProperty("bssj", bssj);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.ZYFXURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载巡查人员表
     * @return 返回所有人员的记录，使用JSON
     * @throws IOException
     */
    public String WebDownloadXCRYTable()throws IOException
    {
        String NoValue="NoValue";

        String rsltStr="";
        String METHOD_NAME = "WebDOwnloadXCRYTable";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("NoValue", NoValue);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载巡查人员表
     * @return 返回所有人员的记录，使用JSON
     * @throws IOException
     */
    public String WebDownloadXCRecords(String BH)throws IOException
    {
        String rsltStr="";
        String METHOD_NAME = "WebQueryXCRecords";
        SoapPrimitive detail;

        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("BH", BH);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(ConstantVar.DZZHQUERYURL,300000);

            ht.debug = true;
            ht.call(NAMESPACE+METHOD_NAME, envelope);
            detail = (SoapPrimitive) envelope.getResponse();
            if (detail != null) {
                rsltStr=detail.toString();
                return rsltStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}