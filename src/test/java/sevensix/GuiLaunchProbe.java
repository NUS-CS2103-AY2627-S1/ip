package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Child-process driver that verifies the real launcher and exits through the GUI's bye command. */
public final class GuiLaunchProbe {
    private static volatile boolean hasVerifiedExit;

    private GuiLaunchProbe() {
    }

    /** Observes startup while the production launcher owns the application's main thread.
     *
     * @param args the arguments forwarded to the production launcher.
     */
    public static void main(String[] args) {
        ScheduledExecutorService observer = Executors.newSingleThreadScheduledExecutor();
        try {
            observer.scheduleAtFixedRate(GuiLaunchProbe::queueWindowCheck, 0, 25, TimeUnit.MILLISECONDS);
            Launcher.main(args);
            assertTrue(hasVerifiedExit, "Launcher returned before the GUI processed bye");
            System.out.println("GUI launch and exit verified");
        } finally {
            observer.shutdownNow();
        }
    }

    /** Retries only until JavaFX is initialized; the parent process bounds the entire startup. */
    private static void queueWindowCheck() {
        try {
            Platform.runLater(GuiLaunchProbe::checkWindow);
        } catch (IllegalStateException exception) {
            // The launcher has not initialized the toolkit yet; the next observation will retry.
        }
    }

    /** Finds the displayed production window and reports assertion failures as a failing process. */
    private static void checkWindow() {
        if (hasVerifiedExit) {
            return;
        }
        try {
            Window window = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
            if (window != null) {
                verifyExit((Stage) window);
            }
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }

    /** Submits bye through the actual button and checks the final conversation before shutdown.
     *
     * @param stage the displayed application window.
     */
    private static void verifyExit(Stage stage) {
        TextField input = (TextField) stage.getScene().lookup(".command-field");
        Button send = (Button) stage.getScene().lookup(".send-button");
        VBox conversation = (VBox) stage.getScene().lookup(".dialog-container");
        assertEquals(Ui.APPLICATION_NAME, stage.getTitle());
        assertEquals(1, conversation.getChildren().size());
        input.setText("bye");
        send.fire();
        assertEquals("", input.getText());
        assertEquals(3, conversation.getChildren().size());
        DialogBox reply = (DialogBox) conversation.getChildren().getLast();
        assertEquals(Ui.getFarewellMessage(), ((Label) reply.getChildren().getLast()).getText());
        hasVerifiedExit = true;
    }
}
