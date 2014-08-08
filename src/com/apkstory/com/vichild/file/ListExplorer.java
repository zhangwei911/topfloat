package com.apkstory.com.vichild.file;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apkstory.R;
import com.apkstory.activity.FullScreen;
import com.apkstory.activity.TopFloatMain;
import com.apkstory.activity.TopFloatVideoView2;
import com.apkstory.service.TopFloatService;
import com.apkstory.tools.Tools;
import com.baidu.cyberplayer.sdk.BCyberPlayerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wei on 13-7-24.
 */
public class ListExplorer extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.setClass(ListExplorer.this, TopFloatMain.class);
                startActivity(intent);
                finish();
                break;
            case 0:
                if (!changepath.equals("")) {
                    fileExplorer.getFilesPathList(changepath);
                    Toast.makeText(context, changepath, Toast.LENGTH_SHORT).show();
                }
                updateTheGridFiles();
                imageAdapter.notifyDataSetChanged();
                file_listView.setVisibility(View.VISIBLE);
                file_gridView.setVisibility(View.GONE);
                m.getItem(0).setEnabled(false);
                m.getItem(1).setEnabled(true);
                break;
            case 1:
                if (!changepath.equals("")) {
                    fileExplorer.getFilesPathList(changepath);
                    Toast.makeText(context, changepath, Toast.LENGTH_SHORT).show();
                }
                updateTheListFiles();
                fileAdapter.notifyDataSetChanged();
                file_listView.setVisibility(View.GONE);
                file_gridView.setVisibility(View.VISIBLE);
                m.getItem(0).setEnabled(true);
                m.getItem(1).setEnabled(false);
                break;
            case 2:
                boolean root = Tools.getRootAuth();
                if (root) {
                    Toast.makeText(ListExplorer.this, R.string.havegetroot, Toast.LENGTH_SHORT).show();
                    actionItem.setTitle(R.string.havegetroot);
                    actionItem.setEnabled(false);
                } else {
                    Tools.upgradeRootPermission(getPackageCodePath());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean flags = true;


    public class MyThread extends Thread {
        public void run() {
            while (flags) {
                video_play_Time++;
                Message msg = new Message();
                msg.what = REFRESH;
                try {
                    mHandler.sendMessage(msg);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("viz.errors", e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionItem = menu.add(0, 2, 2, R.string.menu);
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionItem.setTitle(R.string.getroot);
        m = menu;
        return true;
    }

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.list_select_menu, menu);
            mode.setTitle(getString(R.string.multselect));
            //setSubtitle(mode);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share:
                    Toast.makeText(ListExplorer.this, "Shared " + file_listView.getCheckedItemCount() +
                            " items", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    break;
                default:
                    Toast.makeText(ListExplorer.this, "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked) {
            setSubtitle(mode);
        }

        private void setSubtitle(ActionMode mode) {
            final int checkedCount = file_listView.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(getString(R.string.selectOne));
                    break;
                default:
                    mode.setSubtitle(getString(R.string.selected) + checkedCount + getString(R.string.ge));
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        //初始化BCyberPlayerFactory, 在其他任何接口调用前需要先对BCyberPlayerFactory进行初始化
        BCyberPlayerFactory.init(this);

        setContentView(R.layout.list_files);

        de = findViewById(R.id.delete_enter);
        de.setVisibility(View.GONE);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mFragment1 = fm.findFragmentByTag("f1");
        if (mFragment1 == null) {
            mFragment1 = new MenuFragment();
            ft.add(mFragment1, "f1");
            ft.show(mFragment1);
            ft.commit();
        }
        final ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        }
        fileExplorer.getFilesPathList(FileExplorer.SDPATH);
        fileAdapter = new FileAdapter(context, FileExplorer.fileName.length);
        updateTheListFiles();
        fileAdapter.notifyDataSetChanged();
        file_listView = (ListView) findViewById(R.id.file_listView);
        //file_listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //file_listView.setMultiChoiceModeListener(new ModeCallback());
        file_listView.setAdapter(fileAdapter);
        file_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (fileAdapter.fileName[position].equals("extrasd_bind") || fileAdapter.fileName[position].equals("Root")) {
                    Toast.makeText(ListExplorer.this, "can't be visited.", Toast.LENGTH_SHORT).show();
                } else if (fileAdapter.isDir[position].equals("File")) {
                    list_seekBar.setEnabled(true);
                    pathindex = position;
                    path = fileAdapter.filePath[position];
                    float_button.setVisibility(View.VISIBLE);
                    floatActivity_button.setVisibility(View.VISIBLE);
                    float_button.setEnabled(true);
                    floatActivity_button.setEnabled(true);
                    full_button.setVisibility(View.VISIBLE);
                    mediaPlayer.setDisplay(surfaceHolder);
                    try {
                        flags = false;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        flags = true;
                        video_play_Time = 0;
                        actionBar.setTitle(fileAdapter.fileName[position]);
                        String temp = formatTime(mediaPlayer.getDuration());
                        list_seekBar.setMax(mediaPlayer.getDuration());
                        listwholetime_textView.setText("/" + String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                        mHandler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == REFRESH) {
                                    String temp = formatTime(video_play_Time * 1000);
                                    listplaytime_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                                    list_seekBar.setProgress(video_play_Time * 1000);
                                }
                                super.handleMessage(msg);
                            }
                        };
                        new MyThread().start();
                        listplaytime_textView.setVisibility(View.VISIBLE);
                        listwholetime_textView.setVisibility(View.VISIBLE);
                        listseek_playtime_textView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.e("viz.errors", e.getMessage());
                    }
                } else {
                    changepath = fileAdapter.filePath[position];
                    fileExplorer.getFilesPathList(fileAdapter.filePath[position]);
                    updateTheListFiles();
                }
                fileAdapter.notifyDataSetChanged();
            }
        });
        file_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, getString(R.string.delete));
            }
        });

        file_listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeTheInput();
                return false;
            }
        });
        imageAdapter = new ImageAdapter(this, FileExplorer.fileName.length);
        file_gridView = (GridView) findViewById(R.id.file_gridView);
        file_gridView.setAdapter(imageAdapter);
        updateTheGridFiles();

        file_gridView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, getString(R.string.delete));
            }
        });
        file_gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemid = position;
                return false;
            }
        });
        file_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (imageAdapter.fileName[position].equals("extrasd_bind") || imageAdapter.fileName[position].equals("Root")) {
                    Toast.makeText(ListExplorer.this, "can't be visited.", Toast.LENGTH_SHORT).show();
                } else if (imageAdapter.isDir[position].equals("File")) {
                    list_seekBar.setEnabled(true);
                    pathindex = position;
                    path = imageAdapter.filePath[position];
                    float_button.setVisibility(View.VISIBLE);
                    floatActivity_button.setVisibility(View.VISIBLE);
                    float_button.setEnabled(true);
                    floatActivity_button.setEnabled(true);
                    full_button.setVisibility(View.VISIBLE);
                    mediaPlayer.setDisplay(surfaceHolder);
                    try {
                        flags = false;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        flags = true;
                        video_play_Time = 0;
                        actionBar.setTitle(imageAdapter.fileName[position]);
                        String temp = formatTime(mediaPlayer.getDuration());
                        list_seekBar.setMax(mediaPlayer.getDuration());
                        listwholetime_textView.setText("/" + String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                        mHandler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == REFRESH) {
                                    String temp = formatTime(video_play_Time * 1000);
                                    listplaytime_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                                    list_seekBar.setProgress(video_play_Time * 1000);
                                }
                                super.handleMessage(msg);
                            }
                        };
                        new MyThread().start();
                        listplaytime_textView.setVisibility(View.VISIBLE);
                        listwholetime_textView.setVisibility(View.VISIBLE);
                        listseek_playtime_textView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.e("viz.errors", e.getMessage());
                    }
                } else {
                    changepath = imageAdapter.filePath[position];
                    fileExplorer.getFilesPathList(imageAdapter.filePath[position]);
                    updateTheGridFiles();
                }
                imageAdapter.notifyDataSetChanged();
            }
        });
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(300);
        file_listView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Toast.makeText(ListExplorer.this, "start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Toast.makeText(ListExplorer.this, "end", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        float_button = (Button) findViewById(R.id.float_button);
        floatActivity_button = (Button) findViewById(R.id.floatActivity_button);
        float_button.setVisibility(View.GONE);
        floatActivity_button.setVisibility(View.GONE);
        float_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                String[] p, n;
                if (file_listView.getVisibility() == View.VISIBLE) {
                    int indexfile = fileAdapter.filePath.length - 1;
                    p = new String[indexfile];
                    n = new String[indexfile];
                    for (int i = 0; i < indexfile; i++) {
                        p[i] = fileAdapter.filePath[i + 1];
                        n[i] = fileAdapter.fileName[i + 1];
                    }
                } else {
                    int indexfile = imageAdapter.filePath.length - 1;
                    p = new String[indexfile];
                    n = new String[indexfile];
                    for (int i = 0; i < indexfile; i++) {
                        p[i] = imageAdapter.filePath[i + 1];
                        n[i] = imageAdapter.fileName[i + 1];
                    }
                }
                Intent service = new Intent();
                service.setClass(ListExplorer.this, TopFloatService.class);
//                service.setClass(ListExplorer.this, TopFloatService_surfaceView.class);
                service.putExtra("data", path);
                service.putExtra("curentrtime", mediaPlayer.getCurrentPosition());
                service.putExtra("time", mediaPlayer.getDuration());
                service.putExtra("paths", p);
                service.putExtra("names", n);
                service.putExtra("pathindex", pathindex - 1);
                startService(service);
                finish();

            }
        });

        floatActivity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                String[] p, n;
                if (file_listView.getVisibility() == View.VISIBLE) {
                    int indexfile = fileAdapter.filePath.length - 1;
                    p = new String[indexfile];
                    n = new String[indexfile];
                    for (int i = 0; i < indexfile; i++) {
                        p[i] = fileAdapter.filePath[i + 1];
                        n[i] = fileAdapter.fileName[i + 1];
                    }
                } else {
                    int indexfile = imageAdapter.filePath.length - 1;
                    p = new String[indexfile];
                    n = new String[indexfile];
                    for (int i = 0; i < indexfile; i++) {
                        p[i] = imageAdapter.filePath[i + 1];
                        n[i] = imageAdapter.fileName[i + 1];
                    }
                }

                Intent intent = new Intent();
                intent.setClass(ListExplorer.this, TopFloatVideoView2.class);
                intent.putExtra("data", path);
                intent.putExtra("curentrtime", mediaPlayer.getCurrentPosition());
                intent.putExtra("time", mediaPlayer.getDuration());
                intent.putExtra("paths", fileAdapter.filePath);
                intent.putExtra("names", fileAdapter.fileName);
                intent.putExtra("pathindex", pathindex);
                startActivity(intent);
            }
        });

        delete_ok_button = (Button) de.findViewById(R.id.delete_ok_button);
        delete_cancel_button = (Button) de.findViewById(R.id.delete_cancel_button);
        delete_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (file_listView.getVisibility() == View.VISIBLE) {
                    deleteTheFile(fileAdapter.filePath[itemid]);
                    fileExplorer.getFilesPathList(new File(fileAdapter.filePath[itemid]).getParent());
                    updateTheListFiles();
                    fileAdapter.notifyDataSetChanged();
                } else {
                    deleteTheFile(imageAdapter.filePath[itemid]);
                    fileExplorer.getFilesPathList(new File(imageAdapter.filePath[itemid]).getParent());
                    updateTheGridFiles();
                    imageAdapter.notifyDataSetChanged();
                }
                de.setVisibility(View.GONE);
                if (file_listView.getVisibility() == View.VISIBLE) {
                    file_listView.setEnabled(true);
                } else {
                    file_gridView.setEnabled(true);
                }
                surfaceView.setEnabled(true);
                float_button.setEnabled(true);
                floatActivity_button.setEnabled(true);
                full_button.setEnabled(true);
                list_seekBar.setEnabled(true);
