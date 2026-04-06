package com.SubString.products.products_app.services;

import com.SubString.products.products_app.dtos.UserDto;

public interface UserService {



    UserDto createUser(UserDto userDto);


    UserDto getUserByEmail(String email);

    UserDto UpdateUser(UserDto userDto, String userId);


    void deleteUser(String userId);


    UserDto getUserByUserId(String userId);


    Iterable<UserDto> getAllUsers();







    
}
