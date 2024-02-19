package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@Builder(toBuilder = true)
public class Film {
    private int id;
    @NotBlank(message = "Пустое поле name")
    private String name;
    @Size(min = 1, max = 200, message = "Длина описания не может превышать 200 символов.")
    private String description;
    @NotNull(message = "Поле releaseDate == null")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0.")
    private int duration;

}
