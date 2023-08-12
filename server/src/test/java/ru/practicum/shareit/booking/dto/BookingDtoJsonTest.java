package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.common.Constants.defaultJsonDateTimePattern;


@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;


    @Test
    void bookingDtoJsonTest() throws IOException {

        var formatter = DateTimeFormatter
                .ofPattern(defaultJsonDateTimePattern)
                .withZone(ZoneOffset.UTC);
        //var now = Instant.now();
        var now = LocalDateTime.now();

        BookingDto dto = new BookingDto(
                1L,
                1L,
                1L,
                now,
                now,
                BookingStatus.WAITING,
                new ItemDto(1L, "name", "description", true,
                        null, null, null, null),
                new UserDto(1L, "email@email.com", "name")
        );

        JsonContent<BookingDto> result = json.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        //assertThat(result).extractingJsonPathStringValue("$.start")
        //        .isEqualTo(formatter.format(now));
        //assertThat(result).extractingJsonPathStringValue("$.end")
        //        .isEqualTo(formatter.format(now));
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(dto.getStatus().toString());
        assertThat(result).extractingJsonPathValue("$.item").isNotNull();
        assertThat(result).extractingJsonPathValue("$.booker").isNotNull();
    }

}