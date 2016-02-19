package com.example.e26612.controller2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

class MyView extends View{  //画面左側のタッチパネル

    /* 青色の円の座標 */
    private float Xpos, Ypos;
    /* 変数定義 */
    private int point_move;

    //Viewの初期化
    public MyView(Context context) {
        super(context);
        Xpos = 450;     //X座標の初期設定
        Ypos = 1070;    //Y座標の初期設定
        point_move = 0;

    }

    //グラフ表示実行メソッド
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景色の設定
        Paint set_paint = new Paint();
        canvas.drawColor(Color.BLACK);

        // 円座標の表示・・・白色
        set_paint.setColor(Color.WHITE);
        set_paint.setTextSize(30f);
        canvas.drawCircle(450, 800, 370, set_paint);

        // 円座標の表示・・・青色
        set_paint.setColor(Color.BLUE);
        if (point_move == 1) Movepoint();       //指が離れたときに
        canvas.drawCircle(Xpos, Ypos, 100, set_paint);

        //タッチ座標の表示
        set_paint.setAntiAlias(true);
        set_paint.setTextSize(100f);
        set_paint.setColor(Color.CYAN);
        canvas.drawText("X = " + ((Xpos - 450)), 150, 340, set_paint);      //表示するときX座標の初期設定を0とするため((Xpos - 450))の計算をしている
        if(Ypos - 1070 == 0) canvas.drawText("Y = 0.0", 150, 420, set_paint);
        else canvas.drawText("Y = " + ((Ypos - 1070)*-1), 150, 420, set_paint);  //表示するときX座標の初期設定を0とするため((Ypos - 1070)*-1)の計算をしている

        //再描画
        invalidate();
    }

    //タッチイベント処理
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: //タッチした場合
                Xpos = event.getX();
                Ypos = event.getY();
                Collision();
                point_move = 0;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: // タッチ離れた場合
                point_move = 1;
                break;

            case MotionEvent.ACTION_MOVE: // タッチ移動した場合
                Xpos = event.getX();
                Ypos = event.getY();
                Collision();
                point_move = 0;
                break;
        }
        return true;
    }

    //青色の円を中央ラインに移動させるメゾット
    public void Movepoint() {
        if (Xpos != 450) {              //青色の円が初期位置にいないときに
            if (Xpos > 500) {           //青色の円が右側にいるとき
                Xpos -= 20;
            } else if (Xpos < 400) {   //青色の円が左側にいるとき
                Xpos += 20;
            }
        }
    }

    //青色の円が白色の円を超えないようにするためのメゾット
    public void Collision() {
        while (true) {
            float xDistance = Xpos - 450;                       // 青色の円の横と白色の円の横の距離の差
            float yDistance = Ypos - 800;                       // 青色の円の縦と白色の円の縦の距離の差
            double distance = Math.sqrt(Math.pow(xDistance, 2)  // 青色の円の中心と白色の円の中心の距離
                    + Math.pow(yDistance, 2));

            if (distance > 370) {                               //青色の円と白色の円の距離が一定値以上外に出たとき
                if (Xpos < 450 && Ypos < 800) {                //青色の円が白色の円の左上外側のとき
                    Xpos += 10;
                    Ypos += 10;
                } else if (Xpos > 450 && Ypos < 800) {        //青色の円が白色の円の右上外側のとき
                    Xpos -= 10;
                    Ypos += 10;
                } else if (Xpos < 450 && Ypos > 800) {        //青色の円が白色の円の左下外側のとき
                    Xpos += 10;
                    Ypos -= 10;
                } else if (Xpos > 450 && Ypos > 800) {       //青色の円が白色の円の右下外側のとき
                    Xpos -= 10;
                    Ypos -= 10;
                }
            }
            else break;
        }
    }

    //ロボットにX座標の値を送るための用意をするメゾット
    public float Motor_Xpos() {
        float x = 0;
        x = (Xpos - 450);
        return x;
    }

    //ロボットにY座標の値を送るための用意をするメゾット
    public float Motor_Ypos(){
        float y = 0;
        y = (Ypos - 1070)*-1;
        return y;
    }
}