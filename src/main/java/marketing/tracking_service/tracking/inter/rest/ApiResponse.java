package marketing.tracking_service.tracking.inter.rest;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(HttpStatus status, T body) {}
