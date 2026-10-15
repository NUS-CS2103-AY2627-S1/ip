package zinc.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zinc.task.Deadline;
import zinc.task.Event;
import zinc.task.TaskList;
import zinc.task.Todo;

/** Tests command parsing and the tasks created by recognised commands. */
public class CommandParserTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincTasks.txt");
    private static final Path CONTACT_STORAGE_FILE = Path.of("data", "zincContacts.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
        Files.deleteIfExists(CONTACT_STORAGE_FILE);
    }

    @Test
    public void parseCommand_bye_returnsTrue() {
        CommandParser commandParser = new CommandParser(new TaskList());

        assertTrue(commandParser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_byeWithParameters_returnsFalse() {
        CommandParser commandParser = new CommandParser(new TaskList());

        assertFalse(commandParser.parseCommand("bye now"));
    }

    @Test
    public void parseCommand_byeWithMixedCaseAndWhitespace_returnsTrue() {
        CommandParser commandParser = new CommandParser(new TaskList());

        assertTrue(commandParser.parseCommand("  ByE\t"));
    }

    @Test
    public void parseCommand_emptyInput_returnsFalse() {
        CommandParser commandParser = new CommandParser(new TaskList());

        String output = captureOutput(() -> assertFalse(commandParser.parseCommand("   ")));

        assertTrue(output.contains("No command entered. Type help to see the command setlist."));
    }

    @Test
    public void parseCommand_oversizedOrUnsafeInput_printsErrorWithoutAddingTask() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        String oversizedOutput = captureOutput(() -> commandParser.parseCommand("todo " + "a".repeat(996)));
        String controlOutput = captureOutput(() -> commandParser.parseCommand("todo bad\0description"));
        String separatorOutput = captureOutput(() -> commandParser.parseCommand("todo Rock | Roll"));

        assertTrue(oversizedOutput.contains("1,000 characters or fewer"));
        assertTrue(controlOutput.contains("unsupported control character"));
        assertTrue(separatorOutput.contains("is reserved"));
        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_maximumCommandLength_isProcessed() {
        CommandParser commandParser = new CommandParser(new TaskList());

        String output = captureOutput(() -> commandParser.parseCommand("x".repeat(1000)));

        assertTrue(output.contains("I don't know that command."));
        assertFalse(output.contains("too long"));
    }

    @Test
    public void parseCommand_reservedSeparator_rejectedTaskDoesNotCorruptStoredTasks() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Safe task");

        commandParser.parseCommand("todo Rock | Roll");
        TaskList restoredTaskList = new TaskList();

        assertEquals(1, restoredTaskList.getTaskCount());
        assertEquals("Safe task", restoredTaskList.getTasks().get(0).getDescription());
        assertFalse(restoredTaskList.wasStoredDataCorrupted());
    }

    @Test
    public void parseCommand_commandPrefix_suggestsCommand() {
        CommandParser commandParser = new CommandParser(new TaskList());

        String output = captureOutput(() -> commandParser.parseCommand("to Buy bread"));

        assertTrue(output.contains("Did you mean: todo?"));
    }

    @Test
    public void parseCommand_validTodo_addsTodo() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("todo Buy bread");

        assertEquals("Buy bread", taskList.getTasks().get(0).getDescription());
        assertTrue(taskList.getTasks().get(0) instanceof Todo);
    }

    @Test
    public void parseCommand_mixedCaseTodoAndMaximumDescription_addsTodo() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        String description = "a".repeat(300);

        commandParser.parseCommand("ToDo\t" + description);

        assertEquals(description, taskList.getTasks().get(0).getDescription());
    }

    @Test
    public void parseCommand_oversizedTodoDescription_doesNotAddTodo() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        String output = captureOutput(() -> commandParser.parseCommand("todo " + "a".repeat(301)));

        assertTrue(output.contains("Task description must be 300 characters or fewer."));
        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_todoWithoutDescription_doesNotAddTodo() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("todo");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_deadlineWithOnlyDate_addsDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit report /by 27/08/26");

        Deadline deadline = (Deadline) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0), deadline.getDeadline());
    }

    @Test
    public void parseCommand_deadlineWithDateAndTime_addsDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit report /by 27/08/26 1830");

        Deadline deadline = (Deadline) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30), deadline.getDeadline());
    }

    @Test
    public void parseCommand_deadlineWithColonTimeAndFlexibleSeparator_addsDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("DEADLINE Submit report\t/BY   27/08/26   18:30");

        Deadline deadline = (Deadline) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30), deadline.getDeadline());
    }

    @Test
    public void parseCommand_deadlineWithDuplicateSeparator_doesNotAddDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit /by report /by 27/08/26");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_deadlineWithInvalidDate_doesNotAddDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit report /by 31/02/26");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_validEvent_addsEvent() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("event Meeting /from 27/08/26 1000 /to 27/08/26 1100");

        Event event = (Event) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 10, 0), event.getStart());
        assertEquals(LocalDateTime.of(2026, 8, 27, 11, 0), event.getEnd());
    }

    @Test
    public void parseCommand_eventWithColonTimesAndMixedCaseMarkers_addsEvent() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("event Meeting /FROM 27/08/26 10:00 /TO 27/08/26 11:00");

        Event event = (Event) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 10, 0), event.getStart());
        assertEquals(LocalDateTime.of(2026, 8, 27, 11, 0), event.getEnd());
    }

    @Test
    public void parseCommand_eventWithReorderedOrDuplicateMarkers_doesNotAddEvent() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("event Meeting /to 27/08/26 1100 /from 27/08/26 1000");
        commandParser.parseCommand("event Meeting /from 27/08/26 1000 /to 27/08/26 1100 /to 1200");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_deadlineOnLeapDay_acceptsOnlyLeapYear() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Valid leap day /by 29/02/24");
        commandParser.parseCommand("deadline Invalid leap day /by 29/02/25");

        assertEquals(1, taskList.getTaskCount());
        assertEquals("Valid leap day", taskList.getTasks().get(0).getDescription());
    }

    @Test
    public void parseCommand_eventEndsBeforeItStarts_doesNotAddEvent() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("event Meeting /from 27/08/26 1100 /to 27/08/26 1000");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_markAndUnmark_updatesCompletionStatus() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("mark 1");
        assertTrue(taskList.getTasks().get(0).toString().startsWith("[X]"));

        commandParser.parseCommand("unmark 1");
        assertFalse(taskList.getTasks().get(0).toString().startsWith("[X]"));
    }

    @Test
    public void parseCommand_invalidTaskNumbers_doNotChangeOrDeleteTask() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("mark -1");
        commandParser.parseCommand("mark 1.0");
        commandParser.parseCommand("mark 999999999999999999999999");
        commandParser.parseCommand("delete 0");

        assertFalse(taskList.getTasks().get(0).toString().startsWith("[X]"));
        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_taskNumberExceedsIntegerRange_printsOverflowError() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        String output = captureOutput(() -> commandParser.parseCommand("mark 999999999999999999999999"));

        assertTrue(output.contains("Task number is too large. Usage: mark <task number>"));
        assertFalse(taskList.getTasks().get(0).toString().startsWith("[X]"));
    }

    @Test
    public void parseCommand_listAliasWithArguments_printsUsage() {
        CommandParser commandParser = new CommandParser(new TaskList());

        String output = captureOutput(() -> commandParser.parseCommand("ls extra"));

        assertTrue(output.contains("does not accept extra arguments. Usage: ls"));
    }

    @Test
    public void parseCommand_delete_removesTask() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("delete 1");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_find_searchesTaskDescriptions() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");
        commandParser.parseCommand("todo Read book");

        commandParser.parseCommand("find bread");

        assertEquals("Buy bread", taskList.getTasks().get(0).getDescription());
        assertEquals("Read book", taskList.getTasks().get(1).getDescription());
    }

    @Test
    public void parseCommand_findWithoutKeyword_doesNotChangeList() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("find");

        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_contactAddWithAllFields_addsContact() {
        TaskList taskList = new TaskList();
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(taskList, contactList);

        commandParser.parseCommand("contact add /n Tom /p 91234567 /d Friend");

        assertEquals("Tom", contactList.getContacts().get(0).getName());
        assertEquals("91234567", contactList.getContacts().get(0).getPhoneNumber());
        assertEquals("Friend", contactList.getContacts().get(0).getDescription());
    }

    @Test
    public void parseCommand_contactListKeyword_findsCaseInsensitiveSubstringMatches() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Anna");
        commandParser.parseCommand("contact add /n Joanna");
        commandParser.parseCommand("contact add /n Tom");

        String output = captureOutput(() -> commandParser.parseCommand("contact list ANN"));

        assertTrue(output.contains("Name: Anna"));
        assertTrue(output.contains("Name: Joanna"));
        assertFalse(output.contains("Name: Tom"));
    }

    @Test
    public void parseCommand_contactFieldsWithMixedCase_addsContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);

        commandParser.parseCommand("CONTACT ADD /N Tom /P 91234567 /D Friend");

        assertEquals("Tom", contactList.getContacts().get(0).getName());
        assertEquals("Friend", contactList.getContacts().get(0).getDescription());
    }

    @Test
    public void parseCommand_contactAddWithUnknownOrDuplicateField_doesNotAddContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);

        commandParser.parseCommand("contact add /n Tom /x value");
        commandParser.parseCommand("contact add /n Tom /n Tommy");

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void parseCommand_contactAddWithDuplicateName_doesNotAddSecondContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Tom");

        String output = captureOutput(() -> commandParser.parseCommand("contact add /n Tom /d Other"));

        assertTrue(output.contains("A contact named \"Tom\" already exists."));
        assertEquals(1, contactList.getContactCount());
    }

    @Test
    public void parseCommand_contactAddWithOversizedFields_doesNotAddContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);

        commandParser.parseCommand("contact add /n " + "a".repeat(101));
        commandParser.parseCommand("contact add /n Tom /d " + "a".repeat(301));

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void parseCommand_contactAddWithMaximumFieldLengths_addsContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        String name = "n".repeat(100);
        String description = "d".repeat(300);

        commandParser.parseCommand("contact add /n " + name + " /d " + description);

        assertEquals(name, contactList.getContacts().get(0).getName());
        assertEquals(description, contactList.getContacts().get(0).getDescription());
    }

    @Test
    public void parseCommand_contactAddWithInvalidPhone_doesNotAddContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);

        commandParser.parseCommand("contact add /n Tom /p 1234");

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void parseCommand_contactUpdateWithSomeFields_retainsOtherFields() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Tom /p 91234567 /d Friend");

        commandParser.parseCommand("contact update Tom /n Tommy /d Best friend");

        assertEquals("Tommy", contactList.getContacts().get(0).getName());
        assertEquals("91234567", contactList.getContacts().get(0).getPhoneNumber());
        assertEquals("Best friend", contactList.getContacts().get(0).getDescription());
    }

    @Test
    public void parseCommand_contactUpdateToExistingName_doesNotUpdateContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Tom");
        commandParser.parseCommand("contact add /n Jane");

        commandParser.parseCommand("contact update Tom /n Jane");

        assertEquals("Tom", contactList.getContacts().get(0).getName());
        assertEquals("Jane", contactList.getContacts().get(1).getName());
    }

    @Test
    public void parseCommand_contactDeleteAliases_deleteContacts() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Tom");
        commandParser.parseCommand("contact add /n Jane");

        commandParser.parseCommand("contact del /n Tom");
        commandParser.parseCommand("contact delete /n Jane");

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void parseCommand_uiBackgroundAliases_selectBackground() {
        Ui ui = new Ui();
        CommandParser commandParser = new CommandParser(new TaskList(), new zinc.contact.ContactList(), ui);

        commandParser.parseCommand("ui background morning");
        assertEquals(BackgroundType.MORNING, ui.getBackgroundType());

        commandParser.parseCommand("ui bg night");
        assertEquals(BackgroundType.NIGHT, ui.getBackgroundType());
    }

    @Test
    public void parseCommand_uiBackgroundAuto_restoresAutomaticSelection() {
        Ui ui = new Ui();
        CommandParser commandParser = new CommandParser(new TaskList(), new zinc.contact.ContactList(), ui);
        commandParser.parseCommand("ui bg evening");

        commandParser.parseCommand("ui bg auto");

        assertEquals(BackgroundType.AUTO, ui.getBackgroundType());
    }

    @Test
    public void parseCommand_uiBackgroundWithInvalidArguments_keepsPreviousSelection() {
        Ui ui = new Ui();
        CommandParser commandParser = new CommandParser(new TaskList(), new zinc.contact.ContactList(), ui);
        commandParser.parseCommand("ui bg morning");

        commandParser.parseCommand("ui background dawn");
        commandParser.parseCommand("ui background night extra");

        assertEquals(BackgroundType.MORNING, ui.getBackgroundType());
    }

    @Test
    public void parseCommand_variedMalformedInputs_neverThrowsOrChangesState() {
        TaskList taskList = new TaskList();
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(taskList, contactList);
        String[] malformedInputs = {
            "bye now", "ls unexpected", "list tomorrow", "find", "mark", "unmark 1 2", "delete +1",
            "todo", "deadline /by", "deadline Report /by 99/99/99 9999",
            "event /from /to", "event Meeting /from 27/08/26 /from 28/08/26",
            "contact", "contact add", "contact add /n", "contact add /q value",
            "contact delete Tom", "contact update Tom", "ui", "ui background", "ui bg dawn extra"
        };

        for (String malformedInput : malformedInputs) {
            assertDoesNotThrow(() -> commandParser.parseCommand(malformedInput));
        }

        assertEquals(0, taskList.getTaskCount());
        assertEquals(0, contactList.getContactCount());
    }

    /** Captures text printed while the supplied command is executed. */
    private String captureOutput(Runnable command) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            command.run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
