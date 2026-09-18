package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests static console and GUI response formatting helpers. */
public class UiTest {
    @Test
    public void formatResponse_multipleLines_usesPlatformLineSeparator() {
        assertEquals("one" + System.lineSeparator() + "two", Ui.formatResponse("one", "two"));
    }

    @Test
    public void formatTaskAndNoteLists_numberItemsInOrder() {
        assertEquals("Tasks" + System.lineSeparator() + "1.[T][ ] read",
                Ui.formatTaskList("Tasks", List.of(new Todo("read"))));
        assertEquals("Notes" + System.lineSeparator() + "1.[N] buy milk",
                Ui.formatNoteList("Notes", List.of(new Note("buy milk"))));
    }
}
