package com.SubString.products.products_app.services;

import com.SubString.products.products_app.dtos.UserDto;

public interface AuthService {
    UserDto registerUser(UserDto userDto);
}
