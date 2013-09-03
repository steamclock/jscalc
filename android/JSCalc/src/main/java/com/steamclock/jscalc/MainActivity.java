package com.steamclock.jscalc;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import junit.framework.Assert;

import org.mozilla.javascript.*;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    public TextView display;
    private Scriptable scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.textView);

        testJs();
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
                Function testClick = (Function) scope.get("testClick", scope);
                Assert.assertNotNull(testClick);

                Object wButton = Context.javaToJS(button, scope);
                Object[] args = {wButton};

                Object result = testClick.call(cx, scope, scope, args);
                Log.d(TAG, Context.toString(result));
            }
        });
    }

    public void testJs() {
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


                String test = "console.log('o hai'); " +
                        "var text = 'hello javascript';" +
                        "display.setText(text);" +
                        "function testClick(button) {" +
                        " console.log(button.getText());" +
                        "}";

                Object result = cx.evaluateString(scope, test, "testJs", 1, null);
                Log.d(TAG, Context.toString(result));
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
}