package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command responses and their effects on both memory and saved tasks. */
class BkxssTest {
    @TempDir
    private Path temporaryDirectory;
    private Path dataFile;
    private Storage storage;
    private ArrayList<Task> tasks;

    @BeforeEach
    void setUp() {
        dataFile = temporaryDirectory.resolve("tasks.txt");
        storage = new Storage(dataFile.toString());
        tasks = new ArrayList<>();
    }

    @Test
    void processCommand_addEachTaskType_returnsCompleteResponsesAndSaves() {
        assertSuccess("todo read book", "Got it. I've added this task:\n"
                + "[T][ ] read book\nNow you have 1 tasks in the list.");
        assertSuccess("deadline return book /by 2028-02-29 1800", "Got it. I've added this task:\n"
                + "[D][ ] return book (by: Feb 29 2028 18:00)\nNow you have 2 tasks in the list.");
        assertSuccess("event meeting /from 2028-02-29 2300 /to 2028-03-01 0100",
                "Got it. I've added this task:\n"
                + "[E][ ] meeting (from: 2028-02-29 2300 to: 2028-03-01 0100)\n"
                + "Now you have 3 tasks in the list.");

        assertEquals(tasks.stream().map(Task::toString).toList(),
                storage.load().stream().map(Task::toString).toList());
    }

    @Test
    void processCommand_markAndUnmark_rejectsRepeatedChangesAndPersistsStatus() throws Exception {
        tasks.add(new Todo("read book"));
        storage.save(tasks);

        assertSuccess("mark 1", "Nice! I've marked this task as done:\n  [T][X] read book");
        assertTrue(storage.load().getFirst().isDone());
        assertError("mark 1", "this task is already marked as done!");
        assertTrue(tasks.getFirst().isDone());
        assertTrue(storage.load().getFirst().isDone());
        assertSuccess("unmark 1", "OK, I've marked this task as not done yet:\n  [T][ ] read book");
        assertFalse(storage.load().getFirst().isDone());
        assertError("unmark 1", "this task is already unmarked!");
        assertFalse(tasks.getFirst().isDone());
    }

    @Test
    void processCommand_deleteMiddleThenRemainingTasks_preservesOrderAndRenumbers() throws Exception {
        tasks.addAll(List.of(new Todo("first"), new Todo("middle"), new Todo("last")));
        tasks.get(1).markAsDone();
        storage.save(tasks);

        assertError("delete 4", "there is no task numbered 4.");
        assertSuccess("delete 2", "Noted. I've removed this task:\n  [T][X] middle\n"
                + "Now you have 2 tasks in the list.");
        assertSuccess("list", "Here are the tasks in your list:\n1.[T][ ] first\n2.[T][ ] last");
        assertEquals(List.of("[T][ ] first", "[T][ ] last"),
                storage.load().stream().map(Task::toString).toList());
        assertError("delete 3", "there is no task numbered 3.");
        assertFalse(Bkxss.processCommandResult("delete 1", tasks, storage).isError());
        assertSuccess("delete 1", "Noted. I've removed this task:\n  [T][ ] last\n"
                + "Now you have 0 tasks in the list.");
        assertTrue(tasks.isEmpty());
        assertTrue(storage.load().isEmpty());
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    void processCommand_find_renumbersMatchesAndSearchesOnlyDescriptions() {
        tasks.add(new Todo("unrelated"));
        tasks.add(new Todo("Read BOOK"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2026, 12, 2, 18, 0)));
        tasks.getLast().markAsDone();

        assertSuccess("find book", "Here are the matching tasks in your list:\n"
                + "1.[T][ ] Read BOOK\n2.[D][X] return book (by: Dec 02 2026 18:00)");
        assertSuccess("find Dec", "Here are the matching tasks in your list:");
        assertSuccess("find READ BOOK", "Here are the matching tasks in your list:\n1.[T][ ] Read BOOK");
        assertError("find", "please provide a keyword to search for. Use: find KEYWORD");
        assertEquals(3, tasks.size());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void processCommand_readOnlyCommands_neverAttemptSaving() throws Exception {
        // A regular file in place of the parent directory makes every save fail.
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "keep");
        storage = new Storage(blockedParent.resolve("tasks.txt").toString());

        assertSuccess("list", "Here are the tasks in your list:");
        assertSuccess("find missing", "Here are the matching tasks in your list:");
        assertSuccess("findfree 1 /from 2026-09-12 0900 /to 2026-09-12 1000",
                "The earliest 1-hour free slot is:\n  Sep 12 2026 09:00 to Sep 12 2026 10:00");
        assertSuccess(" \tbye\t ", "Bye. Hope to see you again soon!");
        assertEquals("keep", Files.readString(blockedParent));
    }

