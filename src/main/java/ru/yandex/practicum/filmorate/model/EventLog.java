package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class EventLog {
    private int eventId;
    @NotNull
    private int userId;
    @NotNull
    private int entityId;
    private String eventType;
    private String operation;
    private Long timestamp;
}
