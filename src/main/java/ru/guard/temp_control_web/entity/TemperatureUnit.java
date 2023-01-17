package ru.guard.temp_control_web.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemperatureUnit {
    private String serviceName;
    private String name;
    private String wavFileName;
    private float actualTemperature;
    private float criticalTemperature;
    private boolean status;
    private boolean notification;
    private LocalDateTime dateOfTemperatureMeasurement;
}
