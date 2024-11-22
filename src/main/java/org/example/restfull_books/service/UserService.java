package org.example.restfull_books.service;
import org.example.restfull_books.entity.User;
import org.example.restfull_books.exception.CustomGatewayException;
import org.example.restfull_books.exception.CustomNewNotFoundException;
import org.example.restfull_books.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class UserService {

    @Autowired //внедрение зависимости
    private UserRepository userRepository;

    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public User getByUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomNewNotFoundException("Not Found"));
    }

    public User deleteUserById(Long id) {
        try {
            if (id == 1000L) {
                throw new CustomGatewayException("Simulated external service error for testing purposes");
            }
            User user = getByUserId(id);
            userRepository.delete(user);
            return user; // Возвращаем удаленного пользователя
        } catch (CustomNewNotFoundException e) {
            throw e; // Пропускаем исключение "Not Found" и передаем его дальше
        } catch (CustomGatewayException e) {
            throw e; // Пропускаем исключение "Bad Gateway"
        } catch (Exception e) {
            // Выбрасываем RuntimeException для других ошибок, если они не обработаны ранее
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    //PUT
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new CustomNewNotFoundException("User not found");
        }
        return userRepository.save(user); // Сохраняем изменения
    }


    public User updatePartial(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomNewNotFoundException("User not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName" -> user.setFirstName((String) value);
                case "lastName" -> user.setLastName((String) value);
                default -> throw new RuntimeException("Invalid field: " + key);
            }
        });

        return userRepository.save(user);
    }
}
