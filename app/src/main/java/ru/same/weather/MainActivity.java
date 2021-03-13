package ru.same.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.same.weather.api.PostWeather;

public class MainActivity extends AppCompatActivity {


    private String id;
    private PostWeather postWeather;
    private TextView temp;
    private TextView wind;
    private TextView humidity;
    private TextView description;
    private ImageView image;
    private TextView town;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Добавляем toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Стандартный выбор - Пенза
        id = "511565";
        //Инициализируем view
        temp = findViewById(R.id.temp);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        description = findViewById(R.id.des);
        image = findViewById(R.id.im);
        town = findViewById(R.id.town);
        //Получаем разрешение на использование интернета
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else getData();//Получаем данные
        lat = null;
        lon = null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                lat = String.valueOf(locationManager.getLastKnownLocation(provider).getLatitude());
                lon = String.valueOf(locationManager.getLastKnownLocation(provider).getLongitude());
                locationManager.removeUpdates(locationListener);
            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);

    }

    public void getData() {
        temp.setText("Загружаем данные...");
        description.setVisibility(View.INVISIBLE);
        wind.setVisibility(View.INVISIBLE);
        humidity.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);
        town.setVisibility(View.INVISIBLE);
        App.getWeatherApi().getData(id, App.getUNITS(), App.getLANG(), App.getKEY()).enqueue(new Callback<PostWeather>() {
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

    public void getDataWithAuto() {
        if (lat != null && lon != null) {
            temp.setText("Загружаем данные...");
            description.setVisibility(View.INVISIBLE);
            wind.setVisibility(View.INVISIBLE);
            humidity.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            town.setVisibility(View.INVISIBLE);
            App.getWeatherApi().getData(lat, lon, App.getUNITS(), App.getLANG(), App.getKEY()).enqueue(new Callback<PostWeather>() {
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
        } else {
            Toast.makeText(this, "Ваше местоположение не распознано, повторите через несколько секунд", Toast.LENGTH_LONG).show();
        }
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
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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
                temp.setText("Температура: " + postWeather.getMain().getTemp() + "°C");
                wind.setText("Скорость ветра: " + postWeather.getWind().getSpeed() + " м/с");
                humidity.setText("Влажность: " + postWeather.getMain().getHumidity() + "%");
                description.setText(postWeather.getWeather().get(0).getDescription().substring(0, 1).toUpperCase() + postWeather.getWeather().get(0).getDescription().substring(1));
                town.setText(postWeather.getName());
                description.setVisibility(View.VISIBLE);
                wind.setVisibility(View.VISIBLE);
                humidity.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                town.setVisibility(View.VISIBLE);
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
    //Слушатель нажатий на элементы меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Выбор региона
        switch (id) {
            case R.id.Penza:
                this.id = "511565";
                getData();
                break;
            case R.id.Moscow:
                this.id = "524894";
                getData();
                break;
            case R.id.Petersburg:
                this.id = "536203";
                getData();
                break;
            case R.id.Kazan:
                this.id = "551487";
                getData();
                break;
            case R.id.Novosibirsk:
                this.id = "1496747";
                getData();
                break;
            case R.id.Voronezh:
                this.id = "472045";
                getData();
                break;
            case R.id.Khabarovsk:
                this.id = "2022890";
                getData();
                break;
            case R.id.Sevastopol:
                this.id = "694423";
                getData();
                break;
            case R.id.Anadyr:
                this.id = "2127202";
                getData();
                break;
            case R.id.Tyumen:
                this.id = "1488754";
                getData();
                break;
            case R.id.auto:
                getDataWithAuto();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}