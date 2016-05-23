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


/**
 * Created by tanlang on 2016/5/14.
 */
public class SettingCenterItemView extends LinearLayout {

    private CheckBox checkBox;
    private TextView contentView;
    private View itemView;
    private TextView titleView;

    //代码实例化调用
    public SettingCenterItemView(Context context) {
        super(context);
    }

    //配置文件中，反射实例化设置属性参数
    public SettingCenterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        itemView = View.inflate(context, R.layout.item_settingcenter, null);
        titleView = (TextView) itemView.findViewById(R.id.tv_settingcenter_title);
        contentView = (TextView) itemView.findViewById(R.id.tv_settingcenter_content);
        checkBox = (CheckBox) itemView.findViewById(R.id.cb_settingcenter);
        addView(itemView);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingCenterItemView);
        String title = typedArray.getString(R.styleable.SettingCenterItemView_mTitle);
        titleView.setText(title);
        final String[] contents = typedArray.getString(R.styleable.SettingCenterItemView_mContent).split("-");

        typedArray.recycle();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

    public void setItemOnClickListener(OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    public void setChecked(boolean isChecked) {
        checkBox.setChecked(isChecked);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

}
