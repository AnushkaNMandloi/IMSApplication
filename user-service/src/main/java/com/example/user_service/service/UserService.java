package com.example.user_service.service;


import com.example.user_service.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface UserService {

    User addUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);

    String deleteUser(Long id);

    List<User> getAllUsers();

}
