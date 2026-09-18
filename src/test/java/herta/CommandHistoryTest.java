package herta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests bounded command history without requiring JavaFX controls.
 */
class CommandHistoryTest {
    @Test
    void record_blankAndAdjacentDuplicateCommands_ignoresBoth() {
        CommandHistory history = new CommandHistory(3);
        history.record(" ");
        history.record("first");
        history.record("first");
        history.record("second");

        assertEquals(Optional.of("second"), history.moveToPreviousCommand("draft"));
        assertEquals(Optional.of("first"), history.moveToPreviousCommand("ignored"));
        assertEquals(Optional.of("second"), history.moveToNextCommand());
        assertEquals(Optional.of("draft"), history.moveToNextCommand());
    }

    @Test
    void record_atMaximumCapacity_keepsNewestCommands() {
        CommandHistory history = new CommandHistory(2);
        history.record("first");
        history.record("second");
        history.record("third");

        assertEquals(Optional.of("third"), history.moveToPreviousCommand("draft"));
        assertEquals(Optional.of("second"), history.moveToPreviousCommand("draft"));
        assertEquals(Optional.of("third"), history.moveToNextCommand());
        assertEquals(Optional.of("draft"), history.moveToNextCommand());
        assertEquals(Optional.empty(), history.moveToNextCommand());
    }

    @Test
    void navigation_atBoundaries_returnsNoEntryWhenMovementIsUnavailable() {
        CommandHistory history = new CommandHistory(2);
        history.record("only");

        assertEquals(Optional.empty(), history.moveToNextCommand());
        assertEquals(Optional.of("only"), history.moveToPreviousCommand("draft"));
        assertEquals(Optional.of("only"), history.moveToPreviousCommand("draft"));
        assertEquals(Optional.of("draft"), history.moveToNextCommand());
    }

    @Test
    void navigation_afterPreviousRestoresDraftAtNewestPosition() {
        CommandHistory history = new CommandHistory(2);
        history.record("first");
        history.record("second");

        assertEquals(Optional.of("second"), history.moveToPreviousCommand("unfinished command"));
        assertEquals(Optional.of("first"), history.moveToPreviousCommand("ignored"));
        assertEquals(Optional.of("second"), history.moveToNextCommand());
        assertEquals(Optional.of("unfinished command"), history.moveToNextCommand());
    }

    @Test
    void record_afterNavigation_resetsNavigationToNewestCommand() {
        CommandHistory history = new CommandHistory(2);
        history.record("first");
        history.moveToPreviousCommand("draft");
        history.record("newest");

        assertEquals(Optional.empty(), history.moveToNextCommand());
        assertEquals(Optional.of("newest"), history.moveToPreviousCommand("another draft"));
    }

    @Test
    void create_nonPositiveCapacity_rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new CommandHistory(0));
    }
}
