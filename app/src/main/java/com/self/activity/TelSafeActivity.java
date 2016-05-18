package com.self.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.self.dao.BlackListDao;
import com.self.domain.BlackListBean;
import com.self.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/15.
 */
public class TelSafeActivity extends AppCompatActivity implements OnClickListener {

    private static final int LOADING = 1;
    private static final int LOADED = 2;

    private ListView lv_telsafe;
    private TextView tv_telsafe;
    private ProgressBar pb_telsafe;
    private List<BlackListBean> beans = new ArrayList<>();
    private BlackListDao blackListDao;
    private BlackListAdapter blackListAdapter;
    private Button btn_add;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_telsafe.setVisibility(View.VISIBLE);
                    lv_telsafe.setVisibility(View.GONE);
                    tv_telsafe.setVisibility(View.GONE);
                    break;
                case LOADED:
                    pb_telsafe.setVisibility(View.GONE);
                    if (beans.size() > 0) {
                        //有数据
                        lv_telsafe.setVisibility(View.VISIBLE);
                        tv_telsafe.setVisibility(View.GONE);
                        blackListAdapter.notifyDataSetChanged();
                    } else {
                        //无数据
                        lv_telsafe.setVisibility(View.GONE);
                        tv_telsafe.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    private TextView tv_popup_black_manual;
    private TextView tv_popup_black_contacts;
    private TextView tv_popup_black_phonelog;
    private TextView tv_popup_black_smslog;
    private PopupWindow pw;
    private TextView et_telsafe_blacknumber;
    private CheckBox cb_telsafe_smsmode;
    private CheckBox cb_telsafe_phonemode;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        btn_add.setOnClickListener(this);
    }

    private void initData() {
        new Thread() {

            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                beans = blackListDao.findAll();
                handler.obtainMessage(LOADED).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_telsafe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_add = (Button) findViewById(R.id.btn_addblack);
        lv_telsafe = (ListView) findViewById(R.id.lv_telsafe);
        tv_telsafe = (TextView) findViewById(R.id.tv_telsafe);
        pb_telsafe = (ProgressBar) findViewById(R.id.pb_telsafe);
        blackListDao = new BlackListDao(this);
        blackListAdapter = new BlackListAdapter();
        lv_telsafe.setAdapter(blackListAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_addblack:
                showPopupWindow();
                break;
            case R.id.tv_popup_black_manual:
                pw.dismiss();
                pw = null;
                showDialog();
                break;
            case R.id.tv_popup_black_contacts:
                pw.dismiss();
                pw = null;
                intent = new Intent(this, ContactActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_popup_black_phonelog:
                pw.dismiss();
                pw = null;
                intent = new Intent(this, CallLogActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_popup_black_smslog:
                pw.dismiss();
                pw = null;
                intent = new Intent(this, SmsLogActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.bt_telsafee_add:
                String phone = et_telsafe_blacknumber.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean checked4sms = cb_telsafe_smsmode.isChecked();
                boolean checked4phone = cb_telsafe_phonemode.isChecked();
                if (!checked4sms && !checked4phone) {
                    Toast.makeText(this, "两种拦截方式必须选一种", Toast.LENGTH_SHORT).show();
                    return;
                }

                int mode = 0;
                if (checked4sms) {
                    mode |= Constant.SMS;
                }
                if (checked4phone) {
                    mode |= Constant.TEL;
                }
                BlackListBean bean = new BlackListBean();
                bean.setPhone(phone);
                bean.setMode(mode);
                if (beans.contains(bean)) {
                    //已经存在该号码，修改
                    blackListDao.update(bean);
                    beans.set(beans.indexOf(bean), bean);
                } else {
                    //不存在，新增
                    blackListDao.save(bean);
                    beans.add(bean);
                }
                blackListAdapter.notifyDataSetChanged();

                dialog.dismiss();
                dialog = null;
                break;
            case R.id.bt_telsafe_cancel:
                dialog.dismiss();
                dialog = null;
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            showDialog();
            if (resultCode == RESULT_OK) {
                et_telsafe_blacknumber.setText(data.getStringExtra(Constant.PHONE));
            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_addblacklist, null);
        et_telsafe_blacknumber = (TextView) view.findViewById(R.id.et_telsafe_blacknumber);
        cb_telsafe_smsmode = (CheckBox) view.findViewById(R.id.cb_telsafe_smsmode);
        cb_telsafe_phonemode = (CheckBox) view.findViewById(R.id.cb_telsafe_phonemode);
        Button bt_telsafee_add = (Button) view.findViewById(R.id.bt_telsafee_add);
        Button bt_telsafe_cancel = (Button) view.findViewById(R.id.bt_telsafe_cancel);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        bt_telsafee_add.setOnClickListener(this);
        bt_telsafe_cancel.setOnClickListener(this);
    }

    private void showPopupWindow() {
        if (pw == null || !pw.isShowing()) {
            View view = View.inflate(this, R.layout.popup_blacklist, null);
            tv_popup_black_manual = (TextView) view.findViewById(R.id.tv_popup_black_manual);
            tv_popup_black_contacts = (TextView) view.findViewById(R.id.tv_popup_black_contacts);
            tv_popup_black_phonelog = (TextView) view.findViewById(R.id.tv_popup_black_phonelog);
            tv_popup_black_smslog = (TextView) view.findViewById(R.id.tv_popup_black_smslog);
            tv_popup_black_manual.setOnClickListener(this);
            tv_popup_black_contacts.setOnClickListener(this);
            tv_popup_black_phonelog.setOnClickListener(this);
            tv_popup_black_smslog.setOnClickListener(this);
            pw = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            pw.setFocusable(true);
            pw.setOutsideTouchable(true);
            pw.update();
            pw.setBackgroundDrawable(new BitmapDrawable());
            pw.showAtLocation(btn_add, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else {
            pw.dismiss();
            pw = null;
        }
    }

    private class BlackListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemView itemView;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_telsafe, null);
                itemView = new ItemView();
                itemView.phoneView = (TextView) convertView.findViewById(R.id.tv_telsafe_phone);
                itemView.modeView = (TextView) convertView.findViewById(R.id.tv_telsafe_mode);
                itemView.deleteView = (ImageView) convertView.findViewById(R.id.iv_telsafe_delete);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }

            final BlackListBean bean = beans.get(position);
            itemView.phoneView.setText(bean.getPhone());
            switch (bean.getMode()) {
                case Constant.SMS:
                    itemView.modeView.setText("短信拦截");
                    break;
                case Constant.TEL:
                    itemView.modeView.setText("电话拦截");
                    break;
                case Constant.ALL:
                    itemView.modeView.setText("全部拦截");
                    break;
            }

            itemView.deleteView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TelSafeActivity.this);
                    builder.setTitle("提示").setMessage("确定要删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            blackListDao.delete(bean);
                            //删除list中数据，并通知适配器数据已改变，不需要从数据库再次查询
                            beans.remove(position);
                            blackListAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });

            return convertView;
        }
    }

    /**
     * 抽取出来，避免每次都findViewById
     */
    private class ItemView {
        TextView phoneView;
        TextView modeView;
        ImageView deleteView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                pw = null;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
