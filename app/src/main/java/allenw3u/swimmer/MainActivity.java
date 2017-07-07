/**
 * 本程序包含
 * 1、创建本地文件
 * 2、读取加速度传感器并获取数据
 * Test
 */
package allenw3u.swimmer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup Button of developer mode to open InfoActivity
        Button developer_button = (Button)findViewById(R.id.developer_button);
        developer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2developer = new Intent(MainActivity.this,InfoActivity.class);
                startActivity(intent2developer);
            }
        });

        //Setup Button of swimmer mode to open SwimmerActivity
        Button swimmer_button = (Button)findViewById(R.id.swimmer_button);
        swimmer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2swimmer = new Intent(MainActivity.this,SwimmerActivity.class);
                startActivity(intent2swimmer);
            }
        });
    }
}
