package anders.smoke;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/** Checks smoke-test scheduling without starting or shutting down the shared JavaFX toolkit. */
public class SmokeAgentTest {
    @Test
    public void awaitWindow_slowGui_waitsForEachInspectionAndStopsAfterSuccess() throws Exception {
        BlockingQueue<Runnable> callbacks = new LinkedBlockingQueue<>();
        AtomicInteger inspections = new AtomicInteger();
        try (var observer = Executors.newSingleThreadExecutor()) {
            Future<?> finished = observer.submit(() -> {
                SmokeAgent.awaitWindow(callbacks::add, () -> inspections.incrementAndGet() == 2, 5_000);
                return null;
            });

            Runnable first = callbacks.poll(2, TimeUnit.SECONDS);
            assertNotNull(first);
            assertNull(callbacks.poll(250, TimeUnit.MILLISECONDS),
                    "A slow GUI must not accumulate extra inspection callbacks");
            first.run();

            Runnable second = callbacks.poll(2, TimeUnit.SECONDS);
            assertNotNull(second);
            second.run();
            finished.get(2, TimeUnit.SECONDS);
            assertEquals(2, inspections.get());
            assertTrue(callbacks.isEmpty(), "No inspection may remain queued when the window closes");
        }
    }

    @Test
    public void awaitWindow_toolkitNotReady_retriesUntilAnInspectionCanRun() {
        AtomicInteger attempts = new AtomicInteger();
        Executor dispatcher = callback -> {
            if (attempts.getAndIncrement() == 0) {
                throw new IllegalStateException("Toolkit not initialized");
            }
            callback.run();
        };

        assertDoesNotThrow(() -> SmokeAgent.awaitWindow(dispatcher, () -> true, 2_000));
        assertEquals(2, attempts.get());
    }

    @Test
    public void awaitWindow_commandAssertionFails_propagatesFailure() {
        AssertionError failure = new AssertionError("Unexpected task reply");

        ExecutionException exception = assertThrows(ExecutionException.class, () ->
                SmokeAgent.awaitWindow(Runnable::run, () -> {
                    throw failure;
                }, 2_000));

        assertSame(failure, exception.getCause());
    }

    @Test
    public void awaitWindow_guiNeverRunsCallback_timesOutInsteadOfPassing() {
        BlockingQueue<Runnable> callbacks = new LinkedBlockingQueue<>();

        assertThrows(TimeoutException.class, () ->
                SmokeAgent.awaitWindow(callbacks::add, () -> true, 50));
        assertEquals(1, callbacks.size());
    }
}
