package rt.reminder.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import rt.reminder.bot.commands.CommandExecutor;
import rt.reminder.bot.keyboards.InlineKeyboardService;
import rt.reminder.bot.users.UserService;
import rt.reminder.bot.users.UserStatus;
import rt.reminder.bot.utils.MessageTemplates;
import rt.reminder.bot.utils.Parser;
import rt.reminder.model.Reminder;
import rt.reminder.repo.ReminderRepo;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UpdateHandler {

    @Autowired
    private ReminderRepo repo;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageTemplates messageTemplates;
    @Autowired
    private InlineKeyboardService keyboardService;
    @Autowired
    private Parser parser;
    @Autowired
    private CommandExecutor executor;
    private final ConcurrentHashMap<Long, Reminder> tempReminders = new ConcurrentHashMap<>();

    public SendMessage prepareAnswer(Update update) {
        if (update.hasMessage()) {
            return processMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            return processCallBack(update.getCallbackQuery());
        }
        return null;
    }

    private SendMessage processCallBack(CallbackQuery callbackQuery) {
        Long userID = callbackQuery.getFrom().getId();
        String callBack = callbackQuery.getData();
        if (userService.getUserStatus(userID).equals(UserStatus.CONFIRMING)) {
            if (callBack.equals("APPROVED")) {
                repo.save(tempReminders.get(userID));
                tempReminders.remove(userID);
                userService.setNewStatus(userID, UserStatus.VISITOR);
                return SendMessage.builder()
                        .chatId(userID)
                        .text(messageTemplates.getSaved())
                        .build();
            } else if (callBack.equals("REFUSED")) {
                userService.setNewStatus(userID, UserStatus.VISITOR);
                return SendMessage.builder()
                        .chatId(userID)
                        .text(messageTemplates.getCancelled())
                        .build();
            } else {
                return SendMessage.builder()
                        .chatId(userID)
                        .text(messageTemplates.getConfirmation(tempReminders.get(userID)))
                        .replyMarkup(keyboardService.getKeyboard())
                        .build();
            }
        }
        return null;
    }

    private SendMessage processMessage(Message message) {
        Long userID = message.getFrom().getId();
        String msgText = message.getText();
        if (!userService.userIsPresent(userID)) {
            userService.setNewStatus(userID, UserStatus.VISITOR);
        }
        UserStatus userStatus = userService.getUserStatus(userID);
        switch (userStatus) {
            case VISITOR -> {
                if (message.isCommand()) {
                    return executor.execute(msgText, userID);
                } else {
                    return messageTemplates.sendDescription(userID);
                }
            }
            case WRITING_REMINDER -> {
                tempReminders.put(userID, new Reminder(userID, msgText));
                userService.setNewStatus(userID, UserStatus.WRITING_DEADLINE);
                return messageTemplates.sendDeadlineDesc(userID);
            }
            case WRITING_DEADLINE -> {
                LocalDateTime deadline = parser.parseDeadline(msgText);
                tempReminders.get(userID).setDeadline(deadline);
                userService.setNewStatus(userID, UserStatus.WRITING_PERIOD);
                return SendMessage.builder()
                        .chatId(userID)
                        .text(messageTemplates.getRemindTime())
                        .build();
            }
            case WRITING_PERIOD -> {
                Integer remindTimes = parser.parseInteger(msgText);
                tempReminders.get(userID).setParametersOnRemindTimes(remindTimes);
                userService.setNewStatus(userID, UserStatus.CONFIRMING);
                return SendMessage.builder()
                        .chatId(userID)
                        .text(messageTemplates.getConfirmation(tempReminders.get(userID)))
                        .replyMarkup(keyboardService.getKeyboard())
                        .build();
            }
            default -> {
                return null;
            }
        }
    }
}