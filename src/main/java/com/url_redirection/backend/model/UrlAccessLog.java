package com.url_redirection.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Table(name = "url_access_log")
public class UrlAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String shortCode;
    private String userAgent;
    private String platform;
    private String ipAddress;
    private LocalDateTime accessedAt;

    public void setOriginalUrl(String originalUrl) {
    }
}