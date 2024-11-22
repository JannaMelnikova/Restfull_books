package org.example.restfull_books.exception;

import org.springframework.web.client.HttpServerErrorException;

//Пользовательское исключение для BAD_GATEWAY(502 ошибка)
        public class CustomGatewayException extends RuntimeException {
            public CustomGatewayException(String message, Throwable cause) {
                super(message, cause);
            }
            public CustomGatewayException(String message) {
                super(message);
            }


        }