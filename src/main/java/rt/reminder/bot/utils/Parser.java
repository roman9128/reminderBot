package rt.reminder.bot.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Parser {
    public Integer parseInteger(String msgText) {
        Integer result;
        try {
            result = Integer.parseInt(msgText);
        } catch (NumberFormatException e) {
            result = 0;
        }
        if (result < 0) {
            result = 0;
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

//    private LocalDateTime parseDate(String msgText) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//        return LocalDateTime.parse(msgText, formatter);
//    }
}
