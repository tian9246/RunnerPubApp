
package com.azuisapp.runner.util;

import android.content.Context;

/**
 * 上传相关的代码
 * 
 * @author hansontian
 */
public class LogicUtil {

    private static LogicUtil mInstance;
    public Context context;

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

}
