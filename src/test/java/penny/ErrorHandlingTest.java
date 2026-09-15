package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Guards against malformed input, invalid dates, and loss of tasks after storage failures.
 */
public class ErrorHandlingTest {
    @TempDir
    Path directory;

    @Test
    void handleInput_extraWhitespace_acceptsCommands() throws PennyException {
        TaskList tasks = new TaskList();
        Parser.handleInput(tasks, "  todo    read book \t", false);
        Parser.handleInput(tasks, "\tmark    1  ", false);
        assertEquals(new ParseResult("1. [T][X] read book", false),
                Parser.handleInput(tasks, "  list  ", false));
        assertEquals(LocalDateTime.of(2026, 9, 15, 9, 0), DateTime.parse(" 15092026   0900 "));
    }

    @Test
    void handleInput_invalidSyntax_doesNotMutateTasks() {
        String[] inputs = {null, "", " \t ", "list extra", "bye extra", "find   ", "due  ", "todo  ",
            "todo first\ntodo second", "todo bad\u0000text", "mark 1 2", "delete #1", "unmark 1.0",
            "deadline work /by", "deadline work /by /by 15092026",
            "deadline work /by 15092026 /by 16092026", "deadline work /by15092026",
            "deadline work /unknown 15092026", "deadline work /from 15092026 /by 16092026",
            "event trip /from 15092026 /to", "event trip /from /to 16092026",
            "event trip /from 15092026 /from 16092026 /to 17092026",
            "event trip /from 15092026 /to 16092026 /to 17092026",
            "event trip /to 16092026 /from 15092026"};
        for (String input : inputs) {
            TaskList tasks = new TaskList();
            assertThrows(PennyException.class, () -> Parser.handleInput(tasks, input, false), input);
            assertTrue(tasks.isEmpty(), input);
        }
    }

    @Test
    void parse_impossibleDatesAndTimes_rejectsWithoutNormalizing() {
        String[] dates = {"30022026", "29022025", "31042026", "00012026", "01132026", "01010000",
            "15092026 2400", "15092026 2360", "15092026 1261", "15092026 09:00"};
        for (String date : dates) {
            assertThrows(PennyException.class, () -> DateTime.parse(date), date);
        }
    }

    @Test
    void event_invalidRange_rejectsEqualAndEarlierEnd() {
        for (String range : new String[]{"15092026 /to 15092026", "16092026 /to 15092026",
            "15092026 1000 /to 15092026 0900", "15092026 1000 /to 15092026 1000"}) {
            assertEquals("Event end must be later than its start.",
                    assertThrows(PennyException.class, () -> EventTask.create("trip /from " + range)).getMessage());
        }
    }

    @Test
    void task_validPunctuationAndDates_preservesDescriptionRule() throws PennyException {
        TaskList tasks = new TaskList();
        String description = "read https://example.com/by & notes!";
        Parser.handleInput(tasks, "deadline " + description + " /by 29022024", false);
        assertEquals(description, tasks.get(0).getDescription());
        assertThrows(PennyException.class, () -> Parser.handleInput(tasks, "todo " + description, false));
        assertEquals(1, tasks.size());
        EventTask.create("trip /from 15092026 2359 /to 16092026");
    }

