package ru.guard.temp_control_web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.guard.temp_control_web.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Override
    Page<Event> findAll(Pageable pageable);
}
