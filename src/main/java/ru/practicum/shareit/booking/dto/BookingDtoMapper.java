package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

public class BookingDtoMapper {
    public static BookingDto toDto(Booking booking) {
        return new BookingDto();
    }

    public static Booking fromDto(BookingDto bookingDto) {
        return new Booking();
    }


}
