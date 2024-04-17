package com.root.employeeservice.strategy.context;

import com.root.employeeservice.strategy.MailValidatorStrategy;

public class MailValidator {
    private final MailValidatorStrategy mailValidatorStrategy;

    public MailValidator(MailValidatorStrategy mailValidatorStrategy) {
        this.mailValidatorStrategy = mailValidatorStrategy;
    }

    public boolean validate(String mail) {
        return this.mailValidatorStrategy.validate(mail);
    }
}
