package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingDtoValidator {
    public static Optional<String> validate(BookingDto bookingDto) {
        String message = "";
        if (bookingDto.getItemId() == null || bookingDto.getItemId() < 1) {
            message += "Неверный индекс предмета. ";
        }
        if (bookingDto.getStart() == null) {
            message += "Отсутствует время начала. ";
        }
        if (bookingDto.getEnd() == null) {
            message += "Отсутствует время окончания. ";
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return Optional.ofNullable(message.isBlank() ? null : message);
        }

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            message += "Время начала должно быть раньше времени окончания. ";
        }
        if (bookingDto.getStart().isBefore(Instant.now())) {
            message += "Время начала не может быть раньше текущего. ";
        }
        if (bookingDto.getEnd().isBefore(Instant.now())) {
            message += "Время окончания не может быть раньше текущего. ";
        }
        return Optional.ofNullable(message.isBlank() ? null : message);
    }
}