    @Test
    void processCommand_findFree_ignoresOtherTaskTypesButIncludesCompletedEvents() throws Exception {
        tasks.add(new Todo("read"));
        tasks.add(new Deadline("return", LocalDateTime.of(2026, 9, 12, 10, 0)));
        Event event = new Event("meeting", "2026-09-12 0900", "2026-09-12 1000");
        event.markAsDone();
        tasks.add(event);
        storage.save(tasks);
        String savedText = Files.readString(dataFile);

        assertSuccess("findfree 1 /from 2026-09-12 0900 /to 2026-09-12 1100",
                "The earliest 1-hour free slot is:\n  Sep 12 2026 10:00 to Sep 12 2026 11:00");
        assertSuccess("findfree 2 /from 2026-09-12 0900 /to 2026-09-12 1100",
                "I couldn't find a 2-hour free slot between Sep 12 2026 09:00 and Sep 12 2026 11:00.");
        assertEquals(savedText, Files.readString(dataFile));
        assertTrue(event.isDone());
    }

    @Test
    void processCommand_findFreeLegacyEvent_reportsOriginalTaskNumber() {
        tasks.add(new Todo("read"));
        tasks.add(new Event("old meeting", "Mon 2pm", "4pm"));

        assertError("findfree 1 /from 2026-09-12 0900 /to 2026-09-12 1100",
                "event 2 does not use yyyy-MM-dd HHmm dates. Re-add it with dated /from and /to values.");
        assertEquals(2, tasks.size());
    }

    @Test
    void processCommand_invalidSearchThenValidSearch_keepsStateAndRecovers() {
        String boundaries = " /from 2026-09-12 0900 /to 2026-09-12 1100";
        for (String duration : List.of("0", "-1", "1.5", "+1", "2147483648", "hours")) {
            assertError("findfree " + duration + boundaries,
                    "please provide the duration as a positive whole number of hours.");
            assertSuccess("findfree 1" + boundaries,
                    "The earliest 1-hour free slot is:\n  Sep 12 2026 09:00 to Sep 12 2026 10:00");
        }
        for (String boundary : List.of("2026-02-29 0900", "2026-09-12 2400", "tomorrow")) {
            assertError("findfree 1 /from " + boundary + " /to 2026-09-12 1100",
                    "please provide valid search dates in yyyy-MM-dd HHmm format, e.g. 2026-09-12 0900");
            assertError("findfree 1 /from 2026-09-12 0900 /to " + boundary,
                    "please provide valid search dates in yyyy-MM-dd HHmm format, e.g. 2026-09-12 0900");
        }
        assertError("findfree 1 /from 2026-09-12 1100 /to 2026-09-12 0900",
                "the free-time search start must be before its end.");
        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void processCommand_emptyTaskList_rejectsEveryIndexedOperation() {
        for (String verb : List.of("mark", "unmark", "delete")) {
            assertError(verb + " 1", "there is no task numbered 1.");
            assertError(verb, "please provide a task number. Use: mark/unmark/delete NUMBER");
        }
        assertSuccess("list", "Here are the tasks in your list:");
        assertTrue(tasks.isEmpty());
    }

    @Test
    void processCommand_missingParametersAndInvalidEventEnd_rejectsWithoutAddingTasks() {
        assertError("deadline", "invalid or repeated parameters. Use: deadline DESCRIPTION /by DATE");
        assertError("event", "invalid or repeated parameters. Use: event DESCRIPTION /from START /to END");
        assertError("findfree", "invalid or repeated parameters. Use: findfree HOURS /from START /to END");
        assertError("event meeting /from 2026-09-12 0900 /to tomorrow",
                "please provide valid event dates in yyyy-MM-dd HHmm format, e.g. 2026-09-12 0900");
        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void processCommand_unicodeSpacingAndEmbeddedSlashes_preservesDescription() {
        assertSuccess("\u2003todo\u00a0读书/read\u3000notes\u2003", "Got it. I've added this task:\n"
                + "[T][ ] 读书/read notes\nNow you have 1 tasks in the list.");
        assertSuccess("find 读书/read", "Here are the matching tasks in your list:\n1.[T][ ] 读书/read notes");
        assertEquals("[T][ ] 读书/read notes", storage.load().getFirst().toString());
    }

    @Test
    void processCommand_invalidInput_restoresOutputAndRejectsCommandPrefixes() {
        PrintStream originalOutput = System.out;
        for (String command : List.of("listing", "todoist", "eventual", "findfreedom", "MARK 1")) {
            assertError(command, "I'm sorry, but I don't know what that means :-(");
            assertSame(originalOutput, System.out);
        }
        assertError(null, "please enter a command, e.g. list or todo DESCRIPTION.");
        assertError(" \t ", "please enter a command, e.g. list or todo DESCRIPTION.");
        assertError("todo first\nlist", "commands cannot contain line breaks or control characters.");
        assertSame(originalOutput, System.out);
        assertTrue(tasks.isEmpty());
    }

    /** Checks the full public response so formatting regressions are visible. */
    private void assertSuccess(String command, String expectedMessage) {
        CommandResult result = Bkxss.processCommandResult(command, tasks, storage);
        assertFalse(result.isError(), command);
        assertEquals(expectedMessage, result.message(), command);
    }

    /** Checks both the error flag and the useful validation message. */
    private void assertError(String command, String expectedMessage) {
        CommandResult result = Bkxss.processCommandResult(command, tasks, storage);
        assertTrue(result.isError(), command);
        assertEquals("OhNo!! ERROR :( --> " + expectedMessage, result.message(), command);
    }
}
