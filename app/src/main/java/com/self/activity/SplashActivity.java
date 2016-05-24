package com.self.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.self.domain.VersionBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final int LOAD_MAIN = 1;
    private static final int LOAD_UPLOAD_DIALOG = 2;
    private int versionCode;
    private TextView textView;
    private VersionBean version;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        textView = (TextView) findViewById(R.id.tv_splash_version);

        initAnimation();
        initVersion();
        //checkVersion();
        //拷贝数据库
        copyDB("address.db");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMain();
            }
        }, 3000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_MAIN:
                    loadMain();
                    break;
                case LOAD_UPLOAD_DIALOG:
                    loadUploadDialog();
                    break;
            }
        }
    };

    private void copyDB(final String dbName) {
        new Thread() {
            public void run() {
                File file = new File("/data/data/com.self.activity/files/" + dbName);
                if (file.exists()) {
                    return;
                }
                try {
                    copyFile(dbName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void copyFile(String dbName) throws IOException {
        AssetManager assets = getAssets();
        InputStream is = assets.open(dbName);
        FileOutputStream fos = openFileOutput(dbName, MODE_PRIVATE);
        byte[] buf = new byte[10240];
        int len;
        int count = 0;
        while ((len = is.read()) != -1) {
            fos.write(buf, 0, len);
            if (count % 10 == 0) {
                fos.flush();
            }
            count++;
        }
        fos.flush();
        fos.close();
        is.close();
    }

    private void loadUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示").setMessage("检测到新版本(" + version.getDesc() + ")，是否更新？")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        loadMain();
                    }
                })
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAPK();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadMain();
                    }
                });
        builder.show();
    }

    private void downloadAPK() {

    }

    private void installAPK(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadMain();
    }

    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = packageInfo.versionCode;
            textView.setText("版本:" + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkVersion() {
        new Thread() {

            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                int errorCode = 1;
                try {
                    startTime = System.currentTimeMillis();
                    URL url = new URL("http://192.168.1.3/version.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        String line = br.readLine();
                        StringBuilder sb = new StringBuilder();
                        while (line != null) {
                            sb.append(line);
                            line = br.readLine();
                        }
                        convertToBean(sb.toString());
                    } else {
                        errorCode = 404;
                    }
                } catch (MalformedURLException e) {
                    errorCode = 1000;
                    e.printStackTrace();
                    Log.e("exception", "url异常");
                } catch (IOException e) {
                    errorCode = 1001;
                    e.printStackTrace();
                    Log.e("exception", "读取文件异常");
                } catch (JSONException e) {
                    errorCode = 1002;
                    Log.e("exception", "json格式异常");
                } finally {
                    if (errorCode == 1) {
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime < 3000) {
                            SystemClock.sleep(3000 - (endTime - startTime));
                        }
                        Message msg = Message.obtain();
                        if (version.getVersion() == versionCode) {
                            msg.what = LOAD_MAIN;
                        } else {
                            msg.what = LOAD_UPLOAD_DIALOG;
                        }
                        handler.sendMessage(msg);
                    }
                    try {
                        br.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void convertToBean(String json) throws JSONException {
        version = new VersionBean();
        JSONObject object = new JSONObject(json);
        version.setVersion(object.getInt("version"));
        version.setUrl(object.getString("url"));
        version.setDesc(object.getString("desc"));
    }

    private void initAnimation() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash);
        relativeLayout.startAnimation(animation);
    }

}
