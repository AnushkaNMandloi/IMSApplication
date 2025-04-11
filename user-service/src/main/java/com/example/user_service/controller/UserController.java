package com.example.user_service.controller;


import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String testEndpoint(){
        return "Test successful!";
    }

    @PostMapping
    public String addUser(@RequestBody User user){
        if(userService.getUserByEmail(user.getEmail())!=null){
            return "User with this email already exists, try adding another email";
        }
        User newUser = userService.addUser(user);
        if(newUser!=null){
            return "User added successfully!";
        }
        return "Cannot add user";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        if(user==null){
            return new ResponseEntity<>(Map.of("message", "User not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "User found!",
                "userId", String.valueOf(user.getUserId()),
                "username", user.getUserName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "token", user.getToken());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> getUserByEmail(@PathVariable String email){
        User user = userService.getUserByEmail(email);
        if(user==null){
            return new ResponseEntity<>(Map.of("message", "User not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "User found!",
                "userId", String.valueOf(user.getUserId()),
                "username", user.getUserName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "token", user.getToken());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

}
