package allenw3u.swimmer.Http;

import android.util.Log;

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

public class HttpAssist {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000;
    private static final String CHARSET = "utf-8";
    private static final String SUCCESS = "1";
    private static final String FAILURE = "0";

    public static String uploadFile(File file){
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";
        String RequestURl = "http://119.23.9.146/swim/Uploadfile";

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
            conn.setRequestProperty("Charset",CHARSET);
            conn.setRequestProperty("connection","keep-alive");
            conn.setRequestProperty("Content-Type",CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file != null){
                /**
                 * 当文件不为空时才连接输出流
                 */
                OutputStream outputStream = conn.getOutputStream();
                //输出流，由内存到网络端点
                DataOutputStream dos = new DataOutputStream(outputStream);
                //DataOutputStream and DataInputStream give us the power to write and read primitive data type to a media such as file.
                StringBuilder sb = new StringBuilder();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);


                sb.append("Content-Disposition: form-data; name=\"accData\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream inputStream = new FileInputStream(file);
                //FileInputStream is meant for reading streams of raw bytes such as image data.
                /**
                 * DataInputStream是数据输入流，读取的是java的基本数据类型。
                 * FileInputStream是从文件系统中，读取的单位是字节。
                 * FileReader 是从文件中，读取的单位是字符
                 */
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bytes)) != -1){
                    dos.write(bytes, 0 , len);
                }
                inputStream.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    Log.e(TAG,"request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sbResponse = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1){
                        sbResponse.append((char)ss);
                    }
                    result = sbResponse.toString();
                    Log.i(TAG,"result :" + result);
                    return result;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }
}
