package mochi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CommandTest {
    @ParameterizedTest
    @EnumSource(value = Command.class, names = "UNKNOWN", mode = EnumSource.Mode.EXCLUDE)
    void fromInput_knownKeyword_recognisesFirstWord(Command command) {
        assertEquals(command, Command.fromInput("  " + command.getKeyword() + "\t details  "));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "TODO book", "listall", "todos book", "unknown", "\t"})
    void fromInput_unknownOrMissingKeyword_returnsUnknown(String input) {
        assertEquals(Command.UNKNOWN, Command.fromInput(input));
    }
}
