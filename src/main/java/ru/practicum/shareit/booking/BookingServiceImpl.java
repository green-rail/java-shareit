package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoValidator;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.common.Util;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(Long bookerId, BookingDto bookingDto) {
        var booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(bookerId));

        var item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("предмет с id [%d] не найден", bookingDto.getItemId())));

        if (!item.isAvailable()) {
            throw new ItemUnavailableException(item.getId());
        }

        if (booker.getId().equals(item.getSharerId())) {
            throw new EntityNotFoundException("владелец не может бронировать предмет");
        }

        BookingDtoValidator.validate(bookingDto).ifPresent(s -> {
            throw new InvalidEntityException(s); });

        var booking = BookingDtoMapper.fromDto(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        return BookingDtoMapper.toDto(bookingRepository.save(booking),
                ItemDtoMapper.toDto(item, null, null, null),
                UserDtoMapper.toDto(booker));
    }

    @Override
    public BookingDto approveBooking(Long sharerId, Long bookingId, boolean approved) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("бронирование с id [%d] не найдено", bookingId))
                );
        if (!booking.getItem().getSharerId().equals(sharerId)) {
            throw new EntityNotFoundException(
                    String.format("пользователь [%d] не является владельцем предмета [%d]",
                            sharerId, booking.getItem().getId()));
        }
        if (booking.getStatus().equals(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED)) {
            throw new InvalidEntityException("статус бронирования уже выставлен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingDtoMapper.toDto(booking,
                ItemDtoMapper.toDto(booking.getItem(), null, null, null),
                UserDtoMapper.toDto(booking.getBooker()));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("бронирование с id [%d] не найдено", bookingId))
                );

        if (!(Objects.equals(booking.getItem().getSharerId(), userId)
                || Objects.equals(booking.getBooker().getId(), userId))) {
            throw new EntityNotFoundException(
                    String.format("пользователь [%d] не является владельцем предмета [%d] или создателем бронирования",
                            userId, booking.getItem().getId()));
        }
        var user = UserDtoMapper.toDto(booking.getBooker());
        var item = ItemDtoMapper.toDto(booking.getItem(), null, null, null);

        return BookingDtoMapper.toDto(booking, item, user);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state, int from, int size) {
        Util.checkPageRequestBoundaries(from, size);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Page<Booking> bookings = null;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        var now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByIdDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(
                        userId, now, now, page);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByIdDesc(userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByIdDesc(userId, now, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByIdDesc(
                        userId,
                        BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByIdDesc(
                        userId,
                        BookingStatus.REJECTED, page);
                break;
        }

        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking: bookings) {
            var user = UserDtoMapper.toDto(booking.getBooker());
            var item = ItemDtoMapper.toDto(booking.getItem(), null, null, null);
            dtos.add(BookingDtoMapper.toDto(booking, item, user));
        }
        return dtos;
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long sharerId, BookingState state, int from, int size) {

        Util.checkPageRequestBoundaries(from, size);
        if (!userRepository.existsById(sharerId)) {
            throw new UserNotFoundException(sharerId);
        }

        Page<Booking> bookings = null;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        var now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwner(sharerId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwner(sharerId, now, now, page);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwner(sharerId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwner(sharerId, now, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerAndStatus(sharerId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerAndStatus(sharerId, BookingStatus.REJECTED, page);
                break;
        }


        return  bookings.stream()
                .map(booking -> BookingDtoMapper.toDto(
                        booking,
                        ItemDtoMapper.toDto(booking.getItem(), null, null, null),
                        UserDtoMapper.toDto(booking.getBooker())))
                .collect(Collectors.toUnmodifiableList());
    }


}
