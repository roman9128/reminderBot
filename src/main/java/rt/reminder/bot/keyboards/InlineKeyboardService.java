package rt.reminder.bot.keyboards;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class InlineKeyboardService {

    private final InlineKeyboardMarkup keyboard;

    public InlineKeyboardService() {
        keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtonsOnKeyboard = new ArrayList<>();
        InlineKeyboardButton approve = new InlineKeyboardButton("Всё верно");
        InlineKeyboardButton disapprove = new InlineKeyboardButton("Давай заново");
        approve.setCallbackData("APPROVED");
        disapprove.setCallbackData("REFUSED");
        allButtonsOnKeyboard.add(List.of(approve, disapprove));
        keyboard.setKeyboard(allButtonsOnKeyboard);
    }
}