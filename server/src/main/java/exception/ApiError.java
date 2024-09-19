package exception;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class ApiError extends RuntimeException {
    private final List<String> errors;
    private final String reason;
    private final String status;
    private final String timestamp;

    public ApiError(String message, List<String> errors, String reason, String status, String timestamp) {
        super(message);
        this.errors = errors;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}
