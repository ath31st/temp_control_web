package ru.guard.temp_control_web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.guard.temp_control_web.entity.Event;
import ru.guard.temp_control_web.entity.TemperatureUnit;
import ru.guard.temp_control_web.service.EventService;
import ru.guard.temp_control_web.service.SettingsService;
import ru.guard.temp_control_web.service.SshService;
import ru.guard.temp_control_web.util.wrapper.PageWrapper;
import ru.guard.temp_control_web.util.wrapper.SettingsWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TemperatureController {
    private final TemperatureUnit unit1;
    private final TemperatureUnit unit2;
    private final EventService eventService;
    private final SettingsService settingsService;
    private final SshService sshService;

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("units", List.of(unit1, unit2));
        return "index";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("unit1", unit1);
        model.addAttribute("unit2", unit2);
        model.addAttribute("settingsWrapper", new SettingsWrapper());
        model.addAttribute("num", sshService.getNumberOfPhone());
        return "settings";
    }

    @PostMapping("/settings")
    public String settings(@Valid @ModelAttribute("settingsWrapper") SettingsWrapper settingsWrapper,
                           BindingResult bindingResult, HttpServletRequest request, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("settingsWrapper", settingsWrapper);
            model.addAttribute("unit1", unit1);
            model.addAttribute("unit2", unit2);
            model.addAttribute("num", sshService.getNumberOfPhone());
            return "settings";
        }

        settingsService.applySettings(settingsWrapper, request.getRemoteAddr());
        return "redirect:/settings";
    }

    @GetMapping("/events")
    public String events(Model model, @RequestParam(value = "page.page") int pageNo) {
        PageWrapper<Event> page = new PageWrapper<>(eventService.getPages(pageNo, PageWrapper.MAX_PAGE_ITEM_DISPLAY), "/events");
        model.addAttribute("page", page);
        return "events";
    }
}
