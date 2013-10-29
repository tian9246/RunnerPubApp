
package com.azuisapp.runner.net;

import java.lang.reflect.Type;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

import com.azuisapp.runner.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 带JSON解析的HttpProxy
 * 
 * @author Hanson.Tian
 */
public class HttpJsonProxy extends HttpProxy {

    protected Gson jsonPraser;
    protected Class<?> classOfT;
    protected Type typeOfT;
    protected OnJsonSuccessReturnListener onSuccessListener;
   

    /**
     * 设置发送数据的Handler
     * 
     * @param mHandler
     * @return
     */
    @Override
    public HttpJsonProxy setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
        return this;
    }

    @Override
    public HttpJsonProxy setMethod(String method) {
        this.requestMethod = method;
        return this;
    }

    /**
     * 设置返回Handler的ID
     * 
     * @param singleId
     * @return
     */
    public HttpJsonProxy setSingleId(int singleId) {
        SingleId = singleId;
        return this;
    }

    public HttpJsonProxy setAction(Action pAction) {
        this.action = pAction;
        return this;
    }

    public HttpJsonProxy setURL(String url) {
        this.url = url;
        return this;
    }

    /**
     * 自定义网络错误异常处理
     */
    public HttpJsonProxy setResultListerer(NetWorkFailListener resultListerer) {
        this.resultListerer = resultListerer;
        return this;
    }

    /**
     * 从新设置 覆盖掉原来的RequestParams
     */
    public HttpJsonProxy setRequestParams(Map<String, String> source) {
        this.params = new RequestParams(source);
        return this;
    }

    /**
     * 提供一个快速添加请求参数的方法
     * 
     * @param key
     * @param value
     */
    public HttpJsonProxy putRequestParams(String key, String value) {
        if (params == null)
            params = new RequestParams();
        params.put(key, value);
        return this;
    }

    public HttpJsonProxy setShow3GTips(boolean show3gTips) {
        show3GTips = show3gTips;
        return this;
    }

    /**
     * 自定Gson解析器
     * 
     * @param jsonPraser
     * @return
     */
    public HttpJsonProxy setJsonPraser(Gson jsonPraser) {
        this.jsonPraser = jsonPraser;
        return this;
    }

    public HttpJsonProxy setClassOfT(Class<?> classOfT) {
        this.classOfT = classOfT;
        return this;
    }

    public HttpJsonProxy setTypeOfT(Type typeOfT) {
        this.typeOfT = typeOfT;
        return this;
    }

    public HttpJsonProxy setOnSuccessListener(
            OnJsonSuccessReturnListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
        return this;
    }  

    public HttpJsonProxy setEntityString(String entityString) {
        this.entityString = entityString;
        return this;
    }

    public HttpJsonProxy setEntityInstedForm(boolean entityInstedForm) {
        this.entityInstedForm = entityInstedForm;
        return this;
    }

    /**
     * 如果继承，重写这个方法
     */
    protected void buildHandler() {
        if (jsonPraser == null)
            jsonPraser = new GsonBuilder().setVersion(1).create();
        if ((classOfT == null) & (typeOfT == null))
            throw new IllegalArgumentException("没有设置反序列化对象的类型:HttpJsonProxy");
        responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
             
                super.onStart();
            }

            @Override
            public void onFinish() {
              
                super.onFinish();
            }

            @Override
            public void onSuccess(String responseString) {
                // 重点写这个
                try {
                    Log.d("return json", responseString);
                    Object objDeserialized = jsonPraser.fromJson(
                            responseString, classOfT == null ? typeOfT
                                    : classOfT);
                    // 解析成功
                    onSuccessListener.onSuccess(objDeserialized);
                    sendHandlerSuccess();
                } catch (Exception e) {
                    System.out.println("----HttpJsonProxy 解析Json出错-------");
                    e.printStackTrace();
                    if (resultListerer.onNetworkAccessFail(e, responseString)) {
                        showTips(ERROR_MESSAGE_NETACCESSFAIL);
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (resultListerer.onNetworkAccessFail(error, content)) {
                    showTips(ERROR_MESSAGE_NETACCESSFAIL);
                }
                if (BuildConfig.DEBUG) {
                    System.out
                            .println("----HttpJsonProxy打印信息测试onFailure-------");
                    error.printStackTrace();
                }
            }

        };
    }

    /**
     * 开始建造一个新的HttpClient代理访问实例
     * 
     * @return
     */
    public static HttpJsonProxy getProxyBuilder() {
        HttpJsonProxy client = new HttpJsonProxy();
        client.resultListerer = new NetWorkFailListener();
        client.action = Action.POST;
        client.params = new RequestParams();
        return client;
    }

}
