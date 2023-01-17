package ru.guard.temp_control_web.service;

import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.guard.temp_control_web.entity.TemperatureUnit;
import ru.guard.temp_control_web.util.EventStatus;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class TemperatureService {
    private final Logger logger = Logger.getLogger(TemperatureService.class.getName());
    private final SerialPortService serialPortService;
    private final SnmpService snmpService;
    private final EventService eventService;
    @Value("${temperature-unit.name1}")
    private String unitServiceName1;
    @Value("${temperature-unit.name2}")
    private String unitServiceName2;

    public float getTemperatureByUnit(TemperatureUnit unit) {
        if (unit.getServiceName().equals(unitServiceName1)) {
            return getTempFromServerRoom1();
        } else {
            return getTempFromServerRoom2();
        }
    }

    public float getTempFromServerRoom2() {
        String oidValue = snmpService.getOidTemperature();
        return retrieveTemperature(oidValue);
    }

    public float getTempFromServerRoom1() {
        SerialPort serialPort = serialPortService.getConnection();
        float temperature = 0;
        try {
            temperature = Float.parseFloat(serialPort.readString(6));

            logger.fine("Temperature was successfully read from port.");
        } catch (SerialPortException e) {
            logger.severe("Port cannot be read, check status microcontroller and system.");
        } catch (NumberFormatException e) {
            logger.severe("Port was send not complete data.");
        }

        try {
            serialPort.closePort();

            logger.fine("Port closed");
        } catch (SerialPortException e) {
            logger.severe("Port cannot be close!");
            try {
                TimeUnit.SECONDS.sleep(10);
                serialPort.closePort();
            } catch (InterruptedException | SerialPortException ex) {
                throw new RuntimeException(ex);
            }
        }
        return temperature;
    }

    public boolean compareTemperature(TemperatureUnit unit) {
        return unit.getActualTemperature() > unit.getCriticalTemperature();
    }

    public void saveTemperatureInDb(TemperatureUnit unit) {
        if (unit.getActualTemperature() == 0 | !unit.isStatus()) return;

        eventService.create("Температура в " + unit.getServiceName() + ": "
                + unit.getActualTemperature(), EventStatus.TEMPERATURE);
    }

    public void checkStatusForNotification(TemperatureUnit unit) {
        if (unit.isStatus() & !unit.isNotification()) {
            unit.setNotification(true);

            eventService.create("Сброс статуса оповещения " + unit.getName(), EventStatus.INFO);
            logger.info("Status server room: " + unit.getServiceName() + " was reset!");
        }
    }

    public void checkDropInTemperatureAndEarlyActivationNotifications(TemperatureUnit unit) {
        if (!unit.isNotification() & unit.getActualTemperature() + 5.0 < unit.getCriticalTemperature()) {
            unit.setNotification(true);

            eventService.create("Досрочный сброс статуса оповещения в связи с изменением температуры в " + unit.getName()
                    + " отностительно критической отметки.", EventStatus.INFO);
            logger.info("Status server room: " + unit.getServiceName() + " was early reset!");
        }
    }

    private Float retrieveTemperature(String oidValue) {
        String temperature = oidValue.substring(oidValue.indexOf("=") + 1, oidValue.indexOf("]"));
        return Float.parseFloat(temperature);
    }

}
