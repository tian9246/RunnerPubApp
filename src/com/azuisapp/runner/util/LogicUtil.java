
package com.azuisapp.runner.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.azuisapp.runner.bean.UploadRecoder;

/**
 * 上传相关的代码
 * 
 * @author hansontian
 */
public class LogicUtil {
    
    public static final String KEY_USER = "user";
    public static final String KEY_PASS = "pass";

    private static LogicUtil mInstance;
    public Context context;
    private String username;
    private String password;

    public static LogicUtil getInstance() {
        if (mInstance == null)
            mInstance = new LogicUtil();
        if (mInstance.context != null) {

        }
        return mInstance;
    }

    /**
     * 上传记录
     */
    public void uploadRecoder() {

    }

    public void login(String username, String password) {

    }
    
    /**
     * 登出功能
     */
    public void logout(){
        
    }
    
    public UploadRecoder getUserAndPass(UploadRecoder recoder){
        recoder.username = username;
        recoder.password = password;        
        return recoder;        
    }
    
    private void saveLoginInfo(String user,String password){
        SharedPreferences spf =  context.getSharedPreferences("LOGININFO",Context.MODE_MULTI_PROCESS);
        spf.edit().putString(KEY_USER, user).putString(KEY_PASS, password).commit();        
        this.username = user;
        this.password = password;       
    }
    
  
    
    /**
     * 
     * @return 是否登陆过 T登陆过
     */
    private boolean readLoginInfo(){
        SharedPreferences spf =  context.getSharedPreferences("LOGININFO",Context.MODE_MULTI_PROCESS);
        username = spf.getString(KEY_USER, null);
        password = spf.getString(KEY_PASS, null);
        if(username==null||password==null){
            return false;
        }else{
            return true;
        }
       
    }
    

}
