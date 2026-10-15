package computa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.DayOfWeek;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @TempDir Path directory;
    private Storage storage() { return new Storage(directory.resolve("tasks.txt").toString()); }

    @Test void parsesBasicTaskCommands() throws Exception {
        TaskList list = new TaskList();
        Parser.parse("todo study", list, new Ui(), storage());
        Parser.parse("deadline submit / 2026-12-01 1159", list, new Ui(), storage());
        Parser.parse("event meeting / 2026-12-01 1200 / 2026-12-01 1300", list, new Ui(), storage());
        assertEquals(3, list.getSize());
    }

    @Test void parsesRecurringCommand() throws Exception {
        TaskList list = new TaskList();
        Parser.parse("recurring report / Monday", list, new Ui(), storage());
        assertEquals(DayOfWeek.MONDAY, ((RecurringTask) list.getTask(0)).getDay());
    }

    @Test void rejectsInvalidArguments() {
        TaskList list = new TaskList();
        assertThrows(Computa.ComputaException.class, () -> Parser.parse("deadline report", list, new Ui(), storage()));
        assertThrows(Computa.ComputaException.class, () -> Parser.parse("deadline report / bad-date", list, new Ui(), storage()));
        assertThrows(Computa.ComputaException.class, () -> Parser.parse("recurring report / Funday", list, new Ui(), storage()));
        assertThrows(Computa.ComputaException.class, () -> Parser.parse("nonsense", list, new Ui(), storage()));
    }

    @Test void markUnmarkAndDeleteUseVisibleNumbers() throws Exception {
        TaskList list = new TaskList(); Storage storage = storage();
        Parser.parse("recurring hidden / Monday", list, new Ui(), storage);
        Parser.parse("todo visible", list, new Ui(), storage);
        Parser.parse("mark 1", list, new Ui(), storage);
        assertEquals("[X]", list.getVisibleTask(0).getStatusIcon());
        Parser.parse("unmark 1", list, new Ui(), storage);
        Parser.parse("delete 1", list, new Ui(), storage);
        assertEquals(1, list.getSize());
    }

    @Test void byeReturnsExitSignal() throws Exception {
        assertTrue(Parser.parse("bye", new TaskList(), new Ui(), storage()));
    }
}
