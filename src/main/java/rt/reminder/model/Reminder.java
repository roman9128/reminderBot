package rt.reminder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
    Boolean toBeDeleted;

    public Reminder(Long userID, String reminderText) {
        this.userID = userID;
        this.reminderText = reminderText;
        this.startTime = LocalDateTime.now();
//        this.nextRemindTime = LocalDateTime.ofInstant(Instant.ofEpochSecond((startTime.toEpochSecond(ZoneOffset.UTC) + deadline.toEpochSecond(ZoneOffset.UTC)) / 2), ZoneId.systemDefault());
        this.toBeDeleted = false;
    }

    public void setParametersOnRemindTimes(Integer remindTimes) {
        this.remindTimes = remindTimes;
        this.periodBetweenReminders = (deadline.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) / (remindTimes + 1);
        this.nextRemindTime = startTime.plusSeconds(periodBetweenReminders);
    }

    @Override
    public String toString() {
        return "Напоминание:" +
                System.lineSeparator() +
                reminderText +
                System.lineSeparator() +
                "время начала: " + startTime +
                System.lineSeparator() +
                "нужно успеть до: " + deadline +
                System.lineSeparator() +
                "следующее напоминание: " + nextRemindTime +
                System.lineSeparator() +
                "осталось напоминаний: " + remindTimes;
    }
}