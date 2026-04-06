package com.SubString.products.products_app.services.impl;

import com.SubString.products.products_app.dtos.UserDto;
import com.SubString.products.products_app.entity.Provider;
import com.SubString.products.products_app.entity.User;
import com.SubString.products.products_app.exceptions.ResourceNotFoundException;
import com.SubString.products.products_app.helpers.UserHelper;
import com.SubString.products.products_app.repositories.UserRepository;
import com.SubString.products.products_app.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private  final UserRepository  userRepository;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if(userDto.getEmail() == null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is Required");
        }

        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("Email Already Exists!!");
        }

      User user =   modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL );
        User savedUser = userRepository.save(user);


        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
       User user = userRepository
               .findByEmail(email)
               .orElseThrow(() -> new ResourceNotFoundException("User Not found with given email"));
       return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto UpdateUser(UserDto userDto, String userId) {

        UUID uid = UserHelper.parseUUID(userId);

        User existinguser =  userRepository
                .findById(uid).
                orElseThrow(() -> new ResourceNotFoundException("User Not Founf With this ID"));

        if(userDto.getName() != null) existinguser.setName(userDto.getName());
        if(userDto.getImage() != null) existinguser.setImage(userDto.getImage());
        if(userDto.getProvider() != null) existinguser.setProvider(userDto.getProvider());


        existinguser.setEnable(userDto.isEnable());
        existinguser.setUpdatedAt(Instant.now());
        User updateduser = userRepository.save(existinguser);
        return modelMapper.map(updateduser,UserDto.class);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {

        UUID uid = UserHelper.parseUUID(userId);

      User user =  userRepository.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User Not Founf With this ID"));

      userRepository.delete(user);



    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UUID uid = UserHelper.parseUUID(userId);

        User user =  userRepository.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User Not Founf With this ID"));


        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map( user -> modelMapper.map(user,UserDto.class))
                .toList();
    }
}
