package com.u012e.session_auth_db.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private boolean success = true;
    private String message;
    private T data;

    public static <T> GenericResponse<T> empty() {
        return success(null);
    }

    public static <T> GenericResponse<T> success() {
        return GenericResponse.<T>builder()
                .message("success")
                .data(null)
                .success(true)
                .build();
    }

    public static <T> GenericResponse<T> error() {
        return GenericResponse.<T>builder()
                .message("error")
                .data(null)
                .success(false)
                .build();
    }


    public static <T> GenericResponse<T> success(T data) {
        return GenericResponse.<T>builder()
                .message("success")
                .data(data)
                .success(true)
                .build();
    }

    public static <T> GenericResponse<T> error(T data) {
        return GenericResponse.<T>builder()
                .message("error")
                .data(data)
                .success(false)
                .build();
    }
}