    @Test
    void command_turkishLocale_recognizesEnglishCommands() throws PennyException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(Command.FIND, Command.parse("find"));
            assertEquals(Command.LIST, Command.parse("list"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void respond_failedSave_rollsBackAndAllowsRetry() throws Exception {
        Path file = directory.resolve("commands.txt");
        Penny penny = new Penny(file.toString());
        penny.respond("todo read");
        String original = Files.readString(file);
        Files.delete(file);
        Files.createDirectory(file);
        assertTrue(penny.respond("mark 1").message().contains("No changes were applied"));
        assertEquals("1. [T][ ] read", penny.respond("list").message());
        assertTrue(penny.respond("todo write").message().contains("No changes were applied"));
        assertEquals("1. [T][ ] read", penny.respond("list").message());
        Files.delete(file);
        Files.writeString(file, original);
        assertEquals("Marked as done: [T][X] read", penny.respond("mark 1").message());
        assertEquals("1. [T][X] read", new Penny(file.toString()).respond("list").message());
    }

    @Test
    void startup_corruptHistory_preservesFileAndDisablesChanges() throws Exception {
        Path file = directory.resolve("commands.txt");
        String content = "todo read\nevent bad /from 15092026 /to 15092026\nmark 1";
        Files.writeString(file, content);
        Penny penny = new Penny(file.toString());
        assertTrue(penny.getStartupWarning().contains("line 2"));
        assertEquals("1. [T][ ] read", penny.respond("list").message());
        assertTrue(penny.respond("delete 1").message().contains("Changes are disabled"));
        assertTrue(penny.respond("bye").shouldExit());
        assertEquals(content, Files.readString(file));
    }

    @Test
    void startup_unreadableContent_reportsProblemAndPreservesBytes() throws Exception {
        Path file = directory.resolve("commands.txt");
        Files.write(file, new byte[]{(byte) 0xc3, (byte) 0x28});
        Penny penny = new Penny(file.toString());
        assertTrue(penny.getStartupWarning().contains("Could not read tasks"));
        assertTrue(penny.respond("todo read").message().contains("Changes are disabled"));
        assertEquals(2, Files.size(file));
    }

    @Test
    void startup_windowsLineEndingsAndBlankLines_loadsTasks() throws Exception {
        Path file = directory.resolve("commands.txt");
        Files.writeString(file, "\r\n  todo read  \r\n\r\nmark 1\r\n");
        Penny penny = new Penny(file.toString());
        assertTrue(penny.getStartupWarning().isEmpty());
        assertEquals("1. [T][X] read", penny.respond("list").message());
        assertFalse(penny.respond(null).shouldExit());
    }

    @Test
    void respond_readOnlySave_preservesFileAndTaskState() throws Exception {
        Path file = directory.resolve("commands.txt");
        Files.writeString(file, "todo read");
        DosFileAttributeView attributes = Files.getFileAttributeView(file, DosFileAttributeView.class);
        Assumptions.assumeTrue(attributes != null, "DOS read-only attributes are unavailable");
        Penny penny = new Penny(file.toString());
        try {
            attributes.setReadOnly(true);
            Assumptions.assumeFalse(Files.isWritable(file), "Filesystem does not enforce the read-only attribute");
            assertTrue(penny.respond("delete 1").message().contains("No changes were applied"));
            assertEquals("1. [T][ ] read", penny.respond("list").message());
            assertEquals("todo read", Files.readString(file));
        } finally {
            attributes.setReadOnly(false);
        }
    }

    @Test
    void startup_duplicateDescription_stopsBeforeLaterNumberedCommands() throws Exception {
        Path file = directory.resolve("commands.txt");
        String content = "todo read\ndeadline read /by 15092026\ntodo write\nmark 2";
        Files.writeString(file, content);
        Penny penny = new Penny(file.toString());
        assertTrue(penny.getStartupWarning().contains("This task is already on your list."));
        assertEquals("1. [T][ ] read", penny.respond("list").message());
        assertEquals(content, Files.readString(file));
    }

    @Test
    void fileOperations_invalidPathAndDirectory_reportCheckedErrors() throws Exception {
        assertThrows(PennyException.class, () -> FileManager.readData("bad\u0000path"));
        assertThrows(PennyException.class, () -> FileManager.writeData("bad\u0000path", "data"));
        assertThrows(PennyException.class, () -> FileManager.readData(directory.toString()));
        assertThrows(PennyException.class, () -> FileManager.writeData(directory.toString(), "data"));
        try (var files = Files.list(directory)) {
            assertEquals(0, files.count(), "Failed writes should not leave temporary files");
        }
    }
}
