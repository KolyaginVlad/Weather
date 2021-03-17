package ru.same.weather;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static final String UNITS = "metric";
    private static final String KEY = "87af9e430e18ed3d99612325416fd8bd";
    private static final String LANG = "ru";
    private static WeatherApi weatherApi;
    private Retrofit retrofit;

    public static String getLANG() {
        return LANG;
    }

    public static String getUNITS() {
        return UNITS;
    }

    public static String getKEY() {
        return KEY;
    }

    public static WeatherApi getWeatherApi() {
        return weatherApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")//ставим снову url
                .addConverterFactory(GsonConverterFactory.create())//добавляем конвертор
                .build();
        weatherApi = retrofit.create(WeatherApi.class);

    }

}
