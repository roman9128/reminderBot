package rt.reminder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import rt.reminder.bot.Bot;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ReminderApplication {

    public static void main(String[] args) {

        SpringApplication.run(ReminderApplication.class, args);
    }

}
