package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.InvalidCommentAuthorException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;

    private User user1;
    private User user2;

    private Item item;
    private Comment comment;
    private Booking lastBooking;
    private Booking nextBooking;

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

        item = new Item();
        item.setName("Item 1");
        item.setDescription("Item 1 description");
        item.setAvailable(false);
        item.setSharerId(user2.getId());
        em.persist(item);

        comment = new Comment();
        comment.setCommentText("comment");
        comment.setItemId(item.getId());
        comment.setAuthor(user1);
        em.persist(comment);

        //Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now();
        lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setBooker(user1);
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setStart(now.minusSeconds(10000));
        lastBooking.setEnd(now.minusSeconds(1000));
        em.persist(lastBooking);

        nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setBooker(user1);
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setStart(now.plusSeconds(100));
        nextBooking.setEnd(now.plusSeconds(10000));
        em.persist(nextBooking);
    }

    @Test
    void addItemFail() {
        ItemDto invalidDto = new ItemDto(1L, "   ", "description",
                true, null, null, null, 1L);

        assertThrows(InvalidEntityException.class, () -> itemService.addItem(1L, invalidDto));

        ItemDto dto = new ItemDto(1L, "Item name", "description",
                true, null, null, null, 1L);
        assertThrows(UserNotFoundException.class, () -> itemService.addItem(100L, dto));
    }


    @Test
    void addItem() {
        ItemDto dto = new ItemDto(1L, "Item name", "description", true,
                null, null, null, null);
        ItemDto response = itemService.addItem(user1.getId(), dto);

        assertThat(response.getName(), equalTo(dto.getName()));
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", dto.getName())
                .getSingleResult();

        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getDescription(), equalTo(dto.getDescription()));
        assertThat(item.isAvailable(), equalTo(dto.getAvailable()));
    }

    @Test
    void updateItemFail() {
        ItemDto dto = new ItemDto(1L, "Item name", "description", true,
                null, null, null, null);

        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(100L, dto, item.getId()));
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(user2.getId(), dto, 100L));
        assertThrows(OwnerMismatchException.class, () -> itemService.updateItem(user1.getId(), dto, item.getId()));
    }

    @Test
    void updateItem() {
        ItemDto dto = new ItemDto(null, "Updated name", "Updated description", true,
                null, null, null, null);

        ItemDto response = itemService.updateItem(user2.getId(), dto, item.getId());

        assertThat(response.getId(), equalTo(item.getId()));
        assertThat(response.getName(), equalTo(dto.getName()));
        assertThat(response.getDescription(), equalTo(dto.getDescription()));
        assertThat(response.getAvailable(), equalTo(dto.getAvailable()));

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item responseItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(responseItem.getId(), equalTo(item.getId()));
        assertThat(responseItem.getName(), equalTo(dto.getName()));
        assertThat(responseItem.getDescription(), equalTo(dto.getDescription()));
        assertThat(responseItem.isAvailable(), equalTo(dto.getAvailable()));
    }


    @Test
    void getItemById() {
        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(user1.getId(), 100L));


        ItemDto response = itemService.getItemById(user1.getId(), item.getId());
        assertThat(response.getId(), equalTo(item.getId()));
        assertThat(response.getName(), equalTo(item.getName()));
        assertThat(response.getDescription(), equalTo(item.getDescription()));
        assertThat(response.getAvailable(), equalTo(item.isAvailable()));
        assertThat(response.getComments(), hasSize(1));
        assertThat(response.getLastBooking(), nullValue());
        assertThat(response.getNextBooking(), nullValue());

        response = itemService.getItemById(user2.getId(), item.getId());
        assertThat(response.getId(), equalTo(item.getId()));
        assertThat(response.getName(), equalTo(item.getName()));
        assertThat(response.getDescription(), equalTo(item.getDescription()));
        assertThat(response.getAvailable(), equalTo(item.isAvailable()));
        assertThat(response.getComments(), hasSize(1));
        assertThat(response.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(response.getNextBooking().getId(), equalTo(nextBooking.getId()));
    }

    @Test
    void getAllForSharerFail() {
        assertThrows(UserNotFoundException.class,
                () -> itemService.getAllForSharer(100L, 0, 10));


        List<ItemDto> response = itemService.getAllForSharer(user1.getId(), 0, 10);
        assertThat(response, hasSize(0));
    }

    @Test
    void getAllForSharer() {
        List<ItemDto> response = itemService.getAllForSharer(user2.getId(), 0, 10);
        assertThat(response.get(0).getId(), equalTo(item.getId()));
        assertThat(response.get(0).getName(), equalTo(item.getName()));
        assertThat(response.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(response.get(0).getAvailable(), equalTo(item.isAvailable()));
        assertThat(response.get(0).getComments(), hasSize(1));
        assertThat(response.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(response.get(0).getNextBooking().getId(), equalTo(nextBooking.getId()));
    }

    @Test
    void search() {

        Item itemToFind = new Item();
        itemToFind.setName("Item 2");
        itemToFind.setDescription("Item 2 description");
        itemToFind.setAvailable(false);
        itemToFind.setSharerId(user2.getId());
        em.persist(item);

        List<ItemDto> response = itemService.search("item", 0, 10);
        assertThat(response, hasSize(0));

        itemToFind.setAvailable(true);
        em.persist(itemToFind);

        response = itemService.search("item", 0, 10);
        assertThat(response, hasSize(1));

        response = itemService.search("  ", 0, 10);
        assertThat(response, hasSize(0));

        response = itemService.search("no match search", 0, 10);
        assertThat(response, hasSize(0));

        response = itemService.search("   iteM  ", 0, 10);
        assertThat(response, hasSize(1));

        response = itemService.search("descr  ", 0, 10);
        assertThat(response, hasSize(1));

    }

    @Test
    void addComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment text");
        assertThrows(UserNotFoundException.class,
                () -> itemService.addComment(100L, item.getId(), commentDto));

        assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(user1.getId(), 100L, commentDto));

        User user3 = new User();
        user3.setName("Ivan3");
        user3.setEmail("invan3@email.com");
        em.persist(user3);

        assertThrows(InvalidCommentAuthorException.class,
                () -> itemService.addComment(user3.getId(), item.getId(), commentDto));

        CommentDto response = itemService.addComment(user1.getId(), item.getId(), commentDto);

        assertThat(response.getText(), equalTo(commentDto.getText()));
    }
}