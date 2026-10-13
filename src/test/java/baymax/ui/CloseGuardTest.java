package baymax.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/** Tests closing decisions without opening windows or changing real task files. */
public class CloseGuardTest {
    @Test
    public void canClose_success_savesOnceWithoutPrompt() {
        AtomicInteger attempts = new AtomicInteger();
        assertTrue(CloseGuard.canClose(() -> attempts.incrementAndGet(), exception -> {
            fail("Successful saving must not show a failure prompt.");
            return CloseGuard.Decision.CANCEL;
        }));
        assertEquals(1, attempts.get());
    }

    @Test
    public void canClose_failedSaveCancel_keepsWindowOpen() {
        assertFalse(CloseGuard.canClose(() -> {
            throw new IOException("Access denied");
        }, exception -> {
            assertEquals("Access denied", exception.getMessage());
            return CloseGuard.Decision.CANCEL;
        }));
    }

    @Test
    public void canClose_retryThenSuccess_allowsClosing() {
        AtomicInteger attempts = new AtomicInteger();
        assertTrue(CloseGuard.canClose(() -> {
            if (attempts.incrementAndGet() == 1) {
                throw new IOException("Temporary failure");
            }
        }, exception -> CloseGuard.Decision.RETRY));
        assertEquals(2, attempts.get());
    }

    @Test
    public void canClose_retryFailsThenCancel_keepsWindowOpen() {
        AtomicInteger prompts = new AtomicInteger();
        assertFalse(CloseGuard.canClose(() -> {
            throw new IOException("Still unavailable");
        }, exception -> prompts.incrementAndGet() == 1
                ? CloseGuard.Decision.RETRY : CloseGuard.Decision.CANCEL));
        assertEquals(2, prompts.get());
    }

    @Test
    public void canClose_explicitDiscard_allowsClosingWithoutAnotherSave() {
        AtomicInteger attempts = new AtomicInteger();
        assertTrue(CloseGuard.canClose(() -> {
            attempts.incrementAndGet();
            throw new IOException("Cannot save");
        }, exception -> CloseGuard.Decision.EXIT_WITHOUT_SAVING));
        assertEquals(1, attempts.get());
    }
}
