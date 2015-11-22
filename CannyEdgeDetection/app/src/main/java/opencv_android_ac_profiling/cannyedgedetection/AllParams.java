package opencv_android_ac_profiling.cannyedgedetection;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/*import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;*/

public class AllParams extends AppCompatActivity {
    private static String TAG = "AllParams";
    private ProgressBar progress;
    private TextView text;
    private Button button;

    /*static {
        if (!OpenCVLoader.initDebug()) {
            Log.i(TAG, "Some error on opencv init");
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_params);
        progress = (ProgressBar) findViewById(R.id.progressBar1);
        text = (TextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.startProfile);
    }

    public void startProfile(View view) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                IterativePowerMethod.main();
                //Log.i(TAG, "I am done now");
                button.post(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("Profiling Done");
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_params, menu);
        return true;     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
