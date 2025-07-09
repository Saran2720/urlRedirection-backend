package com.url_redirection.backend.repository;

import com.url_redirection.backend.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link,String> {

    Optional<Link> findByOriginalUrl(String originalUrl);
}
