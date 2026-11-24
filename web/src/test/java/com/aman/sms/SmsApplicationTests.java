package com.aman.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.aman.sms.model.StudentRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Simple unit tests for the input validation rules (no database needed).
 */
class SmsApplicationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validStudentHasNoErrors() {
        StudentRequest request = new StudentRequest("Aman Kumar", "aman@example.com", "9000000001", 1, 88.0);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void phoneIsOptional() {
        StudentRequest request = new StudentRequest("Aman Kumar", "aman@example.com", null, 1, 88.0);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void invalidNameEmailPhoneAndMarksAreRejected() {
        StudentRequest request = new StudentRequest("Aman123", "abc", "12345", 1, 150.0);
        Set<ConstraintViolation<StudentRequest>> errors = validator.validate(request);
        assertEquals(4, errors.size());
    }
}
