
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
 * 包装网络请求的代理类，实现网络异常自动提示和其他Exception的上层拦截的封装
 * 
 * @author Hanson.Tian
 */
public class HttpProxy implements IHttpProxy {

    public static final String METHOD_SHOW_PROGRESS = "showLoading";
    public static final String METHOD_HIDE_PROGRESS = "hideLoading";

    public static final String TAGS = "HttpProxy";
    /**
     * Application 的context指针
     */
    protected static Context context;

    protected static ConnectivityManager connectivityManager;
    /**
     * 是否显示Mobile网络提示
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
     * 设置是否显示LoadingProgress的对话框，默认问显示
     * 
     * @param showLoadingProgress
     */
    public HttpProxy setShowLoadingProgress(boolean showLoadingProgress) {
        this.showLoadingProgress = showLoadingProgress;
        return this;
    }

    /**
     * 设置显示Progress隐藏的Activity
     * 
     * @param showLoadingProgress
     */
    public HttpProxy setLoadingProgressActivity(Activity loadingProgressActivity) {
        this.loadingProgressActivity = loadingProgressActivity;
        showLoadingProgress = true;
        return this;
    }

    /**
     * 显示
     */
    private void showLoadingProgress() {

    }

    /**
     * 隐藏载入进度条
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
     * 发送获取成功的信号
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
     * 设置发送数据的Handler
     * 
     * @param mHandler
     * @return
     */
    public HttpProxy setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
        return this;
    }

    /**
     * 设置返回Handler的ID
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
     * 自定义网络错误异常处理
     */
    public HttpProxy setResultListerer(NetWorkFailListener resultListerer) {
        this.resultListerer = resultListerer;
        return this;
    }

    /**
     * 自定义获取数据成功后的回调
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
     * 提供一个快速添加请求参数的方法
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
     * 初始化Application指针
     * 
     * @param app
     */
    public static void initApplicationContext(Application app) {
        context = app.getApplicationContext();
    }

    /**
     * 开始建造一个新的HttpClient代理访问实例
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
     * 检查网路类型，提示无网络，3G网络提示
     * 
     * @return
     */
    protected boolean checkNetworkStatus() {
        if (context == null)
            throw new IllegalArgumentException("必须先在Application初始化！");
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo netinfo = connectivityManager.getActiveNetworkInfo();
        if ((netinfo == null) || (!netinfo.isConnected())) {
            // 没连接
            if (resultListerer.onNetworkStatusFail()) {
                showTips(ERROR_MESSAGE_NETSTATUS);
            }
            return false;
        }
        // 链接状态为网络
        if (netinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (!show3GTips)
                return true;
            showTips(ERROR_MESSAGE_3GTIP);
            show3GTips = false;// 3G提示只显示一次
            return resultListerer.on3GNetwork();
        }
        return true;
    }

    /**
     * 如果继承，重写这个方法
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
                    System.out.println("----打印信息测试onFailure-------");
                    error.printStackTrace();
                    System.out.println("------打印信息测试结束--------" + content);
                }
            }

        };
    }

    private void buildResultListener() {
        resultListerer = new NetWorkFailListener() {
            /**
             * 网络错误，比如HSOT UNREACHABLE
             * 
             * @return 是否显示提示
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
            throw new IllegalArgumentException("未设置URL");
        if ((action == Action.POST) &&(!entityInstedForm) &&(params == null))
            throw new IllegalArgumentException("使用POST方法未设置params");
    }

    /**
     * 执行HTTP网络访问
     */
    public void execute() {
        buildResultListener();
        AsyncHttpClient client = new AsyncHttpClient();
        if (!checkNetworkStatus()) {
            return;
        }
        // 设置Method属性
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
     * 出现网络ERROR时在UI界面提示信息
     * 
     * @param ERROR_MESSAGE
     */
    protected void showTips(int message) {
        switch (message) {
            case ERROR_MESSAGE_3GTIP:
                showToast("正在用3G网络");
                break;
            case ERROR_MESSAGE_NETSTATUS:
                showToast("无连接");
                break;
            case ERROR_MESSAGE_NETACCESSFAIL:
                showToast("网络访问错误");
                break;
            case ERROR_MESSAGE_SERVERFAIL:
                showToast("服务器错误");
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
         * 请求成功后（200）回调
         * 
         * @param responseString 原始返回信息
         * @return 是否向下执行 t=继续向下执行
         */
        public boolean onSuccess(String responseString);

    }

}
