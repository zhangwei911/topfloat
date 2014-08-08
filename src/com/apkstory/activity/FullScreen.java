package com.apkstory.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.apkstory.R;
import com.apkstory.com.vichild.file.ListExplorer;
import com.apkstory.service.TopFloatService;
import com.apkstory.util.SystemUiHider;
import com.apkstory.web.WebAdapter;

import static com.apkstory.tools.Tools.getsh;
import static com.apkstory.tools.Tools.showMsg;

/**
 * Created by v on 13-11-16.
 */
public class FullScreen extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen);


        names_textView = (TextView) findViewById(R.id.name_textView);
        full_videoView = (VideoView) findViewById(R.id.full_videoView);
        pre_full_imageButton = (ImageButton) findViewById(R.id.pre_full_imageButton);
        next_full_imageButton = (ImageButton) findViewById(R.id.next_full_imageButton);
        fullfloat_button = (Button) findViewById(R.id.fullfloat_button);
        mc = new MediaController(FullScreen.this);
        full_videoView.setMediaController(mc);
        full_videoView.start();
        full_videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    names_textView.setText(names[pathindex]);
                } catch (Exception e) {
                    showMsg(FullScreen.this, getString(R.string.ExceptionExit));
                    finish();
                }
                mediaPlayer.seekTo(ct);
            }
        });
        full_videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!webcheck) {
                    next_full_imageButton.callOnClick();
                } else {
                    finish();
                }
            }
        });
        full_videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        names_textView.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        flags = true;
                        new MyThread().start();
                        break;
                }
                return false;
            }
        });

        pre_full_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathindex--;
                if (pathindex > 0) {
                    if (webcheck) {
                        full_videoView.setVideoURI(Uri.parse(paths[pathindex]));
                    } else {
                        full_videoView.setVideoPath(paths[pathindex]);
                    }
                    full_videoView.start();
                    full_videoView.seekTo(0);
                    names_textView.setText(names[pathindex]);
                    path = paths[pathindex];
                } else {
                    Intent intent = new Intent(FullScreen.this, ListExplorer.class);
                    intent.putExtra("pathcache", path.substring(0, path.lastIndexOf("/")));
                    intent.putExtra("ctrl", "full");
                    startActivity(intent);
                }
            }
        });
        next_full_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathindex++;
                if (pathindex < paths.length) {
                    if (webcheck) {
                        full_videoView.setVideoURI(Uri.parse(paths[pathindex]));
                    } else {
                        full_videoView.setVideoPath(paths[pathindex]);
                    }
                    full_videoView.start();
                    full_videoView.seekTo(0);
                    names_textView.setText(names[pathindex]);
                    path = paths[pathindex];
                } else {
                    Intent intent = new Intent(FullScreen.this, ListExplorer.class);
                    intent.putExtra("pathcache", path.substring(0, path.lastIndexOf("/")));
                    intent.putExtra("ctrl", "full");
                    startActivity(intent);
                }
            }
        });

        fullfloat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_videoView.pause();
                Intent service = new Intent();
                service.setClass(FullScreen.this, TopFloatService.class);
                service.putExtra("data", path);
                service.putExtra("curentrtime", full_videoView.getCurrentPosition());
                service.putExtra("time", full_videoView.getDuration());
                service.putExtra("paths", paths);
                service.putExtra("names", names);
                service.putExtra("pathindex", pathindex);
                startService(service);
                finish();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REFRESH) {
                    names_textView.setVisibility(View.GONE);
                    next_full_imageButton.setVisibility(View.GONE);
                    fullfloat_button.setVisibility(View.GONE);
                    pre_full_imageButton.setVisibility(View.GONE);
                    flags = false;
                }
                super.handleMessage(msg);
            }
        };


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, full_videoView,
                HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight
                            ,
                            mControlsHeight1
                            ,
                            mControlsHeight2
                            ,
                            mControlsHeight3
                            ,
                            mControlsHeight4;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = names_textView.getHeight();
                                mControlsHeight1 = -next_full_imageButton.getHeight() - 50;
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            names_textView
                                    .animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                            next_full_imageButton
                                    .animate()
                                    .translationY(visible ? 0 : mControlsHeight1)
                                    .setDuration(mShortAnimTime);
                            fullfloat_button
                                    .animate()
                                    .translationY(visible ? 0 : mControlsHeight1)
                                    .setDuration(mShortAnimTime);
                            pre_full_imageButton
                                    .animate()
                                    .translationY(visible ? 0 : mControlsHeight1)
                                    .setDuration(mShortAnimTime);
                            full_list_imageButton
                                    .animate()
                                    .translationY(visible ? 0 : mControlsHeight1)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            names_textView.setVisibility(visible ? View.VISIBLE
                                    : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        names_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
        Intent intent = getIntent();
        if (intent.getData() != null) {
            path=intent.getData().toString();
            fullfloat_button.setEnabled(false);
            webcheck = true;
            full_videoView.setVideoURI(Uri.parse(path));
        } else {
            path = intent.getStringExtra("path");
            paths = intent.getStringArrayExtra("paths");
            names = intent.getStringArrayExtra("names");
            full_videoView.setVideoPath(path);
        }
        pathindex = intent.getIntExtra("pathindex", 0);
        ct = intent.getIntExtra("ct", 0);
        full_list_imageButton = (ImageButton) findViewById(R.id.full_list_imageButton);
        full_listView = (ListView) findViewById(R.id.full_listView);
        webAdapter = new WebAdapter(context, paths.length);
        webAdapter.url = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            webAdapter.url[i] = paths[i];
        }
        full_listView.setAdapter(webAdapter);
        full_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pathindex=position;
                if (webcheck) {
                    full_videoView.setVideoURI(Uri.parse(paths[pathindex]));
                } else {
                    full_videoView.setVideoPath(paths[pathindex]);
                }
                full_videoView.start();
                full_videoView.seekTo(0);
                names_textView.setText(names[pathindex]);
                path = paths[pathindex];
                full_listView.setVisibility(View.GONE);
            }
        });
        full_list_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (full_listView.getVisibility() == View.VISIBLE) {
                    inOrOut(0);
                } else {
                    inOrOut(1);
                }
                full_listView.setSelection(pathindex);
                full_listView.setVisibility(getsh(full_listView) == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        full_videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getsh(full_listView) == View.VISIBLE) {
                    full_listView.setVisibility(View.GONE);
                    inOrOut(0);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 下进下出效果
     *
     * @param lr 0为出，1为进
     */
    public void inOrOut(int lr) {
        switch (lr) {
            case 0:
                translateAnimation = new TranslateAnimation(0f, 0f, 0f, 1000f);
                translateAnimation.setDuration(300);
                full_listView.setAnimation(translateAnimation);
                break;
            case 1:
                translateAnimation = new TranslateAnimation(0f, 0f, 1000f, 0f);
                translateAnimation.setDuration(300);
                full_listView.setAnimation(translateAnimation);
                break;
        }
    }

    boolean flags = false;

    public class MyThread extends Thread {
        public void run() {
            while (flags) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = REFRESH;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        savetime = full_videoView.getCurrentPosition();
        super.onPause();
    }

    @Override
    protected void onResume() {
        full_videoView.seekTo(savetime);
        full_videoView.start();
        super.onResume();
    }

    private GestureDetector gd = new GestureDetector(new GestureDetector.OnGestureListener() {
        private static final int SWIPE_MIN_DISTANCEX = 5;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        private static final int SWIPE_MIN_DISTANCEY = 50;

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
//            Toast.makeText(VideoViewPlayingActivity.this,"test",Toast.LENGTH_SHORT).show();
            if (getsh(full_listView) != View.VISIBLE) {
                try {
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCEX && e1.getY() - e2.getY() < SWIPE_MIN_DISTANCEY) {
//                Toast.makeText(VideoViewPlayingActivity.this,"test1",Toast.LENGTH_SHORT).show();

                        if (1 < full_videoView.getCurrentPosition()) {
                            ct = full_videoView.getCurrentPosition() - 1;
                            if (ct != 0) {
                                //如果有记录播放位置,先seek到想要播放的位置
                                full_videoView.seekTo(ct);
                            }
                            //设置播放源
                            full_videoView.setVideoPath(path);
                            //开始播放
                            full_videoView.start();
                        } else {
                        }
                        return true;
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCEX && e1.getY() - e2.getY() < SWIPE_MIN_DISTANCEY) {
                        //right右移
                        if (full_videoView.getDuration() - 1 > full_videoView.getCurrentPosition()) {
                            ct = full_videoView.getCurrentPosition() + 1;
                            if (ct != 0) {
                                //如果有记录播放位置,先seek到想要播放的位置
                                full_videoView.seekTo(ct);
                            }
                            //设置播放源
                            full_videoView.setVideoPath(path);
                            //开始播放
                            full_videoView.start();
                        } else {
                        }
                        return true;
                    } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCEX) {
                        //up上一行

                        return true;
                    } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCEX) {
                        //down下一行

                        return true;
                    }
                } catch (Exception e) {
                    Log.e("viz.errors", e.getMessage());
                }
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCEX &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //left左移
                Log.i("GestureDemo", "left");

                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCEX &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right右移
                Log.i("GestureDemo", "right");

                return true;
            } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCEX &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //up上一行
                Log.i("GestureDemo", "up");

                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCEX &&
                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //down下一行
                Log.i("GestureDemo", "down");

                return true;
            }
            return false;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(FullScreen.this, ListExplorer.class);
        intent.putExtra("pathcache", path.substring(0, path.lastIndexOf("/")));
        intent.putExtra("ctrl", "full");
        startActivity(intent);
    }

    private VideoView full_videoView;
    private MediaController mc;
    private int savetime = 0;
    private int ct = 0, pathindex = 0;
    private String[] paths = null, names = null;
    public static final int REFRESH = 0x000001;
    private Handler mHandler = null;
    private TextView names_textView;
    private ImageButton pre_full_imageButton, next_full_imageButton, full_list_imageButton;
    private Button fullfloat_button;
    private ListView full_listView;
    private String path = "";
    private boolean webcheck;
    private WebAdapter webAdapter = null;
    private Context context = FullScreen.this;
    private TranslateAnimation translateAnimation;
}
