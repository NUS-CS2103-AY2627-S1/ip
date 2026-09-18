package com.benthecat.kachow.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.benthecat.kachow.parser.DateTimeParser;
import com.benthecat.kachow.task.Deadline;
import com.benthecat.kachow.task.Event;
import com.benthecat.kachow.task.TaskList;
import com.benthecat.kachow.task.Todo;
import com.benthecat.kachow.ui.printer.ConsolePrinter;
import com.benthecat.kachow.ui.printer.Printer;

/**
 * Tests console fragments and response presentation signals produced by the UI.
 */
class UiTest {
    private final ByteArrayOutputStream capturedOutputStream = new ByteArrayOutputStream();
    private PrintStream originalOutputStream;
    private Ui userInterface;

    /** Redirects standard output so each UI test can inspect its console text. */
    @BeforeEach
    void redirectStandardOutput() {
        originalOutputStream = System.out;
        System.setOut(new PrintStream(capturedOutputStream, true, StandardCharsets.UTF_8));
        userInterface = new Ui(new ConsolePrinter());
    }

    /** Restores standard output after each UI test. */
    @AfterEach
    void restoreStandardOutput() {
        System.setOut(originalOutputStream);
    }

    /** Verifies the message shown for an empty task list. */
    @Test
    void showTaskList_emptyList_printsEmptyGridMessage() {
        userInterface.showTaskList(new TaskList());

        assertEquals(joinLines(
                "    Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event."),
                getCapturedOutput());
    }

