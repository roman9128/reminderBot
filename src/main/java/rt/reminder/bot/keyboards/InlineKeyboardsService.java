package rt.reminder.bot.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardsService {

    public static InlineKeyboardMarkup getKeyboardConfirmation() {
        InlineKeyboardMarkup keyboardConfirmation = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtonsOnKeyboard = new ArrayList<>();
        InlineKeyboardButton approve = new InlineKeyboardButton("Всё верно");
        InlineKeyboardButton disapprove = new InlineKeyboardButton("Давай заново");
        approve.setCallbackData("APPROVED");
        disapprove.setCallbackData("REFUSED");
        allButtonsOnKeyboard.add(List.of(approve, disapprove));
        keyboardConfirmation.setKeyboard(allButtonsOnKeyboard);
        return keyboardConfirmation;
    }

    public static InlineKeyboardMarkup getKeyboardRemoval(String reminderIDString) {
        InlineKeyboardMarkup keyboardRemoval = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtonsOnKeyboard = new ArrayList<>();
        InlineKeyboardButton remove = new InlineKeyboardButton("Удалить");
        remove.setCallbackData(reminderIDString);
        allButtonsOnKeyboard.add(List.of(remove));
        keyboardRemoval.setKeyboard(allButtonsOnKeyboard);
        return keyboardRemoval;
    }
    public static InlineKeyboardMarkup getKeyboardSkip() {
        InlineKeyboardMarkup keyboardSkip = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtonsOnKeyboard = new ArrayList<>();
        InlineKeyboardButton skip = new InlineKeyboardButton("Пропустить");
        skip.setCallbackData("SKIP");
        allButtonsOnKeyboard.add(List.of(skip));
        keyboardSkip.setKeyboard(allButtonsOnKeyboard);
        return keyboardSkip;
    }
}