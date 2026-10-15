package anders.smoke;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Exercises the real java -jar application without putting test commands or hooks in the release JAR. */
public class SmokeAgent {
    /**
     * Starts a bounded test observer before the application's normal launcher runs.
     *
     * @param mode either create (first launch) or reload (second launch)
     */
    public static void premain(String mode) {
        Thread.setDefaultUncaughtExceptionHandler((thread, failure) -> fail(failure));
        Thread observer = new Thread(() -> waitForWindow(mode), "jar-smoke-observer");
        observer.setDaemon(true);
        observer.start();
    }

    /** Waits for a usable window and makes startup or command failures visible to the test process. */
    private static void waitForWindow(String mode) {
        try {
            awaitWindow(Platform::runLater, () -> inspectWindow(mode), 30_000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail(exception);
        } catch (ExecutionException | TimeoutException exception) {
            fail(exception);
        }
    }

    /**
     * Waits for each inspection before scheduling another so a slow startup cannot build a callback backlog.
     *
     * @param dispatcher the GUI event queue, or a controlled queue in unit tests
     * @param inspection returns true once a usable window has been checked
     * @param timeoutMillis the maximum time to wait for the window
     * @throws InterruptedException if the observer is interrupted
     * @throws ExecutionException if inspecting the window or checking its commands fails
     * @throws TimeoutException if the window never becomes usable
     */
    static void awaitWindow(Executor dispatcher, Callable<Boolean> inspection, long timeoutMillis)
            throws InterruptedException, ExecutionException, TimeoutException {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        while (System.nanoTime() < deadline) {
            FutureTask<Boolean> check = new FutureTask<>(inspection);
            try {
                dispatcher.execute(check);
            } catch (IllegalStateException exception) {
                // The regular application launcher has not started JavaFX yet.
                Thread.sleep(100);
                continue;
            }
            long remaining = Math.max(0, deadline - System.nanoTime());
            if (check.get(remaining, TimeUnit.NANOSECONDS)) {
                return;
            }
            Thread.sleep(100);
        }
        throw new TimeoutException("Anders did not display a usable window before the smoke-test deadline.");
    }

    /** Checks the real application's window, then closes it after the commands' queued layout callbacks. */
    private static boolean inspectWindow(String mode) {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage && stage.isShowing() && stage.getTitle().startsWith("Anders")) {
                checkSession(stage.getScene().getRoot(), mode);
                Platform.runLater(() -> {
                    // Normal last-window shutdown waits for the JavaFX queue to become idle.
                    // Calling Platform.exit() here can detach macOS Glass while callbacks are still pending.
                    stage.close();
                    System.out.println("SMOKE PASSED: " + mode);
                });
                return true;
            }
        }
        return false;
    }

    /** Checks creation, validation, and persistence across two independently launched JVMs. */
    private static void checkSession(Parent root, String mode) {
        if ("create".equals(mode)) {
            submit(root, "list", "A clear path! No tasks yet.");
            submit(root, "todo packaged smoke task /tags #release", "Your trail holds 1 task.");
            submit(root, "deadline impossible /by 31/2/2026", "A deadline needs a real date");
            submit(root, "mark 1", "Task marked as done:");
            submit(root, "find #release", "1.[T][X] packaged smoke task (tags: #release)");
        } else if ("reload".equals(mode)) {
            submit(root, "list", "1.[T][X] packaged smoke task (tags: #release)");
            submit(root, "unmark 1", "Task marked as not done:");
            submit(root, "delete 1", "Your trail holds 0 tasks.");
            submit(root, "list", "A clear path! No tasks yet.");
        } else {
            throw new IllegalArgumentException("Unknown smoke-test mode: " + mode);
        }
    }

    /** Submits through the GUI and verifies the resulting bot message. */
    private static void submit(Parent root, String command, String expected) {
        TextField input = (TextField) root.lookup("#input");
        if (input == null) {
            throw new AssertionError("The packaged FXML did not create the command input.");
        }
        input.setText(command);
        input.fireEvent(new ActionEvent());
        VBox messages = (VBox) root.lookup("#messages");
        Label reply = (Label) messages.getChildren().getLast().lookup("#message");
        String actual = reply.getText();
        System.out.println("> " + command + "\n" + actual);
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected: " + expected + "\nActual: " + actual);
        }
        if (!input.getText().isEmpty()) {
            throw new AssertionError("The command input did not clear after submission.");
        }
    }

    /** Makes application-thread failures visible to the calling smoke-test process. */
    private static void fail(Throwable failure) {
        failure.printStackTrace();
        System.exit(1);
    }
}
