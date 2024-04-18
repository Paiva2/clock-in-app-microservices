package com.root.crossdbservice.repositories;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.crossdbservice.entities.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingTimeRecordActionRepository extends JpaRepository<PendingTimeRecordAction, UUID> {
    Optional<PendingTimeRecordAction> findByTimeRecord(TimeRecord timeRecord);
}