    /** Verifies numbering and status text for a task list containing every task type. */
    @Test
    void showTaskList_mixedTasks_printsNumberedStatusText() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book", true),
                new Deadline("return book", LocalDate.of(2019, 6, 6)),
                new Event(
                        "project meeting",
                        new DateTimeParser.ParsedDateTime(LocalDateTime.of(2019, 8, 6, 14, 0)),
                        new DateTimeParser.ParsedDateTime(LocalDateTime.of(2019, 8, 6, 16, 0)))));

        userInterface.showTaskList(tasks);

        assertEquals(joinLines(
                "    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:",
                "    1.[T][X] read book",
                "    2.[D][ ] return book (by: Jun 06 2019)",
                "    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM"
                        + " to: Aug 06 2019, 4:00 PM)"),
                getCapturedOutput());
    }

    /** Verifies that matching dated tasks retain their numbers from the complete list. */
    @Test
    void showTasksOn_matches_printsOriginalTaskNumbers() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 3, 9, 0));
        Event event = new Event(
                "conference",
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2019, 12, 3, 23, 0)),
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2019, 12, 4, 1, 0)));
        List<TaskList.NumberedTask> matchingTasks = List.of(
                new TaskList.NumberedTask(3, deadline),
                new TaskList.NumberedTask(4, event));

        userInterface.showTasksOn(LocalDate.of(2019, 12, 3), matchingTasks);

        assertEquals(joinLines(
                "    Sally's road map! Here are the deadlines and events on Dec 03 2019:",
                "    3.[D][ ] submit report (by: Dec 03 2019, 9:00 AM)",
                "    4.[E][ ] conference (from: Dec 03 2019, 11:00 PM"
                        + " to: Dec 04 2019, 1:00 AM)"),
                getCapturedOutput());
    }

    /** Verifies the date-specific message shown when no tasks match. */
    @Test
    void showTasksOn_noMatches_printsDateSpecificMessage() {
        userInterface.showTasksOn(LocalDate.of(2019, 12, 5), List.of());

        assertEquals(joinLines("    Cruise through Radiator Springs! No deadlines or events on Dec 05 2019."),
                getCapturedOutput());
    }

    @Test
    void showSearchResults_matches_printsOriginalTaskNumbers() {
        List<TaskList.NumberedTask> matchingTasks = List.of(
                new TaskList.NumberedTask(1, new Todo("read book", true)),
                new TaskList.NumberedTask(3,
                        new Deadline("return book", LocalDate.of(2019, 6, 6))));

        userInterface.showSearchResults("book", matchingTasks);

        assertEquals(joinLines(
                "    Mater found 'em! Here are the tasks that match your search:",
                "    1.[T][X] read book",
                "    3.[D][ ] return book (by: Jun 06 2019)"),
                getCapturedOutput());
    }

    @Test
    void showSearchResults_noMatches_printsKeywordSpecificMessage() {
        userInterface.showSearchResults("tires", List.of());

        assertEquals(joinLines("    Mater checked every back road: no tasks matched \"tires\"."
                + " Try another keyword, buddy."),
                getCapturedOutput());
    }

    /** Verifies the confirmation shown after editing a task detail. */
    @Test
    void showTaskEdited_editedEvent_printsUpdatedTask() {
        Event event = new Event(
                "project meeting",
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2026, 8, 6, 14, 0)),
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2026, 8, 6, 17, 0)));

        userInterface.showTaskEdited(event);

        assertEquals(joinLines(
                "    Pit stop complete! Guido's updated this task's details:",
                "      [E][ ] project meeting (from: Aug 06 2026, 2:00 PM"
                        + " to: Aug 06 2026, 5:00 PM)"),
                getCapturedOutput());
    }

    @Test
    void showTaskAdded_singularAndPluralCounts_printsCompleteConfirmations() {
        userInterface.showTaskAdded(new Todo("first"), 1);
        userInterface.showTaskAdded(new Todo("second"), 2);
        assertEquals(joinLines(
                "    Green light, buddy! I've rolled this task onto the starting grid:",
                "      [T][ ] first", "    Your garage now holds 1 task.",
                "    Green light, buddy! I've rolled this task onto the starting grid:",
                "      [T][ ] second", "    Your garage now holds 2 tasks."), getCapturedOutput());
    }

    @Test
    void showTaskDeleted_remainingCounts_printsSingularPluralAndEmptyConfirmations() {
        for (int count : List.of(2, 1, 0)) {
            userInterface.showTaskDeleted(new Todo("removed", true), count);
        }
        assertEquals(joinLines(
                "    Mater's towing this one off the roster. Task deleted:",
                "      [T][X] removed", "    Your garage now holds 2 tasks.",
                "    Mater's towing this one off the roster. Task deleted:",
                "      [T][X] removed", "    Your garage now holds 1 task.",
                "    Mater's towing this one off the roster. Task deleted:",
                "      [T][X] removed", "    Your garage now holds 0 tasks."), getCapturedOutput());
    }

    @Test
    void showTaskMarkedAndUnmarked_printsCompleteStatusMessages() {
        userInterface.showTaskMarked(new Todo("read", true));
        userInterface.showTaskUnmarked(new Todo("read"));
        assertEquals(joinLines(
                "    Ka-chow! That's Piston Cup spirit! This task is marked done:",
                "      [T][X] read", "    Doc Hudson would be proud. One task at a time, one lap closer.",
                "    Another practice lap! Even Lightning needs those. This task is marked not done:",
                "      [T][ ] read"), getCapturedOutput());
    }

    @Test
    void showError_validationError_marksResponseAsError() {
        RecordingPrinter recordingPrinter = new RecordingPrinter();

        new Ui(recordingPrinter).showError("That command took a wrong turn.");

        assertTrue(recordingPrinter.isErrorResponse);
        assertEquals(List.of("Pit stop, buddy! Let's get you rolling. That command took a wrong turn."),
                recordingPrinter.messages);
    }

    private String getCapturedOutput() {
        userInterface.outputData();
        return capturedOutputStream.toString(StandardCharsets.UTF_8);
    }

    private String joinLines(String... lines) {
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }

    /** Captures response text and its presentation flag without starting JavaFX. */
    private static class RecordingPrinter implements Printer {
        private final List<String> messages = new ArrayList<>();
        private boolean isErrorResponse;

        @Override
        public void addData(String message) {
            messages.add(message);
        }

        @Override
        public void markResponseAsError() {
            isErrorResponse = true;
        }

        @Override
        public void outputData() {
            // This test inspects buffered state before output.
        }
    }
}
