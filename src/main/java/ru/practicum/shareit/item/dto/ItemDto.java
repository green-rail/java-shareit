package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
