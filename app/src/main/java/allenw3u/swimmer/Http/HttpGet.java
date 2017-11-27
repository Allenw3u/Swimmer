package allenw3u.swimmer.Http;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Allen on 2017/7/21.
 */

public class HttpGet {
    private static final String TAG = "HttpGetTest";
    private static final String FAILURE = "FAILURE";

    public static String getTest(){
        String testurl = "http://119.23.9.146:8080/swim/Uploadfile";
        String result = null;
        try{
            URL url = new URL(testurl);
            HttpURLConnection connTest = (HttpURLConnection)url.openConnection();
            connTest.setRequestMethod("GET");
            connTest.setUseCaches(false);
            connTest.setDoInput(true);
            InputStream is = connTest.getInputStream();
            ByteArrayOutputStream resultTest = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                resultTest.write(buffer, 0, length);
            }
            result = resultTest.toString("UTF-8");
            Log.i(TAG,result);
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }
}
