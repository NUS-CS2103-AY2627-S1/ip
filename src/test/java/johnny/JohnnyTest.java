package johnny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import johnny.ui.Ui;

public class JohnnyTest {

    @TempDir
    Path tempDir;

    @Test
    public void processCommand_todo_displaysResponseAndContinues() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        boolean shouldExit = johnny.processCommand("todo read book");

        assertFalse(shouldExit);
        assertEquals(List.of(
                "     Consider it noted:",
                "       [T][ ] read book",
                "     Your agenda now has 1 task."), messages);
    }

    @Test
    public void processCommand_bye_displaysFarewellAndExits() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        boolean shouldExit = johnny.processCommand("bye");

        assertTrue(shouldExit);
        assertTrue(messages.contains("     Until next time. I'll keep things in order."));
    }

    @Test
    public void processCommand_byeWithArguments_displaysErrorAndContinues() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        boolean shouldExit = johnny.processCommand("bye later");

        assertFalse(shouldExit);
        assertEquals(List.of(
                "     I'm afraid something is amiss: The bye command does not accept parameters."),
                messages);
    }

    @Test
    public void processCommand_listWithArguments_displaysError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("list all");

        assertEquals(List.of(
                "     I'm afraid something is amiss: The list command does not accept parameters."),
                messages);
    }

    @Test
    public void processCommand_mark_displaysMarkedTaskAndContinues() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        messages.clear();

        boolean shouldExit = johnny.processCommand("mark 1");

        assertFalse(shouldExit);
        assertEquals(List.of(
                "     Excellent. One task completed:",
                "       [T][X] read book"), messages);
    }

    @Test
    public void processCommand_unmark_displaysUnmarkedTask() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("mark 1");
        messages.clear();

        johnny.processCommand("unmark 1");

        assertEquals(List.of(
                "     Very well. This task is pending again:",
                "       [T][ ] read book"), messages);
    }

    @Test
    public void processCommand_delete_displaysDeletedTaskAndPersistsRemoval() throws IOException {
        Path file = tempDir.resolve("johnny.txt");
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(file.toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        messages.clear();

        johnny.processCommand("delete 1");

        assertEquals(List.of(
                "     Removed from the agenda:",
                "       [T][ ] read book",
                "     Your agenda now has 0 tasks."), messages);
        assertEquals("", Files.readString(file));
    }

    @Test
    public void processCommand_deadlineAndEvent_addsAndPersistsTasks() throws IOException {
        Path file = tempDir.resolve("johnny.txt");
        Johnny johnny = new Johnny(file.toString(), new Ui(message -> { }));

        johnny.processCommand("deadline return book /by 2026-09-20");
        johnny.processCommand("event meeting /from 2026-09-21 /to 2026-09-22");

        assertEquals("D | 0 | return book | 2026-09-20" + System.lineSeparator()
                + "E | 0 | meeting | 2026-09-21 | 2026-09-22" + System.lineSeparator(),
                Files.readString(file));
    }

    @Test
    public void processCommand_find_displaysOnlyMatchingTasks() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("todo buy groceries");
        messages.clear();

        johnny.processCommand("find book");

        assertEquals(List.of(
                "     These entries match your request:",
                "     1.[T][ ] read book"), messages);
    }

    @Test
    public void processCommand_undoAddedTask_removesTask() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:"), messages);
    }

    @Test
    public void processCommand_undoDeletedTask_restoresTaskAtOriginalPosition() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo first");
        johnny.processCommand("todo second");
        johnny.processCommand("delete 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
                "     1.[T][ ] first",
                "     2.[T][ ] second"), messages);
    }

    @Test
    public void processCommand_undoMarkedTask_restoresPreviousStatus() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("mark 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
                "     1.[T][ ] read book"), messages);
    }

    @Test
    public void processCommand_undoRepeatedMark_keepsPreviouslyDoneTaskDone() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("mark 1");
        johnny.processCommand("mark 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
                "     1.[T][X] read book"), messages);
    }

    @Test
    public void processCommand_undoUnmark_restoresPreviouslyDoneTask() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("mark 1");
        johnny.processCommand("unmark 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
                "     1.[T][X] read book"), messages);
    }

    @Test
    public void processCommand_undoRepeatedUnmark_keepsPreviouslyPendingTaskPending() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("unmark 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
                "     1.[T][ ] read book"), messages);
    }

    @Test
    public void processCommand_undoWithoutTaskChange_displaysError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("undo");

        assertEquals(List.of("     I'm afraid something is amiss: There is no command to undo."), messages);
    }

    @Test
    public void processCommand_undoWithArguments_displaysParameterError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("undo now");

        assertEquals(List.of(
                "     I'm afraid something is amiss: The undo command does not accept parameters."),
                messages);
    }

    @Test
    public void processCommand_unknownCommand_displaysButlerError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("blah");

        assertEquals(List.of(
                "     I'm afraid something is amiss: I couldn't identify that command."), messages);
    }

    @Test
    public void constructor_directoryPath_displaysLoadingWarning() {
        List<String> messages = new ArrayList<>();

        new Johnny(tempDir.toString(), new Ui(messages::add));

        assertEquals(List.of(
                "     A note, if I may: I couldn't load your agenda, so we'll start afresh."), messages);
    }

    @Test
    public void processCommand_storagePathIsDirectory_displaysSavingWarning() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.toString(), new Ui(messages::add));
        messages.clear();

        johnny.processCommand("todo read book");

        assertEquals(List.of(
                "     Consider it noted:",
                "       [T][ ] read book",
                "     Your agenda now has 1 task.",
                "     A note, if I may: I couldn't save your agenda."), messages);
    }

    @Test
    @ResourceLock(Resources.GLOBAL)
    public void run_commandsUntilBye_processesCommandsAndIgnoresLaterInput() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(
                    "todo read book\nbye\ntodo ignored\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Johnny(tempDir.resolve("johnny.txt").toString()).run();

            String consoleOutput = output.toString(StandardCharsets.UTF_8);
            assertTrue(consoleOutput.contains("Good day. Johnny at your service."));
            assertTrue(consoleOutput.contains("[T][ ] read book"));
            assertTrue(consoleOutput.contains("Until next time."));
            assertFalse(consoleOutput.contains("ignored"));
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
