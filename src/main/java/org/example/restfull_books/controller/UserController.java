package org.example.restfull_books.controller;

import org.example.restfull_books.exception.CustomGatewayException;
import org.example.restfull_books.entity.User;
import org.example.restfull_books.exception.CustomNewNotFoundException;
import org.example.restfull_books.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.getByUserId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build(); // Возвращаем статус 204 No Content при успешном удалении
        } catch (CustomNewNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // Статус 404
        } catch (CustomGatewayException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Bad Gateway: " + ex.getMessage()); // Статус 502
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()); // Статус 500
        }
    }

    @PostMapping("/new")
    public ResponseEntity<User> newUser(@RequestBody User user) {
        try {
            User userSaved = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userSaved); // Статус 201 при успешном создании
//        } catch (MethodArgumentNotValidException ex) {
//            // Это исключение будет перехвачено глобальным обработчиком.
//            throw ex; // Исключение будет передано на глобальный обработчик
        } catch (CustomNewNotFoundException ex) {
            // Если есть кастомное исключение "NotFound", передаем на глобальный обработчик
            throw ex;
        } catch (RuntimeException ex) {
            // В случае других исключений (например, исключений в сервисе)
            throw new RuntimeException("Internal server error", ex); // Это будет обработано в глобальном обработчике
        }
        catch (Exception ex) {
            // Если возникло какое-то другое исключение, которое не попало в вышеуказанные категории
            throw new RuntimeException("Unexpected error: " + ex.getMessage(), ex); // Бросаем RuntimeException для обработки в глобальном обработчике
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User user) {
        try {
            user.setId(id); // Устанавливаем ID из пути
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser); // Возвращаем статус 200 OK
        } catch (CustomNewNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Статус 404, если пользователь не найден
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserField(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            User updatedUser = userService.updatePartial(id, updates);
            return ResponseEntity.ok(updatedUser); // Возвращаем 200 OK с обновлённым пользователем
        } catch (CustomNewNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }

    }
}
