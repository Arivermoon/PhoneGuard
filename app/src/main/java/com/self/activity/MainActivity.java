package com.self.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.self.utils.Md5Utils;
import com.self.utils.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;

    private int icons[] = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings};

    private String names[] = {"手机防盗", "通讯卫士", "软件管家",
            "进程管理", "流量统计", "病毒查杀",
            "缓存清理", "高级工具", "设置中心"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String savedPsd = SharedPreferencesUtils.getString(getApplicationContext(), "password", "password");
                        if (TextUtils.isEmpty(savedPsd)){
                            showSettingDialog();
                        }else {

                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showSettingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(getApplicationContext(), R.layout.dialog_setting_psd, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button setBtn = (Button) view.findViewById(R.id.btn_set);
        Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);

        setBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et_psd1 = (EditText) view.findViewById(R.id.et_dialog_psd1);
                EditText et_psd2 = (EditText) view.findViewById(R.id.et_dialog_psd2);
                String psd1 = et_psd1.getText().toString();
                String psd2 = et_psd2.getText().toString();

                if (TextUtils.isEmpty(psd1) || TextUtils.isEmpty(psd2)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!psd1.equals(psd2)) {
                    Toast.makeText(getApplicationContext(), "两次密码输入不同", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //保存密码
                    SharedPreferencesUtils.putString(getApplicationContext(), "password", "password", Md5Utils.md5(psd1));
                    dialog.dismiss();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initData() {
        gridView.setAdapter(new gvAdapter());
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gv_main);
    }


    private class gvAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return icons.length;
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
            View view = View.inflate(getApplicationContext(), R.layout.item_gridview_main, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_gv_main);
            TextView textView = (TextView) view.findViewById(R.id.tv_gv_main);
            imageView.setImageResource(icons[position]);
            textView.setText(names[position]);
            return view;
        }
    }
}
