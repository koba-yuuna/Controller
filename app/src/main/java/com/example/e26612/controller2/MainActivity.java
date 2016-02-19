package com.example.e26612.controller2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  implements Runnable, View.OnClickListener{

    //　クラス変数　GUI定数の宣言
    private static MyView graph;
    private static MyView2 graph2;
    private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(1280, WC);
    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(WC, WC);

    //変数定義
    /* Bluetooth Adapter */
    private BluetoothAdapter adapter;
    /* Bluetoothデバイス */
    private BluetoothDevice device;
    /* Bluetooth UUID */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /* デバイス名 */
    private final String DEVICE_NAME = "RNBT-6331";
    /* Soket */
    private BluetoothSocket socket;
    /* Thread */
    private Thread thread;
    /* 接続ボタン.*/
    private Button connectButton;
    /* Bluetoothから受信した値 */
    private TextView mInputTextView;;
    /* Connect確認用フラグ */
    private boolean connectFlg = false;
    /* BluetoothのOutputStream */
    OutputStream output = null;
    /* 左側青い円のXとYの座標  */
    private float Xpos = 0;
    private float Ypos = 0;
    /* 右側青い円のXとYの座標  */
    private float Xpos2 = 0;
    private float Ypos2 = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        LinearLayout layout2 = new LinearLayout(this);
        layout2.setOrientation(LinearLayout.HORIZONTAL);

        connectButton = new Button(this);
        connectButton.setText("接続開始");
        connectButton.setOnClickListener(this);
        layout.addView(connectButton);

        mInputTextView = new TextView(this);
        layout.addView(mInputTextView);

        // Bluetoothのデバイス名を取得
        // デバイス名は、RNBT-XXXXになるため、
        // DVICE_NAMEでデバイス名を定義
        adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            if (device.getName().equals(DEVICE_NAME)) {
                mInputTextView.setText("find: " + device.getName());
                device = device;
            }
        }

        //　グラフ表示領域指定
        graph = new MyView(this);
        layout2.addView(graph,params1);
        graph2 = new MyView2(this);
        layout2.addView(graph2,params2);
        layout.addView(layout2);
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            socket.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            // 取得したデバイス名を使ってBluetoothでSocket接続
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();

            //データ送る準備
            output = socket.getOutputStream();

            //フラグを立てる
            connectFlg = true;

            //接続が状況の表示
            Message valueMsg = new Message();
            valueMsg.obj = "接続完了";
            mHandler.sendMessage(valueMsg);

            while(true){
                if (true) {
                    //XとYの座標獲得
                    Xpos = graph.Motor_Xpos();
                    Ypos = graph.Motor_Ypos();

                    if(Xpos < 0) Xpos *= -1;    //マイナスをプラスに変更
                    if(Ypos < 0) Ypos = 0;      //マイナスは0に変更

                    //桁ごとに値を分ける　(intに変換し、小数点以下を切り捨てる)
                    int Xpos_100 = (int)Xpos/100;   //百の位
                    int Xpos_10  = (int)Xpos/10;    //十の位
                    int Xpos_1   = (int)Xpos%10;    //一の位

                    int Ypos_100 = (int)Ypos/100;   //百の位
                    int Ypos_10  = (int)Ypos/10;    //十の位
                    int Ypos_1   = (int)Ypos%10;    //一の位

                    //文字列に変更　(文字列でないとデータを送れない)
                    String xpos_100 = String.valueOf(Xpos_100);
                    String xpos_10 = String.valueOf(Xpos_10);
                    String xpos_1 = String.valueOf(Xpos_1);

                    String ypos_100 = String.valueOf(Ypos_100);
                    String ypos_10 = String.valueOf(Ypos_10);
                    String ypos_1 = String.valueOf(Ypos_1);

                    //送るデータを表示
                    String xypos = xpos_100 + xpos_10 + xpos_1 +"　"+ ypos_100 + ypos_10 + ypos_1;
                    valueMsg = new Message();
                    valueMsg.obj = xypos;
                    mHandler.sendMessage(valueMsg);

                    //Bluetoothでデータを送信
                    try {
                        output.write(xpos_100.getBytes());
                        output.write(xpos_10.getBytes());
                        output.write(xpos_1.getBytes());
                        output.write(ypos_100.getBytes());
                        output.write(ypos_10.getBytes());
                        output.write(ypos_1.getBytes());
                    } catch (IOException e) {}
                }
            }

        } catch (Exception e) {

            try {
                socket.close();
            } catch (Exception ee) {
            }

            //フラグを下げる
            connectFlg = false;

            //接続が状況の表示
            Message valueMsg = new Message();
            valueMsg.obj = "接続失敗";
            mHandler.sendMessage(valueMsg);
        }

    }

    @Override
    //接続開始ボタンが押されたとき
    public void onClick(View v) {
        if (v.equals(connectButton)) {
            //接続が状況の表示
            Message valueMsg = new Message();
            valueMsg.obj = "接続中";
            mHandler.sendMessage(valueMsg);

            // 接続されていない場合のみ
            if (!connectFlg) {
                thread = new Thread(this);
                thread.start();
            }
        }
    }

    //Handerで接続状況を表示
   Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String msgStr = (String)msg.obj;
            mInputTextView.setText(msgStr);
        }
    };
}