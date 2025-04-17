package com.example.admin_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {

    private long userId;

    private String userName;

    private String email;

    private String role;

}
