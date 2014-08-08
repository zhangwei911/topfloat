package com.apkstory.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.apkstory.R;
import com.apkstory.com.vichild.file.ListExplorer;
import com.apkstory.util.MyApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TopFloatVideoView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.webfloat, null);
        createView();
    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();
        String value = intent.getStringExtra("data");
        webView.loadUrl(value);
        wm.addView(view, wmParams);
        //模拟HOME键
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent1);
        super.onStart();
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
        webView.setWebChromeClient(new MyWebChromeClient());
        closefloat_button = (Button) view.findViewById(R.id.closefloat_button);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setAllowFileAccess(true);

        webSettings.setBuiltInZoomControls(true);

        webSettings.setDisplayZoomControls(false);

        webSettings.setUseWideViewPort(true);

        webSettings.setLoadWithOverviewMode(true);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webSettings.setPluginState(WebSettings.PluginState.ON);

        // webView.setInitialScale(35);
        closefloat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.pauseTimers();
                webView.stopLoading();
                webView.loadData("<a></a>", "text/html", "utf-8");
                finish();
            }
        });
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

            new AlertDialog.Builder(TopFloatVideoView.this).setTitle("AlertDialog").setMessage(message)

                    .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            result.confirm();
                        }
                    }).setCancelable(false).show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(TopFloatVideoView.this).setTitle("ConfirmDialog").setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    result.confirm();
                }
            })

                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            result.confirm();
                        }
                    }).setCancelable(false).show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

            final LayoutInflater factory = LayoutInflater.from(TopFloatVideoView.this);


            final View dialogView = factory.inflate(R.layout.prompt_view, null);


            ((TextView) dialogView.findViewById(R.id.text)).setText(message);


            ((EditText) dialogView.findViewById(R.id.edit)).setText(defaultValue);

            new AlertDialog.Builder(TopFloatVideoView.this).setTitle("PromptDialog")

                    .setView(dialogView).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    String value = ((EditText) dialogView.findViewById(R.id.edit)).getText().toString();
                    result.confirm();
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    result.confirm();
                }
            }).show();
            return true;
        }
    }


    private void updateViewPosition() {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        wmParams.x = (int) (x - mTouchStartX - 60);
        wmParams.y = (int) (y - mTouchStartY - 50);
        if (wmParams.x > dm.widthPixels - 120) {
            wmParams.x = dm.widthPixels - 120;
            if (wmParams.y > dm.heightPixels - 120) {
                wmParams.y = dm.heightPixels - 120;
            } else if (wmParams.y < 0) {
                wmParams.y = 0;
            } else {
                wmParams.y = (int) (y - mTouchStartY - 50);
            }
        } else if (wmParams.x < -dm.widthPixels + 120) {
            wmParams.x = -dm.widthPixels + 120;
            if (wmParams.y > dm.heightPixels - 120) {
                wmParams.y = dm.heightPixels - 120;
            } else if (wmParams.y < 0) {
                wmParams.y = 0;
            } else {
                wmParams.y = (int) (y - mTouchStartY - 50);
            }
        } else if (wmParams.y > dm.heightPixels - 120) {
            wmParams.y = dm.heightPixels - 120;
            if (wmParams.x > dm.widthPixels - 120) {
                wmParams.x = dm.widthPixels - 120;
            } else if (wmParams.x < -dm.widthPixels + 120) {
                wmParams.x = -dm.widthPixels + 120;
            } else {
                wmParams.x = (int) (x - mTouchStartX - 60);
            }
        } else if (wmParams.y < 0) {
            wmParams.x = (int) (x - mTouchStartX - 60);
            wmParams.y = 0;
        }
        wm.updateViewLayout(view, wmParams);
    }

    boolean flag = false;
    Thread videoplaytime;
    int videoTimeSet = 0;

    public void doJob() {
        videoplaytime = new Thread() {
            public void run() {
                while (flag == true) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    videoTimeSet++;
                }
            }

        };
        videoplaytime.start();
    }

    /**
     * 格式化时间
     *
     * @param dur-8*60*60*1000初始化时间为00:00:00
     * @return
     */
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
    Date date = new Date();

    public String formatTime(int dur) {
        date.setTime(dur - 8 * 60 * 60 * 1000);
        return simpleDateFormat.format(date);
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
                Toast.makeText(TopFloatVideoView.this, "zoomin", Toast.LENGTH_SHORT).show();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right
                Log.i("GestureDemo", "zoomout");
                Toast.makeText(TopFloatVideoView.this, "zoomout", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    WindowManager wm = null;
    WindowManager.LayoutParams wmParams = null;
    View view;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    WebView webView;
    Button closefloat_button;
}
