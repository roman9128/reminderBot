package rt.reminder.bot.commands;

import lombok.Getter;

import java.util.HashMap;

@Getter
public enum BotCommand {
    START("/start"),
    REMIND("/remind"),
    ALL("/all");

    private final String commandText;
    private static final HashMap<String, BotCommand> BOT_COMMANDS;


    BotCommand(String commandText) {
        this.commandText = commandText;
    }

    static {
        BOT_COMMANDS = new HashMap<>();
        for (BotCommand command : values()) {
            BOT_COMMANDS.put(command.getCommandText(), command);
        }
    }

    public static BotCommand fromCommandText(String commandText) {
        return BOT_COMMANDS.get(commandText);
    }
}
