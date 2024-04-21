package com.root.employeeservice.strategy;

import java.util.Date;

public interface EqualDatesValidator {
    void validateOriginalAndNewDate(Date newDate, Date originalDate);
}
