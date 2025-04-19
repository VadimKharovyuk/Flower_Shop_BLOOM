package com.example.flowershoptr.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}