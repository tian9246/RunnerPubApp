package com.azuisapp.runner.net;


public  class NetWorkFailListener {

    /**
     * 3G��ʾ����ʾ����ʹ��3G
     * 
     * @return 3G�����Ƿ��������ִ��
     */
    public boolean on3GNetwork() {
        return true;
    }

    /**
     * ������󣬱���HSOT UNREACHABLE
     * 
     * @return �Ƿ���ʾ��ʾ
     */
    public boolean onNetworkAccessFail(Throwable error, String content) {
        return true;
    }

    /**
     * ֻҪ������������ȫ�������������
     * 
     * @return �Ƿ��������ִ��
     */
    public boolean onServerAccessFail() {
        return false;
    }

    /**
     * ����״̬�����󣬱���û��3G�������ӵȵ�
     * 
     * @return �Ƿ���ʾ������Ϣ
     */
    public boolean onNetworkStatusFail() {
        return true;
    }

}