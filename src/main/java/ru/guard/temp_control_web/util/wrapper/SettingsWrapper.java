package ru.guard.temp_control_web.util.wrapper;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
public class SettingsWrapper {
    private boolean statusUnit1;
    private boolean statusUnit2;
    @Min(value = 0, message = "Критическая температура не может быть установлена ниже 0 градусов.")
    @Max(value = 100, message = "Критическая температура не может быть установлена выше 100 градусов.")
    private float criticalTemperatureUnit1;
    @Min(value = 0, message = "Критическая температура не может быть установлена ниже 0 градусов.")
    @Max(value = 100, message = "Критическая температура не может быть установлена выше 100 градусов.")
    private float criticalTemperatureUnit2;
    @Pattern(regexp = "([1-9][0-9]{3}|(^\\s*$))", message = "Введите четырехзначный номер")
    private String numberOfPhone;
}
