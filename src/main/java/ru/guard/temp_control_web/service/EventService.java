package ru.guard.temp_control_web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.guard.temp_control_web.entity.Event;
import ru.guard.temp_control_web.repository.EventRepository;
import ru.guard.temp_control_web.util.EventStatus;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EventService {
    private final Logger logger = Logger.getLogger(EventService.class.getName());
    private final EventRepository eventRepository;

    public Event create(String message, EventStatus status) {
        Event event = new Event();
        event.setDate(LocalDateTime.now());
        event.setMessage(message);
        event.setStatus(status);

        return eventRepository.save(event);
    }

    public Page<Event> getPages(int pageNo, int pageSize) {
        Sort sort = Sort.by("date").descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return eventRepository.findAll(pageable);
    }
}
