package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class BookingDtoValidatorTest {

    @Test
    void validate() {
        BookingDto dto = new BookingDto();
        long count = Arrays.stream(BookingDtoValidator.validate(dto).get().trim().split("\\.")).count();
        assertThat(count, equalTo(3L));

        dto.setItemId(1L);
        count = Arrays.stream(BookingDtoValidator.validate(dto).get().trim().split("\\.")).count();
        assertThat(count, equalTo(2L));

        //var now = Instant.now().plusSeconds(5);
        //var after = Instant.now().plusSeconds(100);
        var now = LocalDateTime.now().plusSeconds(5);
        var after = LocalDateTime.now().plusSeconds(100);
        dto.setStart(now);
        count = Arrays.stream(BookingDtoValidator.validate(dto).get().trim().split("\\.")).count();
        assertThat(count, equalTo(1L));

        //Instant before = Instant.now().minusSeconds(10);
        LocalDateTime before = LocalDateTime.now().minusSeconds(10);
        dto.setStart(before);
        dto.setEnd(after);
        count = Arrays.stream(BookingDtoValidator.validate(dto).get().trim().split("\\.")).count();
        assertThat(count, equalTo(1L));

        dto.setEnd(before.plusSeconds(1));
        count = Arrays.stream(BookingDtoValidator.validate(dto).get().trim().split("\\.")).count();
        assertThat(count, equalTo(2L));


        dto.setStart(now);
        dto.setEnd(after);
        assertThat(BookingDtoValidator.validate(dto).isEmpty(), equalTo(true));

        dto.setStart(after);
        dto.setEnd(now);
        count = Arrays.stream(BookingDtoValidator.validate(dto).get().trim().split("\\.")).count();
        assertThat(count, equalTo(1L));

    }
}