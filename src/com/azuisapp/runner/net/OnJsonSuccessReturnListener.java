package com.azuisapp.runner.net;

public abstract class OnJsonSuccessReturnListener {
    /**
     * ����ɹ���200���ص�
     * 
     * @param responseString
     *            ������Ķ���
     */
    public abstract void onSuccess(Object objDeserialized);
    
    public void onNetworkFail(Throwable error, String content){
        
    }

}