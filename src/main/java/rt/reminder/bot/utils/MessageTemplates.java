package rt.reminder.bot.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import rt.reminder.bot.keyboards.InlineKeyboardsService;
import rt.reminder.model.Reminder;

@Getter
@Component
public class MessageTemplates {

    private final String greetings = "Привет, я - Напоминатель!\nЯ предназначен для того, чтобы создавать длящиеся напоминания.\nДля работы с ботом выберите одну из команд";
    private final String description = "Для работы с ботом выберите одну из команд";
    private final String remind = "Сейчас создадим напоминание.\nЭтот процесс для удобства разбит на этапы: написать текст напоминания, выбрать срок, когда указанное Вами должно быть исполнено, определить количество напоминаний.\nНачнём!\nО чём Вам напоминать?";
    private final String deadline = "Теперь укажите срок в формате ДД.ММ.ГГГГ ЧЧ:ММ (время при необходимости).\nЭтот шаг можно пропустить, тогда напоминание будет установлено на 1 год";
    private final String remindTime = "Сколько раз нужно напомнить до наступления указанного срока?\nЭтот шаг можно пропустить, тогда будет установлено 1 напоминание в конце срока";
    private final String saved = "Напоминание создано";
    private final String cancelled = "Что же, начнём заново";
    private final String removed = "Удалено успешно";
    private final String emptyList = "Список напоминаний пуст\nСоздадим одно?";

    public SendMessage sendGreetings(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getGreetings())
                .build();
    }

    public SendMessage sendDescription(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getDescription())
                .build();
    }

    public SendMessage sendRemind(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getRemind())
                .build();
    }

    public SendMessage sendDeadline(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getDeadline())
                .replyMarkup(InlineKeyboardsService.getKeyboardSkip())
                .build();
    }

    public SendMessage sendRemindTime(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getRemindTime())
                .replyMarkup(InlineKeyboardsService.getKeyboardSkip())
                .build();
    }

    public SendMessage sendConfirmation(Long userID, Reminder reminder) {
        return SendMessage.builder()
                .chatId(userID)
                .text(prepareConfirmationText(reminder))
                .replyMarkup(InlineKeyboardsService.getKeyboardConfirmation())
                .build();
    }

    private String prepareConfirmationText(Reminder reminder) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Прежде чем я всё сохраню, предлагаю перепроверить, что получилось.")
                .append(System.lineSeparator().repeat(2))
                .append("Вы хотите, чтобы я напоминал о:")
                .append(System.lineSeparator())
                .append(reminder.getReminderText())
                .append(System.lineSeparator().repeat(2))
                .append("вплоть до:")
                .append(System.lineSeparator())
                .append(reminder.getStringDateTime(reminder.getDeadline()))
                .append(System.lineSeparator().repeat(2))
                .append("количество напоминаний: ")
                .append(reminder.getRemindTimes())
                .append(System.lineSeparator().repeat(2))
                .append("следующее напоминание")
                .append(System.lineSeparator())
                .append(reminder.getStringDateTime(reminder.getNextRemindTime()));
        return builder.toString();
    }

    public SendMessage sendSaved(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getSaved())
                .build();
    }

    public SendMessage sendCancelled(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getCancelled())
                .build();
    }

    public SendMessage sendNext(Long userID, Reminder reminder) {
        String reminderIDString = reminder.getReminderID().toString();
        return SendMessage.builder()
                .chatId(userID)
                .text(reminder.toString())
                .replyMarkup(InlineKeyboardsService.getKeyboardRemoval(reminderIDString))
                .build();
    }

    public SendMessage sendNotification(Long userID, Reminder reminder) {
        return SendMessage.builder()
                .chatId(userID)
                .text(reminder.toString())
                .build();
    }

    public SendMessage sendRemoved(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getRemoved())
                .build();
    }

    public SendMessage sendEmptyList(Long userID) {
        return SendMessage.builder()
                .chatId(userID)
                .text(getEmptyList())
                .build();
    }
}
