package com.SubString.products.products_app.controllers;

import com.SubString.products.products_app.dtos.UserDto;
import com.SubString.products.products_app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {

    private  final UserService userService;



    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));

    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/email/{email}")
    public  ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @DeleteMapping("/{userId}")
    public void  deleteuser(@PathVariable("userId")String userId){
        userService.deleteUser(userId);
    }


    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable("userId") String userId){

        return ResponseEntity.ok(userService.UpdateUser(userDto,userId));


    }

    @GetMapping("{userId}")
    public  ResponseEntity<UserDto> getUserId(@PathVariable("userId") String userId){
        return ResponseEntity.ok(userService.getUserByUserId(userId));
    }





    
}
