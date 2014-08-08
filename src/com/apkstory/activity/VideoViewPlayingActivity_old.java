//package com.apkstory.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.animation.TranslateAnimation;
//import android.widget.AdapterView;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.apkstory.R;
//import com.apkstory.tools.Tools;
//import com.apkstory.web.WebAdapter;
//import com.baidu.cyberplayer.sdk.BMediaController;
//import com.baidu.cyberplayer.sdk.BVideoView;
//import com.baidu.cyberplayer.sdk.BVideoView.OnCompletionListener;
//import com.baidu.cyberplayer.sdk.BVideoView.OnErrorListener;
//import com.baidu.cyberplayer.sdk.BVideoView.OnInfoListener;
//import com.baidu.cyberplayer.sdk.BVideoView.OnPreparedListener;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import static com.apkstory.tools.Tools.getsh;
//
//public class VideoViewPlayingActivity_old extends Activity implements OnPreparedListener, OnCompletionListener, OnErrorListener, OnInfoListener {
//
//
//    //播放状态
//    private enum PLAYER_STATUS {
//        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
//    }
//
//    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//
//    private int mLastPos = 0;
//
//    private View.OnClickListener mPreListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
////            Toast.makeText(VideoViewPlayingActivity.this, "pre", Toast.LENGTH_SHORT).show();
//            if (5 < mVV.getCurrentPosition()) {
//                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                mLastPos = mVV.getCurrentPosition() - 5;
//                if (mLastPos != 0) {
//                    //如果有记录播放位置,先seek到想要播放的位置
//                    mVV.seekTo(mLastPos);
//                    mLastPos = 0;
//                }
//                //设置播放源
//                mVV.setVideoPath(mVideoSource);
//                //开始播放
//                mVV.start();
//            } else {
//                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//            }
//            Log.v(TAG, "pre btn clicked");
//        }
//    };
//
//    private View.OnClickListener mNextListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
////            Toast.makeText(VideoViewPlayingActivity.this, "next"+mVV.getDuration(), Toast.LENGTH_SHORT).show();
//            if (mVV.getDuration() - 5 > mVV.getCurrentPosition()) {
//                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                mLastPos = mVV.getCurrentPosition() + 5;
//                if (mLastPos != 0) {
//                    //如果有记录播放位置,先seek到想要播放的位置
//                    mVV.seekTo(mLastPos);
//                    mLastPos = 0;
//                }
//                //设置播放源
//                mVV.setVideoPath(mVideoSource);
//                //开始播放
//                mVV.start();
//            } else {
//                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//            }
//            Log.v(TAG, "next btn clicked");
//        }
//    };
//
//    Handler mUIHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case UI_EVENT_PLAY:
//                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                    if (mLastPos != 0) {
//                        //如果有记录播放位置,先seek到想要播放的位置
//                        mVV.seekTo(mLastPos);
//                        mLastPos = 0;
//                    }
//                    //设置播放源
//                    mVV.setVideoPath(mVideoSource);
//                    //开始播放
//                    mVV.start();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.controllerplaying_old);
//
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
//
//        mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
//        videonamefull_textView = (TextView) findViewById(R.id.videonamefull_textView);
//        mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);
//        mControllerHolder = (LinearLayout) findViewById(R.id.controller_holder);
//        web_full_imageButton = (ImageButton) findViewById(R.id.web_full_imageButton);
//        //创建BVideoView和BMediaController
//        mVVCtl = new BMediaController(this);
//        mVV = new BVideoView(context);
//        msg_textView = (TextView) findViewById(R.id.msg_textView);
//        msg_textView.setText("");
//        mViewHolder.addView(mVV);
//        mControllerHolder.addView(mVVCtl);
//
//        //注册listener
//        mVV.setOnPreparedListener(this);
//        mVV.setOnCompletionListener(this);
//        mVV.setOnErrorListener(this);
//        mVV.setOnInfoListener(this);
//        mVVCtl.setPreNextListener(mPreListener, mNextListener);
//
//        //关联BMediaController
//        mVV.setMediaController(mVVCtl);
//        //设置解码模式
//        mVV.setDecodeMode(BVideoView.DECODE_SW);
//        if (getIntent().getData() != null) {
//            uriPath = getIntent().getData();
//            videosUrl = getIntent().getStringArrayExtra("videoUrls");
//            web = getIntent().getStringExtra("ctrl");
//            video_name = getIntent().getStringExtra("video_name");
//            if (null != uriPath) {
//                String scheme = uriPath.getScheme();
//                if (null != scheme && (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp"))) {
//                    mVideoSource = uriPath.toString();
//                    if (web.equals("web")) {
//                        web_listView = (ListView) findViewById(R.id.web_listView);
//                        web_list_imageButton = (ImageButton) findViewById(R.id.web_list_imageButton);
//                        webAdapter = new WebAdapter(VideoViewPlayingActivity_old.this, videosUrl.length);
//                        webAdapter.url = new String[videosUrl.length];
//                        for (int i = 0; i < videosUrl.length; i++) {
//                            if (mVideoSource.equals(videosUrl[i])) {
//                                index = i;
//                            }
//                            webAdapter.url[i] = videosUrl[i];
//                        }
//                        //webAdapter.notifyDataSetChanged();
//                        if (webAdapter != null) {
//                            web_listView.setAdapter(webAdapter);
//                        }
//                    }
//                } else {
//                    mVideoSource = uriPath.getPath();
//                }
//            }
//        } else {
//            mLastPos = getIntent().getIntExtra("ct", 0);
//            videosUrl = getIntent().getStringArrayExtra("videosUrl");
//            mVideoSource = videosUrl[0];
//        }
//
//        web_list_imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (web_listView.getVisibility() == View.VISIBLE) {
//                    inOrOut(0);
//                } else {
//                    inOrOut(1);
//                }
//                web_listView.setSelection(index);
//                web_listView.setVisibility(getsh(web_listView) == View.VISIBLE ? View.GONE : View.VISIBLE);
//            }
//        });
//
//        web_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mVV.stopPlayback();
//                web_listView.setVisibility(View.GONE);
//                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                mVideoSource = videosUrl[position];
//                index = position - 1;
//                Log.e(TAG, "onCompletion  " + mVideoSource);
//                // Tools.showMsg(VideoViewPlayingActivity.this,"onCompletion  "+mVideoSource);
//                //设置播放源
//                mVV.setVideoPath(mVideoSource);
//                //开始播放
//                mVV.start();
//                videonamefull_textView.setText(vm + "  " + getString(R.string.di) + index + getString(R.string.part));
//            }
//        });
//
//        web_full_imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (fu == 0) {
//                    web_full_imageButton.setImageResource(R.drawable.nofull);
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    fu = 1;
//                } else {
//                    web_full_imageButton.setImageResource(R.drawable.full);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    fu = 0;
//                }
//            }
//        });
//        if (video_name != null || !video_name.equals("")) {
//            videonamefull_textView.setText(video_name + "  " + getString(R.string.di) + (index + 1) + getString(R.string.part));
//            vm = video_name;
//        } else {
//            File file = new File(mVideoSource);
//            videonamefull_textView.setText(file.getName());
//            vm = file.getName();
//        }
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//        Log.v(TAG, "onPause");
//        //在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
//        if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
//            mLastPos = mVV.getCurrentPosition();
//            mVV.stopPlayback();
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.v(TAG, "onResume");
//        if (null != mWakeLock && (!mWakeLock.isHeld())) {
//            mWakeLock.acquire();
//        }
//        //发起一次播放任务,当然您不一定要在这发起
//        mUIHandler.sendEmptyMessage(UI_EVENT_PLAY);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.v(TAG, "onStop");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.v(TAG, "onDestroy");
//    }
//
//    @Override
//    public boolean onInfo(int what, int extra) {
//        return false;
//    }
//
//    /**
//     * 播放出错
//     */
//    @Override
//    public boolean onError(int what, int extra) {
//        Log.v(TAG, "onError");
//        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
////        mVV.pause();
////        Intent intent = new Intent(VideoViewPlayingActivity.this, ListExplorer.class);
////        intent.putExtra("pathcache", mVideoSource.substring(0, mVideoSource.lastIndexOf("/")));
////        intent.putExtra("ctrl", "full");
////        startActivity(intent);
//        finish();
//        return true;
//    }
//
//    /**
//     * 播放完成
//     */
//    @Override
//    public void onCompletion() {
//        Log.v(TAG, "onCompletion");
//        if (web.equals("web")) {
//            index++;
//            Log.e(TAG, "onCompletion_1");
//            if (index < videosUrl.length) {
//                Log.e(TAG, "onCompletion_2");
//                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                mVideoSource = videosUrl[index];
//                //Log.e(TAG, "onCompletion  " + mVideoSource);
//                // Tools.showMsg(VideoViewPlayingActivity.this,"onCompletion  "+mVideoSource);
//                //设置播放源
//                mVV.setVideoPath(mVideoSource);
//                //开始播放
//                mVV.start();
//                new LongTimeTask().execute("");
//                Log.e(TAG, "onCompletion_3");
//            } else {
//                Log.e(TAG, "onCompletion_4");
//                Tools.showMsg(VideoViewPlayingActivity_old.this, getString(R.string.playfinish));
//                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//                finish();
//            }
//        } else {
//            Log.e(TAG, "onCompletion_4");
//            Tools.showMsg(VideoViewPlayingActivity_old.this, getString(R.string.playfinish));
//            mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//            finish();
//        }
////        Intent intent = new Intent(VideoViewPlayingActivity.this, ListExplorer.class);
////        intent.putExtra("pathcache", mVideoSource.substring(0, mVideoSource.lastIndexOf("/")));
////        intent.putExtra("ctrl", "full");
////        startActivity(intent);
//    }
//
//    private class LongTimeTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            return params[0];
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            //更新UI的操作，这里面的内容是在UI线程里面执行的
//            videonamefull_textView.setText(vm + "  " + getString(R.string.di) + (index + 1) + getString(R.string.part));
//        }
//
//    }
//
//    /**
//     * 播放准备就绪
//     */
//    @Override
//    public void onPrepared() {
//        // TODO Auto-generated method stub
//        Log.v(TAG, "onPrepared");
//        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
//
//    }
//
//    private GestureDetector gd = new GestureDetector(new GestureDetector.OnGestureListener() {
//        private static final int SWIPE_MIN_DISTANCEX = 5;
//        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
//        private static final int SWIPE_MIN_DISTANCEY = 50;
//
//        @Override
//        public boolean onDown(MotionEvent motionEvent) {
//            if (getsh(web_listView) == View.VISIBLE) {
//                web_listView.setVisibility(View.GONE);
//                inOrOut(0);
//            }
//            return false;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent motionEvent) {
//            return false;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
////            Toast.makeText(VideoViewPlayingActivity.this,"test",Toast.LENGTH_SHORT).show();
//            if (getsh(web_listView) != View.VISIBLE) {
//                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCEX && e1.getY() - e2.getY() < SWIPE_MIN_DISTANCEY) {
////                Toast.makeText(VideoViewPlayingActivity.this,"test1",Toast.LENGTH_SHORT).show();
//
//                    if (1 < mVV.getCurrentPosition()) {
//                        String temp = formatTime(mVV.getCurrentPosition() * 1000 - 1 * 1000);
//                        msg_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
//                        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                        mLastPos = mVV.getCurrentPosition() - 1;
//                        if (mLastPos != 0) {
//                            //如果有记录播放位置,先seek到想要播放的位置
//                            mVV.seekTo(mLastPos);
//                            mLastPos = 0;
//                        }
//                        //设置播放源
//                        mVV.setVideoPath(mVideoSource);
//                        //开始播放
//                        mVV.start();
//                    } else {
//                        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//                    }
//                    return true;
//                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCEX && e1.getY() - e2.getY() < SWIPE_MIN_DISTANCEY) {
//                    //right右移
//                    if (mVV.getDuration() - 1 > mVV.getCurrentPosition()) {
//                        String temp = formatTime(mVV.getCurrentPosition() * 1000 + 1 * 1000);
//                        msg_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
//                        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
//                        mLastPos = mVV.getCurrentPosition() + 1;
//                        if (mLastPos != 0) {
//                            //如果有记录播放位置,先seek到想要播放的位置
//                            mVV.seekTo(mLastPos);
//                            mLastPos = 0;
//                        }
//                        //设置播放源
//                        mVV.setVideoPath(mVideoSource);
//                        //开始播放
//                        mVV.start();
//                    } else {
//                        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//                    }
//                    return true;
//                } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCEX) {
//                    //up上一行
//
//                    return true;
//                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCEX) {
//                    //down下一行
//
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCEX &&
//                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                //left左移
//                Log.i("GestureDemo", "left");
//
//                return true;
//            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCEX &&
//                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                //right右移
//                Log.i("GestureDemo", "right");
//
//                return true;
//            } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCEX &&
//                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                //up上一行
//                Log.i("GestureDemo", "up");
//
//                return true;
//            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCEX &&
//                    Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                //down下一行
//                Log.i("GestureDemo", "down");
//
//                return true;
//            }
//            return false;
//        }
//    });
//    /**
//     * 格式化时间
//     *
//     * @param dur 视频getDuration（）
//     * @return
//     */
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
//    Date date = new Date();
//
//    public String formatTime(int dur) {
//        date.setTime(dur);
//        return simpleDateFormat.format(date);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//
//                break;
//            case MotionEvent.ACTION_UP:
//                if (!msg_textView.getText().equals("")) {
//                    msg_textView.setText("");
//                }
//                break;
//        }
//        return gd.onTouchEvent(event);
//    }
//
//    /**
//     * 左进右出效果
//     *
//     * @param lr 0为出，1为进
//     */
//    public void inOrOut(int lr) {
//        switch (lr) {
//            case 0:
//                translateAnimation = new TranslateAnimation(0f, -1000f, 0f, 0f);
//                translateAnimation.setDuration(300);
//                web_listView.setAnimation(translateAnimation);
//                break;
//            case 1:
//                translateAnimation = new TranslateAnimation(-1000f, 0f, 0f, 0f);
//                translateAnimation.setDuration(300);
//                web_listView.setAnimation(translateAnimation);
//                break;
//        }
//    }
//
//    private Context context=VideoViewPlayingActivity_old.this;
//    private String mVideoSource = null, web = null, vm = null;
//
//    private BVideoView mVV;
//
//    private BMediaController mVVCtl = null;
//    private RelativeLayout mViewHolder = null;
//    private LinearLayout mControllerHolder = null;
//
//    private boolean mIsHwDecode = false;
//
//    private final int UI_EVENT_PLAY = 0;
//
//    private WakeLock mWakeLock = null;
//    private static final String POWER_LOCK = "VideoViewPlayingActivity";
//    private TextView msg_textView;
//    private TextView videonamefull_textView;
//    private Uri uriPath;
//    private int index = 0, fu = 0;
//    private String[] videosUrl;
//    private String ctrl = null, TAG = "viz.errors", video_name = null;
//    private WebAdapter webAdapter = null;
//    private ListView web_listView;
//    private ImageButton web_list_imageButton, web_full_imageButton;
//    private TranslateAnimation translateAnimation;
//}
