
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
 * ��JSON������HttpProxy
 * 
 * @author Hanson.Tian
 */
public class HttpJsonProxy extends HttpProxy {

    protected Gson jsonPraser;
    protected Class<?> classOfT;
    protected Type typeOfT;
    protected OnJsonSuccessReturnListener onSuccessListener;
   

    /**
     * ���÷������ݵ�Handler
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
     * ���÷���Handler��ID
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
     * �Զ�����������쳣����
     */
    public HttpJsonProxy setResultListerer(NetWorkFailListener resultListerer) {
        this.resultListerer = resultListerer;
        return this;
    }

    /**
     * �������� ���ǵ�ԭ����RequestParams
     */
    public HttpJsonProxy setRequestParams(Map<String, String> source) {
        this.params = new RequestParams(source);
        return this;
    }

    /**
     * �ṩһ�����������������ķ���
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
     * �Զ�Gson������
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
     * ����̳У���д�������
     */
    protected void buildHandler() {
        if (jsonPraser == null)
            jsonPraser = new GsonBuilder().setVersion(1).create();
        if ((classOfT == null) & (typeOfT == null))
            throw new IllegalArgumentException("û�����÷����л����������:HttpJsonProxy");
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
                // �ص�д���
                try {
                    Log.d("return json", responseString);
                    Object objDeserialized = jsonPraser.fromJson(
                            responseString, classOfT == null ? typeOfT
                                    : classOfT);
                    // �����ɹ�
                    onSuccessListener.onSuccess(objDeserialized);
                    sendHandlerSuccess();
                } catch (Exception e) {
                    System.out.println("----HttpJsonProxy ����Json����-------");
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
                            .println("----HttpJsonProxy��ӡ��Ϣ����onFailure-------");
                    error.printStackTrace();
                }
            }

        };
    }

    /**
     * ��ʼ����һ���µ�HttpClient�������ʵ��
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
