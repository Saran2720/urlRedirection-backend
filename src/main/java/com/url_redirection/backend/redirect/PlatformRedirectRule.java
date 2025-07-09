package com.url_redirection.backend.redirect;


import lombok.Data;

@Data
public class PlatformRedirectRule {
    private String match;
    private String param;
    private String android;
    private String ios;
}
