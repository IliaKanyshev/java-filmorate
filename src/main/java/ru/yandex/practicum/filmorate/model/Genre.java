package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;


@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Genre {
    @Max(6)
    private Integer id;
    private String name;
}
