package com.azuisapp.runner.net;

import java.util.Map;

import com.azuisapp.runner.net.HttpProxy.OnStatusChangedListener;

public interface IHttpProxy {
	/**
	 * ������Ϣ��ʾ����
	 */	
	public static final int ERROR_MESSAGE_3GTIP = 1;
	public static final int ERROR_MESSAGE_NETSTATUS = 2;
	public static final int ERROR_MESSAGE_NETACCESSFAIL = 3;
	public static final int ERROR_MESSAGE_SERVERFAIL = 4;
	
	public enum Action {
		GET, POST;
	}
	
	public IHttpProxy setAction(Action pAction);

	public IHttpProxy setURL(String url);

	public IHttpProxy setResultListerer(NetWorkFailListener resultListerer);

	public IHttpProxy setOnSuccessListener(OnStatusChangedListener onSuccessListener);	

	public IHttpProxy setRequestParams(Map<String, String> source) ;	
	
	/**
	 * ����API���õķ���
	 * @param method
	 * @return
	 */
	public IHttpProxy setMethod(String method);
	
	public void execute();
	
}
