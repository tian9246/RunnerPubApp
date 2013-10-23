package com.azuisapp.runner.net;

public abstract class OnJsonSuccessReturnListener {
    /**
     * 请求成功后（200）回调
     * 
     * @param responseString
     *            解析后的对象
     */
    public abstract void onSuccess(Object objDeserialized);
    
    public void onNetworkFail(Throwable error, String content){
        
    }

}