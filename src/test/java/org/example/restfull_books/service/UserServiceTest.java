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

import java.util.Map;
import java.util.Optional;

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
        //Создаем объект пользователя, который будет передан в сервис для сохранения
        User requstUser = new User();
        requstUser.setFirstName("John");
        requstUser.setLastName("Doe");
        // Создаем объект пользователя, который должен быть возвращен репозиторием после сохранения
        User responseUser = new User(1L, "John", "Doe");
        //requstUser заменить на responseUser
        // Мокируем поведение репозитория: при вызове метода save с requstUser должен возвращаться responseUser
        when(userRepository.save(requstUser)).thenReturn(responseUser);
        // // Вызываем метод сохранения в сервисе, который должен вернуть сохраненный объект
        User save = userService.save(requstUser);
        // Проверяем, что результат не равен null (пользователь был успешно сохранен)
        assertNotNull(save);
        // Проверяем, что имя пользователя в ответе совпадает с ожидаемым значением
        assertEquals(responseUser.getFirstName(), save.getFirstName());
        // Проверяем, что фамилия пользователя в ответе совпадает с ожидаемым значением
        assertEquals(responseUser.getLastName(), save.getLastName());
        // Проверяем, что ID пользователя в ответе совпадает с ожидаемым значением
        assertEquals(1, save.getId());
        // Проверяем, что метод save репозитория был вызван ровно один раз с объектом requstUser
        verify(userRepository, times(1)).save(requstUser);

    }

    @Test
    void getByUserId() {
        // Создаем ID пользователя, которого будем обновлять
        Long userId = 1L;
        // Создаем объект пользователя, который репозиторий "найдет" по ID
        User responseUser = new User(1L, "John", "Doe");

        // Мокируем поведение метода findById репозитория: возвращаем Optional с найденным пользователем
        when(userRepository.findById(userId)).thenReturn(Optional.of(responseUser));

        //  Вызываем тестируемый метод получения пользователя по ID
        User user = userService.getByUserId(userId);
        // Убеждаемся, что результат не равен null
        assertNotNull(user);
        // Проверяем, что имя пользователя совпадает с ожидаемым (responseUser)
        assertEquals(responseUser.getFirstName(), user.getFirstName());
        // Проверяем, что фамилия пользователя совпадает с ожидаемым (responseUser)
        assertEquals(responseUser.getLastName(), user.getLastName());
        // Проверяем, что ID пользователя совпадает с ожидаемым значением
        assertEquals(1, user.getId());
        // Проверяем, что метод findById был вызван ровно один раз с нужным ID
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUserById() {
        // Создаем ID пользователя, которого будем обновлять
        Long userId = 1L;
        // Создаем объект пользователя до внесения изменений
        User responseUser = new User(1L, "John", "Doe");
        // Мокируем поведение метода findById: возвращаем объект пользователя, если он существует
        when(userRepository.findById(userId)).thenReturn(Optional.of(responseUser));
        // Вызываем тестируемый метод удаления пользователя по ID
        userService.deleteUserById(userId);
        // Проверяем, что метод delete был вызван ровно один раз с объектом updateUser
        verify(userRepository, times(1)).delete(responseUser);
    }

    @Test
    void updateUser() {
        // Создаем ID пользователя, которого будем обновлять
        Long userId = 1L;
        // Создаем объект пользователя до внесения изменений
        User responseUser = new User(userId, "John", "Doe");
        // Создаем объект пользователя с новыми данными
        User updateUser = new User(userId,"Jone", "Doe");


        // Мокируем проверку существования пользователя: метод existsById возвращает true
        when(userRepository.existsById(userId)).thenReturn(true);
        // Мокируем сохранение пользователя: метод save возвращает обновленного пользователя
        when(userRepository.save(updateUser)).thenReturn(updateUser);
        // Вызываем тестируемый метод и сохраняем результат
        User result=userService.updateUser(updateUser);
        // Убеждаемся, что результат не равен null
        assertNotNull(result);
        // Проверяем, что поле firstName было обновлено
        assertEquals(updateUser.getFirstName(), result.getFirstName());
        // Проверяем, что поле lastName было обновлено
        assertEquals(updateUser.getLastName(), result.getLastName());
        // Проверяем, что идентификатор пользователя остался прежним
        assertEquals(updateUser.getId(), result.getId());
        // Проверяем, что метод existsById был вызван ровно один раз для проверки существования пользователя
        verify(userRepository, times(1)).existsById(userId);
        // Проверяем, что метод save был вызван ровно один раз с объектом updateUser
        verify(userRepository, times(1)).save(updateUser);

    }

    @Test
    void updatePartial() {
        // Создаем ID пользователя, которого будем обновлять
        Long userId = 1L;
        // Создаем объект пользователя до внесения изменений
        User responseUser = new User(userId, "John", "Doe");//Пользователь до обновления
        // Создаем карту изменений: обновляем только поле "firstName"
        Map<String, Object> updates = Map.of("firstName", "Jone");//Данные для обновления
        // Создаем объект пользователя с ожидаемыми изменениями
        User updateUser = new User(userId, "Jone", "Doe");//Ожидаемый результат после обновления
        // Настраиваем поведение мока: при вызове findById возвращаем найденного пользователя
        when(userRepository.findById(userId)).thenReturn(Optional.of(responseUser));
        // Настраиваем поведение мока: save должен возвращать тот объект, который ему передали
        when(userRepository.save(any(User.class))).thenAnswer(invocation->invocation.getArgument(0));// Мок возврата обновленного пользователя
        // Вызываем метод updatePartial и получаем результат
        User result=userService.updatePartial(userId, updates);
        // Проверяем, что результат не равен null
        assertNotNull(result);
        // Проверяем, что поле firstName изменилось согласно ожидаемому значению
        assertEquals(updateUser.getFirstName(), result.getFirstName());
        // Проверяем, что поле lastName осталось без изменений
        assertEquals(updateUser.getLastName(), result.getLastName());
        // Проверяем, что идентификатор пользователя остался неизменным
        assertEquals(updateUser.getId(), result.getId());
        // Убеждаемся, что метод findById был вызван ровно один раз
        verify(userRepository, times(1)).findById(userId); // **Добавлено для проверки вызова findById**
        // Убеждаемся, что метод save был вызван с объектом responseUser, модифицированным в методе updatePartial
        verify(userRepository, times(1)).save(responseUser); // **Исправлено: передача responseUser вместо updateUser**

    }
}