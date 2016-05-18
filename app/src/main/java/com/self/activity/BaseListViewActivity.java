package com.self.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.self.constant.Constant;
import com.self.domain.ContactBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseListViewActivity extends AppCompatActivity {

    private ListView listView;
    private List<ContactBean> contacts = new ArrayList<>();

    private static final int LOADING = 0;
    private static final int LOADED = 1;
    private static final int REQUEST_CODE = 10;
    private ContactAdapter contactAdapter;
    private BaseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        
        initData();

        initEvent();
    }

    static class BaseHandler extends Handler {

        private ProgressDialog pd;
        
        WeakReference<BaseListViewActivity> mActivity;

        public BaseHandler(BaseListViewActivity activity){
            mActivity = new WeakReference<>(activity);
        }
        
        @Override
        public void handleMessage(Message msg) {
            BaseListViewActivity activity = mActivity.get();
            switch (msg.what) {
                case LOADING:
                    pd = new ProgressDialog(activity);
                    pd.setTitle("提示");
                    pd.setMessage("玩命加载中...");
                    pd.show();
                    break;
                case LOADED:
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                        pd = null;
                    }
                    //加载结束，通知数据改变了
                    activity.contactAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    
    private void initData() {
        contactAdapter = new ContactAdapter();
        handler = new BaseHandler(this);
        listView = (ListView) findViewById(R.id.lv_contact);
        listView.setAdapter(contactAdapter);
        
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            ItemView itemView;
            if (convertView == null) {
                itemView = new ItemView();
                convertView = View.inflate(getApplicationContext(), R.layout.item_contact, null);
                itemView.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                itemView.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }
            ContactBean contact = contacts.get(position);
            itemView.tv_name.setText(contact.getName());
            itemView.tv_phone.setText(contact.getPhone());

            return convertView;
        }
    }

    class ItemView {
        TextView tv_name;
        TextView tv_phone;
    }
    
}
