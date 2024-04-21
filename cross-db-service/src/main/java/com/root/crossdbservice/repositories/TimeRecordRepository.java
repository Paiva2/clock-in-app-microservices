package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecord, UUID>, JpaSpecificationExecutor<TimeRecord> {
    @Query("SELECT tc FROM TimeRecord tc " +
            "INNER JOIN FETCH tc.user usr " +
            "WHERE usr.id = :employeeId AND FUNCTION('DATE', tc.createdAt) = CURRENT_DATE"
    )
    Set<TimeRecord> findUserRecordsOfDay(@Param("employeeId") UUID employeeId);


    @Query("SELECT tc FROM TimeRecord tc " +
            "INNER JOIN FETCH tc.user usr " +
            "WHERE usr.id = :employeeId " +
            "AND day(tc.createdAt) = :day " +
            "AND month(tc.createdAt) = :month " +
            "AND year(tc.createdAt) = :year"
    )
    Set<TimeRecord> findAllByEmployeeAndDay(
            @Param("employeeId") UUID employeeId,
            @Param("day") int day,
            @Param("month") int month,
            @Param("year") int year
    );
    
    @Query("SELECT tc FROM TimeRecord tc " +
            "INNER JOIN FETCH tc.user usr " +
            "WHERE usr.id = :employeeId AND tc.id = :timeRecordId"
    )
    Optional<TimeRecord> findByIdAndEmployee(
            @Param("timeRecordId") UUID timeRecordId,
            @Param("employeeId") UUID employeeId
    );
}
