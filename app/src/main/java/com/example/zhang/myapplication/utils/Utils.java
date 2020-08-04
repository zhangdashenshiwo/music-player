package com.example.zhang.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {

    //声明SharedPreferences变量
    private static SharedPreferences sp;


    public static boolean cleanSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        return sp.edit().clear().commit();
    }


    /**
     *@param context 上下文
     * @param key   键
     * @param value 值
     * @return true or false
     */
    public static boolean putString(Context context,String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        return sp.edit().putString(key, value).commit();
    }


    /**
     * @param context 上下文
     * @param key          键
     * @param defaultValue 默认值
     * @return 取出值
     */
    public static String getString(Context context,String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        return sp.getString(key, defaultValue);
    }

    /**
     *@param context 上下文
     * @param key   键
     * @param value 值
     * @return true or false
     */
    public static boolean putInt(Context context,String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        return sp.edit().putInt(key, value).commit();
    }


    /**
     * @param context 上下文
     * @param key          键
     * @param defaultValue 默认值
     * @return 取出值
     */
    public static int getInt(Context context,String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        return sp.getInt(key, defaultValue);
    }


    /**
     *@param context 上下文
     * @param key   键
     * @param value 值
     * @return true or false
     */
    public static boolean putBool(Context context,String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        return sp.edit().putBoolean(key, value).commit();
    }


    /**
     * @param context 上下文
     * @param key          键
     * @param defaultValue 默认值
     * @return 取出值
     */
    public static boolean getBool(Context context,String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        return sp.getBoolean(key, defaultValue);
    }

    public static <T> void saveBeanToSp(Context context,T t, String keyName) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        ByteArrayOutputStream bos;
        //对象操作流，ObjectOutputStream ，该流可以将一个对象写出，或者读取一个对象到程序中，也就是执行了序列化和反序列化操作。
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            byte[] bytes = bos.toByteArray();
            String ObjStr = Base64.encodeToString(bytes, Base64.DEFAULT);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(keyName, ObjStr);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.flush();
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> T getBeanFromSp(Context context,String keyNme) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);

        byte[] bytes = Base64.decode(sp.getString(keyNme, ""), Base64.DEFAULT);
        ByteArrayInputStream bis;
        ObjectInputStream ois = null;
        T obj = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }
}
