package bags.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Commands recognized from user input.
 */
public enum Command {
    ADD_TASK,
    LIST,
    MARK,
    UNMARK,
    ECHO,
    DELETE,
    SEARCH,
    BYE,
    EMPTY,
    EDIT,
    UNKNOWN;

    private static final Map<String, Command> commands = createCommands();

    private static Map<String, Command> createCommands() {
        Map<String, Command> commands = new HashMap<>();
        commands.put("add task", ADD_TASK);
        commands.put("list", LIST);
        commands.put("mark", MARK);
        commands.put("unmark", UNMARK);
        commands.put("echo", ECHO);
        commands.put("search", SEARCH);
        commands.put("delete", DELETE);
        commands.put("bye", BYE);
        commands.put("edit", EDIT);
        return commands;
    }

    static Command parseInput(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.startsWith("add task")) {
            return commands.get("add task");
        }

        String commandWord = trimmedInput.split(" ", 2)[0];
        return commands.getOrDefault(commandWord, UNKNOWN);
    }
}

