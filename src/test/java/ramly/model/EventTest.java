package ramly.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ramly.exception.TaskValidationException;

/** Tests event boundary validation and legacy-storage compatibility. */
public class EventTest {
    @Test
    public void constructor_validRange_createsEvent() {
        assertDoesNotThrow(() -> new Event(
                "meeting", "2026-10-01 0900", "2026-10-01 1000"));
    }

    @Test
    public void constructor_shortDateTimeFormat_createsEvent() {
        assertDoesNotThrow(() -> new Event(
                "meeting", "1/10/2026 0900", "1/10/2026 1000"));
    }

    @Test
    public void constructor_storageOnlyFormat_throwsValidationException() {
        assertThrows(TaskValidationException.class, () -> new Event(
                "meeting", "2026-10-01T09:00:00", "2026-10-01T10:00:00"));
    }

    @Test
    public void constructor_equalBounds_throwsValidationException() {
        assertThrows(TaskValidationException.class, () -> new Event(
                "meeting", "2026-10-01 0900", "2026-10-01 0900"));
    }

    @Test
    public void constructor_endBeforeStart_throwsValidationException() {
        assertThrows(TaskValidationException.class, () -> new Event(
                "meeting", "2026-10-01 1000", "2026-10-01 0900"));
    }

    @Test
    public void constructor_nonExistentDate_throwsValidationException() {
        assertThrows(TaskValidationException.class, () -> new Event(
                "meeting", "2026-02-30 0900", "2026-02-30 1000"));
    }

    @Test
    public void fromStorage_legacyTextBounds_loadsEvent() {
        assertDoesNotThrow(() -> Event.fromStorage("meeting", "10am", "11am"));
    }
}
