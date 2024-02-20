package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    UserController userController = new UserController();
    User user;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void init() {
        user = User.builder().id(0).login("log in").email("mail.ru").birthday(LocalDate.of(2256, 12, 12)).build();
    }

    @Test
    public void createUserNoName() {
        userController.create(user);
        assertEquals(user.getName(), user.getLogin());
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
