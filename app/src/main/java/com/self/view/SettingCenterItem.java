package com.self.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.self.activity.R;
import com.self.utils.Constant;
import com.self.utils.SpUtils;


/**
 * Created by tanlang on 2016/5/14.
 */
public class SettingCenterItem extends LinearLayout {

    private CheckBox checkBox;
    private String[] contents;
    private TextView contentView;
    private int order;

    //代码实例化调用
    public SettingCenterItem(Context context) {
        super(context);

    }

    //配置文件中，反射实例化设置属性参数
    public SettingCenterItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.item_settingcenter_view, null);
        addView(view);
        TextView titleView = (TextView) view.findViewById(R.id.tv_settingcenter_title);
        contentView = (TextView) view.findViewById(R.id.tv_settingcenter_content);
        checkBox = (CheckBox) view.findViewById(R.id.cb_settingcenter_checked);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingCenterItem);
        String title = typedArray.getString(R.styleable.SettingCenterItem_mTitle);
        titleView.setText(title);
        String content = typedArray.getString(R.styleable.SettingCenterItem_mContent);
        contents = content.split("-");
        order = typedArray.getInt(R.styleable.SettingCenterItem_mOrder, 0);
        initData();

        typedArray.recycle();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveCheckedInfo(isChecked);
                if (isChecked) {
                    contentView.setText(contents[1]);
                    contentView.setTextColor(Color.RED);
                } else {
                    contentView.setText(contents[0]);
                    contentView.setTextColor(Color.BLACK);
                }
            }
        });
    }

    private void saveCheckedInfo(boolean isChecked) {
        switch (order) {
            case 1:
                SpUtils.putBoolean(getContext(), Constant.AUTO_UPDATE, isChecked);
                break;
            case 2:
                SpUtils.putBoolean(getContext(), Constant.BLACKLIST, isChecked);
                break;
            default:
                break;
        }
    }

    private void initData() {
        switch (order) {
            case 1:
                Boolean checked1 = SpUtils.getBoolean(getContext(), Constant.AUTO_UPDATE);
                if (checked1){
                    contentView.setText(contents[1]);
                    contentView.setTextColor(Color.RED);
                }else {
                    contentView.setText(contents[0]);
                    contentView.setTextColor(Color.BLACK);
                }
                checkBox.setChecked(checked1);
                break;
            case 2:
                Boolean checked2 = SpUtils.getBoolean(getContext(), Constant.BLACKLIST);
                if (checked2){
                    contentView.setText(contents[1]);
                    contentView.setTextColor(Color.RED);
                }else {
                    contentView.setText(contents[0]);
                    contentView.setTextColor(Color.BLACK);
                }
                checkBox.setChecked(checked2);
                break;
            default:
                break;
        }

    }

}
