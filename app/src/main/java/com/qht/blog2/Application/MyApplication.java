package com.qht.blog2.Application;

import com.amitshekhar.DebugDB;
import com.baidu.mapapi.SDKInitializer;
import com.qht.blog2.Net.OK_LoggingInterceptor;
import com.qht.blog2.Util.LogUtil;
import com.qht.blog2.Util.SharePreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePalApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by QHT on 2017-02-27.
 */

public class MyApplication  extends LitePalApplication {

    protected static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
        LogUtil.e(DebugDB.getAddressLog());
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());//初始化百度地图
        SharePreferenceUtil.initSharePreferenceUtil(getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()//init OkHttp3.0
                .connectTimeout(8000L, TimeUnit.MILLISECONDS)
                .readTimeout(8000L, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new OK_LoggingInterceptor())
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        closeAndroidPDialog();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 关闭调用非官方公开API的警示框
     */
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
