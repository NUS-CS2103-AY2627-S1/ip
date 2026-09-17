package dog.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Enum representing available commands in the Dog application.
 * Provides methods to parse and identify commands from user input.
 */
public enum Command {
    LIST("list", "l", "ls"),
    MARK("mark", "m", "done", "complete"),
    DELETE("delete", "del", "remove", "rm"),
    TODO("todo", "t", "task", "add"),
    DEADLINE("deadline", "d"),
    EVENT("event", "e"),
    FIND("find", "f", "search"),
    BYE("bye", "exit", "q"),
    WOOF("woof", "arf", "bark");

    private static final Map<String, Command> ALIAS_MAP = buildAliasMap();
    private final String[] aliases;

    Command(String... aliases) {
        this.aliases = aliases;
    }

    private static Map<String, Command> buildAliasMap() {
        Map<String, Command> map = new HashMap<>();
        for (Command cmd : Command.values()) {
            for (String alias : cmd.aliases) {
                Command duplicateAlias = map.put(alias.toLowerCase(), cmd);
                assert duplicateAlias == null : "no duplicate command aliases allowed";
            }
        }
        return Map.copyOf(map); // unmodifiable snapshot
    }

    /**
     * Parses the input string to identify the command.
     *
     * @param input The trimmed user input string.
     * @return An Entry containing the matching Command enum and the suffix string, or null if no match is found.
     */
    public static Entry<Command, String> fromInput(String input) {
        assert input.equals(input.trim()) : "input should have been trimmed";

        String[] result = input.split(" ", 2);
        assert result.length >= 1 : "Input parsing result must have at least length 1";
        assert result.length <= 2 : "Input parsing result must have at most length 2";

        String inputPrefix = result[0].toLowerCase();
        Command command = ALIAS_MAP.get(inputPrefix);

        if (command == null) {
            return null;
        }

        if (result.length == 1) {
            return Map.<Command, String>entry(command, "");
        }
        return Map.<Command, String>entry(command, result[1]);
    }
}
