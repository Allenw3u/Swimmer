package allenw3u.swimmer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.TableOrder;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Allenw3u on 2017/5/25.
 */

public class SensorActivity extends AppCompatActivity implements SensorEventListener{

    //创建常量，把纳秒转换为毫秒。
    private static final float NS2MS = 1.0f/1000000f;
    //coefficient of low pass filter
    private static final float A = 0.1f;
    //coefficient of inverse low pass filter
    private static final float ALPHA = 0.8f;
    private static final int MAX_SERIES_SIZE = 100;
    //xyplot刷新时间
    private static final int CHART_REFRESH = 50;


    private SensorManager sensorManager;
    //开始记录按钮
    private ImageView startButtonView;
    //停止记录按钮
    private ImageView stopButtonView;
    //提示文本框组件
    private TextView hintText;
    //输出文件按钮
    private Button button;
    //重置按钮
    private Button resetButton;
    //XPlot View
    private XYPlot xyPlot;
    //acc data TextView
    private TextView accText;

    //记录线性加速度传感器数据条目序号
    private int acc_id;
    //记录陀螺仪传感器数据条目序号
    private int gyro_id;
    //将acc_txt,gyro_txt作为全局变量，每次调用Sensor都能够访问
    private StringBuilder acc_txt;
    private StringBuilder gyro_txt;
    //记录每次SensorChanged的SensorEvent的时间戳
    //private long accTimeStamp;
    //private long gyroTimeStamp;
    //两两采样点之间的时间间隔
    private float dT;
    //定义加速度传感器和陀螺仪第一次调用时间
    private long accStartTime;
    private long gyroStartTime;
    //记录图像上一次加载时间
    private long lastChartRefresh;

    //xyz序列对象
    private SimpleXYSeries xAxisSeries;
    private SimpleXYSeries yAxisSeries;
    private SimpleXYSeries zAxisSeries;
    private SimpleXYSeries accelerationSeries;

    private LineAndPointFormatter redFormat;
    private LineAndPointFormatter blueFormat;
    private LineAndPointFormatter greenFormat;
    private LineAndPointFormatter blackFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        //Receive data from InfoActivity : username,swimstyle,lapdistance
        Intent dataIntend = getIntent();
        final String Infodata[] = dataIntend.getStringArrayExtra("infodata");

        //获取系统的传感器管理服务
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //实例化acc_text
        //分别记录加速度传感器和陀螺仪数据条目的StringBuilder容器
        acc_txt = new StringBuilder();
        gyro_txt = new StringBuilder();

