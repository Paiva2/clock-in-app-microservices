package com.root.employeeservice.services;

import com.root.crossdbservice.entities.TimeRecord;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.repositories.TimeRecordRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.employeeservice.exceptions.BadRequestException;
import com.root.employeeservice.exceptions.ConflictException;
import com.root.employeeservice.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TimeRecordService {
    private final TimeRecordRepository timeRecordRepository;
    private final UserRepository userRepository;

    public TimeRecordService(TimeRecordRepository timeRecordRepository, UserRepository userRepository) {
        this.timeRecordRepository = timeRecordRepository;
        this.userRepository = userRepository;
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

        Set<TimeRecord> timeRecordsOfDay = this.timeRecordRepository.findUserRecordsOfDay(
                employee.getId()
        );

        if (timeRecordsOfDay.size() >= 4) {
            throw new ConflictException("Employee already recorded four times today");
        }

        TimeRecord registerRecord = this.timeRecordRepository.save(record);

        return registerRecord;
    }
}
