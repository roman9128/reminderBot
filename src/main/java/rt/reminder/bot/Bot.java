package rt.reminder.bot;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String userName;
    @Autowired
    private UpdateHandler updateHandler;
    @Autowired
    private AutoSenderQueue autoSenderQueue;
    @Autowired
    private Notificator notificator;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
        initializeNotificatorTask();
        initializeAutoSenderThread();
    }

    private void initializeNotificatorTask() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (notificator.isTimeToNotify()) {
                    autoSenderQueue.put(notificator.getNotification());
                }
            } catch (Exception e) {
                System.out.println("Exception in notificator task: " + e.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void initializeAutoSenderThread() {
        executor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    SendMessage sm = autoSenderQueue.take();
                    sendMsg(sm);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Auto sender thread interrupted");
            } catch (Exception e) {
                System.out.println("Exception in auto sender thread: " + e.getMessage());
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        executor.submit(() -> {
            try {
                updateHandler.prepareAnswer(update);
                notificator.checkRepoForUpdates();
            } catch (Exception e) {
                System.err.println("Exception in onUpdateReceived thread: " + e.getMessage());
            }
        });
    }

    private void sendMsg(SendMessage sm) throws InterruptedException {
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            System.out.println(LocalDateTime.now() + ": " + e.getMessage());
            Thread.sleep(1000);
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
