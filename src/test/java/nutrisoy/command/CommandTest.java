package nutrisoy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import nutrisoy.exception.DukeException;
import nutrisoy.parser.Parser;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/** Tests command outcomes, input boundaries, and preservation of state on rejection. */
public class CommandTest {
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
    }

    @Test
    public void add_allTaskTypes_preservesDetailsAndOrder() throws DukeException {
        assertTrue(execute("todo   read\tbook").contains("1 task."));
        assertTrue(execute("deadline report /by 2028-02-29").contains("2 tasks."));
        execute("event trip /from 2026-12-31 /to 2027-01-01");

        assertEquals(List.of("T | 0 | read book", "D | 0 | report | 2028-02-29",
                "E | 0 | trip | 2026-12-31 | 2027-01-01"), records());
    }

    @Test
    public void markAndUnmark_firstAndLastTasks_changesOnlySelectedTask() throws DukeException {
        execute("todo first");
        execute("todo last");
        assertTrue(execute("mark 2").contains("[T][X] last"));
        assertFalse(tasks.get(0).isDone());
        execute("mark 1");
        assertTrue(tasks.get(0).isDone());
        assertTrue(execute("unmark 2").contains("[T][ ] last"));
        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void delete_firstAndLastTasks_preservesRemainingOrder() throws DukeException {
        execute("todo first");
        execute("todo middle");
        execute("todo last");
        assertTrue(execute("delete 1").contains("[T][ ] first"));
        assertEquals(List.of("T | 0 | middle", "T | 0 | last"), records());
        execute("delete 2");
        assertEquals(List.of("T | 0 | middle"), records());
        assertTrue(execute("delete 1").contains("0 tasks."));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void list_emptyAndPopulated_displaysNumberedTasks() throws DukeException {
        assertTrue(execute("list").contains("empty"));
        execute("todo read");
        execute("todo write");
        String response = execute("list");
        assertTrue(response.contains("1.[T][ ] read"));
        assertTrue(response.contains("2.[T][ ] write"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void find_substringAndCase_filtersWithoutChangingTasks() throws DukeException {
        execute("todo read book");
        execute("todo buy milk");
        execute("todo reread notes");
        List<String> before = records();
        String response = execute("find read");
        assertTrue(response.contains("1.[T][ ] read book"));
        assertTrue(response.contains("2.[T][ ] reread notes"));
        assertFalse(response.contains("buy milk"));
        assertTrue(execute("find READ").contains("No matches"));
        assertEquals(before, records());
    }

    @Test
    public void tagAndUntag_caseVariants_preservesOtherTags() throws DukeException {
        execute("todo read");
        assertTrue(execute("tag 1 School").contains("#School"));
        execute("tag 1 project-2_notes");
        assertEquals("T | 0 | read | School,project-2_notes", tasks.get(0).toFileFormat());
        execute("untag 1 SCHOOL");
        assertEquals("T | 0 | read | project-2_notes", tasks.get(0).toFileFormat());
    }

    @Test
    public void duplicate_datedTasks_considersDatesButNotStatusOrTags() throws DukeException {
        execute("deadline report /by 2026-09-20");
        execute("mark 1");
        execute("tag 1 work");
        assertThrows(DukeException.class, () -> execute("deadline REPORT /by 2026-09-20"));
        execute("deadline report /by 2026-09-21");
        execute("event trip /from 2026-09-20 /to 2026-09-21");
        assertThrows(DukeException.class, () -> execute("event TRIP /from 2026-09-20 /to 2026-09-21"));
        execute("event trip /from 2026-09-20 /to 2026-09-22");
        assertEquals(4, tasks.size());
    }

    @Test
    public void exit_onlyByeSignalsExit_leavesTasksIntact() throws DukeException {
        execute("todo read");
        assertFalse(Parser.parse("list").isExit());
        assertTrue(Parser.parse("bye").isExit());
        assertTrue(execute("bye").contains("That's a wrap"));
        assertEquals(List.of("T | 0 | read"), records());
    }

    @TestFactory
    public Stream<DynamicTest> invalidArguments_leaveTasksUnchanged() {
        return Stream.of("todo", "todo |", "find", "tag", "untag", "tag 1", "tag 1 a b",
                "tag 1 #work", "tag 1 a,b", "untag 1 missing", "deadline", "deadline report",
                "deadline /by 2026-09-20", "deadline report /by", "deadline report /by2026-09-20",
                "deadline report /by 2026-02-29", "deadline report /by 2026-13-01",
                "deadline report /by 2026-09-20 /by 2026-09-21", "event", "event trip",
                "event /from 2026-09-20 /to 2026-09-21", "event trip /from /to 2026-09-21",
                "event trip /from 2026-09-20 /to", "event trip /from 2026-09-20",
                "event trip /to 2026-09-21", "event trip /from 2026-02-30 /to 2026-03-01",
                "event trip /from 2026-09-20 /from 2026-09-21 /to 2026-09-22",
                "event trip /from 2026-09-20 /to 2026-09-21 /to 2026-09-22")
                .map(input -> dynamicTest(input, () -> {
                    TaskList localTasks = new TaskList();
                    Ui localUi = new Ui();
                    localUi.startCapturingOutput();
                    Parser.parse("todo existing").execute(localTasks, localUi);
                    assertThrows(DukeException.class, () -> Parser.parse(input).execute(localTasks, localUi));
                    assertEquals(1, localTasks.size());
                    assertEquals("T | 0 | existing", localTasks.get(0).toFileFormat());
                }));
    }

    @TestFactory
    public Stream<DynamicTest> invalidIndexes_leaveTasksUnchanged() {
        return Stream.of("mark", "unmark", "delete", "tag", "untag").flatMap(command ->
                Stream.of("", "0", "-1", "2", "1.5", "abc", "2147483648", "-2147483648")
                        .map(index -> dynamicTest(command + " index=" + index, () -> {
                            TaskList localTasks = new TaskList();
                            Ui localUi = new Ui();
                            localUi.startCapturingOutput();
                            Parser.parse("todo existing").execute(localTasks, localUi);
                            String suffix = command.equals("tag") || command.equals("untag") ? " work" : "";
                            String input = command + " " + index + suffix;
                            assertThrows(DukeException.class, () -> Parser.parse(input).execute(localTasks, localUi));
                            assertEquals("T | 0 | existing", localTasks.get(0).toFileFormat());
                            assertEquals(1, localTasks.size());
                        })));
    }

    private List<String> records() {
        return tasks.getTasks().stream().map(Task::toFileFormat).toList();
    }

    private String execute(String input) throws DukeException {
        ui.startCapturingOutput();
        try {
            Parser.parse(input).execute(tasks, ui);
        } catch (DukeException e) {
            ui.stopCapturingOutput();
            throw e;
        }
        return ui.stopCapturingOutput();
    }
}
