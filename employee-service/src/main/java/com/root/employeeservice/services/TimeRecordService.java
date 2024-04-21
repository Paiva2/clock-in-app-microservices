package com.root.employeeservice.services;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.crossdbservice.entities.TimeRecord;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.repositories.PendingTimeRecordActionRepository;
import com.root.crossdbservice.repositories.TimeRecordRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.employeeservice.exceptions.BadRequestException;
import com.root.employeeservice.exceptions.ConflictException;
import com.root.employeeservice.exceptions.ForbiddenException;
import com.root.employeeservice.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TimeRecordService {
    private final TimeRecordRepository timeRecordRepository;
    private final UserRepository userRepository;
    private final PendingTimeRecordActionRepository pendingTimeRecordActionRepository;

    private static final Integer APP_LAUNCH_DATE = 2024;

    public TimeRecordService(
            TimeRecordRepository timeRecordRepository,
            UserRepository userRepository,
            PendingTimeRecordActionRepository pendingTimeRecordActionRepository
    ) {
        this.timeRecordRepository = timeRecordRepository;
        this.userRepository = userRepository;
        this.pendingTimeRecordActionRepository = pendingTimeRecordActionRepository;
    }

    public TimeRecord insertRegister(UserEntity employee) {
        if (employee == null) {
            throw new BadRequestException("Employee can't be empty");
        }

        if (employee.getId() == null) {
            throw new BadRequestException("Employee id can't be empty");
        }

        UserEntity getEmployee = this.userRepository.findById(employee.getId())
                .orElseThrow(() -> new BadRequestException("Employee not found"));

        if (getEmployee.getDisabled()) {
            throw new ForbiddenException("Employee is disabled");
        }

        TimeRecord record = new TimeRecord();
        record.setUser(getEmployee);
        record.setRecordHour(new Date());

        Set<TimeRecord> timeRecordsOfDay = this.timeRecordRepository.findUserRecordsOfDay(
                employee.getId()
        );

        if (timeRecordsOfDay.size() >= 4) {
            throw new ConflictException("Employee already recorded four times today");
        }

        TimeRecord registerRecord = this.timeRecordRepository.save(record);

        return registerRecord;
    }

    public PendingTimeRecordAction requestTimeRecordUpdate(UUID requesterId, TimeRecord timeRecordToUpdate) {
        if (requesterId == null) {
            throw new BadRequestException("requesterId can't be empty");
        }

        if (timeRecordToUpdate == null) {
            throw new BadRequestException("Time record can't be empty");
        }

        TimeRecord getTimeRecord = this.timeRecordRepository.findByIdAndEmployee(
                timeRecordToUpdate.getId(),
                requesterId
        ).orElseThrow(() -> new NotFoundException("Time Record not found"));

        if (getTimeRecord.getDisabled()) {
            throw new ConflictException("Time record is disabled");
        }

        Optional<PendingTimeRecordAction> timeRecordAlreadyHasPendingAction =
                this.pendingTimeRecordActionRepository.findByTimeRecord(getTimeRecord);

        if (timeRecordAlreadyHasPendingAction.isPresent()) {
            throw new ConflictException("Time record already has a pending action");
        }

        boolean isNewDayEqualFromOriginalDay =
                this.getDayOfTimeRecord(timeRecordToUpdate) == this.getDayOfTimeRecord(getTimeRecord);

        boolean isNewMonthEqualFromOriginalMonth =
                this.getMonthOfTimeRecord(timeRecordToUpdate) == this.getMonthOfTimeRecord(getTimeRecord);

        boolean isNewYearEqualFromOriginalYear =
                this.getYearOfTimeRecord(timeRecordToUpdate) == this.getYearOfTimeRecord(getTimeRecord);

        if (!isNewDayEqualFromOriginalDay) {
            throw new ConflictException("Updated date must be on the same day of time record creation");
        }

        if (!isNewMonthEqualFromOriginalMonth) {
            throw new ConflictException("Updated date must be on the same month of time record creation");
        }

        if (!isNewYearEqualFromOriginalYear) {
            throw new ConflictException("Updated date must be on the same year of time record creation");
        }

        PendingTimeRecordAction newAction = new PendingTimeRecordAction();
        newAction.setTimeRecord(getTimeRecord);
        newAction.setActionType(PendingTimeRecordAction.ActionType.UPDATE);
        newAction.setTimeUpdated(timeRecordToUpdate.getRecordHour());

        return this.pendingTimeRecordActionRepository.save(newAction);
    }

    public PendingTimeRecordAction requestTimeRecordDelete(UUID requesterId, TimeRecord timeRecordToDelete) {
        if (requesterId == null) {
            throw new BadRequestException("requesterId can't be empty");
        }

        if (timeRecordToDelete == null) {
            throw new BadRequestException("Time record can't be empty");
        }

        TimeRecord getTimeRecord = this.timeRecordRepository.findByIdAndEmployee(
                timeRecordToDelete.getId(),
                requesterId
        ).orElseThrow(() -> new NotFoundException("Time Record not found"));

        if (getTimeRecord.getDisabled()) {
            throw new ConflictException("Time record is disabled");
        }

        Optional<PendingTimeRecordAction> timeRecordAlreadyHasPendingAction =
                this.pendingTimeRecordActionRepository.findByTimeRecord(getTimeRecord);

        if (timeRecordAlreadyHasPendingAction.isPresent()) {
            throw new ConflictException("Time record already has a pending action");
        }

        PendingTimeRecordAction newAction = new PendingTimeRecordAction();
        newAction.setTimeRecord(getTimeRecord);
        newAction.setActionType(PendingTimeRecordAction.ActionType.DELETE);
        newAction.setTimeUpdated(null);

        return this.pendingTimeRecordActionRepository.save(newAction);
    }

    public Set<TimeRecord> listEmployeeTimeRecords(
            UUID employeeId,
            int month,
            int year
    ) {
        if (employeeId == null) {
            throw new BadRequestException("Employee id can't be empty");
        }

        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must start at 1 and end at 12");
        }

        if (year < APP_LAUNCH_DATE) {
            year = APP_LAUNCH_DATE;
        }

        ZonedDateTime dateProvided = LocalDate.of(year, month, 1).atStartOfDay(ZoneId.of("UTC"));

        UserEntity getEmployee = this.userRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        
        Set<TimeRecord> listRecords = this.timeRecordRepository.findAllByEmployeeAndDate(
                getEmployee.getId(),
                dateProvided.toLocalDate().getMonthValue(),
                dateProvided.toLocalDate().getYear()
        );

        return listRecords;
    }

    private int getDayOfTimeRecord(TimeRecord timeRecord) {
        return this.convertToLocalDate(timeRecord.getRecordHour()).getDayOfMonth();
    }

    private int getMonthOfTimeRecord(TimeRecord timeRecord) {
        return this.convertToLocalDate(timeRecord.getRecordHour()).getMonthValue();
    }

    private int getYearOfTimeRecord(TimeRecord timeRecord) {
        return this.convertToLocalDate(timeRecord.getRecordHour()).getYear();
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
