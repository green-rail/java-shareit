package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
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

class ItemRequestServiceImplUnitTest {

    private static final User user = new User(
            1L,
            "User name",
            "user@email.com"
    );

    private static final ItemRequest request = new ItemRequest(
            1L,
            "Description",
            1L,
            Instant.now(),
            List.of(new Item(
                    1L,
                    1L,
                    "Item 1",
                    "Item description",
                    true,
                    1L))
            );

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestService itemRequestService;

    @BeforeEach
    void setup() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void addRequest() {

        ItemRequestDto dto = new ItemRequestDto(request.getId(), "  ", LocalDateTime.now(), null);
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemRequestService.addRequest(100L, dto));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(InvalidEntityException.class, () -> itemRequestService.addRequest(1L, dto));

        Mockito.when(itemRequestRepository.save(any())).thenReturn(request);
        ItemRequestDto validDto = new ItemRequestDto(request.getId(), request.getDescription(), LocalDateTime.now(), null);
        ItemRequestDto response = itemRequestService.addRequest(user.getId(), validDto);

        assertThat(response.getDescription(), equalTo(request.getDescription()));
    }

    @Test
    void getUserRequests() {

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getUserRequests(100L));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.getByRequesterIdWithResponses(anyLong())).thenReturn(List.of(request));
        List<ItemRequestDto> response = itemRequestService.getUserRequests(1L);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getDescription(), equalTo(request.getDescription()));
    }

    @Test
    void getAllRequests() {

        Mockito.when(itemRequestRepository.getAllWithResponses(any())).thenReturn(List.of(request));

        List<ItemRequestDto> response = itemRequestService.getAllRequests(0, 10, 2L);
        assertThat(response, hasSize(1));
        assertThat(response.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(response.get(0).getItems(), hasSize(1));

        List<ItemRequestDto> emptyResponse = itemRequestService.getAllRequests(0, 10, 1L);
        assertThat(emptyResponse, hasSize(0));
    }

    @Test
    void getRequestById() {

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequestById(100L, 1L));

        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(100L, 1L));
        Mockito.when(itemRequestRepository.getByIdWithResponses(anyLong())).thenReturn(Optional.of(request));

        ItemRequestDto response = itemRequestService.getRequestById(1L, 1L);
        assertThat(response.getDescription(), equalTo(request.getDescription()));
        assertThat(response.getItems(), hasSize(1));
    }
}