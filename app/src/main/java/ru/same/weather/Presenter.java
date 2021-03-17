package ru.same.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.same.weather.api.PostWeather;

public class Presenter {
    private PostWeather postWeather;
    private final Location mLocation;
    private final View view;
    private String id;

    public Presenter(View view, Location mLocation) {
        this.view = view;
        //Стандартный выбор - Пенза
        id = "511565";
        //Смотрим координаты
        this.mLocation = mLocation;
    }

    public void setId(String id) {
        this.id = id;
    }

    //Получаем данные от сервиса по id города
    public void getData() {
        view.hideViews();
        view.showProgressBar();
        App.getWeatherApi().getData(id, App.getUNITS(), App.getLANG(), App.getKEY()).enqueue(new Callback<PostWeather>() {
            @Override
            public void onResponse(Call<PostWeather> call, Response<PostWeather> response) {
                postWeather = response.body();
                setImage();
            }

            @Override
            public void onFailure(Call<PostWeather> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //Скачиваем изображение и показываем его
    private void setImage() {
        view.updateProgressBar();
        OkHttpClient client = new OkHttpClient();
        Log.d("id", "https://openweathermap.org/img/wn/" + postWeather.getWeather().get(0).getIcon() + "@2x.png");
        Request request = new Request.Builder()
                .url("https://openweathermap.org/img/wn/" + postWeather.getWeather().get(0).getIcon() + "@2x.png")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                if (response.body() != null) {
                    try {
                        byte[] img = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                        //image.setImageBitmap(bitmap);
                        view.setImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                setData();
            }
        });
    }

    //Загружаем данные по автоматически определённым координатам
    public void getDataWithAuto() {
        if (mLocation.getLat() != null && mLocation.getLon() != null) {
            view.hideViews();
            view.showProgressBar();
            App.getWeatherApi().getData(mLocation.getLat(), mLocation.getLon(), App.getUNITS(), App.getLANG(), App.getKEY()).enqueue(new Callback<PostWeather>() {
                @Override
                public void onResponse(Call<PostWeather> call, Response<PostWeather> response) {
                    postWeather = response.body();
                    setImage();
                }

                @Override
                public void onFailure(Call<PostWeather> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            view.sayRepeat();
        }
    }

    //Показываем полученные данные
    private void setData() {
        view.setData(postWeather);
        view.showViews();
        view.hideProgressBar();
    }

    public interface View {
        void showProgressBar();

        void hideProgressBar();

        void updateProgressBar();

        void hideViews();

        void showViews();

        void setData(PostWeather postWeather);

        void setImage(Bitmap bitmap);

        void sayRepeat();
    }
}
