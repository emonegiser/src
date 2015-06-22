package com.arcgis.httputil;

import android.os.Environment;

import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnUtils  {
	
	public static String download(String urlStr) throws TimeoutException {
		
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			//超时设置，单位毫秒
			urlConn.setConnectTimeout(15000);
			urlConn.setReadTimeout(15000);
			//在获取输入流时就进行编码
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"utf-8"));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();

		} catch (Exception te) {
			te.printStackTrace();
		}finally {
			try {
				if(buffer!=null){
					buffer.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String DownUpdateFile(String mUrl){
		try{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				
				String sdpath = Environment.getExternalStorageDirectory() + "/";
				String mSavePath = sdpath + "mobilemap";
				
				URL url = new URL(mUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				conn.setConnectTimeout(5000);
				InputStream is = conn.getInputStream();

				File file = new File(mSavePath,"update.xml");
				
				if (!file.exists()){
					file.createNewFile();
				}
		
				FileOutputStream fos = new FileOutputStream(file);
				byte buf[] = new byte[2048];
				int numread = -1;
				while ((numread = is.read(buf)) != -1) {
					fos.write(buf, 0, numread);
				}
				
				fos.close();
				is.close();
				return 1+"";
			}
		}catch (MalformedURLException e){
			e.printStackTrace();
			return 0+"";
		}catch (IOException e){
			e.printStackTrace();
			return 0+"";
		}
		return null;
	}
	
	public static String getImageFromURL(String imgurl){        
		try {
			URL url = new URL(imgurl);
	        BufferedInputStream bis = new BufferedInputStream(url.openStream());
	        byte[] bytes = new byte[128];	        
	        String outFilePath=HttpConnUtils.getSdCacheDir();
	        String fileName=imgurl.substring(imgurl.lastIndexOf("/")+1);
	        String yuntuPath=outFilePath+fileName;
	        File outFile=new File(yuntuPath);
	        if(!outFile.exists()){
	        	//创建文件，不是创建文件夹(outFile.mkdirs())
	        	outFile.createNewFile();	        	
	        }
	        OutputStream bos = new FileOutputStream(outFile);
	        int len;
	        while ((len = bis.read(bytes)) > 0) {
	            bos.write(bytes, 0, len);
	        }
	        bis.close();
	        bos.flush();
	        bos.close();
	        return yuntuPath;		
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	private static String getSdCacheDir() {
	     if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	         File fExternalStorageDirectory = Environment.getExternalStorageDirectory();
	         File autonaviDir = new File(fExternalStorageDirectory, "mobilemap");
	         if (!autonaviDir.exists()) {
	             autonaviDir.mkdir();
	         }
	         File mapDir = new File(autonaviDir, "map");
	         if (!mapDir.exists()) {
	             mapDir.mkdir();
	         }
	         return mapDir.toString() + "/";
	     } else {
	         return null;
	     }
	}
	
	

}
