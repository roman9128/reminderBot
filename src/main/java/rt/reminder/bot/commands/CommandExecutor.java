package rt.reminder.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import rt.reminder.bot.AutoSenderQueue;
import rt.reminder.bot.utils.MessageTemplates;
import rt.reminder.bot.users.UserService;
import rt.reminder.bot.users.UserStatus;
import rt.reminder.model.Reminder;
import rt.reminder.repo.ReminderRepo;

import java.util.List;
import java.util.Optional;

@Component
public class CommandExecutor {

    @Autowired
    private MessageTemplates messageTemplates;
    @Autowired
    private UserService userService;
    @Autowired
    private ReminderRepo repo;
    @Autowired
    private AutoSenderQueue autoSenderQueue;

    public void execute(String commandText, Long userID) throws InterruptedException {
        BotCommand botCommand = BotCommand.fromCommandText(commandText);
        switch (botCommand) {
            case START -> {
                start(userID);
            }
            case REMIND -> {
                remind(userID);
            }
            case ALL -> {
                all(userID);
            }
            case NEXT -> {
                next(userID);
            }
            default -> {

            }
        }
    }

    private void start(Long userID) throws InterruptedException {
        autoSenderQueue.put(messageTemplates.sendGreetings(userID));
    }

    private void remind(Long userID) throws InterruptedException {
        userService.setNewStatus(userID, UserStatus.WRITING_REMINDER);
        autoSenderQueue.put(messageTemplates.sendRemind(userID));
    }

    private void all(Long userID) throws InterruptedException {
        userService.setNewStatus(userID, UserStatus.VISITOR);
        List<Reminder> allUsersReminders = repo.findAllByUserID(userID);
        for (Reminder reminder : allUsersReminders) {
            autoSenderQueue.put(messageTemplates.sendNext(userID, reminder));
        }
        if (allUsersReminders.isEmpty()) {
            autoSenderQueue.put(messageTemplates.sendEmptyList(userID));
        }
    }

    private void next(Long userID) throws InterruptedException {
        userService.setNewStatus(userID, UserStatus.VISITOR);
        Optional<Reminder> reminderOptional = Optional.ofNullable(repo.findFirstByUserIDOrderByNextRemindTimeAsc(userID));
        if (reminderOptional.isPresent()) {
            Reminder nextReminder = reminderOptional.get();
            autoSenderQueue.put(messageTemplates.sendNext(userID, nextReminder));
        } else {
            autoSenderQueue.put(messageTemplates.sendEmptyList(userID));
        }
    }
}
