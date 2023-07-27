package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws IOException {

        var formatter = DateTimeFormatter
                .ofPattern(ItemRequestDto.jsonDateTimePattern)
                .withZone(ZoneOffset.UTC);
        var now = Instant.now();

        ItemRequestDto dto = new ItemRequestDto(
                1L,
                "Description",
                now,
                List.of(new ItemRequestReplyDto(
                        1L, "Item name",
                        "Item descr", 1L, true))
        );

        JsonContent<ItemRequestDto> result = json.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(formatter.format(now));
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);

    }


}