package com.aman.sms.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data received from the browser when adding or updating a student.
 * The annotations validate the input (same rules as the console version).
 */
public record StudentRequest(

        @NotBlank(message = "Name is required")
        @Pattern(regexp = "[A-Za-z ]{2,50}",
                message = "Name must have only letters and spaces (2-50 characters)")
        String name,

        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$",
                message = "Invalid email format")
        String email,

        @Pattern(regexp = "[6-9][0-9]{9}",
                message = "Phone must be 10 digits starting with 6-9")
        String phone,

        @NotNull(message = "Course is required")
        Integer courseId,

        @NotNull(message = "Marks are required")
        @DecimalMin(value = "0", message = "Marks must be between 0 and 100")
        @DecimalMax(value = "100", message = "Marks must be between 0 and 100")
        Double marks) {
}
