package rt.reminder.bot.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Parser {
    public Integer parseIntegerOrReturnOne(String msgText) {
        Integer result;
        try {
            result = Integer.parseInt(msgText);
        } catch (NumberFormatException e) {
            result = 1;
        }
        if (result < 1) {
            result = 1;
        }
        return result;
    }

    public Long parseLongOrReturnMinusOne(String text) {
        Long result;
        try {
            result = Long.parseLong(text);
        } catch (NumberFormatException e) {
            result = -1L;
        }
        return result;
    }

    public LocalDateTime parseDeadline(String msgText) {
        LocalDateTime result;
        try {
            if (msgText.contains(" ")) {
                result = parseDateTime(msgText);
            } else {
                result = parseDateTime(msgText + " 00:00");
            }
        } catch (Exception e) {
            return LocalDateTime.now().plusYears(1);
        }
        if (result.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now().plusYears(1);
        }
        return result;
    }

    private LocalDateTime parseDateTime(String msgText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return LocalDateTime.parse(msgText, formatter);
    }
}
