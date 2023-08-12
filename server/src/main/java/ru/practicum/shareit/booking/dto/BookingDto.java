package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.time.LocalDateTime;

import static ru.practicum.shareit.common.Constants.defaultJsonDateTimePattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;
    private Long bookerId;
    //@JsonFormat(pattern = defaultJsonDateTimePattern, timezone = "UTC")
    //private Instant start;
    //@JsonFormat(pattern = defaultJsonDateTimePattern, timezone = "UTC")
    //private Instant end;
    @JsonFormat(pattern = defaultJsonDateTimePattern)
    private LocalDateTime start;
    @JsonFormat(pattern = defaultJsonDateTimePattern)
    private LocalDateTime end;
    private BookingStatus status;
    private ItemDto item;
    private UserDto booker;
}
