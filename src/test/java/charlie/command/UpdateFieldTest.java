package charlie.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import charlie.exception.CharlieException;

public class UpdateFieldTest {
    @Test
    public void parseKeyword_allSupportedKeywords_returnsMatchingFields() {
        assertEquals(UpdateField.DESCRIPTION, UpdateField.parseKeyword("description"));
        assertEquals(UpdateField.DEADLINE, UpdateField.parseKeyword("deadline"));
        assertEquals(UpdateField.FROM, UpdateField.parseKeyword("from"));
        assertEquals(UpdateField.TO, UpdateField.parseKeyword("to"));
    }

    @Test
    public void parseKeyword_unknownKeyword_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> UpdateField.parseKeyword("priority"));

        assertEquals(
                "Supported update fields are description, deadline, from, and to.",
                exception.getMessage());
    }

    @Test
    public void validateNewValue_validValues_doesNotThrow() {
        assertDoesNotThrow(() -> UpdateField.DESCRIPTION.validateNewValue("new description"));
        assertDoesNotThrow(() -> UpdateField.DEADLINE.validateNewValue("2026-09-20"));
        assertDoesNotThrow(() -> UpdateField.FROM.validateNewValue("2026-09-20 0900"));
        assertDoesNotThrow(() -> UpdateField.TO.validateNewValue("2026-09-20 1000"));
    }

    @Test
    public void validateNewValue_blankValue_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> UpdateField.DESCRIPTION.validateNewValue("   "));

        assertEquals("The updated value cannot be empty.", exception.getMessage());
    }

    @Test
    public void validateNewValue_invalidDate_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> UpdateField.DEADLINE.validateNewValue("2026-02-30"));

        assertEquals("Deadline must use the yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void validateNewValue_invalidDateTime_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> UpdateField.FROM.validateNewValue("2026-09-20 2500"));

        assertEquals("Event dates must use the yyyy-MM-dd HHmm format.", exception.getMessage());
    }
}
