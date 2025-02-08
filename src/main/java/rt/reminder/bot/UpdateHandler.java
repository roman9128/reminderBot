package rt.reminder.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import rt.reminder.bot.commands.CommandExecutor;
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
    private Parser parser;
    @Autowired
    private CommandExecutor executor;
    @Autowired
    private AutoSenderQueue autoSenderQueue;
    private final ConcurrentHashMap<Long, Reminder> tempReminders = new ConcurrentHashMap<>();

//    public void prepareAnswer(Update update) throws InterruptedException {
//        if (update.hasMessage()) {
//            processMessage(update.getMessage());
//        } else if (update.hasCallbackQuery()) {
//            processCallBack(update.getCallbackQuery());
//        }
//    }

    private void processCallBack(CallbackQuery callbackQuery) throws InterruptedException {
        Long userID = callbackQuery.getFrom().getId();
        String callBack = callbackQuery.getData();
        UserStatus userStatus = userService.getUserStatus(userID);
        switch (userStatus) {
            case VISITOR -> {
                Long reminderID = parser.parseLongOrReturnMinusOne(callBack);
                if (reminderID != -1L) {
                    repo.deleteById(reminderID);
                    autoSenderQueue.put(messageTemplates.sendRemoved(userID));
                }
            }
            case WRITING_DEADLINE -> {
                if (callBack.equals("SKIP")) {
                    LocalDateTime deadline = parser.parseDeadline(callBack);
                    tempReminders.get(userID).setDeadline(deadline);
                    userService.setNewStatus(userID, UserStatus.WRITING_PERIOD);
                    autoSenderQueue.put(messageTemplates.sendRemindTime(userID));
                }
            }
            case WRITING_PERIOD -> {
                if (callBack.equals("SKIP")) {
                    Integer remindTimes = parser.parseIntegerOrReturnOne(callBack);
                    tempReminders.get(userID).setParametersOnRemindTimes(remindTimes);
                    userService.setNewStatus(userID, UserStatus.CONFIRMING);
                    autoSenderQueue.put(messageTemplates.sendConfirmation(userID, tempReminders.get(userID)));
                }
            }
            case CONFIRMING -> {
                if (callBack.equals("APPROVED")) {
                    repo.save(tempReminders.get(userID));
                    tempReminders.remove(userID);
                    userService.setNewStatus(userID, UserStatus.VISITOR);
                    autoSenderQueue.put(messageTemplates.sendSaved(userID));
                } else if (callBack.equals("REFUSED")) {
                    tempReminders.remove(userID);
                    userService.setNewStatus(userID, UserStatus.VISITOR);
                    autoSenderQueue.put(messageTemplates.sendCancelled(userID));
                }
            }
        }
    }

    private void processMessage(Message message) throws InterruptedException {
        Long userID = message.getFrom().getId();
        String msgText = message.getText();
        if (!userService.userIsPresent(userID)) {
            userService.setNewStatus(userID, UserStatus.VISITOR);
        }
        UserStatus userStatus = userService.getUserStatus(userID);
        if (message.isCommand()) {
            executor.execute(msgText, userID);
        } else {
            switch (userStatus) {
                case VISITOR -> autoSenderQueue.put(messageTemplates.sendDescription(userID));
                case WRITING_REMINDER -> {
                    tempReminders.put(userID, new Reminder(userID, msgText));
                    userService.setNewStatus(userID, UserStatus.WRITING_DEADLINE);
                    autoSenderQueue.put(messageTemplates.sendDeadline(userID));
                }
                case WRITING_DEADLINE -> {
                    LocalDateTime deadline = parser.parseDeadline(msgText);
                    tempReminders.get(userID).setDeadline(deadline);
                    userService.setNewStatus(userID, UserStatus.WRITING_PERIOD);
                    autoSenderQueue.put(messageTemplates.sendRemindTime(userID));
                }
                case WRITING_PERIOD -> {
                    Integer remindTimes = parser.parseIntegerOrReturnOne(msgText);
                    tempReminders.get(userID).setParametersOnRemindTimes(remindTimes);
                    userService.setNewStatus(userID, UserStatus.CONFIRMING);
                    autoSenderQueue.put(messageTemplates.sendConfirmation(userID, tempReminders.get(userID)));
                }
                default -> {
                }
            }
        }
    }


/////////////////////////////////////////////////////


    public void prepareAnswer(Update update) throws InterruptedException, NullPointerException {
        Long userID = null;
        String input = "";
        if (update.hasMessage()) {
            userID = update.getMessage().getFrom().getId();
            input = update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            userID = update.getCallbackQuery().getFrom().getId();
            input = update.getCallbackQuery().getData();
        }
        if (userID == null) {
            throw new NullPointerException("userID is null");
        }
        if (!userService.userIsPresent(userID)) {
            userService.setNewStatus(userID, UserStatus.VISITOR);
        }
        UserStatus userStatus = userService.getUserStatus(userID);
        if (isCommand(update)) {
            executor.execute(input, userID);
        } else {
            switch (userStatus) {
                case VISITOR -> {
                    if (update.hasMessage()) {
                        autoSenderQueue.put(messageTemplates.sendDescription(userID));
                    } else if (update.hasCallbackQuery()) {
                        Long reminderID = parser.parseLongOrReturnMinusOne(input);
                        if (reminderID != -1L) {
                            repo.deleteById(reminderID);
                            autoSenderQueue.put(messageTemplates.sendRemoved(userID));
                        }
                    }
                }
                case WRITING_REMINDER -> {
                    tempReminders.put(userID, new Reminder(userID, input));
                    userService.setNewStatus(userID, UserStatus.WRITING_DEADLINE);
                    autoSenderQueue.put(messageTemplates.sendDeadline(userID));
                }
                case WRITING_DEADLINE -> {
                    LocalDateTime deadline = parser.parseDeadline(input);
                    tempReminders.get(userID).setDeadline(deadline);
                    userService.setNewStatus(userID, UserStatus.WRITING_PERIOD);
                    autoSenderQueue.put(messageTemplates.sendRemindTime(userID));
                }
                case WRITING_PERIOD -> {
                    Integer remindTimes = parser.parseIntegerOrReturnOne(input);
                    tempReminders.get(userID).setParametersOnRemindTimes(remindTimes);
                    userService.setNewStatus(userID, UserStatus.CONFIRMING);
                    autoSenderQueue.put(messageTemplates.sendConfirmation(userID, tempReminders.get(userID)));

                }
                case CONFIRMING -> {
                    if (input.equals("APPROVED")) {
                        repo.save(tempReminders.get(userID));
                        tempReminders.remove(userID);
                        userService.setNewStatus(userID, UserStatus.VISITOR);
                        autoSenderQueue.put(messageTemplates.sendSaved(userID));
                    } else if (input.equals("REFUSED")) {
                        tempReminders.remove(userID);
                        userService.setNewStatus(userID, UserStatus.VISITOR);
                        autoSenderQueue.put(messageTemplates.sendCancelled(userID));
                    }
                }
                default -> {
                }
            }
        }
    }

    private boolean isCommand(Update update) {
        return update.hasMessage() && update.getMessage().isCommand();
    }
}