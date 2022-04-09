package com.zxl.sb.cache.securitydemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/test")
public class AdminController {

    @PermitAll
    @GetMapping("/permitAll")
    public String demo(){
        return "permitAll 所有用户都能访问";
    }

    @GetMapping("/authenticated")
    public String home() {
        return "authenticated 需要登录认证用户才能访问";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "hasRole admin 才能访问";
    }

    @PreAuthorize("hasRole('ROLE_NORMAL')")
    @GetMapping("/normal")
    public String normal() {
        return "access hasRole mormal 才能访问";
    }

}
