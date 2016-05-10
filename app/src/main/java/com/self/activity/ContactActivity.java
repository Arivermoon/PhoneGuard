package com.self.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.self.domain.Contact;
import com.self.engine.ReadContactsEngine;
import com.self.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ListView listView;

    private List<Contact> contacts = new ArrayList<>();

    private static final int LOADING = 0;
    private static final int LOADED = 1;
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
                    pd = new ProgressDialog(ContactActivity.this);
                    pd.setTitle("提示");
                    pd.setMessage("玩命加载中...");
                    pd.show();
                    break;
                case LOADED:
                    pd.dismiss();
                    pd = null;
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
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);
                contacts = ReadContactsEngine.getContacts(getApplicationContext());

                msg = Message.obtain();
                msg.what = LOADED;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constant.PHONE, contacts.get(position).getPhone());
                setResult(1, intent);
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
            View v = View.inflate(getApplicationContext(), R.layout.item_contact, null);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) v.findViewById(R.id.tv_phone);
            Contact contact = contacts.get(position);
            tv_name.setText(contact.getName());
            tv_phone.setText(contact.getPhone());

            return v;
        }
    }
}
