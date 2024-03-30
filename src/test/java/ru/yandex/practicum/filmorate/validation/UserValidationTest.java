package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserValidationTest {
    private static final Validator validator;
    private User user;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void init() {
        user = User.builder().id(0).login("log in").email("mail.ru").birthday(LocalDate.of(2256, 12, 12)).build();
    }

    @Test
    void validateUserOk() {
        user = User.builder().id(3)
                .email("vasya@mail.ru")
                .login("Vasya")
                .name("Vasiliy")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
        validator.validate(user);
    }

    @Test
    public void validatorsTest() {
        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(3, validates.size());
        validates.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        user.setLogin(" ");
        Set<ConstraintViolation<User>> validates1 = validator.validate(user);
        assertEquals(4, validates1.size());
        validates1.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
    }
}
