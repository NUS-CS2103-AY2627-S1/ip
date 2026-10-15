package odysseus.task;
import odysseus.OdysseusException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskFactoryTest {

    @Test
    public void fromSaveFormat_doneTodoLine_returnsDoneTodo()
            throws OdysseusException {
        String line = "T | 1 | read book";
        assertEquals("[T][X] read book",
                TaskFactory.fromSaveFormat(line).toString());
    }

    @Test
    public void fromSaveFormat_undoneDeadlineLine_returnsUndoneDeadline()
            throws OdysseusException {
        String line = "D | 0 | deadline task | 2026-06-06";
        assertEquals("[D][ ] deadline task (by: Jun 6 2026)",
                TaskFactory.fromSaveFormat(line).toString());
    }

    @Test
    public void fromSaveFormat_tooFewFieldsLine_throwsOdysseusException() {
        String line = "garbage";
        assertThrows(OdysseusException.class, () ->
                TaskFactory.fromSaveFormat(line));
    }

    @Test
    public void fromSaveFormat_unknownMarkerLine_throwsOdysseusException() {
       String line = "X | 1 | unknown marker";
       assertThrows(OdysseusException.class, () ->
               TaskFactory.fromSaveFormat(line));
    }
}