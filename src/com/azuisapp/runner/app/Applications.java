
package com.azuisapp.runner.app;

import android.app.Application;

import com.azuisapp.runner.net.HttpProxy;
import com.azuisapp.runner.util.LoginUtil;

public class Applications extends Application {

    /**
     * 服务器具体地址
     */
    public final static String BASE_URL = "http://runnerpub.azuis.me:8080/runnerpub/";
    public final static String LOGIN_URL = BASE_URL + "android/login";
    public final static String UPLOAD_URL = BASE_URL + "android/record";

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化httpProxy的context指针
        HttpProxy.initApplicationContext(this);
        LoginUtil.getInstance().initContext(getApplicationContext());
    }

}
