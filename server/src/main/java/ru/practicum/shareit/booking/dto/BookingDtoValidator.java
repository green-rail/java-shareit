package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingDtoValidator {
    public static Optional<String> validate(BookingDto bookingDto) {
        StringBuilder message = new StringBuilder();
        if (bookingDto.getItemId() == null || bookingDto.getItemId() < 1) {
            message.append("Неверный индекс предмета. ");
        }
        if (bookingDto.getStart() == null) {
            message.append("Отсутствует время начала. ");
        }
        if (bookingDto.getEnd() == null) {
            message.append("Отсутствует время окончания. ");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return Optional.ofNullable(message.length() == 0 ? null : message.toString());
        }

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            message.append("Время начала должно быть раньше времени окончания. ");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            message.append("Время начала не может быть раньше текущего. ");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            message.append("Время окончания не может быть раньше текущего. ");
        }
        return Optional.ofNullable(message.length() == 0 ? null : message.toString());
    }
}
