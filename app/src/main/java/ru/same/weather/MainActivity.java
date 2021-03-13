package ru.same.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.same.weather.api.PostWeather;

public class MainActivity extends AppCompatActivity {
    //Penza 511565
    //Moscow 524894
    //St. Petersburg 536203
    //Kazan 551487
    //Novosibirsk 1496747
    //Voronezh 472045
    //Khabarovsk 2022890
    //Sevastopol 694423
    //Anadyr 2127202
    //Tyumen 1488754
//    private Map<String, Integer> regMaps;

    private String id;
    private PostWeather postWeather;
    private TextView temp;
    private TextView wind;
    private TextView humidity;
    private TextView description;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Добавляем toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TODO: 13.03.2021 загружать прошлый выбор региона
        id = "511565";
        //Создаём словарь городов
//        regMaps = new HashMap<>();
//        regMaps.put("Пенза", 511565);
//        regMaps.put("Москва", 524894);
//        regMaps.put("Санкт-Петербург", 536203);
//        regMaps.put("Казань", 551487);
//        regMaps.put("Новосибирск", 1496747);
//        regMaps.put("Воронеж", 472045);
//        regMaps.put("Хабаровск", 2022890);
//        regMaps.put("Севастополь", 694423);
//        regMaps.put("Анадырь", 2127202);
//        regMaps.put("Тюмень", 1488754);
        temp = findViewById(R.id.temp);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        description = findViewById(R.id.des);
        image = findViewById(R.id.im);
        temp.setText("Загружаем данные...");
        description.setVisibility(View.INVISIBLE);
        wind.setVisibility(View.INVISIBLE);
        humidity.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    1);
        } else getData();


    }

    public void getData() {
        App.getWeatherApi().getData(id, App.getUNITS(), App.getKEY()).enqueue(new Callback<PostWeather>() {
            @Override
            public void onResponse(Call<PostWeather> call, Response<PostWeather> response) {
                postWeather = response.body();
                temp.setText("Скачиваем изображение...");
                setImage();
            }

            @Override
            public void onFailure(Call<PostWeather> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    getData();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                            1);
                }
                return;
        }
    }

    private void setImage() {
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
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                byte[] img = response.body().bytes();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                                image.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
                setData();
            }
        });
    }

    private void setData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temp.setText(postWeather.getMain().getTemp() + "°C");
                wind.setText(postWeather.getWind().getSpeed() + " м/с");
                humidity.setText(postWeather.getMain().getHumidity() + "%");
                description.setText(postWeather.getWeather().get(0).getDescription());
                description.setVisibility(View.VISIBLE);
                wind.setVisibility(View.VISIBLE);
                humidity.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
            }
        });

    }

    //Добавляем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Слушатель нажатий на элементы меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.chooseReg) {
            //Переход на активность выбора региона
        }
        return super.onOptionsItemSelected(item);
    }
}