//                Tools.showMsg(context, itemid+"");
            }
        });
        delete_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                de.setVisibility(View.GONE);
                if (file_listView.getVisibility() == View.VISIBLE) {
                    file_listView.setEnabled(true);
                } else {
                    file_gridView.setEnabled(true);
                }
                surfaceView.setEnabled(true);
                float_button.setEnabled(true);
                floatActivity_button.setEnabled(true);
                full_button.setEnabled(true);
                list_seekBar.setEnabled(true);
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        if (surfaceHolder != null) {
            surfaceHolder.setFixedSize(100, 100);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pathindex++;
                if (pathindex < imageAdapter.filePath.length) {
                    path = imageAdapter.filePath[pathindex];
                    float_button.setVisibility(View.VISIBLE);
                    floatActivity_button.setVisibility(View.VISIBLE);
                    try {
                        flags = false;
                        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(imageAdapter.filePath[pathindex]);
                        // mediaPlayer.prepare();
                        mediaPlayer.start();
                        flags = true;
                        video_play_Time = 0;
                        actionBar.setTitle(imageAdapter.fileName[pathindex]);
                        list_seekBar.setMax(mediaPlayer.getDuration());
                        list_seekBar.setProgress(0);
                        String temp = formatTime(mediaPlayer.getDuration());
                        listwholetime_textView.setText("/" + String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                    } catch (Exception e) {
                    }
                } else {
                    flags = false;
                    mediaPlayer.pause();
                }
            }
        });

        full_button = (Button) findViewById(R.id.full_button);
        full_button.setVisibility(View.GONE);
        full_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListExplorer.this, FullScreen.class);
                String[] p, n;
                if (file_listView.getVisibility() == View.VISIBLE) {
                    int indexfile = fileAdapter.filePath.length - 1;
                    p = new String[indexfile];
                    n = new String[indexfile];
                    for (int i = 0; i < indexfile; i++) {
                        p[i] = fileAdapter.filePath[i + 1];
                        n[i] = fileAdapter.fileName[i + 1];
                    }
                } else {
                    int indexfile = imageAdapter.filePath.length - 1;
                    p = new String[indexfile];
                    n = new String[indexfile];
                    for (int i = 0; i < indexfile; i++) {
                        p[i] = imageAdapter.filePath[i + 1];
                        n[i] = imageAdapter.fileName[i + 1];
                    }
                }
                intent.putExtra("path", path);
                intent.putExtra("ct", mediaPlayer.getCurrentPosition());
                intent.putExtra("paths", p);
                intent.putExtra("names", n);
                intent.putExtra("pathindex", pathindex);
                startActivity(intent);

