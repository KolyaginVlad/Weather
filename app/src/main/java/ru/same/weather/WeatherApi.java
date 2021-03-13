package ru.same.weather;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.same.weather.api.PostWeather;

public interface WeatherApi {
    @GET("/data/2.5/weather")
    Call<PostWeather> getData(@Query("id") String id, @Query("units") String units, @Query("lang") String language, @Query("appid") String key);

    @GET("/data/2.5/weather")
    Call<PostWeather> getData(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units, @Query("lang") String language, @Query("appid") String key);
}
