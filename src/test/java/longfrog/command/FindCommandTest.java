package longfrog.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import longfrog.task.Deadline;
import longfrog.task.TaskList;
import longfrog.task.Todo;
import longfrog.ui.Ui;

class FindCommandTest {
    @Test
    void execute_keywordMatchesOneDescription_usesSingularMatchLabel() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new FindCommand(taskList, "read").execute(new Ui());
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput = String.join(System.lineSeparator(),
                "Found 1 matching ripple:",
                "1: [T][ ] read book",
                "");
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_keywordMatchesDescriptionsCaseInsensitively_preservesTaskOrder() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Deadline secondTask = new Deadline("return book", java.time.LocalDateTime.of(2019, 12, 2, 18, 0));
        taskList.addTask(firstTask);
        taskList.addTask(new Todo("go running"));
        taskList.addTask(secondTask);
        firstTask.markAsDone();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        boolean shouldExit;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            shouldExit = new FindCommand(taskList, "BOOK").execute(new Ui());
        } finally {
            System.setOut(originalOutput);
        }

        String expectedOutput = String.join(System.lineSeparator(),
                "Found 2 matching ripples:",
                "1: [T][X] read book",
                "2: [D][ ] return book (by: Dec 02 2019, 6:00 pm)",
                "");
        assertFalse(shouldExit);
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_keywordMatchesNoDescriptions_displaysNoMatchMessage() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new FindCommand(taskList, "holiday").execute(new Ui());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("No ripples for “holiday”." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }
}
