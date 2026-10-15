package megatron.parser;

import megatron.command.Command;
import megatron.exception.MegatronException;
/** Converts user commands into command types and tasks. */
public class Parser {
    /** Parses a complete user input into an executable command. */
    public Command parse(String input) throws MegatronException {
        validateInput(input);
        CommandType type = getCommandType(input);
        if (type == CommandType.BYE) {
            return Command.exit();
        }
        if (type == CommandType.LIST) {
            return Command.list();
        }
        if (type == CommandType.TODO || type == CommandType.DEADLINE || type == CommandType.EVENT) {
            return Command.add(input, type);
        }
        if (type == CommandType.MARK || type == CommandType.UNMARK) {
            return Command.mark(input, type == CommandType.MARK);
        }
        if (type == CommandType.DELETE) {
            return Command.delete(input);
        }
        if (type == CommandType.SNOOZE) {
            return Command.snooze(input);
        }
        if (type == CommandType.FIND) {
            return Command.find(input);
        }
        throw new MegatronException(
                "I don't recognise that command. Try todo, deadline, event, list, mark, delete, or bye.");
    }

    /** Rejects input that cannot be interpreted reliably by the command grammar. */
    private void validateInput(String input) throws MegatronException {
        if (input == null || input.isBlank()) {
            throw new MegatronException("a command is required.");
        }
        if (!input.equals(input.trim())) {
            throw new MegatronException("remove leading and trailing spaces from the command.");
        }
        if (input.contains("  ")) {
            throw new MegatronException("use only one space between command parameters.");
        }
        if (input.chars().anyMatch(Character::isISOControl)) {
            throw new MegatronException("the command contains an unsupported control character.");
        }
    }
    public CommandType getCommandType(String command) {
        String[] words = {"todo", "deadline", "event", "mark", "unmark", "delete", "snooze"};
        CommandType[] types = {CommandType.TODO, CommandType.DEADLINE, CommandType.EVENT,
            CommandType.MARK, CommandType.UNMARK, CommandType.DELETE, CommandType.SNOOZE};
        for (int i = 0; i < words.length; i++) {
            if (command.equals(words[i]) || command.startsWith(words[i] + " ")) {
                return types[i];
            }
        }
        if (command.equals("list")) {
            return CommandType.LIST;
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return CommandType.FIND;
        }
        if (command.equals("bye")) {
            return CommandType.BYE;
        }
        return CommandType.UNKNOWN;
    }
}
