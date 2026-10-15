package serina.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Tests Serina exception messages and retained causes. */
public class SerinaExceptionTest {
    @Test
    public void constructors_errorDetailsAndCause_preserveDiagnosticInformation() {
        RuntimeException cause = new RuntimeException("disk failure");
        SerinaException plain = new SerinaException(SerinaError.EMPTY_COMMAND);
        SerinaException detailed = new SerinaException(SerinaError.LOAD_FAILED, "Invalid record.");
        SerinaException caused = new SerinaException(SerinaError.SAVE_FAILED, cause);

        assertEquals(SerinaError.EMPTY_COMMAND.getMessage(), plain.getMessage());
        assertEquals(SerinaError.LOAD_FAILED.getMessage() + " Invalid record.", detailed.getMessage());
        assertSame(cause, caused.getCause());
        assertFalse(plain.shouldExit());
    }
}
