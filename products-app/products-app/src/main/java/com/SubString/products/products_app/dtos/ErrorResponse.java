package com.SubString.products.products_app.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus status,
        int StatusCode

) {


}
