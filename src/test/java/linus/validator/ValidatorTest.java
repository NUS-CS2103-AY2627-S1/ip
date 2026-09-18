package linus.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ValidatorTest {
    @Test
    public void validate_invalidTaskId_exceptionThrown() {
        Validator validator = new Validator(new ArrayList<>());
        assertThrows(NumberFormatException.class, () -> validator.validate(List.of("delete", "-1")));
    }

    @Test
    public void validate_invalidDate_exceptionThrown() {
        Validator validator = new Validator(new ArrayList<>());
        assertThrows(DateTimeParseException.class, () -> validator.validate(List.of("deadline", "test", "2026/1/1")));
    }
}
