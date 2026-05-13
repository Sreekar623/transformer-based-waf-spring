package com.iomp.WAF.Repository;

import com.iomp.WAF.Entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
}
