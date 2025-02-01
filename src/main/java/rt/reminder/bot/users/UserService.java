package rt.reminder.bot.users;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Data
@Component
public class UserService {

    private final HashMap<Long, UserStatus> BOT_USERS;

    public void remove(Long userID){
        BOT_USERS.remove(userID);
    }

    public boolean userIsPresent(Long userID){
        return BOT_USERS.containsKey(userID);
    }

    public UserStatus getUserStatus(Long userID){
        return BOT_USERS.get(userID);
    }

    public void setNewStatus(Long userID, UserStatus newStatus){
        BOT_USERS.put(userID, newStatus);
    }
}