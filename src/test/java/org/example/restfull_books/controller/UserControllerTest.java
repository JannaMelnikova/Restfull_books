package org.example.restfull_books.controller;

import org.example.restfull_books.entity.User;
import org.example.restfull_books.exception.CustomNewNotFoundException;
import org.example.restfull_books.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // 201 Created Тест успешного создания пользователя POST-запрос
    @Test
    public void testCreateUserSuccess() throws Exception {
        // JSON для создания пользователя
        String userJson = """
                {
                    "firstName": "Михаил",
                    "lastName": "Булгаков"
                }
                """;

        // Настройка мока: userService.save должен возвращать созданного пользователя
        when(userService.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "Михаил", "Булгаков"));

        // Тестирование запроса POST /users/new
        mockMvc.perform(
                         post("/users/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated()) // Статус 201 Created
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Михаил"))
                .andExpect(jsonPath("$.lastName").value("Булгаков"));
    }

    // 200 OK тест внесение изменений в существующий пользователь, PUT-запрос
    @Test
    public void testPutUserSuccess() throws Exception {
        // Создаем исходные данные
        User existingUser = new User(1L, "Иван", "Иванов");
        User updatedUser = new User(1L, "Петр", "Петров");

        // Настройка мока для userService.updateUser
        when(userService.updateUser(Mockito.any(User.class))).thenReturn(updatedUser);

        // JSON для обновления пользователя
        String userJson = """
                {
                    "firstName": "Петр",
                    "lastName": "Петров"
                }
                """;

        // Тестирование PUT-запрос
        mockMvc.perform(put("/users/1") // URL с ID пользователя
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)) // Тело запроса
                .andExpect(status().isOk()) // Ожидаем статус 200 OK
                .andExpect(jsonPath("$.firstName").value("Петр")) // Проверяем, что firstName обновлен
                .andExpect(jsonPath("$.lastName").value("Петров")); // Проверяем, что lastName обновлен
    }
    // 200 ОК Тест на внесение изменений по одному из двух полей, PATCH-запрос
    @Test
    public void testPatchUpdateUser() throws Exception {
        // Создаем исходные данные
        User existingUser = new User(1L, "Иван", "Иванов");
        User updatedUser = new User(1L, "Алексей", "Иванов");

        // Настройка мока
        when(userService.updatePartial(Mockito.eq(1L), Mockito.anyMap()))
                .thenReturn(updatedUser);

        // JSON для частичного обновления
        String patchJson = """
            {
                "firstName": "Алексей"
            }
            """;

        // Тестируем PATCH-запрос
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk()) // Ожидаем статус 200 OK
                .andExpect(jsonPath("$.firstName").value("Алексей")) // Проверяем, что поле обновлено
                .andExpect(jsonPath("$.lastName").value("Иванов")); // Убедиться, что lastName остался неизменным
    }
    // 200 ОК GET-запрос. Тест на получение ответа о существующем пользователе
    @Test
    public void testGetUserSuccess() throws Exception {
        // Настройка мока для userService.findById
        when(userService.getByUserId(1L))
                .thenReturn(new User(1L, "Михаил", "Булгаков"));
        // Тестирование запроса GET /users/{id}
        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // Статус 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Михаил"))
                .andExpect(jsonPath("$.lastName").value("Булгаков"));
    }
    // 204, No Content. DELETE-запрос. Тест удаления существующего пользователя успешно
    @Test
    public void testdeleteNoContent() throws Exception {
        // Тестирование запроса DELETE /users/{id}
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isNoContent()); // Статус 204 No Content
    }
            // 500 Internal Server Error. POST-запрос (внутрення ошибка сервера). Браузер отправил запрос корректно, но сервер не смог его обработать
             @Test
    public void testCreateUserInternalServerError() throws Exception {
        // JSON для создания пользователя
        String userJson = """
                {
                    "firstName": "Михаил",
                    "lastName": "Булгаков"
                }
                """;
        // Настройка мока: userService.save выбрасывает RuntimeException
        Mockito.doThrow(new RuntimeException("Internal server error"))
                .when(userService).save(Mockito.any(User.class));

        // Тестирование запроса POST /users/new
        mockMvc.perform(post("/users/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                        .andExpect(status().isInternalServerError()); // Ожидаем статус 500 Internal Server Error
                        //.andExpect(jsonPath("$.message").value("Internal server error: Internal server error"));
    }

    // NotFound, 404 GET-запрос. Тест получения ответа, что пользователь не найден
    @Test
    public void testGetUserNotFound() throws Exception {
        // Настройка мока для userService.findById
        when(userService.getByUserId(1L))
                .thenThrow(new CustomNewNotFoundException("Not found"));

        // Тестирование запроса GET /users/{id}
        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound()); // Статус 404 Not Found
    }

    //---------------------------------------------------------------------стоп
    //404 Not Found. PUT-запрос. Нет пользователя для внесения изменений.
    @Test
    public void testUpdateUserNotFound() throws Exception {
        // Настройка мока: выбросить CustomNewNotFoundException
        Mockito.doThrow(new CustomNewNotFoundException("User not found"))
                .when(userService).updateUser(Mockito.any(User.class));

        // JSON для обновления пользователя
        String userJson = """
                {
                    "firstName": "Петр",
                    "lastName": "Петров"
                }
                """;

        // Тестируем PUT-запрос
        mockMvc.perform(put("/users/100") // URL с несуществующим ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)) // Тело запроса
                .andExpect(status().isNotFound()); // Ожидаем статус 404 Not Found
        //.andExpect(jsonPath("$").value("User not found")); // Проверяем сообщение об ошибке
    }
    // 404, Not Found. DELETE-запрос. Тест удаление пользователя, если его нет в БД.
    @Test
    public void testDeleteUserNotFound() throws Exception {
        // Настройка мока для userService.deleteUserById
        Mockito.doThrow(new RuntimeException("Not Found"))
                .when(userService).deleteUserById(1L);
        // Тестирование запроса DELETE /users/{id}
        mockMvc.perform(delete("/users/", 1))
                .andExpect(status().isNotFound()); // Статус 404 Internal Server Error
    }
    // 405 Not Allowed. Вместо GET-запроса, POST-запрос. Метод не поддерживается. Сервер получил запрос  /users/id с заданным http-методом, смог его распознать, но не дает добро на его реализацию
    @Test
    public void testGetNotAllowed() throws Exception {
        // Настройка мока для userService.findById: выбрасываем исключение
        //Mockito.doThrow(new RuntimeException("Method Not Allowed"))
        // .when(userService).getByUserId(1L);

        // Отправляем POST вместо GET для маршрута, который поддерживает только GET
        mockMvc.perform(post("/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed()); // Статус 405 Not Allowed
    }
    // 500 Internal Server Error. DELETE-запрос.(внутрення ошибка сервера). Браузер отправил запрос корректно, но сервер не смог его обработать
    @Test
    public void testDeleteUserInternalServerError() throws Exception {
        // Настройка мока для userService.deleteUserById
        Mockito.doThrow(new RuntimeException("Internal server error"))
                .when(userService).deleteUserById(1000L);
        // Тестирование запроса DELETE /users/{id}
        mockMvc.perform(delete("/users/{id}", 1000)) // Передаем ID в путь
                .andExpect(status().isInternalServerError()); // Ожидаем статус 500 Internal Server Error
    }



    //502 BAD_GATEWAY при создании пользователя(проблема на стороне сервера), post-запрос /users/new, ответ от сервера, если есть сбой. Причины возникновения ошибки: высокая нагрузка сервера, сетевые проблемы, проблемы DNS, хакерская атака, ошибки в коде сайта, проблемы с браузером. Лечить - отключить плагины поочередно.
//    @Test
//    public void testCreateUserBadGateway() throws Exception {
//        // JSON для создания пользователя
//        String userJson = """
//            {
//                "firstName": "Михаил",
//                "lastName": "Булгаков"
//            }
//            """;
//
//        // Настройка мока: userService.save выбрасывает RuntimeException
//        when(userService.save(Mockito.any(User.class)))
//                .thenThrow(new CustomGatewayException("External service unavailable"));
//
//        // Тестирование POST-запроса
//        mockMvc.perform(post("/users/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userJson))
//                .andExpect(status().isBadGateway()) // Ожидаем статус 502 Bad Gateway
//                .andExpect(content().string(org.hamcrest.Matchers.containsString("External service unavailable"))); // Проверяем сообщение об ошибке
//    }
}



