package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UpdateFieldTest {

    @Test
    public void fromCommandFlag_allValidFlags_returnsCorrespondingField() {
        assertEquals(
                UpdateField.DESCRIPTION,
                UpdateField.fromCommandFlag("/description"));
        assertEquals(
                UpdateField.DUE_DATE,
                UpdateField.fromCommandFlag("/by"));
        assertEquals(
                UpdateField.START_TIME,
                UpdateField.fromCommandFlag("/from"));
        assertEquals(
                UpdateField.END_TIME,
                UpdateField.fromCommandFlag("/to"));
    }

    @Test
    public void fromCommandFlag_invalidFlag_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> UpdateField.fromCommandFlag("/when"));

        assertEquals(
                "Valid update fields are: /description, /by, /from, /to.",
                exception.getMessage());
    }

    @Test
    public void isCommandFlag_validAndInvalidText_returnsExpectedResult() {
        assertTrue(UpdateField.isCommandFlag("/description"));
        assertTrue(UpdateField.isCommandFlag("/by"));
        assertTrue(UpdateField.isCommandFlag("/from"));
        assertTrue(UpdateField.isCommandFlag("/to"));
        assertFalse(UpdateField.isCommandFlag("description"));
        assertFalse(UpdateField.isCommandFlag("/when"));
    }
}
