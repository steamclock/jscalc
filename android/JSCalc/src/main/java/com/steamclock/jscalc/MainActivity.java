package com.steamclock.jscalc;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.Reader;

import junit.framework.Assert;

import org.mozilla.javascript.*;

import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    public TextView display;
    private Scriptable scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.textView);

        initJs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void buttonPressed (View view) {
        final Button button = (Button) view;

        runInJSContext(new JSRunnable() {
            @Override
            public void run(Context cx) {
                Scriptable calculator = (Scriptable)scope.get("calculator", scope);
                Function click = (Function) calculator.get("buttonPress", calculator);

                Object[] args = {button.getText()};

                click.call(cx, scope, calculator, args);
            }
        });
    }

    public void initJs() {
        runInJSContext(new JSRunnable() {
            @Override
            public void run(Context cx) {
                scope = cx.initStandardObjects();

                //set up console.log
                Object wConsole = Context.javaToJS(new ConsoleWrapper(), scope);
                ScriptableObject.putProperty(scope, "console", wConsole);

                //set up display widget
                Object wDisplay = Context.javaToJS(display, scope);
                ScriptableObject.putProperty(scope, "display", wDisplay);

                //load the code
                try {
                    Reader reader = getJSFileAsReader();
                    cx.evaluateReader(scope, reader, "initJs", 1, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Reader getJSFileAsReader() throws IOException {
        InputStream is = getAssets().open("calc.js");
        Log.d(TAG, "converting...");
        Reader reader = new InputStreamReader(is);
        return reader;
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
}