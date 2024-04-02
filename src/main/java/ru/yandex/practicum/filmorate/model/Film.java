package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.Marker;
import ru.yandex.practicum.filmorate.validators.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder(toBuilder = true)
public class Film {
    @NotNull(groups = Marker.OnUpdate.class, message = "Пустое поле id")
    private int id;
    @NotBlank(message = "Пустое поле name")
    private String name;
    @Size(min = 1, max = 200, message = "Длина описания не может превышать 200 символов.")
    private String description;
    @NotNull(message = "Поле releaseDate == null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0.")
    private int duration;
    private final Set<Integer> likes = new HashSet<>();
    @NotNull
    private Mpa mpa;
    private final List<Genre> genres = new ArrayList<>();
}