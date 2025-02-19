package org.itprom.employees.exeption;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex,
																  HttpServletRequest request) {
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		ApiError apiError = new ApiError(
				status.value(),
				status.getReasonPhrase(),
				ex.getReason(),
				request.getRequestURI()
		);
		return ResponseEntity
				.status(status.value())
				.body(apiError);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex,
																  HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ApiError apiError = new ApiError(
				status.value(),
				status.getReasonPhrase(),
				extractErrorMessages(ex),
				request.getRequestURI()
		);
		return ResponseEntity
				.status(status.value())
				.body(apiError);
	}

	private String extractErrorMessages(ConstraintViolationException ex) {
		return ex.getConstraintViolations().stream()
				.map(cv -> "Field: " + cv.getPropertyPath() + ", Error: " + cv.getMessage())
				.collect(Collectors.joining("; "));
	}
}
