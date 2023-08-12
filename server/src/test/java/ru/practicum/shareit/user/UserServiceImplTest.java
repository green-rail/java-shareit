package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;


    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setName("Ivan");
        user1.setEmail("ivan1@email.com");
        em.persist(user1);

        user2 = new User();
        user2.setName("Ivan2");
        user2.setEmail("ivan2@email.com");
        em.persist(user2);
    }

    @Test
    void getAllUsers() {
        List<UserDto> response = userService.getAllUsers();
        assertThat(response, hasSize(2));
    }

    @Test
    void getUser() {
        assertThrows(UserNotFoundException.class, () -> userService.getUser(100L));
        UserDto response = userService.getUser(user1.getId());
        assertThat(response.getId(), equalTo(user1.getId()));
        assertThat(response.getName(), equalTo(user1.getName()));
        assertThat(response.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void addUser() {

        UserDto dto = new UserDto(null, "new@email.com", "New user name");
        UserDto response = userService.addUser(dto);
        assertThat(response.getName(), equalTo(dto.getName()));
        assertThat(response.getEmail(), equalTo(dto.getEmail()));

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", dto.getEmail()).getSingleResult();
        assertThat(user.getName(), equalTo(dto.getName()));
        assertThat(user.getEmail(), equalTo(dto.getEmail()));

        final UserDto sameEmailDto = new UserDto(null, "ivan1@email.com", "user name");
        assertThrows(DataConflictException.class, () -> userService.addUser(sameEmailDto));
    }

    @Test
    void updateUser() {
        UserDto dto = new UserDto(null, "ivan2@email.com", null);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(100L, dto));
        assertThrows(DataConflictException.class, () -> userService.updateUser(user1.getId(), dto));

        UserDto updateDto1 = new UserDto(null, "updated@email.com", null);
        UserDto response = userService.updateUser(user1.getId(), updateDto1);
        assertThat(response.getEmail(), equalTo(updateDto1.getEmail()));

        UserDto updateDto2 = new UserDto(null, null, "updated name");
        response = userService.updateUser(user1.getId(), updateDto2);
        assertThat(response.getName(), equalTo(updateDto2.getName()));
    }

    @Test
    void removeUser() {
        assertThrows(UserNotFoundException.class, () -> userService.removeUser(100L));

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", user1.getId()).getSingleResult();
        assertThat(user, notNullValue());

        userService.removeUser(user1.getId());

        assertThrows(NoResultException.class, query::getSingleResult);
    }
}