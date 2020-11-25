package com.example.nathanielweatherappassessment.service;

import android.app.Activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nathanielweatherappassessment.model.CurrentWeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherService {

    private static final String TAG = WeatherService.class.getSimpleName();

    private static final String URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String CURRENT_WEATHER_TAG = "Current weather tg";
    private static final String API_KEY = "8e27b4038726f8564a3a6868535e3ad1";

    private RequestQueue requestQuee;

    public WeatherService(@NonNull final Activity activity) {
        requestQuee = Volley.newRequestQueue(activity.getApplicationContext());
    }

    public interface WeatherCallback {
        @MainThread
        void onCurrentWeather(@NonNull final CurrentWeatherData currentWeatherData);

        @MainThread
        void onError(@Nullable Exception exception);
    }

    public void getCurrentWeather(@NonNull final String locationName, @NonNull final WeatherCallback callback) {
        final String url = String.format("%s?q=%s&appId=%s", URL, locationName, API_KEY);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject currentWeatherJSONObject = new JSONObject(response);
                            final JSONArray weather = currentWeatherJSONObject.getJSONArray("weather");
                            final JSONObject weatherCondition = weather.getJSONObject(0);
                            final String locationName = currentWeatherJSONObject.getString("name");
                            //final String weatherDescription = currentWeatherJSONObject.getString("description");
                            final int conditionId = weatherCondition.getInt("id");
                            final String conditionName = weatherCondition.getString("main");
                            final double tempKelvin = currentWeatherJSONObject.getJSONObject("main").getDouble("temp");
                            final CurrentWeatherData currentWeatherData = new CurrentWeatherData(locationName, conditionId, conditionName, tempKelvin);
                            callback.onCurrentWeather(currentWeatherData);
                        } catch (JSONException e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        stringRequest.setTag(CURRENT_WEATHER_TAG);
        requestQuee.add(stringRequest);
    }

    public void cancel() {
        requestQuee.cancelAll(CURRENT_WEATHER_TAG);
    }
}
