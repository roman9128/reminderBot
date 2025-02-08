package rt.reminder.bot;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import rt.reminder.bot.utils.MessageTemplates;
import rt.reminder.model.Reminder;
import rt.reminder.repo.ReminderRepo;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@NoArgsConstructor
public class Notificator {
    private Reminder reminder;
    @Autowired
    private ReminderRepo repo;
    @Autowired
    private MessageTemplates messageTemplates;

    public void checkRepoForUpdates() {
        reminder = repo.findFirstByFinishedFalseOrderByNextRemindTimeAsc();
    }

    public boolean isTimeToNotify() {
        if (reminder != null) {
            return !reminder.getNextRemindTime().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public SendMessage getNotification() {
        Reminder updatedReminder = reminder;
        updatedReminder.updateReminder();
        repo.save(updatedReminder);
        checkRepoForUpdates();
        return messageTemplates.sendNotification(updatedReminder.getUserID(), updatedReminder);
    }
}