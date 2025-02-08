package rt.reminder.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class AutoSenderQueue {
    private final LinkedBlockingQueue<SendMessage> MESSAGE_QUEUE = new LinkedBlockingQueue<>();

    public void put(SendMessage message) throws InterruptedException {
        if (message != null) {
            MESSAGE_QUEUE.put(message);
        }
    }

    public SendMessage take() throws InterruptedException {
        return MESSAGE_QUEUE.take();
    }

//    public boolean isEmpty() {
//        return MESSAGE_QUEUE.isEmpty();
//    }
}
