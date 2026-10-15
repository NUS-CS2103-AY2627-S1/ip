package james;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import james.command.CommandProcessor;
import james.command.CommandResponse;
import james.command.Sticker;

/**
 * Tests command processing across the shared application backend.
 */
public class CommandProcessorTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that adding and listing a task updates the shared state.
     */
    @Test
    public void process_addThenList_returnsUpdatedTaskList() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());

        CommandResponse addResponse = processor.process("todo revise Java");
        CommandResponse listResponse = processor.process("list");

        assertEquals(CommandResponse.Type.ADD, addResponse.getType());
        assertEquals("Let's peek in the basket. Your tasks:\n1.[T][ ] revise Java", listResponse.getMessage());
    }

    /**
     * Verifies that invalid input becomes a styled error response.
     */
    @Test
    public void process_invalidCommand_returnsErrorResponse() {
        CommandResponse response = new CommandProcessor(getStoragePath()).process("unknown");

        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertEquals("Gomen! A little flour in the gears.\nI don't know that recipe! Try list or todo <description>.",
                response.getMessage());
        assertFalse(response.isExit());
    }

    /**
     * Verifies that mutations persist when a new processor is created.
     */
    @Test
    public void process_addThenReload_persistsTask() {
        String storagePath = getStoragePath();
        new CommandProcessor(storagePath).process("deadline submit report /by 2026-09-07");

        CommandResponse response = new CommandProcessor(storagePath).process("list");

        assertTrue(response.getMessage().contains("submit report"));
    }

    /**
     * Verifies that the bye command requests application termination.
     */
    @Test
    public void process_bye_returnsExitResponse() {
        CommandResponse response = new CommandProcessor(getStoragePath()).process("bye");

        assertTrue(response.isExit());
        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals("Mata ne! Rest up. I smell fresh bread!", response.getMessage());
    }

    @Test
    public void process_undoMixedChanges_restoresDetailsAndOrder() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo read");
        processor.process("deadline report /by 2026-09-15");
        processor.process("event meeting /from 2026-09-15 /to 2026-09-16");
        String original = processor.process("list").getMessage();
        processor.process("mark 2");
        String marked = processor.process("list").getMessage();
        processor.process("delete 2");
        processor.process("undo");
        assertEquals(marked, processor.process("list").getMessage());
        processor.process("unmark 2");
        processor.process("undo");
        assertEquals(marked, processor.process("list").getMessage());
        processor.process("undo");
        assertEquals(original, processor.process("list").getMessage());
        processor.process("undo");
        assertFalse(processor.process("list").getMessage().contains("meeting"));
        processor.process("undo");
        assertFalse(processor.process("list").getMessage().contains("report"));
        processor.process("undo");
        assertEquals("Let's peek in the basket. Your tasks:", processor.process("list").getMessage());
        assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
    }

    @Test
    public void process_readOnlyInvalidAndNoOpCommands_preservesUndoHistory() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo read");
        processor.process("unmark 1");
        processor.process("mark 1");
        processor.process("mark 1");
        processor.process("find read");
        processor.process("list_by_date 2026-09-15");
        processor.process("delete 99");
        processor.process("todo");
        assertEquals(CommandResponse.Type.ERROR, processor.process("undo 1").getType());
        processor.process("undo");
        assertTrue(processor.process("list").getMessage().contains("[ ] read"));
        processor.process("undo");
        assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
    }

    @Test
    public void process_moreThanTwentyChanges_limitsHistoryAndSupportsNewChanges() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        for (int i = 0; i < 21; i++) {
            processor.process("todo task " + i);
        }
        for (int i = 0; i < 20; i++) {
            processor.process("undo");
        }
        assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
        assertEquals("Let's peek in the basket. Your tasks:\n1.[T][ ] task 0",
                processor.process("list").getMessage());
        processor.process("todo new task");
        processor.process("undo");
        assertFalse(processor.process("list").getMessage().contains("new task"));
    }

    @Test
    public void process_undoThenRestart_persistsRestorationWithoutHistory() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo read");
        processor.process("mark 1");
        processor.process("undo");
        CommandProcessor restarted = new CommandProcessor(getStoragePath());
        assertTrue(restarted.process("list").getMessage().contains("[ ] read"));
        assertEquals("Not a crumb to retrace. Nothing to undo.", restarted.process("undo").getMessage());
    }

    @Test
    public void process_saveFailure_preservesStateAndUndoForRetry() throws IOException {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo read");
        Path destination = Path.of(getStoragePath());
        Files.delete(destination);
        Files.createDirectory(destination);
        Files.writeString(destination.resolve("blocker"), "prevent replacement");
        assertEquals(CommandResponse.Type.ERROR, processor.process("mark 1").getType());
        assertTrue(processor.process("list").getMessage().contains("[ ] read"));
        assertEquals(CommandResponse.Type.ERROR, processor.process("undo").getType());
        assertTrue(processor.process("list").getMessage().contains("read"));
        Files.delete(destination.resolve("blocker"));
        Files.delete(destination);
        processor.process("undo");
        assertEquals("Let's peek in the basket. Your tasks:", processor.process("list").getMessage());
        assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
    }

    private String getStoragePath() {
        return temporaryDirectory.resolve("james.txt").toString();
    }

    /**
     * Tests that mutation confirmations report the size after the operation.
     */
    @Test
    public void process_addThenDelete_reportsUpdatedTaskCounts() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());

        CommandResponse added = processor.process("todo revise Java");
        CommandResponse deleted = processor.process("delete 1");

        assertEquals("Hehe! Tucked this task into my bread basket:\n[T][ ] revise Java" +
                "\nNow you have 1 tasks in the list.", added.getMessage());
        assertEquals("Poof! Snatched this task out of the basket:\n[T][ ] revise Java" +
                "\nNow you have 0 tasks in the list.", deleted.getMessage());
    }

    @Test
    public void process_markTask_returnsCompletedTaskMessage() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo buy bread");

        CommandResponse response = processor.process("mark 1");

        assertEquals(CommandResponse.Type.MARK, response.getType());
        assertEquals("Yatta! Task done. Time for a bread break:\n[T][X] buy bread", response.getMessage());
    }

    @Test
    public void process_unmarkTask_returnsIncompleteTaskMessage() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo buy bread");
        processor.process("mark 1");

        CommandResponse response = processor.process("unmark 1");

        assertEquals(CommandResponse.Type.MARK, response.getType());
        assertEquals("Back in the oven! This task is not done yet:\n[T][ ] buy bread", response.getMessage());
    }

    @Test
    public void process_findMatchingTasks_returnsNumberedMatches() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo buy bread");
        processor.process("todo read book");
        processor.process("deadline return book /by 2026-06-06");

        CommandResponse response = processor.process("find BOOK");

        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals("Sniff sniff... here are the matching tasks:\n1.[T][ ] read book" +
                "\n2.[D][ ] return book (by: Jun 06 2026)", response.getMessage());
    }

    @Test
    public void process_findWithoutMatches_returnsHeadingOnly() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo read book");

        CommandResponse response = processor.process("find magazine");

        assertEquals("Sniff sniff... here are the matching tasks:", response.getMessage());
    }

    @Test
    public void process_listByDateWithMatches_returnsNumberedMatches() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo read book");
        processor.process("deadline return book /by 2026-10-15");
        processor.process("event conference /from 2026-10-14 /to 2026-10-16");

        CommandResponse response = processor.process("list_by_date 2026-10-15");

        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals("Tasks on the menu for 2026-10-15:" +
                "\n1.[D][ ] return book (by: Oct 15 2026)" +
                "\n2.[E][ ] conference (from: Oct 14 2026 to: Oct 16 2026)", response.getMessage());
    }

    @Test
    public void process_listByDateWithoutMatches_returnsHeadingOnly() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("deadline return book /by 2026-10-15");

        CommandResponse response = processor.process("list_by_date 2026-10-16");

        assertEquals("Tasks on the menu for 2026-10-16:", response.getMessage());
    }
    @Test
    public void process_invalidInputs_preservesTasksAndHistory() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("todo keep");
        String[] invalidInputs = {
            "todo keep", "todo bad | description", "todo bad\nrecord", "list extra", "bye extra",
            "deadline report /by 2026-02-30", "deadline report /by 2026-09-16 /by 2026-09-17",
            "event trip /from 2026-09-16 /to 2026-09-16",
            "event trip /from 2026-09-17 /to 2026-09-16",
            "event trip /to 2026-09-17 /from 2026-09-16",
            "event trip /from 2026-09-16 /from 2026-09-17 /to 2026-09-18",
            "mark 99999999999999999999", "delete 1 1", "deadline report /until 2026-09-16"
        };
        String[] expectedErrors = {
            "Already in the basket! This task exists in your list.",
            "Task descriptions cannot contain | or control characters.",
            "Task descriptions cannot contain | or control characters.",
            "LIST does not take arguments.", "BYE does not take arguments.",
            "Formatting of the date is incorrect, try: yyyy-mm-dd",
            "Date options must appear once and in order: /by",
            "An event must end after its start date.", "An event must end after its start date.",
            "Date options must appear once and in order: /from /to",
            "Date options must appear once and in order: /from /to",
            "No half-slices here! The task number must be a whole number.\nTry: mark <task number>",
            "No half-slices here! The task number must be a whole number.\nTry: delete <task number>",
            "A deadline needs a by date.\nTry: deadline <description> /by <date>"
        };
        String originalList = processor.process("list").getMessage();
        for (int i = 0; i < invalidInputs.length; i++) {
            String input = invalidInputs[i];
            CommandResponse response = processor.process(input);
            assertEquals(CommandResponse.Type.ERROR, response.getType(), input);
            assertEquals("Gomen! A little flour in the gears.\n" + expectedErrors[i], response.getMessage(), input);
            assertFalse(response.isExit(), input);
            assertEquals(originalList, processor.process("list").getMessage(), input);
        }
        assertEquals("Let's peek in the basket. Your tasks:\n1.[T][ ] keep",
                processor.process("list").getMessage());
        processor.process("undo");
        assertEquals("Let's peek in the basket. Your tasks:", processor.process("list").getMessage());
    }

    @Test
    public void process_whitespaceBetweenArguments_acceptsCommands() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        assertEquals(CommandResponse.Type.ADD,
                processor.process("  deadline\t report   /by\t2026-09-16  ").getType());
        assertEquals(CommandResponse.Type.ADD,
                processor.process("event\ttrip\t/from  2026-09-16\t/to  2026-09-17").getType());
    }

    @Test
    public void process_duplicateCompletedTask_rejectsButAllowsDifferentDates() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("deadline report /by 2026-09-16");
        processor.process("mark 1");
        assertEquals(CommandResponse.Type.ERROR,
                processor.process("deadline report /by 2026-09-16").getType());
        assertEquals(CommandResponse.Type.ADD,
                processor.process("deadline report /by 2026-09-17").getType());
    }

    @Test
    public void process_corruptedStorage_explainsRecoveryAndPreservesData() throws IOException {
        Path file = Path.of(getStoragePath());
        Files.writeString(file, "broken record\n");
        CommandProcessor processor = new CommandProcessor(file.toString());
        CommandResponse response = processor.process("todo keep");
        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertTrue(response.getMessage().contains("restart James"));
        assertEquals(Sticker.GOMEN, response.getSticker());
        assertEquals("broken record\n", Files.readString(file));
        assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
    }

    @Test
    public void process_randomStickerControlledIndexes_returnsEveryPackagedSticker() throws IOException {
        String[] names = {"nankore", "otsu", "naisu", "gomen", "yatta"};
        for (int i = 0; i < names.length; i++) {
            final int selectedIndex = i;
            RandomGenerator generator = new RandomGenerator() {
                @Override
                public long nextLong() {
                    throw new AssertionError("Selection should use nextInt(bound)");
                }

                @Override
                public int nextInt(int bound) {
                    assertEquals(names.length, bound);
                    return selectedIndex;
                }
            };
            CommandProcessor processor = new CommandProcessor(getStoragePath(), generator);
            CommandResponse response = processor.process("  RANDOM_STICKER  ");
            String expectedPath = "/images/" + names[i] + "_pandorobou.png";
            assertEquals(expectedPath, response.getStickerPath());
            assertEquals(CommandResponse.DisplayMode.STICKER_ONLY, response.getDisplayMode());
            assertEquals("Hehe! A little treat from my secret stash!", response.getMessage());
            assertEquals(CommandResponse.Type.NORMAL, response.getType());
            assertFalse(response.isExit());
            try (var stream = getClass().getResourceAsStream(expectedPath)) {
                assertNotNull(stream);
                assertNotNull(javax.imageio.ImageIO.read(stream));
            }
            assertEquals(expectedPath, processor.process("random_sticker").getStickerPath());
        }
    }

    @Test
    public void process_randomSticker_preservesTasksStorageAndUndo() throws IOException {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        processor.process("random_sticker");
        assertFalse(Files.exists(Path.of(getStoragePath())));
        assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
        processor.process("todo keep");
        String saved = Files.readString(Path.of(getStoragePath()));
        String tasks = processor.process("list").getMessage();
        processor.process("random_sticker");
        assertEquals(saved, Files.readString(Path.of(getStoragePath())));
        assertEquals(tasks, processor.process("list").getMessage());
        processor.process("undo");
        assertEquals("Let's peek in the basket. Your tasks:", processor.process("list").getMessage());
    }

    @Test
    public void process_randomStickerExtraArguments_returnsConfusedStickerAndError() {
        CommandProcessor processor = new CommandProcessor(getStoragePath());
        CommandResponse response = processor.process("random_sticker extra");
        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertEquals("Gomen! A little flour in the gears.\nRANDOM_STICKER does not take arguments.",
                response.getMessage());
        assertEquals(Sticker.NANKORE, response.getSticker());
        assertEquals(CommandResponse.DisplayMode.STICKER_WITH_TEXT, response.getDisplayMode());
    }

}
