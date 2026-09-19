package carl.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import carl.exceptions.CarlCommandException;

public class InputValidatorTest {
    @Test
    public void normalizeTaskName_surroundingAndRepeatedWhitespace_returnsNormalizedName() throws Exception {
        assertEquals("write unit tests",
                InputValidator.normalizeTaskName("  write   unit\ttests  ", "usage"));
    }

    @Test
    public void normalizeTaskName_emptyUnsafeOrTooLong_throwsCommandException() {
        assertThrows(CarlCommandException.class, () ->
                InputValidator.normalizeTaskName(null, "usage"));
        assertThrows(CarlCommandException.class, () ->
                InputValidator.normalizeTaskName("   ", "usage"));
        assertThrows(CarlCommandException.class, () ->
                InputValidator.normalizeTaskName("task | unsafe", "usage"));
        assertThrows(CarlCommandException.class, () ->
                InputValidator.normalizeTaskName("a".repeat(501), "usage"));
    }
}
