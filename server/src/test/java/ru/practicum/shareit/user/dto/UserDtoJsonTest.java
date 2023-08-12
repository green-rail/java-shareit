package ru.practicum.shareit.user.dto;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoJsonTest() throws IOException {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var invalidDto = new UserDto(1L, "invalidemail.com", "   ");
            Set<ConstraintViolation<UserDto>> violations = validator.validate(invalidDto);
            assertThat(violations, hasSize(2));
        }

        var dto = new UserDto(1L, "email@email.com", "name");
        JsonContent<UserDto> result = json.write(dto);
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(dto.getEmail());
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(dto.getName());
    }

}