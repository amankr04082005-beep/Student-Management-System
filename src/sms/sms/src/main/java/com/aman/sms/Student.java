package com.aman.sms;
import jakarta.validation.constraints.*;
public record Student(
        Integer id,
        @NotBlank String name,
        @NotBlank @Email String email,
        @Pattern(regexp = "\\d{10}", message = "phone 10 digit ka hona chahiye") String phone,
        @NotNull Integer courseId,
        String courseName,
        @NotNull @DecimalMin("0") @DecimalMax("100") Double marks) {
}