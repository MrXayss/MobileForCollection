package com.example.compascoord;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener {


    //Объявляем работу с сенсором устройства
    private SensorManager mSensorManager;
    //Объявляем объект TextView
    TextView CompOrient;
    TextView tvLocationNet;
    Button push_bottom,btnPhoto;


    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    DBHelper dbHelper;
    private String zap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Связываем объект ImageView с нашим изображением:
        push_bottom = (Button) findViewById(R.id.button1);
        //TextView в котором будет отображаться градус поворота:
        CompOrient = (TextView) findViewById(R.id.Header);
        push_bottom.setOnClickListener(this);

        btnPhoto = (Button) findViewById(R.id.button2);
        btnPhoto.setOnClickListener(this);

        //Инициализируем возможность работать с сенсором устройства:
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        tvLocationNet = findViewById(R.id.tvLocationNet);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        dbHelper = new DBHelper(this);
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

        //Устанавливаем слушателя ориентации сенсора
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

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
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                0, 1, locationListener);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 1,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Останавливаем при надобности слушателя ориентации
        //сенсора с целью сбережения заряда батареи:
        mSensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Получаем градус поворота от оси, которая направлена на север, север = 0 градусов:
        s = event.values[0];
        float degree = Math.round(event.values[0]);
        CompOrient.setText("Отклонение от севера: " + Float.toString(degree) + " градусов");
//        String a = Float.toString(degree);
//        SendLoginData UploadFileAsync1 = new SendLoginData();
//        UploadFileAsync1.parammetrs = a;
//        UploadFileAsync1.execute();

    }
    private float s;
    private double b;
    private double c;
    protected void upload() {

        float degree = Math.round(this.s);
        String a = Float.toString(degree);
//        SendLoginData UploadFileAsync1 = new SendLoginData();
//        UploadFileAsync1.parammetrs = a;
//        UploadFileAsync1.execute();
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("gradus" , a);
            post_dict.put("coord1", b);
            post_dict.put("coord2", c);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            new SendDeviceDetails().execute(String.valueOf(post_dict));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Этот метод не используется, но без него программа будет ругаться
    }

    private LocationListener locationListener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {
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
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }
    }


    private String formatLocation(Location location) {
        if (location == null)
            return "";
        b = location.getLatitude();
        c = location.getLongitude();
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f",
                location.getLatitude(), location.getLongitude());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                //upload();
                float degree = Math.round(this.s);
                String a = Float.toString(degree);
                UploadFileAsync UploadFileAsync1 = new UploadFileAsync();
                UploadFileAsync1.filename =zap;
                UploadFileAsync1.gradus =a;
                UploadFileAsync1.latitude =b;
                UploadFileAsync1.longtitude =c;
                UploadFileAsync1.execute();
                break;
            case R.id.button2:
                Intent intent = new Intent(MainActivity.this, TakeShoot.class);
                startActivityForResult(intent,1);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data==null)
        {
            return;
        }
        zap =  data.getStringExtra("name");
    }
}
