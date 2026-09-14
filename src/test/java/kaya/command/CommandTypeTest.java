package kaya.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Checks that command recognition uses complete, case-sensitive command words.
 */
public class CommandTypeTest {
    @Test
    public void fromInput_knownCommandsWithWhitespace_identifiesEveryCommand() {
        Map<String, CommandType> commands = Map.ofEntries(
                Map.entry("bye", CommandType.BYE), Map.entry("list", CommandType.LIST),
                Map.entry("mark", CommandType.MARK), Map.entry("unmark", CommandType.UNMARK),
                Map.entry("delete", CommandType.DELETE), Map.entry("update", CommandType.UPDATE),
                Map.entry("find", CommandType.FIND), Map.entry("todo", CommandType.TODO),
                Map.entry("deadline", CommandType.DEADLINE), Map.entry("event", CommandType.EVENT));

        commands.forEach((word, expected) -> {
            assertEquals(expected, CommandType.fromInput(word), word);
            assertEquals(expected, CommandType.fromInput(" \t" + word + "\targuments  "), word);
        });
    }

    @Test
    public void fromInput_unknownOrPartialWords_doesNotMatchByPrefix() {
        for (String input : new String[] {"", "  ", "Todo book", "LIST", "todos book", "mark1", "bye!", "unknown"}) {
            assertEquals(CommandType.UNKNOWN, CommandType.fromInput(input), input);
        }
    }
}
