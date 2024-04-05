package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Review {

    @NotNull(groups = Marker.OnUpdate.class, message = "Пустое поле id")
    private Integer reviewId;

    @NotBlank(message = "Пустое поле отзыв")
    @Size(min = 1, max = 4000, message = "Длина отзыва не может превышать 4000 символов.")
    private String content;

    private Boolean isPositive;

    @NotNull(message = "В отзыве отустствует id автора.")
    private Integer userId;

    @NotNull(message = "В отзыве отустствует id фильма.")
    private Integer filmId;

    private Integer useful;
}
