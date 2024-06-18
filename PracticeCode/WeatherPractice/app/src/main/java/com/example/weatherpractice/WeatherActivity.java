package com.example.weatherpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherpractice.gson.Forecast;
import com.example.weatherpractice.gson.Weather;
import com.example.weatherpractice.util.HttpUtil;
import com.example.weatherpractice.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherActivity extends AppCompatActivity {
    public static final String HTTP_GUOLIN_TECH_API_WEATHER_CITYID = "http://guolin.tech/api/weather?cityid=";
    public static final String HTTP_GUOLIN_TECH_API_BING_PIC = "https://cn.apihz.cn/api/img/bingapi.php?id=88888888&key=88888888&type=1";
    public static final String WEATHER_REQUEST_KEY = "&key=bc0418b57b2d4918819d3974ac1285d9";
    private ScrollView weatherLayout;
    private TextView titleCity, titleUpdateTime, degreeText, weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText, pm25Text, comfortText, carWashText, sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;

    public DrawerLayout drawerLayout;

    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        setupNavButton();

        initializePageLayout();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(pub.devrel.easypermissions.R.color.colorPrimary);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = preferences.getString("weather",null);
        final String weatherID;
        if (weatherString != null) {
            // 有缓存，解析天气
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherID = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存，请求天气数据
            weatherID = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherID);
        }

        swipeRefreshLayout.setOnRefreshListener(()->{
            Log.d("ddd", "onCreate: weather id"+weatherID);
            requestWeather(weatherID);
        });

        createBingPicImage(preferences);

    }

    private void setupNavButton() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }


    private void createBingPicImage(SharedPreferences preferences) {
        bingPicImg = findViewById(R.id.bing_pic_img);
        String bingPic = preferences.getString("bing_pic",null);
        if (bingPic != null) {
            Log.d("ddd", "createBingPicImage: bingPic"+bingPic);
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * load pic from bing
     */
    private void loadBingPic() {
        String requestBingPic = HTTP_GUOLIN_TECH_API_BING_PIC;
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String bingPic = response.body().string();
                try {
                    JSONObject picObject = new JSONObject(bingPic);
                    String picURL = picObject.getString("msg");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic",picURL);
                    editor.apply();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(picURL).into(bingPicImg);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
    }

    /**
     * 根据天气id请求数据
     */
    public void requestWeather(String weatherID) {
        String weatherURL = HTTP_GUOLIN_TECH_API_WEATHER_CITYID +
                weatherID + WEATHER_REQUEST_KEY;
        HttpUtil.sendOkHttpRequest(weatherURL, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("WD", "request weather failed:", e);
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this,"获取天气失败", Toast.LENGTH_SHORT).show();
                            Log.d("WD", "get weather object failed");
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        for(Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒服度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void initializePageLayout() {
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
    }
}