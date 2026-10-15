import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tungtung.TungTungGui;

/** Exercises the packaged GUI in an isolated directory without showing a visible window. */
public class GuiSmoke {
    /** Runs normal, corrupt-startup, or failed-save checks against the real JavaFX controls. */
    public static void main(String[] args) throws Exception {
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> { });
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setOpacity(0);
            try {
                new TungTungGui().start(stage);
                TextField input = (TextField) stage.getScene().lookup(".text-field");
                Button send = (Button) stage.getScene().lookup(".button");
                if (args[0].equals("corrupt")) {
                    String before = Files.readString(Path.of("data/tungtung.txt"));
                    require(messages(stage).contains("Unable to load saved tasks."));
                    submit(input, send, "todo replacement");
                    require(Files.readString(Path.of("data/tungtung.txt")).equals(before));
                } else if (args[0].equals("save-failure")) {
                    Files.writeString(Path.of("data"), "block directory creation");
                    submit(input, send, "todo failed");
                    require(messages(stage).contains("No changes were made."));
                    submit(input, send, "list");
                    require(messages(stage).contains("There are no tasks to show."));
                    Files.delete(Path.of("data"));
                    submit(input, send, "todo recovered");
                    require(Files.readString(Path.of("data/tungtung.txt")).equals("T | 0 | recovered\n"));
                } else {
                    submit(input, send, "todo groceries");
                    input.setText("todo book");
                    input.fireEvent(new ActionEvent());
                    submit(input, send, "find book");
                    require(messages(stage).contains("2. [T][ ] book"));
                    submit(input, send, "mark 2");
                    require(messages(stage).contains("[T][X] book"));
                    submit(input, send, "delete 2");
                    submit(input, send, "deadline report | /by 2026-09-18");
                    submit(input, send, "event meeting /to 2026-09-17 /from 2026-09-18");
                    require(Files.readString(Path.of("data/tungtung.txt")).equals("T | 0 | groceries\n"));
                }
                System.out.println("GUI_SMOKE_PASS " + args[0]);
            } catch (Throwable exception) {
                failure.set(exception);
            } finally {
                stage.close();
                finished.countDown();
            }
        });
        boolean completed = finished.await(30, TimeUnit.SECONDS);
        Platform.exit();
        if (!completed || failure.get() != null) {
            throw new IllegalStateException("GUI smoke test failed or timed out.", failure.get());
        }
    }

    /** Submits text through the actual Send button. */
    private static void submit(TextField input, Button send, String command) {
        input.setText(command);
        send.fire();
    }

    /** Reads all displayed chat labels after CSS has created the control nodes. */
    private static String messages(Stage stage) {
        stage.getScene().getRoot().applyCss();
        return stage.getScene().getRoot().lookupAll(".label").stream()
                .filter(node -> node instanceof Label)
                .map(node -> ((Label) node).getText()).collect(Collectors.joining("\n"));
    }

    /** Fails independently of whether JVM assertions are enabled. */
    private static void require(boolean condition) {
        if (!condition) {
            throw new AssertionError("Unexpected GUI behavior.");
        }
    }
}
