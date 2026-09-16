package anaconda.testutil;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Runs GUI assertions on the JavaFX thread without opening application windows.
 */
public class JavaFxTestSupport {
    private static boolean isStarted;

    /**
     * Starts the shared toolkit once and waits until it is ready for scene construction.
     */
    public static synchronized void startToolkit() throws InterruptedException {
        if (isStarted) {
            return;
        }
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        if (!ready.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX did not start within 10 seconds.");
        }
        isStarted = true;
    }

    /**
     * Runs a GUI check and propagates failures to the JUnit thread with a bounded wait.
     */
    public static void runOnFxThread(Callable<Void> action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
