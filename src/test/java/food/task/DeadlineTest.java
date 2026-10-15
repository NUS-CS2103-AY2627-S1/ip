package food.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import food.exception.FoodInputException;

/** Tests the input checks in the {@link Deadline} constructor. */
public class DeadlineTest {

    @Test
    public void constructor_validLine_dateShownInDisplayForm() throws FoodInputException {
        Deadline deadline = new Deadline("deadline return book /by 2026-09-07");
        assertEquals("[D] [] return book (by: 7/9/2026)", deadline.toString());
    }

    @Test
    public void constructor_nonExistentDate_exceptionThrown() {
        assertThrows(FoodInputException.class, () -> new Deadline("deadline x /by 2026-02-30"));
    }

    @Test
    public void constructor_wrongDateFormat_exceptionThrown() {
        assertThrows(FoodInputException.class, () -> new Deadline("deadline x /by 7/9/2026"));
    }

    @Test
    public void constructor_byGivenTwice_exceptionThrown() {
        FoodInputException e = assertThrows(FoodInputException.class,
                () -> new Deadline("deadline x /by 2026-09-07 /by 2026-09-08"));
        assertEquals("One /by per ticket, chef.", e.getMessage());
    }

    @Test
    public void constructor_missingBy_exceptionThrown() {
        assertThrows(FoodInputException.class, () -> new Deadline("deadline return book"));
    }

    @Test
    public void constructor_blankDescription_exceptionThrown() {
        assertThrows(FoodInputException.class, () -> new Deadline("deadline   /by 2026-09-07"));
    }
}
