package ff15.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.DateRange;
import ff15.task.Deadline;
import ff15.task.TaskList;
import ff15.task.TaskTime;
import ff15.task.Todo;

/** Tests the two things {@code on} can say: the tasks on that day, or that there are none. */
public class OnCommandTest {

    @TempDir
    Path tempDir;

    private PrintStream realOut;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        realOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.resolve("t.txt").toString(), tempDir.resolve("c.txt").toString());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_tasksOnThatDay_listsThemUnderTheDate() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"), new Deadline("due", TaskTime.parse("2019-12-02")));

        new OnCommand(DateRange.parse("2019-12-02")).execute(tasks, new ContactList(), ui, storage);

        String shown = ui.drainTranscript();
        assertTrue(shown.startsWith("On Dec 02 2019 we've got:"), shown);
        assertTrue(shown.contains("1.[D][ ] due"), shown);
        assertFalse(shown.contains("no date"), shown);
    }

    @Test
    public void execute_nothingOnThatDay_saysSoAndNamesTheDay() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("due", TaskTime.parse("2019-12-02")));

        new OnCommand(DateRange.parse("2020-01")).execute(tasks, new ContactList(), ui, storage);

        String shown = ui.drainTranscript();
        assertTrue(shown.startsWith("Nothing on Jan 2020."), shown);
    }
}
