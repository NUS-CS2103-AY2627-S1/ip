package yachiyo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.function.Executable;

import yachiyo.exception.ErrorCategory;
import yachiyo.exception.YachiyoException;

/**
 * Provides assertions shared by Yachiyo tests.
 */
public final class TestAssertions {
    private TestAssertions() {
    }

    /**
     * Verifies that an operation reports a Yachiyo error with the expected category.
     *
     * @param expectedCategory expected presentation category.
     * @param executable operation expected to fail.
     * @return exception reported by the operation.
     */
    public static YachiyoException assertYachiyoException(
            ErrorCategory expectedCategory, Executable executable) {
        YachiyoException exception = assertThrows(YachiyoException.class, executable);
        assertEquals(expectedCategory, exception.getCategory());
        return exception;
    }
}
