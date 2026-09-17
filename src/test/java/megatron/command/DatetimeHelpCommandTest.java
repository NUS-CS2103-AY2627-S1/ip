package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Tests help display and exit behavior by {@link DatetimeHelpCommand}.
 */
class DatetimeHelpCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_taskList_displaysHelpWithoutChangingState() throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new DatetimeHelpCommand().execute(tasks, createUi(output),
                new TaskStorage(storageFile.toString()));

        assertEquals(expectedHelpOutput(), output.toString(StandardCharsets.UTF_8));
        assertEquals(1, tasks.size());
        assertEquals("task", tasks.getTask(1).getDescription());
        assertFalse(Files.exists(storageFile));
    }

    @Test
    void isExit_datetimeHelpCommand_returnsFalse() {
        assertFalse(new DatetimeHelpCommand().isExit());
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedHelpOutput() {
        return String.join(System.lineSeparator(), List.of(
                "Initiating temporal intelligence. Supported formats:",
                "Dates with a year: yyyy-MM-dd, d/M/yyyy",
                "MMM d yyyy, MMMM d yyyy",
                "d MMM yyyy, d MMMM yyyy",
                "Dates without a year: MMM d, MMMM d",
                "d MMM, d MMMM (current year is used)",
                "Times: HHmm, H:mm, h[am|pm], h:mm[am|pm]",
                "Examples: 2145, 21:45, 9pm, 9:45pm",
                "Weekdays: mon/tue/wed/thu/fri/sat/sun",
                "Full names are also accepted, for example monday 6pm.",
                "Missing times default to 0000 (midnight).",
                "A weekday resolves to its next available occurrence.",
                "A time-only event end uses the event start date.",
                "Example: event Exam /from 6 Jul 26 1200 /to 1400",
                "The above sets an event occuring from 6 Jul 26 1200hrs to 6 Jul 26 1400hrs",
                "Output format: dd MMM uu, HHmm'hrs' (example: 24 Aug 26, 2145hrs)"))
                + System.lineSeparator();
    }
}
