package com.example.compascoord;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity implements  View.OnClickListener, SensorEventListener {

    private SensorManager mSensorManager;;
    TextView tvLocationNet;
    TextView ViewID;
    TextView IDTraffic;
    TextView accuracy;
    Button push_bottom,btnPhoto,btnadd,btndrop;
    EditText location;

    ArrayList<Parcelable> XArray;
    ArrayList<Parcelable> YArray;
    ArrayList<Parcelable> ZArray;
    private LocationManager locationManager;
    private String filename;
    private String text_signal;
    private String text_degree;
    public String deviceId;
    public String TrafficId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        push_bottom = (Button) findViewById(R.id.button1);
        push_bottom.setOnClickListener(this);

        btnPhoto = (Button) findViewById(R.id.button2);
        btnPhoto.setOnClickListener(this);

        btnadd = (Button) findViewById(R.id.button);
        btnadd.setOnClickListener(this);

        btndrop = (Button) findViewById(R.id.button3);
        btndrop.setOnClickListener(this);
        btndrop.setVisibility(View.INVISIBLE);

        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        ViewID = (TextView) findViewById(R.id.textView);
        ViewID.setText("ID: " + deviceId);

        IDTraffic = (TextView) findViewById(R.id.textView2);

        accuracy = (TextView) findViewById(R.id.textView7);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        tvLocationNet = findViewById(R.id.tvLocationNet);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        location =findViewById(R.id.editTextTextPersonName);

        ArrayList<String> permissions_to_request = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissions_to_request.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissions_to_request.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissions_to_request.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissions_to_request.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissions_to_request.size() > 0) {
            String[] _permissions = permissions_to_request.toArray(new String[permissions_to_request.size()]);
            ActivityCompat.requestPermissions(this,
                    _permissions,
                    1);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }


        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 200, 1,
                locationListener);
//        locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER, 0, 1,
//                locationListener);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private double latitude;
    private double longtitude;
    protected void upload() throws ExecutionException, InterruptedException {

        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("name_traffic" ,location.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //new SendDeviceDetails().execute(String.valueOf(post_dict)).get();
        TrafficId = String.valueOf(new SendDeviceDetails().execute(String.valueOf(post_dict)).get());
        location.setVisibility(View.INVISIBLE);
        btnadd.setVisibility(View.INVISIBLE);
        IDTraffic.setText("ID светофора: " + TrafficId);
        IDTraffic.setVisibility(View.VISIBLE);
        btndrop.setVisibility(View.VISIBLE);
    }


    private LocationListener locationListener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {
            Log.i("sas", String.valueOf(location));
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


        @Override
        public void onProviderEnabled(String provider) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(
                LocationManager.GPS_PROVIDER)) {
//            LocationManager.NETWORK_PROVIDER)) {
            accuracy.setText("Точность:"+String.valueOf(location.getAccuracy()));
            tvLocationNet.setText(formatLocation(location));
            push_bottom.setVisibility(View.VISIBLE);
        }
    }


    private String formatLocation(Location location) {
        if (location == null)
            return "";
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        return String.format(
                "   Широта = %1$f, Долгота = %2$f",
                location.getLatitude(), location.getLongitude());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                JSONObject post_dict = new JSONObject();

                UploadFileAsync UploadFileAsync1 = new UploadFileAsync();
                UploadFileAsync1.filename =filename;
                UploadFileAsync1.gradus = text_degree;
                UploadFileAsync1.latitude =latitude;
                UploadFileAsync1.longtitude =longtitude;
                UploadFileAsync1.id_device =deviceId;
                UploadFileAsync1.text_signal =text_signal;
                UploadFileAsync1.text_loc =TrafficId;
                try {
                    post_dict.put("x" , XArray);
                    post_dict.put("y", YArray);
                    post_dict.put("z", ZArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UploadFileAsync1.json =post_dict;
                UploadFileAsync1.execute();
                break;
            case R.id.button2:
                Intent intent = new Intent(MainActivity.this, TakeShoot.class);
                startActivityForResult(intent,1);
                break;
            case R.id.button:
                try {
                    upload();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button3:
                IDTraffic.setVisibility(View.INVISIBLE);
                TrafficId = null;
                location.setVisibility(View.VISIBLE);
                location.setText("");
                btnadd.setVisibility(View.VISIBLE);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data==null)
        {
            return;
        }
        filename =  data.getStringExtra("name");
        text_signal =  data.getStringExtra("text_signal");
        text_degree = data.getStringExtra("text_degree");
        XArray = data.getParcelableArrayListExtra("xArray");
        YArray = data.getParcelableArrayListExtra("yArray");
        ZArray = data.getParcelableArrayListExtra("zArray");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
