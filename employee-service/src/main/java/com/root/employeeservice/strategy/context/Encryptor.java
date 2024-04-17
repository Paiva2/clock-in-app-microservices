package com.root.employeeservice.strategy.context;

import com.root.employeeservice.strategy.EncryptStrategy;

public class Encryptor {
    private final EncryptStrategy encryptStrategy;

    public Encryptor(EncryptStrategy encryptStrategy) {
        this.encryptStrategy = encryptStrategy;
    }

    public String encrypt(String rawPass) {
        return this.encryptStrategy.hash(rawPass);
    }
}
