package james;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import james.command.CommandProcessor;
import james.command.CommandResponse;
import james.command.Sticker;

/**
 * Verifies failed mutations preserve data and undo history.
 */
public class CommandRecoveryTest {
    @TempDir
    private Path directory;

    @Test
    public void process_deleteSaveFailure_restoresTaskOrderAndHistory() throws IOException {
        assertFailedMutationPreservesState("delete 1");
    }

    @Test
    public void process_unmarkSaveFailure_restoresCompletedStatusAndHistory() throws IOException {
        assertFailedMutationPreservesState("unmark 1");
    }

    @Test
    public void process_undoWhileWriterLocked_preservesStateAndHistoryForRetry() throws IOException {
        Path file = directory.resolve("tasks.txt");
        CommandProcessor processor = new CommandProcessor(file.toString());
        processor.process("todo keep");
        String before = processor.process("list").getMessage();
        byte[] original = Files.readAllBytes(file);
        try (FileChannel channel = FileChannel.open(directory.resolve("tasks.txt.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.lock()) {
            assertTrue(lock.isValid());
            CommandResponse response = processor.process("undo");
            assertEquals(CommandResponse.Type.ERROR, response.getType());
            assertEquals(Sticker.GOMEN, response.getSticker());
            assertTrue(response.getMessage().contains("Another instance"));
            assertEquals(before, processor.process("list").getMessage());
            assertArrayEquals(original, Files.readAllBytes(file));
        }
        assertEquals(Sticker.NAISU, processor.process("undo").getSticker());
        assertEquals("Let's peek in the basket. Your tasks:", processor.process("list").getMessage());
        assertEquals("", Files.readString(file));
    }

    @Test
    public void process_partialLoadErrors_blocksEveryMutationWithoutChangingData() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 1 | completed\nT | 0 | pending\nbroken record\n");
        byte[] original = Files.readAllBytes(file);
        CommandProcessor processor = new CommandProcessor(file.toString());
        String before = processor.process("list").getMessage();
        for (String command : new String[]{"todo new", "delete 1", "mark 2", "unmark 1"}) {
            CommandResponse response = processor.process(command);
            assertEquals(CommandResponse.Type.ERROR, response.getType(), command);
            assertEquals(Sticker.GOMEN, response.getSticker(), command);
            assertEquals("Gomen! A little flour in the gears.\n" +
                    "Saved tasks could not be fully loaded. No changes were made.\n" +
                    "Repair the saved file or restore read access, then restart James.", response.getMessage());
            assertFalse(response.isExit());
            assertEquals(before, processor.process("list").getMessage(), command);
            assertArrayEquals(original, Files.readAllBytes(file), command);
            assertEquals("Not a crumb to retrace. Nothing to undo.", processor.process("undo").getMessage());
        }
    }

    @Test
    public void process_restartAfterRepair_allowsChangesWithoutWarning() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken record\n");
        CommandProcessor original = new CommandProcessor(file.toString());
        assertEquals(CommandResponse.Type.ERROR, original.process("todo rejected").getType());
        Files.writeString(file, "T | 0 | repaired\n");
        CommandProcessor restarted = new CommandProcessor(file.toString());
        assertEquals("", restarted.getLoadWarning());
        assertEquals(CommandResponse.Type.ADD, restarted.process("todo new").getType());
        assertEquals("Let's peek in the basket. Your tasks:\n1.[T][ ] repaired\n2.[T][ ] new",
                new CommandProcessor(file.toString()).process("list").getMessage());
    }

    /**
     * Holds the writer lock to force a recoverable failure, then verifies the previous undo entry.
     */
    private void assertFailedMutationPreservesState(String command) throws IOException {
        Path file = directory.resolve("tasks.txt");
        CommandProcessor processor = new CommandProcessor(file.toString());
        processor.process("todo first");
        processor.process("todo second");
        String unmarked = processor.process("list").getMessage();
        processor.process("mark 1");
        String before = processor.process("list").getMessage();
        byte[] original = Files.readAllBytes(file);
        try (FileChannel channel = FileChannel.open(directory.resolve("tasks.txt.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.lock()) {
            assertTrue(lock.isValid());
            CommandResponse response = processor.process(command);
            assertEquals(CommandResponse.Type.ERROR, response.getType());
            assertEquals(Sticker.GOMEN, response.getSticker());
            assertEquals(CommandResponse.DisplayMode.STICKER_WITH_TEXT, response.getDisplayMode());
            assertTrue(response.getMessage().contains("Another instance"));
            assertFalse(response.isExit());
            assertEquals(before, processor.process("list").getMessage());
            assertArrayEquals(original, Files.readAllBytes(file));
        }
        assertEquals(Sticker.NAISU, processor.process("undo").getSticker());
        assertEquals(unmarked, processor.process("list").getMessage());
        assertEquals(unmarked, new CommandProcessor(file.toString()).process("list").getMessage());
    }
}
