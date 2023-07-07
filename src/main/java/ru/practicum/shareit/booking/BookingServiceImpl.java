package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.item.storage.ItemRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        var booking = BookingDtoMapper.fromDto(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        return BookingDtoMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long sharerId, Long bookingId, boolean approved) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow( () -> new EntityNotFoundException(
                        String.format("бронирование с id [%d] не найдено", bookingId))
                );
        if (booking.getItem().getSharerId() != sharerId) {
            throw new OwnerMismatchException(
                    String.format("пользователь [%d] не является владельцем предмета [%d]",
                            sharerId, booking.getItem().getId()));
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingDtoMapper.toDto(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow( () -> new EntityNotFoundException(
                        String.format("бронирование с id [%d] не найдено", bookingId))
                );
        if (booking.getItem().getSharerId() != userId && booking.getBookerId() != userId) {
            throw new OwnerMismatchException(
                    String.format("пользователь [%d] не является владельцем предмета [%d] или создателем бронирования",
                            userId, booking.getItem().getId()));
        }

        return BookingDtoMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        List<Booking> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, Instant.now(), Instant.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, Instant.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, Instant.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.REJECTED);
                break;
        }

        return bookings.stream()
                .map(BookingDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {

        if (!itemRepository.existsBySharerId(userId)) {
            return Collections.emptyList();
        }

        List<Booking> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, Instant.now(), Instant.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, Instant.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, Instant.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.REJECTED);
                break;
        }

        return bookings.stream()
                .map(BookingDtoMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }


}
