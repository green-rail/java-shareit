package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class BookingServiceImplUnitTest {


    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingService bookingService;


    private BookingDto bookingDto;
    private Item item;
    private User owner;
    private User booker;
    private Booking booking;


    @BeforeEach
    void setup() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);

        bookingDto = new BookingDto(
                1L,
                1L,
                1L,
                //Instant.now(),
                //Instant.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                BookingStatus.WAITING,
                null,
                null
        );

        item = new Item(
                1L,
                1L,
                "Item name",
                "Item description",
                false,
                null
        );

        owner = new User(
                1L,
                "owner",
                "user@email.com"
        );

        booker = new User(
                2L,
                "Booker",
                "user1@email.com"
        );

        booking = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(100),
                LocalDateTime.now().plusSeconds(1000),
                item,
                booker,
                BookingStatus.WAITING
        );
    }


    @Test
    void addBooking() {

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(booker.getId(), bookingDto));

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(booker.getId(), bookingDto));

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(ItemUnavailableException.class, () -> bookingService.addBooking(booker.getId(), bookingDto));


        item.setAvailable(true);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(owner.getId(), bookingDto));

        bookingDto.setStart(null);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        assertThrows(InvalidEntityException.class, () -> bookingService.addBooking(booker.getId(), bookingDto));

        bookingDto.setStart(LocalDateTime.now().plusSeconds(100));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(1000));


        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto response = bookingService.addBooking(booker.getId(), bookingDto);
        assertThat(response.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(response.getBookerId(), equalTo(booker.getId()));
        assertThat(response.getBooker().getName(), equalTo(booker.getName()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
        assertThat(response.getStart(), equalTo(booking.getStart()));
        assertThat(response.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void approveBooking() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(InvalidEntityException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));

        booking.setStatus(BookingStatus.REJECTED);
        assertThrows(InvalidEntityException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), false));

        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(100),
                LocalDateTime.now().plusSeconds(1000),
                item,
                booker,
                BookingStatus.APPROVED
        );

        Mockito.when(bookingRepository.save(any())).thenReturn(savedBooking);
        BookingDto response = bookingService.approveBooking(owner.getId(), booking.getId(), true);
        assertThat(response.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(response.getBookerId(), equalTo(booker.getId()));
        assertThat(response.getBooker().getName(), equalTo(booker.getName()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
        assertThat(response.getStart(), equalTo(booking.getStart()));
        assertThat(response.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getBooking() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(owner.getId(), booking.getId()));

        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(3L, booking.getId()));

        BookingDto response = bookingService.getBooking(owner.getId(), booking.getId());
        assertThat(response.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(response.getBookerId(), equalTo(booker.getId()));
        assertThat(response.getBooker().getName(), equalTo(booker.getName()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
        assertThat(response.getStart(), equalTo(booking.getStart()));
        assertThat(response.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getUserBookings() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.getUserBookings(20L, BookingState.CURRENT, 0, 10));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        Mockito.when(bookingRepository.findByBookerId(anyLong(), any())).thenReturn(makePage());
        List<BookingDto> response = bookingService.getUserBookings(1L, BookingState.ALL, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                .findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getUserBookings(1L, BookingState.CURRENT, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                .findByBookerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getUserBookings(1L, BookingState.PAST, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findByBookerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getUserBookings(1L, BookingState.FUTURE, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getUserBookings(1L, BookingState.WAITING, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getUserBookings(1L, BookingState.REJECTED, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

    }

    @Test
    void getOwnerBookings() {

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.getOwnerBookings(20L, BookingState.CURRENT, 0, 10));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        Mockito.when(bookingRepository.findAllByOwner(anyLong(), any())).thenReturn(makePage());
        List<BookingDto> response = bookingService.getOwnerBookings(1L, BookingState.ALL, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findCurrentByOwner(anyLong(), any(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getOwnerBookings(1L, BookingState.CURRENT, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findPastByOwner(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getOwnerBookings(1L, BookingState.PAST, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findFutureByOwner(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getOwnerBookings(1L, BookingState.FUTURE, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findByOwnerAndStatus(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getOwnerBookings(1L, BookingState.WAITING, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));

        Mockito.when(bookingRepository
                        .findByOwnerAndStatus(anyLong(), any(), any()))
                .thenReturn(makePage());
        response = bookingService.getOwnerBookings(1L, BookingState.REJECTED, 0, 10);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getBooker().getName(), equalTo(booker.getName()));
    }

    private Page<Booking> makePage() {
        int totalElements = 10;
        int pageSize = 5;
        PageRequest pageable = PageRequest.of(0, pageSize);
        return new PageImpl<>(List.of(booking), pageable, totalElements);
    }
}