package com.self.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.self.domain.ContactBean;
import com.self.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListViewActivity extends AppCompatActivity {

    private ListView listView;
    private List<ContactBean> contacts = new ArrayList<>();

    private static final int LOADING = 0;
    private static final int LOADED = 1;
    private static final int REQUEST_CODE = 10;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        listView = (ListView) findViewById(R.id.lv_contact);
        contactAdapter = new ContactAdapter();
        listView.setAdapter(contactAdapter);

        initData();

        initEvent();
    }

    private Handler handler = new Handler() {

        private ProgressDialog pd;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pd = new ProgressDialog(BaseListViewActivity.this);
                    pd.setTitle("提示");
                    pd.setMessage("玩命加载中...");
                    pd.show();
                    break;
                case LOADED:
                    if (pd != null && pd.isShowing()){
                        pd.dismiss();
                        pd = null;
                    }
                    //加载结束，通知数据改变了
                    contactAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        //连接网络，获取本地数据都是耗时操作，需子线程执行
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getPermission()) != PackageManager.PERMISSION_GRANTED) {
                    //安卓6.0（SDK23）以上
                    requestPermissions(new String[]{getPermission()}, REQUEST_CODE);
                } else {
                    contacts = getDatas();
                    handler.obtainMessage(LOADED).sendToTarget();
                }
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contacts = getDatas();
                handler.obtainMessage(LOADED).sendToTarget();
            } else {
                Toast.makeText(this, "您阻止了权限，无法获取数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public abstract List<ContactBean> getDatas();

    public abstract String getPermission();

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constant.PHONE, contacts.get(position).getPhone());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contacts.size();
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
            View view;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.item_contact, null);
            } else {
                view = convertView;
            }
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            ContactBean contact = contacts.get(position);
            tv_name.setText(contact.getName());
            tv_phone.setText(contact.getPhone());

            return view;
        }
    }
}
