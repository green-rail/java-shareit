package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.InvalidCommentAuthorException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
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
import static org.mockito.ArgumentMatchers.*;

class ItemServiceImplUnitTest {

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemService itemService;


    private final Item item = new Item(
            1L,
            1L,
            "Item 1",
            "Item description",
            true,
            1L
    );

    private final User owner = new User(
            1L,
            "Owner",
            "user@email.com"
    );

    private final User booker = new User(
            2L,
            "Booker",
            "user1@email.com"
    );

    private final Comment comment = new Comment(
            1L,
            "Comment text",
            1L,
            booker,
            Instant.now()
    );

    private final Booking lastBooking = new Booking(
            1L,
            //LocalDateTime.now().toInstant(ZoneOffset.UTC).minusSeconds(1000),
            //LocalDateTime.now().toInstant(ZoneOffset.UTC).minusSeconds(500),
            LocalDateTime.now().minusSeconds(1000),
            LocalDateTime.now().minusSeconds(500),
            item,
            booker,
            BookingStatus.APPROVED
    );

    private final Booking nextBooking = new Booking(
            2L,
            //LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(500),
            //LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(1000),
            LocalDateTime.now().plusSeconds(500),
            LocalDateTime.now().plusSeconds(1000),
            item,
            booker,
            BookingStatus.WAITING
    );


    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }


    @Test
    void addItem() {

        ItemDto dto = new ItemDto(
                1L,
                "",
                item.getDescription(),
                true,
                null,
                null,
                null,
                null
        );

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(InvalidEntityException.class, () -> itemService.addItem(1L, dto));

        ItemDto validDto = new ItemDto(
                1L,
                item.getName(),
                item.getDescription(),
                true,
                null,
                null,
                null,
                null
        );
        assertThrows(UserNotFoundException.class, () -> itemService.addItem(100L, validDto));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto response = itemService.addItem(1L, validDto);
        assertThat(response.getName(), equalTo(item.getName()));
        assertThat(response.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void updateItem() {

        ItemDto updateDto = new ItemDto(
                1L,
                "updated name",
                "updated description",
                true,
                null,
                null,
                null,
                null
        );

        Item updatedItem = new Item(
                1L,
                1L,
                updateDto.getName(),
                updateDto.getDescription(),
                true,
                1L
        );


        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(1L, updateDto, 1L));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(1L, updateDto, 100L));

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));
        assertThrows(OwnerMismatchException.class, () -> itemService.updateItem(10L, updateDto, 1L));

        Mockito.when(itemRepository.save(any())).thenReturn(updatedItem);
        ItemDto response = itemService.updateItem(1L, updateDto, 1L);
        assertThat(response.getName(), equalTo(updatedItem.getName()));
        assertThat(response.getDescription(), equalTo(updatedItem.getDescription()));
    }

    @Test
    void getItemById() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1L, 10L));

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findByItemOrderByStartAsc(any())).thenReturn(List.of(lastBooking, nextBooking));

        ItemDto response = itemService.getItemById(booker.getId(), 1L);
        assertThat(response.getName(), equalTo(item.getName()));
        assertThat(response.getComments(), hasSize(1));
        assertThat(response.getLastBooking(), equalTo(null));
        assertThat(response.getNextBooking(), equalTo(null));

        ItemDto ownerResponse = itemService.getItemById(owner.getId(), 1L);
        assertThat(ownerResponse.getName(), equalTo(item.getName()));
        assertThat(ownerResponse.getComments(), hasSize(1));
        assertThat(ownerResponse.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(ownerResponse.getNextBooking().getId(), equalTo(nextBooking.getId()));
    }

    @Test
    void getAllForSharer() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.getAllForSharer(1L, 0, 10));

        Mockito.when(itemRepository.findAllBySharerId(anyLong(), any())).thenReturn(makePage());
        Mockito.when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findByItemOrderByStartAsc(any())).thenReturn(List.of(lastBooking, nextBooking));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        List<ItemDto> ownerResponse = itemService.getAllForSharer(owner.getId(), 0, 10);
        assertThat(ownerResponse, hasSize(1));
        assertThat(ownerResponse.get(0).getName(), equalTo(item.getName()));
        assertThat(ownerResponse.get(0).getComments(), hasSize(1));
        assertThat(ownerResponse.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(ownerResponse.get(0).getNextBooking().getId(), equalTo(nextBooking.getId()));
    }

    private Page<Item> makePage() {
        int totalElements = 10;
        int pageSize = 5;
        PageRequest pageable = PageRequest.of(0, pageSize);
        return new PageImpl<>(List.of(item), pageable, totalElements);
    }

    @Test
    void search() {
        List<ItemDto> response = itemService.search("   ", 0, 10);
        assertThat(response, hasSize(0));

        ItemDto validDto = new ItemDto(
                1L,
                item.getName(),
                item.getDescription(),
                true,
                null,
                null,
                null,
                null
        );

        Mockito.when(itemRepository
                .findByAvailableTrueAndDescriptionContainingIgnoreCase(anyString(), any()))
                .thenReturn(makePage());

        List<ItemDto> searchResponse = itemService.search("Item", 0, 10);
        assertThat(searchResponse, hasSize(1));
        assertThat(searchResponse.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    void addComment() {
        CommentDto commentDto = new CommentDto(
                1L,
                comment.getCommentText(),
                comment.getAuthor().getName(),
                Instant.now()
        );
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemService.addComment(10L, 1L, commentDto));

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(1L, 1L, commentDto));

        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(List.of(nextBooking));
        assertThrows(InvalidCommentAuthorException.class, () -> itemService.addComment(1L, 1L, commentDto));

        Mockito.when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(List.of(lastBooking, nextBooking));
        Mockito.when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto response = itemService.addComment(1L, 1L, commentDto);
        assertThat(response.getText(), equalTo(comment.getCommentText()));
        assertThat(response.getAuthorName(), equalTo(comment.getAuthor().getName()));
    }
}