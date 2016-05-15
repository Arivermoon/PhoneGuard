package com.self.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.self.dao.BlackListDao;
import com.self.domain.BlackListBean;
import com.self.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/15.
 */
public class TelSafeActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int LOADED = 2;

    private ListView lv_telsafe;
    private TextView tv_telsafe;
    private ProgressBar pb_telsafe;
    private List<BlackListBean> beans = new ArrayList<>();
    private BlackListDao blackListDao;
    private BlackListAdapter blackListAdapter;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
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
        lv_telsafe = (ListView) findViewById(R.id.lv_telsafe);
        tv_telsafe = (TextView) findViewById(R.id.tv_telsafe);
        pb_telsafe = (ProgressBar) findViewById(R.id.pb_telsafe);
        blackListDao = new BlackListDao(this);
        blackListAdapter = new BlackListAdapter();
        lv_telsafe.setAdapter(blackListAdapter);
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
        public View getView(int position, View convertView, ViewGroup parent) {
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

            itemView.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread() {
                        @Override
                        public void run() {
                            blackListDao.delete(bean);
                            beans = blackListDao.findAll();
                            handler.obtainMessage(LOADED).sendToTarget();
                        }
                    }.start();
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
}
