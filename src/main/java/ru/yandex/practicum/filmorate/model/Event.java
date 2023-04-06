package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Event {

    @NotNull(message = "Feed date cannot be null")
    @PastOrPresent(message = "Feed cannot be in the future")
    private Long timestamp;

    @NotNull(message = "Feed userId cannot be null")
    private Long userId;

    @NotNull(message = "Feed eventType cannot be null")
    private EventType eventType;

    @NotNull(message = "Feed operation cannot be null")
    private Operation operation;

    private Long eventId;

    @NotNull(message = "entityId cannot be null")
    private Long entityId;
}