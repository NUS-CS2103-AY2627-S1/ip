package gary.smoke;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

/**
 * Drives the packaged application's real window during release verification.
 */
public class JarSmokeAgent {
    private static volatile boolean completed;

    /**
     * Waits for JavaFX startup and checks task interaction and automatic exit.
     *
     * @param argument Farewell command to submit.
     */
    public static void premain(String argument) {
        Thread probe = new Thread(() -> {
            for (int attempt = 0; attempt < 100 && !completed; attempt++) {
                try {
                    Thread.sleep(200);
                    Platform.runLater(() -> checkWindow(argument));
                } catch (IllegalStateException exception) {
                    // The launcher has not initialized the toolkit yet.
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        probe.setDaemon(true);
        probe.start();
    }

    private static void checkWindow(String farewell) {
        if (completed || Window.getWindows().isEmpty()) {
            return;
        }
        Window window = Window.getWindows().getFirst();
        if (!window.isShowing() || window.getScene() == null) {
            return;
        }
        completed = true;
        try {
            Parent root = window.getScene().getRoot();
            TextField input = (TextField) root.lookup("#userInput");
            submit(input, "todo release smoke test");
            submit(input, "list");
            root.applyCss();
            boolean taskShown = root.lookupAll(".label").stream().filter(Label.class::isInstance)
                    .map(Label.class::cast).anyMatch(label -> label.getText().contains("1.[T][ ] release smoke test"));
            if (!taskShown) {
                throw new AssertionError("Task list was not rendered");
            }
            window.setOnHidden(event -> System.out.println("SMOKE_WINDOW_CLOSED"));
            submit(input, farewell);
            if (!input.isDisabled()) {
                throw new AssertionError("Farewell did not disable input");
            }
            System.out.println("SMOKE_GUI_PASS " + System.getProperty("java.version")
                    + " " + System.getProperty("os.arch") + " " + farewell);
        } catch (Throwable exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static void submit(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent(input, input));
    }
}
