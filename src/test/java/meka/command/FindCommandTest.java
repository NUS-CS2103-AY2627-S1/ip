package meka.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meka.exception.MekaException;
import meka.parser.Parser;
import meka.storage.Storage;
import meka.task.TaskList;
import meka.task.Todo;
import meka.ui.Ui;

public class FindCommandTest {
    @TempDir
    private Path directory;

    @Test
    public void execute_mixedCaseMatches_preservesOrderStatusAndSavedData() throws Exception {
        TaskList tasks = new TaskList();
        Todo first = new Todo("Read a Book");
        first.markAsDone();
        tasks.add(first);
        tasks.add(new Todo("meeting"));
        tasks.add(new Todo("return book"));
        Path file = directory.resolve("meka.txt");
        Storage storage = new Storage(file);
        storage.save(tasks);
        String saved = Files.readString(file);
        CapturingUi ui = new CapturingUi();

        Command command = Parser.parse("find BOOK");
        command.execute(tasks, ui, storage);

        assertFalse(command.isExit());
        assertEquals(2, ui.matches.size());
        assertSame(first, ui.matches.get(1));
        assertEquals("X", ui.matches.get(1).getStatusIcon());
        assertSame(tasks.get(3), ui.matches.get(2));
        assertEquals(3, tasks.size());
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void find_noMatchesOrEmptyList_returnsEmptyResults() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.find("book").size());
        tasks.add(new Todo("meeting"));
        assertEquals(0, tasks.find("book").size());
        assertEquals(1, tasks.size());
    }

    @Test
    public void find_substringMatch_returnsIndependentCollection() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("notebook"));
        TaskList matches = tasks.find("book");
        assertEquals(1, matches.size());
        matches.delete(1);
        assertEquals(1, tasks.size());
    }

    @Test
    public void parse_missingKeyword_rejectsFind() {
        assertThrows(MekaException.class, () -> Parser.parse("find"));
        assertThrows(MekaException.class, () -> Parser.parse("find   "));
    }

    private static class CapturingUi extends Ui {
        private TaskList matches;

        @Override
        public void showMatchingTasks(TaskList matches) {
            this.matches = matches;
        }
    }
}
