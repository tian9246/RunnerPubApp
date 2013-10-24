
package com.azuisapp.runner.activity;

import android.app.Activity;
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

import com.azuisapp.runner.R;
import com.azuisapp.runner.util.Datasource;
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
    // public final static int SIG_GPS_

    private TextView distance_show_textview;
    private Chronometer chronometer;
    private Button startButton;

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
            }
            super.handleMessage(msg);
        }

    };

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

    /**
     * 显示
     * 
     * @param content
     */
    private void showMessage(String content) {
        distance_show_textview.setText(content);
    }

    private void updateDistanceView() {
        Double distance = TrackerUtil.getInstance().getAllDistance();
        distance_show_textview.setText(distance / 1000 + "Km");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.action_clear_recoder:
                clearRecoder();
                break;
            case R.id.action_stop:
                stopTracker();
                break;
            case R.id.action_upload:
                uploadRecoder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clearRecoder() {
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

    public void uploadRecoder() {
        Double distance = TrackerUtil.getInstance().getAllDistance();
        distance_show_textview.setText(distance / 1000 + "Km");
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackerUtil.getInstance().removeGPS();
    }

    /**
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        TrackerUtil.getInstance().initGPS();

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

}
