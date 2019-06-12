package com.londonappbrewery.climapm;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE=123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    Button button;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);
        button =(Button)findViewById(R.id.button);




        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clima","button clicked");
                Intent myIntent =new Intent(WeatherController.this,ChangeCityController.class);
                startActivity(myIntent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clima","button clicked");
                Intent myIntent =new Intent(WeatherController.this,WebController.class);
                startActivity(myIntent);
                finish();
            }
        });

    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("clima", "onResume() called");
        Intent myIntent=getIntent();
        String city=myIntent.getStringExtra("city");

        if (city!=null){
            getWeatherForNewCity(city);
        }else {


            Log.d("clima", "Getting weather location");
            getWeatherForCurrentLoaction();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private  void  getWeatherForNewCity(String city){
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);


    }


    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLoaction() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("clima", "onLocationChanged() callback");
                String longitude=String.valueOf(location.getLongitude());
                String latitude=String.valueOf(location.getLatitude());

                Log.d("clima","longitude= "+longitude);
                Log.d("clima","Latitude ="+latitude);
                RequestParams params=new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("clima", "onstatuschanged() callback");

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("clima", "onProvideEnabled() callback");

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("clima", "onProviderDisabled() callback");

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("clima", "0nRequestPermissionResult(): Permission granted");
                getWeatherForCurrentLoaction();
            } else {
                Log.d("clima", "permission denied=");
            }
        }

    }
// TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private  void letsDoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public  void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("clima","Successful JSON !"+response.toString());
                WeatherDataModel  weatherData= WeatherDataModel.fromJson(response);
                updateUI(weatherData);
                addNotification(weatherData);

            }
            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
                Log.d("clima","failed "+e.toString());
                Log.d("clima","status code "+statusCode);
                Toast.makeText(WeatherController.this, "Request failed !!", Toast.LENGTH_SHORT).show();

            }

        });
    }



    // TODO: Add updateUI() here:
    private  void  updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getmTemperature());
        mCityLabel.setText(weather.getmCity());

        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);

    }



    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();

        if(mLocationManager!=null)
            mLocationManager.removeUpdates(mLocationListener);
    }
    private void addNotification(WeatherDataModel weather) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cloudy2) //set icon for notification
                        .setContentTitle(weather.getmCity()) //set title of notification
                        .setContentText(weather.getmTemperature())//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, WebController.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}

