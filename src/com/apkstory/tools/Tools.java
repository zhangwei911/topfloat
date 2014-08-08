package com.apkstory.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apkstory.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by v on 13-11-22.
 */
public class Tools {

    //let the fielExplorer get the right of System root
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;

        if (!getRootAuth()) {
            try {
                String cmd = "chmod 777 " + pkgCodePath;
                process = Runtime.getRuntime().exec("su");//transfer to root
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(cmd + "\n");
                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                        process.destroy();
                    }
                } catch (Exception e) {

                }
            }
            return true;
        } else {
            return false;
        }
    }


    //判断机器是否Root
    public static boolean isRoot() {
        boolean bool = false;
        try {
            if (!new File("/system/bin/su").exists() && !new File("/system/xbin/su").exists()) {
                bool = false;
            } else {
                bool = true;
            }
            Log.i("viz.isRoot", "isRoot   " + bool);
        } catch (Exception e) {

        }
        return bool;
    }

    public static synchronized boolean getRootAuth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //显示Toast
    public static void showMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //检查机子是否安装的有Adobe Flash相关APK
    public static boolean checkinstallornotadobeflashapk(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infoList = pm
                .getInstalledPackages(PackageManager.GET_SERVICES);
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取View控件可见性状态
     *
     * @param view View控件
     * @return View控件可见性状态
     */
    public static int getsh(View view) {
        return view.getVisibility();
    }

    /**
     * 判断网络状态
     *
     * @param context 上下文
     * @return true表示有网络，false表示无网络
     */
    public static boolean isNetworkAvailable(Context context) {
        //get the network statue manager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo networkInfo : infos) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 无网络时提示并提供跳转到网络设置的方法
     *
     * @param context 上下文
     */
    public static void checkNetwork(final Context context) {
        if (!isNetworkAvailable(context)) {
            TextView msg = new TextView(context);
            msg.setText(R.string.set_desc);
            new AlertDialog.Builder(context).setIcon(R.drawable.circle)//set the icon of the AlertDialog
                    .setTitle(R.string.nonetwork)//set the title of the AlertDialog
                    .setView(msg)//set the view(layout) of the AlertDialog content
                    .setPositiveButton(R.string.network_set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })//set the ok button and ClickListener
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create().show();
        }
    }

    public static void download(String weburl) {
        String dirName = "/sdcard/download_test/";
        try {
            URL url = new URL(weburl);
            URLConnection urlConn = url.openConnection();
            String line = System.getProperty("line.separator");
                    /*
                     * jta.append("Host:"+url.getHost()); jta.append(line);
					 * jta.append("Port:"+url.getDefaultPort());
					 * jta.append(line);
					 * jta.append("ContentType:"+urlConn.getContentType());
					 * jta.append(line);
					 * jta.append("ContentLength:"+urlConn.getContentLength());
					 */

            InputStream is = urlConn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            FileOutputStream fos = new FileOutputStream(dirName + "");
            int data;
            while ((data = is.read()) != -1) {
                fos.write(data);
            }
            is.close();
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, Class _class) {
        Intent intent = new Intent(context, _class);
        context.startActivity(intent);
    }
}
