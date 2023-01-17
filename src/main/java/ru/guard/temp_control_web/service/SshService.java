package ru.guard.temp_control_web.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.guard.temp_control_web.util.EventStatus;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SshService {
    private final Logger logger = Logger.getLogger(SshService.class.getName());
    private final EventService eventService;
    @Value("${asterisk.host}")
    private String host;
    @Value("${asterisk.port}")
    private int port;
    @Value("${asterisk.user-name}")
    private String username;
    @Value("${asterisk.password}")
    private String password;
    @Value("${asterisk.number-of-phone}")
    private int numberOfPhone;

    public void sendCommandToAsteriskServer(String unitWavFileName) {
        String command = "asterisk -rx \"channel originate local/" + numberOfPhone + "@nonfixed-number-notify application playback custom/" + unitWavFileName + "\"";
        try {
            sendCommand(username, password, host, port, command);

            eventService.create("Команда аварийного вызова успешно выполнена сервером Asterisk", EventStatus.INFO);
            logger.fine("Command (" + command + ") successfully send");
        } catch (Exception e) {
            eventService.create("Команда аварийного вызова не выполнена сервером Asterisk по причине: " + e.getMessage(), EventStatus.ERROR);
            logger.severe("Send command was failed with cause: " + e.getMessage() + ". Check status Asterisk server or LAN");

            try {
                TimeUnit.SECONDS.sleep(60);
                sendCommandToAsteriskServer(unitWavFileName);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void sendCommand(
            String username, String password,
            String host, int port, String command) throws Exception {

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }

            String responseString = responseStream.toString();
            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public int getNumberOfPhone() {
        return numberOfPhone;
    }

    public void setNumberOfPhone(int numberOfPhone) {
        this.numberOfPhone = numberOfPhone;
    }
}
