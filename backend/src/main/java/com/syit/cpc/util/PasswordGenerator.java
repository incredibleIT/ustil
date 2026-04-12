package com.syit.cpc.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String hashedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt 哈希: " + hashedPassword);
        System.out.println("\nMySQL 更新命令:");
        System.out.println("mysql -u root -pyy3908533 -e \"USE syit_cpc; UPDATE users SET password = '" + hashedPassword + "' WHERE email = 'admin@syit.edu.cn';\"");
    }
}
