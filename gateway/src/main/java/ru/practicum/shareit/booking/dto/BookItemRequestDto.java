package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent
    //@JsonFormat(pattern = DEFAULT_JSON_DATETIME_FORMAT, timezone = "UTC")
    private LocalDateTime start;
    @Future
    //@JsonFormat(pattern = DEFAULT_JSON_DATETIME_FORMAT, timezone = "UTC")
    private LocalDateTime end;
}