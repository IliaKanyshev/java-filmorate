package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventLog {
    @NotNull
    private int eventId;
    @NotNull
    private int userId;
    @NotNull
    private int entityId;
    private String eventType;
    private String operation;
    private Long timestamp;
}
