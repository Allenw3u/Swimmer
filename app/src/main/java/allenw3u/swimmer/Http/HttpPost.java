package allenw3u.swimmer.Http;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Allenw3u on 2017/7/17.
 */

public class HttpPost {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000;
    private static final String CHARSET = "utf-8";
    private static final String SUCCESS = "1";
    private static final String FAILURE = "0";

    public static String postTest(){
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";
        String RequestURl = "http://119.23.9.146:8080/swim/Uploadfile/";
        String result = null;
        try {
            URL url = new URL(RequestURl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);//允许输入流,输入流为外存（或网络端点等）到内存
            conn.setDoOutput(true);//允许输出流，输出流为内存到外存（或网络端点等）
            //To upload data to a web server, configure the connection for output using setDoOutput(true).
            conn.setUseCaches(false);//不允许使用缓存
            conn.setRequestMethod("POST");//请求方式
            //conn.setRequestProperty("Charset",CHARSET);
            //conn.setRequestProperty("Connection","Keep-Alive");
            //conn.setRequestProperty("Content-Type",CONTENT_TYPE + ";boundary=" + BOUNDARY);

            //如果跑出了一个IO异常，则获得异常流
            InputStream inputStream = null;
            try{
                inputStream = conn.getInputStream();
            }catch (IOException e){
                inputStream = conn.getErrorStream();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            result = baos.toString("UTF-8");
            Log.i(TAG,result);
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "1";
        } catch (IOException e) {
            e.printStackTrace();
            return "2";
        }
    }
}