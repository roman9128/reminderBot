package rt.reminder.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import rt.reminder.bot.utils.MessageTemplates;
import rt.reminder.bot.users.UserService;
import rt.reminder.bot.users.UserStatus;
import rt.reminder.model.Reminder;
import rt.reminder.repo.ReminderRepo;

@Component
public class CommandExecutor {

    @Autowired
    private MessageTemplates messageTemplates;
    @Autowired
    private UserService userService;
    @Autowired
    private ReminderRepo repo;

    public SendMessage execute(String commandText, Long userID) {
        BotCommand botCommand = BotCommand.fromCommandText(commandText);
        switch (botCommand) {
            case START -> {
                return start(userID);
            }
            case REMIND -> {
                return remind(userID);
            }
            case ALL -> {
                return all(userID);
            }
            case NEXT -> {
                return next(userID);
            }
            default -> {
                return null;
            }
        }
    }


    private SendMessage start(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(messageTemplates.getGreetings())
                .build();
    }

    private SendMessage remind(Long userID) {
        userService.setNewStatus(userID, UserStatus.WRITING_REMINDER);
        return SendMessage.builder()
                .chatId(userID)
                .text(messageTemplates.getRemind())
                .build();
    }

    private SendMessage all(Long userID) {
        StringBuilder builder = new StringBuilder();
        for (Reminder reminder : repo.findAllByUserID(userID)) {
            builder.append(reminder);
            builder.append(System.lineSeparator());
        }
        return SendMessage.builder()
                .chatId(userID)
                .text(builder.toString())
                .build();
    }

    private SendMessage next(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(repo.findFirstByUserIDOrderByNextRemindTimeAsc(userID).toString())
                .build();
    }
}
