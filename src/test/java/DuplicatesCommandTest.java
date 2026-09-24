package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sage.command.DuplicatesCommand;
import sage.exception.SageException;
import sage.model.Deadline;
import sage.model.Event;
import sage.model.TaskList;
import sage.model.Todo;
import sage.ui.Ui;

class DuplicatesCommandTest {
    @Test
    void duplicatesCommandReportsDuplicateGroupsAcrossAllTaskTypes() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("  Read   book "));
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 12, 10, 0)));
        tasks.add(new Deadline("SUBMIT REPORT", LocalDateTime.of(2026, 9, 12, 10, 0)));
        tasks.add(new Event("team meeting", LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)));
        tasks.add(new Event(" team   meeting ", LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)));
        tasks.get(1).markAsDone();

        assertEquals("Here are the duplicate task groups in your list:\n"
                + "Group:\n1.[T][ ]   Read   book \n2.[T][X] read book\n"
                + "Group:\n3.[D][ ] submit report (by: Sep 12 2026, 10:00AM)\n"
                + "4.[D][ ] SUBMIT REPORT (by: Sep 12 2026, 10:00AM)\n"
                + "Group:\n5.[E][ ] team meeting (from: Sep 12 2026, 11:00AM to: "
                + "Sep 12 2026, 12:00PM)\n6.[E][ ]  team   meeting  (from: Sep 12 2026, 11:00AM to: "
                + "Sep 12 2026, 12:00PM)\n", execute(tasks));
    }

    @Test
    void duplicatesCommandDoesNotGroupTasksWithDifferentTimeRanges() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 12, 10, 0)));
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 13, 10, 0)));
        tasks.add(new Event("meeting", LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)));
        tasks.add(new Todo("meeting"));

        assertEquals("There are no duplicate tasks.\n", execute(tasks));
    }

    @Test
    void duplicatesCommandRejectsArguments() {
        assertThrows(SageException.class, () -> new DuplicatesCommand("unexpected")
                .execute(new TaskList(), new Ui(), null));
    }

    private String execute(TaskList tasks) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        try {
            new DuplicatesCommand("").execute(tasks, new Ui(), null);
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }
}
