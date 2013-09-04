package com.steamclock.rhinogyrotest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.util.Log;
import android.widget.TextView;

import org.mozilla.javascript.*;

public class MainActivity extends Activity implements SensorEventListener {
    public static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    //UI
    public TextView mAccuracy;
    public TextView mTimestamp;
    public TextView mX;
    public TextView mY;
    public TextView mZ;
    public TextView mC;
    public TextView mA;
    public TextView mFps;
    //js
    private Scriptable scope;
    private boolean mInJsMode;
    //FPS
    public long mLastFpsUpdate;
    public int mEventCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mAccuracy = (TextView) findViewById(R.id.accuracy);
        mTimestamp = (TextView) findViewById(R.id.timestamp);
        mX = (TextView) findViewById(R.id.valX);
        mY = (TextView) findViewById(R.id.valY);
        mZ = (TextView) findViewById(R.id.valZ);
        mC = (TextView) findViewById(R.id.valC);
        mA = (TextView) findViewById(R.id.valA);
        mFps = (TextView) findViewById(R.id.fps);

        initJs();

        mInJsMode = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();

        mLastFpsUpdate = System.nanoTime();
        mEventCount = 0;

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void initJs() {
        runInJSContext(new JSRunnable() {
            @Override
            public void run(Context cx) {
                scope = cx.initStandardObjects();//set up console.log

                //console.log
                Object wConsole = Context.javaToJS(new ConsoleWrapper(), scope);
                ScriptableObject.putProperty(scope, "console", wConsole);

                //ui access
                Object wActivity = Context.javaToJS(MainActivity.this, scope);
                ScriptableObject.putProperty(scope, "activity", wActivity);

                cx.evaluateString(scope, mJsCode, "initJs", 1, null);
            }
        });
    }

    private void runInJSContext(JSRunnable code) {
        final Context cx = Context.enter();
        cx.setOptimizationLevel(-1); //needed for the stock rhino jar. TODO: try the ASE jar instead.
        try {
            code.run(cx);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            Context.exit();
        }
    }
    private interface JSRunnable {
        public void run(Context cx);
    }
    public class ConsoleWrapper {
        public void log(String text) {
            Log.d("js-console", text);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        assert(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR);

        if (mInJsMode) {
            jsSensorHandler(sensorEvent);
        } else {
            nativeSensorHandler(sensorEvent);
        }
    }

    public void nativeSensorHandler(SensorEvent sensorEvent) {
        //Log.d(TAG, "event");
        mAccuracy.setText(String.valueOf(sensorEvent.accuracy));
        mTimestamp.setText(String.valueOf(sensorEvent.timestamp));
        mX.setText(String.valueOf(sensorEvent.values[0]));
        mY.setText(String.valueOf(sensorEvent.values[1]));
        mZ.setText(String.valueOf(sensorEvent.values[2]));
        if (sensorEvent.values.length > 3) {
            mC.setText(String.valueOf(sensorEvent.values[3]));
        }
        if (sensorEvent.values.length > 4) {
            mA.setText(String.valueOf(sensorEvent.values[4]));
        }
        mEventCount++;

        long now = System.nanoTime();
        if (now - mLastFpsUpdate >= 1e9) {
            //Log.d(TAG, "updating fps");
            mFps.setText(String.valueOf(mEventCount));
            mLastFpsUpdate = now;
            mEventCount = 0;
        }

    }

    public void jsSensorHandler(final SensorEvent sensorEvent) {
        //call handleEvent
        runInJSContext(new JSRunnable() {
            @Override
            public void run(Context cx) {
                Function handleEvent = (Function) scope.get("handleEvent", scope);
                Object wEvent = Context.javaToJS(sensorEvent, scope);
                Object[] args = {wEvent};
                handleEvent.call(cx, scope, scope, args);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "accuracy change");
    }

    private static final String mJsCode = "function handleEvent(sensorEvent){" +
            " console.log('got event');" +
            " activity.mAccuracy.setText(sensorEvent.accuracy.toString());" +
            " activity.mTimestamp.setText(sensorEvent.timestamp.toString());" +
            " activity.mX.setText(sensorEvent.values[0].toString());" +
            " activity.mY.setText(sensorEvent.values[1].toString());" +
            " activity.mZ.setText(sensorEvent.values[2].toString());" +
            " if (sensorEvent.values.length > 3) {" +
            "  activity.mC.setText(sensorEvent.values[3].toString());" +
            " }" +
            " if (sensorEvent.values.length > 4) {" +
            "  activity.mA.setText(sensorEvent.values[4].toString());" +
            " }" +
            " activity.mEventCount++;" +
            "" +
            " var now = java.lang.System.nanoTime();" +
            " if (now - activity.mLastFpsUpdate >= 1e9) {" +
            "  activity.mFps.setText(activity.mEventCount.toString());" +
            "  activity.mLastFpsUpdate = now;" +
            "  activity.mEventCount = 0;" +
            " }" +
            "}" +
            "console.log('js loaded');";
}
