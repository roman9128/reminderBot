package rt.reminder.bot;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private UpdateHandler updateHandler;
    @Value("${bot.username}")
    private String userName;

    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage reply = updateHandler.prepareAnswer(update);
        if (reply != null) {
            sendMsg(reply);
        }
    }

    private void sendMsg(SendMessage sm) {
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            System.out.println(LocalDateTime.now() + ": " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}
