package ru.guard.temp_control_web.service;

import jssc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.guard.temp_control_web.util.EventStatus;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SerialPortService {
    private static final Logger logger = Logger.getLogger(SerialPort.class.getName());
    private final EventService eventService;
    private static SerialPort serialPort;

    @Value("${serial-port.name}")
    private String portName;

    public SerialPort getConnection() {
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            logger.fine("Port " + portName + " was opened.");
        } catch (SerialPortException e) {
            eventService.create("Не удалось соединиться с портом " + portName + ". Повторное соединение через 60 секунд", EventStatus.ERROR);
            logger.severe("Port " + portName + " not found or its busy! Connection was failed, through 60 sec will be retry.");
            logger.info("Available ports: " + Arrays.toString(SerialPortList.getPortNames()));
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            getConnection();
        }
        return serialPort;
    }

    public static class PortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String data = serialPort.readString(event.getEventValue());
                    System.out.println(data);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
