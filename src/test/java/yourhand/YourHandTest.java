package yourhand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import yourhand.commands.AddTaskCommand;
import yourhand.commands.DeleteCommand;
import yourhand.commands.ExitCommand;
import yourhand.commands.FindCommand;
import yourhand.commands.HelpCommand;
import yourhand.commands.ListCommand;
import yourhand.commands.TaskStatusCommand;
import yourhand.commands.ViewScheduleCommand;
import yourhand.exceptions.CorruptFileException;
import yourhand.exceptions.InvalidCommandException;
import yourhand.exceptions.InvalidDateException;
import yourhand.exceptions.InvalidTimeException;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests command parsing, date parsing, and startup error recovery. */
class YourHandTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void parseCommand_supportedCommands_returnsExpectedCommandTypes() throws YourHandException {
        assertInstanceOf(HelpCommand.class, YourHand.parseCommand("help"));
        assertInstanceOf(ExitCommand.class, YourHand.parseCommand("bye"));
        assertInstanceOf(ListCommand.class, YourHand.parseCommand("list"));
        assertInstanceOf(FindCommand.class, YourHand.parseCommand("find book"));
        assertInstanceOf(ViewScheduleCommand.class, YourHand.parseCommand("view schedule 2026-09-10"));
        assertInstanceOf(TaskStatusCommand.class, YourHand.parseCommand("mark 1"));
        assertInstanceOf(DeleteCommand.class, YourHand.parseCommand("delete 1"));
        assertInstanceOf(AddTaskCommand.class, YourHand.parseCommand("todo read book"));
    }

    @Test
    void parseCommand_validDatedCommands_returnsAddCommand() throws YourHandException {
        assertInstanceOf(AddTaskCommand.class,
                YourHand.parseCommand("deadline report /by 2026-09-10 1800"));
        assertInstanceOf(AddTaskCommand.class,
                YourHand.parseCommand("event meeting /from 2026-09-10 /to 2026-09-11"));
    }

    @Test
    void parseCommand_blankOrNullCommand_throwsHelpfulError() {
        assertTrue(assertThrows(InvalidCommandException.class, () -> YourHand.parseCommand(" "))
                .getMessage().contains("enter a command"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand(null));
    }

    @Test
    void parseCommand_badWhitespaceOrControlCharacter_throwsHelpfulError() {
        assertTrue(assertThrows(InvalidCommandException.class, () -> YourHand.parseCommand(" todo read book"))
                .getMessage().contains("before or after"));
        assertTrue(assertThrows(InvalidCommandException.class, () -> YourHand.parseCommand("todo  read book"))
                .getMessage().contains("only one space"));
        assertTrue(assertThrows(YourHandException.class, () -> YourHand.parseCommand("todo read\nbook"))
                .getMessage().contains("control characters"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("todo read | book"));
    }

    @Test
    void parseCommand_missingParameters_throwsCommandSpecificErrors() {
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("find"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("view schedule"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("mark"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("delete"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("deadline report"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("event meeting /from 2026-09-10"));
    }

    @Test
    void parseCommand_invalidNumbersOrRepeatedParameters_throwsError() {
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("mark one"));
        assertThrows(YourHandException.class, () -> YourHand.parseCommand("delete 1 2"));
        assertThrows(YourHandException.class,
                () -> YourHand.parseCommand("deadline report /by 2026-09-10 /by 2026-09-11"));
        assertThrows(YourHandException.class,
                () -> YourHand.parseCommand("event meeting /from 2026-09-10 /from 2026-09-11 /to 2026-09-12"));
    }

    @Test
    void parseCommand_invalidTaskDateOrRange_throwsError() {
        assertThrows(YourHandException.class,
                () -> YourHand.parseCommand("deadline report /by 2026-02-30"));
        YourHandException exception = assertThrows(YourHandException.class,
                () -> YourHand.parseCommand("event meeting /from 2026-09-10 /to 2026-09-10"));
        assertTrue(exception.getMessage().contains("must end after"));
    }

    @Test
    void parseTaskDateTime_supportedFormats_returnsExpectedValues() throws YourHandException {
        assertEquals(LocalDate.of(2026, 9, 10).atStartOfDay(),
                YourHand.parseTaskDateTime("2026-9-10").getValue());
        assertEquals(LocalDateTime.of(2026, 9, 10, 18, 5),
                YourHand.parseTaskDateTime("2026-9-10 1805").getValue());
        assertEquals(LocalDateTime.of(2026, 9, 10, 18, 5),
                YourHand.parseTaskDateTime("2026-9-10 18:05").getValue());
        assertEquals(LocalDateTime.of(2026, 9, 10, 18, 5),
                YourHand.parseTaskDateTime("10/9/2026 1805").getValue());
    }

    @Test
    void parseTaskDateTime_missingOrInvalidValue_throwsHelpfulError() {
        assertThrows(YourHandException.class, () -> YourHand.parseTaskDateTime(null));
        assertTrue(assertThrows(InvalidDateException.class, () -> YourHand.parseTaskDateTime("2026-02-30"))
                .getMessage().contains("[2026-02-30]"));
        assertTrue(assertThrows(InvalidTimeException.class, () -> YourHand.parseTaskDateTime("2026-09-10 25:00"))
                .getMessage().contains("Time [25:00]"));
    }

    @Test
    void loadTasks_missingFile_returnsEmptyList() throws IOException, CorruptFileException {
        TaskList taskList = YourHand.loadTasks(
                new Storage(temporaryDirectory.resolve("missing.txt")), silentUi());

        assertTrue(taskList.isEmpty());
    }

    @Test
    void loadTasks_corruptFile_returnsEmptyListAndWarning() throws IOException {
        Path dataFile = temporaryDirectory.resolve("corrupt.txt");
        Files.writeString(dataFile, "D | 0 | report | 2026-02-30");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskList taskList = YourHand.loadTasks(new Storage(dataFile), new Ui(new PrintStream(output)));

        assertTrue(taskList.isEmpty());
        assertTrue(output.toString().contains("Man got hacked ggwp"));
    }

    @Test
    void loadTasks_unreadablePath_returnsEmptyListAndWarning() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("not-a-file");
        Files.createDirectory(directoryPath);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskList taskList = YourHand.loadTasks(new Storage(directoryPath), new Ui(new PrintStream(output)));

        assertTrue(taskList.isEmpty());
        assertTrue(output.toString().contains("Hands can't save you from load failure zzz"));
    }

    private Ui silentUi() {
        return new Ui(new PrintStream(new ByteArrayOutputStream()));
    }
}
