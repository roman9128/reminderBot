package rt.reminder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

@Data
@NoArgsConstructor
@Entity
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long reminderID;
    Long userID;
    String reminderText;
    LocalDateTime startTime;
    LocalDateTime nextRemindTime;
    LocalDateTime deadline;
    Integer remindTimes;
    Long periodBetweenReminders;
    Boolean finished;

    public Reminder(Long userID, String reminderText) {
        this.userID = userID;
        this.reminderText = reminderText;
        this.startTime = LocalDateTime.now();
//        this.nextRemindTime = LocalDateTime.ofInstant(Instant.ofEpochSecond((startTime.toEpochSecond(ZoneOffset.UTC) + deadline.toEpochSecond(ZoneOffset.UTC)) / 2), ZoneId.systemDefault());
        this.finished = false;
    }

    public void setParametersOnRemindTimes(Integer remindTimes) {
        this.remindTimes = remindTimes;
        this.periodBetweenReminders = (deadline.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) / (remindTimes + 1);
        this.nextRemindTime = startTime.plusSeconds(periodBetweenReminders);
    }

    public String getStringDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
        return dateTime.format(formatter);
    }

    @Override
    public String toString() {
        return "Напоминание:" +
                System.lineSeparator() +
                reminderText +
                System.lineSeparator() +
                "время начала: " + getStringDateTime(startTime) +
                System.lineSeparator() +
                "нужно успеть до: " + getStringDateTime(deadline) +
                System.lineSeparator() +
                "следующее напоминание: " + getStringDateTime(nextRemindTime) +
                System.lineSeparator() +
                "осталось напоминаний: " + remindTimes +
                System.lineSeparator();
    }
}