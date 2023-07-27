package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

public class ItemRequestServiceTest {

    ItemRequestService service;

    @Mock
    ItemRequestRepository mockItemRequestRepo;

    @Mock
    UserRepository mockUserRepo;

    @Mock
    ItemRepository mockItemRepo;

    @BeforeEach
    void initService() {

    }

    @Test
    void testAddRequest() {
    }

    @Test
    void testGetAllRequests() {

    }

    @Test
    void testGetRequestById() {

    }

    @Test
    void testGetUserRequests() {

    }
}
