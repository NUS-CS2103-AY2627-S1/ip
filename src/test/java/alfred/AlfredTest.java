package alfred;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alfred.storage.Storage;
import alfred.ui.Ui;

class AlfredTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_validCommands_addsAndListsTasks() {
        Alfred alfred = createGuiAlfred();

        assertEquals("Consider it noted in the cave ledger:\n  [T][ ] read book\n"
                        + "Your cave ledger now holds 1 task.",
                alfred.getResponse("todo read book"));
        assertEquals("AddCommand", alfred.getLastCommandType());
        assertEquals("Here is your cave ledger:\n1.[T][ ] read book", alfred.getResponse("list"));
        assertEquals("ListCommand", alfred.getLastCommandType());
    }

    @Test
    void getResponse_invalidCommand_returnsErrorWithoutChangingTasks() {
        Alfred alfred = createGuiAlfred();

        alfred.getResponse("todo read book");

        assertEquals("Apologies, sir: Alfred cannot add a to-do without a mission description.",
                alfred.getResponse("todo"));
        assertEquals("Error", alfred.getLastCommandType());
        assertEquals("Here is your cave ledger:\n1.[T][ ] read book", alfred.getResponse("list"));
    }

    @Test
    void getResponse_duplicateTask_returnsErrorWithoutChangingTasks() {
        Alfred alfred = createGuiAlfred();

        alfred.getResponse("todo read book");

        assertEquals("Apologies, sir: Alfred already has an identical mission in the cave ledger.",
                alfred.getResponse("todo read book"));
        assertEquals("Error", alfred.getLastCommandType());
        assertEquals("Here is your cave ledger:\n1.[T][ ] read book", alfred.getResponse("list"));
    }

    @Test
    void getResponse_sortCommand_displaysSortedViewWithoutChangingTaskOrder() {
        Alfred alfred = createGuiAlfred();

        alfred.getResponse("todo read book");
        alfred.getResponse("deadline submit report /by 2019-12-03");
        alfred.getResponse("event meeting /from 2/12/2019 1800 /to 2/12/2019 1900");

        assertEquals("Here is your cave ledger in descending chronological order:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[D][ ] submit report (by: Dec 03 2019)\n"
                        + "3.[E][ ] meeting (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)",
                alfred.getResponse("sort desc"));
        assertEquals("SortCommand", alfred.getLastCommandType());
        assertEquals("Here is your cave ledger:\n1.[T][ ] read book\n"
                        + "2.[D][ ] submit report (by: Dec 03 2019)\n"
                        + "3.[E][ ] meeting (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)",
                alfred.getResponse("list"));
    }

    @Test
    void getResponse_byeRequestsExit() {
        Alfred alfred = createGuiAlfred();

        assertEquals("The cave is secure. Until our next mission, sir.", alfred.getResponse("bye"));
        assertEquals("ExitCommand", alfred.getLastCommandType());
        assertTrue(alfred.isExitRequested());
    }

    /** Creates Alfred with temporary storage and a GUI response buffer. */
    private Alfred createGuiAlfred() {
        StringBuilder responseBuffer = new StringBuilder();
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("alfred.txt"));
        return new Alfred(storage, new Ui(responseBuffer), responseBuffer);
    }
}
