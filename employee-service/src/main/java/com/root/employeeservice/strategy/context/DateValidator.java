package com.root.employeeservice.strategy.context;

import com.root.employeeservice.strategy.EqualDatesValidator;

import java.util.Date;

public class DateValidator {
    private EqualDatesValidator equalDatesValidator;

    public DateValidator(EqualDatesValidator equalDatesValidator) {
        this.equalDatesValidator = equalDatesValidator;
    }

    public void checkIfDatesAreEquals(Date updatedDate, Date originalDate) {
        this.equalDatesValidator.validateOriginalAndNewDate(updatedDate, originalDate);
    }
}
