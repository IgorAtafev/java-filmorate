package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Value
@Builder
public class Event {
    @NotNull(message = "Feed date cannot be null")
    @PastOrPresent(message = "Feed cannot be in the future")
    Long timestamp;
    @NotNull(message = "Feed userId cannot be null")
    Long userId;
    @NotNull(message = "Feed eventType cannot be null")
    String eventType;
    @NotNull(message = "Feed operation cannot be null")
    String operation;
    @With
    Long eventId;
    @NotNull(message = "entityId cannot be null")
    Long entityId;
}
