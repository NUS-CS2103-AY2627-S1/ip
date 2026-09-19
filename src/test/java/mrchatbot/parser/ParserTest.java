package mrchatbot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mrchatbot.command.AddCommand;
import mrchatbot.command.Command;
import mrchatbot.command.FindCommand;
import mrchatbot.command.ListCommand;
import mrchatbot.command.MarkCommand;
import mrchatbot.exception.MrChatbotException;
import mrchatbot.task.Task;

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void createTask_eventEndNotAfterStart_exceptionThrown() {
        String[] inputs = {
            "event meeting /from 2026-09-21 /to 2026-09-20",
            "event meeting /from 2026-09-21 /to 2026-09-21",
            "event meeting /to 2026-09-20 /from 2026-09-21"
        };
        for (String input : inputs) {
            MrChatbotException exception = assertThrows(
                    MrChatbotException.class, () -> parser.createTask(input), input);

            assertEquals("Event end date must be after its start date. "
                    + "Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>",
                    exception.getMessage());
        }
    }

    @Test
    public void parseCommand_listCommand_listCommandReturned() throws MrChatbotException {
        Command command = parser.parseCommand("list");

        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parseCommand_markCommand_markCommandReturned() throws MrChatbotException {
        Command command = parser.parseCommand("mark 1");

        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parseCommand_todoCommand_addCommandReturned() throws MrChatbotException {
        Command command = parser.parseCommand("todo read book");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parseCommand_findCommand_findCommandReturned() throws MrChatbotException {
        Command command = parser.parseCommand("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parseCommand_bareMarkCommand_exceptionThrown() {
        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> parser.parseCommand("mark"));

        assertEquals("Sorry, I don't understand that task format.", exception.getMessage());
    }

    @Test
    public void parseCommand_blankFindKeyword_exceptionThrown() {
        MrChatbotException exception = assertThrows(
                MrChatbotException.class, () -> parser.parseCommand("find   "));

        assertEquals("Find keyword cannot be empty. Please use the format: find <keyword>", exception.getMessage());
    }

    @Test
    public void parseCommand_bareFindCommand_exceptionThrown() {
        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> parser.parseCommand("find"));

        assertEquals("Find keyword cannot be empty. Please use the format: find <keyword>", exception.getMessage());
    }

    @Test
    public void createTask_validTodo_todoCreated() throws MrChatbotException {
        Task task = parser.createTask("todo read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void createTask_validDeadline_deadlineCreatedWithFormattedDate() throws MrChatbotException {
        Task task = parser.createTask("deadline return book /by 2019-12-01");

        assertEquals("[D][ ] return book (by: Dec 1 2019)", task.toString());
    }

    @Test
    public void createTask_validEvent_eventCreatedWithFormattedDates() throws MrChatbotException {
        Task task = parser.createTask("event project meeting /from 2019-12-01 /to 2019-12-02");

        assertEquals("[E][ ] project meeting (from: Dec 1 2019 to: Dec 2 2019)", task.toString());
    }

    @Test
    public void createTask_unknownCommand_exceptionThrown() {
        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> parser.createTask("dance"));

        assertEquals("Sorry, I don't understand that command. Please type \"help\".", exception.getMessage());
    }

    @Test
    public void createTask_invalidDeadlineDate_exceptionThrown() {
        MrChatbotException exception = assertThrows(
                MrChatbotException.class, () -> parser.createTask("deadline return book /by 12/01/2019"));

        assertEquals("Deadline date must be in yyyy-mm-dd format. "
                + "Please use the format: deadline <description> /by <yyyy-mm-dd>", exception.getMessage());
    }

    @Test
    public void createTask_missingEventTo_exceptionThrown() {
        MrChatbotException exception = assertThrows(
                MrChatbotException.class, () -> parser.createTask("event project meeting /from 2019-12-01"));

        assertEquals("Event /to cannot be empty. "
                + "Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>",
                exception.getMessage());
    }

    @Test
    public void createTask_reversedEventDelimiters_datesAssignedToCorrectFields() throws MrChatbotException {
        Task task = parser.createTask("EVENT project meeting /TO 2019-12-02 /FROM 2019-12-01");

        assertEquals("[E][ ] project meeting (from: Dec 1 2019 to: Dec 2 2019)", task.toString());
    }

    @Test
    public void createTask_missingEventFields_specificMessagesPreserved() {
        String[][] cases = {
            {"event", "description, /from, and /to"},
            {"event project meeting", "/from and /to"},
            {"event /from 2019-12-01 /to 2019-12-02", "description"},
            {"event /from 2019-12-01", "description and /to"},
            {"event /to 2019-12-02", "description and /from"},
            {"event project meeting /to 2019-12-02", "/from"},
            {"event project meeting /from  /to 2019-12-02", "/from"},
            {"event project meeting /from 2019-12-01 /to ", "/to"}
        };

        for (String[] testCase : cases) {
            MrChatbotException exception = assertThrows(
                    MrChatbotException.class, () -> parser.createTask(testCase[0]), testCase[0]);

            assertEquals("Event " + testCase[1] + " cannot be empty. "
                    + "Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>",
                    exception.getMessage(), testCase[0]);
        }
    }

    @Test
    public void createTask_missingDeadlineFields_specificMessagesPreserved() {
        String[][] cases = {
            {"deadline", "description and /by"},
            {"deadline   ", "description and /by"},
            {"deadline return book", "/by"},
            {"deadline return book /by ", "/by"},
            {"deadline /by 2019-12-01", "description"}
        };

        for (String[] testCase : cases) {
            MrChatbotException exception = assertThrows(
                    MrChatbotException.class, () -> parser.createTask(testCase[0]), testCase[0]);

            assertEquals("Deadline " + testCase[1] + " cannot be empty. "
                    + "Please use the format: deadline <description> /by <yyyy-mm-dd>",
                    exception.getMessage(), testCase[0]);
        }
    }

    @Test
    public void createTask_missingTodoDescription_exceptionThrown() {
        for (String input : new String[]{"todo", "TODO   "}) {
            MrChatbotException exception = assertThrows(
                    MrChatbotException.class, () -> parser.createTask(input));

            assertEquals("Todo description cannot be empty. Please use the format: todo <description>",
                    exception.getMessage());
        }
    }
}
