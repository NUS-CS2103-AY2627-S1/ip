package larper.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NonNumberDeleteExceptionTest {
    @Test
    public void getMessage_nonNumberDelete_expectedMessage() {
        assertEquals(" Deletion needs a number. Reputation management requires specifics.",
                new NonNumberDeleteException().getMessage());
    }
}
