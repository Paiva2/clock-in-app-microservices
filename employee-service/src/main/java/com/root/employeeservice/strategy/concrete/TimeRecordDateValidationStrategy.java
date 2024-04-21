package com.root.employeeservice.strategy.concrete;

import com.root.employeeservice.exceptions.ConflictException;
import com.root.employeeservice.strategy.EqualDatesValidator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TimeRecordDateValidationStrategy implements EqualDatesValidator {
    @Override
    public void validateOriginalAndNewDate(Date newDate, Date originalDate) {
        boolean isNewDayEqualFromOriginalDay =
                this.getDayOfTimeRecord(newDate) == this.getDayOfTimeRecord(originalDate);

        boolean isNewMonthEqualFromOriginalMonth =
                this.getMonthOfTimeRecord(newDate) == this.getMonthOfTimeRecord(originalDate);

        boolean isNewYearEqualFromOriginalYear =
                this.getYearOfTimeRecord(newDate) == this.getYearOfTimeRecord(originalDate);

        if (!isNewDayEqualFromOriginalDay) {
            throw new ConflictException("Updated date must be on the same day of time record creation");
        }

        if (!isNewMonthEqualFromOriginalMonth) {
            throw new ConflictException("Updated date must be on the same month of time record creation");
        }

        if (!isNewYearEqualFromOriginalYear) {
            throw new ConflictException("Updated date must be on the same year of time record creation");
        }
    }

    private int getDayOfTimeRecord(Date timeRecordDate) {
        return this.convertToLocalDate(timeRecordDate).getDayOfMonth();
    }

    private int getMonthOfTimeRecord(Date timeRecordDate) {
        return this.convertToLocalDate(timeRecordDate).getMonthValue();
    }

    private int getYearOfTimeRecord(Date timeRecordDate) {
        return this.convertToLocalDate(timeRecordDate).getYear();
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
