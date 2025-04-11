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

    @PostMapping
    public String addUser(@RequestBody User user){
        User newUser = userService.addUser(user);
        if(newUser!=null){
            return "User added successfully!";
        }
        return "Cannot add user";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        Map<String,String> response = Map.of("message", "User added successfully!",
                "userId",  "userId",
                "username", "username",
                "email", "email",
                "role", "role",
                "token", "token");
        if(user!=null){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Map<String, String>> getUserByEmail(@PathVariable String email){
        User user = userService.getUserByEmail(email);
        Map<String,String> response = Map.of("message", "User added successfully!",
                "userId",  "userId",
                "username", "username",
                "email", "email",
                "role", "role",
                "token", "token");
        if(user!=null){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
