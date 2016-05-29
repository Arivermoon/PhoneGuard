package com.self.engine;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.self.constant.Constant;
import com.self.domain.ContactBean;
import com.self.util.EncryptUtils;
import com.self.util.SpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadSmsEngine {

    public static final Uri SMS_URI = Uri.parse("content://sms/inbox");

    /**
     * 根据手机号获取收件箱的短信
     * 用于监听短信
     *
     * @param context
     * @return
     */
    public static ContactBean getSms(Context context) {
        ContactBean bean = new ContactBean();
        String phone = EncryptUtils.decrypt(SpUtils.getString(context, Constant.PHONE));
        String selection = "read = 0 and address = " + phone + " date > " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "body"}, selection, null, "date desc limit 1");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bean.setPhone(cursor.getString(0));
                bean.setContent(cursor.getString(1));
            }
            cursor.close();
        }

        return bean;
    }

    /**
     * 根据新收到短信号码
     * 用于监听短信
     *
     * @param context
     * @return
     */
    public static ContactBean getLatestSms(Context context) {
        ContactBean bean = new ContactBean();
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "body"}, null, null, "date desc limit 1");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bean.setPhone(cursor.getString(0));
                bean.setContent(cursor.getString(1));
            }
            cursor.close();
        }

        return bean;
    }

    public static void deleteSms(Context context, String phone) {
        context.getContentResolver().delete(SMS_URI, "address = ?", new String[]{phone});
    }

    /**
     * 获取所有短信的号码
     *
     * @param context
     * @return
     */
    public static List<ContactBean> getAllSms(Context context) {
        Map<String, ContactBean> map = new TreeMap<>();
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "person"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean bean = new ContactBean();
                String phone = cursor.getString(0);
                bean.setPhone(phone);
                bean.setName(cursor.getString(1));
                map.put(phone, bean);
            }
            cursor.close();
        }

        return new ArrayList<>(map.values());
    }

    public interface BackUpProgress {
        void show();

        void setMax(int max);

        void setProgress(int progress);

        void end();
    }

    private static class Data {
        int progress;
    }

    /**
     * 通过子线程来做短信的还原json格式
     *
     * @param context Activity可以调用runOnUiThread方法
     * @param pd      通过接口回调备份的数据（所有回调方法都在主线程中执行）
     */
    public static void smsRestoreJson(final Activity context, final BackUpProgress pd) {
        final Data data = new Data();
        new Thread() {
            @Override
            public void run() {
                Uri uri = Uri.parse("content://sms");
                try {
                    FileInputStream fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "sms.json"));
                    StringBuilder jsonSmsStr = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String line = br.readLine();
                    while (line != null) {
                        jsonSmsStr.append(line);
                        line = br.readLine();
                    }
                    JSONObject jsonObj = new JSONObject(jsonSmsStr.toString());
                    final int count = Integer.parseInt(jsonObj.getString("count"));

                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(count);
                        }
                    });

                    JSONArray array = (JSONArray) jsonObj.get("smses");
                    for (int i = 0; i < count; i++) {
                        data.progress = i;
                        JSONObject smsjson = array.getJSONObject(i);
                        ContentValues values = new ContentValues();
                        values.put("address", smsjson.getString("address"));
                        values.put("body", smsjson.getString("body"));
                        values.put("date", smsjson.getString("date"));
                        values.put("type", smsjson.getString("type"));
                        context.getContentResolver().insert(uri, values);

                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pd.setProgress(data.progress);
                            }
                        });
                    }
                    br.close();

                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.end();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 通过子线程来做短信的备份
     *
     * @param context
     * @param pd      通过接口回调备份的数据（所有回调方法都在主线程中执行）
     */
    public static void smsBackUpJson(final Activity context, final BackUpProgress pd) {
        new Thread() {
            @Override
            public void run() {
                Uri uri = Uri.parse("content://sms");
                final Cursor cursor = context.getContentResolver().query(uri,
                        new String[]{"address", "date", "body", "type"}, null, null, " _id desc");
                if (cursor != null) {
                    final int count = cursor.getCount();
                    File file = new File(Environment.getExternalStorageDirectory(), "sms.json");
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        PrintWriter out = new PrintWriter(fos);
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pd.show();
                                pd.setMax(count);
                            }
                        });

                        final Data data = new Data();
                        out.println("{\"count\":\"" + count + "\"");
                        out.println(",\"smses\":[");
                        while (cursor.moveToNext()) {
                            data.progress++;
                            // 取短信
                            if (cursor.getPosition() == 0) {
                                out.println("{");
                            } else {
                                out.println(",{");
                            }
                            out.println("\"address\":\"" + cursor.getString(0) + "\",");
                            out.println("\"date\":\"" + cursor.getString(1) + "\",");
                            out.println("\"body\":\"" + cursor.getString(2) + "\",");
                            out.println("\"type\":\"" + cursor.getString(3) + "\"");
                            out.println("}");
                            context.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    pd.setProgress(data.progress);
                                }
                            });
                        }

                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pd.end();
                            }
                        });
                        out.println("]}");
                        out.close();
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
}
