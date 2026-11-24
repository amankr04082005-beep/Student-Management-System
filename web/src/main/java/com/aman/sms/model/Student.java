package com.aman.sms.model;

/**
 * Student data sent to the browser (response).
 * A record is a short way to write a class with private final fields and getters.
 */
public record Student(
        int id,
        String name,
        String email,
        String phone,
        int courseId,
        String courseName,
        double marks) {
}
