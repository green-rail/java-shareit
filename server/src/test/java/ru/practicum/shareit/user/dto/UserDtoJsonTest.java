package ru.practicum.shareit.user.dto;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoJsonTest() throws IOException {

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