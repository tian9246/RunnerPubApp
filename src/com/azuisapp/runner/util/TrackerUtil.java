
package com.azuisapp.runner.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.azuisapp.runner.activity.MainActivity;
import com.azuisapp.runner.app.Applications;
import com.azuisapp.runner.bean.ResultInfo;
import com.azuisapp.runner.bean.UploadRecord;
import com.azuisapp.runner.net.HttpJsonProxy;
import com.azuisapp.runner.net.IHttpProxy.Action;
import com.azuisapp.runner.net.OnJsonSuccessReturnListener;
import com.google.gson.Gson;

public class TrackerUtil {
    /* 时间间隔 */
    public static int TIME_INTEVAL = 1000 * 3;

    private static TrackerUtil mInstance;
    private LocationManager locationManager;
    private Context context;
    private Handler handler;
    private boolean runningState = false;

    public final LocationListener locationChangeListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {// 当监听到位置变化时回调
            if (location != null) {
                onLocationChange(location);
            }
        }

        /* 当Provider已离开服务范围时 */
        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.AVAILABLE) {
                sendHandlerMessage(MainActivity.SIG_GPS_READY, "");
                sendHandlerMessage(MainActivity.SIG_SHOW_MESSAGE, "GPS IS READY");
            }
        }
    };

    private TrackerUtil() {

    }

    /**
     * 获取位置服务的实例
     * 
     * @return
     */
    public static TrackerUtil getInstance() {
        if (mInstance == null)
            mInstance = new TrackerUtil();
        return mInstance;
    }

    /**
     * 获取需要上传的信息
     * 
     * @return
     */
    private UploadRecord getUploadRecord() {
        ArrayList<Location> locations = Datasource.getInstance().getAllLocation();
        UploadRecord recoder = new UploadRecord();
        recoder.distance = 34885.5d;//getAllDistance();
        recoder.starttime = 1500508548;//locations.get(0).getTime();
        recoder.endtime = 1500508588l;//ocations.get(locations.size()).getTime();
        return recoder;

    }

    /**
     * 显示toast
     * 
     * @param content
     */
    public void showToast(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 插入节点信息
     * 
     * @param location
     */
    public void onLocationChange(Location location) {
        if (runningState) {
            Datasource.getInstance().insertLocation(location);
            sendHandlerMessage(MainActivity.SIG_UPDATE_DISTANCE_SHOW, "");
        }

    }

    /**
     * 设置 context 初始化locationManager
     * 
     * @param context
     * @param handler
     */
    public void setContextAndInit(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Datasource.getInstance().initDatabase(context);
    }

    /**
     * 获取总距离
     * 
     * @return 单位是米
     */
    public Double getAllDistance() {
        ArrayList<Location> locations = Datasource.getInstance().getAllLocation();
        Log.e("All", locations.size() + "");
        double distance = 0;
        Location preLocation = null;
        for (Location location : locations) {
            if (preLocation == null) {
                preLocation = location;
            }
            else
            {
                distance += Math
                        .abs(DistanceUtil.getDistance(preLocation.getLongitude(),
                                preLocation.getLatitude(), location.getLongitude(),
                                location.getLatitude()));
                preLocation = location;
                Log.e("distance", distance + "");
            }
        }
        return distance;
    }

    /**
     * 通过handler返回到主线程信息
     * 
     * @param what
     * @param content
     */
    private void sendHandlerMessage(int what, String content) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = content;
        handler.sendMessage(msg);
    }

    /**
     * 预热GPS功能
     */
    public void initGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showToast("Please turn on your GPS");
            sendHandlerMessage(MainActivity.SIG_SHOW_MESSAGE, "GPS NOT ENABLE!");
            sendHandlerMessage(MainActivity.SIG_GPS_NOT_ENABLE, null);

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    TIME_INTEVAL, 0, locationChangeListener);
            sendHandlerMessage(MainActivity.SIG_SHOW_MESSAGE, "WAITTING FOR YOUR GPS...");
        }
    }

    /**
     * @return GPS是否已经打开
     */
    public void startTracker() {
        runningState = true;
    }

    public boolean isRunningState() {
        return runningState;
    }

    public void stopTracker() {
        runningState = false;
    }

    /**
     * 祛除GPS跟踪
     */
    public void removeGPS() {
        try {
            locationManager.removeUpdates(locationChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        runningState = false;
    }

    /**
     * 上传记录
     * 
     * @param onJsonSuccessReturnListener
     */
    public void updateRecord(OnJsonSuccessReturnListener onJsonSuccessReturnListener) {
        UploadRecord recoder = getUploadRecord();
        Gson gson = new Gson();
        String uploadinfo = gson.toJson(recoder);
        if (uploadinfo != null) {           
            HttpJsonProxy.getProxyBuilder().setAction(Action.POST)
                    .setOnSuccessListener(onJsonSuccessReturnListener)
                    .setURL(Applications.UPLOAD_URL).setClassOfT(ResultInfo.class)
                    .setEntityInstedForm(true).setEntityString(uploadinfo).execute();
        }

    }

}
