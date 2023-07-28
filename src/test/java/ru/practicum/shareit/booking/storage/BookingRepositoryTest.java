package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    private User user1;
    private User user2;
    private User user3;
    private Item itemOwnedByUser1;


    private Booking bookingUser2PastApproved;
    private Booking bookingUser3PastApproved;
    private Booking bookingUser2CurrentApproved;
    private Booking bookingUser2CurrentRejected;
    private Booking bookingUser2FutureApproved;
    private Booking bookingUser2FutureWaiting;

    private final PageRequest page = PageRequest.of(0, 10);

    private final boolean setupComplete = false;

    @BeforeEach
    public void setup() {
        //if (setupComplete) {
        //    return;
        //}
        //setupComplete = true;

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

        itemOwnedByUser1 = new Item();
        itemOwnedByUser1.setName("Item 1");
        itemOwnedByUser1.setDescription("Item 1 description");
        itemOwnedByUser1.setAvailable(true);
        itemOwnedByUser1.setSharerId(user1.getId());
        em.persist(itemOwnedByUser1);

        bookingUser2PastApproved = new Booking();
        bookingUser2PastApproved.setItem(itemOwnedByUser1);
        bookingUser2PastApproved.setBooker(user2);
        bookingUser2PastApproved.setStart(Instant.now().minusSeconds(10_000));
        bookingUser2PastApproved.setEnd(Instant.now().minusSeconds(9_000));
        bookingUser2PastApproved.setStatus(BookingStatus.APPROVED);
        em.persist(bookingUser2PastApproved);

        bookingUser3PastApproved = new Booking();
        bookingUser3PastApproved.setItem(itemOwnedByUser1);
        bookingUser3PastApproved.setBooker(user3);
        bookingUser3PastApproved.setStart(Instant.now().minusSeconds(8_000));
        bookingUser3PastApproved.setEnd(Instant.now().minusSeconds(7_000));
        bookingUser3PastApproved.setStatus(BookingStatus.APPROVED);
        em.persist(bookingUser3PastApproved);

        bookingUser2CurrentApproved = new Booking();
        bookingUser2CurrentApproved.setItem(itemOwnedByUser1);
        bookingUser2CurrentApproved.setBooker(user2);
        bookingUser2CurrentApproved.setStart(Instant.now().minusSeconds(100));
        bookingUser2CurrentApproved.setEnd(Instant.now().plusSeconds(100));
        bookingUser2CurrentApproved.setStatus(BookingStatus.APPROVED);
        em.persist(bookingUser2CurrentApproved);

        bookingUser2CurrentRejected = new Booking();
        bookingUser2CurrentRejected.setItem(itemOwnedByUser1);
        bookingUser2CurrentRejected.setBooker(user2);
        bookingUser2CurrentRejected.setStart(Instant.now().minusSeconds(100));
        bookingUser2CurrentRejected.setEnd(Instant.now().plusSeconds(100));
        bookingUser2CurrentRejected.setStatus(BookingStatus.REJECTED);
        em.persist(bookingUser2CurrentRejected);

        bookingUser2FutureApproved = new Booking();
        bookingUser2FutureApproved.setItem(itemOwnedByUser1);
        bookingUser2FutureApproved.setBooker(user2);
        bookingUser2FutureApproved.setStart(Instant.now().plusSeconds(5_000));
        bookingUser2FutureApproved.setEnd(Instant.now().plusSeconds(6_000));
        bookingUser2FutureApproved.setStatus(BookingStatus.APPROVED);
        em.persist(bookingUser2FutureApproved);

        bookingUser2FutureWaiting = new Booking();
        bookingUser2FutureWaiting.setItem(itemOwnedByUser1);
        bookingUser2FutureWaiting.setBooker(user2);
        bookingUser2FutureWaiting.setStart(Instant.now().plusSeconds(7_000));
        bookingUser2FutureWaiting.setEnd(Instant.now().plusSeconds(8_000));
        bookingUser2FutureWaiting.setStatus(BookingStatus.WAITING);
        em.persist(bookingUser2FutureWaiting);
    }

    @Test
    void findByBookerIdOrderByIdDesc() {
        Page<Booking> response = repository.findByBookerIdOrderByIdDesc(user2.getId(), page);

        assertThat(response.getTotalElements(), equalTo(5L));
    }

    @Test
    void findByBookerIdAndStatusOrderByIdDesc() {
        Page<Booking> response = repository
                .findByBookerIdAndStatusOrderByIdDesc(user2.getId(), BookingStatus.APPROVED, page);
        assertThat(response.getTotalElements(), equalTo(3L));

        response = repository.findByBookerIdAndStatusOrderByIdDesc(user2.getId(), BookingStatus.REJECTED, page);
        assertThat(response.getTotalElements(), equalTo(1L));

        response = repository.findByBookerIdAndStatusOrderByIdDesc(user2.getId(), BookingStatus.WAITING, page);
        assertThat(response.getTotalElements(), equalTo(1L));
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc() {
        Page<Booking> response = repository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(user2.getId(), Instant.now(), Instant.now(), page);
        assertThat(response.getTotalElements(), equalTo(2L));
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByIdDesc() {
        Page<Booking> response = repository
                .findByBookerIdAndEndBeforeOrderByIdDesc(user2.getId(), Instant.now(), page);
        assertThat(response.getTotalElements(), equalTo(1L));
    }

    @Test
    void findByBookerIdAndStartAfterOrderByIdDesc() {
        Page<Booking> response = repository
                .findByBookerIdAndStartAfterOrderByIdDesc(user2.getId(), Instant.now(), page);
        assertThat(response.getTotalElements(), equalTo(2L));
    }

    @Test
    void findByBookerIdAndItemId() {
        List<Booking> response = repository
                .findByBookerIdAndItemId(user2.getId(), itemOwnedByUser1.getId());
        assertThat(response.size(), equalTo(5));
    }

    @Test
    void findAllByOwner() {
        Page<Booking> response = repository.findAllByOwner(user1.getId(), page);
        assertThat(response.getTotalElements(), equalTo(6L));

        response = repository.findAllByOwner(user2.getId(), page);
        assertThat(response.getTotalElements(), equalTo(0L));
    }

    @Test
    void findCurrentByOwner() {
        Page<Booking> response = repository.findCurrentByOwner(user1.getId(), Instant.now(), Instant.now(), page);
        assertThat(response.getTotalElements(), equalTo(2L));
    }

    @Test
    void findPastByOwner() {
        Page<Booking> response = repository.findPastByOwner(user1.getId(), Instant.now(), page);
        assertThat(response.getTotalElements(), equalTo(2L));
    }

    @Test
    void findFutureByOwner() {
        Page<Booking> response = repository.findFutureByOwner(user1.getId(), Instant.now(), page);
        assertThat(response.getTotalElements(), equalTo(2L));
    }

    @Test
    void findByOwnerAndStatus() {
        Page<Booking> response = repository.findByOwnerAndStatus(user1.getId(), BookingStatus.APPROVED, page);
        assertThat(response.getTotalElements(), equalTo(4L));

        response = repository.findByOwnerAndStatus(user1.getId(), BookingStatus.WAITING, page);
        assertThat(response.getTotalElements(), equalTo(1L));

        response = repository.findByOwnerAndStatus(user1.getId(), BookingStatus.REJECTED, page);
        assertThat(response.getTotalElements(), equalTo(1L));
    }

    @Test
    void findByItemOrderByStartAsc() {
        List<Booking> response = repository.findByItemOrderByStartAsc(itemOwnedByUser1);
        assertThat(response.size(), equalTo(6));
        assertThat(response.get(0).getId(), equalTo(bookingUser2PastApproved.getId()));
        assertThat(response.get(response.size() - 1).getId(), equalTo(bookingUser2FutureWaiting.getId()));
    }
}