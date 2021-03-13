package ru.same.weather;

import android.Manifest;
import android.app.Application;

import androidx.core.content.ContextCompat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static WeatherApi weatherApi;
    private Retrofit retrofit;
    private static final String UNITS = "metric";
    private static final String KEY = "87af9e430e18ed3d99612325416fd8bd";

    public static String getUNITS() {
        return UNITS;
    }

    public static String getKEY() {
        return KEY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")//ставим снову url
                .addConverterFactory(GsonConverterFactory.create())//добавляем конвертор
                .build();
        weatherApi = retrofit.create(WeatherApi.class);
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

    }

    public static WeatherApi getWeatherApi() {
        return weatherApi;
    }

}
