package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nyonbot.Logic.Result;
import nyonbot.Parser;
import nyonbot.model.Deadline;
import nyonbot.model.Event;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

class HelpAndOnCommandTest {
    private TaskList tasks;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
    }

    @Test
    void parser_helpAndOnKeywords_returnsCorrespondingCommands() {
        Parser parser = Parser.getInstance();

        assertInstanceOf(HelpCommand.class, parser.parse("help"));
        assertInstanceOf(HelpCommand.class, parser.parse("? event"));
        assertInstanceOf(OnCommand.class, parser.parse("on 05/09/2026"));
    }

    @Test
    void helpCommand_withoutArgument_listsAvailableCommands() throws NyonException {
        Result result = new HelpCommand(arguments("help")).execute();

        assertTrue(result.out().contains("help, ?"));
        assertTrue(result.out().contains("on - list deadlines and events"));
        assertTrue(result.out().contains("list, ls"));
    }

    @Test
    void helpCommand_aliasArgument_returnsCanonicalCommandSummary() throws NyonException {
        Result result = new HelpCommand(arguments("help rm")).execute();

        assertTrue(result.out().contains("delete, del, rm"));
        assertTrue(result.out().contains("delete a task"));
    }

    @Test
    void helpCommand_unknownArgument_throwsNyonException() {
        HelpCommand command = new HelpCommand(arguments("help missing"));

        assertThrows(NyonException.class, command::execute);
    }

    @Test
    void onCommand_tasksOccurringOnDay_returnsDeadlinesAndEvents() throws NyonException {
        tasks.add(new Task("undated task"));
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 5, 9, 0)));
        tasks.add(new Deadline("later deadline", LocalDateTime.of(2026, 9, 6, 9, 0)));
        tasks.add(new Event("conference",
                LocalDateTime.of(2026, 9, 4, 9, 0),
                LocalDateTime.of(2026, 9, 6, 17, 0)));
        tasks.add(new Event("past event",
                LocalDateTime.of(2026, 9, 1, 9, 0),
                LocalDateTime.of(2026, 9, 2, 17, 0)));

        Result result = new OnCommand(arguments("on 05/09/2026"), tasks).execute();

        assertTrue(result.out().contains("submit report"));
        assertTrue(result.out().contains("conference"));
        assertFalse(result.out().contains("later deadline"));
        assertFalse(result.out().contains("undated task"));
        assertFalse(result.out().contains("past event"));
    }

    @Test
    void onCommand_noMatchingTasks_returnsNoTasksMessage() throws NyonException {
        Result result = new OnCommand(arguments("on 05/09/2026"), tasks).execute();

        assertTrue(result.out().contains("No deadlines or events on 05/09/2026."));
    }

    @Test
    void onCommand_invalidDate_throwsNyonException() {
        OnCommand command = new OnCommand(arguments("on 31/02/2026"), tasks);

        assertThrows(NyonException.class, command::execute);
    }

    private static HashMap<String, String> arguments(String input) {
        return Parser.getInstance().parseArguments(input);
    }
}
