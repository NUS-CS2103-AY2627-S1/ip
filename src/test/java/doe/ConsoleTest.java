package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises console input and output in isolation, restoring global streams after every test. */
class ConsoleTest {
    private static final String SEPARATOR = "____________________________________________________________\n";
    @TempDir
    Path directory;
    private InputStream originalInput;
    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private ByteArrayOutputStream output;

    @BeforeEach
    void captureOutput() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    @Test
    void readCommand_preservesWhitespaceAndBlankLines_reportsEndOfInput() {
        input("  hello  \n\n");
        UserInterface ui = new UserInterface();
        assertEquals("  hello  ", ui.readCommand());
        assertEquals("", ui.readCommand());
        assertThrows(NoSuchElementException.class, ui::readCommand);
        ui.closeScanner();
        assertThrows(IllegalStateException.class, ui::readCommand);
    }

    @Test
    void getTaskIndex_boundariesAndMalformedNumbers_reportErrors() {
        input("1\n 3 \n0\n4\n-1\nno\n2147483648\n\n");
        UserInterface ui = new UserInterface();
        assertThrows(AssertionError.class, () -> ui.getTaskIndex(-1));
        assertEquals(-1, ui.getTaskIndex(0));
        assertEquals("there are no tasks to mark yet. add a task first.\n", text());
        output.reset();
        assertEquals(0, ui.getTaskIndex(3));
        assertEquals(2, ui.getTaskIndex(3));
        assertEquals("enter the task number:\nenter the task number:\n", text());
        for (int i = 0; i < 6; i++) {
            output.reset();
            assertEquals(-1, ui.getTaskIndex(3));
            assertEquals("enter the task number:\n"
                    + "that task number is incorrect. please enter a number from 1 to 3.\n", text());
        }
        ui.closeScanner();
    }

    @Test
    void preventCorrupt_delimiterRejected_cleanInputSilent() {
        assertFalse(UserInterface.preventCorrupt("学习"));
        assertEquals("", text());
        assertTrue(UserInterface.preventCorrupt("bad|input"));
        assertEquals("error: input cannot contain '|' character.\n", text());
    }

    @Test
    void printLists_emptyAndMatchingTasks_keepOriginalNumbers() {
        UserInterface ui = new UserInterface();
        Task first = new Todo("first");
        Task second = new Todo("second");
        ui.printTodoList(List.of());
        assertEquals(SEPARATOR + "here are the tasks in your list:\n" + SEPARATOR, text());
        output.reset();
        ui.printMatchingTasks(List.of(second), List.of(first, second));
        assertEquals(SEPARATOR + "Here are the matching tasks in your list:\n2. [t][ ] second\n"
                + SEPARATOR, text());
        output.reset();
        ui.printMatchingTasks(List.of(), List.of(first));
        assertEquals(SEPARATOR + "no matching tasks found.\n" + SEPARATOR, text());
        ui.closeScanner();
    }

    @Test
    void sortedList_emptyDatedAndUndatedSections_useContinuousNumbers() {
        assertEquals("here are the tasks in your list:", UserInterface.sortedTodoListText(List.of()));
        assertEquals("here are the tasks in your list:\n\nno deadlines:\n1. [t][ ] a\n2. [t][ ] b",
                UserInterface.sortedTodoListText(List.of(new Todo("a"), new Todo("b"))));
        String dated = UserInterface.sortedTodoListText(List.of(new Event("a", "01-01-2027 1200")));
        assertTrue(dated.startsWith("here are the tasks in your list:\n1. [e][ ] a"));
        assertFalse(dated.contains("no deadlines"));
    }

    @Test
    void console_mainCommandsAndBadMenus_recoverAndExit() {
        run("neigh\nmeow\nlist\nunknown\ntodo\nwrong\nadd\nwrong\nexit\nexit\nbye\n");
        assertTrue(text().contains("eurhggghhhhh!"));
        assertTrue(text().contains("meow!"));
        assertTrue(text().contains("roles and responsibilities"));
        assertTrue(text().contains("horh"));
        assertTrue(text().contains("please choose one of the todo options"));
        assertTrue(text().contains("please choose 1, 2, 3, todo, deadline, or event"));
        assertTrue(text().contains("bye. hope to see you again soon!"));
    }

    @Test
    void console_addMarkUnmarkFindAndRemove_persistFinalState() {
        run("todo\nadd\ntodo\nread\ntodo\nadd\nevent\nmeeting\n01-01-2027 1200\n"
                + "todo\nmark\n1\ntodo\nunmark\n1\ntodo\nview\n"
                + "todo\nfind\nmissing\ntodo\nremove\nmissing\ntodo\nremove\nread\nbye\n");
        assertTrue(text().contains("nice! i've marked this task as done:\n[x] read"));
        assertTrue(text().contains("ok i've marked this task as not done yet:\n[] read"));
        assertTrue(text().contains("no matching tasks found."));
        assertTrue(text().contains("could not find a task with that name or number"));
        assertTrue(text().contains("item removed successfully!"));
        assertEquals(List.of("EVENT|false|meeting|01-01-2027 1200"), records());
    }

    @Test
    void console_invalidMarkAndUnmark_returnsToTodoMenuForRetry() {
        run("todo\nmark\nunmark\nadd\ntodo\nread\ntodo\nmark\n0\nunmark\nno\nmark\n1\nbye\n");
        assertTrue(text().contains("there are no tasks to mark yet"));
        assertTrue(text().contains("that task number is incorrect"));
        assertEquals(List.of("TODO|true|read"), records());
    }

    @Test
    void console_badDescriptionAndDate_recoverAtExpectedMenu() {
        run("todo\nadd\ntodo\nbad|name\ntodo\nadd\ndeadline\nreport\nbad|date\n"
                + "event\nmeeting\n31-02-2027 1200\nevent\nmeeting\n01-01-2027 1200\nbye\n");
        assertTrue(text().contains("task description cannot contain"));
        assertTrue(text().contains("error: input cannot contain '|' character."));
        assertTrue(text().contains("invalid date format"));
        assertEquals(List.of("EVENT|false|meeting|01-01-2027 1200"), records());
    }

    @Test
    void console_sortMixedTasks_displaysSectionsAndPersistsOrder() {
        run("todo\nadd\ntodo\nread\ntodo\nadd\ndeadline\nreport\n02-01-2027 1200\n"
                + "todo\nadd\nevent\nmeeting\n01-01-2027 1200\ntodo\nsort\nbye\n");
        assertTrue(text().contains("1. [e][ ] meeting"));
        assertTrue(text().contains("2. [d][ ] report"));
        assertTrue(text().contains("\n\nno deadlines:\n3. [t][ ] read"));
        assertEquals(List.of("EVENT|false|meeting|01-01-2027 1200",
                "DEADLINE|false|report|02-01-2027 1200", "TODO|false|read"), records());
    }

    @Test
    void console_damagedFile_warnsAndPreservesOriginal() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken\n");
        run("todo\nadd\ntodo\nread\nbye\n");
        assertTrue(text().startsWith("warning: Some tasks could not be loaded because lines 1"));
        assertTrue(text().contains("changes are in memory only and have not been saved."));
        assertEquals("broken\n", Files.readString(file));
    }

    private void input(String commands) {
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
    }

    private void run(String commands) {
        input(commands);
        new Doe(directory.resolve("tasks.txt").toString()).run();
    }

    private List<String> records() {
        return new Storage(directory.resolve("tasks.txt").toString()).load().stream().map(Task::toFileFormat).toList();
    }

    private String text() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
