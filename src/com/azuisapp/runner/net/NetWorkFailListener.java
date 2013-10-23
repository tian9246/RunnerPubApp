package com.azuisapp.runner.net;


public  class NetWorkFailListener {

    /**
     * 3G提示，提示正在使用3G
     * 
     * @return 3G网络是否继续向下执行
     */
    public boolean on3GNetwork() {
        return true;
    }

    /**
     * 网络错误，比如HSOT UNREACHABLE
     * 
     * @return 是否显示提示
     */
    public boolean onNetworkAccessFail(Throwable error, String content) {
        return true;
    }

    /**
     * 只要解析不出来，全都算服务器错误
     * 
     * @return 是否继续向下执行
     */
    public boolean onServerAccessFail() {
        return false;
    }

    /**
     * 网络状态检查错误，比如没有3G网络连接等等
     * 
     * @return 是否显示错误信息
     */
    public boolean onNetworkStatusFail() {
        return true;
    }

}