package com.dat.financialtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus(HttpStatus.NOT_FOUND): Chi thi cho Spring Boot tu dong tra ve HTTP status code 404
// khi exception nay duoc nem ra tu bat ky tang nao (controller/service) ma khong can try-catch thu cong.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
