package nori.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    @Test
    public void recallEarlier_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), new CommandHistory().recallEarlier());
    }

    @Test
    public void recallLater_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), new CommandHistory().recallLater());
    }

    @Test
    public void recallEarlier_oneCommand_returnsThatCommand() {
        CommandHistory history = new CommandHistory();
        history.add("todo read book");

        assertEquals(Optional.of("todo read book"), history.recallEarlier());
    }

    @Test
    public void recallEarlier_repeatedly_walksBackToTheOldestAndStops() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        history.add("third");

        assertEquals(Optional.of("third"), history.recallEarlier());
        assertEquals(Optional.of("second"), history.recallEarlier());
        assertEquals(Optional.of("first"), history.recallEarlier());
        assertEquals(Optional.of("first"), history.recallEarlier());
    }

    @Test
    public void recallLater_afterWalkingBack_walksForwardToAnEmptyLine() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        history.recallEarlier();
        history.recallEarlier();

        assertEquals(Optional.of("second"), history.recallLater());
        assertEquals(Optional.of(""), history.recallLater());
        assertEquals(Optional.empty(), history.recallLater());
    }

    @Test
    public void add_repeatOfTheNewestCommand_isNotRecordedTwice() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("list");

        assertEquals(Optional.of("list"), history.recallEarlier());
        assertEquals(Optional.of("list"), history.recallEarlier());
    }

    @Test
    public void add_repeatThatIsNotTheNewest_isRecorded() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("help");
        history.add("list");

        assertEquals(Optional.of("list"), history.recallEarlier());
        assertEquals(Optional.of("help"), history.recallEarlier());
        assertEquals(Optional.of("list"), history.recallEarlier());
    }

    @Test
    public void add_whileBrowsing_returnsBrowsingToTheNewestCommand() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        history.recallEarlier();
        history.recallEarlier();

        history.add("third");

        assertEquals(Optional.of("third"), history.recallEarlier());
    }
}
