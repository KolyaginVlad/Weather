package ru.same.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import ru.same.weather.api.PostWeather;

public class MainActivity extends AppCompatActivity implements Presenter.View {

    private Presenter presenter;
    private TextView temp;
    private TextView wind;
    private TextView humidity;
    private TextView description;
    private ImageView image;
    private TextView town;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Добавляем toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Инициализируем view
        temp = findViewById(R.id.temp);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        description = findViewById(R.id.des);
        image = findViewById(R.id.im);
        town = findViewById(R.id.town);
        progressBar = findViewById(R.id.proBar);
        Location location;
        //Получаем разрешение на использование интернета
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            location = new Location((LocationManager) getSystemService(LOCATION_SERVICE),
                    getApplicationContext());
            presenter = new Presenter(this, location);
            presenter.getData();//Получаем данные

        }
    }

    // Получаем подтверждение до того момента пока пользователь их не выдаст
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location location =
                            new Location((LocationManager) getSystemService(LOCATION_SERVICE),
                                    getApplicationContext());
                    presenter = new Presenter(this, location);
                    presenter.getData();
                } else {
                    ActivityCompat
                            .requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION},
                                    1);
                }
                return;
        }
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
                presenter.setId("511565");
                presenter.getData();
                break;
            case R.id.Moscow:
                presenter.setId("524894");
                presenter.getData();
                break;
            case R.id.Petersburg:
                presenter.setId("536203");
                presenter.getData();
                break;
            case R.id.Kazan:
                presenter.setId("551487");
                presenter.getData();
                break;
            case R.id.Novosibirsk:
                presenter.setId("1496747");
                presenter.getData();
                break;
            case R.id.Voronezh:
                presenter.setId("472045");
                presenter.getData();
                break;
            case R.id.Khabarovsk:
                presenter.setId("2022890");
                presenter.getData();
                break;
            case R.id.Sevastopol:
                presenter.setId("694423");
                presenter.getData();
                break;
            case R.id.Anadyr:
                presenter.setId("2127202");
                presenter.getData();
                break;
            case R.id.Tyumen:
                presenter.setId("1488754");
                presenter.getData();
                break;
            case R.id.auto:
                presenter.getDataWithAuto();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temp.setVisibility(View.INVISIBLE);
                description.setVisibility(View.INVISIBLE);
                wind.setVisibility(View.INVISIBLE);
                humidity.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                town.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void showViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temp.setVisibility(View.VISIBLE);
                description.setVisibility(android.view.View.VISIBLE);
                wind.setVisibility(android.view.View.VISIBLE);
                humidity.setVisibility(android.view.View.VISIBLE);
                image.setVisibility(android.view.View.VISIBLE);
                town.setVisibility(android.view.View.VISIBLE);
            }
        });
    }

    @Override
    public void setData(PostWeather postWeather) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temp.setText("Температура: " + postWeather.getMain().getTemp() + "°C");
                wind.setText("Скорость ветра: " + postWeather.getWind().getSpeed() + " м/с");
                humidity.setText("Влажность: " + postWeather.getMain().getHumidity() + "%");
                description.setText(postWeather.getWeather().get(0).getDescription().substring(0, 1)
                        .toUpperCase() + postWeather.getWeather().get(0).getDescription()
                        .substring(1));
                town.setText(postWeather.getName());
            }
        });
    }

    @Override
    public void setImage(Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    public void sayRepeat() {
        Toast.makeText(this, "Ваше местоположение не распознано, повторите через несколько секунд",
                Toast.LENGTH_LONG).show();
    }
}