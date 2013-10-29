
package com.azuisapp.runner.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.azuisapp.runner.app.Applications;
import com.azuisapp.runner.bean.LoginInfo;
import com.azuisapp.runner.bean.ResultInfo;
import com.azuisapp.runner.bean.UploadRecoder;
import com.azuisapp.runner.net.HttpJsonProxy;
import com.azuisapp.runner.net.IHttpProxy.Action;
import com.azuisapp.runner.net.OnJsonSuccessReturnListener;
import com.google.gson.Gson;

/**
 * 上传相关的代码
 * 
 * @author hansontian
 */
public class LoginUtil {

    public static final String KEY_USER = "user";
    public static final String KEY_PASS = "pass";

    private static LoginUtil mInstance;
    public Context context;
    private String username;
    private String password;

    public static LoginUtil getInstance() {
        if (mInstance == null)
            mInstance = new LoginUtil();
        if (mInstance.context != null) {

        }
        return mInstance;
    }

    public void initContext(Context context) {
        this.context = context;
    }

    /**
     * 尝试登陆
     * 
     * @param username
     * @param password
     * @param onJsonSuccessReturnListener 设置消息回调
     */
    public void login(String username, String password,
            OnJsonSuccessReturnListener onJsonSuccessReturnListener) {
        LoginInfo info = new LoginInfo();
        info.email = username;
        info.password = password;
        HttpJsonProxy.getProxyBuilder().setAction(Action.POST)
                .setEntityString((new Gson()).toJson(info))
                .setEntityInstedForm(true)
                .setOnSuccessListener(onJsonSuccessReturnListener)
                .setURL(Applications.LOGIN_URL).setClassOfT(ResultInfo.class)
                .execute();
        this.username = username;
        this.password = password;
    }

    /**
     * 登出功能
     */
    public void logout() {
        username = "";
        password = "";
        saveLoginInfo();

    }

    /**
     * 对UploadRecoder设置用户名密码
     * 
     * @param recoder
     * @return
     */
    public UploadRecoder getUserAndPass(UploadRecoder recoder) {
        recoder.email = username;
        recoder.password = password;
        return recoder;
    }

    /**
     * 保存登陆信息
     * 
     * @param user
     * @param password
     */
    public void saveLoginInfo() {
        SharedPreferences spf = context.getSharedPreferences("LOGININFO",
                Context.MODE_MULTI_PROCESS);
        if(username.equals("")){
            spf.edit().clear().commit();
        }else{
            spf.edit().putString(KEY_USER, username).putString(KEY_PASS, password).commit();
        }
        

    }

    /**
     * 读取登陆信息
     * 
     * @return 是否登陆过 T登陆过
     */
    public boolean readLoginInfo() {
        SharedPreferences spf = context.getSharedPreferences("LOGININFO",
                Context.MODE_MULTI_PROCESS);
        username = spf.getString(KEY_USER, null);
        password = spf.getString(KEY_PASS, null);
        if (username == null || password == null) {
            return false;
        } else {
            return true;
        }
    }

}
