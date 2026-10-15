package tuesday.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tuesday.exceptions.TuesdayExceptions;
import tuesday.task.Task;
import tuesday.task.TaskList;
import tuesday.task.Todo;
import tuesday.ui.Strings;

/**
 * Preserves command validation responses, error precedence, and task state.
 */
public class ValidationTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void processCommand_missingEventTimes_explainsFieldsAndPreservesState() throws Exception {
        String[] inputs = {
            "event work",
            "event work /to 11:00",
            "event work /from 10:00 /to",
            "event work /from/to 11:00",
            "event work /from /to"
        };
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        CommandProcessor commandProcessor = new CommandProcessor(saveFile.toString());
        TaskList tasks = new TaskList();
        Task existingTask = new Todo("existing");
        tasks.add(existingTask);

        for (String input : inputs) {
            String response = commandProcessor.processCommand(input, tasks);
            assertTrue(response.startsWith("Sir, please provide /"), input);
            assertTrue(response.contains("YYYY-MM-DD, HH:mm (24-hour), or YYYY-MM-DD HH:mm."), input);
            assertTrue(response.contains("Example: event meeting /from 14:00 /to 15:00"), input);
            assertThrows(TuesdayExceptions.EventMissingTimeException.class, () ->
                    UserInputParser.isAvailableTaskCommand(input), input);
        }
        assertEquals(1, tasks.size());
        assertSame(existingTask, tasks.get(0));
        assertFalse(Files.exists(saveFile));
    }

    @Test
    public void processCommand_invalidInputs_preservesResponseAndState() throws IOException {
        String[][] cases = {
            {"todo", "please add description, sir!"},
            {"deadline", "please add description, sir!"},
            {"deadline /by 2026-09-20", "please add description, sir!"},
            {"deadline work", "Sir, please provide /by"},
            {"deadline work /by", "Sir, please provide /by"},
            {"deadline work /by nonsense", "Sir, the value after /by is not a valid date or time."},
            {"event", "please add description, sir!"},
            {"event /from 10:00 /to 11:00", "please add description, sir!"},
            {"event work /from 10:00", "Sir, please provide /to"},
            {"event work /to 11:00 /from 10:00", "Sir, include /from followed by /to, each exactly once."},
            {"event work /from /to 11:00", "Sir, please provide /from"},
            {"event work /from 10:00 /from 10:30 /to 11:00",
                "Sir, include /from followed by /to, each exactly once."},
            {"event work /from 10:00 /to 11:00 /to 12:00", "Sir, include /from followed by /to"},
            {"deadline work /by 10:00 /by 11:00", "Sir, include /by exactly once."},
            {"event work /from nonsense /to 11:00", "Sir, the value after /from is not a valid date or time."},
            {"event work /from 10:00 /to 25:00", "Sir, the value after /to is not a valid date or time."}
        };
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "original bytes");
        TaskList tasks = new TaskList();
        Task existing = new Todo("existing");
        tasks.add(existing);
        CommandProcessor commandProcessor = new CommandProcessor(file.toString());
        for (String[] example : cases) {
            CommandResponse response = commandProcessor.processResponse(example[0], tasks);
            assertTrue(response.text().startsWith(example[1]), example[0]);
            assertFalse(response.showUserGuide(), example[0]);
        }
        for (String input : new String[] {null, "", "  "}) {
            assertEquals(new CommandResponse(Strings.EMPTY_COMMAND, false),
                    commandProcessor.processResponse(input, tasks));
        }
        for (String input : new String[] {"old", "new"}) {
            assertEquals(new CommandResponse(Strings.DUPLICATE_CHOICE_UNAVAILABLE, false),
                    commandProcessor.processResponse(input, tasks));
        }
        assertEquals(1, tasks.size());
        assertSame(existing, tasks.get(0));
        assertEquals("[T][ ] existing", existing.toString());
        assertEquals("original bytes", Files.readString(file));
    }

    @Test
    public void processCommand_invalidIndices_preservesErrorPrecedence() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("existing"));
        CommandProcessor commandProcessor = new CommandProcessor(temporaryDirectory.resolve("tasks.txt").toString());
        for (String command : new String[] {"mark", "unmark", "delete"}) {
            String range = "Sir, that " + (command.equals("delete") ? "delete" : "mark")
                    + " number is out of range.";
            for (String argument : new String[] {"0", "-1", "2", "-2147483648", "2147483647"}) {
                assertEquals(range, commandProcessor.processCommand(command + " " + argument, tasks));
            }
            for (String argument : new String[] {"", "abc", "2147483648", "-2147483649", "1 extra", "0 extra"}) {
                String input = command + " " + argument;
                assertEquals("Sir, provide one whole task number with no extra arguments.\nFormat: "
                        + command + " NUMBER\nExample: " + command + " 1",
                        commandProcessor.processCommand(input, tasks));
            }
            assertEquals(range, commandProcessor.processCommand(command + " 1", new TaskList()));
        }
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] existing", tasks.get(0).toString());
    }

    @Test
    public void processCommand_fullList_validatesBeforeCapacity() {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new Todo("task " + i));
        }
        CommandProcessor commandProcessor = new CommandProcessor(temporaryDirectory.resolve("tasks.txt").toString());
        assertEquals("please add description, sir!", commandProcessor.processCommand("todo", tasks));
        assertEquals("Sir, this will cost too much time", commandProcessor.processCommand("todo task 0", tasks));
        assertEquals(Strings.DUPLICATE_CHOICE_UNAVAILABLE, commandProcessor.processCommand("old", tasks));
    }

    @Test
    public void processCommand_capacityBoundary_acceptsLastSlotOnly() throws IOException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 99; i++) {
            tasks.add(new Todo("task " + i));
        }
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage.modifyData(file.toString(), tasks);
        CommandProcessor commandProcessor = new CommandProcessor(file.toString());
        assertTrue(commandProcessor.processCommand("todo last slot", tasks).startsWith("Got it. I've added"));
        String saved = Files.readString(file);
        assertEquals(100, tasks.size());
        assertEquals("Sir, this will cost too much time", commandProcessor.processCommand("todo overflow", tasks));
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void processResponse_unknownCommand_includesGuideWithoutChangingState() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "original bytes");
        CommandProcessor processor = new CommandProcessor(file.toString());
        TaskList tasks = new TaskList();
        for (String input : new String[] {"task abc", " task abc ", "task"}) {
            CommandResponse response = processor.processResponse(input, tasks);
            assertEquals("Sir, I don't recognize the command \"task\".", response.text());
            assertTrue(response.showUserGuide());
            assertEquals(response.text() + "\nUser guide: https://soy046.github.io/ip/", response.toPlainText());
            assertEquals(response.toPlainText(), processor.processCommand(input, tasks));
        }
        assertEquals(0, tasks.size());
        assertEquals("original bytes", Files.readString(file));
    }

    @Test
    public void processResponse_pendingDuplicate_preservesChoiceInstructions() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        CommandProcessor processor = new CommandProcessor(file.toString());
        TaskList tasks = new TaskList();
        processor.processResponse("todo work", tasks);
        String saved = Files.readString(file);
        processor.processResponse("todo WORK", tasks);
        for (String input : new String[] {"task abc", "", "new extra", "list"}) {
            assertEquals(new CommandResponse(Strings.DUPLICATE_CHOICE_INSTRUCTION, false),
                    processor.processResponse(input, tasks));
        }
        assertTrue(processor.processResponse("old", tasks).text().startsWith("Okay."));
        assertEquals(1, tasks.size());
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void processCommand_invalidDateTimes_givesFormatsAndExampleWithoutSaving() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        CommandProcessor processor = new CommandProcessor(file.toString());
        TaskList tasks = new TaskList();
        for (String value : new String[] {"tomorrow", "2026-02-30", "25:00", "9am", "2026/09/20"}) {
            String response = processor.processCommand("deadline work /by " + value, tasks);
            assertEquals("Sir, the value after /by is not a valid date or time.\n"
                    + "Format: deadline DESCRIPTION /by WHEN\n"
                    + "Use YYYY-MM-DD, HH:mm (24-hour), or YYYY-MM-DD HH:mm.\n"
                    + "Use leading zeros and one space between the date and time.\n"
                    + "Example: deadline work /by 2026-09-20 18:00", response);
        }
        assertEquals(0, tasks.size());
        assertFalse(Files.exists(file));
    }
}
