package cookie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cookie.command.CookieException;

/** Tests conversion between saved task type codes and enum values. */
public class TaskTypeTest {
    @Test
    public void getCode_returnsStorageCode() {
        assertEquals("T", TaskType.TODO.getCode());
        assertEquals("D", TaskType.DEADLINE.getCode());
        assertEquals("E", TaskType.EVENT.getCode());
    }

    @Test
    public void fromCode_validCodesAreCaseInsensitive_returnsMatchingType() throws CookieException {
        assertEquals(TaskType.TODO, TaskType.fromCode("t"));
        assertEquals(TaskType.DEADLINE, TaskType.fromCode("D"));
        assertEquals(TaskType.EVENT, TaskType.fromCode("e"));
    }

    @Test
    public void fromCode_unknownCode_throwsCookieException() {
        CookieException exception = assertThrows(CookieException.class, () -> TaskType.fromCode("X"));
        assertEquals("I have never seen this task type 'X' before.", exception.getMessage());
    }
}
