
package com.azuisapp.runner.activity;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.azuisapp.runner.R;
import com.azuisapp.runner.bean.ResultInfo;
import com.azuisapp.runner.net.OnJsonSuccessReturnListener;
import com.azuisapp.runner.util.Datasource;
import com.azuisapp.runner.util.LoginUtil;
import com.azuisapp.runner.util.TrackerUtil;

/**
 * 主界面
 * 
 * @author hansontian
 */
public class MainActivity extends Activity implements View.OnClickListener {
    public final static int BUTTON_STATE_START = 1;
    public final static int BUTTON_STATE_STOP = 2;
    public final static int BUTTON_STATE_NOTREADY = 3;

    /* 显示里程数 */
    public final static int SIG_UPDATE_DISTANCE_SHOW = 1;
    /* 显示信息 */
    public final static int SIG_SHOW_MESSAGE = 3;
    /* TRACKER READY */
    public final static int SIG_GPS_READY = 4;

    public final static int SIG_GPS_NOT_ENABLE = 5;

    public final static int SIG_SHOW_TOAST = 6;

    // public final static int SIG_GPS_

    private TextView distance_show_textview;
    private Chronometer chronometer;
    private Button startButton;
    private ProgressDialog progressDialog;

    private Handler refreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SIG_UPDATE_DISTANCE_SHOW:
                    updateDistanceView();
                    break;
                case SIG_SHOW_MESSAGE:
                    showMessage((String) msg.obj);
                    break;
                case SIG_GPS_READY:
                    setButton(BUTTON_STATE_START);
                    break;
                case SIG_GPS_NOT_ENABLE:
                    openGPS();
                    break;
                case SIG_SHOW_TOAST:
                    showToast((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * 设置按钮状态
     * 
     * @param state BUTTON_STATE_START
     */
    private void setButton(int state) {
        if (state == BUTTON_STATE_START) {
            startButton.setText(this.getString(R.string.menu_action_start));
            startButton.setEnabled(true);
        }
        else if (state == BUTTON_STATE_STOP) {
            startButton.setText(this.getString(R.string.menu_action_stop));
            startButton.setEnabled(true);
        } else if (state == BUTTON_STATE_NOTREADY) {
            startButton.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* 阻止锁屏 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TrackerUtil.getInstance().setContextAndInit(this, refreshHandler);
        distance_show_textview = (TextView) findViewById(R.id.activity_distance_show);
        chronometer = (Chronometer) findViewById(R.id.activity_chronometer);
        startButton = (Button) findViewById(R.id.activity_button);
        startButton.setOnClickListener(this);
        setButton(BUTTON_STATE_NOTREADY);       
    }
    
    /**
     * 检查是否登陆
     * @return
     */
    private boolean checkLogined(){
        if(!LoginUtil.getInstance().readLoginInfo()){
            showToast("Login First,Please~");
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.action_clear_recoder:
                clearRecord();
                break;
            case R.id.action_stop:
                stopTracker();
                break;
            case R.id.action_upload:
                uploadRecord();
                break;
            case R.id.action_logout:
                logout();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 登出功能
     */
    private void logout(){
        LoginUtil.getInstance().logout();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 显示content到主界面的textView上面
     * 
     * @param content
     */
    private void showMessage(String content) {
        distance_show_textview.setText(content);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackerUtil.getInstance().removeGPS();
    }

  
    @Override
    protected void onResume() {
        super.onResume();
        if(checkLogined()){
            TrackerUtil.getInstance().initGPS();
        }
    }

    @Override
    public void onClick(View v) {
        if (TrackerUtil.getInstance().isRunningState()) {
            stopTracker();
        } else {
            startTracker();
        }
    }

    /**
     * 更新里程距离
     */
    private void updateDistanceView() {
        Double distance = TrackerUtil.getInstance().getAllDistance();
        distance_show_textview.setText(new DecimalFormat("#.00").format(distance / 1000) + "Km");
    }

    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    public void clearRecord() {
        Datasource.getInstance().clearAllLocation();
    }

    public void startTracker() {
        TrackerUtil.getInstance().startTracker();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        setButton(BUTTON_STATE_STOP);
    }

    public void stopTracker() {
        TrackerUtil.getInstance().stopTracker();
        updateDistanceView();
        chronometer.stop();
        setButton(BUTTON_STATE_START);
    }

    /**
     * 上传记录信息
     */
    public void uploadRecord() {
        Double distance = TrackerUtil.getInstance().getAllDistance();
        if (distance >= 0) {
            TrackerUtil.getInstance().updateRecord(new OnJsonSuccessReturnListener() {
                @Override
                public void onSuccess(Object objDeserialized) {
                    ResultInfo result = (ResultInfo) objDeserialized;
                    handleUploadResult(result);
                }
            });
        } else {
            // 没有记录
            showToast("No recoder press start button first");
        }
    }

    /**
     * 处理返回的结果信息
     * 
     * @param result
     */
    private void handleUploadResult(ResultInfo result) {
        if (result.status.equals("success")) {
            showMessage("Upload Record Successed!");
        } else {
            showMessage("Upload Fail~");
        }
    }

    /**
     * 强制帮用户打开GPS
     * 
     * @param context
     */
    public void openGPS() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            startActivity(intent);

        } catch (ActivityNotFoundException ex)
        {
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 显示信息对话框
     */
    public void showUploadeDialog() {
        progressDialog = ProgressDialog.show(this, "Uploading...", "Please wait...", true, false);
        progressDialog.show();
    }

    /**
     * 隐藏信息对话框
     */
    public void hideUploadDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

}
