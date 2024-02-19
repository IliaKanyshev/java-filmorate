package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@Builder(toBuilder = true)
public class User {
    private int id;
    @NotBlank
    @Email(message = "Неверный формат email.")
    private String email;
    @NotBlank(message = "Пустое поле login")
    @Pattern(regexp = "\\S*", message = "Пробел в логине.")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем.")
    private LocalDate birthday;
}
