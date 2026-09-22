package oz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ResponseType} enum.
 */
public class ResponseTypeTest {

    @Test
    public void valueOf_validConstants_returnsEnumConstant() {
        for (ResponseType type : ResponseType.values()) {
            assertNotNull(type);
            assertEquals(type, ResponseType.valueOf(type.name()));
        }
    }
}
