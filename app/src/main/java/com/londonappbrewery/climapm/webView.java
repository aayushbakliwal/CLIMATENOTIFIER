package com.londonappbrewery.climapm;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class webView extends Activity {
    WebView wv;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherforcast);
        wv = (WebView)findViewById(R.id.wvi);
        wv.loadUrl("http://www.weather.com/wx/today/?lat=12.64&lon=77.44&locale=en_IN&par=google&temp=c");

    }
}
