
package com.azuisapp.runner.util;

import java.util.ArrayList;

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
import com.azuisapp.runner.bean.UploadRecoder;

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
                sendHandlerMessage(MainActivity.SIG_SHOW_MESSAGE,"GPS IS READY");
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

    
    public  UploadRecoder getUploadRecoder(){
        ArrayList<Location> locations = Datasource.getInstance().getAllLocation();
        UploadRecoder recoder = new UploadRecoder();
        recoder.distance = getAllDistance();
        recoder.starttime = locations.get(0).getTime();
        recoder.endtime = locations.get(locations.size()).getTime();
        
        return recoder;
        
    }

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
            sendHandlerMessage(MainActivity.SIG_UPDATE_DISTANCE_SHOW,"");
        }

    }

   
    /**
     * 设置 context 初始化locationManager
     * 
     * @param context
     */
    public void setContextAndInit(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Datasource.getInstance().initDatabase(context);
    }

    /**
     * @return the unit is metter
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
        locationManager.removeUpdates(locationChangeListener);
        runningState = false;
    }

    /**
     * 上传记录
     */
    public void updateRecoder() {

    }

}
