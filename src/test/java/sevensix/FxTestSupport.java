package sevensix;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/** Runs GUI assertions on the JavaFX thread and propagates failures to JUnit. */
final class FxTestSupport {
    private static final int TIMEOUT_SECONDS = 15;
    private static boolean hasStarted;

    private FxTestSupport() {
    }

    /** Starts one toolkit for the test worker, keeping it alive between windows. */
    static synchronized void startToolkit() throws Exception {
        if (hasStarted) {
            return;
        }
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        if (!started.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX toolkit did not start");
        }
        hasStarted = true;
    }

    /** Waits for an action and all previously queued UI work without arbitrary sleeps.
     *
     * @param action the UI operation or assertions to execute.
     */
    static void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
