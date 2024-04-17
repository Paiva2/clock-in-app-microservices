package com.root.employeeservice.strategy.concrete;

import com.root.employeeservice.strategy.MailValidatorStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailValidatorRegexStrategy implements MailValidatorStrategy {
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean validate(String email) {
        Matcher matcher = this.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }
}
