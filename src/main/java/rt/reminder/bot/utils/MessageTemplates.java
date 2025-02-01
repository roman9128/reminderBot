package rt.reminder.bot.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;
import rt.reminder.model.Reminder;

@Getter
@Component
public class MessageTemplates {

    private final String greetings = "Привет, я - Напоминатель!\nЯ предназначен для того, чтобы создавать длящиеся напоминания.\nДля работы с ботом выберите одну из команд";
    private final String description = "Для работы с ботом выберите одну из команд";
    private final String remindDesc = "Сейчас создадим напоминание.\nЭтот процесс для удобства разбит на этапы: написать текст напоминания, выбрать срок, когда указанное Вами должно быть исполнено, определить количество напоминаний.\nНачнём!\nО чём Вам напоминать?";
    private final String deadlineDesc = "Теперь укажите срок в формате ДД.ММ.ГГГГ ЧЧ:ММ (время при необходимости).\nЭтот шаг можно пропустить, тогда напоминание будет установлено на 1 год.\nЧтобы пропустить, отправьте что угодно, кроме даты :)";
    private final String remindTimeDesc = "Сколько раз нужно напомнить до наступления указанного срока?\nЭтот шаг можно пропустить, тогда будет установлено 1 напоминание в середине срока.\nЧтобы пропустить, отправьте что угодно, кроме числа";
    private final String saved = "Напоминание создано";
    private final String cancelled = "Что же, начнём заново";

    public String getConfirmation(Reminder reminder) {
        StringBuilder builder = new StringBuilder();
        builder.append("Прежде чем я всё сохраню, предлагаю перепроверить, что получилось.");
        builder.append(System.lineSeparator().repeat(2));
        builder.append("Вы хотите, чтобы я напоминал о");
        builder.append(System.lineSeparator().repeat(2));
        builder.append(reminder.getReminderText());
        builder.append(System.lineSeparator().repeat(2));
        builder.append("вплоть до");
        builder.append(System.lineSeparator().repeat(2));
        builder.append(reminder.getDeadline());
        builder.append(System.lineSeparator().repeat(2));
        builder.append("количество напоминаний до наступления указанного срока: ");
        builder.append(reminder.getRemindTimes());
        builder.append(System.lineSeparator().repeat(2));
        builder.append("Следующее напоминание");
        builder.append(System.lineSeparator().repeat(2));
        builder.append(reminder.getNextRemindTime());
        return builder.toString();
    }
}