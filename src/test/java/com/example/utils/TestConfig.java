package com.example.utils;

public final class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://dq2embcxfli7y.cloudfront.net/";
    private static final String DEFAULT_TEST_EMAIL = "viadmin@gmail.com";
    private static final String DEFAULT_TEST_PASSWORD = "Virtualintros@30026";

    private TestConfig() {
    }

    public static String baseUrl() {
        String url = System.getenv("BASE_URL");
        if (url == null || url.isBlank()) {
            return DEFAULT_BASE_URL;
        }
        return url.endsWith("/") ? url : url + "/";
    }

    public static String testEmail() {
        String email = System.getenv("TEST_EMAIL");
        if (email == null || email.isBlank()) {
            return DEFAULT_TEST_EMAIL;
        }
        return email;
    }

    public static String testPassword() {
        String password = System.getenv("TEST_PASSWORD");
        if (password == null || password.isBlank()) {
            return DEFAULT_TEST_PASSWORD;
        }
        return password;
    }
}
