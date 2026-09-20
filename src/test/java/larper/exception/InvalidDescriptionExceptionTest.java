package larper.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InvalidDescriptionExceptionTest {
    @Test
    public void getMessage_separatorInDescription_expectedMessage() {
        assertEquals(" The task description cannot contain `|` because Larper uses it to save data safely.",
                new InvalidDescriptionException().getMessage());
    }
}
