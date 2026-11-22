package kz.iitu.registration.controller;

import kz.iitu.registration.model.Event;
import kz.iitu.registration.service.EventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final EventService eventService;
    private final String applicationName;
    private final String course;
    private final int semester;

    public RegistrationController(
            EventService eventService,
            @Value("${spring.application.name}") String applicationName,
            @Value("${app.course}") String course,
            @Value("${app.semester}") int semester
    ) {
        this.eventService = eventService;
        this.applicationName = applicationName;
        this.course = course;
        this.semester = semester;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Welcome to the Event Registration API");
    }

    @GetMapping("/events")
    public List<Event> getEvents(@RequestParam(required = false) String title) {
        return eventService.getEvents(title);
    }

    @GetMapping("/events/{id}")
    public Event getEventById(@PathVariable("id") Long id) {
        return eventService.getEventById(id);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event createdEvent = eventService.createEvent(event.getTitle(), event.getLocation());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/events/{id}")
    public Event updateEvent(@PathVariable("id") Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event.getTitle(), event.getLocation());
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return Map.of(
                "applicationName", applicationName,
                "course", course,
                "semester", semester
        );
    }
}
