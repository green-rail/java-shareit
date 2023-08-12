package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;

public class BookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        return BookingState.valueOf(source.toUpperCase());
    }
}
