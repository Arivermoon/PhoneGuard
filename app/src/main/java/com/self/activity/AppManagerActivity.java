package com.self.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.self.constant.Constant;
import com.self.domain.AppBean;
import com.self.engine.AppManagerEngine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

    private TextView tv_sdAvail;
    private TextView tv_romAvail;
    private ListView lv_datas;
    private ProgressBar pb_loading;
    private TextView tv_lable;
    private PackageManager pm;
    private AppManagerAdapter adapter;
    private List<AppBean> userApks = new ArrayList<>();
    private List<AppBean> sysApks = new ArrayList<>();
    private AppManagerHandler handler;
    private long sdCardFreeSpace;
    private long romFreeSpace;
    private PopupWindow pw;
    private View popupView;
    private AppBean clickedBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initView();

        initData();

        initEvent();

        initPopupWindow();
    }

    private static class AppManagerHandler extends Handler {

        WeakReference<AppManagerActivity> wr;

        public AppManagerHandler(AppManagerActivity activity) {
            wr = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerActivity activity = wr.get();
            switch (msg.what) {
                case Constant.LOADING:
                    activity.pb_loading.setVisibility(View.VISIBLE);
                    activity.lv_datas.setVisibility(View.GONE);
                    activity.tv_lable.setVisibility(View.GONE);
                    break;
                case Constant.LOADED:
                    activity.pb_loading.setVisibility(View.GONE);
                    activity.lv_datas.setVisibility(View.VISIBLE);
                    activity.tv_lable.setVisibility(View.VISIBLE);
                    activity.tv_romAvail.setText("ROM可用空间:" + Formatter.formatFileSize(activity, activity.romFreeSpace));
                    activity.tv_sdAvail.setText("SD卡可用空间:" + Formatter.formatFileSize(activity, activity.sdCardFreeSpace));
                    activity.tv_lable.setText("用户软件(" + activity.userApks.size() + ")");
                    activity.adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void initEvent() {
        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == userApks.size() + 1) {
                    return;
                }
                clickedBean = (AppBean) lv_datas.getItemAtPosition(position);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                TagView tagView = (TagView) view.getTag();
                showPopupWindow(view, location[0] + tagView.iv_icon.getWidth(), location[1]);
            }
        });

        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < userApks.size() + 1) {
                    tv_lable.setText("用户软件(" + userApks.size() + ")");
                } else {
                    tv_lable.setText("系统软件(" + sysApks.size() + ")");
                }
                closePopupWindow();
            }
        });
    }

    private void initPopupWindow() {
        popupView = View.inflate(getApplicationContext(), R.layout.popup_app_manager, null);
        LinearLayout ll_remove = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_remove);
        LinearLayout ll_setting = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_setting);
        LinearLayout ll_share = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_share);
        LinearLayout ll_start = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_start);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_appmanager_remove:
                        removeApk();
                        break;
                    case R.id.ll_appmanager_setting:
                        setApk();
                        break;
                    case R.id.ll_appmanager_share:
                        shareApk();
                        break;
                    case R.id.ll_appmanager_start:
                        startApk();
                        break;

                    default:
                        break;
                }
                closePopupWindow();
            }
        };
        ll_remove.setOnClickListener(listener);
        ll_setting.setOnClickListener(listener);
        ll_share.setOnClickListener(listener);
        ll_start.setOnClickListener(listener);
        pw = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new ColorDrawable());
    }

    private void shareApk() {
        
    }

    private void setApk() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + clickedBean.getPackageName()));
        startActivity(intent);
    }

    private void startApk() {
        Intent intent = pm.getLaunchIntentForPackage(clickedBean.getPackageName());
        startActivity(intent);
    }

    private void removeApk() {
        if (clickedBean.isSystem()) {
            Toast.makeText(this, "没有权限卸载该app", Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = Uri.parse("package:" + clickedBean.getPackageName());
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            startActivity(intent);
        }
    }

    private void showPopupWindow(View view, int x, int y) {
        closePopupWindow();
        pw.showAtLocation(view, Gravity.LEFT | Gravity.TOP, x, y);
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_app_manager));
    }

    private void closePopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
    }

    private void initData() {
        pm = getPackageManager();
        handler = new AppManagerHandler(this);
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(Constant.LOADING).sendToTarget();
                List<AppBean> allApks = AppManagerEngine.getAllApks(pm);
                for (AppBean apk : allApks) {
                    if (apk.isSystem()) {
                        sysApks.add(apk);
                    } else {
                        userApks.add(apk);
                    }
                }
                sdCardFreeSpace = AppManagerEngine.getSDCardFreeSpace();
                romFreeSpace = AppManagerEngine.getRomFreeSpace();
                handler.obtainMessage(Constant.LOADED).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        tv_sdAvail = (TextView) findViewById(R.id.tv_appmanager_sdsize);
        tv_romAvail = (TextView) findViewById(R.id.tv_appmanager_romsize);
        lv_datas = (ListView) findViewById(R.id.lv_appmanager);
        pb_loading = (ProgressBar) findViewById(R.id.pb_appmanager_loading);
        tv_lable = (TextView) findViewById(R.id.tv_appmanager_lable);
        adapter = new AppManagerAdapter();
        lv_datas.setAdapter(adapter);
    }

    private class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userApks.size() + sysApks.size() + 2;
        }

        @Override
        public AppBean getItem(int position) {
            if (position < userApks.size() + 1) {
                return userApks.get(position - 1);
            } else {
                return sysApks.get(position - 2 - userApks.size());
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("个人软件(" + userApks.size() + ")");
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                return tv;
            } else if (position == userApks.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统软件(" + sysApks.size() + ")");
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                return tv;
            } else {
                TagView tagView;
                if (convertView != null && convertView instanceof RelativeLayout) {
                    tagView = (TagView) convertView.getTag();
                } else {
                    tagView = new TagView();
                    convertView = View.inflate(getApplicationContext(), R.layout.item_app_manager, null);
                    tagView.iv_icon = (ImageView) convertView.findViewById(R.id.iv_appmanager_icon);
                    tagView.tv_title = (TextView) convertView.findViewById(R.id.tv_appmanager_title);
                    tagView.tv_location = (TextView) convertView.findViewById(R.id.tv_appmanager_location);
                    tagView.tv_size = (TextView) convertView.findViewById(R.id.tv_appmanager_size);
                    convertView.setTag(tagView);
                }

                AppBean bean = getItem(position);
                tagView.iv_icon.setImageDrawable(bean.getAppIcon());
                tagView.tv_title.setText(bean.getAppName());
                tagView.tv_size.setText(Formatter.formatFileSize(getApplicationContext(), bean.getSize()));
                if (bean.isSdCard()) {
                    tagView.tv_location.setText("SDCard存储");
                } else {
                    tagView.tv_location.setText("Rom存储");
                }

                return convertView;
            }
        }
    }

    private class TagView {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_location;
        TextView tv_size;
    }
}
