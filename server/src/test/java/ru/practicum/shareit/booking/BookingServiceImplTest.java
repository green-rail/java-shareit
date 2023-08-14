package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingService bookingService;

    private User user1;
    private User user2;
    private User user3;
    private Item itemMadeByUser2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setName("Ivan");
        user1.setEmail("invan@email.com");
        em.persist(user1);

        user2 = new User();
        user2.setName("Ivan2");
        user2.setEmail("invan2@email.com");
        em.persist(user2);

        user3 = new User();
        user3.setName("Ivan3");
        user3.setEmail("invan3@email.com");
        em.persist(user3);

        itemMadeByUser2 = new Item();
        itemMadeByUser2.setName("Item 1");
        itemMadeByUser2.setDescription("Item 1 description");
        itemMadeByUser2.setAvailable(false);
        itemMadeByUser2.setSharerId(user2.getId());
        em.persist(itemMadeByUser2);
    }

    @Test
    void addBookingFail() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(100L);
        assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(100L, bookingDto));
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(user1.getId(), bookingDto));

        bookingDto.setItemId(itemMadeByUser2.getId());
        assertThrows(ItemUnavailableException.class, () -> bookingService.addBooking(user1.getId(), bookingDto));

        itemMadeByUser2.setAvailable(true);
        em.persist(itemMadeByUser2);
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(user2.getId(), bookingDto));
    }

    @Test
    void addBooking() {

        itemMadeByUser2.setAvailable(true);
        em.persist(itemMadeByUser2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemMadeByUser2.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(10));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(1000));

        BookingDto responseDto = bookingService.addBooking(user1.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery(
                "Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", responseDto.getId()).getSingleResult();


        assertThat(booking.getItem().getName(), equalTo(itemMadeByUser2.getName()));
        assertThat(booking.getBooker().getName(), equalTo(user1.getName()));

        assertThat(responseDto.getItemId(), equalTo(itemMadeByUser2.getId()));
        assertThat(responseDto.getBookerId(), equalTo(user1.getId()));
    }

    @Test
    void approveBookingFail() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(user2.getId(), 100L, true));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(user1.getId(), booking.getId(), true));

        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        assertThrows(InvalidEntityException.class,
                () -> bookingService.approveBooking(user2.getId(), booking.getId(), true));

        booking.setStatus(BookingStatus.REJECTED);
        em.persist(booking);
        assertThrows(InvalidEntityException.class,
                () -> bookingService.approveBooking(user2.getId(), booking.getId(), false));
    }

    private Booking makeBookingByUser1() {
        Booking booking = new Booking();
        booking.setItem(itemMadeByUser2);
        booking.setBooker(user1);
        var now = LocalDateTime.now();
        booking.setStart(now.plusSeconds(100));
        booking.setEnd(now.plusSeconds(10000));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    @Test
    void approveBooking() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        BookingDto responseDto = bookingService.approveBooking(user2.getId(), booking.getId(), true);


        TypedQuery<Booking> query = em.createQuery(
                "Select b from Booking b where b.id = :id", Booking.class);
        Booking receivedBooking = query.setParameter("id", responseDto.getId()).getSingleResult();


        assertThat(receivedBooking.getItem().getName(), equalTo(itemMadeByUser2.getName()));
        assertThat(receivedBooking.getBooker().getName(), equalTo(user1.getName()));
        assertThat(receivedBooking.getStatus(), equalTo(BookingStatus.APPROVED));

        assertThat(responseDto.getItemId(), equalTo(itemMadeByUser2.getId()));
        assertThat(responseDto.getBookerId(), equalTo(user1.getId()));
        assertThat(responseDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingFail() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(user2.getId(), 100L));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(user3.getId(), booking.getId()));
    }

    @Test
    void getBooking() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        BookingDto responseDto = bookingService.getBooking(user2.getId(), booking.getId());

        TypedQuery<Booking> query = em.createQuery(
                "Select b from Booking b where b.id = :id", Booking.class);
        Booking receivedBooking = query.setParameter("id", responseDto.getId()).getSingleResult();


        assertThat(receivedBooking.getItem().getName(), equalTo(itemMadeByUser2.getName()));
        assertThat(receivedBooking.getBooker().getName(), equalTo(user1.getName()));
        assertThat(receivedBooking.getStatus(), equalTo(BookingStatus.WAITING));

        assertThat(responseDto.getItemId(), equalTo(itemMadeByUser2.getId()));
        assertThat(responseDto.getBookerId(), equalTo(user1.getId()));
        assertThat(responseDto.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getUserBookingsFail() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getUserBookings(100L, BookingState.ALL, 0, 10));
    }

    @Test
    void getUserBookings() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        List<BookingDto> response = bookingService.getUserBookings(user1.getId(), BookingState.ALL, 0, 10);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getBookerId(), equalTo(user1.getId()));

        response = bookingService.getUserBookings(user1.getId(), BookingState.CURRENT, 0, 10);
        assertThat(response, hasSize(0));

        response = bookingService.getUserBookings(user1.getId(), BookingState.PAST, 0, 10);
        assertThat(response, hasSize(0));

        response = bookingService.getUserBookings(user1.getId(), BookingState.FUTURE, 0, 10);
        assertThat(response, hasSize(1));

        response = bookingService.getUserBookings(user1.getId(), BookingState.WAITING, 0, 10);
        assertThat(response, hasSize(1));

        response = bookingService.getUserBookings(user1.getId(), BookingState.REJECTED, 0, 10);
        assertThat(response, hasSize(0));
    }

    @Test
    void getOwnerBookingsFail() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getOwnerBookings(100L, BookingState.ALL, 0, 10));
    }

    @Test
    void getOwnerBookings() {
        Booking booking = makeBookingByUser1();
        em.persist(booking);

        List<BookingDto> response = bookingService.getOwnerBookings(user2.getId(), BookingState.ALL, 0, 10);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getBookerId(), equalTo(user1.getId()));
        assertThat(response.get(0).getBooker(), notNullValue());
        assertThat(response.get(0).getItem(), notNullValue());

        response = bookingService.getOwnerBookings(user2.getId(), BookingState.CURRENT, 0, 10);
        assertThat(response, hasSize(0));

        response = bookingService.getOwnerBookings(user2.getId(), BookingState.PAST, 0, 10);
        assertThat(response, hasSize(0));

        response = bookingService.getOwnerBookings(user2.getId(), BookingState.FUTURE, 0, 10);
        assertThat(response, hasSize(1));

        response = bookingService.getOwnerBookings(user2.getId(), BookingState.WAITING, 0, 10);
        assertThat(response, hasSize(1));

        response = bookingService.getOwnerBookings(user2.getId(), BookingState.REJECTED, 0, 10);
        assertThat(response, hasSize(0));
    }
}