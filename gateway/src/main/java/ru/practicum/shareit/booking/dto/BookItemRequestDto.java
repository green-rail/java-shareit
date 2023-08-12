package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Optional;

import static ru.practicum.shareit.common.Defaults.DEFAULT_JSON_DATETIME_FORMAT;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @PositiveOrZero
    private long itemId;

    @FutureOrPresent
    @NotNull
    @JsonFormat(pattern = DEFAULT_JSON_DATETIME_FORMAT)
    private LocalDateTime start;

    @Future
    @NotNull
    @JsonFormat(pattern = DEFAULT_JSON_DATETIME_FORMAT)
    private LocalDateTime end;

    public Optional<String> invalidityErrorMessage() {
        return Optional.ofNullable(start.isBefore(end) ? null : "Начало не может быть раньше окончания");
    }
}