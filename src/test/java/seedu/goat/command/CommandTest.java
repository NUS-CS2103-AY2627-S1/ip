package seedu.goat.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.goat.GoatException;

public class CommandTest {
    @Test
    public void fromKeyword_everyCommand_roundTrips() throws GoatException {
        for (Command command : Command.values()) {
            assertEquals(command, Command.fromKeyword(command.getKeyword()));
        }
    }

    @Test
    public void fromKeyword_missingOrWrongCase_reportsKnownCommands() {
        for (String keyword : new String[]{"", "LIST", "unknown"}) {
            GoatException error = assertThrows(GoatException.class, () -> Command.fromKeyword(keyword));
            for (Command command : Command.values()) {
                assertTrue(error.getMessage().contains(command.getKeyword()));
            }
        }
    }
}
