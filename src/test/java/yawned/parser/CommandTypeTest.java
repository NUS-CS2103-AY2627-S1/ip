package yawned.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/** Tests command-word recognition performed by {@link CommandType}. */
class CommandTypeTest {

    @Test
    void getWord_everyCommandType_returnsItsCommandWord() {
        Map<CommandType, String> commandWords = Map.ofEntries(
                Map.entry(CommandType.TODO, "todo"),
                Map.entry(CommandType.DEADLINE, "deadline"),
                Map.entry(CommandType.EVENT, "event"),
                Map.entry(CommandType.LIST, "list"),
                Map.entry(CommandType.MARK, "mark"),
                Map.entry(CommandType.UNMARK, "unmark"),
                Map.entry(CommandType.DELETE, "delete"),
                Map.entry(CommandType.FIND, "find"),
                Map.entry(CommandType.ALIAS, "alias"),
                Map.entry(CommandType.UNKNOWN, ""));

        commandWords.forEach((commandType, expectedWord) ->
                assertEquals(expectedWord, commandType.getWord()));
    }

    @Test
    void fromInput_exactSupportedCommand_returnsMatchingType() {
        Map<String, CommandType> commands = Map.of(
                "todo", CommandType.TODO,
                "deadline", CommandType.DEADLINE,
                "event", CommandType.EVENT,
                "list", CommandType.LIST,
                "mark", CommandType.MARK,
                "unmark", CommandType.UNMARK,
                "delete", CommandType.DELETE,
                "find", CommandType.FIND,
                "alias", CommandType.ALIAS);

        commands.forEach((input, expectedType) ->
                assertEquals(expectedType, CommandType.fromInput(input)));
    }

    @Test
    void fromInput_argumentAcceptingCommandWithDetails_returnsMatchingType() {
        Map<String, CommandType> commands = Map.of(
                "todo read book", CommandType.TODO,
                "deadline submit report /by 2026-01-01 0900", CommandType.DEADLINE,
                "event meeting /from 2026-01-01 0900 /to 2026-01-01 1000", CommandType.EVENT,
                "mark 1", CommandType.MARK,
                "unmark 1", CommandType.UNMARK,
                "delete 1", CommandType.DELETE,
                "find book", CommandType.FIND);

        commands.forEach((input, expectedType) ->
                assertEquals(expectedType, CommandType.fromInput(input)));
    }

    @Test
    void fromInput_builtinAliases_returnsMatchingCommandType() {
        Map<String, CommandType> aliases = Map.of(
                "T read book", CommandType.TODO,
                "d submit report /by 2026-01-01 0900", CommandType.DEADLINE,
                "E meeting /from 2026-01-01 0900 /to 2026-01-01 1000", CommandType.EVENT,
                "l", CommandType.LIST,
                "M 1", CommandType.MARK,
                "u 1", CommandType.UNMARK,
                "DeL 1", CommandType.DELETE,
                "F book", CommandType.FIND);

        aliases.forEach((input, expectedType) ->
                assertEquals(expectedType, CommandType.fromInput(input)));
    }

    @Test
    void fromInput_noArgumentCommandWithExtraText_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("list all"));
    }

    @Test
    void fromInput_emptyMalformedOrCaseMismatchedCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(""));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("todoist buy bread"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("mark2"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(" list"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("TODO buy bread"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("todolist buy bread"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("deluxe 1"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("l all"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("b"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("bye"));
    }
}
