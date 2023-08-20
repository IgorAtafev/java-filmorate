package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Event {

    private Long eventId;

    private Long timestamp;

    private Long userId;

    private EventType eventType;

    private Operation operation;

    private Long entityId;
}
