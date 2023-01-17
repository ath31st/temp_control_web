package ru.guard.temp_control_web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.guard.temp_control_web.entity.TemperatureUnit;
import ru.guard.temp_control_web.util.EventStatus;
import ru.guard.temp_control_web.util.wrapper.SettingsWrapper;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private static final Logger logger = Logger.getLogger(SettingsService.class.getName());
    private final EventService eventService;
    private final SshService sshService;
    private final TemperatureUnit unit1;
    private final TemperatureUnit unit2;

    public void applySettings(SettingsWrapper wrapper, String remoteAddress) {
        StringBuilder sb = new StringBuilder();

        if (wrapper.getCriticalTemperatureUnit1() != 0 & wrapper.getCriticalTemperatureUnit1() != unit1.getCriticalTemperature()) {
            unit1.setCriticalTemperature(wrapper.getCriticalTemperatureUnit1());
            sb.append("|u1t=").append(wrapper.getCriticalTemperatureUnit1());
        }
        if (wrapper.getCriticalTemperatureUnit2() != 0 & wrapper.getCriticalTemperatureUnit2() != unit2.getCriticalTemperature()) {
            unit2.setCriticalTemperature(wrapper.getCriticalTemperatureUnit2());
            sb.append("|u2t=").append(wrapper.getCriticalTemperatureUnit2());
        }
        if (wrapper.isStatusUnit1()) {
            unit1.setStatus(!unit1.isStatus());
            sb.append("|u1s=").append(unit1.isStatus());
        }
        if (wrapper.isStatusUnit2()) {
            unit2.setStatus(!unit2.isStatus());
            sb.append("|u2s=").append(unit2.isStatus());
        }

        if (!wrapper.getNumberOfPhone().isBlank()) {
            int numberOfPhone = Integer.parseInt(wrapper.getNumberOfPhone());
            if (numberOfPhone != sshService.getNumberOfPhone()) {
                sshService.setNumberOfPhone(numberOfPhone);
                sb.append("|nop=").append(numberOfPhone);
            }
        }

        if (!sb.isEmpty()) {
            eventService.create("Настройки были изменены с IP адреса: " + remoteAddress +
                    ". Внесенные изменения:" + sb.append("|"), EventStatus.INFO);
        }
    }
}
