
package com.azuisapp.runner.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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

    public final static int SIG_UPDATE_DISTANCE_SHOW = 1;
    public final static int SIG_START_TIMER = 2;
    public final static int SIG_SHOW_MESSAGE = 3;
    public final static int SIG_SET_BUTTON_START = 4;

    private TextView distance_show_textview;
    private Chronometer chronometer;
    private Button startButton;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            refreshHandler.sendEmptyMessage(SIG_UPDATE_DISTANCE_SHOW);
        }
    };

    private Handler refreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SIG_UPDATE_DISTANCE_SHOW:
                    updateDistanceView();
                    break;
                case SIG_START_TIMER:
                    startTimer();
                    break;
                case SIG_SHOW_MESSAGE:
                    showMessage((String) msg.obj);
                    break;
                case SIG_SET_BUTTON_START:
                    stopTracker();
            }
            super.handleMessage(msg);
        }

    };

    private void setButton(boolean isStart) {
        if (isStart) {
            startButton.setText(this.getString(R.string.menu_action_start));
        }
        else {
            startButton.setText(this.getString(R.string.menu_action_stop));
        }
    }

    private void startTimer() {
        updateDistanceView();
        timer.schedule(task, TrackerUtil.TIME_INTEVAL, TrackerUtil.TIME_INTEVAL);
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

    public void clearRecoder(){
        Datasource.getInstance().clearAllLocation();
    }
    
    public void startTracker() {
        TrackerUtil.getInstance().startTracker();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        setButton(false);
    }

    public void stopTracker() {
        TrackerUtil.getInstance().stopTracker();
        chronometer.stop();
        timer.cancel();
        setButton(true);
    }

    public void uploadRecoder() {
        Double distance = TrackerUtil.getInstance().getAllDistance();
        distance_show_textview.setText(distance / 1000 + "Km");
    }

    @Override
    protected void onPause() {
        stopTracker();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (TrackerUtil.getInstance().isRunningState()) {
            stopTracker();
        } else {
            startTracker();
        }

    }

}
