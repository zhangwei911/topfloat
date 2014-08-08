package com.apkstory.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.apkstory.R;
import com.apkstory.util.MyApplication;

public class TopFloatService2 extends Service {

    WindowManager wm = null;
    WindowManager.LayoutParams wmParams = null;
    View view;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    WebView webView;
    Button closefloat_button;

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.webfloat, null);
        createView();

        Intent intent=new Intent();
        String value = intent.getStringExtra("data");
        webView.loadUrl(value);
        wm.addView(view, wmParams);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    private void createView() {

        wm = (WindowManager) getApplicationContext().getSystemService("window");

        wmParams = ((MyApplication) getApplication()).getMywmParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        wmParams.format = PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明

        wmParams.x = 0;
        wmParams.y = 0;

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = PixelFormat.RGBA_8888;


        view.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
//Toast.makeText(TopFloatService.this,"click",Toast.LENGTH_SHORT).show();
                if (event.getPointerCount() == 3) {
                    wm.removeView(view);
                }
                x = event.getRawX();

                y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return false;
            }

        });

        webView = (WebView) view.findViewById(R.id.webView);
        closefloat_button = (Button) view.findViewById(R.id.closefloat_button);
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setAllowFileAccess(true);

        settings.setBuiltInZoomControls(true);

        settings.setDisplayZoomControls(false);

        settings.setUseWideViewPort(true);

        settings.setLoadWithOverviewMode(true);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        settings.setLoadsImagesAutomatically(true);
        settings.setAllowContentAccess(false);

        // disable content url access
        settings.setAllowContentAccess(false);

        // HTML5 API flags
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);

        settings.setPluginState(WebSettings.PluginState.ON);//设置adobe插件可用


       // webView.setInitialScale(35);
        closefloat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.pauseTimers();
                webView.stopLoading();
                webView.loadData("<a></a>", "text/html", "utf-8");
                wm.removeView(view);
            }
        });
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void updateViewPosition() {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        wmParams.x = (int) (x - mTouchStartX - 60);
        wmParams.y = (int) (y - mTouchStartY - 50);
        if (wmParams.x > dm.widthPixels - 100 || wmParams.x < -dm.widthPixels + 100 || wmParams.y > dm.heightPixels - 100 || wmParams.y < 0) {

        } else {
            wm.updateViewLayout(view, wmParams);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private GestureDetector gd = new GestureDetector(new GestureDetector.OnGestureListener() {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //left
                Log.i("GestureDemo", "zoomin");
                Toast.makeText(TopFloatService2.this, "zoomin", Toast.LENGTH_SHORT).show();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right
                Log.i("GestureDemo", "zoomout");
                Toast.makeText(TopFloatService2.this, "zoomout", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    });

    @Override
    public void onDestroy() {
        wm.removeView(view);
        super.onDestroy();
    }
}
