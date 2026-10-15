package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Exercises domain boundaries, Unicode text, and lookup rules independently of the UI. */
class DomainBoundaryTest {
    @Test
    void parser_allAliases_acceptCaseAndWhitespace() {
        String[] main = {"neigh", "meow", "list", "todo"};
        for (int i = 0; i < main.length; i++) {
            assertEquals(Parser.MainMenu.valueOf(main[i].toUpperCase(Locale.ROOT)),
                    Parser.MainMenu.fromString(Integer.toString(i + 1)));
            assertEquals(Parser.MainMenu.valueOf(main[i].toUpperCase(Locale.ROOT)),
                    Parser.MainMenu.fromString(" \t" + main[i].toUpperCase(Locale.ROOT) + " "));
        }
        String[] todo = {"add", "remove", "view", "sort", "mark", "unmark", "find", "exit"};
        for (int i = 0; i < todo.length; i++) {
            assertEquals(Parser.TodoMenu.valueOf(todo[i].toUpperCase(Locale.ROOT)),
                    Parser.TodoMenu.fromString(Integer.toString(i + 1)));
            assertEquals(Parser.TodoMenu.valueOf(todo[i].toUpperCase(Locale.ROOT)),
                    Parser.TodoMenu.fromString(" " + todo[i].toUpperCase(Locale.ROOT) + " "));
        }
        String[] types = {"todo", "deadline", "event", "exit"};
        for (int i = 0; i < types.length; i++) {
            assertEquals(Parser.TaskMenu.valueOf(types[i].toUpperCase(Locale.ROOT)),
                    Parser.TaskMenu.fromString(Integer.toString(i + 1)));
            assertEquals(Parser.TaskMenu.valueOf(types[i].toUpperCase(Locale.ROOT)),
                    Parser.TaskMenu.fromString(" " + types[i].toUpperCase(Locale.ROOT) + " "));
        }
        for (String invalid : List.of("", " ", "0", "9", "add extra")) {
            assertEquals(Parser.MainMenu.UNKNOWN, Parser.MainMenu.fromString(invalid));
            assertEquals(Parser.TodoMenu.UNKNOWN, Parser.TodoMenu.fromString(invalid));
            assertEquals(Parser.TaskMenu.UNKNOWN, Parser.TaskMenu.fromString(invalid));
        }
    }

    @Test
    void baseTask_repeatedStatusChangesAndUnicode_preserveDescription() {
        Task task = new Task("\u2003学习 📚\u2003");
        assertEquals("[][ ] 学习 📚", task.toString());
        assertNull(task.getDate());
        task.markAsDone();
        task.markAsDone();
        assertEquals("[][x] 学习 📚", task.toString());
        task.markAsNotDone();
        task.markAsNotDone();
        assertEquals("false|学习 📚", task.toFileFormat());
        assertThrows(IllegalArgumentException.class, () -> Task.validateDescription(null));
        assertThrows(IllegalArgumentException.class, () -> Task.validateDescription("\u2003\t"));
    }

    @Test
    void datedTasks_leapDayAndTimeBoundaries_normalizeStorage() {
        Deadline deadline = new Deadline("leap", " 29-02-2024\t0000 ");
        Event event = new Event("last minute", "31-12-2026 2359");
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), deadline.getDate());
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59), event.getDate());
        event.markAsDone();
        assertEquals("EVENT|true|last minute|31-12-2026 2359", event.toFileFormat());
        assertEquals("DEADLINE|false|leap|29-02-2024 0000", deadline.toFileFormat());
    }

    @Test
    void taskList_invalidArguments_assertionsProtectInvariants() {
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList(new Todo("read"), null)));
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("read"))));
        for (int index : new int[]{-1, 1, Integer.MAX_VALUE}) {
            assertThrows(AssertionError.class, () -> tasks.getTask(index));
            assertThrows(AssertionError.class, () -> tasks.removeTask(index));
        }
        assertThrows(AssertionError.class, () -> tasks.findTaskIndex(null));
        assertThrows(AssertionError.class, () -> tasks.findTasks(null));
    }

    @Test
    void lookup_numericNamesAndOverflow_obeyNumberThenExactNameRule() {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("second"), new Todo("1"),
                new Todo("99"), new Todo("2147483648"))));
        assertEquals(0, tasks.findTaskIndex("1"));
        assertEquals(2, tasks.findTaskIndex(" 99 "));
        assertEquals(3, tasks.findTaskIndex("2147483648"));
        assertEquals(0, tasks.findTaskIndex(" second "));
        assertEquals(-1, tasks.findTaskIndex("SECOND"));
        assertEquals(-1, tasks.findTaskIndex(""));
    }

    @Test
    void search_turkishLocaleAndUnicode_matchesWithoutMutatingBackingList() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Task first = new Todo("FINISH reading");
            Task second = new Todo("学习 Java");
            TaskList tasks = new TaskList(new ArrayList<>(List.of(first, second)));
            assertEquals(List.of(first), tasks.findTasks(" finish "));
            assertEquals(List.of(second), tasks.findTasks("学习"));
            assertEquals(List.of(), tasks.findTasks("missing"));
            assertEquals(List.of(first, second), tasks.getTasks());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void duplicates_areRetainedAlongsideDistinctTypesAndDates() {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task done = new Event("meeting", "01-01-2027 1200");
        done.markAsDone();
        tasks.addTask(done);
        tasks.addTask(new Event("MEETING", "01-01-2027 1200"));
        tasks.addTask(new Event("meeting", "01-01-2027 1201"));
        tasks.addTask(new Deadline("meeting", "01-01-2027 1200"));
        tasks.addTask(new Event("different", "01-01-2027 1200"));
        assertEquals(5, tasks.size());
    }
}
