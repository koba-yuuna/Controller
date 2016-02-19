package com.example.e26612.controller2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

class MyView2 extends View {    //画面右側のタッチパネル

    /* 青色の円の座標 */
    private float Xpos2, Ypos2;
    /* 変数定義 */
    private int point_move2;


    //Viewの初期化
    public MyView2(Context context) {
        super(context);
        Xpos2 = 850;        //X座標の初期設定
        Ypos2 = 800;        //Y座標の初期設定
        point_move2 = 0;
    }

    //グラフ表示実行メソッド
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //背景色の設定
        Paint set_paint2 = new Paint();
        canvas.drawColor(Color.BLACK);

        //円座標の表示・・・白色
        set_paint2.setColor(Color.WHITE);
        set_paint2.setTextSize(30f);
        canvas.drawCircle(850, 800, 370, set_paint2);

        // 円座標の表示・・・青色
        set_paint2.setColor(Color.BLUE);
        if (point_move2 == 1) Movepoint2();       //指が離れたときに
        canvas.drawCircle(Xpos2, Ypos2, 100, set_paint2);

        //タッチ座標の表示
        set_paint2.setAntiAlias(true);
        set_paint2.setTextSize(100f);
        set_paint2.setColor(Color.CYAN);
        canvas.drawText("X = " + ((Xpos2 - 850)), 550, 340, set_paint2);    //表示するときX座標の初期設定を0とするため((Xpos2 - 850))の計算をしている
        if(Ypos2 - 800 == 0) canvas.drawText("Y = 0.0", 550, 420, set_paint2);
        else canvas.drawText("Y = " + ((Ypos2 - 800) * -1), 550, 420, set_paint2); //表示するときX座標の初期設定を0とするため((Ypos2 - 800)*-1)の計算をしている

        //再描画
        invalidate();
    }

    //タッチイベント処理
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: //タッチした場合
                Xpos2 = event.getX();
                Ypos2 = event.getY();
                Collision2();
                point_move2 = 0;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: // タッチ離れた場合
                point_move2 = 1;
                break;

            case MotionEvent.ACTION_MOVE: // タッチ移動した場合
                Xpos2 = event.getX();
                Ypos2 = event.getY();
                Collision2();
                point_move2 = 0;
                break;
        }
        return true;
    }

    //青色の円を中央に移動させるメゾット
    public void Movepoint2() {
        Xpos2 = 850;
        Ypos2 = 800;
    }

    //青色の円が白色の円を超えないようにするためのメゾット
    public void Collision2() {

        while (true) {

            float xDistance = Xpos2 - 850;                      //青色の円の横と白色の円の横の距離の差
            float yDistance = Ypos2 - 800;                      //青色の円の縦と白色の円の縦の距離の差
            double distance = Math.sqrt(Math.pow(xDistance, 2)  // 青色の円の中心と白色の円の中心の距離
                    + Math.pow(yDistance, 2));

            if (distance > 370) {                               //青色の円と白色の円の距離が一定値以上外に出たとき
                if (Xpos2 < 850 && Ypos2 < 800) {              //青色の円が白色の円の左上外側のとき
                    Xpos2 += 10;
                    Ypos2 += 10;
                } else if (Xpos2 > 850 && Ypos2 < 800) {        //青色の円が白色の円の右上外側のとき
                    Xpos2 -= 10;
                    Ypos2 += 10;
                } else if (Xpos2 < 850 && Ypos2 > 800) {        //青色の円が白色の円の左下外側のとき
                    Xpos2 += 10;
                    Ypos2 -= 10;
                } else if (Xpos2 > 850 && Ypos2 > 800) {        //青色の円が白色の円の右下外側のとき
                    Xpos2 -= 10;
                    Ypos2 -= 10;
                }
            } else break;
        }

    }

    //ロボットにX座標の値を送るための用意をするメゾット
    public float Motor_Xpos2() {
        float x = 0;
        x = (Xpos2 - 850);
        return x;
    }

    //ロボットにY座標の値を送るための用意をするメゾット
    public float Motor_Ypos2() {
        float y = 0;
        y = (Ypos2 - 800) * -1;
        return y;
    }
}