        //设置xyplot参数
        xyPlot = (XYPlot)findViewById(R.id.XYPlot);
        xyPlot.setDomainLabel("Elapsed Time (ms)");
        xyPlot.setRangeLabel("Acceleration (m/sec^2)");
        xyPlot.setBorderPaint(null);
        xyPlot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);
        xyPlot.getGraph().setLineLabelEdges(XYGraphWidget.Edge.LEFT);
        xyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 2);
        xyPlot.getGraph().getLineLabelInsets().setLeft(PixelUtils.dpToPix(10));
        xyPlot.getLegend().setTableModel(new DynamicTableModel(4, 1, TableOrder.COLUMN_MAJOR));

        xAxisSeries = new SimpleXYSeries("X Axis");
        yAxisSeries = new SimpleXYSeries("Y Axis");
        zAxisSeries = new SimpleXYSeries("Z Axis");
        accelerationSeries = new SimpleXYSeries("Acceleration");
        redFormat = new LineAndPointFormatter(Color.RED,null,null,null);
        blueFormat = new LineAndPointFormatter(Color.BLUE,null,null,null);
        greenFormat = new LineAndPointFormatter(Color.GREEN,null,null,null);
        blackFormat = new LineAndPointFormatter(Color.BLACK,null,null,null);

        accText = (TextView)findViewById(R.id.accText);
        //获取程序界面上的提示文本框组件
        hintText = (TextView)findViewById(R.id.hintText) ;
        hintText.setText("点击开始记录数据");
        //获取程序界面上的按钮组件，能够创建txt文件到本地路径
        button = (Button)findViewById(R.id.outputFileButton) ;
        resetButton = (Button)findViewById(R.id.resetButton) ;
        //获取程序界面上的图标按钮组件
        startButtonView = (ImageView)findViewById(R.id.startButton) ;
        //为开始图标按钮注册监听器，并为系统加速度传感器注册监听器
        startButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //目前采样频率无法完美自定义，并且采样存在频率不稳定等情况
                sensorManager.registerListener(SensorActivity.this,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),20000);
                sensorManager.registerListener(SensorActivity.this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),20000);//时间为微秒单位
                hintText.setText("正在记录数据");
                xAxisSeries.clear();
                yAxisSeries.clear();
                zAxisSeries.clear();
                accelerationSeries.clear();
                acc_txt.setLength(0);
                gyro_txt.setLength(0);
                //初始化加速度传感器和陀螺仪数据条目序号，初始化为0
                acc_id = 0;
                gyro_id = 0;
                accStartTime = 0;
                gyroStartTime = 0;

                button.setEnabled(false);
                resetButton.setEnabled(false);
            }
        });

        stopButtonView = (ImageView) findViewById(R.id.stopButton);
        //点击结束图标按钮停止注册加速度传感器监听器
        stopButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(SensorActivity.this);
                hintText.setText("点击开始记录数据");

                button.setEnabled(true);
                resetButton.setEnabled(true);
            }
        });

        //为按钮注册监听器
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //输出流 第一个参数为文件名
                    FileOutputStream out;
                    out = new FileOutputStream("/sdcard/accelarator/"+Infodata[0]+Infodata[1]+Infodata[2]+"Acc.txt");
                    String acc_String = acc_txt.toString();
                    //把内容转为字节类型
                    byte[] acc_message = acc_String.getBytes();
                    out.write(acc_message);
                    out.close();

                    out = new FileOutputStream("/sdcard/accelarator/"+Infodata[0]+Infodata[1]+Infodata[2]+"Gyro.txt");
                    String grco_String = gyro_txt.toString();
                    //把内容转为字节类型
                    byte[] gyro_message = grco_String.getBytes();
                    out.write(gyro_message);
                    out.close();
                    Toast.makeText(SensorActivity.this,Infodata[0]+Infodata[1]+Infodata[2]+"Acc.txt"+"/"
                            +Infodata[0]+Infodata[1]+Infodata[2]+"Gyro.txt"+" has created",Toast.LENGTH_LONG).show();



                } catch (FileNotFoundException e) {
                    Log.e("e",e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //为重置按钮注册监听器
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acc_txt.setLength(0);
                gyro_txt.setLength(0);
                xyPlot.clear();
                xAxisSeries.clear();
                yAxisSeries.clear();
                zAxisSeries.clear();
                accelerationSeries.clear();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消注册监听器
        sensorManager.unregisterListener(this);
        hintText.setText("点击开始记录数据");
        //重置传感器数据序号，初始化为0
    }

    //当传感器的值发生改变时回调该方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        //计算总加速度
        double sumOfSquares = (values[0] * values[0])
                + (values[1] * values[1])
                + (values[2] * values[2]);
        double acceleration = Math.sqrt(sumOfSquares);
        //获取触发event传感器类型
        int sensorType = event.sensor.getType();
        StringBuilder sb = null;
        switch (sensorType)
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                //若为第一个数据条目，dt为0
                if (acc_id == 0){
                    dT = 0;
                    //创建静态终态变量，传感器初始时间戳
                    accStartTime = event.timestamp;
                    lastChartRefresh = 0;
                }else {
                    //获得传感器数据时间轴数据（距离第一个点的时间数据）
                    dT = (event.timestamp - accStartTime)*NS2MS;
                }
                //accTimeStamp = event.timestamp;
                sb = new StringBuilder();
                sb.append("间隔时间： ");
                sb.append(dT+"ms");
                sb.append("\nX方向上的加速度： ");
                sb.append(values[0]);
                sb.append("\nY方向上的加速度： ");
                sb.append(values[1]);
                sb.append("\nZ方向上的加速度： ");
                sb.append(values[2]);
                accText.setText(sb.toString());
                //将每次采集的加速度速度append到StringBuilder中
                acc_txt.append(acc_id+" ,"+dT+" ,"+values[0]+ " ,"+ values[1]+" ,"+values[2]+"\n");
                acc_id++;

                /**
                 * Add data to XYPlot and show
                 */
                if(xyPlot != null){
                    long current = SystemClock.uptimeMillis();
                    // Limit how much the chart gets updated
                    if ((current - lastChartRefresh) >=  CHART_REFRESH )
                    {

                        //Plot data
                        addDataPoint(xAxisSeries,dT,values[0]);
                        addDataPoint(yAxisSeries,dT,values[1]);
                        addDataPoint(zAxisSeries,dT,values[2]);
                        addDataPoint(accelerationSeries,dT,acceleration);

                        xyPlot.addSeries(xAxisSeries, redFormat);
                        xyPlot.addSeries(yAxisSeries, greenFormat);
                        xyPlot.addSeries(zAxisSeries, blueFormat);
                        xyPlot.addSeries(accelerationSeries, blackFormat);

                        xyPlot.redraw();

                        lastChartRefresh = current;

                    }
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                //若为第一个数据条目，dt为0
                if (gyro_id == 0){
                    dT = 0;
                    gyroStartTime = event.timestamp;
                }else {
                    dT = (event.timestamp - gyroStartTime)*NS2MS;
                }
                //gyroTimeStamp = event.timestamp;
                sb = new StringBuilder();
                sb.append("间隔时间: ");
                sb.append(dT);
                sb.append("\n绕X轴旋转的角速度： ");
                sb.append(values[0]);
                sb.append("\n绕Y轴旋转的角速度： ");
                sb.append(values[1]);
                sb.append("\n绕Z轴旋转的角速度： ");
                sb.append(values[2]);
                //将每次采集的陀螺仪数据append到StringBuilder中
                gyro_txt.append(gyro_id+" ,"+dT+" ,"+values[0]+ " ,"+ values[1]+" ,"+values[2]+"\n");
                gyro_id++;
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
    * simple low-pass filter,
    * current means new data from sensor
    * lass means last data with filter
    */
    float[] lowPass(float currentX, float currentY, float currentZ,
                  float lastX, float lastY, float lastZ)
    {
        float filterValues[] = new float[3];
        filterValues[0] = lastX * (1.0f - A ) + currentX * A;
        filterValues[1] = lastY * (1.0f - A ) + currentY * A;
        filterValues[2] = lastZ * (1.0f - A ) + currentZ * A;
        return filterValues;
    }

    //Inverse Low-pass-filter
    private float[] inverseLowPass(float x, float y, float z){

        float[] filterValues = new float[3];
        float[] hightValues = new float[3];

        hightValues[0] = ALPHA * hightValues[0] + (1 - ALPHA) * x;
        hightValues[1] = ALPHA * hightValues[1] + (1 - ALPHA) * x;
        hightValues[2] = ALPHA * hightValues[2] + (1 - ALPHA) * x;

        filterValues[0] = filterValues[0] - hightValues[0];
        filterValues[1] = filterValues[1] - hightValues[1];
        filterValues[2] = filterValues[2] - hightValues[2];

        return filterValues;
    }


    //Simple Moving Average,k阶平滑滤波器
    //TODO

    //添加数据点
    private void addDataPoint(SimpleXYSeries series, Number timestamp, Number value)
    {
        if (series.size() == MAX_SERIES_SIZE)
        {
            series.removeFirst();
        }
        series.addLast(timestamp, value);
    }


}


/**
 * 注释掉原有代码，原代码实现app创建后立即注册加速度传感器监听器，并在app生命周期onStop后取消注册
 @Override
 protected void onResume() {
 super.onResume();
 //为系统的加速度传感器注册监听器，
 //采样频率为50HZ，每个采样点间隔时间为20000us（0.02s）
 sensorManager.registerListener(MainActivity.this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),20000);

 }

 @Override
 protected void onStop() {
 //取消注册
 sensorManager.unregisterListener(this);
 super.onStop();
 }
 */

/**
 * 显示当前北京时间的文本框
 * 删除不必要代码与功能
 *
 //获取程序界面上的时间戳文本框组件
 timestampText = (TextView)findViewById(R.id.systemTime);
 //new TimeThread().start();//启动新的线程，获取北京时间更新
class TimeThread extends Thread{
    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
                Message msg = new Message();
                msg.what = 1;//消息（一个整型值）
                mHandler.sendMessage(msg);//每隔1秒发送一个msg给mHandler
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }while (true);
    }
}
    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    long sysTime = System.currentTimeMillis();
                    Date date = new Date(sysTime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEE");
                    timestampText.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }
    };
 */
