package com.apkstory.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.apkstory.R;
import com.apkstory.activity.FullScreen;
import com.apkstory.com.vichild.file.ListExplorer;
import com.apkstory.tools.Tools;
import com.apkstory.util.MyApplication;
import com.baidu.cyberplayer.sdk.BVideoView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TopFloatService_BaiDu extends Service implements BVideoView.OnPreparedListener, BVideoView.OnCompletionListener, BVideoView.OnErrorListener, BVideoView.OnInfoListener {

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.floating_baidu, null);
        initUI();
        createView();
        intent=new Intent();
        if (intent.getData() != null) {
            uriPath = intent.getData();
            videosUrl = intent.getStringArrayExtra("videoUrls");
            web = intent.getStringExtra("ctrl");
            video_name = intent.getStringExtra("video_name");
            if (null != uriPath) {
                String scheme = uriPath.getScheme();
                if (null != scheme && (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp"))) {
                    mVideoSource = uriPath.toString();
                    if (web.equals("web")) {
                        for (int i = 0; i < videosUrl.length; i++) {
                            if (mVideoSource.equals(videosUrl[i])) {
                                index = i;
                            }
                        }
                    }
                } else {
                    mVideoSource = uriPath.getPath();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            new onstartLongTimeTask().execute("");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    boolean flags = true;

    @Override
    public boolean onInfo(int i, int i2) {
        return false;
    }

    @Override
    public void onCompletion() {
        Log.v(TAG, "onCompletion");
        if (web.equals("web")) {
            index++;
            Log.e(TAG, "onCompletion_1");
            if (index < videosUrl.length) {
                Log.e(TAG, "onCompletion_2");
                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                mVideoSource = videosUrl[index];
                Log.e(TAG, "onCompletion  " + mVideoSource);
                // Tools.showMsg(VideoViewPlayingActivity.this,"onCompletion  "+mVideoSource);
                //设置播放源
                mVV.setVideoPath(mVideoSource);
                //开始播放
                mVV.start();
                Log.e(TAG, "onCompletion_3");
            }
        } else {
            Log.e(TAG, "onCompletion_4");
            mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        }
    }


    //播放状态
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

    @Override
    public boolean onError(int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared() {
        mVV.seekTo(curentrtime);
    }


    public class MyThread extends Thread {
        public void run() {
            while (flags) {
                video_play_Time++;
                Message msg = new Message();
                msg.what = REFRESH;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("viz.errors", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private class LongTimeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                //线程睡眠3秒，模拟耗时操作，这里面的内容Android系统会自动为你启动一个新的线程执行
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            //更新UI的操作，这里面的内容是在UI线程里面执行的
            if (list_imageButton.getVisibility() == View.VISIBLE) {
                buttonsh();
                if (!smallshow) {
                    small_toggleButton.setVisibility(View.INVISIBLE);
                }
            }
        }

    }

    private class onstartLongTimeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                lastvideo = false;
                mVV.setVideoPath(value);
                mVV.start();
                video_play_Time = curentrtime / 1000;
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == REFRESH) {
                            String temp = formatTime(video_play_Time * 1000);
                            playtime_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                            seekBar.setProgress(video_play_Time * 1000);
                        }
                        super.handleMessage(msg);
                    }
                };
                new MyThread().start();

                pause_imageButton.setVisibility(View.VISIBLE);
                list_imageButton.setVisibility(View.VISIBLE);
                next_imageButton.setVisibility(View.VISIBLE);
                exit_imageButton.setVisibility(View.VISIBLE);
                videoname_textView.setVisibility(View.VISIBLE);
                floatfull_button.setVisibility(View.VISIBLE);
                //开始执行AsyncTask，并传入某些数据
                new LongTimeTask().execute("New Text");
                seekBar.setMax(time);
                String temp = formatTime(time);
                wholetime_textView.setText("/" + String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                videoname_textView.setText(names[pathindex]);
                wm.addView(view, wmParams);
            } catch (Exception e) {
                pause_imageButton.callOnClick();
                pause_imageButton.setVisibility(View.INVISIBLE);
                Log.e("viz.errors.onStartCommand", e.getMessage());
            }
        }

    }

    public void buttonsh() {
        if (mVV.isPlaying()) {
            if (pause_imageButton.getVisibility() == View.VISIBLE) {
                pause_imageButton.setVisibility(View.INVISIBLE);
            } else {
                pause_imageButton.setVisibility(View.VISIBLE);
            }
        } else {
            if (play_imageButton.getVisibility() == View.VISIBLE) {
                play_imageButton.setVisibility(View.INVISIBLE);
            } else {
                play_imageButton.setVisibility(View.VISIBLE);
            }
        }
        if (pause_imageButton.getVisibility() == View.INVISIBLE || play_imageButton.getVisibility() == View.INVISIBLE) {
            exit_imageButton.setVisibility(View.INVISIBLE);
            list_imageButton.setVisibility(View.INVISIBLE);
            next_imageButton.setVisibility(View.INVISIBLE);
            videoname_textView.setVisibility(View.INVISIBLE);
            floatfull_button.setVisibility(View.INVISIBLE);
            small_toggleButton.setVisibility(View.INVISIBLE);
        } else {
            exit_imageButton.setVisibility(View.VISIBLE);
            list_imageButton.setVisibility(View.VISIBLE);
            next_imageButton.setVisibility(View.VISIBLE);
            videoname_textView.setVisibility(View.VISIBLE);
            floatfull_button.setVisibility(View.VISIBLE);
            small_toggleButton.setVisibility(View.VISIBLE);
            //开始执行AsyncTask，并传入某些数据
            new LongTimeTask().execute("New Text");
        }
    }

    private void createView() {

        wm = (WindowManager) getApplicationContext().getSystemService("window");

        if (((MyApplication) getApplication()) != null) {
            wmParams = ((MyApplication) getApplication()).getMywmParams();
        }
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
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
                    flags = false;
                    video_play_Time = 0;
                    wm.removeView(view);
                }
                x = event.getRawX();

                y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        touchtime1 = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        touchtime2 = System.currentTimeMillis();
                        if (touchtime2 - touchtime1 > 100) {
                            return true;
                        }
                        break;
                }

                return false;
            }

        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsh();
            }
        });

        exit_imageButton = (ImageButton) view.findViewById(R.id.float_exit_imageButton);
        pause_imageButton = (ImageButton) view.findViewById(R.id.float_pause_imageButton);
        play_imageButton = (ImageButton) view.findViewById(R.id.float_play_imageButton);
        list_imageButton = (ImageButton) view.findViewById(R.id.float_list_imageButton);
        next_imageButton = (ImageButton) view.findViewById(R.id.float_next_imageButton);
        videoname_textView = (TextView) view.findViewById(R.id.baidu_videoname_textView);
        floatfull_button = (Button) view.findViewById(R.id.baidu_floatfull_button);
        seekBar = (SeekBar) view.findViewById(R.id.float_seekBar);
        exit_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flags = false;
                video_play_Time = 0;
                stopSelf();
            }
        });
        pause_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVV.pause();
                flags = false;
                play_imageButton.setVisibility(View.VISIBLE);
                pause_imageButton.setVisibility(View.GONE);
            }
        });
        play_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastvideo) {
                    mVV.seekTo(0);
                    video_play_Time = 0;
                }
                mVV.start();
                flags = true;
                new MyThread().start();
                play_imageButton.setVisibility(View.GONE);
                pause_imageButton.setVisibility(View.VISIBLE);
            }
        });
        list_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flags = false;
                video_play_Time = 0;
                Intent intent = new Intent(TopFloatService_BaiDu.this, ListExplorer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pathcache", value.substring(0, value.lastIndexOf("/")));
                intent.putExtra("ctrl", "float");
                startActivity(intent);
                stopSelf();
            }
        });

        next_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathindex++;
                if (pathindex < paths.length) {
                    try {
                        Log.e("viz.errors", pathindex + "  " + (paths.length - 1));
                        mVV.setVideoPath(paths[pathindex]);
                        videoname_textView.setText(names[pathindex]);
                        mVV.start();
                        video_play_Time = 0;
                        mVV.seekTo(0);
                        //                    new MyThread().start();
                        seekBar.setProgress(0);
                    } catch (Exception e) {
                        flags = false;
                        video_play_Time = 0;
                        mVV.pause();
                        Tools.showMsg(TopFloatService_BaiDu.this, getString(R.string.ExceptionExit));
                        stopSelf();
                        Intent intent = new Intent(TopFloatService_BaiDu.this, ListExplorer.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("pathcache", value.substring(0, value.lastIndexOf("/")));
                        intent.putExtra("ctrl", "float");
                        startActivity(intent);
                        stopSelf();
                        Log.e("viz.errors", e.getMessage());
                    }
                } else {
                    flags = false;
                    stopSelf();
                    Intent intent = new Intent(TopFloatService_BaiDu.this, ListExplorer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("pathcache", value.substring(0, value.lastIndexOf("/")));
                    intent.putExtra("ctrl", "float");
                    startActivity(intent);
                    stopSelf();
                }
            }
        });

        floatfull_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopFloatService_BaiDu.this, FullScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("path", paths[pathindex]);
                intent.putExtra("ct", mVV.getCurrentPosition());
                intent.putExtra("paths", paths);
                intent.putExtra("names", names);
                intent.putExtra("pathindex", pathindex);
                startActivity(intent);
                stopSelf();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seek_check) {
                    String temp = formatTime(seekBar.getProgress());
                    seek_playtime_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seek_check = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mVV.seekTo(seekBar.getProgress());
                video_play_Time = seekBar.getProgress() / 1000;
                if (!mVV.isPlaying()) {
                    mVV.start();
                    flags = true;
                    new MyThread().start();
                    play_imageButton.setVisibility(View.GONE);
                    pause_imageButton.setVisibility(View.VISIBLE);
                }
                seek_playtime_textView.setText("");
                seek_check = false;
            }
        });

        playtime_textView = (TextView) view.findViewById(R.id.playtime_textView);
        wholetime_textView = (TextView) view.findViewById(R.id.wholetime_textView);
        seek_playtime_textView = (TextView) view.findViewById(R.id.seek_palytime_textView);

        small_toggleButton = (ToggleButton) view.findViewById(R.id.small_toggleButton);
        small_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    smallshow = true;
                    DisplayMetrics dm = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(dm);
                    wmParams.x = dm.widthPixels - 20;
                    wmParams.y = 25;
                    pause_imageButton.callOnClick();
                    hide();
                    sh(View.GONE);
                } else {
                    small_toggleButton.setVisibility(View.INVISIBLE);
                    smallshow = false;
                    play_imageButton.callOnClick();
                    sh(View.VISIBLE);
                    pause_imageButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        small_toggleButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (wholetime_textView.getVisibility() == View.GONE) {
                    x = event.getRawX();

                    y = event.getRawY();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mTouchStartX = event.getX();
                            mTouchStartY = event.getY();
                            touchtime1 = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            updateViewPosition();
                            break;
                        case MotionEvent.ACTION_UP:
                            updateViewPosition();
                            mTouchStartX = mTouchStartY = 0;
                            touchtime2 = System.currentTimeMillis();
                            if (touchtime2 - touchtime1 > 100) {
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }
//
//    private float spacing(MotionEvent event) {
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return FloatMath.sqrt(x * x + y * y);
//    }

    public void sh(int v) {
        mVV.setVisibility(v);
        seekBar.setVisibility(v);
        playtime_textView.setVisibility(v);
        wholetime_textView.setVisibility(v);
    }

    public void hide() {
        exit_imageButton.setVisibility(View.INVISIBLE);
        list_imageButton.setVisibility(View.INVISIBLE);
        next_imageButton.setVisibility(View.INVISIBLE);
        videoname_textView.setVisibility(View.INVISIBLE);
        play_imageButton.setVisibility(View.INVISIBLE);
        pause_imageButton.setVisibility(View.INVISIBLE);
        floatfull_button.setVisibility(View.INVISIBLE);
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


    /**
     * 格式化时间
     *
     * @param dur 视频getDuration（）
     * @return
     */
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
    Date date = new Date();

    public String formatTime(int dur) {
        date.setTime(dur);
        return simpleDateFormat.format(date);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("viz.errors", "onBind");
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
                Toast.makeText(TopFloatService_BaiDu.this, "zoomin", Toast.LENGTH_SHORT).show();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right
                Log.i("GestureDemo", "zoomout");
                Toast.makeText(TopFloatService_BaiDu.this, "zoomout", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    });

    @Override
    public void onDestroy() {
        flags = false;
        wm.removeView(view);
        Log.e("viz.errors", "onDestroy");
        super.onDestroy();
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        mViewHolder = (RelativeLayout) view.findViewById(R.id.float_view_holder);
        //创建BVideoView和BMediaController
        mVV = new BVideoView(this);
        mViewHolder.addView(mVV);

        //注册listener
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);

        //设置解码模式
        mVV.setDecodeMode(BVideoView.DECODE_SW);
    }

    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private View view;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x, x1, x2, oldLineDistance;
    private float y, y1, y2, newLineDistance;
    private ImageButton exit_imageButton, pause_imageButton, play_imageButton, list_imageButton, next_imageButton;
    private TextView videoname_textView, playtime_textView, wholetime_textView, seek_playtime_textView;
    private SeekBar seekBar;
    private boolean seek_check = false;
    private String[] paths = null, names = null;
    private int pathindex = 0, curentrtime = 0, time = 0;
    private long touchtime1 = 0, touchtime2 = 0;
    int video_play_Time = 0;
    private Handler mHandler = null;
    public static final int REFRESH = 0x000001;
    private String value = "";
    private Button floatfull_button;
    private boolean lastvideo = false, smallshow = false;
    private ToggleButton small_toggleButton;
    //倍率
    private float rate = 1;
    //记录上次的倍率
    private float oldRate = 1;
    private String mVideoSource = null, web = null;

    private BVideoView mVV = null;
    private RelativeLayout mViewHolder = null;
    private String ctrl = null, TAG = "viz.errors", video_name = null;
    private int index = 0;
    private String[] videosUrl = null;
    private Intent intent;
    private Uri uriPath=null;
}
