package kz.iitu.registration.service;

import kz.iitu.registration.model.Event;
import kz.iitu.registration.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents(String title) {
        if (StringUtils.hasText(title)) {
            return eventRepository.findByTitleContainingIgnoreCase(title);
        }
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event with id " + id + " was not found"));
    }

    public Event createEvent(String title, String location) {
        validate(title, location);
        return eventRepository.save(new Event(title, location));
    }

    public Event updateEvent(Long id, String title, String location) {
        validate(title, location);
        Event existingEvent = getEventById(id);
        existingEvent.setTitle(title);
        existingEvent.setLocation(location);
        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        eventRepository.delete(getEventById(id));
    }

    private void validate(String title, String location) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(location)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Title and location must not be blank");
        }
    }
}
