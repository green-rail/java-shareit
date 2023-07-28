package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService service;
    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setName("Ivan");
        user1.setEmail("invan@email.com");
        em.persist(user1);
        //em.flush();

        user2 = new User();
        user2.setName("Ivan2");
        user2.setEmail("invan2@email.com");
        em.persist(user2);
    }

    @Test
    void testAddRequest() {

        var requestDto = new ItemRequestDto();
        assertThrows(UserNotFoundException.class, () -> service.addRequest(100L, requestDto));
        requestDto.setDescription("   ");
        assertThrows(InvalidEntityException.class, () -> service.addRequest(user1.getId(), requestDto));
        System.out.println(user1.getId());

        String requestText = "Test request";
        requestDto.setDescription(requestText);
        ItemRequestDto itemRequestDto = service.addRequest(user1.getId(), requestDto);

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select r from ItemRequest r where r.description = :description", ItemRequest.class);
        ItemRequest request = query.setParameter("description", requestText)
                .getSingleResult();

        assertThat(request.getDescription(), equalTo(requestText));
        assertThat(request.getRequesterId(), equalTo(user1.getId()));

        assertThat(itemRequestDto.getDescription(), equalTo(requestText));
        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
    }

    @Test
    void testGetAllRequests() {
        List<ItemRequest> requestList = List.of(
                makeRequest("Request 1", user2.getId(), 10),
                makeRequest("Request 2", user2.getId(), 5),
                makeRequest("Request 3", user2.getId(), 0)
        );
        requestList.forEach(em::persist);
        em.flush();

        List<ItemRequestDto> requests = service.getAllRequests(0, 10, user1.getId());
        assertThat(requests, hasSize(requestList.size()));
        assertThat(requests.get(0).getDescription(), equalTo("Request 3"));
        assertThat(requests.get(2).getDescription(), equalTo("Request 1"));
        for (ItemRequestDto dto : requests) {
            assertThat(requests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(dto.getDescription()))
            )));
        }
    }

    private ItemRequest makeRequest(String description, Long userId, int offsetSeconds) {
        var request = new ItemRequest();
        request.setDescription(description);
        request.setRequesterId(userId);
        request.setCreated(Instant.now().minusSeconds(offsetSeconds));
        return request;
    }

    @Test
    void testGetRequestById() {

        assertThrows(EntityNotFoundException.class, () -> service.getRequestById(user1.getId(), 100L));

        var request = makeRequest("Request", user1.getId(), 0);
        em.persist(request);

        var item1 = new Item();
        item1.setSharerId(user2.getId());
        item1.setName("Item 1");
        item1.setDescription("Item 1 description");
        item1.setAvailable(true);
        item1.setRequestId(request.getId());

        em.persist(item1);
        ItemRequestDto dto = service.getRequestById(user1.getId(), request.getId());
        assertThat(dto, allOf(
                        hasProperty("id", notNullValue()),
                        hasProperty("description", equalTo("Request")),
                        hasProperty("created", notNullValue()),
                        hasProperty("items", hasSize(1))
                )
        );
    }

    @Test
    void testGetUserRequests() {
        assertThrows(UserNotFoundException.class, () -> service.getUserRequests(100L));

        List<ItemRequest> requestList = List.of(
                makeRequest("Request 1", user1.getId(), 10),
                makeRequest("Request 2", user1.getId(), 5),
                makeRequest("Request 3", user1.getId(), 0),
                makeRequest("Request from user 2", user2.getId(), 100)
        );
        requestList.forEach(em::persist);
        em.flush();

        var item1 = new Item();
        item1.setSharerId(user2.getId());
        item1.setName("Item 1");
        item1.setDescription("Item 1 description");
        item1.setAvailable(true);
        item1.setRequestId(requestList.get(0).getId());
        em.persist(item1);

        List<ItemRequestDto> requests = service.getUserRequests(user1.getId());
        assertThat(requests, hasSize(requestList.size() - 1));
        assertThat(requests.get(0).getDescription(), equalTo("Request 3"));
        assertThat(requests.get(2).getDescription(), equalTo("Request 1"));
        for (ItemRequestDto dto : requests) {
            assertThat(requests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(dto.getDescription()))
            )));
        }
        assertThat(requests.get(2).getItems(), hasSize(1));
        assertThat(requests.get(2).getItems().get(0), hasProperty("name", equalTo("Item 1")));
    }
}
