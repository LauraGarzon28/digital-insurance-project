package com.auth_service.web;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) { }