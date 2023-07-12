package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingDtoMapper {
    public static BookingDto toDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                itemDto,
                userDto);
    }

    public static Booking fromDto(BookingDto bookingDto) {
        var booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

}
