package exception;

import constant.Constant;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiError extends RuntimeException {
    private final List<String> errors;
    private final String reason;
    private final String status;
    private final String timestamp = LocalDateTime.now().format(Constant.getFORMATTER());

    public ApiError(String message, List<String> errors, String reason, String status) {
        super(message);
        this.errors = errors;
        this.reason = reason;
        this.status = status;
    }
}
