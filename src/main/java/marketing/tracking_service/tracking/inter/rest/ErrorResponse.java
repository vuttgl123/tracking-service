package marketing.tracking_service.tracking.inter.rest;

import java.time.Instant;
import java.util.Map;

record ErrorResponse(int status, String error, String message, Instant timestamp, Map<String, String> details) {
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    static class ErrorResponseBuilder {
        private int status;
        private String error;
        private String message;
        private Instant timestamp;
        private Map<String, String> details;

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponseBuilder details(Map<String, String> details) {
            this.details = details;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(status, error, message, timestamp, details);
        }
    }
}
