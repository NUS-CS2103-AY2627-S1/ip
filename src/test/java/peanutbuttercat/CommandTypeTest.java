package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests recognition of command words and their argument boundaries. */
class CommandTypeTest {
    @Test
    void fromInput_argumentSeparatedByWhitespace_returnsMatchingCommand() {
        assertEquals(CommandType.TODO, CommandType.fromInput("todo buy milk"));
        assertEquals(CommandType.STATISTICS, CommandType.fromInput("stats unexpected"));
    }

    @Test
    void fromInput_commandWordUsedAsPrefix_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("todoist"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("listening"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("statsweek"));
    }
}
