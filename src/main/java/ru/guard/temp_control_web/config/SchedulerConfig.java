package ru.guard.temp_control_web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.guard.temp_control_web.entity.TemperatureUnit;
import ru.guard.temp_control_web.service.EventService;
import ru.guard.temp_control_web.service.SshService;
import ru.guard.temp_control_web.service.TemperatureService;
import ru.guard.temp_control_web.util.EventStatus;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulerConfig {
    private final Logger logger = Logger.getLogger(SchedulerConfig.class.getName());
    private final TemperatureUnit unit1;
    private final TemperatureUnit unit2;
    private final TemperatureService temperatureService;
    private final SshService sshService;
    private final EventService eventService;

    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
    private void getAndCheckTemperature1() {
        getTemperature(unit1);
        checkTemperature(unit1);
    }

    @Scheduled(initialDelay = 12000, fixedDelay = 60000)
    private void getAndCheckTemperature2() {
        getTemperature(unit2);
        checkTemperature(unit2);
    }

    @Scheduled(initialDelay = 20000, fixedDelay = 3600000)
    private void saveTemperatureInDb() {
        temperatureService.saveTemperatureInDb(unit1);
        temperatureService.saveTemperatureInDb(unit2);
    }

    @Scheduled(initialDelay = 5000, fixedDelayString = "${fixed-delay.in.milliseconds}")
    private void checkStatus() {
        temperatureService.checkStatusForNotification(unit1);
        temperatureService.checkStatusForNotification(unit2);
    }

    private void getTemperature(TemperatureUnit unit) {
        if (!unit.isStatus()) return;
        temperatureService.checkDropInTemperatureAndEarlyActivationNotifications(unit);

        unit.setActualTemperature(temperatureService.getTemperatureByUnit(unit));
        unit.setDateOfTemperatureMeasurement(LocalDateTime.now());

        logger.info("Temperature in server room: " + unit.getServiceName() + " " + unit.getActualTemperature());
    }

    private void checkTemperature(TemperatureUnit unit) {
        if (unit.isStatus() && unit.isNotification() && temperatureService.compareTemperature(unit)) {

            eventService.create("Температура в помещении: " + unit.getName()
                    + " = " + unit.getActualTemperature() + " превысила критическую!", EventStatus.WARNING);
            logger.warning("TEMPERATURE IN SERVER ROOM: " + unit.getServiceName() + " OVER CRITICAL: " + unit.getActualTemperature());

            sshService.sendCommandToAsteriskServer(unit.getWavFileName());
            unit.setNotification(false);
        }
    }
}
