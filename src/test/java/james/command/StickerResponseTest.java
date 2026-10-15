package james.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies sticker selection from command outcomes rather than response wording.
 */
public class StickerResponseTest {
    @TempDir
    private Path directory;

    @Test
    public void process_successfulChanges_returnsMappedStickers() {
        CommandProcessor processor = new CommandProcessor(directory.resolve("tasks.txt").toString());
        assertSticker(processor, "todo read", Sticker.YATTA);
        assertSticker(processor, "deadline report /by 2026-09-16", Sticker.YATTA);
        assertSticker(processor, "event trip /from 2026-09-16 /to 2026-09-17", Sticker.YATTA);
        assertSticker(processor, "mark 1", Sticker.NAISU);
        assertSticker(processor, "mark 1", Sticker.NAISU);
        assertSticker(processor, "unmark 1", Sticker.OTSU);
        assertSticker(processor, "unmark 1", Sticker.OTSU);
        assertSticker(processor, "delete 1", Sticker.NAISU);
        assertSticker(processor, "undo", Sticker.NAISU);
    }

    @Test
    public void process_queriesWithAndWithoutResults_selectsAcknowledgmentOrApology() {
        CommandProcessor processor = new CommandProcessor(directory.resolve("tasks.txt").toString());
        assertSticker(processor, "list", Sticker.GOMEN);
        assertSticker(processor, "undo", Sticker.GOMEN);
        assertSticker(processor, "find report", Sticker.GOMEN);
        assertSticker(processor, "list_by_date 2026-09-16", Sticker.GOMEN);
        processor.process("deadline report /by 2026-09-16");
        assertSticker(processor, "list", Sticker.OTSU);
        assertSticker(processor, "find report", Sticker.OTSU);
        assertSticker(processor, "list_by_date 2026-09-16", Sticker.OTSU);
        assertSticker(processor, "find missing", Sticker.GOMEN);
        assertSticker(processor, "list_by_date 2026-09-18", Sticker.GOMEN);
    }

    @Test
    public void process_invalidInput_overridesSuccessStickerAndPreservesErrorText() {
        CommandProcessor processor = new CommandProcessor(directory.resolve("tasks.txt").toString());
        String[] commands = {"unknown", "", "todo", "deadline report /by wrong", "event trip",
            "mark 99", "unmark 99", "delete 99", "find", "list extra", "undo extra",
            "list_by_date wrong", "random_sticker extra", "bye extra"};
        for (String command : commands) {
            CommandResponse response = processor.process(command);
            assertEquals(Sticker.NANKORE, response.getSticker(), command);
            assertEquals(CommandResponse.Type.ERROR, response.getType(), command);
            assertEquals(CommandResponse.DisplayMode.STICKER_WITH_TEXT, response.getDisplayMode());
        }
        assertEquals("Gomen! A little flour in the gears.\nI don't know that recipe! Try list or todo <description>.",
                processor.process("unknown").getMessage());
    }

    @Test
    public void process_duplicateAndStorageFailures_returnsApologyInsteadOfSuccess() throws IOException {
        Path file = directory.resolve("tasks.txt");
        CommandProcessor processor = new CommandProcessor(file.toString());
        processor.process("todo keep");
        assertSticker(processor, "todo keep", Sticker.GOMEN);
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "prevent replacement");
        assertSticker(processor, "mark 1", Sticker.GOMEN);
        assertSticker(processor, "todo another", Sticker.GOMEN);
        assertSticker(processor, "undo", Sticker.GOMEN);
        assertTrue(processor.process("list").getMessage().contains("[ ] keep"));
    }

    @Test
    public void create_greetingAndExit_remainsTextOnly() {
        CommandResponse greeting = new CommandResponse("Hello!", CommandResponse.Type.NORMAL, false);
        CommandResponse exit = new CommandProcessor(directory.resolve("tasks.txt").toString()).process("bye");
        assertEquals(CommandResponse.DisplayMode.TEXT_ONLY, greeting.getDisplayMode());
        assertEquals(CommandResponse.DisplayMode.TEXT_ONLY, exit.getDisplayMode());
        assertNull(greeting.getStickerPath());
        assertNull(exit.getSticker());
        assertTrue(exit.isExit());
    }

    /**
     * Checks that a command returns a sticker followed by its response text.
     */
    private void assertSticker(CommandProcessor processor, String command, Sticker expected) {
        CommandResponse response = processor.process(command);
        assertEquals(expected, response.getSticker(), command);
        assertEquals(expected.getResourcePath(), response.getStickerPath(), command);
        assertEquals(CommandResponse.DisplayMode.STICKER_WITH_TEXT, response.getDisplayMode(), command);
    }
}
