package com.example.nathanielweatherappassessment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nathanielweatherappassessment.model.CurrentWeatherData;
import com.example.nathanielweatherappassessment.service.WeatherService;
import com.example.nathanielweatherappassessment.utils.WeatherUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WeatherService weatherService;

    private View weatherContainerData;
    private ProgressBar progressBar;
    private TextView temperature, location, weatherConditionData;
    private ImageView weatherConditionImage;
    private EditText fieldLocation;
    private FloatingActionButton btn_fab;

    private boolean fetchingWeatherData = false;
    private int countTxt = 0;
    private String currentLocation = "Pretoria";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherService = new WeatherService(this);

        weatherContainerData = findViewById(R.id.weather_container_data);
        progressBar = findViewById(R.id.progress_bar);
        temperature = findViewById(R.id.temperature_units);
        location = findViewById(R.id.location);
        weatherConditionData = findViewById(R.id.weather_condition_data);
        weatherConditionImage = findViewById(R.id.weather_condition_image);
       // temperature_descripton = findViewById(R.id.temperature_descripton);
        fieldLocation = findViewById(R.id.field_location);
        btn_fab = findViewById(R.id.btn_fab);

        fieldLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                count = s.toString().trim().length();
                btn_fab.setImageResource(count == 0 ? R.drawable.ic_refresh : R.drawable.ic_search);
                countTxt = count;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btn_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countTxt == 0) {
                    refreshWeather();
                } else {
                    weatherSearch(fieldLocation.getText().toString());
                    fieldLocation.setText("");
                }
            }
        });

        weatherSearch(currentLocation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherService.cancel();
    }

    private void refreshWeather() {
        if (fetchingWeatherData) {
            return;
        }
        weatherSearch(currentLocation);
    }

    private void weatherSearch(@NonNull final String location) {
        toggleProgress(true);
        fetchingWeatherData = true;
        weatherService.getCurrentWeather(location, weatherCallback);
    }

    private void toggleProgress(final boolean showProgress) {
        weatherContainerData.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    private final WeatherService.WeatherCallback weatherCallback = new WeatherService.WeatherCallback() {

        @Override
        public void onCurrentWeather(@NonNull CurrentWeatherData currentWeatherData) {
            currentLocation = currentWeatherData.location;
            temperature.setText(String.valueOf(currentWeatherData.getTempFahrenheit()));
            location.setText(currentWeatherData.location);
            weatherConditionData.setText(currentWeatherData.weatherCondition);
          //  temperature_descripton.setText(currentWeatherData.description);
            weatherConditionImage.setImageResource(WeatherUtils.getWeatherIconResId
                    (currentWeatherData.conditionId));
            toggleProgress(false);
            fetchingWeatherData = false;
        }

        @Override
        public void onError(@Nullable Exception exception) {
            toggleProgress(false);
            fetchingWeatherData = false;
            Toast.makeText(MainActivity.this, "Error fetching weather data, please " +
                    "try again.", Toast.LENGTH_SHORT).show();
        }
    };
}
