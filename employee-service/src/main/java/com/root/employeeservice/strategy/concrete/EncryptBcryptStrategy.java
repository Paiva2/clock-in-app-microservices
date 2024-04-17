package com.root.employeeservice.strategy.concrete;

import com.root.employeeservice.strategy.EncryptStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptBcryptStrategy implements EncryptStrategy {
    @Override
    public String hash(String rawPass) {
        return new BCryptPasswordEncoder(11).encode(rawPass);
    }
}
