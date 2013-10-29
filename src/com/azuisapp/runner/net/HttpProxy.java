
package com.azuisapp.runner.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.http.entity.ByteArrayEntity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.azuisapp.runner.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * ��װ��������Ĵ����࣬ʵ�������쳣�Զ���ʾ������Exception���ϲ����صķ�װ
 * 
 * @author Hanson.Tian
 */
public class HttpProxy implements IHttpProxy {

    public static final String METHOD_SHOW_PROGRESS = "showLoading";
    public static final String METHOD_HIDE_PROGRESS = "hideLoading";

    public static final String TAGS = "HttpProxy";
    /**
     * Application ��contextָ��
     */
    protected static Context context;

    protected static ConnectivityManager connectivityManager;
    /**
     * �Ƿ���ʾMobile������ʾ
     */
    protected static boolean show3GTips = true;
    protected boolean showNetFailInBaseFragment = true;
    protected boolean showLoadingProgress = true;
    protected String url;
    protected Action action;
    protected RequestParams params;
    protected NetWorkFailListener resultListerer;
    protected OnSuccessListener onSuccessListener;
    protected AsyncHttpResponseHandler responseHandler;
    protected String requestMethod;
    protected Handler mHandler;
    protected int SingleId;
    protected Activity loadingProgressActivity;
    protected String entityString = "";
    protected boolean entityInstedForm = false;

   

    /**
     * �����Ƿ���ʾLoadingProgress�ĶԻ���Ĭ������ʾ
     * 
     * @param showLoadingProgress
     */
    public HttpProxy setShowLoadingProgress(boolean showLoadingProgress) {
        this.showLoadingProgress = showLoadingProgress;
        return this;
    }

    /**
     * ������ʾProgress���ص�Activity
     * 
     * @param showLoadingProgress
     */
    public HttpProxy setLoadingProgressActivity(Activity loadingProgressActivity) {
        this.loadingProgressActivity = loadingProgressActivity;
        showLoadingProgress = true;
        return this;
    }

    /**
     * ��ʾ
     */
    private void showLoadingProgress() {

    }

    /**
     * �������������
     */
    private void hideLoadingProgress() {
        if (!showLoadingProgress)
            return;
        if (loadingProgressActivity == null) {

            return;
        }
        try {
            Method method = loadingProgressActivity.getClass().getMethod(
                    METHOD_HIDE_PROGRESS);
            method.invoke(loadingProgressActivity);

        } catch (NoSuchMethodException e) {

            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {

            e.printStackTrace();
        }
    }

    /**
     * ���ͻ�ȡ�ɹ����ź�
     */
    public void sendHandlerSuccess() {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = SingleId;
            mHandler.sendMessage(msg);
        }

    }

    @Override
    public IHttpProxy setMethod(String method) {
        this.requestMethod = method;
        return this;
    }

