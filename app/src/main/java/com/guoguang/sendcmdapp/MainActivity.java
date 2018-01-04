package com.guoguang.sendcmdapp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private SerialHelper comprint;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            result.setText(data);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView) findViewById(R.id.receive);

        comprint = new SerialHelper("/dev/ttyS1", 115200);

        String str = "1D02";
        comprint.openPort();
        comprint.send(CHexConver.hexStringToBytes(str));
        result.setText("已发送命令："+str);
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            String str = "abcd12345678911";
            comprint.openPort();
            comprint.send(str.getBytes());
        }
    }

}
