package com.steamclock.jscalc;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.mozilla.javascript.*;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    public TextView display;

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

    public void testJs() {
        Context cx = Context.enter();
        cx.setOptimizationLevel(-1); //needed for the stock rhino jar. TODO: try the ASE jar instead.
        try {
            Scriptable scope = cx.initStandardObjects();

            //set up console.log
            Object console = Context.javaToJS(new ConsoleWrapper(), scope);
            ScriptableObject.putProperty(scope, "console", console);

            //set up display widget
            Object wDisplay = Context.javaToJS(display, scope);
            ScriptableObject.putProperty(scope, "display", wDisplay);


            String test = "console.log('o hai'); " +
                    "var text = 'hello javascript';" +
                    "display.setText(text);";

            Object result = cx.evaluateString(scope, test, "testJs", 1, null);

            Log.d(TAG, Context.toString(result));
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            Context.exit();
        }
    }

    public class ConsoleWrapper {
        public void log(String text) {
            Log.d("js-console", text);
        }
    }
}