    /**
     * ���÷������ݵ�Handler
     * 
     * @param mHandler
     * @return
     */
    public HttpProxy setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
        return this;
    }

    /**
     * ���÷���Handler��ID
     * 
     * @param singleId
     * @return
     */
    public HttpProxy setSingleId(int singleId) {
        SingleId = singleId;
        return this;
    }

    public HttpProxy setAction(Action pAction) {
        this.action = pAction;
        return this;
    }

    public HttpProxy setURL(String url) {
        this.url = url;
        return this;
    }

    /**
     * �Զ�����������쳣����
     */
    public HttpProxy setResultListerer(NetWorkFailListener resultListerer) {
        this.resultListerer = resultListerer;
        return this;
    }

    /**
     * �Զ����ȡ���ݳɹ���Ļص�
     */
    public HttpProxy setOnSuccessListener(OnSuccessListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
        return this;
    }

    public HttpProxy setRequestParams(Map<String, String> source) {
        this.params = new RequestParams(source);
        return this;
    }

    public HttpProxy setShow3GTips(boolean show3gTips) {
        show3GTips = show3gTips;
        return this;
    }

    /**
     * �ṩһ�����������������ķ���
     * 
     * @param key
     * @param value
     */
    public IHttpProxy putRequestParams(String key, String value) {
        if (params == null)
            params = new RequestParams();
        params.put(key, value);
        return this;
    }

    /**
     * ��ʼ��Applicationָ��
     * 
     * @param app
     */
    public static void initApplicationContext(Application app) {
        context = app.getApplicationContext();
    }

    /**
     * ��ʼ����һ���µ�HttpClient�������ʵ��
     * 
     * @return
     */
    public static HttpProxy getProxyBuilder() {
        HttpProxy client = new HttpProxy();
        client.action = Action.POST;
        client.params = new RequestParams();
        client.onSuccessListener = new OnSuccessListener() {
            @Override
            public boolean onSuccess(String responseString) {
                return true;
            }
        };
        return client;
    }

    /**
     * �����·���ͣ���ʾ�����磬3G������ʾ
     * 
     * @return
     */
    protected boolean checkNetworkStatus() {
        if (context == null)
            throw new IllegalArgumentException("��������Application��ʼ����");
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo netinfo = connectivityManager.getActiveNetworkInfo();
        if ((netinfo == null) || (!netinfo.isConnected())) {
            // û����
            if (resultListerer.onNetworkStatusFail()) {
                showTips(ERROR_MESSAGE_NETSTATUS);
            }
            return false;
        }
        // ����״̬Ϊ����
        if (netinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (!show3GTips)
                return true;
            showTips(ERROR_MESSAGE_3GTIP);
            show3GTips = false;// 3G��ʾֻ��ʾһ��
            return resultListerer.on3GNetwork();
        }
        return true;
    }

    /**
     * ����̳У���д�������
     */
    protected void buildHandler() {
        responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showLoadingProgress();

                super.onStart();
            }

            @Override
            public void onFinish() {
                hideLoadingProgress();

                super.onFinish();
            }

            @Override
            public void onSuccess(String responseString) {
                onSuccessListener.onSuccess(responseString);
                sendHandlerSuccess();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (resultListerer.onNetworkAccessFail(error, content)) {
                    showTips(ERROR_MESSAGE_NETACCESSFAIL);
                }
                if (BuildConfig.DEBUG) {
                    System.out.println("----��ӡ��Ϣ����onFailure-------");
                    error.printStackTrace();
                    System.out.println("------��ӡ��Ϣ���Խ���--------" + content);
                }
            }

        };
    }

    private void buildResultListener() {
        resultListerer = new NetWorkFailListener() {
            /**
             * ������󣬱���HSOT UNREACHABLE
             * 
             * @return �Ƿ���ʾ��ʾ
             */
            public boolean onNetworkAccessFail(Throwable error, String content) {
                if (showNetFailInBaseFragment) {
                    try {
                        // MoboApplication app = (MoboApplication) context;
                        // ((MainInterface)app.mi).showFragmentNetworkFail();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

                return true;
            }

        };

        if (url == null)
            throw new IllegalArgumentException("δ����URL");
        if ((action == Action.POST) &&(!entityInstedForm) &&(params == null))
            throw new IllegalArgumentException("ʹ��POST����δ����params");
    }

    /**
     * ִ��HTTP�������
     */
    public void execute() {
        buildResultListener();
        AsyncHttpClient client = new AsyncHttpClient();
        if (!checkNetworkStatus()) {
            return;
        }
        // ����Method����
        if (requestMethod != null) {
            if (params == null)
                params = new RequestParams();
            params.put("method", requestMethod);
        }
        buildHandler();
        switch (action) {
            case POST:
                if(entityInstedForm){
                    ByteArrayEntity entity;
                    try {
                        entity = new ByteArrayEntity(entityString.getBytes("UTF-8"));
                        client.post(context, url, entity, "application/json", responseHandler);
                    } catch (UnsupportedEncodingException e) {                      
                        e.printStackTrace();
                    }   
                    
                    Log.d("http post", url + "?" + params.toString());
                }else{
                    client.post(url, params, responseHandler);
                    Log.d("http post", url + "?" + params.toString());
                }

                break;
            case GET:
                client.get(url, params, responseHandler);
                Log.d("http get", url + "?" + params.toString());
                break;
        }

    }

    /**
     * ��������ERRORʱ��UI������ʾ��Ϣ
     * 
     * @param ERROR_MESSAGE
     */
    protected void showTips(int message) {
        switch (message) {
            case ERROR_MESSAGE_3GTIP:
                showToast("������3G����");
                break;
            case ERROR_MESSAGE_NETSTATUS:
                showToast("������");
                break;
            case ERROR_MESSAGE_NETACCESSFAIL:
                showToast("������ʴ���");
                break;
            case ERROR_MESSAGE_SERVERFAIL:
                showToast("����������");
                break;
            default:
                break;
        }
    }

    private void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public interface OnSuccessListener {
        /**
         * ����ɹ���200���ص�
         * 
         * @param responseString ԭʼ������Ϣ
         * @return �Ƿ�����ִ�� t=��������ִ��
         */
        public boolean onSuccess(String responseString);

    }

}
