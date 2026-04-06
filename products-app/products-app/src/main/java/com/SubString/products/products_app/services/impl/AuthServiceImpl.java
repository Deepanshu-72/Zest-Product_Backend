package com.SubString.products.products_app.services.impl;

import com.SubString.products.products_app.dtos.UserDto;
import com.SubString.products.products_app.services.AuthService;
import com.SubString.products.products_app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl  implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDto registerUser(UserDto userDto) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserDto userDto1 =  userService.createUser(userDto);
        return userDto1;
    }
}
