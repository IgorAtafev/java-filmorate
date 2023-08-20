package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    /**
     * Returns a list of user events
     *
     * @param id
     * @return list of user events
     */
    List<Event> getUserEvents(Long id);

    /**
     * Adds an event to the database
     *
     * @param event
     * @return Event
     */
    Event addEvent(Event event);
}
