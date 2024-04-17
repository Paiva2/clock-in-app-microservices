package com.root.employeeservice.strategy;

public interface EncryptStrategy {
    String hash(String rawPass);
}
