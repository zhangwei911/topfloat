package com.apkstory.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.apkstory.R;
import com.apkstory.com.vichild.file.ListExplorer;
import com.apkstory.util.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TopFloatVideoView2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.floating, null);
        createView();
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
                    flags = false;
                    video_play_Time = 0;
                    wm.removeView(view);
                } else if (event.getPointerCount() == 2) {
                    return gd.onTouchEvent(event);
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

        Intent intent = getIntent();
        if (intent != null) {
            value = intent.getStringExtra("data");
            curentrtime = intent.getIntExtra("curentrtime", 0);
            time = intent.getIntExtra("time", 0);
            pathindex = intent.getIntExtra("pathindex", 0);
            paths = intent.getStringArrayExtra("paths");
            names = intent.getStringArrayExtra("names");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsh();
            }
        });

        videoView = (VideoView) view.findViewById(R.id.videoView);
        exit_imageButton = (ImageButton) view.findViewById(R.id.exit_imageButton);
        pause_imageButton = (ImageButton) view.findViewById(R.id.pause_imageButton);
        play_imageButton = (ImageButton) view.findViewById(R.id.play_imageButton);
        list_imageButton = (ImageButton) view.findViewById(R.id.list_imageButton);
        next_imageButton = (ImageButton) view.findViewById(R.id.next_imageButton);
        floatfull_button = (Button) view.findViewById(R.id.floatfull_button);
        videoname_textView = (TextView) view.findViewById(R.id.videoname_textView);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        exit_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flags = false;
                video_play_Time = 0;
                wm.removeView(view);
            }
        });
        pause_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                flags = false;
                play_imageButton.setVisibility(View.VISIBLE);
                pause_imageButton.setVisibility(View.GONE);
            }
        });
        play_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
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
                Intent intent = new Intent(TopFloatVideoView2.this, ListExplorer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pathcache", value.substring(0, value.lastIndexOf("/")));
                intent.putExtra("ctrl", "float");
                startActivity(intent);
                wm.removeView(view);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                flags = false;
                video_play_Time = 0;
                next_imageButton.callOnClick();
            }
        });

        next_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathindex++;
                flags = false;
                if (pathindex < paths.length) {
                    videoView.setVideoPath(paths[pathindex]);
                    videoname_textView.setText(names[pathindex]);
                    videoView.seekTo(0);
                    videoView.start();
                    flags = true;
                    video_play_Time = 0;
//                    new MyThread().start();
                    seekBar.setProgress(0);
                } else {
                    flags = false;
                }
            }
        });

        floatfull_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopFloatVideoView2.this, FullScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("path", paths[pathindex]);
                intent.putExtra("ct", videoView.getCurrentPosition());
                intent.putExtra("paths", paths);
                intent.putExtra("names", names);
                intent.putExtra("pathindex", pathindex);
                startActivity(intent);
                finish();
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
                videoView.seekTo(seekBar.getProgress());
                video_play_Time = seekBar.getProgress() / 1000;
                if (!videoView.isPlaying()) {
                    videoView.start();
                    flags = true;
                    new MyThread().start();
                    play_imageButton.setVisibility(View.GONE);
                    pause_imageButton.setVisibility(View.VISIBLE);
                }
                seek_playtime_textView.setText("");
                seek_check = false;
            }
        });

        seekBar.setScaleY(0.2f);

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
        videoView.setVideoPath(value);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(curentrtime);
            }
        });
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
        //开始执行AsyncTask，并传入某些数据
        new LongTimeTask().execute("New Text");
        seekBar.setMax(time);
        String temp = formatTime(time);
        wholetime_textView.setText("/" + String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
        videoname_textView.setText(names[pathindex]);
        wm.addView(view, wmParams);

        //模拟Home键
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    public void sh(int v) {
        videoView.setVisibility(v);
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

    boolean flags = true;

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
                    e.printStackTrace();
                }
            }
        }
    }

    private class LongTimeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                //线程睡眠5秒，模拟耗时操作，这里面的内容Android系统会自动为你启动一个新的线程执行
                Thread.sleep(2000);
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

    public void buttonsh() {
        if (videoView.isPlaying()) {
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
            small_toggleButton.setVisibility(View.INVISIBLE);
        } else {
            exit_imageButton.setVisibility(View.VISIBLE);
            list_imageButton.setVisibility(View.VISIBLE);
            next_imageButton.setVisibility(View.VISIBLE);
            videoname_textView.setVisibility(View.VISIBLE);
            small_toggleButton.setVisibility(View.VISIBLE);
            //开始执行AsyncTask，并传入某些数据
            new LongTimeTask().execute("New Text");
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
        date.setTime(dur);
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
                Toast.makeText(TopFloatVideoView2.this, "zoomin", Toast.LENGTH_SHORT).show();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right
                Log.i("GestureDemo", "zoomout");
                Toast.makeText(TopFloatVideoView2.this, "zoomout", Toast.LENGTH_SHORT).show();
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


    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private View view;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private ImageButton exit_imageButton, pause_imageButton, play_imageButton, list_imageButton, next_imageButton;
    private TextView videoname_textView, playtime_textView, wholetime_textView, seek_playtime_textView;
    private SeekBar seekBar;
    private Button floatfull_button;
    private VideoView videoView;
    private boolean seek_check = false, smallshow = false;
    private String[] paths = null, names = null;
    private int pathindex = 0,curentrtime=0,time = 0;
    private long touchtime1 = 0, touchtime2 = 0;
    int video_play_Time = 0;
    private Handler mHandler = null, hideHandler = null, h = null;
    public static final int REFRESH = 0x000001;
    private String value = "";
    private ToggleButton small_toggleButton;
}