//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.putExtra("isHW",true); //（可选）
//                Uri uri = Uri.parse(path);
//                intent.setClassName("com.baidu.cyberplayer.engine","com.baidu.cyberplayer.engine.PlayingActivity");
//                intent.setData(uri);
//                startActivity(intent);

                //播放前需要用ak,sk来初始化BEngineManager, 播放的时候会对ak,sk进行权限认证
                //当前您也可以到VideoViewPlayingActivity进行初始化
//                BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
//                mgr.initCyberPlayerEngine(AK, SK);
//                Intent intent = new Intent(ListExplorer.this, VideoViewPlayingActivity.class);
//                intent.setData(Uri.parse(path));
//                intent.putExtra("ct", mediaPlayer.getCurrentPosition());
//                startActivity(intent);
            }
        });

        listplaytime_textView = (TextView) findViewById(R.id.listplaytime_textView);
        listwholetime_textView = (TextView) findViewById(R.id.listwholetime_textView);
        listseek_playtime_textView = (TextView) findViewById(R.id.listseek_playtime_textView);
        list_seekBar = (SeekBar) findViewById(R.id.list_seekBar);
        list_seekBar.setEnabled(false);

        list_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seek_check) {
                    String temp = formatTime(seekBar.getProgress());
                    listseek_playtime_textView.setText(String.format("%02d", (Integer.parseInt(temp.substring(0, 2)) - 8)) + temp.substring(2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seek_check = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                video_play_Time = seekBar.getProgress() / 1000;
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    flags = true;
                    new MyThread().start();
                }
                Log.e("viz.errors.seekto", "seekto");
                mediaPlayer.seekTo(seekBar.getProgress());
                listseek_playtime_textView.setText("");
                seek_check = false;
            }
        });


        file_listView.setVisibility(View.GONE);
        file_gridView.setVisibility(View.VISIBLE);

        if (mediaPlayer.isPlaying()) {
            flags = false;
            mediaPlayer.pause();
        }
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
    public void onPrepared(MediaPlayer mp) {
        Log.v("viz.errors.onprepared", "onPrepared");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();
        if (intent.getStringExtra("ctrl") != null) {
            String str = intent.getStringExtra("ctrl");
            if (str.equals("float") || str.equals("full")) {
                fileExplorer.getFilesPathList(intent.getStringExtra("pathcache"));
                updateTheListFiles();
                updateTheGridFiles();
                imageAdapter.notifyDataSetChanged();
                fileAdapter.notifyDataSetChanged();
                flags = false;
                video_play_Time = 0;
                mediaPlayer.reset();
                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
            }
        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("viz.errors.onpause", "onPause");
        //在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
        if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
            savetime = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("viz.errors.onresume", "onResume");
        mediaPlayer.seekTo(savetime);
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        Intent intent = new Intent();
        intent.putExtra("error", "error");
        ListExplorer.this.setResult(0, intent);
        finish();
        Log.v("viz.errors.onstop", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("viz.errors.ondestroy", "onDestroy");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("viz.errors.onerror", "onError");
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        finish();
        return false;
    }

    public void closeTheInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(ListExplorer.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void updateTheListFiles() {
        int temp_count = fileExplorer.fileName.length;
        fileAdapter.count = temp_count;
        fileAdapter.fileName = new String[temp_count];
        fileAdapter.filePath = new String[temp_count];
        fileAdapter.fileUpdateTime = new String[temp_count];
        fileAdapter.isDir = new String[temp_count];
        for (int i = 0; i < temp_count; i++) {
            fileAdapter.fileName[i] = fileExplorer.fileName[i];
            fileAdapter.filePath[i] = fileExplorer.filePath[i];
            fileAdapter.fileUpdateTime[i] = fileExplorer.fileUpdateTime[i];
            fileAdapter.isDir[i] = fileExplorer.isDir[i];
        }
    }


    public void updateTheGridFiles() {
        int temp_count = fileExplorer.fileName.length;
        //Log.e("viz.errors", temp_count + "");
        imageAdapter.count = temp_count;
        imageAdapter.fileName = new String[temp_count];
        imageAdapter.filePath = new String[temp_count];
        imageAdapter.fileUpdateTime = new String[temp_count];
        imageAdapter.fileType = new String[temp_count];
        imageAdapter.isDir = new String[temp_count];
        for (int i = 0; i < temp_count; i++) {
            imageAdapter.fileName[i] = fileExplorer.fileName[i];
            imageAdapter.filePath[i] = fileExplorer.filePath[i];
            imageAdapter.fileUpdateTime[i] = fileExplorer.fileUpdateTime[i];
            imageAdapter.isDir[i] = fileExplorer.isDir[i];
            if (fileExplorer.fileName[i].contains(".")) {
                if (fileExplorer.fileName[i].substring(fileExplorer.fileName[i].lastIndexOf(".") + 1).length() > 5) {
                    imageAdapter.fileType[i] = fileExplorer.fileName[i].substring(fileExplorer.fileName[i].lastIndexOf(".") + 1, fileExplorer.fileName[i].lastIndexOf(".") + 1 + 5) + "...";
                } else {
                    imageAdapter.fileType[i] = fileExplorer.fileName[i].substring(fileExplorer.fileName[i].lastIndexOf(".") + 1);
                }
            }
        }
    }

    //播放状态
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;


    public void deleteTheFile(String file) {
        boolean success = doDeleteEmptyDir(file);
        if (!success) {
            deleteDir(new File(file));
        }
        Tools.showMsg(context, getString(R.string.file_deleted));
    }

    /**
     * Deletes the directory passed in.
     *
     * @param dir Directory to be deleted
     */
    private static boolean doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();

        if (success) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Deletes all files and subdirectories under "dir".
     *
     * @param dir Directory to be deleted
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    private static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so now it can be smoked
        return dir.delete();
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        MID = (int) info.id;

        switch (item.getItemId()) {
            case 0:
                de.setVisibility(View.VISIBLE);
                if (file_listView.getVisibility() == View.VISIBLE) {
                    file_listView.setEnabled(false);
                } else {
                    file_gridView.setEnabled(false);
                }
                surfaceView.setEnabled(false);
                float_button.setEnabled(false);
                floatActivity_button.setEnabled(false);
                full_button.setEnabled(false);
                list_seekBar.setEnabled(false);
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);

    }


    private static ListView file_listView;
    private Context context = ListExplorer.this;
    private FileAdapter fileAdapter = null;
    private FileExplorer fileExplorer = new FileExplorer();
    private Button float_button, floatActivity_button, full_button, delete_ok_button, delete_cancel_button;
    private String path = "";
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private int pathindex = 0;

    private TextView listplaytime_textView, listwholetime_textView, listseek_playtime_textView;
    private SeekBar list_seekBar;

    int video_play_Time = 0;
    private Handler mHandler = null;
    public static final int REFRESH = 0x000001;
    private boolean seek_check = false;
    private MenuItem actionItem;
    private GridView file_gridView;
    private ImageAdapter imageAdapter;
    private Fragment mFragment1;
    private Menu m;
    private String changepath = "";
    private int savetime = 0;
    public int MID, itemid = 0;
    private View de;
}
