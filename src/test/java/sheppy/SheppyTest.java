package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Sheppy's command handling across storage-backed sessions. */
class SheppyTest {
    /** Provides an isolated folder for each storage test. */
    @TempDir
    private Path temporaryDirectory;

    /** Checks that sorting is retained after the task list is reloaded. */
    @Test
    void sortCommand_reloadedTasksRemainSorted() {
        String filePath = temporaryDirectory.resolve("tasks.txt").toString();
        Sheppy sheppy = new Sheppy(filePath);
        sheppy.getResponse("todo Write report");
        sheppy.getResponse("todo buy milk");
        sheppy.getResponse("todo Attend meeting");

        sheppy.getResponse("sort");
        Sheppy reloadedSheppy = new Sheppy(filePath);

        String lineSeparator = System.lineSeparator();
        assertEquals("Here are the tasks in your list:" + lineSeparator
                + "1.[T][ ] Attend meeting" + lineSeparator
                + "2.[T][ ] buy milk" + lineSeparator
                + "3.[T][ ] Write report", reloadedSheppy.getResponse("list"));
    }
}
