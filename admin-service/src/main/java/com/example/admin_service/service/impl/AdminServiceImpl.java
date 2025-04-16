package com.example.admin_service.service.impl;

import com.example.admin_service.service.AdminService;

public class AdminServiceImpl implements AdminService {


    @Override
    public boolean authenticateAdmin(String adminName, String password) {
        return adminName.equals("admin") && password.equals("admin");
    }

}
