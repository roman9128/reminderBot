package rt.reminder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
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
        this.finished = false;
    }

    public void setParametersOnRemindTimes(Integer remindTimes) {
        this.remindTimes = remindTimes;
        this.periodBetweenReminders = (deadline.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) / remindTimes;
        this.nextRemindTime = startTime.plusSeconds(periodBetweenReminders);
    }

    public String getStringDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy г., HH:mm", Locale.getDefault());
        return dateTime.format(formatter);
    }

    public void updateReminder() {
        if (!deadline.isAfter(LocalDateTime.now())) {
            finished = true;
            remindTimes = 0;
        }
        if (!finished) {
            nextRemindTime = nextRemindTime.plusSeconds(periodBetweenReminders);
            remindTimes--;
        }
        if (nextRemindTime.isAfter(deadline)) {
            nextRemindTime = deadline;
        }
        if (remindTimes == 1) {
            nextRemindTime = deadline;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Напоминание:")
                .append(System.lineSeparator())
                .append(reminderText)
                .append(System.lineSeparator().repeat(2))
                .append("создано: ")
                .append(getStringDateTime(startTime))
                .append(System.lineSeparator());
        if (!finished) {
            builder.append("успеть до: ")
                    .append(getStringDateTime(deadline))
                    .append(System.lineSeparator())
                    .append("следующее: ")
                    .append(getStringDateTime(nextRemindTime))
                    .append(System.lineSeparator());
            if (remindTimes == 1) {
                builder.append("Это будет последнее напоминание");
                nextRemindTime = deadline;
            } else {
                builder.append("осталось напоминаний: ")
                        .append(remindTimes)
                        .append(System.lineSeparator());
            }
        } else {
            builder.append("окончено: ")
                    .append(getStringDateTime(deadline))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }
}