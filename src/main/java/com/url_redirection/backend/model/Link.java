package com.url_redirection.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Link {
    private String originalUrl;
    @Id
    private String shortCode;
}
