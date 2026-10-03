package ramly.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ramly.exception.CommandFormatException;
import ramly.exception.TaskValidationException;

/** Tests normalization, length limits, and storage-safe text validation. */
public class InputValidatorTest {
    @Test
    public void normalizeCommand_multipleSpacesAndTabs_collapsesWhitespace() {
        assertEquals("todo buy milk", InputValidator.normalizeCommand("  todo\t buy   milk  "));
    }

    @Test
    public void normalizeCommand_tooLong_throwsFormatException() {
        String input = "x".repeat(InputValidator.MAX_COMMAND_LENGTH + 1);

        assertThrows(CommandFormatException.class, () -> InputValidator.normalizeCommand(input));
    }

    @Test
    public void normalizeDescription_reservedDelimiter_throwsValidationException() {
        assertThrows(TaskValidationException.class,
                () -> InputValidator.normalizeDescription("first | second"));
    }

    @Test
    public void normalizeDescription_controlCharacter_throwsValidationException() {
        assertThrows(TaskValidationException.class,
                () -> InputValidator.normalizeDescription("first\nsecond"));
    }

    @Test
    public void normalizeDescription_tooLong_throwsValidationException() {
        String description = "x".repeat(InputValidator.MAX_DESCRIPTION_LENGTH + 1);

        assertThrows(TaskValidationException.class,
                () -> InputValidator.normalizeDescription(description));
    }
}
