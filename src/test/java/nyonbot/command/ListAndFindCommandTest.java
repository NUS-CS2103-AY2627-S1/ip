package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nyonbot.Parser;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

class ListAndFindCommandTest {
    private TaskList tasks;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
    }

    @Test
    void listCommand_emptyList_returnsEmptyListMessage() {
        String output = new ListCommand(arguments("list"), tasks).execute().out();

        assertTrue(output.contains("No tasks nyon..."));
    }

    @Test
    void listCommand_multipleTasks_numbersTasksInOrder() {
        tasks.add(new Task("first"));
        tasks.add(new Task("second"));

        String output = new ListCommand(arguments("list"), tasks).execute().out();

        assertTrue(output.contains("1. [T][ ] first"));
        assertTrue(output.contains("2. [T][ ] second"));
    }

    @Test
    void findCommand_matchingKeyword_returnsMatchingTasksOnly()
            throws NyonException {
        tasks.add(new Task("read book"));
        tasks.add(new Task("buy milk"));

        String output = new FindCommand(arguments("find book"), tasks).execute().out();

        assertTrue(output.contains("read book"));
        assertFalse(output.contains("buy milk"));
    }

    @Test
    void findCommand_noMatch_returnsNoMatchMessage() throws NyonException {
        tasks.add(new Task("read book"));

        assertEquals("couldn't find anything matching the search string...",
                new FindCommand(arguments("find milk"), tasks).execute().out());
    }

    @Test
    void findCommand_missingSearchString_throwsNyonException() {
        assertThrows(NyonException.class, () -> new FindCommand(arguments("find"), tasks).execute());
    }

    private static HashMap<String, String> arguments(String input) {
        return Parser.getInstance().parseArguments(input);
    }
}
