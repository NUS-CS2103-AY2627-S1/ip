package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import happybot.task.Deadline;
import happybot.task.Task;
import happybot.task.ToDo;

public class UiTest {
    private static final String DIVIDER = "____________________________________________________________";

    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @AfterEach
    void restoreStandardStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void hasNextCommandAndReadCommand_multipleCommands_commandsReadInOrder() {
        System.setIn(new ByteArrayInputStream("todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void getWelcomeAndGoodbyeMessage_standardMessagesReturned() {
        Ui ui = new Ui();

        assertEquals("Hello! I'm HappyBot.\nHow can I cheer you up today?", ui.getWelcomeMessage());
        assertEquals("Bye. Hope to see you again soon!", ui.getGoodbyeMessage());
    }

    @Test
    public void formatTaskList_emptyAndPopulatedLists_expectedEntriesReturned() {
        Ui ui = new Ui();

        assertEquals(" Here are the tasks in your list:", ui.formatTaskList(List.of()));
        assertEquals(" Here are the tasks in your list:\n 1.[T][ ] read book\n 2.[T][ ] return book",
                ui.formatTaskList(List.of(new ToDo("read book"), new ToDo("return book"))));
    }

    @Test
    public void formatDeadlinesDueOn_emptyAndPopulatedMaps_expectedMessagesReturned() {
        Ui ui = new Ui();
        Map<Integer, Deadline> deadlines = new LinkedHashMap<>();
        deadlines.put(2, new Deadline("return book", LocalDateTime.of(2026, 9, 16, 18, 0)));

        assertEquals(" There are no deadlines due on Sep 16 2026.",
                ui.formatDeadlinesDueOn("Sep 16 2026", Map.of()));
        assertEquals(" Here are the deadlines due on Sep 16 2026:\n"
                        + " 2.[D][ ] return book (by: Sep 16 2026 6:00PM)",
                ui.formatDeadlinesDueOn("Sep 16 2026", deadlines));
    }

    @Test
    public void formatMatchingTasks_emptyAndPopulatedMaps_expectedMessagesReturned() {
        Ui ui = new Ui();
        Map<Integer, Task> tasks = new LinkedHashMap<>();
        tasks.put(3, new ToDo("read book"));

        assertEquals(" There are no tasks matching \"book\".", ui.formatMatchingTasks("book", Map.of()));
        assertEquals(" Here are the matching tasks in your list:\n 3.[T][ ] read book",
                ui.formatMatchingTasks("book", tasks));
    }

    @Test
    public void formatStatistics_countsEachCategory_expectedMessageReturned() {
        Ui ui = new Ui();
        TaskStatistics statistics = new TaskStatistics(4, 3, 1, 1, 1, 1);

        assertEquals(" Here are your statistics for this week:\n"
                        + " Total tasks: 4\n"
                        + " Completed this week: 3\n"
                        + "   To-dos: 1\n"
                        + "   Deadlines: 1\n"
                        + "   Events: 1\n"
                        + " Uncompleted deadlines due this week: 1",
                ui.formatStatistics(statistics));
    }

    @Test
    public void formatTaskChangeMessages_taskAndCount_expectedMessagesReturned() {
        Ui ui = new Ui();
        ToDo task = new ToDo("read book");

        assertEquals(" Got it. I've added this task:\n"
                        + "   [T][ ] read book\n"
                        + " Now you have 1 tasks in the list.",
                ui.formatAddedTask(task, 1));
        assertEquals(" Alrighties I've removed this task:\n"
                        + "   [T][ ] read book\n"
                        + " Now you have 0 tasks in the list.",
                ui.formatDeletedTask(task, 0));

        task.markAsDone();
        assertEquals(" Nice! I've marked this task as done:\n   [T][X] read book", ui.formatMarkedTask(task));

        task.unmarkAsDone();
        assertEquals(" OK, I've marked this task as not done yet:\n   [T][ ] read book",
                ui.formatUnmarkedTask(task));
    }

    @Test
    public void formatWarningsAndError_filePathAndMessage_expectedMessagesReturned() {
        Ui ui = new Ui();
        Path filePath = Path.of("data", "HappyBot.txt");

        assertEquals(" Heads up! I could not read data/HappyBot.txt, so I am starting with an empty task list.",
                ui.formatLoadingError(filePath));
        assertEquals(" Heads up! I skipped 2 unreadable line(s) in your saved data.",
                ui.formatSkippedLinesNotice(2));
        assertEquals(" Heads up! I could not save your tasks to data/HappyBot.txt.",
                ui.formatSavingError(filePath));
        assertEquals(" Heads up! Your task list at data/HappyBot.txt is already open in another HappyBot session."
                        + " Close the other session, then restart this HappyBot session.",
                ui.formatDataFileInUse(filePath));
        assertEquals("Your task list at data/HappyBot.txt is already open in another HappyBot session."
                        + " Close the other session, then restart this HappyBot session.",
                ui.getDataFileInUseMessage(filePath));
        assertEquals(" Oops! bad command", ui.formatError("bad command"));
    }

    @Test
    public void showDivider_dividerPrinted() {
        Ui ui = new Ui();

        assertEquals(DIVIDER + "\n", captureOutput(ui::showDivider));
    }

    @Test
    public void showWelcomeAndGoodbye_standardMessagesPrintedWithDividers() {
        Ui ui = new Ui();

        String output = captureOutput(() -> {
            ui.showWelcome();
            ui.showGoodbye();
        });

        assertTrue(output.startsWith(DIVIDER + "\nH   H   AAA"));
        assertTrue(output.contains("Hello! I'm HappyBot.\nHow can I cheer you up today?\n" + DIVIDER));
        assertTrue(output.endsWith("Bye. Hope to see you again soon!\n" + DIVIDER + "\n"));
    }

    @Test
    public void showNoticeAndResponse_noticeAndReplyPrintedWithDividers() {
        Ui ui = new Ui();

        String output = captureOutput(() -> {
            ui.showNotice(" Heads up!");
            ui.showResponse(" Here is your reply.");
        });

        assertEquals(DIVIDER + "\n Heads up!\n" + DIVIDER + "\n"
                        + " Here is your reply.\n" + DIVIDER + "\n",
                output);
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

        action.run();

        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
