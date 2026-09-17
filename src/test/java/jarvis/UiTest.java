package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests console task-list rendering. */
class UiTest {
    @Test
    void showTasks_varargs_rendersAllTasksInOrder() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(output));

        try {
            new Ui().showTasks(new Todo("first"), new Todo("second"));
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("     Here are the tasks in your list:\n"
                + "     1.[T][ ] first\n"
                + "     2.[T][ ] second\n", normalizeLineEndings(output));
    }

    @Test
    void showTaskUpdates_renderExpectedConfirmationMessages() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(output));

        try {
            Ui ui = new Ui();
            Todo todo = new Todo("read book");
            ui.showTaskAdded(todo, 1);
            ui.showTaskRemoved(todo, 0);
            ui.showTaskMarked(todo);
            ui.showTaskUnmarked(todo);
            ui.showTaskSnoozed(1, new Deadline("submit report", LocalDate.of(2026, 9, 20)));
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("     Got it. I've added this task:\n"
                + "       [T][ ] read book\n"
                + "     Now you have 1 tasks in the list.\n"
                + "     Noted. I've removed this task:\n"
                + "       [T][ ] read book\n"
                + "     Now you have 0 tasks in the list.\n"
                + "     Nice! I've marked this task as done:\n"
                + "       [X] read book\n"
                + "     OK, I've marked this task as not done yet:\n"
                + "       [ ] read book\n"
                + "     Snoozed task 1 until Sep 20 2026:\n"
                + "       [D][ ] submit report (by: Sep 20 2026)\n", normalizeLineEndings(output));
    }

    /** Returns captured output with platform-specific line endings normalized. */
    private String normalizeLineEndings(ByteArrayOutputStream output) {
        return output.toString().replace("\r\n", "\n");
    }
}
