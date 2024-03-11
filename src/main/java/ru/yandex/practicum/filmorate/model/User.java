package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@Builder(toBuilder = true)
public class User {
    @NotNull(groups = Marker.OnUpdate.class, message = "Пустое поле id")
    private int id;
    @NotBlank
    @Email(message = "Неверный формат email.")
    private String email;
    @NotBlank(message = "Пустое поле login")
    @Pattern(regexp = "\\S*", message = "Пробел в логине.")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Set<Integer> friends;
}
