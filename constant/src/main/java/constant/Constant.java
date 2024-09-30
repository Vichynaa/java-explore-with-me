package constant;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

public class Constant {
    @Getter
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Getter
    private static final String EWM_MAIN_SERVICE = "ewm-main-service";
}
