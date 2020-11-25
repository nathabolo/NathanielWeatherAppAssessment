package com.example.nathanielweatherappassessment.model;

public class CurrentWeatherData {
    public final String location;
    public final int conditionId;
    public final String weatherCondition;
    //public final String description;
    final double tempKelvin;

    public CurrentWeatherData(final String location,
                              final int conditionId,
                              final String weatherCondition,
                             // final String description,
                              final double tempKelvin) {
        this.location = location;
        this.conditionId = conditionId;
        this.weatherCondition = weatherCondition;
        //this.description = description;
        this.tempKelvin = tempKelvin;
    }

    public int getTempFahrenheit() {
        return (int) ((int) (tempKelvin) - 273.15);
    }

    }

