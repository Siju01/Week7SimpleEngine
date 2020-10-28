package com.pens.week7simpleengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleGameEngineWeek7 extends Activity {

    GameView gameView;

    int Xmax;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView (this);
        setContentView(gameView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        width = size.x;
        height = size.y;
        Xmax = getResources().getDisplayMetrics().widthPixels;

    }

    class GameView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing;

        Canvas canvas;
        Paint paint;

        long fps;

        Bitmap bitmapGurita;

        boolean isMoving = false;

        float walkSpeedPerSecond = 150;

        float guritaXPosition = 10;

        public GameView (Context context){

            super(context);
            ourHolder = getHolder();
            paint = new Paint();

            bitmapGurita = BitmapFactory.decodeResource(this.getResources(),R.drawable.gurita);
            playing = true;

        }

        @Override
        public void run() {
            while (playing) {

                long startFrameTime = System.currentTimeMillis();

                update();

                draw();

                long timeThisFrame = System.currentTimeMillis() - startFrameTime;

                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public  void update (){
            if (isMoving){
                if (guritaXPosition > Xmax - 100 || guritaXPosition < 0){
                    walkSpeedPerSecond = - walkSpeedPerSecond;
                }
                guritaXPosition = guritaXPosition + (walkSpeedPerSecond/fps);
            }

        }

        public void draw (){
            if (ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(255,26,128,172));

                paint.setColor(Color.argb(255,249,129,0));

                paint.setTextSize(50);

                canvas.drawText("FPS : ", 20,40,paint) ;

                canvas.drawBitmap (bitmapGurita,guritaXPosition,300,paint);

                ourHolder.unlockCanvasAndPost(canvas);

            }
        }
        public void pause(){

            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e){
                Log.e ("Error:","joining thread");
            }
        }
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    isMoving = true;

                    break;

                case MotionEvent.ACTION_UP:

                    isMoving = false;

                    break;

            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();

        gameView.pause();
    }
}
