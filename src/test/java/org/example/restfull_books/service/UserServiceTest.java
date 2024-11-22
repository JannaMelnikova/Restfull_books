package org.example.restfull_books.service;

import org.example.restfull_books.entity.User;
import org.example.restfull_books.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)//внедрение мока
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }



    @Test
    void save() {
        User requstUser = new User();
        requstUser.setFirstName("John");
        requstUser.setLastName("Doe");

        User responseUser = new User(1L, "John", "Doe");
        //requstUser заменить на responseUser
        when(userRepository.save(requstUser)).thenReturn(responseUser);
        User save = userService.save(requstUser);

        assertNotNull(save);
        assertEquals(responseUser.getFirstName(), save.getFirstName());
        assertEquals(responseUser.getLastName(), save.getLastName());
        assertEquals(1, save.getId());

        verify(userRepository,times(1)).save(requstUser);

    }

    @Test
    void getByUserId() {
    }

    @Test
    void deleteUserById() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void updatePartial() {
    }
}