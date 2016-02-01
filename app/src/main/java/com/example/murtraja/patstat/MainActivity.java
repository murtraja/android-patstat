package com.example.murtraja.patstat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView sensorData;
    private int mInterval = 2000;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sMgr;
        sMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        List<Sensor> list = sMgr.getSensorList(Sensor.TYPE_ALL);
        sensorData = (TextView) findViewById(R.id.sensorData);
        final Button postbtn = (Button) findViewById(R.id.postButton);
        boolean flag = true;
        for(Sensor sensor: list){
            if(sensor.getType()==sensor.TYPE_LIGHT)
            {
                Log.d("MMR", sensor.toString());
                sensorData.setText("Light sensor available!");
                flag = false;
            }
        }
        if (flag)
        {
            Log.d("MMR", "no light sensor found");
            sensorData.setText("Light sensor not found");
            postbtn.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No light sensor found! Sorry...")
                    .setTitle("H/w N/A");
            final Activity act = this;
            builder.setPositiveButton("exit", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    act.finish();
                }
            }).show();
            //AlertDialog dialog = builder.create();

        }
        sMgr.registerListener(this, sMgr.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

        postbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText etName = (EditText)findViewById(R.id.sensorName);
                String Name = etName.getText().toString();
                EditText etServer = (EditText)findViewById(R.id.serverURI);
                String serverURL = etServer.getText().toString();
                Log.d("MMR", Name);
                if(Name==null || "".equals(Name))
                {
                    etName.setError("required");
                    return;
                }
                if(serverURL == null || "".equals(serverURL))
                {
                    etServer.setError("required");
                    return;
                }
                PostData pd = new PostData();
                pd.execute(serverURL, Name, sensorData.getText().toString());
            }
        });
        mHandler = new Handler();
        final Runnable periodicPoster = new Runnable() {
            @Override
            public void run() {
                try{
                    postbtn.callOnClick();
                }finally{
                    mHandler.postDelayed(this, mInterval);
                }
            }
        };
        Button startTemp = (Button) findViewById(R.id.startPostTemp);
        Button stopTemp = (Button) findViewById(R.id.stopPostTemp);
        startTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                periodicPoster.run();
            }
        });
        stopTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(periodicPoster);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_LIGHT)
        {

            sensorData.setText(Float.toString(event.values[0]));
            //Log.d("MMR","onSensorChanged invoked");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType()==sensor.TYPE_LIGHT)
        {
            Log.d("MMR", "onAccuracyChanged invoked");
        }
    }
}
