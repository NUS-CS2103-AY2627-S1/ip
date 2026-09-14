package morgan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import morgan.command.SortCommand;
import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

public class ParserTest {

    @Test
    public void parse_sortCommand_returnsSortCommand() throws MorganException {
        assertTrue(Parser.parse("sort") instanceof SortCommand);
    }

    @Test
    public void parseAndExecute_byeCommand_returnsTrue() throws MorganException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        boolean isExit = Parser.parseAndExecute("bye", tasks, ui, storage);
        assertTrue(isExit);
    }

    @Test
    public void parseAndExecute_invalidCommand_exceptionThrown() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        MorganException exception = assertThrows(MorganException.class, () -> {
            Parser.parseAndExecute("invalidCmd", tasks, ui, storage);
        });


        assertEquals("Meow? Is that a fish? Try list, todo, deadline, event, find, sort, or bye.",
                exception.getMessage());
    }

    @Test
    public void parseAndExecute_emptyTodoDescription_exceptionThrown() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        MorganException exception = assertThrows(MorganException.class, () -> {
            Parser.parseAndExecute("todo ", tasks, ui, storage);
        });

        assertEquals("Meow? Tell me what to add, e.g., todo have a nice sleep.", exception.getMessage());
    }

    @Test
    public void parse_eventEndBeforeStart_exceptionThrown() {
        MorganException exception = assertThrows(MorganException.class, () ->
                Parser.parse("event fish party /from 2026-09-18 1600 /to 2026-09-18 1400"));

        assertEquals("Meow? Morgan doesn't think you can do time traveling.", exception.getMessage());
    }

    @Test
    public void parse_eventStartEqualsEnd_exceptionThrown() {
        MorganException exception = assertThrows(MorganException.class, () ->
                Parser.parse("event fish party /from 2026-09-18 1400 /to 2026-09-18 1400"));

        assertEquals("Meow? Morgan doesn't think you can do time traveling.", exception.getMessage());
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        MorganException exception = assertThrows(MorganException.class, () ->
                Parser.parse("event /from 2026-08-31 /to 2026-08-25"));

        assertEquals("Meow? Tell me what event to add, e.g., event fish party "
                        + "/from 2026-08-31 1400 /to 2026-08-31 1600.",
                exception.getMessage());
    }

    @Test
    public void parse_eventWithInvalidDelimiterStructure_exceptionThrown() {
        MorganException exception = assertThrows(MorganException.class, () ->
                Parser.parse("event fish party /to 2026-09-18 1600 /from 2026-09-18 1400"));

        assertEquals("Meow? An event needs one /from and one /to, in that order.", exception.getMessage());
    }

    @Test
    public void parse_eventWithoutTimes_exceptionThrown() {
        MorganException exception = assertThrows(MorganException.class, () ->
                Parser.parse("event fish party /from 2026-08-31 /to 2026-08-25"));

        assertEquals("Meow! Please use date format: yyyy-MM-dd HHmm (e.g., 2026-08-31 1400)",
                exception.getMessage());
    }

    @Test
    public void parse_noArgumentCommandsWithExtraArguments_exceptionThrown() {
        String[] commands = {"bye now", "list fishes", "sort alphabetically"};

        for (String command : commands) {
            MorganException exception = assertThrows(MorganException.class, () -> Parser.parse(command));
            assertEquals("Meow? This command needs no extra treats. Please use it on its own.",
                    exception.getMessage());
        }
    }
}
