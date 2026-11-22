package kz.iitu.registration.service;

import kz.iitu.registration.model.Event;
import kz.iitu.registration.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventService = new EventService(eventRepository);
    }

    @Test
    void createEventReturnsDatabaseGeneratedId() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(42L);
            return event;
        });

        Event created = eventService.createEvent("Quiz", "Room 1");

        assertEquals(42L, created.getId());
        assertEquals("Quiz", created.getTitle());
        assertEquals("Room 1", created.getLocation());
    }

    @Test
    void updateEventPreservesStoredIdAndChangesOnlyEventDetails() {
        Event existing = new Event("Old title", "Old location");
        existing.setId(8L);
        when(eventRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(existing)).thenReturn(existing);

        Event updated = eventService.updateEvent(8L, "New title", "New location");

        assertEquals(8L, updated.getId());
        assertEquals("New title", updated.getTitle());
        assertEquals("New location", updated.getLocation());
        verify(eventRepository).save(existing);
    }

    @Test
    void blankFieldsAreRejectedWithoutWritingToRepository() {
        assertThrows(ResponseStatusException.class,
                () -> eventService.createEvent(" ", "Room 1"));

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void missingEventReturnsNotFound() {
        when(eventRepository.findById(404L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> eventService.getEventById(404L));

        assertEquals(404, exception.getStatusCode().value());
    }
}
