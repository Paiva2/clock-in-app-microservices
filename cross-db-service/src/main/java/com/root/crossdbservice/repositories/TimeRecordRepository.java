package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecord, UUID> {

    @Query("SELECT tc FROM TimeRecord tc " +
            "INNER JOIN FETCH tc.user usr " +
            "WHERE usr.id = :employeeId AND FUNCTION('DATE', tc.createdAt) = CURRENT_DATE"
    )
    Set<TimeRecord> findUserRecordsOfDay(@Param("employeeId") UUID employeeId);
}
