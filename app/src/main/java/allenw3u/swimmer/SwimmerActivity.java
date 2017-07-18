package allenw3u.swimmer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import allenw3u.swimmer.Http.HttpAssist;


/**
 * Created by Allenw3u on 2017/5/25.
 */

public class SwimmerActivity extends AppCompatActivity implements SensorEventListener {
    //创建常量，把纳秒转换为毫秒。
    private static final float NS2MS = 1.0f/1000000f;

    private Button mStartButton;
    private Button mStopButton;
    private Button mReportButton;
    private SensorManager sensorManager;

    //记录线性加速度传感器数据条目序号
    private int acc_id;

    //将acc_txt,gyro_txt作为全局变量，每次调用Sensor都能够访问
    private StringBuilder acc_txt;

    //两两采样点之间的时间间隔
    private float dT;

    //定义加速度传感器和陀螺仪第一次调用时间
    private long accStartTime;

    //定义文件输出时的系统时间
    private String mtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_swimmer);

        mStartButton = (Button)findViewById(R.id.start_button);
        mStopButton = (Button)findViewById(R.id.stop_button);
        mReportButton = (Button)findViewById(R.id.report_button);

        //获取系统的传感器管理服务
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //分别记录加速度传感器和陀螺仪数据条目的StringBuilder容器
        acc_txt = new StringBuilder();

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册加速度传感器
                sensorManager.registerListener(SwimmerActivity.this,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),20000);

                //记录加速度期间只有开始按钮可见
                mStopButton.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
                mReportButton.setVisibility(View.INVISIBLE);

                acc_id = 0;
                accStartTime = 0;
                acc_txt.setLength(0);
                mtime = null;
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消注册加速度传感器
                sensorManager.unregisterListener(SwimmerActivity.this);
                //点击结束按钮后，开始按钮和报告按钮都可见
                mReportButton.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.VISIBLE);
                mStopButton.setVisibility(View.INVISIBLE);
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //先实现输出文件到存储器的功能
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                    Date curTime = new Date(System.currentTimeMillis());
                    mtime = formatter.format(curTime);
                    //输出流 第一个参数为文件名
                    FileOutputStream out;
                    out = new FileOutputStream("/sdcard/accelarator/" + mtime + "Acc.txt");
                    String acc_String = acc_txt.toString();
                    //把内容转为字节类型
                    byte[] acc_message = acc_String.getBytes();
                    out.write(acc_message);
                    out.close();
                    //提示toast
                    Toast.makeText(SwimmerActivity.this,"file"+" has created",Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    Log.e("e",e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //实现上传文件
                File file = new File("/sdcard/accelarator/" + mtime + "Acc.txt");
                if (file != null)
                {
                    String request = HttpAssist.uploadFile(file);
                    Toast.makeText(SwimmerActivity.this,request,Toast.LENGTH_LONG).show();
                }
            }
        });

    /*
     * The simplest incarnation of the DenseInstance constructor will only
     * take a double array as argument an will create an instance with given
     * values as attributes and no class value set. For unsupervised machine
     * learning techniques this is probably the most convenient constructor.
     */
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
        acc_txt.setLength(0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (acc_id == 0){
            dT = 0;
            //创建静态终态变量，传感器初始时间戳
            accStartTime = event.timestamp;
        }else {
            //获得传感器数据时间轴数据（距离第一个点的时间数据）
            dT = (event.timestamp - accStartTime)*NS2MS;
        }
        //accTimeStamp = event.timestamp;
        //将每次采集的加速度速度append到StringBuilder中
        acc_txt.append(acc_id+" ,"+dT+" ,"+values[0]+ " ,"+ values[1]+" ,"+values[2]+"\n");
        acc_id++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
