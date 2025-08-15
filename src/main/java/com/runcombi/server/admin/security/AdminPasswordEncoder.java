package com.runcombi.server.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminPasswordEncoder {

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
