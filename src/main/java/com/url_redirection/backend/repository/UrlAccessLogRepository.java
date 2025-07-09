package com.url_redirection.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.url_redirection.backend.model.UrlAccessLog;

@Repository
public interface UrlAccessLogRepository extends JpaRepository<UrlAccessLog,Long> {
}
