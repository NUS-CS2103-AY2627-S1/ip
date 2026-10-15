package samantha.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class HelpWindowTest {
    @Test
    void getCommandNames_allSupportedCommandsAppearInNavigationOrder() {
        assertEquals(List.of(
                "help", "todo", "deadline", "event", "list", "find", "note", "notes",
                "edit-note", "delete-note", "mark", "unmark", "delete", "undo", "bye"),
                HelpWindow.getCommandNames());
    }

    @Test
    void getAdjacentCommandIndex_movesAndWrapsAroundGuide() {
        assertEquals(1, HelpWindow.getAdjacentCommandIndex(0, 1));
        assertEquals(14, HelpWindow.getAdjacentCommandIndex(0, -1));
        assertEquals(0, HelpWindow.getAdjacentCommandIndex(14, 1));
    }

    @Test
    void hasFormatNote_onlyDateAndTimeCommandsRequireFormatHints() {
        assertTrue(HelpWindow.hasFormatNote("deadline"));
        assertTrue(HelpWindow.hasFormatNote("event"));
        assertTrue(HelpWindow.hasFormatNote("list"));
        assertFalse(HelpWindow.hasFormatNote("todo"));
        assertFalse(HelpWindow.hasFormatNote("note"));
    }
}
