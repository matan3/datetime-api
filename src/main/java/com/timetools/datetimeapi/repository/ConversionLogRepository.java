package com.timetools.datetimeapi.repository;

import com.timetools.datetimeapi.model.ConversionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionLogRepository extends JpaRepository<ConversionLog, Long> {
}
