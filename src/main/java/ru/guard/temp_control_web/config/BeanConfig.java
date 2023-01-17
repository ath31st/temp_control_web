package ru.guard.temp_control_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.guard.temp_control_web.entity.TemperatureUnit;

import java.time.LocalDateTime;

@Configuration
public class BeanConfig {
    @Value("${temperature-unit.name1}")
    private String unitServiceName1;
    @Value("${temperature-unit.wav.file.name1}")
    private String unitWavFileName1;
    @Value("${temperature-unit.name2}")
    private String unitServiceName2;
    @Value("${temperature-unit.wav.file.name2}")
    private String unitWavFileName2;
    @Value("${temperature-unit.critical-temperature1}")
    private float criticalTemperature1;
    @Value("${temperature-unit.critical-temperature2}")
    private float criticalTemperature2;

    @Bean(name = "unit1")
    public TemperatureUnit temperatureUnit1() {
        TemperatureUnit unit = new TemperatureUnit();
        unit.setServiceName(unitServiceName1);
        unit.setName(unitServiceName1);
        unit.setWavFileName(unitWavFileName1);
        unit.setCriticalTemperature(criticalTemperature1);
        unit.setActualTemperature(0);
        unit.setDateOfTemperatureMeasurement(LocalDateTime.now());
        unit.setStatus(true);
        unit.setNotification(false);
        return unit;
    }

    @Bean(name = "unit2")
    public TemperatureUnit temperatureUnit2() {
        TemperatureUnit unit = new TemperatureUnit();
        unit.setServiceName(unitServiceName2);
        unit.setName(unitServiceName2);
        unit.setWavFileName(unitWavFileName2);
        unit.setCriticalTemperature(criticalTemperature2);
        unit.setActualTemperature(0);
        unit.setDateOfTemperatureMeasurement(LocalDateTime.now());
        unit.setStatus(true);
        unit.setNotification(false);
        return unit;
    }
}
