package meka.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meka.command.AddCommand;
import meka.command.ArchiveCommand;
import meka.command.Command;
import meka.command.DeleteCommand;
import meka.command.ExitCommand;
import meka.command.ListCommand;
import meka.command.MarkCommand;
import meka.command.UnmarkCommand;
import meka.exception.MekaException;
import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

public class ParserTest {
    @TempDir
    private Path directory;

    @Test
    public void parse_repeatedParameters_rejectsAmbiguousValues() {
        String[] inputs = {
            "deadline report /by 2/12/2019 1800 /by 3/12/2019 1800",
            "event meeting /from 2/12/2019 0900 /from 2/12/2019 1000 /to 2/12/2019 1100",
            "event meeting /from 2/12/2019 0900 /to 2/12/2019 1000 /to 2/12/2019 1100"
        };
        String[] markers = {"by", "from", "to"};
        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            MekaException exception = assertThrows(MekaException.class, () -> Parser.parse(input));
            assertEquals("Specify /" + markers[i] + " only once.", exception.getMessage());
        }
    }

    @Test
    public void parse_nonIncreasingEventTimes_rejectsInvalidRange() {
        for (String end : new String[]{"0900", "0800"}) {
            String input = "event meeting /from 2/12/2019 0900 /to 2/12/2019 " + end;
            MekaException exception = assertThrows(MekaException.class, () -> Parser.parse(input));
            assertEquals("The event end must be after its start.", exception.getMessage());
        }
    }

    @Test
    public void parse_extraWhitespace_acceptsCommandsAndDates() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(directory.resolve("whitespace.txt"));
        Parser.parse(" \tdeadline\t report   /by\t2/12/2019   1800  ")
                .execute(tasks, new Ui(), storage);

        assertEquals("D | 0 | report | 2019-12-02T18:00", tasks.get(1).toDataString());
        assertInstanceOf(ListCommand.class, Parser.parse(" \tlist\t "));
    }

    @Test
    public void parse_supportedCommands_returnsExecutableCommands() throws Exception {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(ArchiveCommand.class, Parser.parse("archive all"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        Command exit = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, exit);
        assertTrue(exit.isExit());
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_validTaskTypes_executesAndPersistsParsedDates() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(directory.resolve("meka.txt"));
        Ui ui = new Ui();
        Parser.parse("todo read book").execute(tasks, ui, storage);
        Parser.parse("deadline return book /by 2/12/2019 1800").execute(tasks, ui, storage);
        Parser.parse("event meeting /from 3/12/2019 0900 /to 4/12/2019 1730").execute(tasks, ui, storage);

        TaskList loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(1).toDataString());
        assertEquals("D | 0 | return book | 2019-12-02T18:00", loaded.get(2).toDataString());
        assertEquals("E | 0 | meeting | 2019-12-03T09:00 | 2019-12-04T17:30", loaded.get(3).toDataString());
    }

    @Test
    public void parse_mutatingCommands_updatesAndPersistsTaskState() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(directory.resolve("meka.txt"));
        Ui ui = new Ui();
        Parser.parse("todo first").execute(tasks, ui, storage);
        Parser.parse("todo second").execute(tasks, ui, storage);
        Parser.parse("mark 1").execute(tasks, ui, storage);
        assertEquals("X", storage.load().get(1).getStatusIcon());
        Parser.parse("unmark 1").execute(tasks, ui, storage);
        assertEquals(" ", storage.load().get(1).getStatusIcon());
        Parser.parse("delete 1").execute(tasks, ui, storage);
        assertEquals(1, storage.load().size());
        assertEquals("T | 0 | second", storage.load().get(1).toDataString());
    }

    @Test
    public void parse_invalidNumbers_rejectsMissingNonNumericAndOverflowValues() {
        for (String input : new String[]{"mark", "unmark none", "delete 2147483648"}) {
            MekaException exception = assertThrows(MekaException.class, () -> Parser.parse(input));
            assertEquals("The following command requires a number to proceed.", exception.getMessage());
        }
    }

    @Test
    public void parse_invalidTaskDetails_rejectsMalformedCommands() {
        String[] inputs = {
            "todo", "todo text | reserved", "deadline /by 2/12/2019 1800",
            "deadline task /by", "deadline task /by 31/2/2019 1800",
            "deadline task /bye 2/12/2019 1800", "event meeting /from 3/12/2019 0900",
            "event meeting /to 4/12/2019 1730 /from 3/12/2019 0900",
            "archive", "archive 1", "dance", "list extra"
        };
        for (String input : inputs) {
            assertThrows(MekaException.class, () -> Parser.parse(input), input);
        }
    }
}
