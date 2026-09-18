package aster.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aster.storage.Storage;
import aster.task.TaskList;
import aster.task.Todo;
import aster.ui.Ui;

/**
 * Tests what {@link FindCommand#execute(TaskList, Ui, Storage)} shows the user, and
 * what it leaves alone.
 *
 * <p>The command is checked through the lines it actually prints, captured by standing
 * in for standard output, rather than by asking it what it would print. That is what
 * makes these cases cover the heading, the numbering and the wording of the empty
 * result together, as the user meets them.
 *
 * <p>Each test hands the command a store pointed at its own temporary directory, so the
 * claim that searching never writes anything is checked rather than assumed, and the
 * real {@code data/aster.txt} is never touched.
 */
class FindCommandTest {
    private static final String DATA_FILE_NAME = "aster.txt";

    @TempDir
    Path tempDir;

    private ByteArrayOutputStream printed;
    private PrintStream realOut;

    @BeforeEach
    void captureOutput() {
        printed = new ByteArrayOutputStream();
        realOut = System.out;
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(realOut);
    }

    @Test
    void execute_matchingTasks_printsHeadingAndRenumberedMatches() {
        TaskList tasks = todosOf("read book", "wash up", "return book");

        find("book", tasks);

        // The second match is task 3 in the list but is shown as 2, because the matches
        // are numbered across themselves.
        assertEquals(List.of("Here are the matching tasks in your list:",
                "1. [T][ ] read book",
                "2. [T][ ] return book"), printedLines());
    }

    @Test
    void execute_noMatchingTasks_printsNoMatchMessage() {
        TaskList tasks = todosOf("read book", "wash up");

        find("bicycle", tasks);

        assertEquals(List.of("No tasks match that keyword."), printedLines());
    }

    @Test
    void execute_emptyList_printsNoMatchMessage() {
        find("book", new TaskList());

        assertEquals(List.of("No tasks match that keyword."), printedLines());
    }

    @Test
    void execute_anyKeyword_writesNothingToDisk() throws IOException {
        TaskList tasks = todosOf("read book");

        find("book", tasks);
        find("bicycle", tasks);

        assertTrue(isEmptyFolder(tempDir),
                "searching must neither save the list nor create the data file");
    }

    @Test
    void execute_anyKeyword_leavesTaskListUnchanged() {
        TaskList tasks = todosOf("read book", "wash up");

        find("book", tasks);

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("wash up", tasks.get(1).getDescription());
    }

    // ---------- helpers ----------

    /**
     * Runs a find command against the given list, with a store of its own.
     *
     * @param keyword the keyword to search for.
     * @param tasks the task list to search.
     */
    private void find(String keyword, TaskList tasks) {
        new FindCommand(keyword)
                .execute(tasks, new Ui(), new Storage(tempDir.resolve(DATA_FILE_NAME)));
    }

    /**
     * Returns the lines printed since the current test began.
     *
     * @return the printed lines, without their line endings.
     */
    private List<String> printedLines() {
        String output = printed.toString(StandardCharsets.UTF_8);
        return List.of(output.split("\\R"));
    }

    /**
     * Returns a task list holding one todo per description, in the order given.
     *
     * @param descriptions the descriptions to build todos from.
     * @return a list holding those todos.
     */
    private static TaskList todosOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    /**
     * Returns whether a folder holds nothing at all.
     *
     * @param folder the folder to look in.
     * @return {@code true} if it has no entries.
     * @throws IOException if the folder cannot be listed.
     */
    private static boolean isEmptyFolder(Path folder) throws IOException {
        try (Stream<Path> entries = Files.list(folder)) {
            return entries.findAny().isEmpty();
        }
    }
}
