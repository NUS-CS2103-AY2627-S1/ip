package bibi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks that browsing commands never loses the draft or changes submitted text.
 */
public class CommandHistoryTest {
    @Test
    public void recallPrevious_emptyHistory_preservesInput() {
        CommandHistory history = new CommandHistory();

        assertEquals("unfinished task", history.recallPrevious("unfinished task"));
        assertEquals("unfinished task", history.recallNext("unfinished task"));
    }

    @Test
    public void recallPrevious_multipleCommands_stopsAtOldest() {
        CommandHistory history = new CommandHistory();
        history.add("todo first");
        history.add("  LiSt  ");

        assertEquals("  LiSt  ", history.recallPrevious("draft"));
        assertEquals("todo first", history.recallPrevious("  LiSt  "));
        assertEquals("todo first", history.recallPrevious("todo first"));
    }

    @Test
    public void recallNext_browsingHistory_restoresOriginalDraft() {
        CommandHistory history = new CommandHistory();
        history.add("todo first");
        history.add("list");
        history.recallPrevious("todo unfinished");
        history.recallPrevious("list");

        assertEquals("list", history.recallNext("todo first"));
        assertEquals("todo unfinished", history.recallNext("list"));
        assertEquals("edited draft", history.recallNext("edited draft"));
    }

    @Test
    public void recallPrevious_secondBrowse_preservesUpdatedDraft() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.recallPrevious("first draft");
        history.recallNext("list");

        assertEquals("list", history.recallPrevious("second draft"));
        assertEquals("second draft", history.recallNext("list"));
    }

    @Test
    public void add_whileBrowsing_startsFreshHistoryPosition() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.recallPrevious("old draft");

        history.add("todo corrected");

        assertEquals("todo corrected", history.recallPrevious("new draft"));
        assertEquals("new draft", history.recallNext("todo corrected"));
    }

    @Test
    public void add_blankCommand_keepsHistoryAndDraft() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.recallPrevious("draft");

        history.add(" \t ");

        assertEquals("draft", history.recallNext("list"));
        assertEquals("list", history.recallPrevious("draft"));
    }
}
